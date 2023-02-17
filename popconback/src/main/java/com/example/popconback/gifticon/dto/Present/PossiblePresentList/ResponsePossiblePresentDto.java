package com.example.popconback.gifticon.dto.Present.PossiblePresentList;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResponsePossiblePresentDto {
    @ApiModelProperty(name = "barcodeNum", value = "바코드넘버", example = "12345678")
    private String barcodeNum;
    @ApiModelProperty(name = "x", value = "x 경도", example = "128.404054139264")
    private String x;
    @ApiModelProperty(name = "y", value = "y 위도", example = "36.1063553399842")
    private String y;

}
