package com.example.popconback.gifticon.dto.Present.GetPresent;

import com.example.popconback.gifticon.domain.Gifticon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetPresentDto {

    @ApiModelProperty(name = "barcodeNum", value = "바코드넘버", example = "12345678")
    private String barcodeNum;
    @ApiModelProperty(name = "message", value = "감사메세지", example = "감사합니다")
    private String message;
    @ApiModelProperty(name = "x", value = "x 경도", example = "128.404054139264")
    private String x;
    @ApiModelProperty(name = "y", value = "y 위도", example = "36.1063553399842")
    private String y;


}