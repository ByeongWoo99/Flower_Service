package com.ttasum.memorial.exception.recipientLetter;

import com.ttasum.memorial.exception.common.badRequest.BadRequestException;

public class RecipientOrganNameEmptyException extends BadRequestException {
  public RecipientOrganNameEmptyException() {
    super("장기명을 반드시 입력해야 합니다.");
  }
}
