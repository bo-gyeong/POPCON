package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Bookmark;
import com.example.popconback.gifticon.domain.Brand;
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
import com.example.popconback.gifticon.repository.Bookmarkrepository;
import com.example.popconback.gifticon.repository.Brandrepository;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.time.LocalTime.now;
import static org.springframework.data.domain.Sort.Order.asc;

@Service
@RequiredArgsConstructor
public class GifticonService {
    private final GifticonRepository gifticonRepository;
    private final UserRepository userRepository;
    private final Brandrepository brandrepository;
    private final Bookmarkrepository bookmarkrepository;

    public List<GifticonDto> gifticonList (String email, String social){// 기프티콘 리스트 뽑아오기
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setSocial(social);
        int hash = user.hashCode();
        List<Gifticon>list = gifticonRepository.findByUser_Hash(hash, Sort.by(asc("due")));
        List<GifticonDto> rlist = new ArrayList<>();

        for (Gifticon gifticon:list) {
            GifticonDto rgifticon = new GifticonDto();
            BeanUtils.copyProperties(gifticon,rgifticon);
            rgifticon.setHash(gifticon.getUser().getHash());
            rgifticon.setBrandName(gifticon.getBrand().getBrandName());
            rlist.add(rgifticon);
        }
        return rlist;
    }


    public List<ResponseCreateGifticonDto> createGifticon (List<CreateGifticonDto> createGifticonDtoList){
        List<ResponseCreateGifticonDto> rlist = new ArrayList<>();

        for (CreateGifticonDto createGifticonDto: createGifticonDtoList) {
            Optional<User> user = userRepository.findById(createGifticonDto.getHash());
            if (!user.isPresent()) {
                ResponseCreateGifticonDto NoUser = new ResponseCreateGifticonDto();
                rlist.add(NoUser);
            }
            Optional<Brand> brand = brandrepository.findById(createGifticonDto.getBrandName());
            if (!user.isPresent()) {
                ResponseCreateGifticonDto NoBrand = new ResponseCreateGifticonDto();
                rlist.add(NoBrand);
            }

            Gifticon gifticon = new Gifticon();
            BeanUtils.copyProperties(createGifticonDto, gifticon);

            gifticon.setUser(user.get());
            gifticon.setBrand(brand.get());

            ResponseCreateGifticonDto responDto = new ResponseCreateGifticonDto();

            BeanUtils.copyProperties(gifticonRepository.save(gifticon),responDto);
            responDto.setHash(gifticon.getUser().getHash());
            responDto.setBrandName(gifticon.getBrand().getBrandName());

            rlist.add(responDto);
        }
        return rlist;
    }


    public List<GifticonDto> sortGifticon (SortGifticonDto sortGifticonDto){
        Optional<User> user = userRepository.findById(sortGifticonDto.getHash());
        List<GifticonDto> rlist = new ArrayList<>();
        if (!user.isPresent()) {
            //throw new EntityNotFoundException("User Not Found");
            return rlist;
        }
        Optional<Brand> brand = brandrepository.findById(sortGifticonDto.getBrandName());
        if (!user.isPresent()) {
            //throw new EntityNotFoundException("Brand Not Found");
            return rlist;
        }

        int hash = sortGifticonDto.getHash();
        //System.out.println(hash);

        List <Gifticon>list = gifticonRepository.findByUser_HashAndBrand_BrandName(hash,sortGifticonDto.getBrandName());
        for (Gifticon gifticon: list
             ) {
            GifticonDto rgifticon = new GifticonDto();
            BeanUtils.copyProperties(gifticon, rgifticon);
            rgifticon.setHash(gifticon.getUser().getHash());
            rgifticon.setBrandName(gifticon.getBrand().getBrandName());
            rlist.add(rgifticon);
        }
        return rlist;
    }


    public ResponseUpdateGifticonDto updateGifticon (UpdateGifticonDto updateGifticonDto, String barcode_num){
        Optional<Gifticon> optionalGifticon = gifticonRepository.findById(barcode_num);
//        System.out.println(optionalGifticon);
//        System.out.println(barcode_num);
        ResponseUpdateGifticonDto responDto = new ResponseUpdateGifticonDto();

        if (!optionalGifticon.isPresent()){
            return responDto;
            //throw new EntityNotFoundException("Gifticon not present in the database");
        }
        Gifticon gifticon = optionalGifticon.get();
        BeanUtils.copyProperties(updateGifticonDto, gifticon);
        BeanUtils.copyProperties(gifticonRepository.save(gifticon),responDto);
        responDto.setHash(gifticon.getUser().getHash());
        responDto.setBrandName(gifticon.getBrand().getBrandName());
        return responDto;
    }

    public void deleteGifticon (String barcode){
        Optional<Gifticon> gifticon = gifticonRepository.findById(barcode);
        if(!gifticon.isPresent()){
            throw new EntityNotFoundException("Gifticon Not Found");
        }
        gifticonRepository.deleteById(barcode);
    }

    public ResponseCreateBookmarkDto createBookmark (CreateBookmarkDto createBookmarkDto){
        Optional<User> user = userRepository.findById(createBookmarkDto.getHash());

        ResponseCreateBookmarkDto responDto = new ResponseCreateBookmarkDto();
        if (!user.isPresent()) {
            return responDto;
            //throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(createBookmarkDto.getBrandName());
        if (!user.isPresent()) {
            return responDto;
           // throw new EntityNotFoundException("Brand Not Found");
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user.get());
        bookmark.setBrand(brand.get());

        bookmarkrepository.save(bookmark);

        responDto.setBrandName(bookmark.getBrand().getBrandName());
        responDto.setHash(bookmark.getUser().getHash());
        return responDto;
    }

    public void deleteBookmark (DeleteBookmarkDto deleteBookmarkDto){
        Optional<User> user = userRepository.findById(deleteBookmarkDto.getHash());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(deleteBookmarkDto.getBrandName());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }
        int hash = deleteBookmarkDto.getHash();
        String brand_name = deleteBookmarkDto.getBrandName();
        bookmarkrepository.deleteByUser_HashAndBrand_BrandName(hash, brand_name);
    }

    public List<GifticonDto> getPushGifticon (int hash, int Dday){// 사용한 기프티콘이나 기간지난거는 스테이트로 구분 하면 되는
        Date date = java.sql.Date.valueOf(LocalDate.now().plusDays(Dday));
        List<GifticonDto> rlist = new ArrayList<>();
        List <Gifticon> list = gifticonRepository.findByUser_HashAndDueLessThanEqualAndState(hash, date,1);
        for (Gifticon gifticon:list
             ) {
            GifticonDto responDto = new GifticonDto();
            BeanUtils.copyProperties(gifticon,responDto);
            responDto.setHash(gifticon.getUser().getHash());
            responDto.setBrandName(gifticon.getBrand().getBrandName());
            rlist.add(responDto);
        }
        return rlist;

    }

    public void check_overdate(){
        Date date =java.sql.Date.valueOf(LocalDate.now());
        List <Gifticon> list = gifticonRepository.findByDueAndState(date,1);
        for (Gifticon gifticon: list) {
            gifticon.setState(0);
            gifticonRepository.save(gifticon);
        }
    }

}
