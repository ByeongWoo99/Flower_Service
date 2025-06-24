package com.ttasum.memorial.domain.repository.blameText;

import com.ttasum.memorial.domain.entity.donationStory.DonationStory;
import com.ttasum.memorial.domain.entity.Story;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
    Query 1: 전체 비난글 조회(label == 1)
        SELECT *
        FROM blame_text_letter
        WHERE label = 1
        ORDER BY update_time DESC;

    Query 2: 전체 글 조회
        SELECT *
        FROM blame_text_letter
        ORDER BY update_time DESC;

    Query 3: 전체 비난이 아닌 글 조회(label == 0)
        SELECT *
        FROM blame_text_letter
        WHERE label = 0
        ORDER BY update_time DESC;

    Query 4: 기증 후 스토리 관련 비난글 조회(board_type == "donation" AND label == 1)
        SELECT *
        FROM blame_text_letter
        WHERE board_type = "donation" AND label = 1
        ORDER BY update_time DESC;

    Query 5: 하늘나라 편지 관련 비난글 조회(board_type == "heaven" AND label == 1)
        SELECT *
        FROM blame_text_letter
        WHERE board_type = "heaven" AND label = 1
        ORDER BY update_time DESC;

    Query 6: 수혜자 편지 관련 비난글 조회(board_type == "recipient" AND label == 1)
        SELECT *
        FROM blame_text_letter
        WHERE board_type = "recipient" AND label = 1
        ORDER BY update_time DESC;
    */
public interface BlameTextLetterRepository extends JpaRepository<BlameTextLetter, Integer> {
    Optional<BlameTextLetter> findByOriginSeqAndDeleteFlag(Integer originSeq, int deleteFlag);

    // Query 1: 전체 비난글 조회
    Page<BlameTextLetter> findBlameTextLetterByLabelOrderByUpdateTimeDesc(Integer label, Pageable pageable);

    // seq를 통해 비난 글 찾기
    List<BlameTextLetter> findBlameTextLettersByBoardTypeAndOriginSeqAndDeleteFlag(String boardType, Integer originSeq, int deleteFlag);

    Page<BlameTextLetter> findBlameTextLettersByDeleteFlag(int deleteFlag, Pageable pageable);

    Page<BlameTextLetter> findBlameTextLettersByLabel(Integer label, Pageable pageable);

//    Optional<BlameTextLetter> findBlameTextByBoardTypeAndSeqAndDeleteFlag(String boardType, int seq, int i);

    Optional<ArrayList<BlameTextLetter>> findBlameTextsByBoardTypeAndOriginSeqAndDeleteFlag(String boardType, int seq, int i);
}
