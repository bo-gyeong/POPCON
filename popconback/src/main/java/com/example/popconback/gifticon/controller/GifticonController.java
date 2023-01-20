package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.domain.Bookmark;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.CreateBookmarkDto;
import com.example.popconback.gifticon.dto.CreateGifticonDto;
import com.example.popconback.gifticon.dto.SortGifticonDto;
import com.example.popconback.gifticon.service.GifticonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Component
public class GifticonController {

    private final GifticonService gifticonService;
    @GetMapping("/gifticon/{email}/{social}") //유저의 기프티콘 정보 DB에서 보내주기
    public ResponseEntity<List<Gifticon>> gifticonList(@PathVariable String email, @PathVariable String social){
            return ResponseEntity.ok(gifticonService.gifticonList(email, social));
    }

    @PostMapping("/gifticon") //기프티콘 정보 저장
    public ResponseEntity<Gifticon> CreateGifticon (@RequestBody CreateGifticonDto createGifticonDto){
        return ResponseEntity.ok(gifticonService.createGifticon(createGifticonDto));
    }

    @PostMapping("/favorites") // 즐겨찾기 브랜드 등록
    public ResponseEntity<Bookmark> CreateBookmark (@RequestBody CreateBookmarkDto createBookmarkDto){
        return ResponseEntity.ok(gifticonService.createBookmark(createBookmarkDto));
    }

    @DeleteMapping("/favorites") // 즐겨찾기 브랜드 삭제
    public ResponseEntity<Void> DeleteBookmark (@RequestBody CreateBookmarkDto createBookmarkDto){
        gifticonService.deleteBookmark(createBookmarkDto);
        return ResponseEntity.ok().build();
    }

   @PostMapping("/gifticon_brand") //기프티콘 브랜드별 정렬
   public ResponseEntity<List<Gifticon>> SortGifticon (@RequestBody SortGifticonDto sortGifticonDto){
        return ResponseEntity.ok(gifticonService.sortGifticon(sortGifticonDto));
   }
//   @GetMapping("/gifticon/img") //기프티콘 OCR 요청

     @PostMapping("/gifticon/{barcode_num}") //기프티콘 정보 수정
     public ResponseEntity<Gifticon> UpdateGifticon (@RequestBody CreateGifticonDto createGifticonDto, @PathVariable String barcode_num ){
        return ResponseEntity.ok(gifticonService.updateGifticon(createGifticonDto, barcode_num));
     }
    @DeleteMapping("gifticon/{barcode_num}") //기프티콘 삭제
    public ResponseEntity<Void> DeleteGifticon (@PathVariable String barcode_num){
        gifticonService.deleteGifticon(barcode_num);
        return ResponseEntity.ok().build();
    }




}
