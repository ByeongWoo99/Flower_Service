package com.ttasum.memorial.service.admin;

import com.ttasum.memorial.domain.entity.admin.AdminAuthority;
import com.ttasum.memorial.domain.entity.admin.AdminDepartment;
import com.ttasum.memorial.domain.entity.admin.AdminPosition;
import com.ttasum.memorial.domain.entity.admin.User;
import com.ttasum.memorial.domain.entity.blameText.BlameTextComment;
import com.ttasum.memorial.domain.entity.blameText.BlameTextCommentSentence;
import com.ttasum.memorial.domain.entity.blameText.BlameTextLetter;
import com.ttasum.memorial.domain.type.ContentType;
import com.ttasum.memorial.dto.blameText.BlameTextCommentDto;
import com.ttasum.memorial.dto.donationStory.response.PageResponse;
import com.ttasum.memorial.dto.UserDto;
import com.ttasum.memorial.dto.admin.AdminRequestDto;
import com.ttasum.memorial.dto.blameText.BlameTextLetterDto;
import com.ttasum.memorial.dto.blameText.BlameTextLetterSentenceDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface AdminService {
    ResponseEntity<?> duplicationId(String id);

    ArrayList<AdminAuthority> findAllAuthority();
    ArrayList<AdminPosition> findAllPosition();
    ArrayList<AdminDepartment> findAllDepartment();

    ResponseEntity<?> signupAdmin(UserDto admin);
    User toEntity(UserDto userDto);

    PageResponse<BlameTextLetterDto> getBlameTextLetters(String option, String orderBy, Pageable pageable);

    ArrayList<BlameTextLetterSentenceDto> getBlameTextLettersSentences(int seq);

    ResponseEntity<?> deleteBlameText(int seq, String boardType, ContentType contentType);

    PageRequest setOrderByOptions(int page, int size, String orderBy);

    PageResponse<BlameTextCommentDto> getBlameTextComment(String filter, String orderBy, Pageable pageRequest);

    ArrayList<BlameTextCommentSentence> getBlameTextCommentSentence(int seq);
}
