package com.example.popconback.gifticon.controller;


import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.OCR.CheckValidationDto;
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
    public ResponseEntity<CheckValidationDto> checkBarcode(@RequestParam(value = "barcodeNum") String barcodeNum) throws Exception {

        if (barcodeNum.length() == 0) {
            CheckValidationDto checkValidationDto = new CheckValidationDto(-1);
            return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.OK);
        }


        try {
            Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

            if (byBarcodeNum.isPresent()) {
                CheckValidationDto checkValidationDto = new CheckValidationDto(0);
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.OK);
            }
            else {
                CheckValidationDto checkValidationDto = new CheckValidationDto(1);
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.OK);
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
    public ResponseEntity<CheckValidationDto> checkBrand(@RequestParam(value = "brandName") String brandName) throws Exception {

        Optional<Brand> byBrandName = Optional.ofNullable(brandrepository.findByBrandName(brandName));

        try {
            if (byBrandName.isEmpty()) {
                CheckValidationDto checkValidationDto = new CheckValidationDto(0);
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.OK);
            }
            else {
                CheckValidationDto checkValidationDto = new CheckValidationDto(1);
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.OK);
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
    public ResponseEntity<List<GifticonResponse>> detectText(@RequestBody String[] fileNames) throws Exception {

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




        try {
            for (String fileName : fileNames) {

                definePublisher = -1;

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


                        for (EntityAnnotation ress : newRes) {


                            if (ress.getBoundingPoly().getVertices(0).getX() > 288 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 245 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 384 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 272) {
                                checkGS += ((String) ress.getDescription().replaceAll("\n", "").replaceAll(" ", ""));
                                //System.out.println(checkGS);


                                String isGS = checkGS.replaceAll("\n", "").replaceAll(" ", "");
                                System.out.println(isGS);
                                if (isGS.contains("GS&쿠폰")) {
                                    definePublisher = 0;

                                    System.out.println(definePublisher);
                                    break;
                                }
                            }

                        }
                        for (EntityAnnotation ress : newRes) {

                            if (ress.getBoundingPoly().getVertices(0).getX() > 218 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 1499 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 584 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 1558) {
                                checkKakao += ress.getDescription();


                                String isKakao = checkKakao.replaceAll("\n", "").replaceAll(" ", "");
                                System.out.print(isKakao);
                                if (isKakao.contains("kakaotalk")) {
                                    definePublisher = 1;
                                    break;
                                }
                            }

                        }

                        for (EntityAnnotation ress : newRes) {

                            if (ress.getBoundingPoly().getVertices(0).getX() > 56 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 413 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 398 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 444) {
                                checkGiftishow += ress.getDescription();


                                String isGiftishow = checkGiftishow.replaceAll("\n", "").replaceAll(" ", "");
                                System.out.print(isGiftishow);
                                if (isGiftishow.contains("기프티쇼") || isGiftishow.contains("giftishow")) {
                                    definePublisher = 2;
                                    break;
                                }
                            }

                        }

                        for (EntityAnnotation ress : newRes) {


                            if (ress.getBoundingPoly().getVertices(0).getX() > 0 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 312 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 320 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 333) {
                                checkGifticon += ress.getDescription();


                                String isGifticon = checkGifticon.replaceAll("\n", "").replaceAll(" ", "");
                                System.out.print(isGifticon);
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



                        System.out.println(definePublisher);}

                    List<String> checkVoucher = new ArrayList<>();
                    checkVoucher.add("금액권");
                    checkVoucher.add("상품권");
                    checkVoucher.add("모바일금액권");
                    checkVoucher.add("모바일상품권");
                    checkVoucher.add("기프티카드");
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
                            GifticonResponse gifticonResponse = new GifticonResponse(-1,-1,"", "", "", null, null,"",null,-1);

                            finalGifticonResponse = gifticonResponse;

                            finalResult.add(finalGifticonResponse);



                            System.out.println(gifticonResponse);
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



                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 200 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 205 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 430 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 250){
                                    checkGsBrand += gsRes.getDescription().replaceAll("\n","").replaceAll(" ","");
                                    //System.out.println(checkGsBrand);

                                }

                                String preBrandName = checkGsBrand.replaceAll("\n","").replaceAll(" ","");
                                System.out.println(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replaceAll(chk,"").replaceAll(" ","");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }




                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 200 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 17 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 430 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 120){
                                    checkGsProduct += gsRes.getDescription().replaceAll("\n","");
                                    //System.out.println(checkGsProduct);

                                }

                                productName = checkGsProduct.replaceAll("\n","");
                                System.out.println(productName);


                                // isVoucher




                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 199 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 143 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 329 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 171) {
                                    checkGsDue += gsRes.getDescription().replaceAll("\n", "").replaceAll(" ", "");
                                    //System.out.println(checkGsDue);
                                }



                                preDue = checkGsDue.replaceAll("\n","").replaceAll(" ","");
                                System.out.println(preDue);





                                if (gsRes.getBoundingPoly().getVertices(0).getX() > 96 &&
                                        gsRes.getBoundingPoly().getVertices(0).getY() > 354 &&
                                        gsRes.getBoundingPoly().getVertices(2).getX() < 330 &&
                                        gsRes.getBoundingPoly().getVertices(2).getY() < 381){
                                    checkGsBarcode += gsRes.getDescription().replaceAll("\n","").replaceAll(" ","").replaceAll("-","");
                                    System.out.println(checkGsBarcode);

                                }

                                barcodeNum = checkGsBarcode.replaceAll("\n","").replaceAll(" ","").replaceAll("-","");
                                System.out.println(barcodeNum);





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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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



                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 66 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 742 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 576 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 801){
                                    checkKakaoBrand += kakaoRes.getDescription().replaceAll("\n","").replaceAll(" ","");

                                }

                                preBrandName = checkKakaoBrand.replace("\n","").replace(" ","");
                                System.out.println(preBrandName);



                                for (String chk : checkVoucher) {

                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        System.out.println(brandName);
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }







                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 40 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 798 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 590 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 944){
                                    checkKakadoProduct += kakaoRes.getDescription();

                                }

                                productName = checkKakadoProduct.replace("\n","");
                                System.out.println(productName);





                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }



                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 420 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 1303 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 717 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 1373){
                                    checkKakaoDue += kakaoRes.getDescription();

                                }

                                preDue = checkKakaoDue.replace("\n","").replace(" ","");
                                System.out.println(preDue);






                                // barcodeNum


                                if (kakaoRes.getBoundingPoly().getVertices(0).getX() > 135 &&
                                        kakaoRes.getBoundingPoly().getVertices(0).getY() > 1105 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getX() < 671 &&
                                        kakaoRes.getBoundingPoly().getVertices(2).getY() < 1188){
                                    checkKakaoBarcode += kakaoRes.getDescription();

                                }

                                barcodeNum = checkKakaoBarcode.replace("\n","").replace(" ","");
                                System.out.println(barcodeNum);






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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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


                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 105 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 579 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 601){
                                    checkGiftishowBrand += giftishowRes.getDescription();

                                }

                                String preBrandName = checkGiftishowBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }
                                }




                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 105 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 556 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 580){
                                    checkGiftishowProduct += giftishowRes.getDescription();

                                }

                                productName = checkGiftishowProduct.replace("\n","");
                                System.out.print(productName);





                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 127 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 602 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 450 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 630){
                                    checkGiftishowDue += giftishowRes.getDescription();

                                }

                                preDue = checkGiftishowDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);




                                if (giftishowRes.getBoundingPoly().getVertices(0).getX() > 74 &&
                                        giftishowRes.getBoundingPoly().getVertices(0).getY() > 504 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getX() < 377 &&
                                        giftishowRes.getBoundingPoly().getVertices(2).getY() < 535){
                                    checkGiftishowBarcode += giftishowRes.getDescription();

                                }

                                barcodeNum = checkGiftishowBarcode.replace("\n","").replace(" ","");
                                System.out.print(barcodeNum);





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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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



                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 183 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 286 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 314){
                                    checkGifticonBrand += gifticonRes.getDescription();

                                }

                                String preBrandName = checkGifticonBrand.replace("\n","").replace(" ","");
                                System.out.println(preBrandName);



                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                    else {
                                        brandName = preBrandName;
                                    }

                                }




                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 126 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 214 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 254){
                                    checkGifticonProduct += gifticonRes.getDescription();

                                }

                                productName = checkGifticonProduct.replace("\n","");
                                System.out.print(productName);




                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                        break;
                                    }
                                }




                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 196 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 273 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 320 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 288){
                                    checkGifticonDue += gifticonRes.getDescription();

                                }

                                preDue = checkGifticonDue.replace("\n","").replace(" ","");
                                System.out.print(preDue);




                                if (gifticonRes.getBoundingPoly().getVertices(0).getX() > 89 &&
                                        gifticonRes.getBoundingPoly().getVertices(0).getY() > 390 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getX() < 233 &&
                                        gifticonRes.getBoundingPoly().getVertices(2).getY() < 413){
                                    checkGifticonBarcode += gifticonRes.getDescription();

                                }

                                barcodeNum = checkGifticonBarcode.replace("\n","").replace(" ","");
                                System.out.print(barcodeNum);




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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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
                                            System.out.println(prePrice);
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

