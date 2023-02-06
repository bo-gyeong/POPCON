package com.example.popconback.gifticon.controller;
import org.springframework.security.core.Authentication;
import com.example.popconback.gifticon.dto.Present.GetPresent.GetPresentDto;
import com.example.popconback.gifticon.dto.Present.GetPresent.ResponseGetPresentDto;
import com.example.popconback.gifticon.dto.Present.PossiblePresentList.PossiblePresentListDto;
import com.example.popconback.gifticon.dto.Present.PossiblePresentList.ResponsePossiblePresentListDto;
import com.example.popconback.gifticon.service.PresentService;
import com.example.popconback.user.dto.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "PresentController")
@SwaggerDefinition(tags = {@Tag(name = "PresentContoller", description = "선물 컨트롤러")})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/presents")
@Component
public class PresentController {


    private final PresentService presentService;

    @ApiOperation(value = "기부 줍기", notes = "기부 줍기", httpMethod = "POST")
    @PostMapping("/get_present") //기부 줍기
    public ResponseEntity<ResponseGetPresentDto> GetPresent (@RequestBody GetPresentDto getPresentDto, Authentication authentication){
        UserDto us= (UserDto)authentication.getPrincipal();
        return ResponseEntity.ok(presentService.getPresent(getPresentDto,us.hashCode()));
    }


    @ApiOperation(value = "possiblePresentList", notes = "가까운 선물 리스트, 줍기가능한 선물 리스트", httpMethod = "POST")
    @PostMapping("/possible_list")
    public ResponseEntity<ResponsePossiblePresentListDto> possiblePresentList(@RequestBody PossiblePresentListDto possiblePresentListDto) {

        String x = possiblePresentListDto.getX();
        String y = possiblePresentListDto.getY();

        return new ResponseEntity<>(presentService.findPresentByPosition(x,y), HttpStatus.OK);

    }


}
