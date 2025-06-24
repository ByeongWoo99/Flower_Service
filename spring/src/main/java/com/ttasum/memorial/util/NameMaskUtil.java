package com.ttasum.memorial.util;

import com.ttasum.memorial.dto.heavenLetter.response.MemorialSearchResponseDto;

public class NameMaskUtil {

    //인스턴스화 방지
    private NameMaskUtil() { }

    //기증자 마스킹 (두 번째 글자만 마스킹 처리)
    public static String maskDonorNameIfAnonymous(String donorName, String anonymityFlag){
        if("Y".equalsIgnoreCase(anonymityFlag)
            && donorName != null
            && donorName.length() >= 2) {
            String first = String.valueOf(donorName.charAt(0));
            String rest = donorName.length() > 2 ? donorName.substring(2) : "";
            return first + "*" + rest;
        }else{
            return donorName;
        }
    }
    //수혜자 마스킹 (첫 글자를 제외한 전부 마스킹 처리)
    public static String maskRecipientNameIfAnonymous(String recipientName, String anonymityFlag) {
        if ("Y".equalsIgnoreCase(anonymityFlag)
                && recipientName != null
                && recipientName.length() >= 1) {
            String first = String.valueOf(recipientName.charAt(0));
            String rest = "";
            for (int i = 1; i < recipientName.length(); i++) {
                rest += "*";
            }
            return first + rest;
        } else{
                // 익명 아니거나 조건 미충족 시 원본 이름 반환
                return recipientName;
        }
    }
    //기증자 마스킹 (두 번째 글자만 마스킹 처리)
    public static String maskNameIfAnonymous(String name, String anonymityFlag){
        if("Y".equalsIgnoreCase(anonymityFlag)
                && name != null
                && name.length() >= 2) {
            String first = String.valueOf(name.charAt(0));
            String rest = name.length() > 2 ? name.substring(2) : "";
            return first + "*" + rest;
        }else{
            return name;
        }
    }
}

