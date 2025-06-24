package com.ttasum.memorial.service.blameText;

import com.ttasum.memorial.domain.entity.Comment;
import com.ttasum.memorial.domain.entity.Contents;
import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.entity.donationStory.DonationStoryComment;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.domain.entity.blameText.*;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetterComment;
import com.ttasum.memorial.domain.repository.donationStory.DonationStoryCommentRepository;
import com.ttasum.memorial.domain.repository.blameText.BlameTextCommentRepository;
import com.ttasum.memorial.domain.repository.blameText.BlameTextLetterRepository;
import com.ttasum.memorial.domain.repository.blameText.BlameTextLetterSentenceRepository;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterCommentRepository;
import com.ttasum.memorial.dto.blameText.BlameResponseDto;
import com.ttasum.memorial.exception.blameText.BlameTextException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BlameTextPersistenceService {
    private final BlameTextLetterRepository blameTextLetterRepository;
    private final BlameTextLetterSentenceRepository blameTextLetterSentenceRepository;
    private final BlameTextCommentRepository blameTextCommentRepository;
    private final DonationStoryCommentRepository donationStoryCommentRepository;
    private final HeavenLetterCommentRepository heavenLetterCommentRepository;
    // 매직 넘버 제거
    private static final double DEFAULT_REGULATION = 0.7;
    private static final String DEFAULT_BOARD_TYPE = "donation";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T extends Contents> void saveToDb(BlameResponseDto response, T contents, String boardType) {
        if (contents instanceof Story story) {
            BlameTextLetter savedLetter = blameTextLetterRepository.save(
                    setBlameLetter(response, story, boardType));
            setBlameLetterSentence(response, savedLetter);
        } else if (contents instanceof Comment comment) {
            BlameTextComment savedComment = blameTextCommentRepository.save(
                    setBlameComment(response, comment, boardType));
            this.setBlameLetterSentence(response, savedComment);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다: " + contents.getClass().getSimpleName());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)  //호출자가 트랜잭션 중이더라도 기존 트랜잭션을 분리
    public void updateToDb(BlameResponseDto response, Contents contents, String boardType) {
        if(contents instanceof Story story) {
            BlameTextLetter existing =
                    blameTextLetterRepository.findByOriginSeqAndDeleteFlag(story.getId(), 0)
                            .orElseThrow(() -> new EntityNotFoundException("기존 비난 텍스트 없음"));
            // 기존 데이터 삭제 delete_flag (text, sentence 모두)
            existing.setDeleteFlag(1);
            existing.setUpdateTime(LocalDateTime.now());
            blameTextLetterRepository.save(existing);
            // 다시 저장
            this.saveToDb(response, story, boardType);
        } else if(contents instanceof Comment comment) {
            BlameTextComment existing =
                    blameTextCommentRepository.findByOriginSeqAndDeleteFlag(comment.getCommentSeq(), 0)
                            .orElseThrow(() -> new EntityNotFoundException("기존 비난 텍스트 없음"));
            // 기존 데이터 삭제 delete_flag (text, sentence 모두)
            existing.setDeleteFlag(1);
            existing.setUpdateTime(LocalDateTime.now());
            blameTextCommentRepository.save(existing);
            // 다시 저장
            this.saveToDb(response, comment, boardType);
        }

    }

    private BlameTextLetter setBlameLetter(BlameResponseDto response, Story story, String boardType) {
        return BlameTextLetter.builder()
                .sentence(response.getSentence())
                .label(response.getLabel())
                .confidence(response.getConfidence())
                .sentenceLine(response.getDetails().size())
                .regulation(DEFAULT_REGULATION)
                .updateTime(LocalDateTime.now())
                .boardType(boardType)
                .originSeq(story.getId())
                .deleteFlag(0)
                .build();
    }

    private BlameTextComment setBlameComment(BlameResponseDto response, Comment comment, String boardType) {
//        // 1. Comment 저장 (영속 상태로 만들어야 함)
//        if (comment instanceof DonationStoryComment donation) {
//            donationStoryCommentRepository.save(donation);
//        } else if(comment instanceof HeavenLetterComment heaven) {
//            heavenLetterCommentRepository.save(heaven);
//        }

        // 2. 이후 BlameTextComment에 연결
        return BlameTextComment.builder()
                .storySeq(comment.getLetterSeq().getId())
                .contents(response.getSentence())
                .label(response.getLabel())
                .confidence(response.getConfidence())
                .sentenceLine(response.getDetails().size())
                .regulation(DEFAULT_REGULATION)
                .updateTime(LocalDateTime.now())
                .boardType(boardType)
                .originSeq(comment.getCommentSeq())
                .deleteFlag(0)
                .build();
    }

    // python 서버 json 응답 값 중 Text를 sentence로 split한 결과를 DB에 저장
    private void setBlameLetterSentence(
            BlameResponseDto response, Contents savedContents){
        int seq = 0;
        if(savedContents instanceof BlameTextLetter saved) {
            ArrayList<BlameTextLetterSentence> list= new ArrayList<>();
            for (Map<String, Object> oneDetail : response.getDetails() ) {
                BlameTextLetterSentence blameTextLetterSentence = BlameTextLetterSentence.builder()
                        .id(new BlameTextLetterSentenceId(saved.getSeq(), seq++))
                        .sentence((String) oneDetail.get("sentence"))
                        .confidence((double) oneDetail.get("confidence"))
                        .label((int) oneDetail.get("label"))
                        .letter((BlameTextLetter) savedContents)
                        .build();
                list.add(blameTextLetterSentence);
            }
            saved.setSentences(list);  //부모만 save()해도 자식들이 자동 저장
        } else if (savedContents instanceof BlameTextComment saved) {
            ArrayList<BlameTextCommentSentence> list= new ArrayList<>();
            for (Map<String, Object> oneDetail : response.getDetails() ) {
                BlameTextCommentSentence blameTextCommentSentence = BlameTextCommentSentence.builder()
                        .id(new BlameTextCommentSentenceId(saved.getSeq() ,seq++))
                        .sentence((String) oneDetail.get("sentence"))
                        .confidence((double) oneDetail.get("confidence"))
                        .label((int) oneDetail.get("label"))
                        .comment((BlameTextComment) savedContents)
                        .build();
                list.add(blameTextCommentSentence);
            }
            saved.setComments(list);  //부모만 save()해도 자식들이 자동 저장
        }
    }

    @Transactional
    public void deleteBlameTextLetter(int seq, String boardType) {
        ArrayList<BlameTextLetter> letter = blameTextLetterRepository.findBlameTextsByBoardTypeAndOriginSeqAndDeleteFlag(boardType, seq, 0)
                .orElseThrow(BlameTextException::new);
        for(BlameTextLetter blameTextLetter : letter){
            blameTextLetter.setDeleteFlag(1);
        }
    }

    @Transactional
    public void deleteBlameTextComment(int letterSeq, int commentSeq, String boardType) {
        List<BlameTextComment> comment = blameTextCommentRepository
                .findBlameTextCommentsByBoardTypeAndStorySeqAndOriginSeqAndDeleteFlag(boardType, letterSeq, commentSeq, 0)
                .orElseThrow(BlameTextException::new);

        for(BlameTextComment blameTextComment : comment) {
            blameTextComment.setDeleteFlag(1);
        }
    }
}
