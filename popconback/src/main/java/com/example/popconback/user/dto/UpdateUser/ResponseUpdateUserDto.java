package com.example.popconback.user.dto.UpdateUser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;
@Data
public class ResponseUpdateUserDto {
    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "abc@naver.com")
    private String email;
    @ApiModelProperty(name = "social", value = "소셜 로그인 구분, ex)'카카오', '네이버'", example = "카카오")
    private String social;
    @ApiModelProperty(name = "Token", value = "토큰값", example = "")
    private String Token;
    @ApiModelProperty(name = "alarm", value = "알람 설정, 0:OFF, 1:ON", example = "1")
    private int alarm;
    @ApiModelProperty(name = "Nday", value = "유효기간 N일전부터 알람 시작", example = "7")
    private int Nday;
    @ApiModelProperty(name = "term", value = "알람 시작부터 n일 주기로 알람", example = "1")
    private int term;
    @ApiModelProperty(name = "timezone", value = "알림시간대, 1:아침, 2:점심, 3:저녁", example = "1")
    private int timezone;
    @ApiModelProperty(name = "manner_temp", value = "매너온도", example = "30")
    private int manner_temp;

    @Override
    public int hashCode() {
        return Objects.hash(email,social);
    }
}
