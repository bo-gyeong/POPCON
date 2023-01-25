package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.domain.Bookmark;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.CreateBookmark.CreateBookmarkDto;
import com.example.popconback.gifticon.dto.CreateBookmark.ResponseCreateBookmarkDto;
import com.example.popconback.gifticon.dto.CreateGifticon.CreateGifticonDto;
import com.example.popconback.gifticon.dto.CreateGifticon.ResponseCreateGifticonDto;
import com.example.popconback.gifticon.dto.DeleteBookmark.DeleteBookmarkDto;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.SortGifticonDto;
import com.example.popconback.gifticon.dto.UpdateGifticon.ResponseUpdateGifticonDto;
import com.example.popconback.gifticon.dto.UpdateGifticon.UpdateGifticonDto;
import com.example.popconback.gifticon.service.GifticonService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Api(value = "GifticonController")
@SwaggerDefinition(tags = {@Tag(name = "GifticonContoller", description = "기프티콘 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/gifticons")
@Component
public class GifticonController {

    private final GifticonService gifticonService;

    @ApiOperation(value = "기프티콘 조회", notes = "유저의 기프티콘 정보 조회", httpMethod = "GET")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "email",
                value = "계정 이메일",
                required = true,
                dataType = "string",
                paramType = "path",
                defaultValue = "None"
        ),
        @ApiImplicitParam(
                name = "social",
                value = "소셜 로그인 구분",
                required = true,
                dataType = "string",
                paramType = "path",
                defaultValue = "None"
        )
    })
    @GetMapping("/{email}/{social}") //유저의 기프티콘 정보 DB에서 보내주기 // 이것도 만료되거나 사용한거 다보낼까?
    public ResponseEntity<List<GifticonDto>> gifticonList(@PathVariable String email, @PathVariable String social){
        return ResponseEntity.ok(gifticonService.gifticonList(email, social));
    }

    @ApiOperation(value = "CreateGifticon",
            notes = "기프티콘 정보 저장",
            httpMethod = "POST")
    @PostMapping("/gifticon") //기프티콘 정보 저장
    public ResponseEntity<List<ResponseCreateGifticonDto>> CreateGifticon (@RequestBody CreateGifticonDto[] createGifticonDtos){
        List<CreateGifticonDto> Dtolist = Arrays.asList(createGifticonDtos);
        return ResponseEntity.ok(gifticonService.createGifticon(Dtolist));

    }

    @ApiOperation(value = "즐겨찾기 등록", notes = "즐겨찾기 브랜드 등록", httpMethod = "POST")
    @PostMapping("/favorites") // 즐겨찾기 브랜드 등록
    public ResponseEntity<ResponseCreateBookmarkDto> CreateBookmark (@RequestBody CreateBookmarkDto createBookmarkDto){
        return ResponseEntity.ok(gifticonService.createBookmark(createBookmarkDto));
    }

    @ApiOperation(value = "즐겨찾기 삭제", notes = "즐겨찾기 브랜드 삭제", httpMethod = "DELETE")
    @DeleteMapping("/favorites") // 즐겨찾기 브랜드 삭제
    public ResponseEntity<Void> DeleteBookmark (@RequestBody DeleteBookmarkDto deleteBookmarkDto){
        gifticonService.deleteBookmark(deleteBookmarkDto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "기프티콘 정렬", notes = "기프티콘 브랜드별 정렬", httpMethod = "POST")
    @PostMapping("/brand") //기프티콘 브랜드별 정렬 // 사용한거 표시 제외하고 보낼지 말지 고민
    public ResponseEntity<List<GifticonDto>> SortGifticon (@RequestBody SortGifticonDto sortGifticonDto){
            return ResponseEntity.ok(gifticonService.sortGifticon(sortGifticonDto));
   }


    @ApiOperation(value = "기프티콘 수정", notes = "기프티콘 정보 수정", httpMethod = "POST")
    @ApiImplicitParam(
            name = "barcode_num",
            value = "바코드 넘버",
            required = true,
            dataType = "string",
            paramType = "path",
            defaultValue = "None"
    )
    @PostMapping("/{barcode_num}") //기프티콘 정보 수정
    public ResponseEntity<ResponseUpdateGifticonDto> UpdateGifticon (@RequestBody UpdateGifticonDto updateGifticonDto, @PathVariable String barcode_num ){
            return ResponseEntity.ok(gifticonService.updateGifticon(updateGifticonDto, barcode_num));
     }

    @ApiOperation(
            value = "기프티콘 삭제",
            notes = "기프티콘 삭제",
            httpMethod = "DELETE")
    @ApiImplicitParam(
            name = "barcode_num",
            value = "바코드 넘버",
            required = true,
            dataType = "string",
            paramType = "path",
            defaultValue = "None"
    )
    @DeleteMapping("/{barcode_num}") //기프티콘 삭제
    public ResponseEntity<Void> DeleteGifticon (@PathVariable String barcode_num){
        gifticonService.deleteGifticon(barcode_num);
        return ResponseEntity.ok().build();
    }


    //@Scheduled(cron = "0 0 09 * * ?")
    @ApiOperation(
            value = "기프티콘 상태 업데이트",
            notes = "기프티콘 유호기간 체크 후 상태 변경 / 서버용 API",
            httpMethod = "GET")
    @GetMapping("/check")// 유효기간 지난거 상태 변경
    public void Check_Overdate () {
        gifticonService.check_overdate();
    }
}
