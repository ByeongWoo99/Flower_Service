package com.ttasum.memorial.dto.Main;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RememberanceMainDto {

    private final String donateName;
    private final String donateGender;
    private final int donateAge;

    public RememberanceMainDto(String donateName, String donateGender, int donateAge) {
        this.donateName = donateName;
        this.donateGender = convertGender(donateGender);
        this.donateAge = donateAge;
    }

    private String convertGender(String genderCode) {
        return switch (genderCode) {
            case "M" -> "남";
            case "F" -> "여";
            default -> "기타";
        };
    }
}
