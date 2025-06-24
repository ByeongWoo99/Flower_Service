from datasets import load_dataset
import pandas as pd

"""
Hugging Face의 KOTE 데이터셋을 불러와서,
각 예제에 대해 safe_emotions 포함 여부를 기준으로 label 값을 부여한 뒤,
train/validation/test 데이터를 라벨이 포함된 CSV (엑셀 열기 가능) 로 저장

이 코드는 다음 3개 CSV 파일을 생성:
kote_negemotion_train.csv
kote_negemotion_validation.csv
kote_negemotion_test.csv

negative_labels = [
    '불평/불만', '지긋지긋', '화남/분노', '우쭐댐/무시함', '의심/불신', '한심함',
    '역겨움/징그러움', '짜증', '어이없음', '귀찮음', '증오/혐오', '부담/안_내킴', '재미없음'
]
"""

# 1. 데이터셋 로드
dataset = load_dataset("searle-j/kote", trust_remote_code=True)

# 2. 전체 라벨 이름 리스트 확인
id2label = dataset['train'].features['labels'].feature.names

# 3. 부정 감정 라벨 리스트 (분노 계열 확장)
negative_labels = [
    '불평/불만', '지긋지긋', '화남/분노', '우쭐댐/무시함', '의심/불신', '한심함',
    '역겨움/징그러움', '짜증', '어이없음', '귀찮음', '증오/혐오', '부담/안_내킴', '재미없음'
]

# 4. 위 감정들의 인덱스를 추출
negative_label_indices = [id2label.index(label) for label in negative_labels]

# 5. 이진 라벨링 함수 정의 (부정 감정 포함 여부 → label: 1 or 0)
def to_binary_negative(example):
    return {
        'ID': example['ID'],
        'text': example['text'],
        'label': int(any(label in negative_label_indices for label in example['labels']))
    }

# 6. 각 split 별로 변환 → CSV 저장
for split in ['train', 'validation', 'test']:
    data = dataset[split].map(to_binary_negative)
    df = pd.DataFrame(data)
    df.to_csv(f'kote/kote_negemotion_{split}.csv', index=False, encoding='utf-8-sig')

print(df['label'].value_counts())