from fastapi import FastAPI
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
import os
import logging
import datetime
from emotion_analysis_utils import predict_sentences, load_model
from bs4 import BeautifulSoup

# 날짜 기반 로그 디렉토리 및 파일 설정
today = datetime.date.today()
year = today.strftime("%Y")
month = today.strftime("%m")
day = today.strftime("%d")

month_dir = os.path.join("logs", f"{year}-{month}")
log_file_path = os.path.join(month_dir, f"{day}.log")
os.makedirs(month_dir, exist_ok=True)

# logging 설정 (루트 로거 사용)
logger = logging.getLogger()
logger.setLevel(logging.INFO)

formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")

file_handler = logging.FileHandler(log_file_path, encoding="utf-8")
file_handler.setFormatter(formatter)

stream_handler = logging.StreamHandler()
stream_handler.setFormatter(formatter)

if logger.hasHandlers():
    logger.handlers.clear()
logger.addHandler(file_handler)
logger.addHandler(stream_handler)

# 디버그: 실행 중인 경로 출력
logger.info(f"현재 실행 중인 파일 경로: {os.path.abspath(__file__)}")

app = FastAPI()

# 모델 로딩
MODEL_NAME = "./results/final_e3_model"
tokenizer, model = load_model(MODEL_NAME)

# 요청 형식 정의
class TextRequest(BaseModel):
    sentence: str

# escaped_text = json.dumps({"sentence": text})

# 감정 분석 API
@app.post("/predict-blameText")
def predict_emotion(req: TextRequest):
    # HTML 문법 기반 개행/공백 변환 처리
    # HTML 기반 개행 제거 → 평문 변환
    soup = BeautifulSoup(req.sentence, "html.parser")
    text_only = soup.get_text(separator=" ")
    cleaned_sentence = text_only.replace("\n", " ").replace("\r", " ").strip()


    final_label, avg_confidence, details = predict_sentences(
        text=cleaned_sentence,
        tokenizer=tokenizer,
        model=model,
        threshold=0.7
    )

    # 요약 로그
    logger.info(f"[PREDICT] sentence=\"{cleaned_sentence}\" | label={final_label} | confidence={avg_confidence:.4f}")

    # 상세 로그 (문장 단위 예측 결과)
    for idx, d in enumerate(details):
        logger.info(f"[DETAIL-{idx+1}] sentence=\"{d['sentence']}\" | label={d['label']} | confidence={d['confidence']:.4f}")

    return {
        "sentence": cleaned_sentence,
        "label": final_label,
        "confidence": avg_confidence,
        "details": details
    }
