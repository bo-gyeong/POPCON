package com.example.popconback.location.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.GifticonResponse;
import com.example.popconback.gifticon.service.GifticonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class LocationController {
    @Value("${kakao.apikey}")
    private String key;
    private String url = "https://dapi.kakao.com/v2/local/search/keyword";
    @Autowired
    GifticonService gifticonService;
    private Object res;

    @GetMapping({"/shake"})
    public List<String> shakeSearch(@RequestParam String email, @RequestParam String social,  @RequestParam(required = false) String x, @RequestParam(required = false) String y, @RequestParam(required = false) String radius) throws Exception{

        List<Gifticon> gifticons = gifticonService.gifticonList(email, social);

        List<String> brandList = new ArrayList<String>();

        for (Gifticon gifticon : gifticons) {
            String nowBrand = gifticon.getBrand().getBrandName();
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
            URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{keyword}).queryParam("sort", new Object[]{"distance"}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{radius}).build().encode(StandardCharsets.UTF_8).toUri();
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


        return finalBrands;

    }


    @GetMapping({"/local"})
    public List<Object> localSearch(@RequestParam String email, @RequestParam String social,  @RequestParam(required = false) String x, @RequestParam(required = false) String y, @RequestParam(required = false) String radius) throws Exception{

        List<Gifticon> gifticons = gifticonService.gifticonList(email, social);

        List<String> brandList = new ArrayList<String>();

        for (Gifticon gifticon : gifticons) {
            String nowBrand = gifticon.getBrand().getBrandName();
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
