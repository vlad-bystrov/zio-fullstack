package com.samples.reviewboard.http

import com.samples.reviewboard.http.controllers.{BaseController, CompanyController, HealthController}
import com.samples.reviewboard.services.CompanyService
import sttp.tapir.server.ServerEndpoint
import zio.{RIO, Task}

object HttpApi {

  private def gatherRoutes(controllers: List[BaseController]): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers: RIO[CompanyService, List[BaseController]] = for {
    health    <- HealthController.makeZIO
    companies <- CompanyController.makeZIO
  } yield List(health, companies)

  val endpointsZIO: RIO[CompanyService, List[ServerEndpoint[Any, Task]]] =
    makeControllers.map(gatherRoutes)
}
