package com.ttasum.memorial.domain.repository.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextLetterSentence;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetterSentenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface BlameTextLetterSentenceRepository extends JpaRepository<BlameTextLetterSentence, BlameTextLetterSentenceId> {

    @Query("SELECT s FROM BlameTextLetterSentence s WHERE s.id.letterSeq = :seq ORDER BY s.id.seq ASC")
    ArrayList<BlameTextLetterSentence> getBlameTextLetterSentencesByIdLetterSeqOrderByIdSeq(int seq);
}
