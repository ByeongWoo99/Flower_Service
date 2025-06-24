import logging
import os
from contextlib import asynccontextmanager

from asyncmy import create_pool
from dotenv import load_dotenv
from fastapi import FastAPI, Request
from starlette.responses import JSONResponse

from .api.reply import router
from .core.exceptions import GeneralError, CustomBaseError, DBConnectionError
from .core.services import prepare_chat_data
from .schemas.response import ApiResponse

logging.basicConfig(
	format='%(asctime)s %(levelname)s %(name)s %(message)s',
	datefmt='%Y-%m-%d %H:%M:%S',
	level=logging.INFO)

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
	# Startup tasks
	logger.info('[chatbot] FastAPI 챗봇 애플리케이션 실행')

	# .env 파일을 읽어 환경 변수로 로드
	load_dotenv()

	app.state.pool = await create_db_pool()
	await prepare_chat_data(app)

	yield

	# Shutdown tasks
	app.state.pool.close()
	await app.state.pool.wait_closed()
	logger.info('[DB POOL] 풀 내의 모든 DB 연결 종료')
	logger.info('[chatbot] FastAPI 챗봇 애플리케이션 종료')


app = FastAPI(lifespan=lifespan)
app.include_router(router)


async def create_db_pool():
	""" MySQL DB 풀을 생성합니다.

	Returns:
		Pool: DB 풀 객체.

	Raises:
		DBConnectionError: DB 풀 생성에 실패할 경우.
		GeneralError: MySQL 관련 오류 외의 예상치 못한 일반적인 오류가 발생할 경우.
					  (예: 환경 변수 설정 오류 등)

	"""
	# MySQL DB 연결 및 예외 처리
	try:
		pool = await create_pool(
			host=os.getenv('DB_HOST'),
			port=int(os.getenv('DB_PORT')),
			user=os.getenv('DB_USER'),
			password=os.getenv('DB_PASSWORD'),
			database=os.getenv('DB_DATABASE'),
			autocommit=True,  # 현재 SELECT 문만 실행하기 때문에 설정
			maxsize=10
		)
	except Exception as e:
		raise DBConnectionError(details={'reason': 'DB 풀 생성 실패',
										 'error': str(e)})
	logger.info("[DB POOL] DB 풀 생성 완료")
	return pool


@app.exception_handler(CustomBaseError)
async def custom_base_error_handler(request: Request, exc: CustomBaseError):
	""" 사용자 정의 예외를 처리하는 전역 예외 처리기를 정의합니다.

	Args:
		request(Request): 사용자 요청 객체.
		exc(CustomBaseError): 발생한 예외 객체.

	Returns:
		JSONResponse: JSON 형식의 응답 객체.
					  HTTP 상태 코드와 ApiResponse(응답 상태코드, 사용자 메시지)를 포함합니다.
					  반환 예시:
					  {
						"success": false,
						"code": 500,
						"message": "DB 서버 연결 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
						"data": null
					  }
	"""

	logger.error(
		f"[{request.method} {request.url.path}] {exc.developer_message}: "
		f"{exc.details}")
	return JSONResponse(
		status_code=exc.code,
		content=ApiResponse.error(exc.code, exc.user_message).model_dump()
	)


@app.exception_handler(GeneralError)
async def internal_server_error_handler(request: Request, exc: GeneralError):
	""" 사용자 정의 예외를 처리하지 못한 그 외 모든 예외를 처리합니다.

	Args:
		request(Request): 사용자 요청 객체.
		exc(GeneralError): 발생한 예외 객체.

	Returns:
		JSONResponse: JSON 형식의 응답 객체.
					  HTTP 상태 코드와 ApiResponse(응답 상태코드, 사용자 메시지)를 포함합니다.
					  반환 예시:
					  {
						"success": false,
						"code": 500,
						"message": "죄송합니다. 서버에 일시적인 문제가 발생했습니다.",
						"data": null
					  }
	"""

	logger.error(f"[{request.method} {request.url.path}] {str(exc)}",
				 exc_info=exc)
	return JSONResponse(
		status_code=exc.code,
		content=ApiResponse.internal_server_error().model_dump()
	)
