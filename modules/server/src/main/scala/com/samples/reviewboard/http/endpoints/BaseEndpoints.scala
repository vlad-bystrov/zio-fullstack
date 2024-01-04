package com.samples.reviewboard.http.endpoints

import com.samples.reviewboard.domain.errors.HttpError
import sttp.tapir.*

trait BaseEndpoints {

  val baseEndpoint: PublicEndpoint[Unit, Throwable, Unit, Any] =
    endpoint
      .errorOut(statusCode and plainBody[String])
      .mapErrorOut[Throwable](HttpError.decode)(HttpError.encode)
}
