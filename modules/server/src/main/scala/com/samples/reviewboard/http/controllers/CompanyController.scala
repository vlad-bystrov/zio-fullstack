package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.http.endpoints.CompanyEndpoints
import com.samples.reviewboard.services.CompanyService
import sttp.tapir.server.ServerEndpoint
import zio.{RIO, Task, ZIO}

private class CompanyController(service: CompanyService) extends BaseController with CompanyEndpoints {

  val create: ServerEndpoint[Any, Task] =
    createEndpoint.serverLogicSuccess { req =>
      service.create(req)
    }

  val getAll: ServerEndpoint[Any, Task] =
    getAllEndpoint.serverLogicSuccess(_ => service.getAll)

  val getById: ServerEndpoint[Any, Task] =
    getByIdEndpoint.serverLogicSuccess { id =>
      ZIO.attempt(id.toLong).flatMap(idLong => service.getById(idLong))
    }

  override val routes: List[ServerEndpoint[Any, Task]] = List(create, getAll, getById)
}

object CompanyController {
  val makeZIO: RIO[CompanyService, CompanyController] =
    for {
      service <- ZIO.service[CompanyService]
    } yield new CompanyController(service)
}
