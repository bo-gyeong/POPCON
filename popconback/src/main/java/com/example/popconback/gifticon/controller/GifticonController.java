package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.dto.Favorites.CreateFavorites.CreateFavoritesDto;
import com.example.popconback.gifticon.dto.Favorites.CreateFavorites.ResponseCreateFavoritesDto;
import com.example.popconback.gifticon.dto.Gifticon.CreateGifticon.CreateGifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.CreateGifticon.ResponseCreateGifticonDto;
import com.example.popconback.gifticon.dto.Favorites.DeleteFavorites.DeleteFavoritesDto;
import com.example.popconback.gifticon.dto.Gifticon.DeleteGifticon.DeleteGifticonDto;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.HistoryGifticon.GifticonHistoryDto;
import com.example.popconback.gifticon.dto.Gifticon.HistoryGifticon.ResponseGifticonHistoryDto;
import com.example.popconback.gifticon.dto.Favorites.ListFavorites.ResponseListFavoritesDto;
import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.ResponseListGifticonUserDto;
import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.ListGifticonUserDto;
import com.example.popconback.gifticon.dto.Gifticon.UpdateGifticon.ResponseUpdateGifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.UpdateGifticon.UpdateGifticonDto;
import com.example.popconback.gifticon.service.GifticonService;
import com.example.popconback.user.dto.UserDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.tool.schema.ast.SqlScriptParserException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Api(value = "GifticonController")
@SwaggerDefinition(tags = {@Tag(name = "GifticonContoller", description = "기프티콘 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/gifticons")
@Component
public class GifticonController {

    private final GifticonService gifticonService;

    @GetMapping("/test")
    ResponseEntity<String> test(Authentication authentication){
        UserDto user = (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok().body(user.getEmail()+user.getSocial()+"test");
    }

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
    public ResponseEntity<List<ResponseListGifticonUserDto>> gifticonList(@PathVariable String email, @PathVariable String social,Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();

        return ResponseEntity.ok(gifticonService.gifticonList(us.getEmail(), us.getSocial()));
    }

    @ApiOperation(value = "기프티콘 조회 지도에서", notes = "유저의 기프티콘 정보 조회", httpMethod = "GET")
    @GetMapping("/{email}/{social}/map") //유저의 기프티콘 정보 DB에서 보내주기 // 이것도 만료되거나 사용한거 다보낼까?
    public ResponseEntity<List<ResponseListGifticonUserDto>> gifticonListForMap(@PathVariable String email, @PathVariable String social,Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();

        return ResponseEntity.ok(gifticonService.gifticonListForMap(us.getEmail(), us.getSocial()));
    }

    @ApiOperation(value = "기프티콘 저장", notes = "기프티콘 정보 저장", httpMethod = "POST")
    @PostMapping("") //기프티콘 정보 저장
    public ResponseEntity<List<ResponseCreateGifticonDto>> CreateGifticon (@RequestBody CreateGifticonDto[] createGifticonDtos, Authentication authentication){
        List<CreateGifticonDto> Dtolist = Arrays.asList(createGifticonDtos);
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(gifticonService.createGifticon(Dtolist,us.hashCode()));
    }

    @ApiOperation(value = "즐겨찾기 등록", notes = "즐겨찾기 브랜드 등록", httpMethod = "POST")
    @PostMapping("/favorites") // 즐겨찾기 브랜드 등록
    public ResponseEntity<ResponseCreateFavoritesDto> CreateFavorites (@RequestBody CreateFavoritesDto createFavoritesDto,Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(gifticonService.createFavorites(createFavoritesDto,us.hashCode()));
    }

    @ApiOperation(value = "즐겨찾기 삭제", notes = "즐겨찾기 브랜드 삭제", httpMethod = "DELETE")
    @DeleteMapping("/favorites") // 즐겨찾기 브랜드 삭제
    public ResponseEntity<Void> DeleteFavorites (@RequestBody DeleteFavoritesDto deleteFavoritesDto,Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        gifticonService.deleteFavorites(deleteFavoritesDto,us.hashCode());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "즐겨찾기 조회", notes = "즐겨찾기 브랜드 조회", httpMethod = "GET")
    @GetMapping("/favorites/{email}/{social}") // 즐겨찾기 브랜드 등록
    public ResponseEntity<List<ResponseListFavoritesDto>> CreateFavorites (@PathVariable String email, @PathVariable String social, Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(gifticonService.listFavorites(email,social,us.hashCode()));
    }




    @ApiOperation(value = "기프티콘 정렬", notes = "기프티콘 브랜드별 정렬", httpMethod = "POST") // post 로 수정
    @PostMapping("/brand") //기프티콘 브랜드별 정렬 // 사용한거 표시 제외하고 보낼지 말지 고민
    public ResponseEntity<List<ResponseListGifticonUserDto>> SortGifticon (@RequestBody ListGifticonUserDto sortGifticonDto, Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
            return ResponseEntity.ok(gifticonService.sortGifticon(sortGifticonDto,us.hashCode()));
   }


    @ApiOperation(value = "기프티콘 히스토리", notes = "기프티콘 히스토리", httpMethod = "POST") // post 로 수정
    @PostMapping("/history")
    public ResponseEntity<List<ResponseGifticonHistoryDto>> historyGifticon (@RequestBody GifticonHistoryDto gifticonHistoryDto,Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(gifticonService.historyGifticon(gifticonHistoryDto,us.hashCode()));
    }



    @ApiOperation(value = "기프티콘 수정", notes = "기프티콘 정보 수정", httpMethod = "PUT")
    @PutMapping("") //기프티콘 정보 수정
    public ResponseEntity<ResponseUpdateGifticonDto> UpdateGifticon (@RequestBody UpdateGifticonDto updateGifticonDto, Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(gifticonService.updateGifticon(updateGifticonDto,us.hashCode()));
     }

    @ApiOperation(value = "기프티콘 삭제", notes = "기프티콘 삭제", httpMethod = "DELETE")
    @DeleteMapping("") //기프티콘 삭제
    public ResponseEntity<Void> DeleteGifticon (@RequestBody DeleteGifticonDto deleteGifticonDto,Authentication authentication) {
        UserDto us= (UserDto)authentication.getPrincipal();
        gifticonService.deleteGifticon(deleteGifticonDto.getBarcodeNum(),us.hashCode());
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "기프티콘 조회", notes = "기프티콘 삭제", httpMethod = "DELETE")
    @GetMapping("{barcode_num}") //기프티콘 삭제
    public ResponseEntity<GifticonDto> getGifticon (@PathVariable String barcode_num) {
        return ResponseEntity.ok(gifticonService.getGifticon(barcode_num));
    }

    @ApiOperation(value = "브랜드 기프티콘 순으로 정렬", notes = "브랜드 기프티콘 순으로 정렬", httpMethod = "GET")
    @GetMapping("/brandsort/{email}/{social}")
    public ResponseEntity<List<Map<String,Object>>> getGifticon (@PathVariable String email, @PathVariable String social) {
        return ResponseEntity.ok(gifticonService.brandListOrderByGifticonCountEachUser(email,social));
    }

    @Scheduled(cron = "0 59 23 * * ?")
    @ApiOperation(value = "기프티콘 상태 업데이트", notes = "기프티콘 유호기간 체크 후 상태 변경 / 서버용 API", httpMethod = "GET")
    @GetMapping("/check")// 유효기간 지난거 상태 변경
    public void Check_Overdate () {
        gifticonService.check_overdate();
    }
}
