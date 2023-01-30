package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.dto.GifticonResponse;
import com.example.popconback.gifticon.service.S3Service;
import com.google.cloud.vision.v1.*;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.*;

@Api(value = "GoogleOcrController")
@SwaggerDefinition(tags = {@Tag(name = "GoogleOcrController",
        description = "구글 OCR 컨트롤러")})
@Controller
@RequestMapping(value = "/api/v1/gcp")
public class GoogleOcrController {
    private S3Service s3Service;

    private static final String BASE_PATH = "C:\\upload\\";

    @ApiOperation(value = "텍스트 추출", notes = "기프티콘 이미지 텍스트 추출", httpMethod = "GET")
    @ApiImplicitParam(
            name = "filePath",
            value = "이미지 파일 경로 url",
            required = true,
            dataType = "string",
            paramType = "query",
            defaultValue = "None"
    )
    @GetMapping("/ocr")
    public ResponseEntity<GifticonResponse> detectText(@RequestParam(value = "fileName") String fileName) throws Exception {


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

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    //System.out.format("%s", annotation.getDescription());

                    String descript = annotation.getDescription();


                    if(descript.contains("GS&쿠폰")) {

                        String onlyWords = descript.replace("\n","");

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

                        GifticonResponse gifticonResponse = new GifticonResponse("GS&쿠폰", lineList.get(lineList.size()-3), findProductName[0], productPosition, expiration,lineList.get(lineList.size()-1).replace("-",""),barcodePosition);


                        return new ResponseEntity<GifticonResponse>(gifticonResponse, HttpStatus.OK);

                    }
                    else if (descript.contains("kakaotalk")) {

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

                        GifticonResponse gifticonResponse = new GifticonResponse("kakaotalk", lineList.get(0), findProductName[0], productPosition, expiration,lineList.get(lineList.size()-5).replace(" ",""),barcodePosition);


                        return new ResponseEntity<GifticonResponse>(gifticonResponse, HttpStatus.OK);

                    }
                    else if (descript.contains("giftishow")) {

                        String onlyWord = descript.replace("\n","");
                        String onlyWords = onlyWord.replace(" ","");

                        String findProductName = onlyWords.split("상품명:")[1];
                        String productName = findProductName.split("교환처:")[0];

                        String findBrandName = findProductName.split("교환처:")[1];
                        String brandName = findBrandName.split("유효기간:")[0];

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

                        GifticonResponse gifticonResponse = new GifticonResponse("giftishow", brandName, productName, productPosition, expiration,barcodeNum,barcodePosition);



                        return new ResponseEntity<GifticonResponse>(gifticonResponse, HttpStatus.OK);

                    }
                    else if (descript.contains("gifticon")) {

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
                        String brandName = fullBrand[1].trim();

                        String[] findProductName = onlyWords.split(brandName);

                        String ProductName = findProductName[1].split("교환수량")[0];

                        String fullExpiration = lineList.get(lineList.size()-3);

                        Map<String, String> expiration = new HashMap<>();

                        expiration.put("Y",fullExpiration.substring(0,4));
                        expiration.put("M",fullExpiration.substring(5,7));
                        expiration.put("D",fullExpiration.substring(8,10));


                        GifticonResponse gifticonResponse = new GifticonResponse("gifticon", brandName, ProductName, productPosition, expiration,lineList.get(lineList.size()-2).replace(" ",""),barcodePosition);



                        return new ResponseEntity<GifticonResponse>(gifticonResponse, HttpStatus.OK);

                    }
                    else {
                        GifticonResponse gifticonResponse = new GifticonResponse("직접 입력해주세요.", "직접 입력해주세요.", "직접 입력해주세요.", null, null,"직접 입력해주세요.",null);

                        File file = new File(filePath);
                        if(file.delete()){
                            System.out.println("파일삭제 성공");
                        }else{
                            System.out.println("파일삭제 실패");
                        }

                        return new ResponseEntity<GifticonResponse>(gifticonResponse, HttpStatus.NOT_FOUND);
                    }




                    //System.out.format ("Position : %s%n", annotation.getBoundingPoly());
                }
            }
        }


        return null;
    }

}

