package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.http.endpoints.ReviewEndpoints
import com.samples.reviewboard.services.ReviewService
import sttp.tapir.server.ServerEndpoint
import zio.{RIO, Task, ZIO}

private class ReviewController(service: ReviewService) extends BaseController with ReviewEndpoints {

  val create: ServerEndpoint[Any, Task] =
    createEndpoint.serverLogicSuccess { req =>
      service.create(req, -1L)
    }

  val getById: ServerEndpoint[Any, Task] =
    getByIdEndpoint.serverLogicSuccess { id =>
      service.getById(id)
    }

  val getByCompanyId: ServerEndpoint[Any, Task] =
    getByCompanyIdEndpoint.serverLogicSuccess { companyId =>
      service.findByCompanyId(companyId)
    }

  override val routes: List[ServerEndpoint[Any, Task]] = List(create, getById, getByCompanyId)
}

object ReviewController {
  val makeZIO: RIO[ReviewService, ReviewController] =
    for {
      service <- ZIO.service[ReviewService]
    } yield new ReviewController(service)
}
