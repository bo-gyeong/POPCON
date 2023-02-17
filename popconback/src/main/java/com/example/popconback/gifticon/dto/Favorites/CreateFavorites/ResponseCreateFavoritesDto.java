package com.example.popconback.gifticon.dto.Favorites.CreateFavorites;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class ResponseCreateFavoritesDto {

    @ApiModelProperty(name = "brandName", value = "브랜드명", example = "스타벅스")
    private String brandName;
}
