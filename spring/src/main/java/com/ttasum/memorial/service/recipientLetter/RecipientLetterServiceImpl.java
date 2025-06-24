package com.ttasum.memorial.service.recipientLetter;


import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.recipientLetter.RecipientLetter;
import com.ttasum.memorial.domain.enums.OrganCode;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.domain.repository.recipientLetter.RecipientLetterCommentRepository;
import com.ttasum.memorial.domain.repository.recipientLetter.RecipientLetterRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterUpdateRequestDto;
import com.ttasum.memorial.dto.recipientLetter.request.RecipientLetterVerifyRequestDto;
import com.ttasum.memorial.dto.recipientLetter.response.*;
import com.ttasum.memorial.exception.common.conflict.AlreadyDeletedException;
import com.ttasum.memorial.exception.common.badRequest.InvalidPasscodeException;
import com.ttasum.memorial.exception.common.badRequest.PathVariableMismatchException;
import com.ttasum.memorial.exception.common.notFound.NotFoundException;
import com.ttasum.memorial.exception.common.serverError.FileStorageException;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.exception.heavenLetter.HeavenLetterNotFoundException;
import com.ttasum.memorial.exception.recipientLetter.RecipientOrganNameEmptyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ttasum.memorial.exception.recipientLetter.RecipientLetterNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
@RequiredArgsConstructor

public class RecipientLetterServiceImpl implements RecipientLetterService {

    private final RecipientLetterRepository recipientLetterRepository;
    private final RecipientLetterCommentRepository recipientLetterCommentRepository;

    private final FlowerRepository flowerRepo;

    /* 편지 등록 */
    @Transactional
    @Override
    public void createLetter(RecipientLetterRequestDto createRequestDto) {

            if ("ORGAN000".equals(createRequestDto.getOrganCode())
                    && (createRequestDto.getOrganEtc() == null || createRequestDto.getOrganEtc().isBlank())) {
                throw new RecipientOrganNameEmptyException();
            }


        RecipientLetter recipientLetter = RecipientLetter.builder()
                .letterWriter(createRequestDto.getLetterWriter())
                .anonymityFlag(createRequestDto.getAnonymityFlag())
                .letterPasscode(createRequestDto.getLetterPasscode())
                .organCode(createRequestDto.getOrganCode())
                .organEtc(createRequestDto.getOrganEtc())
                .recipientYear(createRequestDto.getRecipientYear())
                .storyTitle(createRequestDto.getStoryTitle())
                .letterContents(createRequestDto.getLetterContents())
                .orgFileName(createRequestDto.getOrgFileName())
                .fileName(createRequestDto.getFileName())
                .letterPaper(createRequestDto.getLetterPaper())
                .letterFont(createRequestDto.getLetterFont())
                .writerId(createRequestDto.getWriterId())
                .build();

        recipientLetterRepository.save(recipientLetter);
    }

    /* 편지 상세 조회 (댓글 포함) */
    @Transactional
    @Override
    public RecipientLetterDetailResponse getLetterById(Integer letterSeq) {

            RecipientLetter recipientLetter = recipientLetterRepository
                    .findWithCommentsByLetterSeq(letterSeq)
                    .orElseThrow(() -> new NotFoundException("해당 수혜자 편지를 찾을 수 없습니다."));

            //커맨드 메서드 사용
            recipientLetter.increaseReadCount();

            //엔티티 -> DTO
            return new RecipientLetterDetailResponse(recipientLetter);
        }

    /* 편지 전체 조회 */
    @Transactional(readOnly = true)
    @Override
    public Page<RecipientLetterListResponseDto> getAllLetters(Pageable pageable) {
        return recipientLetterRepository
                .findAll(pageable)
                .map(recipientLetter -> {
                    Long commentCount = recipientLetterCommentRepository
                            .countByLetterSeq_LetterSeqAndDelFlag(
                                    recipientLetter.getLetterSeq(),"N");
                    return  RecipientLetterListResponseDto.fromEntity(recipientLetter, commentCount);
        });

     }
    /* 편지 수정/삭제 전 비밀번호 검증 */
    @Transactional(readOnly = true)
    @Override
    public boolean verifyPasscode(Integer letterSeq, String passcode) {

//        // 리소스(수혜자 편지)를 찾을 수 없음 (404 Not Found)
        RecipientLetter recipientLetter = recipientLetterRepository.findById(letterSeq)
                .orElseThrow(RecipientLetterNotFoundException::new);


        if (!recipientLetter.getLetterPasscode().equals(passcode)) {
            throw new InvalidPasscodeException();
        }

        return true;
    }

    /* 편지 수정*/
    @Transactional
    @Override
    public void updateLetter(Integer letterSeq, RecipientLetterUpdateRequestDto updateRequestDto) {

        if (!letterSeq.equals(updateRequestDto.getLetterSeq())) {
            throw new PathVariableMismatchException("수혜자 편지 번호와 요청 번호가 일치하지 않습니다.");
        }

        // 2) 레코드 존재 확인
        RecipientLetter recipientLetter = recipientLetterRepository.findById(letterSeq)
                .orElseThrow(RecipientLetterNotFoundException::new);

        // 3) 비밀번호 검증
        if (!verifyPasscode(letterSeq, updateRequestDto.getLetterPasscode())) {
            throw new InvalidPasscodeException();
        }

        // 4) organCode = "ORGAN000" 이면서 organEtc 가 비어있으면 예외
        if ("ORGAN000".equalsIgnoreCase(updateRequestDto.getOrganCode())
                && (updateRequestDto.getOrganEtc() == null || updateRequestDto.getOrganEtc().isBlank())) {
            throw new RecipientOrganNameEmptyException();
        }

        // 5) 실제 수정 메서드 호출
        recipientLetter.updateLetterContents(updateRequestDto);
    }


    /* 편지 삭제(soft delete) */
    @Transactional
    @Override
    public void deleteLetter(Integer letterSeq, RecipientLetterVerifyRequestDto deleteRequest) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if (!letterSeq.equals(deleteRequest.getLetterSeq())) {
            throw new PathVariableMismatchException("수혜자 편지 번호와 요청 번호가 일치하지 않습니다.");
        }

        // 리소스(수혜자 편지)를 찾을 수 없음 (404 Not Found)
        RecipientLetter recipientLetter = recipientLetterRepository.findById(letterSeq)
                .orElseThrow(RecipientLetterNotFoundException::new);

        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!verifyPasscode(letterSeq, deleteRequest.getLetterPasscode())) {
            throw new InvalidPasscodeException();
        }

        // 이미 삭제된 리소스에 대한 요청 (409 Conflict)
        if ("Y".equals(recipientLetter.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        // 5. 삭제 처리
        recipientLetter.softDelete();
    }

    /* 편지 검색 */
    @Transactional(readOnly = true)
    @Override
    public Page<RecipientLetterListResponseDto> searchLetters(String type, String keyword, Pageable pageable) {
        Logger log = LoggerFactory.getLogger(RecipientLetterServiceImpl.class);

        log.info("[HeavenLetter 검색] type={}, keyword={}, page={}, size={}",
                type, keyword, pageable.getPageNumber(), pageable.getPageSize());

        Specification<RecipientLetter> spec = notDeleted();

        if ("title".equalsIgnoreCase(type)) {
            spec = spec.and(titleContains(keyword));
        } else if ("contents".equalsIgnoreCase(type)) {
            spec = spec.and(contentsContains(keyword));
        } else if ("all".equalsIgnoreCase(type)) {
            spec = spec.and(allFieldsContains(keyword));
        }

        Page<RecipientLetter> result = recipientLetterRepository.findAll(spec, pageable);
        log.info("[검색 결과] 총 건수: {}", result.getTotalElements());

        return result.map(RecipientLetterListResponseDto::fromEntity);
    }

    private Specification<RecipientLetter> notDeleted() {
        return (root, query, cb) -> cb.equal(root.get("delFlag"), "N");
    }

    private Specification<RecipientLetter> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("storyTitle")), "%" + (keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT)) + "%");
    }

    private Specification<RecipientLetter> contentsContains(String keyword) {
        return (root, query, cb) ->
                cb.like(root.get("letterContents"), "%" + (keyword == null ? "" : keyword.trim()) + "%");
    }

    private Specification<RecipientLetter> allFieldsContains(String keyword) {

        String raw = (keyword == null ? "" : keyword.trim());
        String lower = raw.toLowerCase(Locale.ROOT);

        // 한글 장기명 매핑 시도
        Optional<OrganCode> organCode = OrganCode.findByName(raw);

        return (root, query, cb) -> {
            Predicate title = cb.like(cb.lower(root.get("storyTitle")), "%" + lower + "%");
            Predicate contents = cb.like(cb.lower(root.get("letterContents")), "%" + lower + "%");
            Predicate etc = cb.like(cb.lower(root.get("organEtc")), "%" + lower + "%");
            Predicate year = cb.like(cb.lower(root.get("recipientYear")), "%" + lower + "%");

            // 장기명 → 코드로 변환되었으면 해당 코드도 포함하여 검색
            if (organCode.isPresent()) {
                String code = organCode.get().getCode();
                Predicate organCodeMatch = cb.like(cb.lower(root.get("organCode")), "%" + code.toLowerCase() + "%");
                return cb.or(title, contents, organCodeMatch, etc, year);
            } else {
                // 장기명 매핑 실패 시 organCode 조건 제외
                return cb.or(title, contents, etc, year);
            }
        };
    }

        /* 파일 업로드 */
        // uuid 방식
        @Transactional
        @Override
        public List<Map<String, String>> uploadFiles(List<MultipartFile> files, String subFolder) throws IOException {
            String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
            String uploadDir = baseDir + subFolder + "/";
            List<Map<String, String>> resultList = new ArrayList<>();

        try{
            for (MultipartFile file : files) {
                String orgFileName = file.getOriginalFilename();

                // 확장자 추출
                String ext = "";
                if (orgFileName != null && orgFileName.contains(".")) {
                    ext = orgFileName.substring(orgFileName.lastIndexOf("."));
                }
                // [선택 1] UUID 기반 파일명 (기본값)
                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + ext; // 저장용 파일명

                // [선택 2] 랜덤 HEX (32자리, 대문자)
    //            String fileName = generateHexFileName(ext);

                //디렉토리 생성
                Path dirPath = Paths.get(uploadDir);
                Files.createDirectories(dirPath);

                //파일 저장
                Path filePath = dirPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                // 접근 가능한 URL 경로 생성
                String url = "/images/" + subFolder + "/" + fileName;

                Map<String, String> fileMap = new HashMap<>();
                fileMap.put("fileName", fileName);
                fileMap.put("orgFileName", orgFileName);
                fileMap.put("url","/images/" + subFolder + "/" + fileName);
                resultList.add(fileMap);
            }
            return resultList;
        }catch (IOException e){
            throw new FileStorageException();
        }
        }
        // HEX 방식
    //    private String generateHexFileName(String ext) {
    //        SecureRandom random = new SecureRandom();
    //        byte[] bytes = new byte[16];
    //        random.nextBytes(bytes);
    //
    //        StringBuilder hex = new StringBuilder();
    //        for (byte b : bytes) {
    //            hex.append(String.format("%02X", b));
    //        }
    //        return hex.toString() + ext;
    //    }

    //꽃 선택 메서드
    @Override
    @Transactional
    public FlowerSelectResponseDto selectFlower(Integer letterSeq, Integer flowerSeq, String passcode) {
        RecipientLetter recipientLetter = recipientLetterRepository.findByLetterSeqWithCommentsAndFlower(letterSeq)
                .orElseThrow(HeavenLetterNotFoundException::new);

        //코드 변수 선언
        int code;

        //비밀번호 성공 여부 판단
        boolean matched = recipientLetter.getLetterPasscode().equals(passcode);

        String message = matched ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다";
        //비밀번호 불 일치시 꽃 : null반환, code : BAD_REQUEST 반환
        code = HttpStatus.BAD_REQUEST.value();
        FlowerDto flowerDto = null;

        //비밀번호 일치 시
        if (matched) {
            Flower flower = flowerRepo.findById(flowerSeq)
                    .orElseThrow(() -> new FlowerNotFoundException("해당하는 꽃을 찾을 수 없습니다. seq=" + flowerSeq));
            recipientLetter.setFlower(flower);
            code = HttpStatus.OK.value();
            flowerDto = FlowerDto.fromEntity(flower);
        }

        return new FlowerSelectResponseDto(matched, code, message, flowerDto);
    }

    }



