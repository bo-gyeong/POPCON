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

        try {
            Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));

            if (byBarcodeNum.isPresent()) {
                CheckValidationDto checkValidationDto = new CheckValidationDto(0);
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.BAD_REQUEST);
            } else {
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
                return new ResponseEntity<CheckValidationDto>(checkValidationDto, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<List<GifticonResponse>> detectText(String[] fileNames) throws Exception {

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

                        for (EntityAnnotation ress : newRes) {
                            if (ress.getBoundingPoly().getVertices(0).getX() > 200 && ress.getBoundingPoly().getVertices(2).getY() < 117){
                                descList.add(ress.getDescription());
                                System.out.println(ress.getDescription());
                            }

                        }



                        // For full list of available annotations, see http://g.co/cloud/vision/docs
                        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                            //System.out.format("%s", annotation.getDescription());

                            String descript = annotation.getDescription();
                            String trimDescript = descript.replace(" ","");


                            //System.out.println(descript);

                            // 금액권,상품권,모바일금액권,모바일상품권,기프티카드,디지털상품권,모바일교환권
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
                            checkVoucher.add("0원");




                            if(trimDescript.contains("GS&쿠폰")) {

                                String onlyWords = descript.replace("\n","");

                                System.out.println(onlyWords);



                                String[] findProductName = onlyWords.split("유효기간");

                                String[] lines = descript.split("\n");

                                List<String> lineList = new ArrayList<>(Arrays.asList(lines));

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

                                String fullExpiration = lineList.get(lineList.size()-5);
                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",fullExpiration.substring(0,4));
                                expiration.put("M",fullExpiration.substring(5,7));
                                expiration.put("D",fullExpiration.substring(8,10));

                                String barcodeNum = lineList.get(lineList.size()-1).replace("-","");

                                String preBrandName = lineList.get(lineList.size()-3);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }



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

                                int isVoucher = 0;

                                //String productName = findProductName[0];
                                String productName = lineList.get(lineList.size()-7);

                                System.out.println(productName);

                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                    }
                                }



                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,"GS&쿠폰", brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);

                                finalGifticonResponse = gifticonResponse;

                                //System.out.println(gifticonResponse);
                                break;





                            }
                            else if (trimDescript.contains("kakaotalk")) {

                                String onlyWords = descript.replace("\n","");

                                String[] findProductName = onlyWords.split("교환처");

                                String[] lines = descript.split("\n");

                                List<String> lineList = new ArrayList<>(Arrays.asList(lines));

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

                                String fullExpiration = lineList.get(lineList.size()-3);
                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",fullExpiration.substring(0,4));
                                expiration.put("M",fullExpiration.substring(6,8));
                                expiration.put("D",fullExpiration.substring(10,12));

                                String barcodeNum = lineList.get(lineList.size()-5).replace(" ","");
                                String preBrandName = lineList.get(0);


                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }



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



                                int isVoucher = 0;

                                String productName = findProductName[0];

                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                    }
                                }


                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,"kakaotalk", brandName, productName, productPosition, expiration,barcodeNum,barcodePosition, validation);


                                finalGifticonResponse = gifticonResponse;

                                break;


                            }
                            else if (trimDescript.contains("giftishow")) {

                                String onlyWord = descript.replace("\n","");
                                String onlyWords = onlyWord.replace(" ","");

                                String findProductName = onlyWords.split("상품명:")[1];
                                String productName = findProductName.split("교환처:")[0];

                                String findBrandName = findProductName.split("교환처:")[1];
                                String preBrandName = findBrandName.split("유효기간:")[0];

                                String findExpiration = findBrandName.split("유효기간:")[1];

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",findExpiration.substring(1,5));
                                expiration.put("M",findExpiration.substring(6,8));
                                expiration.put("D",findExpiration.substring(9,11));


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



                                String findBarcode = onlyWords.split("상품명:")[0];
                                String barcodeNum = findBarcode.substring(findBarcode.length()-12,findBarcode.length());

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }

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


                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                    }
                                }

                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,"giftishow", brandName, productName, productPosition, expiration,barcodeNum,barcodePosition,validation);


                                finalGifticonResponse = gifticonResponse;

                                break;



                            }
                            else if (trimDescript.contains("gifticon")) {

                                String onlyWords = descript.replace("\n","");



                                String[] lines = descript.split("\n");

                                List<String> lineList = new ArrayList<>(Arrays.asList(lines));

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

                                String[] fullBrand = lineList.get(lineList.size()-5).split("1");
                                String preBrandName = fullBrand[1].trim();

                                String[] findProductName = onlyWords.split(preBrandName);

                                String productName = findProductName[1].split("교환수량")[0];

                                String fullExpiration = lineList.get(lineList.size()-3);

                                Map<String, String> expiration = new HashMap<>();

                                expiration.put("Y",fullExpiration.substring(0,4));
                                expiration.put("M",fullExpiration.substring(5,7));
                                expiration.put("D",fullExpiration.substring(8,10));



                                String barcodeNum = lineList.get(lineList.size()-2).replace(" ","");
                                System.out.println(barcodeNum);

                                String brandName = "";

                                for (String chk : checkVoucher) {
                                    if (preBrandName.contains(chk)) {
                                        brandName = preBrandName.replace(chk, "").replace(" ", "");
                                        break;
                                    }
                                }

                                int validation = 0;

                                try {
                                    Optional<Gifticon> byBarcodeNum = Optional.ofNullable(gifticonRepository.findByBarcodeNum(barcodeNum));
                                    System.out.println(byBarcodeNum.isPresent());

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


                                int isVoucher = 0;


                                for (String word : checkVoucher) {
                                    if(productName.contains(word)) {
                                        isVoucher = 1;
                                    }
                                }


                                GifticonResponse gifticonResponse = new GifticonResponse(isVoucher,"gifticon", brandName, productName, productPosition, expiration,barcodeNum,barcodePosition,validation);

                                finalGifticonResponse = gifticonResponse;

                                break;




                            }
                            else {
                                GifticonResponse gifticonResponse = new GifticonResponse(-1,"직접 입력해주세요.", "직접 입력해주세요.", "직접 입력해주세요.", null, null,"직접 입력해주세요.",null,-1);

                                finalGifticonResponse = gifticonResponse;

                                break;

                            }






                            //System.out.format ("Position : %s%n", annotation.getBoundingPoly());
                        }
                        finalResult.add(finalGifticonResponse);
                    }
                }

            }

            //System.out.println(finalResult);
            //return finalResult;
        }
        catch (NullPointerException e) {
            System.out.println(e);
        }


        System.out.println(finalResult);

        return new ResponseEntity<>(finalResult,HttpStatus.OK);


    }


}

