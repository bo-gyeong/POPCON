package com.example.popconback.gifticon.service;


import com.example.popconback.files.domain.InputFile;
import com.example.popconback.files.repository.FileRepository;
import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Favorites;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.Favorites.CreateFavorites.CreateFavoritesDto;
import com.example.popconback.gifticon.dto.Favorites.CreateFavorites.ResponseCreateFavoritesDto;
import com.example.popconback.gifticon.dto.Gifticon.CreateGifticon.CreateGifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.CreateGifticon.ResponseCreateGifticonDto;
import com.example.popconback.gifticon.dto.Favorites.DeleteFavorites.DeleteFavoritesDto;
import com.example.popconback.gifticon.dto.GifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.HistoryGifticon.GifticonHistoryDto;
import com.example.popconback.gifticon.dto.Gifticon.HistoryGifticon.ResponseGifticonHistoryDto;
import com.example.popconback.gifticon.dto.Favorites.ListFavorites.ResponseListFavoritesDto;
import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.BrandForRLGUDto;
import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.ResponseListGifticonUserDto;
import com.example.popconback.gifticon.dto.Gifticon.ListGifticonUser.ListGifticonUserDto;
import com.example.popconback.gifticon.dto.Gifticon.UpdateGifticon.ResponseUpdateGifticonDto;
import com.example.popconback.gifticon.dto.Gifticon.UpdateGifticon.UpdateGifticonDto;
import com.example.popconback.gifticon.repository.Favoritesrepository;
import com.example.popconback.gifticon.repository.Brandrepository;
import com.example.popconback.gifticon.repository.GifticonRepository;
import com.example.popconback.user.domain.User;
import com.example.popconback.user.dto.UserDto;
import com.example.popconback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.AccessControlException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.LocalTime.now;
import static org.springframework.data.domain.Sort.Order.asc;

@Service
@RequiredArgsConstructor
public class GifticonService {
    private final GifticonRepository gifticonRepository;

    private final FileRepository fileRepository;

    private final UserRepository userRepository;
    private final Brandrepository brandrepository;
    private final Favoritesrepository favoritesrepository;

    public List<ResponseListGifticonUserDto> gifticonList (String email, String social){// 기프티콘 리스트 뽑아오기
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setSocial(social);
        int hash = user.hashCode();

        List<ResponseListGifticonUserDto> rlist = new ArrayList<>();

        for (int i = 0; i < 3; i++){
            if(i == 1){continue;}
            List<Gifticon>list = gifticonRepository.findByUser_HashAndState(hash,i, Sort.by(asc("due")));
            for (Gifticon gifticon:list) {
                ResponseListGifticonUserDto rgifticon = new ResponseListGifticonUserDto();
                BeanUtils.copyProperties(gifticon,rgifticon);// 찾은 기프티콘 정보 복사

                BrandForRLGUDto brand = new BrandForRLGUDto();// 브랜드는 따로 복사
                BeanUtils.copyProperties(gifticon.getBrand(),brand);
                rgifticon.setBrand(brand);

                List<InputFile>gflist = fileRepository.findByGifticon_BarcodeNum(gifticon.getBarcodeNum());//사진들도 따로 복사
                for (InputFile gifticonfile: gflist
                ) {
                    if(gifticonfile.getImageType() == 0){// 0: 원본
                        rgifticon.setOrigin_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 1){// 1: 바코드
                        rgifticon.setBarcode_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 2){// 2: 상품
                        rgifticon.setProduct_filepath(gifticonfile.getFilePath());
                    }
                }

                rlist.add(rgifticon);
            }

        }
        return rlist;
    }

    public List<ResponseListGifticonUserDto> gifticonListForMap (String email, String social){// 기프티콘 리스트 뽑아오기
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setSocial(social);
        int hash = user.hashCode();

        List<ResponseListGifticonUserDto> rlist = new ArrayList<>();

        for (int i = 0; i < 3; i++){
            if(i == 1){continue;}
            if(i == 2){continue;}
            List<Gifticon>list = gifticonRepository.findByUser_HashAndStateAndIsVoucher(hash,i,0, Sort.by(asc("due")));
            for (Gifticon gifticon:list) {
                ResponseListGifticonUserDto rgifticon = new ResponseListGifticonUserDto();
                BeanUtils.copyProperties(gifticon,rgifticon);// 찾은 기프티콘 정보 복사

                BrandForRLGUDto brand = new BrandForRLGUDto();// 브랜드는 따로 복사
                BeanUtils.copyProperties(gifticon.getBrand(),brand);
                rgifticon.setBrand(brand);

                List<InputFile>gflist = fileRepository.findByGifticon_BarcodeNum(gifticon.getBarcodeNum());//사진들도 따로 복사
                for (InputFile gifticonfile: gflist
                ) {
                    if(gifticonfile.getImageType() == 0){// 0: 원본
                        rgifticon.setOrigin_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 1){// 1: 바코드
                        rgifticon.setBarcode_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 2){// 2: 상품
                        rgifticon.setProduct_filepath(gifticonfile.getFilePath());
                    }
                }

                rlist.add(rgifticon);
            }

        }
        return rlist;
    }

    
    public List<ResponseGifticonHistoryDto> historyGifticon (GifticonHistoryDto gifticonHistoryDto, int hash){// 기프티콘 리스트 뽑아오기

        List<Gifticon>list = gifticonRepository.findByUser_HashAndStateBetween(hash,1,2);
        List<ResponseGifticonHistoryDto> rlist = new ArrayList<>();

        for (Gifticon gifticon:list) {
            ResponseGifticonHistoryDto rgifticon = new ResponseGifticonHistoryDto();
            BeanUtils.copyProperties(gifticon,rgifticon);// 찾은 기프티콘 정보 복사

            BrandForRLGUDto brand = new BrandForRLGUDto();// 브랜드는 따로 복사
            BeanUtils.copyProperties(gifticon.getBrand(),brand);
            rgifticon.setBrand(brand);

            List<InputFile>gflist = fileRepository.findByGifticon_BarcodeNum(gifticon.getBarcodeNum());//사진들도 따로 복사
            for (InputFile gifticonfile: gflist
            ) {
                if(gifticonfile.getImageType() == 0){// 0: 원본
                    rgifticon.setOrigin_filepath(gifticonfile.getFilePath());
                }
                if(gifticonfile.getImageType() == 1){// 1: 바코드
                    rgifticon.setBarcode_filepath(gifticonfile.getFilePath());
                }
                if(gifticonfile.getImageType() == 2){// 2: 상품
                    rgifticon.setProduct_filepath(gifticonfile.getFilePath());
                }
            }

            rlist.add(rgifticon);
        }
        return rlist;
    }


    public List<ResponseCreateGifticonDto> createGifticon (List<CreateGifticonDto> createGifticonDtoList, int hash){
        List<ResponseCreateGifticonDto> rlist = new ArrayList<>();

        UserDto tuser = new UserDto();


        for (CreateGifticonDto createGifticonDto: createGifticonDtoList) {
//            tuser.setEmail(createGifticonDto.getEmail());
//            tuser.setSocial(createGifticonDto.getSocial());
//            int hash = tuser.hashCode();
            Optional<User> user = userRepository.findById(hash);

            if (!user.isPresent()) {
                ResponseCreateGifticonDto NoUser = new ResponseCreateGifticonDto();
                rlist.add(NoUser);
            }

            String b = createGifticonDto.getBrandName().toUpperCase();

            Optional<Brand> brand = brandrepository.findById(b);
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
            responDto.setBrandName(gifticon.getBrand().getBrandName());

            rlist.add(responDto);
        }
        return rlist;
    }
    public ResponseUpdateGifticonDto updateGifticon (UpdateGifticonDto updateGifticonDto,int hash){
        Optional<Gifticon> optionalGifticon = gifticonRepository.findById(updateGifticonDto.getBarcodeNum());
        ResponseUpdateGifticonDto responDto = new ResponseUpdateGifticonDto();

        if (!optionalGifticon.isPresent()){
            return responDto;
            //throw new EntityNotFoundException("Gifticon not present in the database");
        }
        Gifticon gifticon = optionalGifticon.get();

        if(gifticon.getUser().getHash() != hash){
            return responDto; //본인꺼 아니면 건들지 마시오
        }

        BeanUtils.copyProperties(updateGifticonDto, gifticon,"email","social");


        gifticon.setBrand(brandrepository.findByBrandName(updateGifticonDto.getBrandName()));
        BeanUtils.copyProperties(gifticonRepository.save(gifticon),responDto);
        responDto.setBrandName(gifticon.getBrand().getBrandName());
        return responDto;
    }


    public List<ResponseListGifticonUserDto> sortGifticon (ListGifticonUserDto sortGifticonDto, int hash){

        Optional<User> user = userRepository.findById(hash);
        List<ResponseListGifticonUserDto> rlist = new ArrayList<>();
        if (!user.isPresent()) {
            //throw new EntityNotFoundException("User Not Found");
            return rlist;
        }
        Optional<Brand> brand = brandrepository.findById(sortGifticonDto.getBrandName());
        if (!user.isPresent()) {
            //throw new EntityNotFoundException("Brand Not Found");
            return rlist;
        }

        for (int i = 0; i < 3; i++){
            if(i == 1){continue;}
            List <Gifticon>list = gifticonRepository.findByUser_HashAndBrand_BrandNameAndState(hash,sortGifticonDto.getBrandName(),i,Sort.by(asc("due")));
            for (Gifticon gifticon: list
            ) {
                ResponseListGifticonUserDto rgifticon = new ResponseListGifticonUserDto();
                BeanUtils.copyProperties(gifticon, rgifticon);

                BrandForRLGUDto rbrand = new BrandForRLGUDto();
                BeanUtils.copyProperties(gifticon.getBrand(),rbrand);
                rgifticon.setBrand(rbrand);

                List<InputFile>gflist = fileRepository.findByGifticon_BarcodeNum(gifticon.getBarcodeNum());//사진들도 따로 복사
                for (InputFile gifticonfile: gflist
                ) {
                    if(gifticonfile.getImageType() == 0){// 0: 원본
                        rgifticon.setOrigin_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 1){// 1: 바코드
                        rgifticon.setBarcode_filepath(gifticonfile.getFilePath());
                    }
                    if(gifticonfile.getImageType() == 2){// 2: 상품
                        rgifticon.setProduct_filepath(gifticonfile.getFilePath());
                    }
                }
                rlist.add(rgifticon);
            }
        }

        return rlist;
    }





    public void deleteGifticon (String barcode,int hash){
        Optional<Gifticon> gifticon = gifticonRepository.findById(barcode);
        if(!gifticon.isPresent()){
            throw new EntityNotFoundException("Gifticon Not Found");
        }
        if(gifticon.get().getUser().getHash() != hash){
            throw new AccessControlException("not your gift");
        }
        gifticonRepository.deleteById(barcode);
    }

    public GifticonDto getGifticon(String barcode_num){
        Optional<Gifticon> gifticon = gifticonRepository.findById(barcode_num);
        if(!gifticon.isPresent()){
            throw new EntityNotFoundException("Gifticon Not Found");
        }
        Gifticon Tgifticon = gifticon.get();
        GifticonDto responsDto = new GifticonDto();
        BeanUtils.copyProperties(Tgifticon,responsDto);
        responsDto.setHash(Tgifticon.getUser().getHash());
        responsDto.setBrandName(Tgifticon.getBrand().getBrandName());


        List<InputFile>gflist = fileRepository.findByGifticon_BarcodeNum(responsDto.getBarcodeNum());//사진들도 따로 복사
        for (InputFile gifticonfile: gflist
        ) {
            if(gifticonfile.getImageType() == 0){// 0: 원본
                responsDto.setOrigin_filepath(gifticonfile.getFilePath());
            }
            if(gifticonfile.getImageType() == 1){// 1: 바코드
                responsDto.setBarcode_filepath(gifticonfile.getFilePath());
            }
            if(gifticonfile.getImageType() == 2){// 2: 상품
                responsDto.setProduct_filepath(gifticonfile.getFilePath());
            }
        }
        return responsDto;

    }

    public ResponseCreateFavoritesDto createFavorites (CreateFavoritesDto createFavoritesDto,int hash){
        Optional<User> user = userRepository.findById(hash);


        ResponseCreateFavoritesDto responDto = new ResponseCreateFavoritesDto();
        if (!user.isPresent()) {
            return responDto;
            //throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(createFavoritesDto.getBrandName());
        if (!brand.isPresent()) {
            return responDto;
           // throw new EntityNotFoundException("Brand Not Found");
        }
        Favorites bookmark = new Favorites();
        bookmark.setUser(user.get());
        bookmark.setBrand(brand.get());

        favoritesrepository.save(bookmark);

        responDto.setBrandName(bookmark.getBrand().getBrandName());
        return responDto;
    }

    public void deleteFavorites(DeleteFavoritesDto deleteFavoritesDto,int hash){

        Optional<User> user = userRepository.findById(hash);
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User Not Found");
        }
        Optional<Brand> brand = brandrepository.findById(deleteFavoritesDto.getBrandName());
        if (!brand.isPresent()) {
            throw new EntityNotFoundException("Brand Not Found");
        }

        String brand_name = deleteFavoritesDto.getBrandName();
        favoritesrepository.deleteByUser_HashAndBrand_BrandName(hash, brand_name);
    }

    public List<ResponseListFavoritesDto> listFavorites (String email, String social,int hash){

        Optional<User> user = userRepository.findById(hash);

        List<ResponseListFavoritesDto> rlist = new ArrayList<>();

        if (!user.isPresent()) {
            return rlist;
            //throw new EntityNotFoundException("User Not Found");
        }

        List<Favorites> list = favoritesrepository.findByUser_Hash(hash);

        for (Favorites favorite: list
             ) {
            ResponseListFavoritesDto responDto = new ResponseListFavoritesDto();
            responDto.setBrandName(favorite.getBrand().getBrandName());
            rlist.add(responDto);
        }

        return rlist;
    }


    public List<GifticonDto> getPushGifticon (int hash, int Dday){// 사용한 기프티콘이나 기간지난거는 스테이트로 구분 하면 되는
        Date date = java.sql.Date.valueOf(LocalDate.now().plusDays((Dday+1)));
        List<GifticonDto> rlist = new ArrayList<>();
        List <Gifticon> list = gifticonRepository.findByUser_HashAndDueLessThanAndState(hash, date,0);
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


    public List<Map<String,Object>> brandListOrderByGifticonCountEachUser(String email, String social){
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setSocial(social);
        List<Map<String,Object>> list = gifticonRepository.selectSQLById(user.hashCode());
//        List<Gifticon> list = gifticonRepository.findByUser_Hash(user.hashCode(),Sort.by(asc("due")));
//        List<ResponseBrandDto> rlist = new ArrayList<>();
//        List<String> blist = new ArrayList<>();
//        for (Gifticon gifticon:list
//             ) {
//            String brandname = gifticon.getBrand().getBrandName();
//            if(!blist.contains(brandname)){
//                blist.add(brandname);
//            }
//        }
//        for (String brandname:blist
//             ) {
//            List<Gifticon> glist = gifticonRepository.findByUser_HashAndBrand_BrandName(user.hashCode(),brandname);
//
//        }
        return list;
    }

    public void check_overdate(){
        Date date =java.sql.Date.valueOf(LocalDate.now().plusDays(1));
        List <Gifticon> list = gifticonRepository.findByDueLessThanEqualAndState(date,0);
        for (Gifticon gifticon: list) {
            gifticon.setState(2);
            gifticonRepository.save(gifticon);
        }
    }

}
