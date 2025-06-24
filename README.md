## 비난 글 판단 AI 모델 사용법
이 프로젝트는 한국어 문장에서 비난 의도를 판별하는 감정 분류 모델을 제공
FastAPI 서버를 통해 RESTful 방식으로 사용할 수 있음

## 모델 파일 구성
아래와 같은 모델 구성 파일들이 /python/predictEmotion/results/final_e3_model 폴더에 포함되어 있어야 함:
- config.json  
- model.safetensors  
- special_tokens_map.json  
- tokenizer.json  
- tokenizer_config.json  
- training_args.bin  
- vocab.txt

폴더 전체 다운로드: [Google Drive 링크](https://drive.google.com/drive/folders/1vmYgVjQV9z3diasossEyTWKd-xN-wYyN)

## 실행 방법
1. Conda 환경 구성

   conda env create -f environment.yml

   conda activate forbiddenWord-env
2. FastAPI 서버 실행

   uvicorn main:app --reload --host 0.0.0.0 --port 8000
3. Swagger 문서 접속:

   http://localhost:8000/docs
