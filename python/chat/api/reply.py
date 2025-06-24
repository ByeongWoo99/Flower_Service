import json
import logging
from http import HTTPStatus

from fastapi import APIRouter, Request
from starlette.responses import JSONResponse

from ..core.exceptions import (BadRequestError, DocumentNotFoundError,
							   JsonParsingError, InvalidTypeReturnError)
from ..models.chat import ChatInput
from ..schemas.response import ApiResponse

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# 라우터 생성
router = APIRouter(
	prefix="/chat",
	tags=['chat']
)


@router.post("/")
async def reply(chat: ChatInput, request: Request):
	""" 사용자 입력에 대하여 챗봇 응답을 반환하는 API를 구현합니다.

	Args:
		chat(ChatInput): 사용자 입력과 관련된 데이터를 포함하는 객체.
		request(Request): 요청 객체.

	Returns:
		JSONResponse: JSON 형식의 응답 객체.
					  HTTP 상태 코드와 ApiResponse(응답 상태코드, 사용자 메시지, 데이터)를 포함합니다.
				 	  반환 예시:
					  {
					   	"success": true,
						"code": 200,
						"message": "응답이 성공적으로 반환되었습니다.",
						"data": {
							"message": "안녕하세요! 한국장기조직기증원(KODA)입니다. 어떻게
							도와드릴까요?"
						}
					  }

	Raises:
		BadRequestError: 사용자 입력 유효성 검사 실패할 경우.
		InvalidTypeReturnError: 사용자 입력 유형 반환 시 미리 정의하지 않은 값으로 반환할 경우.
		JsonParsingError: LLM 응답 결과 역직렬화 실패할 경우.
		DocumentNotFoundError: 사용자 질의와 관련된 문서를 찾지 못할 경우.
	"""

	# 서버 로딩 때 저장한 값 가져오기
	retriever = request.app.state.retriever
	summarize_chain = request.app.state.summarize_chain
	chain = request.app.state.chain

	question = chat.question
	# 1. 입력 유효성 검사: 빈 문자열 여부 확인
	if not question or not question.strip():
		raise BadRequestError(details={'question': question})
	logger.info(f"[INPUT_CHECK_OK] 챗봇 입력 유효성 검사 성공: \"{question}\"")

	# 2. 입력 요약 및 유형 분류 (1차 LLM 호출)
	summarized_question = await summarize_chain.ainvoke({'question':
															 question})
	try:
		parsed_data = json.loads(summarized_question.content)
		summary = parsed_data['summary']
		question_type = parsed_data['type']
		logger.info(f"[INPUT_SUMMARIZED] 챗봇 입력 요약 결과: [{question_type}]"
					f" {summary}")

		if question_type != '일반':
			response = get_fixed_response_for(question_type)
			if not response:
				raise InvalidTypeReturnError({'type': question_type})
			return JSONResponse(
				status_code=HTTPStatus.OK,
				content=ApiResponse.ok({'message': response}).model_dump()
			)
	except:
		raise JsonParsingError(
			{'summarized_result': summarized_question.content})

	# 3. 요약된 질문으로 벡터 DB에서 관련 문서 검색
	context = await retriever.ainvoke(summary)
	if not context:
		raise DocumentNotFoundError()
	logger.info('[DB DOCS] 챗봇 문서 불러오기 성공')

	# 4. 최종 응답 생성 (2차 LLM 호출)
	result = await chain.ainvoke({'question': summary, 'context': context})
	return JSONResponse(
		status_code=HTTPStatus.OK,
		content=ApiResponse.ok(
			{"message": result.content}).model_dump()
	)


def get_fixed_response_for(question_type):
	""" 요약된 질문 유형에 따라 미리 정의한 답변을 반환합니다.

	Args:
		question_type(str): 요약된 질문 유형.

	Returns:
		str | None: 유형에 따라 미리 정의한 답변. 없으면 None.
	"""

	match question_type:
		case '인사말_시작':
			return ('안녕하세요, 한국장기조직기증원(KODA)입니다. 무엇을 도와드릴까요? 궁금한 점을 구체적으로 말씀해 '
					'주시면 해당 부서와 업무를 안내해 드리겠습니다!')
		case '인사말_종료':
			return '도움이 되셨길 바랍니다! 또 궁금한 점이 있으시면 언제든 편하게 물어봐 주세요. 감사합니다.'
		case '부정어/비속어':
			return '죄송합니다, 질문을 이해하지 못 했습니다. 다시 질문해 주세요.'
		case '업무 무관':
			return (
				'안녕하세요! 한국장기조직기증원(KODA)입니다. 문의해 주셔서 감사합니다. 현재 문의 주신 내용은 저희 '
				'챗봇이 답변할 수 있는 분야와 다소 거리가 있어 자세한 안내가 어렵습니다. 장기 기증과 관련된 '
				'일반적인 문의가 있으시다면, KODA 홈페이지를 방문하시거나, 대표 전화번호(02-3447-5632) 또는 '
				'채널톡, 카카오톡/인스타그램을 통해 문의해 주시면 친절하게 안내해 드리겠습니다. 감사합니다.')
		case _:
			return None
