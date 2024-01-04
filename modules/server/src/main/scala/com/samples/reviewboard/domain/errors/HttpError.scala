package com.samples.reviewboard.domain.errors

import sttp.model.StatusCode

case class HttpError(statusCode: StatusCode, message: String, cause: Throwable)
    extends RuntimeException(message, cause)

object HttpError {
  def decode(tuple: (StatusCode, String)): HttpError =
    HttpError(tuple(0), tuple(1), new RuntimeException(tuple(1)))

  def encode(error: Throwable): (StatusCode, String) =
    (StatusCode.InternalServerError, error.getMessage)
}
