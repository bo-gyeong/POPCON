package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.dto.ResponseBrandDto;
import com.example.popconback.gifticon.service.BrandService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "BrandController")
@SwaggerDefinition(tags = {@Tag(name = "BrandContoller", description = "브랜드 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/brands")
@Component
public class BrandController {

    private final BrandService brandService;

    @ApiOperation(value = "brandListOrderByGifticonCount", notes = "기프티콘 개수 많은 순서대로 정렬된 브랜드 리스트", httpMethod = "GET")
    @GetMapping("/orderby_gifticon")
    public ResponseEntity<List<ResponseBrandDto>> brandListOrderByGifticonCount(){
        return ResponseEntity.ok(brandService.brandListOrderByGifticonCount());
    }

}
