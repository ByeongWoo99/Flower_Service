package com.ttasum.memorial.dto.Main;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MainDto {
    private final List<HeavenLetterMainDto> HeavenLetterMainDtoList;
    private final List<RememberanceMainDto> RememberanceMainDtoList;
}
