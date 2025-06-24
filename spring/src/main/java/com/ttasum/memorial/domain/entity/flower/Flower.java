package com.ttasum.memorial.domain.entity.flower;

import com.ttasum.memorial.dto.flower.FlowerDto;
import com.ttasum.memorial.dto.flower.FlowerUpdateRequestDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flower")
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private int seq;

    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "emotion", length = 10)
    private String emotion;

    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "org_file_name")
    private String orgFileName;

    //기본값 "N", 기본값 적용이 안되서 추가
    @Setter
    @Builder.Default
    @Column(name = "del_flag", length = 1)
    private String delFlag = "N";

    //수정 메서드 추가
    public void updateFlower(FlowerUpdateRequestDto requestDto) {
        this.name = requestDto.getName();
        this.emotion = requestDto.getEmotion();
        this.meaning = requestDto.getMeaning();
        this.imgUrl = requestDto.getImgUrl();
        this.orgFileName = requestDto.getOrgFileName();
        this.delFlag = requestDto.getDelFlag();
    }

    // delFlag == null 이면 "N"으로 채워줌
    @PreUpdate
    public void preUpdate() {
        if (this.delFlag == null) {
            this.delFlag = "N";
        }
    }
}
