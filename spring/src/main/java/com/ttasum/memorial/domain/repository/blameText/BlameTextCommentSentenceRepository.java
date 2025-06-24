package com.ttasum.memorial.domain.repository.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextCommentSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface BlameTextCommentSentenceRepository extends JpaRepository<BlameTextCommentSentence, Integer> {
    @Query("SELECT c FROM BlameTextCommentSentence c WHERE c.id.letterSeq = :seq ORDER BY c.id.seq ASC")
    Optional<ArrayList<BlameTextCommentSentence>> getBlameTextCommentSentenceByIdLetterSeq(@Param("seq") int seq);
}
