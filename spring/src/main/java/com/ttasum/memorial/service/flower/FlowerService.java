package com.ttasum.memorial.service.flower;

import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class FlowerService {

    private final FlowerRepository flowerRepo;

    private final FastApiClientService fastApiClientService;

    private final Random random = new Random();

    /**
     *
     * @param content
     * @return recommendations(꽃 추천 리스트)
     */
    @Transactional(readOnly = true)
    public List<FlowerDto> recommendFlowersByContent(String content) {

        // FastApi로 content 전송 후 감정 받기
        List<String> emotions = fastApiClientService.getEmotions(content);

        // 전달 받은 감정 별로 어울리는 꽃 추천 리스트 반환
        List<FlowerDto> recommendations = new ArrayList<>();
        for (String emotion : emotions) {
            List<Flower> candidates = flowerRepo.findByEmotionAndDelFlag(emotion, "N");
            if (candidates.isEmpty()) {
                // 감정과 어울리는 꽃 없으면 건너뛰기
                continue;
            }
            //감정과 일치하는 꽃 랜덤으로 가져오기
            Flower flower = candidates.get(random.nextInt(candidates.size()));
            recommendations.add(new FlowerDto(
                    flower.getSeq(),
                    flower.getName(),
                    flower.getEmotion(),
                    flower.getMeaning(),
                    flower.getImgUrl(),
                    flower.getDelFlag()
            ));
        }
        // 추천 결과가 아예 없을 때만 예외 처리
        if (recommendations.isEmpty()) {
            throw new FlowerNotFoundException("추천할 수 있는 꽃이 없습니다.");
        }
        return recommendations;
    }

}

