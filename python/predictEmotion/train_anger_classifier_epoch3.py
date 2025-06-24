import pandas as pd
from datasets import Dataset
from transformers import AutoTokenizer, AutoModelForSequenceClassification, Trainer, TrainingArguments, EarlyStoppingCallback
from sklearn.metrics import accuracy_score, precision_recall_fscore_support

# ------------------------------------------------------------
# 🔹 사용된 감정 라벨 (전처리 기준)
"""
negative_labels = [
    '불평/불만', '지긋지긋', '화남/분노', '우쭐댐/무시함', '의심/불신', '한심함',
    '역겨움/징그러움', '짜증', '어이없음', '귀찮음', '증오/혐오', '부담/안_내킴', '재미없음'
]

label == 1 → 위 감정 중 하나 이상 포함
label == 0 → 그 외 감정 or 감정 없음
"""
# ------------------------------------------------------------

# 🔸 CSV 파일 불러오기
train_df = pd.read_csv("kote/kote_negemotion_train.csv")
val_df = pd.read_csv("kote/kote_negemotion_validation.csv")

# label을 int로 확실히 변환
train_df["label"] = train_df["label"].astype(int)
val_df["label"] = val_df["label"].astype(int)

# Dataset으로 변환
train_ds = Dataset.from_pandas(train_df)
val_ds = Dataset.from_pandas(val_df)

# ➕ 이 부분 추가!
train_ds = train_ds.map(lambda x: {"label": int(x["label"])})
val_ds = val_ds.map(lambda x: {"label": int(x["label"])})

train_ds = train_ds.remove_columns(["labels", "ID"])
val_ds = val_ds.remove_columns(["labels", "ID"])

# 확인
print(train_ds.features)
print(train_ds[0])

# 🔸 기존 모델의 토크나이저 로드
tokenizer = AutoTokenizer.from_pretrained("./results/final_model")  # ✅ 기존 모델 사용

# 🔸 토큰화 함수 정의
def tokenize_fn(example):
    return tokenizer(example["text"], padding="max_length", truncation=True, max_length=128)

# 🔸 토큰화 적용
train_ds = train_ds.map(tokenize_fn, batched=True)
val_ds = val_ds.map(tokenize_fn, batched=True)

# 🔸 기존 학습 모델 로드 (이진 분류용)
model = AutoModelForSequenceClassification.from_pretrained("./results/final_model", num_labels=2)  # ✅ 기존 모델 사용

# 🔸 평가 지표 함수
def compute_metrics(p):
    preds = p.predictions.argmax(-1)
    labels = p.label_ids
    precision, recall, f1, _ = precision_recall_fscore_support(labels, preds, average='binary', zero_division=0)
    acc = accuracy_score(labels, preds)
    return {"accuracy": acc, "f1": f1, "precision": precision, "recall": recall}

# 🔸 훈련 파라미터 설정 (EarlyStopping + 로그 설정 포함)
training_args = TrainingArguments(
    output_dir="./results/final_e3_model",
    evaluation_strategy="epoch",
    save_strategy="epoch",
    learning_rate=2e-5,
    max_grad_norm=1.0,
    warmup_steps=500,  # 🔹 (선택) 안정성 강화
    logging_strategy="steps",
    logging_steps=50,
    logging_dir="./logs/final_e3_log",
    num_train_epochs=3,
    per_device_train_batch_size=16,
    per_device_eval_batch_size=16,
    load_best_model_at_end=True,
    metric_for_best_model="f1",
    greater_is_better=True,
    save_total_limit=2,
    seed=42
)

# 🔸 Trainer 객체 생성 (EarlyStoppingCallback 포함)
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=train_ds,
    eval_dataset=val_ds,
    tokenizer=tokenizer,
    compute_metrics=compute_metrics,
    callbacks=[EarlyStoppingCallback(early_stopping_patience=2)]
)

# 🔸 학습 시작
trainer.train()

# 🔸 모델 저장
trainer.save_model("./results/final_e3_model")
