package com.example.popconback.location.controller;

import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.ResponseListGifticonUserDto;
import com.example.popconback.location.dto.LocationSearchByBrandDto;
import com.example.popconback.location.dto.LocationShakeDto;
import com.example.popconback.gifticon.dto.ResponseBrandDto;
import com.example.popconback.gifticon.service.BrandService;
import com.example.popconback.gifticon.service.GifticonService;
import com.example.popconback.location.dto.LocationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
            httpMethod = "POST")
    @PostMapping({"/shake"})
    public ResponseEntity<List<ResponseBrandDto>> shakeSearch(@RequestBody LocationShakeDto locationShakeDto) {
        try {
            String email = locationShakeDto.getEmail();
            String social = locationShakeDto.getSocial();
            String x = locationShakeDto.getX();
            String y = locationShakeDto.getY();

            List<ResponseListGifticonUserDto> gifticons = gifticonService.gifticonList(email, social);

            List<String> brandList = new ArrayList<String>();

            for (ResponseListGifticonUserDto gifticon : gifticons) {
                try {
                    String nowBrand = gifticon.getBrand().getBrandName();
                    if (!brandList.contains(nowBrand)) {
                        brandList.add(nowBrand);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
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
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.set("Authorization", "KakaoAK " + this.key);
                    HttpEntity<String> httpEntity = new HttpEntity(httpHeaders);
                    URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{keyword}).queryParam("sort", new Object[]{"distance"}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{"20000"}).build().encode(StandardCharsets.UTF_8).toUri();
                    ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

                    //System.out.println(result.getBody().get("documents").getClass());

                    ArrayList temp = (ArrayList) result.getBody().get("documents");


                    List<Object> tempResultList = new ArrayList<Object>();
                    for (Object res : temp) {
                        try {
                            if (categoryCode.contains(((LinkedHashMap<String, String>) res).get("category_group_code"))) {
                                //System.out.println(((LinkedHashMap<String, String>) res).get("category_group_code"));
                                tempResultList.add(res);
                                resultList.add(res);

                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(e);
                        }

                    }
                    String distance = ((LinkedHashMap<String, String>) tempResultList.get(0)).get("distance");
                    minDistanceList.add(Integer.valueOf(distance));

                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }

            }
            Integer minDistance = Collections.min(minDistanceList);

            Integer minDistControl = minDistance + 50;

            for (Object res : resultList) {
                try {
                    String distance = ((LinkedHashMap<String, String>) res).get("distance");
                    if (Integer.valueOf(distance) <= minDistControl) {
                        finalResults.add(res);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }

            }

            for (Object fRes : finalResults) {
                try {
                    String fullCategory = ((LinkedHashMap<String, String>) fRes).get("category_name");
                    String[] PreBrand = fullCategory.split(" > ");
                    String brand = PreBrand[PreBrand.length - 1];

                    finalBrands.add(brand);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }

            }

            List<ResponseBrandDto> brandInfoList = new ArrayList<>();

            for (String brandName : finalBrands) {

                try {
                    ResponseBrandDto responseBrandDto = brandService.findBrand(brandName);
                    brandInfoList.add(responseBrandDto);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }
            }

            return new ResponseEntity<>(brandInfoList,HttpStatus.OK);
        } catch (NoSuchElementException | NullPointerException | NumberFormatException e) {
            List<ResponseBrandDto> blankList = new ArrayList<>();
            return new ResponseEntity<>(blankList,HttpStatus.OK);
        }

    }


    @ApiOperation(value = "localSearch",
            notes = "현위치 기반 기프티콘 사용가능 한 모든 매장",
            httpMethod = "POST")
    @PostMapping({"/search"})
    public ResponseEntity<List<Object>> localSearch(@RequestBody LocationShakeDto locationShakeDto){
        try {
            String email = locationShakeDto.getEmail();
            String social = locationShakeDto.getSocial();
            String x = locationShakeDto.getX();
            String y = locationShakeDto.getY();

            List<ResponseListGifticonUserDto> gifticons = gifticonService.gifticonList(email, social);

            List<String> brandList = new ArrayList<String>();

            for (ResponseListGifticonUserDto gifticon : gifticons) {
                try{
                    String nowBrand = gifticon.getBrand().getBrandName();
                    if (!brandList.contains(nowBrand)) {
                        brandList.add(nowBrand);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
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

                try {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.set("Authorization", "KakaoAK " + this.key);
                    HttpEntity<String> httpEntity = new HttpEntity(httpHeaders);
                    URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{keyword}).queryParam("sort", new Object[]{"distance"}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{"2000"}).build().encode(StandardCharsets.UTF_8).toUri();
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
                catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }

            }

            for (Object result : resultList) {

                try {
                    String fullCategory = ((LinkedHashMap<String, String>) result).get("category_name");
                    String[] PreBrand = fullCategory.split(" > ");
                    String brand = PreBrand[PreBrand.length-1];

                    String phone = ((LinkedHashMap<String, String>) result).get("phone");
                    String placeName = ((LinkedHashMap<String, String>) result).get("place_name");
                    String xPos = ((LinkedHashMap<String, String>) result).get("x");
                    String yPos = ((LinkedHashMap<String, String>) result).get("y");



                    ResponseBrandDto brandInfo = brandService.findBrand(brand);


                    //System.out.println(result);

                    LocationResponse locationResponse = new LocationResponse(phone, placeName, xPos, yPos, brandInfo);



                    ResponseEntity<LocationResponse> locationResponseBody = new ResponseEntity<LocationResponse>(locationResponse, HttpStatus.OK);
                    finalResults.add(locationResponseBody.getBody());

                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }


            }



            return new ResponseEntity<>(finalResults, HttpStatus.OK);
        }
        catch (NoSuchElementException | NullPointerException | NumberFormatException e) {
            System.out.println(e);
            List<Object> blankList = new ArrayList<>();
            return new ResponseEntity<>(blankList,HttpStatus.OK);

        }


    }

    @ApiOperation(value = "localSearchByBrand",
            notes = "현위치 기반 기프티콘 사용가능 한 지정 브랜드 매장",
            httpMethod = "POST")
    @PostMapping({"/search/byBrand"})
    public ResponseEntity<List<Object>> searchByBrand(@RequestBody LocationSearchByBrandDto locationSearchByBrandDto) {
        try {

            LocationShakeDto locationShakeDto = new LocationShakeDto();

            String email = locationSearchByBrandDto.getEmail();
            String social = locationSearchByBrandDto.getSocial();
            String x = locationSearchByBrandDto.getX();
            String y = locationSearchByBrandDto.getY();
            String brandName = locationSearchByBrandDto.getBrandName();

            locationShakeDto.setEmail(email);
            locationShakeDto.setSocial(social);
            locationShakeDto.setX(x);
            locationShakeDto.setY(y);

            List<Object> finalResults = localSearch(locationShakeDto).getBody();

            List<Object> nowResults = new ArrayList<>();


            for (Object result : finalResults) {
                try {
                    ResponseBrandDto nowBrandInfo = ((LocationResponse)result).getBrandInfo();
                    String nowBrand = nowBrandInfo.getBrandName();

                    if (brandName.equals(nowBrand)) {
                        nowResults.add(result);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }

            }
            return new ResponseEntity<>(nowResults,HttpStatus.OK);
        }
        catch (NoSuchElementException | NullPointerException | NumberFormatException e) {
            System.out.println(e);
            List<Object> blankList = new ArrayList<>();
            return new ResponseEntity<>(blankList,HttpStatus.OK);
        }
        }




}
