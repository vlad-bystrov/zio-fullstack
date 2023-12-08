package com.samples.reviewboard.http

import com.samples.reviewboard.http.controllers.{BaseController, CompanyController, HealthController}
import sttp.tapir.server.ServerEndpoint
import zio.{Task, UIO, ZIO}

object HttpApi {

  private def gatherRoutes(controllers: List[BaseController]): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers: UIO[List[BaseController]] = for {
    health    <- HealthController.makeZIO
    companies <- CompanyController.makeZIO
  } yield List(health, companies)

  val endpointsZIO: UIO[List[ServerEndpoint[Any, Task]]] = makeControllers.map(gatherRoutes)
}
