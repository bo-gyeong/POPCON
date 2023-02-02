package com.example.popconback.gifticon.controller;


import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.CheckValidationDto;
import com.example.popconback.gifticon.dto.GifticonResponse;
import com.example.popconback.gifticon.repository.Brandrepository;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.gifticon.service.BrandService;
import com.example.popconback.gifticon.service.GifticonService;
import com.google.cloud.vision.v1.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

import com.example.popconback.gifticon.dto.GifticonResponse;
import com.example.popconback.gifticon.service.S3Service;
import com.google.cloud.vision.v1.*;
import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Api(value = "GoogleOcrController")
@SwaggerDefinition(tags = {@Tag(name = "GoogleOcrController",
        description = "구글 OCR 컨트롤러")})
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/v1/gcp")
public class OcrController {

    private GifticonService gifticonService;

    private BrandService brandService;
    final GifticonRepository gifticonRepository;

    final Brandrepository brandrepository;




    private static final String BASE_PATH = "C:\\upload\\";


    @ApiOperation(value = "detectTextAndValidation", notes = "0: success / 1: barcode error / 2: brand error / 3: both error", httpMethod = "GET")
    @ApiImplicitParam(
            name = "fileName",
            value = "gcp 이미지 파일 이름",
            required = true,
            dataType = "string",
            paramType = "query",
            defaultValue = "None"
    )
    @PostMapping("/ocr/test")
    public ResponseEntity<List<GifticonResponse>> detectText(@RequestBody String[] fileNames) throws Exception {

        List<GifticonResponse> finalResult = new ArrayList<>();



        try {
            for (String fileName : fileNames) {

                GifticonResponse finalGifticonResponse = null;

                System.out.println(fileName);

                String filePath = "gs://popcon/"+fileName;


                List<AnnotateImageRequest> requests = new ArrayList<>();

                ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(filePath).build();
                Image img = Image.newBuilder().setSource(imgSource).build();
                Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
                AnnotateImageRequest request =
                        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                requests.add(request);


                // Initialize client that will be used to send requests. This client only needs to be created
                // once, and can be reused for multiple requests. After completing all of your requests, call
                // the "close" method on the client to safely clean up any remaining background resources.
                try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                    List<AnnotateImageResponse> responses = response.getResponsesList();

                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            System.out.format("Error: %s%n", res.getError().getMessage());
                            break;
                        }

                        List<EntityAnnotation> resList = res.getTextAnnotationsList();
                        List<EntityAnnotation> newRes = new ArrayList<>(resList.subList(1,resList.size()));
                        List<String> descList = new ArrayList<>();

                        int definePublisher = -1; // 0:gs , 1:kakao , 2:giftishow , 3:gifticon

                        for (EntityAnnotation ress : newRes) {

                            // GS&쿠폰
                            String checkGS = "";

                            if (ress.getBoundingPoly().getVertices(0).getX() > 288 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 245 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 384 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 272){
                                checkGS += ress.getDescription().replaceAll("(\r\n|\r|\n|\n\r)", " ");

                            }

                            String isGS = checkGS.replaceAll("(\r\n|\r|\n|\n\r)", " ");
                            System.out.println(isGS);
                            if (isGS.contains("GS&쿠폰")) {
                                definePublisher = 0;

                                System.out.println(definePublisher);
                                break;
                            }

                            // 카카오톡
                            String checkKakao = "";

                            if (ress.getBoundingPoly().getVertices(0).getX() > 218 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 1499 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 584 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 1558){
                                checkKakao += ress.getDescription();

                            }

                            String isKakao = checkKakao.replace("\n","").replace(" ","");
                            System.out.print(isKakao);
                            if (isKakao.contains("kakaotalk")) {
                                definePublisher = 1;
                                //break;
                            }


                            // 기프티쇼
                            String checkGiftishow = "";

                            if (ress.getBoundingPoly().getVertices(0).getX() > 56 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 413 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 398 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 444){
                                checkGiftishow += ress.getDescription();

                            }

                            String isGiftishow = checkGiftishow.replace("\n","").replace(" ","");
                            System.out.print(isGiftishow);
                            if (isGiftishow.contains("기프티쇼") || isGiftishow.contains("giftishow")) {
                                definePublisher = 2;
                                //break;
                            }


                            // 기프티콘
                            String checkGifticon = "";

                            if (ress.getBoundingPoly().getVertices(0).getX() > 125 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 312 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 196 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 333){
                                checkGifticon += ress.getDescription();

                            }

                            String isGifticon = checkGifticon.replace("\n","").replace(" ","");
                            System.out.print(isGifticon);
                            if (isGifticon.contains("gifticon")) {
                                definePublisher = 3;
                                //break;
                            }

                        }

                        System.out.println(definePublisher);

                        List<String> checkVoucher = new ArrayList<>();
                        checkVoucher.add("금액권");
                        checkVoucher.add("상품권");
                        checkVoucher.add("모바일금액권");
                        checkVoucher.add("모바일상품권");
                        checkVoucher.add("기프티카드");
                        checkVoucher.add("디지털상품권");
                        checkVoucher.add("모바일교환권");
                        checkVoucher.add("원권");

                        // isVoucher, publisher, brandName, productName, productImg,
                        // due, barcodeNum, barcodeImg, validation

                        // GS&쿠폰
                        if (definePublisher==0) {

                            String publisher = "GS&쿠폰";

                            for (EntityAnnotation gsRes : newRes) {

                                // brandName
                                String checkGsBrand = "";

                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 199 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 204 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 430 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 245){
                                    checkGsBrand += gsRes.getDescription();

                                }

                                String preBrandName = checkGsBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrandName);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }


                                // productName
                                String checkGsProduct = "";

                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 200 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 17 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 430 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 120){
                                    checkGsProduct += gsRes.getDescription();

                                }

                                String productName = checkGsProduct.replace("\n","");
                                System.out.print(productName);


                                // isVoucher

                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }


                                // due
                                String checkGsDue = "";

                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 199 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 143 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 329 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 171){
                                    checkGsDue += gsRes.getDescription();

                                }

                                String preDue = checkGsDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));


                                // barcodeNum
                                String checkGsBarcode = "";

                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 122 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 351 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 317 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 386){
                                    checkGsBarcode += gsRes.getDescription();

                                }

                                String barcodeNum = checkGsBarcode.replace("\n","").replace(" ","").replace("-","");
                                System.out.print(barcodeNum);


                                // validation
                                int validation = 0;

                                try {
                                    Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

                                    if (byBarcodeNum.isPresent()) {
                                        validation = 1;
                                    }


                                    Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName));

                                    if (byBrandName.isEmpty()) {
                                        validation = 2;
                                    }


                                    if (byBarcodeNum.isPresent() && byBrandName.isEmpty()) {
                                        validation = 3;
                                    }
                                }
                                catch (NullPointerException e) {
                                    System.out.println(e);
                                }



                                // barcodeImg, productImg
                                Map<String, String> productPosition = new HashMap<>();

                                productPosition.put("x1", "25");
                                productPosition.put("y1", "31");
                                productPosition.put("x2", "183");
                                productPosition.put("y2", "31");
                                productPosition.put("x3", "25");
                                productPosition.put("y3", "187");
                                productPosition.put("x4", "183");
                                productPosition.put("y4", "187");

                                Map<String, String> barcodePosition = new HashMap<>();

                                barcodePosition.put("x1", "0");
                                barcodePosition.put("y1", "282");
                                barcodePosition.put("x2", "430");
                                barcodePosition.put("y2", "282");
                                barcodePosition.put("x3", "0");
                                barcodePosition.put("y3", "347");
                                barcodePosition.put("x4", "430");
                                barcodePosition.put("y4", "347");


                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                                finalGifticonResponse = gifticonResponse;

                                System.out.println(gifticonResponse);
                                //break;


                            }

                        }
                        else if (definePublisher==1) {

                            String publisher = "kakaotalk";

                            for (EntityAnnotation kakaoRes : newRes) {

                                // brandName
                                String checkKakaoBrand = "";

                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 200 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 1213 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 722 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 245){
                                    checkKakaoBrand += kakaoRes.getDescription();

                                }

                                String preBrandName = checkKakaoBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrandName);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }


                                // productName
                                String checkKakadoProduct = "";

                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 40 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 798 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 590 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 944){
                                    checkKakadoProduct += kakaoRes.getDescription();

                                }

                                String productName = checkKakadoProduct.replace("\n","");
                                System.out.print(productName);


                                // isVoucher

                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }


                                // due
                                String checkKakaoDue = "";

                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 420 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 1303 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 717 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 1373){
                                    checkKakaoDue += kakaoRes.getDescription();

                                }

                                String preDue = checkKakaoDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));


                                // barcodeNum
                                String checkKakaoBarcode = "";

                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 135 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 1105 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 671 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 1188){
                                    checkKakaoBarcode += kakaoRes.getDescription();

                                }

                                String barcodeNum = checkKakaoBarcode.replace("\n","").replace(" ","");
                                System.out.print(barcodeNum);


                                // validation
                                int validation = 0;

                                try {
                                    Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

                                    if (byBarcodeNum.isPresent()) {
                                        validation = 1;
                                    }


                                    Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName));

                                    if (byBrandName.isEmpty()) {
                                        validation = 2;
                                    }


                                    if (byBarcodeNum.isPresent() && byBrandName.isEmpty()) {
                                        validation = 3;
                                    }
                                }
                                catch (NullPointerException e) {
                                    System.out.println(e);
                                }



                                // barcodeImg, productImg
                                Map<String, String> productPosition = new HashMap<>();

                                productPosition.put("x1", "71");
                                productPosition.put("y1", "80");
                                productPosition.put("x2", "723");
                                productPosition.put("y2", "80");
                                productPosition.put("x3", "71");
                                productPosition.put("y3", "678");
                                productPosition.put("x4", "723");
                                productPosition.put("y4", "678");


                                Map<String, String> barcodePosition = new HashMap<>();

                                barcodePosition.put("x1", "71");
                                barcodePosition.put("y1", "975");
                                barcodePosition.put("x2", "723");
                                barcodePosition.put("y2", "975");
                                barcodePosition.put("x3", "71");
                                barcodePosition.put("y3", "1070");
                                barcodePosition.put("x4", "723");
                                barcodePosition.put("y4", "1070");


                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                                finalGifticonResponse = gifticonResponse;

                                //System.out.println(gifticonResponse);
                                //break;


                            }

                        }
                        else if (definePublisher==2) {

                            String publisher = "giftishow";

                            for (EntityAnnotation giftishowRes : newRes) {

                                // brandName
                                String checkGiftishowBrand = "";

                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 105 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 579 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 601){
                                    checkGiftishowBrand += giftishowRes.getDescription();

                                }

                                String preBrandName = checkGiftishowBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrandName);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }


                                // productName
                                String checkGiftishowProduct = "";

                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 105 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 556 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 580){
                                    checkGiftishowProduct += giftishowRes.getDescription();

                                }

                                String productName = checkGiftishowProduct.replace("\n","");
                                System.out.print(productName);


                                // isVoucher

                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }


                                // due
                                String checkGiftishowDue = "";

                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 127 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 602 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 630){
                                    checkGiftishowDue += giftishowRes.getDescription();

                                }

                                String preDue = checkGiftishowDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));


                                // barcodeNum
                                String checkGiftishowBarcode = "";

                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 74 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 504 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 377 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 535){
                                    checkGiftishowBarcode += giftishowRes.getDescription();

                                }

                                String barcodeNum = checkGiftishowBarcode.replace("\n","").replace(" ","");
                                System.out.print(barcodeNum);


                                // validation
                                int validation = 0;

                                try {
                                    Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

                                    if (byBarcodeNum.isPresent()) {
                                        validation = 1;
                                    }


                                    Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName));

                                    if (byBrandName.isEmpty()) {
                                        validation = 2;
                                    }


                                    if (byBarcodeNum.isPresent() && byBrandName.isEmpty()) {
                                        validation = 3;
                                    }
                                }
                                catch (NullPointerException e) {
                                    System.out.println(e);
                                }



                                // barcodeImg, productImg
                                Map<String, String> productPosition = new HashMap<>();

                                productPosition.put("x1", "26");
                                productPosition.put("y1", "210");
                                productPosition.put("x2", "206");
                                productPosition.put("y2", "210");
                                productPosition.put("x3", "26");
                                productPosition.put("y3", "330");
                                productPosition.put("x4", "206");
                                productPosition.put("y4", "330");


                                Map<String, String> barcodePosition = new HashMap<>();

                                barcodePosition.put("x1", "44");
                                barcodePosition.put("y1", "458");
                                barcodePosition.put("x2", "405");
                                barcodePosition.put("y2", "458");
                                barcodePosition.put("x3", "44");
                                barcodePosition.put("y3", "492");
                                barcodePosition.put("x4", "405");
                                barcodePosition.put("y4", "492");


                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                                finalGifticonResponse = gifticonResponse;

                                //System.out.println(gifticonResponse);
                                //break;


                            }

                        }
                        else if (definePublisher==3) {

                            String publisher = "gifticon";

                            for (EntityAnnotation gifticonRes : newRes) {

                                // brandName
                                String checkGifticonBrand = "";

                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 183 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 290 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 314){
                                    checkGifticonBrand += gifticonRes.getDescription();

                                }

                                String preBrandName = checkGifticonBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrandName);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }


                                // productName
                                String checkGifticonProduct = "";

                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 126 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 214 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 254){
                                    checkGifticonProduct += gifticonRes.getDescription();

                                }

                                String productName = checkGifticonProduct.replace("\n","");
                                System.out.print(productName);


                                // isVoucher

                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }


                                // due
                                String checkGifticonDue = "";

                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 196 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 273 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 288){
                                    checkGifticonDue += gifticonRes.getDescription();

                                }

                                String preDue = checkGifticonDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));


                                // barcodeNum
                                String checkGifticonBarcode = "";

                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 89 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 390 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 233 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 413){
                                    checkGifticonBarcode += gifticonRes.getDescription();

                                }

                                String barcodeNum = checkGifticonBarcode.replace("\n","").replace(" ","");
                                System.out.print(barcodeNum);


                                // validation
                                int validation = 0;

                                try {
                                    Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

                                    if (byBarcodeNum.isPresent()) {
                                        validation = 1;
                                    }


                                    Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName));

                                    if (byBrandName.isEmpty()) {
                                        validation = 2;
                                    }


                                    if (byBarcodeNum.isPresent() && byBrandName.isEmpty()) {
                                        validation = 3;
                                    }
                                }
                                catch (NullPointerException e) {
                                    System.out.println(e);
                                }



                                // barcodeImg, productImg
                                Map<String, String> productPosition = new HashMap<>();

                                productPosition.put("x1", "28");
                                productPosition.put("y1", "217");
                                productPosition.put("x2", "114");
                                productPosition.put("y2", "217");
                                productPosition.put("x3", "28");
                                productPosition.put("y3", "299");
                                productPosition.put("x4", "114");
                                productPosition.put("y4", "229");


                                Map<String, String> barcodePosition = new HashMap<>();

                                barcodePosition.put("x1", "80");
                                barcodePosition.put("y1", "345");
                                barcodePosition.put("x2", "237");
                                barcodePosition.put("y2", "345");
                                barcodePosition.put("x3", "80");
                                barcodePosition.put("y3", "383");
                                barcodePosition.put("x4", "237");
                                barcodePosition.put("y4", "383");

                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                                finalGifticonResponse = gifticonResponse;

                                //System.out.println(gifticonResponse);
                                //break;


                            }

                        }
                        finalResult.add(finalGifticonResponse);

                    }


                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(finalResult,HttpStatus.OK);
    }
}


