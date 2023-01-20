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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Component
public class GifticonController {

    private final GifticonService gifticonService;
    @GetMapping("/gifticon/{email}/{social}") //유저의 기프티콘 정보 DB에서 보내주기 // 이것도 만료되거나 사용한거 다보낼까?
    public ResponseEntity<List<GifticonDto>> gifticonList(@PathVariable String email, @PathVariable String social){
        return ResponseEntity.ok(gifticonService.gifticonList(email, social));
    }

    @PostMapping("/gifticon") //기프티콘 정보 저장
    public ResponseEntity<ResponseCreateGifticonDto> CreateGifticon (@RequestBody CreateGifticonDto createGifticonDto){
        return ResponseEntity.ok(gifticonService.createGifticon(createGifticonDto));
    }

    @PostMapping("/favorites") // 즐겨찾기 브랜드 등록
    public ResponseEntity<ResponseCreateBookmarkDto> CreateBookmark (@RequestBody CreateBookmarkDto createBookmarkDto){
        return ResponseEntity.ok(gifticonService.createBookmark(createBookmarkDto));
    }

    @DeleteMapping("/favorites") // 즐겨찾기 브랜드 삭제
    public ResponseEntity<Void> DeleteBookmark (@RequestBody DeleteBookmarkDto deleteBookmarkDto){
        gifticonService.deleteBookmark(deleteBookmarkDto);
        return ResponseEntity.ok().build();
    }

   @PostMapping("/gifticon_brand") //기프티콘 브랜드별 정렬 // 사용한거 표시 제외하고 보낼지 말지 고민
   public ResponseEntity<List<GifticonDto>> SortGifticon (@RequestBody SortGifticonDto sortGifticonDto){
        return ResponseEntity.ok(gifticonService.sortGifticon(sortGifticonDto));
   }
//   @GetMapping("/gifticon/img") //기프티콘 OCR 요청

     @PostMapping("/gifticon/{barcode_num}") //기프티콘 정보 수정
     public ResponseEntity<ResponseUpdateGifticonDto> UpdateGifticon (@RequestBody UpdateGifticonDto updateGifticonDto, @PathVariable String barcode_num ){
        return ResponseEntity.ok(gifticonService.updateGifticon(updateGifticonDto, barcode_num));
     }
    @DeleteMapping("gifticon/{barcode_num}") //기프티콘 삭제
    public ResponseEntity<Void> DeleteGifticon (@PathVariable String barcode_num){
        gifticonService.deleteGifticon(barcode_num);
        return ResponseEntity.ok().build();
    }

    //@Scheduled(cron = "0 0 09 * * ?")
    @GetMapping("gifticon/checkOver")// 유효기간 지난거 상태 변경
    public void Check_Overdate () {
        gifticonService.check_overdate();
    }



}
