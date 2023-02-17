package com.example.popconback.gifticon.controller;


import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.OCR.CheckBarcodeValidationDto;
import com.example.popconback.gifticon.dto.OCR.CheckBrandValidationDto;
import com.example.popconback.gifticon.dto.OCR.DetectTextDto;
import com.example.popconback.gifticon.dto.OCR.GifticonResponse;
import com.example.popconback.gifticon.repository.Brandrepository;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.gifticon.service.BrandService;
import com.example.popconback.gifticon.service.GifticonService;
import com.google.cloud.vision.v1.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.IntStream;

@Api(value = "GoogleOcrController")
@SwaggerDefinition(tags = {@Tag(name = "GoogleOcrController",
        description = "구글 OCR 컨트롤러")})
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/api/v1/gcp")
public class GoogleOcrController {

    private GifticonService gifticonService;

    private BrandService brandService;
    final GifticonRepository gifticonRepository;

    final Brandrepository brandrepository;




    private static final String BASE_PATH = "C:\\upload\\";

    @ApiOperation(value = "checkBarcodeValidaiton", notes = "0:error / 1:success", httpMethod = "GET")
    @ApiImplicitParam(
            name = "barcodeNum",
            value = "기프티콘 바코드 넘버",
            required = true,
            dataType = "string",
            paramType = "query",
            defaultValue = "None"
    )
    @GetMapping("/ocr/check_barcode")
    public ResponseEntity<CheckBarcodeValidationDto> checkBarcode(@RequestParam(value = "barcodeNum") String barcodeNum) throws Exception {

        if (barcodeNum.length() == 0) {
            CheckBarcodeValidationDto checkBarcodeValidationDto = new CheckBarcodeValidationDto(-1);
            return new ResponseEntity<CheckBarcodeValidationDto>(checkBarcodeValidationDto, HttpStatus.OK);
        }


        try {
            Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

            if (byBarcodeNum.isPresent()) {
                CheckBarcodeValidationDto checkBarcodeValidationDto = new CheckBarcodeValidationDto(0);
                return new ResponseEntity<CheckBarcodeValidationDto>(checkBarcodeValidationDto, HttpStatus.OK);
            }
            else {
                CheckBarcodeValidationDto checkBarcodeValidationDto = new CheckBarcodeValidationDto(1);
                return new ResponseEntity<CheckBarcodeValidationDto>(checkBarcodeValidationDto, HttpStatus.OK);
            }
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }

        return null;

    }

    @ApiOperation(value = "checkBrandValidation", notes = "0:error / 1:success", httpMethod = "GET")
    @ApiImplicitParam(
            name = "brandName",
            value = "기프티콘 브랜드 명",
            required = true,
            dataType = "string",
            paramType = "query",
            defaultValue = "None"
    )
    @GetMapping("/ocr/check_brand")
    public ResponseEntity<CheckBrandValidationDto> checkBrand(@RequestParam(value = "brandName") String brandName) throws Exception {

        Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName.toUpperCase()));

        try {
            if (byBrandName.isEmpty()) {
                CheckBrandValidationDto checkBrandValidationDto = new CheckBrandValidationDto(0,"Wrong Brand");
                return new ResponseEntity<CheckBrandValidationDto>(checkBrandValidationDto, HttpStatus.OK);
            }
            else {
                CheckBrandValidationDto checkBrandValidationDto = new CheckBrandValidationDto(1, brandName.toUpperCase());
                return new ResponseEntity<CheckBrandValidationDto>(checkBrandValidationDto, HttpStatus.OK);
            }
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }


        return null;

    }


    @ApiOperation(value = "detectTextAndValidation", notes = "0: success / 1: barcode error / 2: brand error / 3: both error", httpMethod = "GET")
    @ApiImplicitParam(
            name = "fileName",
            value = "gcp 이미지 파일 이름",
            required = true,
            dataType = "string",
            paramType = "query",
            defaultValue = "None"
    )
    @PostMapping("/ocr")
    public ResponseEntity<List<GifticonResponse>> detectText(@RequestBody DetectTextDto[] detectTextDtoList) throws Exception {

        List<GifticonResponse> finalResult = new ArrayList<>();

        int definePublisher = -1; // 0:gs , 1:kakao , 2:giftishow , 3:gifticon

        // GS&쿠폰
        String checkGS = "";

        // 카카오톡
        String checkKakao = "";

        // 기프티쇼
        String checkGiftishow = "";

        // 기프티콘
        String checkGifticon = "";

        int width = 0;
        int height = 0;



        try {
            for (DetectTextDto detectTextDto : detectTextDtoList) {

                String fileName = detectTextDto.getFileName();
                width = detectTextDto.getWidth();
                height = detectTextDto.getHeight();

                //System.out.println(width);
                //System.out.println(height);

                //System.out.println((double)218 / 800 * width);
                //System.out.println((double)1499 / 1661 * height);
                //System.out.println((double)584 / 800 * width);
                //System.out.println((double)1558 / 1661 * height);

                definePublisher = -1;

                GifticonResponse finalGifticonResponse = null;

                //System.out.println(fileName);

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


                        for (EntityAnnotation ress : newRes) {


                            if ((ress.getBoundingPoly().getVertices(0).getX() > (((double)288 / 430) * width)) &&
                                    (ress.getBoundingPoly().getVertices(0).getY() > (((double)245 / 400) * height)) &&
                                    (ress.getBoundingPoly().getVertices(2).getX() < (((double)384 / 430) * width)) &&
                                    (ress.getBoundingPoly().getVertices(2).getY() < (((double)272 / 400) * height))) {
                                checkGS += ((String) ress.getDescription().replaceAll("\n", "").replaceAll(" ", ""));
                                //System.out.println(checkGS);




                                String isGS = checkGS.replaceAll("\n", "").replaceAll(" ", "");
                                //System.out.println(isGS);
                                if (isGS.contains("GS&쿠폰")) {
                                    definePublisher = 0;

                                    //System.out.println(definePublisher);
                                    break;
                                }
                            }

                        }
                        for (EntityAnnotation ress : newRes) {

                            if ((ress.getBoundingPoly().getVertices(0).getX() > (((double)218 / 800) * width)) &&
                                    (ress.getBoundingPoly().getVertices(0).getY() > (((double)1499 / 1661) * height)) &&
                                    (ress.getBoundingPoly().getVertices(2).getX() < (((double)584 / 800) * width)) &&
                                    (ress.getBoundingPoly().getVertices(2).getY() < (((double)1558 / 1661) * height))) {
                                checkKakao += ress.getDescription();



                                String isKakao = checkKakao.replaceAll("\n", "").replaceAll(" ", "");
                                //System.out.print(isKakao);
                                if (isKakao.contains("kakaotalk")) {
                                    definePublisher = 1;
                                    break;
                                }
                            }

                        }

                        for (EntityAnnotation ress : newRes) {

                            if ((ress.getBoundingPoly().getVertices(0).getX() > (((double)56 / 450) * width)) &&
                                    (ress.getBoundingPoly().getVertices(0).getY() > (((double)413 / 630) * height)) &&
                                    (ress.getBoundingPoly().getVertices(2).getX() < (((double)398 / 450) * width)) &&
                                    (ress.getBoundingPoly().getVertices(2).getY() < (((double)444 / 630) * height))) {
                                checkGiftishow += ress.getDescription();


                                String isGiftishow = checkGiftishow.replaceAll("\n", "").replaceAll(" ", "");
                                //System.out.print(isGiftishow);
                                if (isGiftishow.contains("기프티쇼") || isGiftishow.contains("giftishow")) {
                                    definePublisher = 2;
                                    break;
                                }
                            }

                        }

                        for (EntityAnnotation ress : newRes) {


                            if ((ress.getBoundingPoly().getVertices(0).getX() > 0) &&
                                    (ress.getBoundingPoly().getVertices(0).getY() > (((double)312 / 480) * height)) &&
                                    (ress.getBoundingPoly().getVertices(2).getX() < (((double)320 / 320) * width)) &&
                                    (ress.getBoundingPoly().getVertices(2).getY() < (((double)333 / 480) * height))) {
                                checkGifticon += ress.getDescription();


                                String isGifticon = checkGifticon.replaceAll("\n", "").replaceAll(" ", "");
                                //System.out.print(isGifticon);
                                if (isGifticon.contains("gifticon")) {
                                    definePublisher = 3;
                                    break;
                                }
                            }

                        }

//                            else {
//                                break;
//
//
//                            }



                        //System.out.println(definePublisher);
                    }

                    List<String> checkVoucher = new ArrayList<>();
                    checkVoucher.add("금액권");
                    checkVoucher.add("상품권");
                    checkVoucher.add("모바일금액권");
                    checkVoucher.add("모바일상품권");
                    checkVoucher.add("기프티카드");
                    checkVoucher.add("기프트카드");
                    checkVoucher.add("디지털상품권");
                    checkVoucher.add("모바일교환권");
                    checkVoucher.add("원권");
                    checkVoucher.add("천원");
                    checkVoucher.add("만원");

                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            System.out.format("Error: %s%n", res.getError().getMessage());
                            break;
                        }
                        if (definePublisher==-1) {
                            GifticonResponse gifticonResponse = new GifticonResponse(0,-1,"", "", "", null, null,"",null,-1);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);



                            //System.out.println(gifticonResponse);
                            break;

                        }

                        List<EntityAnnotation> resList = res.getTextAnnotationsList();
                        List<EntityAnnotation> newRes = new ArrayList<>(resList.subList(1,resList.size()));

                        //System.out.println(newRes);

                        // isVoucher, publisher, brandName, productName, productImg,
                        // due, barcodeNum, barcodeImg, validation

                        // GS&쿠폰
                        if (definePublisher==0) {

                            String publisher = "GS&쿠폰";

                            // brandName
                            String checkGsBrand = "";
                            // productName
                            String checkGsProduct = "";
                            // due
                            String checkGsDue = "";
                            // barcodeNum
                            String checkGsBarcode = "";

                            String brandName = "";

                            String productName = "";

                            String barcodeNum = "";

                            String preDue = "";

                            int isVoucher = 0;

                            int validation = 0;

                            Map<String, String> expiration = new HashMap<>();

                            Map<String, String> productPosition = new HashMap<>();

                            Map<String, String> barcodePosition = new HashMap<>();

                            int price = -1;


                            for (EntityAnnotation gsRes : newRes) {
                                //System.out.println(gsRes.getBoundingPoly().getVertices(0));
                                //System.out.println(gsRes.getBoundingPoly().getVertices(2));



                                if ((gsRes.getBoundingPoly().getVertices(0).getX() > (((double)200 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(0).getY() > (((double)205 / 400) * height)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getX() < (((double)430 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getY() < (((double)250 / 400) * height))){
                                    checkGsBrand += gsRes.getDescription().replaceAll("\n","").replaceAll(" ","");
                                    //System.out.println(checkGsBrand);

                                }

                                String preBrandName = checkGsBrand.replaceAll("\n","").replaceAll(" ","");
                                //System.out.println(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replaceAll(chk,"").replaceAll(" ","");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }

                                if (brandName.contains("교환권")) {
                                    brandName = brandName.replaceAll("교환권","").replaceAll(" ","");
                                }




                                if ((gsRes.getBoundingPoly().getVertices(0).getX() > (((double)200 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(0).getY() > (((double)17 / 400) * height)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getX() < (((double)430 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getY() < (((double)120 / 400) * height))){
                                    checkGsProduct += gsRes.getDescription().replaceAll("\n","");
                                    //System.out.println(checkGsProduct);

                                }

                                String preProductName = checkGsProduct.replaceAll("\n","");

                                try {
                                    if (productName.contains("]")) {
                                        int chr_idx = productName.indexOf("]");
                                        String tempStr = productName.substring(0,chr_idx+1);
                                        productName = preProductName.replace(tempStr,"");

                                    }
                                    else {
                                        productName = preProductName;
                                    }
                                }
                                catch (IndexOutOfBoundsException e) {
                                    System.out.println(e);
                                }

                                //System.out.println(productName);


                                // isVoucher




                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if ((gsRes.getBoundingPoly().getVertices(0).getX() > (((double)199 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(0).getY() > (((double)143 / 400) * height)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getX() < (((double)329 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getY() < (((double)171 / 400) * height))) {
                                    checkGsDue += gsRes.getDescription().replaceAll("\n", "").replaceAll(" ", "");
                                    //System.out.println(checkGsDue);
                                }



                                preDue = checkGsDue.replaceAll("\n","").replaceAll(" ","");
                                //System.out.println(preDue);





                                if ((gsRes.getBoundingPoly().getVertices(0).getX() > (((double)96 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(0).getY() > (((double)354 / 400) * height)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getX() < (((double)330 / 430) * width)) &&
                                        (gsRes.getBoundingPoly().getVertices(2).getY() < (((double)381 / 400) * height))){
                                    checkGsBarcode += gsRes.getDescription().replaceAll("\n","").replaceAll(" ","").replaceAll("-","");
                                    //System.out.println(checkGsBarcode);

                                }

                                barcodeNum = checkGsBarcode.replaceAll("\n","").replaceAll(" ","").replaceAll("-","");
                                //System.out.println(barcodeNum);





                                // barcodeImg, productImg


                                productPosition.put("x1", "25");
                                productPosition.put("y1", "31");
                                productPosition.put("x2", "183");
                                productPosition.put("y2", "31");
                                productPosition.put("x3", "25");
                                productPosition.put("y3", "187");
                                productPosition.put("x4", "183");
                                productPosition.put("y4", "187");



                                barcodePosition.put("x1", "0");
                                barcodePosition.put("y1", "282");
                                barcodePosition.put("x2", "430");
                                barcodePosition.put("y2", "282");
                                barcodePosition.put("x3", "0");
                                barcodePosition.put("y3", "347");
                                barcodePosition.put("x4", "430");
                                barcodePosition.put("y4", "347");





                            }
                            int[] checkPriceList = IntStream.rangeClosed(1000,500000).toArray();

                            if (isVoucher == 1) {
                                if (productName.contains("만원")) {
                                    int nowIdx = productName.indexOf("만원");

                                    if (Character.isDigit(productName.charAt(nowIdx-2))) {
                                        String checkInt = productName.substring(nowIdx-2,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                    else if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else if (productName.contains("천원")) {
                                    int nowIdx = productName.indexOf("천원");

                                    if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*1000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    for (int prices : checkPriceList) {
                                        if (productName.contains(String.valueOf(prices))) {
                                            price = prices;
                                            break;
                                        }
                                    }

                                }
                            }

                            if (preDue.length()>0) {
                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));
                            }


                            // validation


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


                            GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,price,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);

                            System.out.println(gifticonResponse);
                            break;


                        }
                        else if (definePublisher==1) {

                            String publisher = "kakaotalk";

                            // brandName
                            String checkKakaoBrand = "";
                            // productName
                            String checkKakadoProduct = "";
                            // isVoucher
                            int isVoucher = 0;
                            // due
                            String checkKakaoDue = "";

                            String brandName = "";

                            String productName = "";

                            String checkKakaoBarcode = "";

                            String preBrandName = "";

                            String barcodeNum = "";

                            String preDue = "";

                            int validation = 0;

                            Map<String, String> expiration = new HashMap<>();

                            Map<String, String> productPosition = new HashMap<>();

                            Map<String, String> barcodePosition = new HashMap<>();

                            int price = -1;



                            for (EntityAnnotation kakaoRes : newRes) {



                                if ((kakaoRes.getBoundingPoly().getVertices(0).getX() > (((double)200 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(0).getY() > (((double)1214 / 1661) * height)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getX() < (((double)720 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getY() < (((double)1293 / 1661) * height))){
                                    checkKakaoBrand += kakaoRes.getDescription().replaceAll("\n","").replaceAll(" ","");

                                }

                                preBrandName = checkKakaoBrand.replace("\n","").replace(" ","");
                                //System.out.println(preBrandName);



                                for (String chk : checkVoucher) {

                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        //System.out.println(brandName);
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }

                                if (brandName.contains("교환권")) {
                                    brandName = brandName.replaceAll("교환권","").replaceAll(" ","");
                                }



                                if ((kakaoRes.getBoundingPoly().getVertices(0).getX() > (((double)40 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(0).getY() > (((double)798 / 1661) * height)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getX() < (((double)590 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getY() < (((double)944 / 1661) * height))){
                                    checkKakadoProduct += kakaoRes.getDescription();

                                }

                                productName = checkKakadoProduct.replace("\n","");
                                //System.out.println(productName);





                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }



                                if ((kakaoRes.getBoundingPoly().getVertices(0).getX() > (((double)420 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(0).getY() > (((double)1303 / 1661) * height)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getX() < (((double)717 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getY() < (((double)1373 / 1661) * height))){
                                    checkKakaoDue += kakaoRes.getDescription();

                                }

                                preDue = checkKakaoDue.replace("\n","").replace(" ","");
                                //System.out.println(preDue);






                                // barcodeNum


                                if ((kakaoRes.getBoundingPoly().getVertices(0).getX() > (((double)135 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(0).getY() > (((double)1105 / 1661) * height)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getX() < (((double)671 / 800) * width)) &&
                                        (kakaoRes.getBoundingPoly().getVertices(2).getY() < (((double)1188 / 1661) * height))){
                                    checkKakaoBarcode += kakaoRes.getDescription();

                                }

                                barcodeNum = checkKakaoBarcode.replace("\n","").replace(" ","");
                                //System.out.println(barcodeNum);






                                // barcodeImg, productImg


                                productPosition.put("x1", "71");
                                productPosition.put("y1", "80");
                                productPosition.put("x2", "723");
                                productPosition.put("y2", "80");
                                productPosition.put("x3", "71");
                                productPosition.put("y3", "678");
                                productPosition.put("x4", "723");
                                productPosition.put("y4", "678");




                                barcodePosition.put("x1", "71");
                                barcodePosition.put("y1", "975");
                                barcodePosition.put("x2", "723");
                                barcodePosition.put("y2", "975");
                                barcodePosition.put("x3", "71");
                                barcodePosition.put("y3", "1070");
                                barcodePosition.put("x4", "723");
                                barcodePosition.put("y4", "1070");





                            }
                            int[] checkPriceList = IntStream.rangeClosed(1000,500000).toArray();

                            if (isVoucher == 1) {
                                if (productName.contains("만원")) {
                                    int nowIdx = productName.indexOf("만원");

                                    if (Character.isDigit(productName.charAt(nowIdx-2))) {
                                        String checkInt = productName.substring(nowIdx-2,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                    else if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else if (productName.contains("천원")) {
                                    int nowIdx = productName.indexOf("천원");

                                    if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*1000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    for (int prices : checkPriceList) {
                                        if (productName.contains(String.valueOf(prices))) {
                                            price = prices;
                                            break;
                                        }
                                    }

                                }
                            }

                            if (preDue.length()>0) {
                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));
                            }


                            // validation


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

                            GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,price,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);



                            System.out.println(gifticonResponse);
                            break;

                        }
                        else if (definePublisher==2) {

                            String publisher = "giftishow";
                            // brandName
                            String checkGiftishowBrand = "";

                            String brandName = "";
                            // productName
                            String checkGiftishowProduct = "";

                            String productName = "";

                            // isVoucher
                            int isVoucher = 0;

                            // due
                            String checkGiftishowDue = "";

                            String preDue = "";

                            Map<String, String> expiration = new HashMap<>();

                            // barcodeNum
                            String checkGiftishowBarcode = "";

                            String barcodeNum = "";

                            // validation
                            int validation = 0;

                            Map<String, String> productPosition = new HashMap<>();

                            Map<String, String> barcodePosition = new HashMap<>();

                            int price = -1;


                            for (EntityAnnotation giftishowRes : newRes) {


                                if ((giftishowRes.getBoundingPoly().getVertices(0).getX() > (((double)105 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(0).getY() > (((double)579 / 630) * height)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getX() < (((double)450 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getY() < (((double)601 / 630) * height))){
                                    checkGiftishowBrand += giftishowRes.getDescription();

                                }

                                String preBrandName = checkGiftishowBrand.replace("\n","").replace(" ","");
                                //System.out.print(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }

                                if (brandName.contains("교환권")) {
                                    brandName = brandName.replaceAll("교환권","").replaceAll(" ","");
                                }



                                if ((giftishowRes.getBoundingPoly().getVertices(0).getX() > (((double)105 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(0).getY() > (((double)556 / 630) * height)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getX() < (((double)450 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getY() < (((double)580 / 630) * height))){
                                    checkGiftishowProduct += giftishowRes.getDescription();

                                }

                                productName = checkGiftishowProduct.replace("\n","");
                                //System.out.print(productName);





                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if ((giftishowRes.getBoundingPoly().getVertices(0).getX() > (((double)127 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(0).getY() > (((double)602 / 630) * height)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getX() < (((double)450 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getY() < (((double)630 / 630) * height))){
                                    checkGiftishowDue += giftishowRes.getDescription();

                                }

                                preDue = checkGiftishowDue.replace("\n","").replace(" ","");
                                //System.out.print(preDue);




                                if ((giftishowRes.getBoundingPoly().getVertices(0).getX() > (((double)74 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(0).getY() > (((double)504 / 630) * height)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getX() < (((double)377 / 450) * width)) &&
                                        (giftishowRes.getBoundingPoly().getVertices(2).getY() < (((double)535 / 630) * height))){
                                    checkGiftishowBarcode += giftishowRes.getDescription();

                                }

                                barcodeNum = checkGiftishowBarcode.replace("\n","").replace(" ","");
                                //System.out.print(barcodeNum);





                                // barcodeImg, productImg


                                productPosition.put("x1", "26");
                                productPosition.put("y1", "210");
                                productPosition.put("x2", "206");
                                productPosition.put("y2", "210");
                                productPosition.put("x3", "26");
                                productPosition.put("y3", "330");
                                productPosition.put("x4", "206");
                                productPosition.put("y4", "330");




                                barcodePosition.put("x1", "44");
                                barcodePosition.put("y1", "458");
                                barcodePosition.put("x2", "405");
                                barcodePosition.put("y2", "458");
                                barcodePosition.put("x3", "44");
                                barcodePosition.put("y3", "492");
                                barcodePosition.put("x4", "405");
                                barcodePosition.put("y4", "492");





                            }
                            int[] checkPriceList = IntStream.rangeClosed(1000,500000).toArray();

                            if (isVoucher == 1) {
                                if (productName.contains("만원")) {
                                    int nowIdx = productName.indexOf("만원");

                                    if (Character.isDigit(productName.charAt(nowIdx-2))) {
                                        String checkInt = productName.substring(nowIdx-2,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                    else if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else if (productName.contains("천원")) {
                                    int nowIdx = productName.indexOf("천원");

                                    if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*1000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    for (int prices : checkPriceList) {
                                        if (productName.contains(String.valueOf(prices))) {
                                            price = prices;
                                            break;
                                        }
                                    }

                                }
                            }


                            if (preDue.length()>0) {
                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));
                            }


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

                            GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,price,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);



                            System.out.println(gifticonResponse);
                            break;

                        }
                        else if (definePublisher==3) {

                            String publisher = "gifticon";

                            // brandName
                            String checkGifticonBrand = "";

                            String brandName = "";

                            String productName = "";

                            // productName
                            String checkGifticonProduct = "";

                            // isVoucher
                            int isVoucher = 0;

                            // due
                            String checkGifticonDue = "";

                            String preDue = "";

                            Map<String, String> expiration = new HashMap<>();

                            // barcodeNum
                            String checkGifticonBarcode = "";

                            String barcodeNum = "";

                            // validation
                            int validation = 0;

                            Map<String, String> productPosition = new HashMap<>();
                            Map<String, String> barcodePosition = new HashMap<>();

                            int price = -1;



                            for (EntityAnnotation gifticonRes : newRes) {



                                if ((gifticonRes.getBoundingPoly().getVertices(0).getX() > (((double)183 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(0).getY() > (((double)286 / 480) * height)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getX() < (((double)320 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getY() < (((double)314 / 480) * height))){
                                    checkGifticonBrand += gifticonRes.getDescription();

                                }

                                String preBrandName = checkGifticonBrand.replace("\n","").replace(" ","");
                                //System.out.println(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }

                                }

                                if (brandName.contains("교환권")) {
                                    brandName = brandName.replaceAll("교환권","").replaceAll(" ","");
                                }




                                if ((gifticonRes.getBoundingPoly().getVertices(0).getX() > (((double)126 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(0).getY() > (((double)214 / 480) * height)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getX() < (((double)320 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getY() < (((double)254 / 480) * height))){
                                    checkGifticonProduct += gifticonRes.getDescription();

                                }

                                productName = checkGifticonProduct.replace("\n","");
                                //System.out.print(productName);




                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if ((gifticonRes.getBoundingPoly().getVertices(0).getX() > (((double)196 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(0).getY() > (((double)273 / 480) * height)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getX() < (((double)320 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getY() < (((double)288 / 480) * height))){
                                    checkGifticonDue += gifticonRes.getDescription();

                                }

                                preDue = checkGifticonDue.replace("\n","").replace(" ","");
                                //System.out.print(preDue);




                                if ((gifticonRes.getBoundingPoly().getVertices(0).getX() > (((double)89 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(0).getY() > (((double)390 / 480) * height)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getX() < (((double)233 / 320) * width)) &&
                                        (gifticonRes.getBoundingPoly().getVertices(2).getY() < (((double)413 / 480) * height))){
                                    checkGifticonBarcode += gifticonRes.getDescription();

                                }

                                barcodeNum = checkGifticonBarcode.replace("\n","").replace(" ","");
                                //System.out.print(barcodeNum);




                                // barcodeImg, productImg


                                productPosition.put("x1", "28");
                                productPosition.put("y1", "217");
                                productPosition.put("x2", "114");
                                productPosition.put("y2", "217");
                                productPosition.put("x3", "28");
                                productPosition.put("y3", "299");
                                productPosition.put("x4", "114");
                                productPosition.put("y4", "229");




                                barcodePosition.put("x1", "80");
                                barcodePosition.put("y1", "345");
                                barcodePosition.put("x2", "237");
                                barcodePosition.put("y2", "345");
                                barcodePosition.put("x3", "80");
                                barcodePosition.put("y3", "383");
                                barcodePosition.put("x4", "237");
                                barcodePosition.put("y4", "383");




                            }
                            int[] checkPriceList = IntStream.rangeClosed(1000,500000).toArray();

                            if (isVoucher == 1) {
                                if (productName.contains("만원")) {
                                    int nowIdx = productName.indexOf("만원");

                                    if (Character.isDigit(productName.charAt(nowIdx-2))) {
                                        String checkInt = productName.substring(nowIdx-2,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                    else if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*10000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else if (productName.contains("천원")) {
                                    int nowIdx = productName.indexOf("천원");

                                    if (Character.isDigit(productName.charAt(nowIdx-1))) {
                                        String checkInt = productName.substring(nowIdx-1,nowIdx);
                                        try{
                                            int prePrice = Integer.parseInt(checkInt);
                                            price = prePrice*1000;
                                            //System.out.println(prePrice);
                                        }
                                        catch (NumberFormatException ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                                else {
                                    for (int prices : checkPriceList) {
                                        if (productName.contains(String.valueOf(prices))) {
                                            price = prices;
                                            break;
                                        }
                                    }

                                }
                            }

                            if (preDue.length()>0) {
                                expiration.put("Y",preDue.substring(0,4));
                                expiration.put("M",preDue.substring(5,7));
                                expiration.put("D",preDue.substring(8,10));
                            }


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



                            GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,price,publisher, brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);



                            System.out.println(gifticonResponse);
                            break;


                        }



                    }


                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new ResponseEntity<>(finalResult,HttpStatus.OK);

    }


}

