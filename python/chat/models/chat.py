from pydantic import BaseModel


class ChatInput(BaseModel):
	""" 사용자 입력과 관련된 데이터를 갖습니다.

	Attributes:
		question(str): 사용자 질의 메시지.
	"""

	question: str  # 키 값 일치