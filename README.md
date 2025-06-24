## 프로젝트 소개
이 프로젝트는 편지 내용을 입력받아 감정을 분석하고, 
해당 감정에 어울리는 꽃을 추천해주는 서비스입니다.

- 감정 분석은 FastAPI와 AI모델을 활용했습니다.
- 꽃 추천은 꽃 정보를 담고 있는 데이터베이스에서 랜덤하게 선택하여 제공합니다.


- 사용한 모델 : **HuggingFace**의 **pongjin/roberta_with_kornli**
- 감정 분석 방법 : **Zero-Shot** 분류
  
### 감정 분석 기준 
분류는 아래의 **7가지 감정 후보**를 기반으로 이루어집니다
- 슬픔
- 그리움
- 감사
- 후회
- 미안함(사죄)
- 희망
- 추모·존경
 
입력된 문장은 이 감정들 중 여러 감정과 관련이 있을 수 있으며,
AI 모델은 그중 **가장 관련 있는 감정 2개**를 반환 합니다.
---
## 실행 방법

**1.Conda 환경 생성**
````
conda env create -f environment.yml

conda activate analysisLetter-env
````
**2.FastAPI 서버 실행**
````
uvicorn main:app --reload
````
---
## Flower 서비스 소개
편지의 감정 분석 결과를 바탕으로 꽃을 추천하고,
관리자는 꽃 등록/수정/삭제 등 꽃 정보 관리를 할 수 있습니다.

관리자의 프론트 페이지는 첨부한 레포지토리의 주소를 통해 확인 할 수 있습니다.

[프론트 GitHub 주소](https://github.com/ByeongWoo99/Admin_Flower_Service)

---
## 주요 구성
- FlowerAdminService : 관리자가 꽃 정보를 등록/수정/삭제 등 조회할 수 있는 서비스


- FlowerService : 감정 기반 꽃 추천 로직과 꽃 선택 기능을 제공하는 서비스
  - 꽃 저장하는 기능은 해당하는 게시글의 Service 파일에 작성했습니다.

---
## 주요 기능

1. **꽃 추천**
- 감정 분석 API(FastAPI)로부터 감정을 받아 해당 감정과 어울리는 꽃을 추천합니다.
  - 감정마다 등록된 꽃 중 랜덤으로 1개씩 추천합니다.
  - 추천 가능한 꽃이 없으면 예외를 반환합니다.
  - 감정 추출은 `FastApiClientService`의 `getEmotions(content)` 메서드를 통해 수행되며, 내부적으로 WebClient를 사용해 FastAPI의 `/classify` 엔드포인트에 POST 요청을 보냅니다.
  
2. **꽃 선택**
- 편지 작성 후 비밀번호 인증을 통해 원하는 꽃을 선택할 수 있습니다.
  - 비밀번호가 일치하면 편지에 선택한 꽃 정보를 저장합니다.
  - 불일치 시 선택은 무효 처리됩니다.

3. **꽃 관리 (관리자용)**
- 꽃 등록: 꽃 정보와 이미지 파일을 함께 저장합니다.
- 꽃 수정: 텍스트 정보 또는 이미지 포함 전체 수정이 가능합니다.
- 꽃 삭제: delFlag 값을 활용한 소프트 삭제 방식입니다.
- 꽃 조회: 전체 꽃 목록을 페이징 처리하여 조회하거나, 개별 꽃 정보를 조회할 수 있습니다.

---
## API 목록
###  관리자용 꽃 관리 API

| HTTP Method | Endpoint                      | 설명                           | 
|-------------|-------------------------------|--------------------------------|
| GET         | `/admin/flowers/{flowerSeq}`  | 꽃 상세 조회                    |
| GET         | `/admin/flowers`              | 꽃 목록 페이지 조회             |
| POST        | `/admin/flowers`              | 꽃 등록 (JSON + 이미지 업로드)  |
| PATCH       | `/admin/flowers/{flowerSeq}`  | 꽃 정보 수정 (이미지 포함)     |
| PATCH       | `/admin/flowers/{flowerSeq}`  | 꽃 정보 수정 (텍스트만)        |
| DELETE      | `/admin/flowers/{flowerSeq}`  | 꽃 삭제 (소프트 삭제)          |

### 사용자용 꽃 추천 API

| HTTP Method | Endpoint                                       | 설명            |
|-------------|------------------------------------------------|---------------|
| POST        | `/donationLetters/{storySeq}/flowers`          | 기증 후 스토리 꽃 저장 |
| POST        | `/heavenLetters/{letterSeq}/flowers`           | 하늘나라 편지 꽃 저장  |
| POST        | `/recipientLetters/{letterSeq}/flowers`        | 수혜자 편지 꽃 저장   |

- 꽃 추천 API는 해당 편지의 본문 내용을 기반으로 감정을 분석하고, 그 감정에 어울리는 꽃을 추천합니다.