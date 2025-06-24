package com.ttasum.memorial.service.heavenLetter;

import com.ttasum.memorial.domain.entity.flower.Flower;
import com.ttasum.memorial.domain.entity.heavenLetter.HeavenLetter;
import com.ttasum.memorial.domain.entity.memorial.Memorial;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterCommentRepository;
import com.ttasum.memorial.domain.repository.flower.FlowerRepository;
import com.ttasum.memorial.domain.repository.memorial.MemorialRepository;
import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerSelectResponseDto;
import com.ttasum.memorial.dto.heavenLetter.request.HeavenLetterRequestDto;
import com.ttasum.memorial.dto.heavenLetter.request.HeavenLetterUpdateRequestDto;
import com.ttasum.memorial.dto.heavenLetter.request.HeavenLetterVerifyRequestDto;
import com.ttasum.memorial.dto.heavenLetter.request.MemorialSearchRequestDto;
import com.ttasum.memorial.dto.heavenLetter.response.*;
import com.ttasum.memorial.domain.repository.heavenLetter.HeavenLetterRepository;
import com.ttasum.memorial.exception.common.badRequest.InvalidPasscodeException;
import com.ttasum.memorial.exception.common.badRequest.PathVariableMismatchException;
import com.ttasum.memorial.exception.common.conflict.AlreadyDeletedException;
import com.ttasum.memorial.exception.flower.FlowerNotFoundException;
import com.ttasum.memorial.exception.heavenLetter.HeavenLetterNotFoundException;
import com.ttasum.memorial.exception.heavenLetter.MemorialNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
//final 필드에 대해 생성자 주입 자동 생성
@RequiredArgsConstructor
public class HeavenLetterServiceImpl implements HeavenLetterService {

    private final HeavenLetterRepository heavenLetterRepository;
    private final MemorialRepository memorialRepository;
    private final HeavenLetterCommentRepository heavenLetterCommentRepository;
    private final Logger log = LoggerFactory.getLogger(HeavenLetterServiceImpl.class);

    private final FlowerRepository flowerRepo;

    /* 편지 등록 */
    @Transactional
    @Override
    public HeavenLetter createLetter(HeavenLetterRequestDto heavenLetterRequestDto) {

        Memorial memorial = null;
        if (heavenLetterRequestDto.getDonateSeq() != null) {
            memorial = memorialRepository.findById(heavenLetterRequestDto.getDonateSeq())
                    .orElseThrow(MemorialNotFoundException::new);
        }

        HeavenLetter heavenLetter = HeavenLetter.builder()
                .donateSeq(memorial)
                .areaCode(heavenLetterRequestDto.getAreaCode())
                .letterTitle(heavenLetterRequestDto.getLetterTitle())
                .donorName(heavenLetterRequestDto.getDonorName())
                .letterPasscode(heavenLetterRequestDto.getLetterPasscode())
                .letterWriter(heavenLetterRequestDto.getLetterWriter())
                .anonymityFlag(heavenLetterRequestDto.getAnonymityFlag())
                .contents(heavenLetterRequestDto.getLetterContents())
                .fileName(heavenLetterRequestDto.getFileName())
                .orgFileName(heavenLetterRequestDto.getOrgFileName())
                .letterPaper(heavenLetterRequestDto.getLetterPaper())
                .letterFont(heavenLetterRequestDto.getLetterFont())
                .writerId(heavenLetterRequestDto.getWriterId())
                .build();

        return heavenLetterRepository.save(heavenLetter);
    }

    /* 추모관 편지 등록 */
    @Override
    @Transactional(readOnly = true)
    public HeavenLetterFormResponseDto getFormWithDonor(Integer donateSeq) {
        Memorial memorial = memorialRepository.findById(donateSeq)
                .orElseThrow(MemorialNotFoundException::new);

        return HeavenLetterFormResponseDto.builder()
                .donateSeq(memorial.getId())
                .donorName(memorial.getDonorName())
                .areaCode(memorial.getAreaCode())
                .build();
    }

    /* 편지 상세 조회 (댓글 포함) */
    @Transactional
    @Override
    public HeavenLetterDetailResponseDto getLetterById(Integer letterSeq) {

        HeavenLetter heavenLetter = heavenLetterRepository
                .findByIdAndDelFlag(letterSeq, "N")
                .orElseThrow(HeavenLetterNotFoundException::new);

        //커맨드 메서드 사용
        heavenLetter.increaseReadCount();

        //엔티티 -> DTO
        return new HeavenLetterDetailResponseDto(heavenLetter);
    }

    /* 편지 전체 조회 */
    @Transactional
    @Override
    public Page<HeavenLetterListResponseDto> getAllLetters(Pageable pageable) {
        return heavenLetterRepository
                .findAllByDelFlag("N", pageable)
                .map(letter -> {
                    Long commentCount = heavenLetterCommentRepository
                            .countByLetterSeqAndDelFlag(
                                    letter.getId(), "N");
                    return HeavenLetterListResponseDto.fromEntity(letter, commentCount);
                });
    }
    /* 편지 수정/삭제 전 비밀번호 검증 */
    @Transactional(readOnly = true)
    @Override
    public boolean verifyPasscode(Integer letterSeq, String passcode) {

        HeavenLetter heavenLetter = heavenLetterRepository.findById(letterSeq)
                .orElseThrow(HeavenLetterNotFoundException::new);

        if (!heavenLetter.getLetterPasscode().equals(passcode)) {
            throw new InvalidPasscodeException();
        }
        return true;
    }

    /* 편지 수정*/
    @Transactional
    @Override
    public HeavenLetter updateLetter(Integer letterSeq,HeavenLetterUpdateRequestDto heavenLetterUpdateRequestDto) {

        if (!letterSeq.equals(heavenLetterUpdateRequestDto.getLetterSeq())) {
            throw new PathVariableMismatchException("하늘나라 편지 번호와 요청 번호가 일치하지 않습니다.");
        }

        HeavenLetter heavenLetterUpdate = heavenLetterRepository.findById(letterSeq)
                .orElseThrow(HeavenLetterNotFoundException::new);

        if ("Y".equals(heavenLetterUpdate.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        if (!verifyPasscode(letterSeq, heavenLetterUpdateRequestDto.getLetterPasscode())) {
            throw new InvalidPasscodeException();
        }

        Memorial memorial = null;
        if (heavenLetterUpdateRequestDto.getDonateSeq() != null) {
            memorial = memorialRepository.findById(heavenLetterUpdateRequestDto.getDonateSeq())
                    .orElseThrow(MemorialNotFoundException::new);
        }

        //내용수정
        return heavenLetterUpdate.updateLetterContents(heavenLetterUpdateRequestDto, memorial);
    }

    //삭제
    @Transactional
    @Override
    public void deleteLetter(Integer letterSeq,HeavenLetterVerifyRequestDto deleteRequest) {

        // 경로와 본문 값 불일치 (400 Bad Request)
        if (!letterSeq.equals(deleteRequest.getLetterSeq())) {
            throw new PathVariableMismatchException("하늘나라 편지 번호와 요청 번호가 일치하지 않습니다.");
        }
        // 리소스(수혜자 편지)를 찾을 수 없음 (404 Not Found)
        HeavenLetter heavenLetter = heavenLetterRepository.findById(letterSeq)
                .orElseThrow(HeavenLetterNotFoundException::new);

        // 비밀번호 인증 실패 시 예외 발생(400)
        if (!verifyPasscode(letterSeq, deleteRequest.getLetterPasscode())) {
            throw new InvalidPasscodeException();
        }

        // 이미 삭제된 리소스에 대한 요청 (409 Conflict)
        if ("Y".equals(heavenLetter.getDelFlag())) {
            throw new AlreadyDeletedException("이미 삭제된 수혜자 편지입니다.");
        }

        // 5. 삭제 처리
        heavenLetter.softDelete();
    }
    //검색
    @Transactional(readOnly = true)
    @Override
    public Page<HeavenLetterListResponseDto> searchLetters(String type, String keyword, Pageable pageable) {


        log.info("[HeavenLetter 검색] type={}, keyword={}, page={}, size={}",
                type, keyword, pageable.getPageNumber(), pageable.getPageSize());

        Specification<HeavenLetter> spec = notDeleted();

        if ("title".equalsIgnoreCase(type)) {
            spec = spec.and(titleContains(keyword));
        } else if ("contents".equalsIgnoreCase(type)) {
            spec = spec.and(contentsContains(keyword));
        } else if ("all".equalsIgnoreCase(type)) {
            spec = spec.and(titleOrContentsContains(keyword));
        }

        Page<HeavenLetter> result = heavenLetterRepository.findAll(spec, pageable);
        log.info("[검색 결과] 총 건수: {}", result.getTotalElements());

        return result.map(HeavenLetterListResponseDto::fromEntity);
    }

    private Specification<HeavenLetter> notDeleted() {
        return (root, query, cb) -> cb.equal(root.get("delFlag"), "N");
    }

    private Specification<HeavenLetter> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("letterTitle")), "%" + keyword.toLowerCase(Locale.ROOT) + "%");
    }

    private Specification<HeavenLetter> contentsContains(String keyword) {
        return (root, query, cb) ->
                cb.like(root.get("letterContents"), "%" + keyword + "%");
    }

    private Specification<HeavenLetter> titleOrContentsContains(String keyword) {
        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("letterTitle")), "%" + keyword.toLowerCase(Locale.ROOT) + "%"),
                        cb.like(root.get("letterContents"), "%" + keyword + "%")
                );
    }

    //이미지 업로드(
    // uuid 방식
    @Transactional
    @Override
    public List<Map<String, String>> uploadFiles(List<MultipartFile> files, String subFolder) throws IOException {
        String baseDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
        String uploadDir = baseDir + subFolder + "/";
        List<Map<String, String>> resultList = new ArrayList<>();

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

    //기증자 검색
    @Override
    @Transactional(readOnly = true)
    public Page<MemorialSearchResponseDto> searchDonors(
            MemorialSearchRequestDto SearchRequest,
            Pageable pageable) {

        Specification<Memorial> spec = (root, q, cb) ->
                cb.equal(root.get("delFlag"), "N");

        // 이름 검색
        if (SearchRequest.getDonateName() != null && !SearchRequest.getDonateName().isBlank()) {
            String lower = SearchRequest.getDonateName().toLowerCase();
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("donorName")), "%" + lower + "%"));
        }

        // 문자열 그대로 >=, <= 비교
        if (SearchRequest.getStartDate() != null && !SearchRequest.getStartDate().isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("donateDate"),
                            SearchRequest.getStartDate()
                    )
            );
        }
        if (SearchRequest.getEndDate() != null && !SearchRequest.getEndDate().isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("donateDate"),
                            SearchRequest.getEndDate()
                    )
            );
        }

        return memorialRepository.findAll(spec, pageable)
                .map(MemorialSearchResponseDto::of);
    }

    //꽃 선택 메서드
    @Override
    @Transactional
    public FlowerSelectResponseDto selectFlower(Integer letterSeq, Integer flowerSeq, String passcode) {
        HeavenLetter heavenLetter = heavenLetterRepository.findByIdAndDelFlag(letterSeq, "N")
                .orElseThrow(HeavenLetterNotFoundException::new);

        //코드 변수 선언
        int code;

        //비밀번호 성공 여부 판단
        boolean matched = heavenLetter.getLetterPasscode().equals(passcode);

        String message = matched ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다";
        //비밀번호 불 일치시 꽃 : null반환, code : BAD_REQUEST 반환
        code = HttpStatus.BAD_REQUEST.value();
        FlowerDto flowerDto = null;

        //비밀번호 일치 시
        if (matched) {
            Flower flower = flowerRepo.findById(flowerSeq)
                    .orElseThrow(() -> new FlowerNotFoundException("해당하는 꽃을 찾을 수 없습니다. seq=" + flowerSeq));
            heavenLetter.setFlower(flower);
            code = HttpStatus.OK.value();
            flowerDto = FlowerDto.fromEntity(flower);
        }

        return new FlowerSelectResponseDto(matched, code, message, flowerDto);
    }
}
