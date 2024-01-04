package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.http.endpoints.ReviewEndpoints
import com.samples.reviewboard.services.ReviewService
import sttp.tapir.server.ServerEndpoint
import zio.{RIO, Task, ZIO}

private class ReviewController(service: ReviewService) extends BaseController with ReviewEndpoints {

  val create: ServerEndpoint[Any, Task] =
    createEndpoint.serverLogic[Task] { req =>
      service.create(req, -1L).either
    }

  val getById: ServerEndpoint[Any, Task] =
    getByIdEndpoint.serverLogic[Task] { id =>
      service.getById(id).either
    }

  val getByCompanyId: ServerEndpoint[Any, Task] =
    getByCompanyIdEndpoint.serverLogic[Task] { companyId =>
      service.findByCompanyId(companyId).either
    }

  override val routes: List[ServerEndpoint[Any, Task]] = List(create, getById, getByCompanyId)
}

object ReviewController {
  val makeZIO: RIO[ReviewService, ReviewController] =
    for {
      service <- ZIO.service[ReviewService]
    } yield new ReviewController(service)
}
