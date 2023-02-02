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

                        int definePublisher = -1; // 0:gs , 1:kakao , 2:giftishow , 3:gifticon

                        for (EntityAnnotation ress : newRes) {

                            // GS&쿠폰
                            String checkGS = "";

                            if (ress.getBoundingPoly().getVertices(0).getX() > 288 &&
                                    ress.getBoundingPoly().getVertices(0).getY() > 245 &&
                                    ress.getBoundingPoly().getVertices(2).getX() < 384 &&
                                    ress.getBoundingPoly().getVertices(2).getY() < 272){
                                checkGS += ress.getDescription();

                            }

                            String isGS = checkGS.replace("\n","").replace(" ","");
                            System.out.print(isGS);
                            if (isGS.contains("GS&쿠폰")) {
                                definePublisher = 0;
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
                                break;
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
                                break;
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
                                break;
                            }

                        }


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

                                String preBrand = checkGsBrand.replace("\n","").replace(" ","");
                                System.out.print(preBrand);






                            }

                        }

                    }


                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(finalResult,HttpStatus.OK);
    }
}


