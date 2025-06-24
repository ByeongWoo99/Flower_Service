from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
import kss  #pip install kss
from typing import List, Tuple

def load_model(model_path: str, num_labels: int = 2):
    tokenizer = AutoTokenizer.from_pretrained(model_path, use_fast=False)
    model = AutoModelForSequenceClassification.from_pretrained(model_path, num_labels=num_labels)
    model.eval()
    return tokenizer, model

def predict_sentences(
    text: str,
    tokenizer,
    model,
    threshold: float = 0.7
) -> Tuple[int, float, List[dict]]:
    """
    긴 문장을 문장 단위로 분리한 후 각각 감정 예측.
    전체 중 threshold 이상 label=1로 판단된 문장이 60% 이상이면 최종 label=1.
    """
    split_sentences = kss.split_sentences(text)
    results = []

    for sent in split_sentences:
        inputs = tokenizer(sent, return_tensors="pt", truncation=True, padding=True)
        with torch.no_grad():
            outputs = model(**inputs)
            probs = torch.softmax(outputs.logits, dim=1)
            confidence = float(probs[0][1])
            label = int(confidence >= threshold)
            results.append({"sentence": sent, "label": label, "confidence": confidence})

    # 집계 판단 기준
    label_1_count = sum(r["label"] for r in results)
    final_label = int(label_1_count / len(results) >= 0.6)
    average_confidence = sum(r["confidence"] for r in results) / len(results)

    return final_label, average_confidence, results
