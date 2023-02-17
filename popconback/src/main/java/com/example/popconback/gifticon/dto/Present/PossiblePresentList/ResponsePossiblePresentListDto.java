package com.example.popconback.gifticon.dto.Present.PossiblePresentList;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ResponsePossiblePresentListDto {

    // allNearPresentList: 반경2km , gettablePresentList: 반경30m(줍기가능)

    @ApiModelProperty(name = "allNearPresentList", value = "반경2km 선물 바코드넘버,x,y 리스트", example = "[{12345678,128.404054139264,36.1063553399842}...]")
    private List<ResponsePossiblePresentDto> allNearPresentList;
    @ApiModelProperty(name = "gettablePresentList", value = "반경30m(줍기가능) 선물 바코드넘버,x,y 리스트", example = "[{12345678,128.404054139264,36.1063553399842}...]")
    private List<ResponsePossiblePresentDto> gettablePresentList;



}
