package com.ttasum.memorial.domain.repository.blameText;

import com.ttasum.memorial.domain.entity.blameText.BlameTextComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BlameTextCommentRepository extends JpaRepository<BlameTextComment, Integer> {
    Optional<BlameTextComment> findByOriginSeqAndDeleteFlag(Integer OriginSeq, Integer deleteFlag);
//    @Query("SELECT b FROM BlameTextComment b WHERE b.comment.commentSeq = :commentSeq AND b.deleteFlag = :deleteFlag")
//    Optional<BlameTextComment> findByCommentSeqAndDeleteFlag(@Param("commentSeq") Integer commentSeq, @Param("deleteFlag") Integer deleteFlag);

    Page<BlameTextComment> findBlameTextCommentsByLabel(int label, Pageable pageable);
    Optional<List<BlameTextComment>> findBlameTextCommentsByBoardTypeAndStorySeqAndOriginSeqAndDeleteFlag(String boardType, Integer storySeq, Integer commentSeq, int i);
}
