//package com.ttasum.memorial.flower.service;
//
//import com.ttasum.memorial.config.WebConfig;
//import com.ttasum.memorial.dto.flower.FastApiResponseDto;
//import com.ttasum.memorial.exception.flower.ContentEmptyException;
//import com.ttasum.memorial.exception.flower.EmotionAnalysisException;
//import com.ttasum.memorial.exception.flower.EmotionEmptyException;
//import com.ttasum.memorial.service.flower.FastApiClientService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.*;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class FastApiClientServiceTest {
//
//    @Mock private WebConfig webConfig;
//    @Mock private WebClient webClient;
//    @Mock private WebClient.RequestBodyUriSpec uriSpec;
//    @Mock private WebClient.RequestBodySpec bodySpec;
//    @Mock private WebClient.ResponseSpec responseSpec;
//
//    private FastApiClientService service;
//
//    @BeforeEach
//    void setUp() {
//        // webConfig.fastApiWebClient() 호출 설정
//        lenient().doReturn(webClient).when(webConfig).fastApiWebClient();
//        service = new FastApiClientService(webConfig);
//    }
//
//    @Test
//    @DisplayName("Fast API 서버에서 2개의 감정을 담은 리스트 반환")
//    void getEmotions() {
//        FastApiResponseDto dto = new FastApiResponseDto();
//        dto.setEmotions(List.of("슬픔", "감사"));
//
//        // Fluent chain 모킹
//        doReturn(uriSpec).when(webClient).post();
//        doReturn(bodySpec).when(uriSpec).uri("/classify");
//        doReturn(bodySpec).when(bodySpec).header("Content-Type", "application/json");
//        doReturn(bodySpec).when(bodySpec).bodyValue(anyMap());
//        doReturn(responseSpec).when(bodySpec).retrieve();
//        doReturn(Mono.just(dto)).when(responseSpec).bodyToMono(FastApiResponseDto.class);
//
//        List<String> emotions = service.getEmotions("테스트");
//        assertEquals(2, emotions.size());
//        assertIterableEquals(List.of("슬픔", "감사"), emotions);
//    }
//
//    @Test
//    @DisplayName("Fast API 서버로 전달 내용 없을 때 예외 테스트")
//    void testGetEmotions_ContentEmpty() {
//        //내용이 null or 비어있으면 예외 발생
//        assertThrows(ContentEmptyException.class, () -> service.getEmotions(null));
//        assertThrows(ContentEmptyException.class, () -> service.getEmotions("   "));
//    }
//
//    @Test
//    @DisplayName("Fast API 서버 연걸 예외 테스트")
//    void getEmotions_EmotionAnalysis() {
//        // WebClient.post() 호출 시 WebClientException 던지기
//        doThrow(new WebClientException("connection failed") {})
//                .when(webClient).post();
//
//        EmotionAnalysisException ex = assertThrows(
//                EmotionAnalysisException.class,
//                () -> service.getEmotions("테스트")
//        );
//        assertEquals("감정 분석 서버에 연결할 수 없습니다.", ex.getMessage());
//    }
//
//    @Test
//    @DisplayName("Fast API 서버에서 빈 감정 반환 했을 때 예외 테스트")
//    void getEmotions_EmotionEmpty() {
//        // Mono.empty() → block() 결과 null
//        doReturn(uriSpec).when(webClient).post();
//        doReturn(bodySpec).when(uriSpec).uri("/classify");
//        doReturn(bodySpec).when(bodySpec).header(any(), any());
//        doReturn(bodySpec).when(bodySpec).bodyValue(anyMap());
//        doReturn(responseSpec).when(bodySpec).retrieve();
//        doReturn(Mono.empty()).when(responseSpec).bodyToMono(FastApiResponseDto.class);
//
//        assertThrows(EmotionEmptyException.class, () -> service.getEmotions("테스트"));
//
//        // 빈 리스트 반환 케이스
//        FastApiResponseDto emptyDto = new FastApiResponseDto();
//        emptyDto.setEmotions(List.of());
//        doReturn(Mono.just(emptyDto)).when(responseSpec).bodyToMono(FastApiResponseDto.class);
//
//        assertThrows(EmotionEmptyException.class, () -> service.getEmotions("테스트"));
//    }
//
//}
