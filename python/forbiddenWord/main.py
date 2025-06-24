from fastapi import FastAPI
from pydantic import BaseModel
from transformers import ElectraTokenizer, ElectraForSequenceClassification
import torch
import os
import logging
import datetime

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

# # 로그 디렉토리 및 파일 설정
# os.makedirs("logs", exist_ok=True)
# log_file_path = os.path.join("logs", "predict.log")
#
# logging.basicConfig(
#     level=logging.INFO,
#     format="%(asctime)s [%(levelname)s] %(message)s",
#     handlers=[
#         logging.FileHandler(log_file_path, encoding="utf-8"),
#         logging.StreamHandler()  # 콘솔 출력도 같이
#     ]
# )

app = FastAPI()

# 모델 및 토크나이저 로딩
MODEL_PATH = "./finetuned-koelectra-badword"
BASE_MODEL = "monologg/koelectra-base-discriminator"

tokenizer = ElectraTokenizer.from_pretrained(BASE_MODEL)
model = ElectraForSequenceClassification.from_pretrained(MODEL_PATH)
model.eval()

# 요청 데이터 구조
class TextRequest(BaseModel):
    sentence: str

# 금칙어 판별 API
@app.post("/predict-forbiddenWord")
def predict(req: TextRequest):
    inputs = tokenizer(req.sentence, return_tensors="pt", truncation=True, padding=True)
    with torch.no_grad():
        outputs = model(**inputs)
        probs = torch.softmax(outputs.logits, dim=1)
        confidence = float(probs[0][1])  # 금칙어(1) 확률
        threshold = 0.7
        label = int(confidence > threshold)

    # 로그 기록
    logging.info(f"[PREDICT] sentence=\"{req.sentence}\" | label={label} | confidence={confidence:.4f}")

    return {
        "sentence": req.sentence,
        "label": label,
        "confidence": confidence
    }
