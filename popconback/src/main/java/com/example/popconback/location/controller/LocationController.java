package com.example.popconback.location.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    public LocationController() {
    }

    @GetMapping({"/local"})
    public Map callApi(@RequestParam String query, @RequestParam(required = false) String x, @RequestParam(required = false) String y, @RequestParam(required = false) String radius) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + this.key);
        HttpEntity<String> httpEntity = new HttpEntity(httpHeaders);
        URI targetUrl = UriComponentsBuilder.fromUriString(this.url).queryParam("query", new Object[]{query}).queryParam("x", new Object[]{x}).queryParam("y", new Object[]{y}).queryParam("radius", new Object[]{radius}).build().encode(StandardCharsets.UTF_8).toUri();
        ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);
        return (Map)result.getBody();
    }
}
