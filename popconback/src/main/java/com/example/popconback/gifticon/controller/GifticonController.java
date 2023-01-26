package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.dto.CreateFavorites.CreateFavoritesDto;
import com.example.popconback.gifticon.dto.CreateFavorites.ResponseCreateFavoritesDto;
import com.example.popconback.gifticon.dto.CreateGifticon.CreateGifticonDto;
import com.example.popconback.gifticon.dto.CreateGifticon.ResponseCreateGifticonDto;
import com.example.popconback.gifticon.dto.DeleteFavorites.DeleteFavoritesDto;
import com.example.popconback.gifticon.dto.DeleteGifticon.DeleteGifticonDto;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.ListFavorites.ResponseListFavoritesDto;
import com.example.popconback.gifticon.dto.ListGifticonUser.ResponseListGifticonUserDto;
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
import java.util.List;


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
    public ResponseEntity<List<ResponseListGifticonUserDto>> gifticonList(@PathVariable String email, @PathVariable String social){
        return ResponseEntity.ok(gifticonService.gifticonList(email, social));
    }

    @ApiOperation(value = "기프티콘 저장", notes = "기프티콘 정보 저장", httpMethod = "POST")
    @PostMapping("") //기프티콘 정보 저장
    public ResponseEntity<List<ResponseCreateGifticonDto>> CreateGifticon (@RequestBody CreateGifticonDto[] createGifticonDtos){
        List<CreateGifticonDto> Dtolist = Arrays.asList(createGifticonDtos);
        return ResponseEntity.ok(gifticonService.createGifticon(Dtolist));

    }

    @ApiOperation(value = "즐겨찾기 등록", notes = "즐겨찾기 브랜드 등록", httpMethod = "POST")
    @PostMapping("/favorites") // 즐겨찾기 브랜드 등록
    public ResponseEntity<ResponseCreateFavoritesDto> CreateFavorites (@RequestBody CreateFavoritesDto createFavoritesDto){
        return ResponseEntity.ok(gifticonService.createFavorites(createFavoritesDto));
    }

    @ApiOperation(value = "즐겨찾기 삭제", notes = "즐겨찾기 브랜드 삭제", httpMethod = "DELETE")
    @DeleteMapping("/favorites") // 즐겨찾기 브랜드 삭제
    public ResponseEntity<Void> DeleteFavorites (@RequestBody DeleteFavoritesDto deleteFavoritesDto){
        gifticonService.deleteFavorites(deleteFavoritesDto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "즐겨찾기 조회", notes = "즐겨찾기 브랜드 조회", httpMethod = "GET")
    @GetMapping("/favorites/{email}/{social}") // 즐겨찾기 브랜드 등록
    public ResponseEntity<List<ResponseListFavoritesDto>> CreateFavorites (@PathVariable String email, @PathVariable String social){
        return ResponseEntity.ok(gifticonService.listFavorites(email,social));
    }



    @ApiOperation(value = "기프티콘 정렬", notes = "기프티콘 브랜드별 정렬", httpMethod = "GET") // get 으로 수정
    @GetMapping("/brand") //기프티콘 브랜드별 정렬 // 사용한거 표시 제외하고 보낼지 말지 고민
    public ResponseEntity<List<ResponseListGifticonUserDto>> SortGifticon (@RequestBody SortGifticonDto sortGifticonDto){
            return ResponseEntity.ok(gifticonService.sortGifticon(sortGifticonDto));
   }


    @ApiOperation(value = "기프티콘 수정", notes = "기프티콘 정보 수정", httpMethod = "PUT")
    @PutMapping("") //기프티콘 정보 수정
    public ResponseEntity<ResponseUpdateGifticonDto> UpdateGifticon (@RequestBody UpdateGifticonDto updateGifticonDto){
            return ResponseEntity.ok(gifticonService.updateGifticon(updateGifticonDto));
     }

    @ApiOperation(value = "기프티콘 삭제", notes = "기프티콘 삭제", httpMethod = "DELETE")
    @DeleteMapping("") //기프티콘 삭제
    public ResponseEntity<Void> DeleteGifticon (@RequestBody DeleteGifticonDto deleteGifticonDto){
        gifticonService.deleteGifticon(deleteGifticonDto.getBarcodeNum());
        return ResponseEntity.ok().build();
    }


    //@Scheduled(cron = "0 0 09 * * ?")
    @ApiOperation(value = "기프티콘 상태 업데이트", notes = "기프티콘 유호기간 체크 후 상태 변경 / 서버용 API", httpMethod = "GET")
    @GetMapping("/check")// 유효기간 지난거 상태 변경
    public void Check_Overdate () {
        gifticonService.check_overdate();
    }
}
