from typing import Optional, Dict, Any

from pydantic import BaseModel


class ApiResponse(BaseModel):
	""" 챗봇 응답에 사용할 데이터를 가집니다.

	Attributes:
		success(bool): 오류가 발생하지 않으면 True, 발생하면 False.
		code(int): 상태 코드.
		message(str): 성공 시 응답 결과 메시지 및 실패 시 사용자에게 표시할 오류 메시지.
		data(Optional[Dict[str, Any]]): 챗봇 응답 메시지.
	"""

	success: bool
	code: int
	message: str
	data: Optional[Dict[str, Any]] = None

	# 성공적으로 반환했을 경우: 데이터 지정
	@classmethod
	def ok(cls, data: Dict[str, Any]):
		return cls(success=True, code=200, message="응답이 성공적으로 반환되었습니다.",
				   data=data)

	# 오류가 발생한 경우: 상태 코드, 메시지 지정
	@classmethod
	def error(cls, code: int, message: str):
		return cls(success=False, code=code, message=message)

	# 그 외 발생한 오류 (기본 500, 기본 메시지)
	@classmethod
	def internal_server_error(cls):
		return cls(success=False, code=500, message="서버에 일시적인 문제가 발생했습니다.")
