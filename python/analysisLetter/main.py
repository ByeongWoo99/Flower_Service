from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from abc import ABC, abstractmethod #Abstract Base Class 상속

import torch
from transformers import pipeline, AutoTokenizer

# RoBERTa 계열 모델을 사용할 때, token_type_ids가 따로 사용되지 않으므로
# tokenizer를 미리 만들어 두고, sep_token(문장 구분 토큰)을 꺼내서 사용
tokenizer = AutoTokenizer.from_pretrained("pongjin/roberta_with_kornli")

#입력 인자 어떻게 토크나이저에게 전달할지 결정하는 인자 파서 사용
#ArgumentHandler는 인자 파서의 인터페이스 역할, __call__ method 구현하도록 강제
class ArgumentHandler(ABC):
    """
    Base interface for handling arguments for each :class:`~transformers.pipelines.Pipeline`.
    """

    @abstractmethod
    def __call__(self, *args, **kwargs):
        #ArgumentHandler자체는 인자 처리 로직 없음 -> 에러 발생, 서브 클래스에서 오버라이딩(__call__) 해야함.
        raise NotImplementedError()

#ArgumentHandler 상속 -> __call__ 메서드 구현
class CustomZeroShotClassificationArgumentHandler(ArgumentHandler):
    """
    Handles arguments for zero-shot for text classification by turning each possible label into an NLI
    premise/hypothesis pair.
    """
    #문자열을 ,로 분리하여 리스트 반환
    def _parse_labels(self, labels):
        #str타입인지 검사
        if isinstance(labels, str):
            labels = [label.strip() for label in labels.split(",")]
        return labels

    #sequence - 분류할 문장 리스트, labels - 감정 목록 hypothesis_template - 가설문 템플릿
    def __call__(self, sequences, labels, hypothesis_template):
        #문장이 없거나 감정 목록이 없으면 에러
        if len(labels) == 0 or len(sequences) == 0:
            raise ValueError("You must include at least one label and at least one sequence.")
        if hypothesis_template.format(labels[0]) == hypothesis_template:
            raise ValueError(
                (
                    'The provided hypothesis_template "{}" was not able to be formatted with the target labels. '
                    "Make sure the passed template includes formatting syntax such as {{}} where the label should go."
                ).format(hypothesis_template)
            )

        if isinstance(sequences, str):
            sequences = [sequences]
        labels = self._parse_labels(labels)

        sequence_pairs = []
        for label in labels:
            # 수정부: 두 문장을 페어로 입력했을 때, `token_type_ids`가 자동으로 붙는 문제를 방지하기 위해 미리 두 문장을 `sep_token` 기준으로 이어주도록 함
            sequence_pairs.append(f"{sequences} {tokenizer.sep_token} {hypothesis_template.format(label)}")

        return sequence_pairs, sequences



# zero-shot pipeline 초기화

classifier = pipeline(
    "zero-shot-classification",
    model="pongjin/roberta_with_kornli",
    device=0 if torch.cuda.is_available() else -1,
    args_parser=CustomZeroShotClassificationArgumentHandler()
)

candidate_labels = ["슬픔", "그리움", "감사", "후회", "미안함(사죄)", "희망", "추모+존경"]

app = FastAPI()


class Letter(BaseModel):
    content: str


class ClassifyResponse(BaseModel):
    emotions: List[str]

#ClassifyResponse - 자동으로 JSON으로 직렬화, 스키마 검증 수행
@app.post("/classify", response_model=ClassifyResponse)
def classify(letter: Letter):
    # multi_label=True - 각 레이블 독립적 판단
    result = classifier(letter.content, candidate_labels, multi_label=True)
    # result는 {'labels': [...], 'scores': [...]} 딕셔너리 반환

    # 점수 순으로 정렬
    top2 = sorted(
        zip(result["labels"], result["scores"]),
        key=lambda x: x[1],
        reverse=True
    )[:2] # 상위 2개 반환
    return ClassifyResponse(emotions=[lab for lab, _ in top2])
