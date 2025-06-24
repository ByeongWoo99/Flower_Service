package com.ttasum.memorial.dto.heavenLetter.response;

import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.util.NameMaskUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class HeavenLetterResponseDto {
    //등록
    private boolean success;
    private int code;
    private String message;

    //등록 - 성공 201
    public static HeavenLetterResponseDto success(){

        return new HeavenLetterResponseDto(true, 201, "편지가 성공적으로 등록되었습니다.");
    }
    //등록 - 실패 400, 500
    public static HeavenLetterResponseDto fail(int code, String message){
        return new HeavenLetterResponseDto(false,code,message);
    }
}





