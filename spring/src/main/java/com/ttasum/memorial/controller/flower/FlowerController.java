package com.ttasum.memorial.controller.flower;

import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.service.donationStory.DonationStoryService;
import com.ttasum.memorial.service.flower.FlowerService;
import com.ttasum.memorial.service.heavenLetter.HeavenLetterServiceImpl;
import com.ttasum.memorial.service.recipientLetter.RecipientLetterServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlowerController {

    private final FlowerService flowerService;

    //꽃 추천 위해 각 편지서비스 불러오기
    private final DonationStoryService donationStoryService;
    private final HeavenLetterServiceImpl heavenLetterServiceImpl;
    private final RecipientLetterServiceImpl recipientLetterServiceImpl;


    //DonationLetter 꽃 추천 요청
    @PostMapping("/donationLetters/{storySeq}/flowers")
    public ResponseEntity<List<FlowerDto>> recommendForDonationStory(@PathVariable Integer storySeq) {
        // 편지 내용(content)만 꺼내오기
        String content = (donationStoryService.getStory(storySeq)).getStoryContents();

        // FastAPI로 content 전달 → 추천된 꽃 리스트 받아오기
        List<FlowerDto> recommendations = flowerService.recommendFlowersByContent(content);

        //추천 꽃 리스트 반환
        return ResponseEntity.ok(recommendations);
    }

    //HeavenLetter 꽃 추천 요청
    @PostMapping("/heavenLetters/{letterSeq}/flowers")
    public ResponseEntity<List<FlowerDto>> recommendForHavenLetter(@PathVariable Integer letterSeq) {

        String content = (heavenLetterServiceImpl.getLetterById(letterSeq)).getLetterContents();

        List<FlowerDto> recommendations = flowerService.recommendFlowersByContent(content);

        return ResponseEntity.ok(recommendations);
    }

    //RecipientLetter 꽃 추천 요청
    @PostMapping("/recipientLetters/{letterSeq}/flowers")
    public ResponseEntity<List<FlowerDto>> recommendForRecipientLetter(@PathVariable Integer letterSeq) {

        String content = (recipientLetterServiceImpl.getLetterById(letterSeq)).getLetterContents();

        List<FlowerDto> recommendations = flowerService.recommendFlowersByContent(content);

        return ResponseEntity.ok(recommendations);
    }
}
