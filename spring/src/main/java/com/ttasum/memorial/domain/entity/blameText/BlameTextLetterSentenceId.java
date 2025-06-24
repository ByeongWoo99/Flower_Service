package com.ttasum.memorial.domain.entity.blameText;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
/** Embeddable
 * 테이블에 직접 매핑되는 컬럼이 아니고, 엔티티에 포함되는 값 객체로 동작
 * 보통 복합 키나 주소(Address), 기간(Period) 등 객체 내부 구성에 많이 사용  **/
// 복합 기본 키를 정의
@Embeddable  //JPA에서 "객체 안에 포함될 수 있는 값 타입 클래스"를 정의
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode  //두 객체가 같은지 비교하기 위해 반드시 필요 (식별자 비교 시)
public class BlameTextLetterSentenceId implements Serializable {  //IdClass로 사용할 클래스는 반드시 직렬화 가능
    private Integer letterSeq;
    private Integer seq;
}