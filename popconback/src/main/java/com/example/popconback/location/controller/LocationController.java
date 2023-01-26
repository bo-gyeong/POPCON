package com.example.popconback.location.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.GifticonResponse;
import com.example.popconback.gifticon.dto.ResponseBrandDto;
import com.example.popconback.gifticon.service.BrandService;
import com.example.popconback.gifticon.service.GifticonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Api(value = "LocationController")
@SwaggerDefinition(tags = {@Tag(name = "LocationController",
        description = "위치 기반 검색 컨트롤러")})
@RequestMapping(value = "/api/v1/local")
@RestController
public class LocationController {
    @Value("${kakao.apikey}")
    private String key;
    private String url = "https://dapi.kakao.com/v2/local/search/keyword";
    @Autowired
    GifticonService gifticonService;

    @Autowired
    BrandService brandService;
    private Object res;


    @ApiOperation(value = "shakeSearch",
            notes = "흔들었을때 사용가능한 주변 매장 브랜드",
            httpMethod = "GET")
    @GetMapping({"/shake"})
    public List<ResponseBrandDto> shakeSearch(@RequestParam String email, @RequestParam String social,  @RequestParam(required = false) String x, @RequestParam(required = false) String y, @RequestParam(required = false) String radius) throws Exception{


        List<GifticonDto> gifticons = gifticonService.gifticonList(email, social);

        List<String> brandList = new ArrayList<String>();

        for (GifticonDto gifticon : gifticons) {
            String nowBrand = gifticon.getBrandName();
            if (!brandList.contains(nowBrand)) {
                brandList.add(nowBrand);
            }

        }

        List<String> finalBrands = new ArrayList<String>();
        List<Object> finalResults = new ArrayList<Object>();
        List<Object> resultList = new ArrayList<Object>();
        List<Integer> minDistanceList = new ArrayList<Integer>();


        List<String> categoryCode = new ArrayList<>();
        //[MT1, CS2, OL7, FD6, CE7]
        categoryCode.add("MT1");
        categoryCode.add("CS2");
        categoryCode.add("OL7");
        categoryCode.add("FD6");
        categoryCode.add("CE7");


        for (String keyword : brandList) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "KakaoAK " + this.key);
            HttpEntity<String> httpEntity = new HttpEntity(httpHeaders);
            URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{keyword}).queryParam("sort", new Object[]{"distance"}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{"500"}).build().encode(StandardCharsets.UTF_8).toUri();
            ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

            //System.out.println(result.getBody().get("documents").getClass());

            ArrayList temp = (ArrayList)result.getBody().get("documents");


            List<Object> tempResultList = new ArrayList<Object>();
            for (Object res : temp) {

                if (categoryCode.contains(((LinkedHashMap<String, String>) res).get("category_group_code"))) {
                    //System.out.println(((LinkedHashMap<String, String>) res).get("category_group_code"));
                    tempResultList.add(res);
                    resultList.add(res);

                }

            }
            String distance = ((LinkedHashMap<String, String>) tempResultList.get(0)).get("distance");
            minDistanceList.add(Integer.valueOf(distance));


        }
        Integer minDistance = Collections.min(minDistanceList);

        Integer minDistControl = minDistance + 50;

        for (Object res : resultList) {
            String distance = ((LinkedHashMap<String, String>) res).get("distance");
            if (Integer.valueOf(distance)<= minDistControl) {
                finalResults.add(res);
            }
        }

        for (Object fRes : finalResults) {

            String fullCategory = ((LinkedHashMap<String, String>) fRes).get("category_name");
            String[] PreBrand = fullCategory.split(" > ");
            String brand = PreBrand[PreBrand.length-1];

            finalBrands.add(brand);
        }

        List<ResponseBrandDto> brandInfoList = new ArrayList<>();

        for (String brandName : finalBrands) {
            ResponseBrandDto responseBrandDto = brandService.findBrand(brandName);
            brandInfoList.add(responseBrandDto);
        }


        return brandInfoList;

    }

    @ApiOperation(value = "localSearch",
            notes = "현위치 기반 기프티콘 사용가능 한 모든 매장",
            httpMethod = "GET")
    @GetMapping({"/search"})
    public List<Object> localSearch(@RequestParam String email, @RequestParam String social,  @RequestParam(required = false) String x, @RequestParam(required = false) String y, @RequestParam(required = false) String radius) throws Exception{

        List<GifticonDto> gifticons = gifticonService.gifticonList(email, social);

        List<String> brandList = new ArrayList<String>();

        for (GifticonDto gifticon : gifticons) {
            String nowBrand = gifticon.getBrandName();
            if (!brandList.contains(nowBrand)) {
                brandList.add(nowBrand);
            }

        }

        List<Object> finalResults = new ArrayList<Object>();
        List<Object> resultList = new ArrayList<Object>();


        List<String> categoryCode = new ArrayList<>();
        //[MT1, CS2, OL7, FD6, CE7]
        categoryCode.add("MT1");
        categoryCode.add("CS2");
        categoryCode.add("OL7");
        categoryCode.add("FD6");
        categoryCode.add("CE7");


        for (String keyword : brandList) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "KakaoAK " + this.key);
            HttpEntity<String> httpEntity = new HttpEntity(httpHeaders);
            URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{keyword}).queryParam("sort", new Object[]{"distance"}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{radius}).build().encode(StandardCharsets.UTF_8).toUri();
            ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

            //System.out.println(result.getBody().get("documents").getClass());

            ArrayList temp = (ArrayList)result.getBody().get("documents");


            List<Object> tempResultList = new ArrayList<Object>();
            for (Object res : temp) {

                if (categoryCode.contains(((LinkedHashMap<String, String>) res).get("category_group_code"))) {
                    //System.out.println(((LinkedHashMap<String, String>) res).get("category_group_code"));

                    resultList.add(res);

                }

            }

        }

        for (Object result : resultList) {

            String fullCategory = ((LinkedHashMap<String, String>) result).get("category_name");
            String[] PreBrand = fullCategory.split(" > ");
            String brand = PreBrand[PreBrand.length-1];

            String phone = ((LinkedHashMap<String, String>) result).get("phone");
            String placeName = ((LinkedHashMap<String, String>) result).get("place_name");
            String xPos = ((LinkedHashMap<String, String>) result).get("x");
            String yPos = ((LinkedHashMap<String, String>) result).get("y");

            System.out.println(result);

            LocationResponse locationResponse = new LocationResponse(phone, placeName, xPos, yPos, brand);
            ResponseEntity<LocationResponse> locationResponseBody = new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.OK);
            finalResults.add(locationResponseBody.getBody());
        }



        return finalResults;
    }
}
