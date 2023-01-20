package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Bookmark;
import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.CreateBookmarkDto;
import com.example.popconback.gifticon.dto.CreateGifticonDto;
import com.example.popconback.gifticon.dto.SortGifticonDto;
import com.example.popconback.gifticon.repository.Bookmarkrepository;
import com.example.popconback.gifticon.repository.Brandrepository;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.user.domain.User;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class GifticonService {
    private final GifticonRepository gifticonRepository;
    private final UserRepository userRepository;
    private final Brandrepository brandrepository;

    private final Bookmarkrepository bookmarkrepository;

    public List<Gifticon> gifticonList (String email, String social){
        User user = new User();
        user.setEmail(email);
        user.setSocial(social);
        int hash = user.hashCode();
        System.out.println(user.getEmail());
        System.out.println(user.getSocial());
        System.out.println(hash);
        return gifticonRepository.findByUser_Hash(hash, Sort.by(asc("due")));
    }


    public Gifticon createGifticon (CreateGifticonDto createGifticonDto){

        Optional<User> user = userRepository.findById(createGifticonDto.getHash());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(createGifticonDto.getBrandName());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }
//        System.out.println(createGifticonDto.getHash());
//        System.out.println(createGifticonDto.getBarcode_num());
        Gifticon gifticon = new Gifticon();
        BeanUtils.copyProperties(createGifticonDto, gifticon);
//        System.out.println(gifticon.getBarcode_num());
        gifticon.setUser(user.get());
        gifticon.setBrand(brand.get());
        return gifticonRepository.save(gifticon);
    }

    public List<Gifticon> sortGifticon (SortGifticonDto sortGifticonDto){
        Optional<User> user = userRepository.findById(sortGifticonDto.getHash());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(sortGifticonDto.getBrandName());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }

        int hash = sortGifticonDto.getHash();
        System.out.println(hash);
        return gifticonRepository.findByUser_HashAndBrand_BrandName(hash,sortGifticonDto.getBrandName());
    }


    public Gifticon updateGifticon (CreateGifticonDto createGifticonDto, String barcode_num){
        Optional<Gifticon> optionalGifticon = gifticonRepository.findById(barcode_num);
        System.out.println(optionalGifticon);
        System.out.println(barcode_num);
        if (!optionalGifticon.isPresent()){
            throw new EntityNotFoundException("Gifticon not present in the database");
        }
        Gifticon gifticon = optionalGifticon.get();
        BeanUtils.copyProperties(createGifticonDto, gifticon);
        return gifticonRepository.save(gifticon);
    }

    public void deleteGifticon (String barcode){
        Optional<Gifticon> gifticon = gifticonRepository.findById(barcode);
        if(!gifticon.isPresent()){
            throw new EntityNotFoundException("Gifticon Not Found");
        }
        gifticonRepository.deleteById(barcode);
    }

    public Bookmark createBookmark (CreateBookmarkDto createBookmarkDto){
        Optional<User> user = userRepository.findById(createBookmarkDto.getHash());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(createBookmarkDto.getBrandName());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user.get());
        bookmark.setBrand(brand.get());
        return bookmarkrepository.save(bookmark);
    }

    public void deleteBookmark (CreateBookmarkDto createBookmarkDto){
        Optional<User> user = userRepository.findById(createBookmarkDto.getHash());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(createBookmarkDto.getBrandName());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }
        int hash = createBookmarkDto.getHash();
        String brand_name = createBookmarkDto.getBrandName();
        bookmarkrepository.deleteByUser_HashAndBrand_BrandName(hash, brand_name);
    }

}
