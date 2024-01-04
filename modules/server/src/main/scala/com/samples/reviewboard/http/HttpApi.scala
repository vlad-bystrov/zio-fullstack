package com.samples.reviewboard.http

import com.samples.reviewboard.http.controllers.{
  BaseController,
  CompanyController,
  HealthController,
  ReviewController
}
import com.samples.reviewboard.services.{CompanyService, ReviewService}
import sttp.tapir.server.ServerEndpoint
import zio.{RIO, Task}

object HttpApi {

  private type Env = CompanyService & ReviewService

  private def gatherRoutes(controllers: List[BaseController]): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers: RIO[Env, List[BaseController]] =
    for {
      health    <- HealthController.makeZIO
      companies <- CompanyController.makeZIO
      reviews   <- ReviewController.makeZIO
    } yield List(health, companies, reviews)

  val endpointsZIO: RIO[Env, List[ServerEndpoint[Any, Task]]] =
    makeControllers.map(gatherRoutes)
}
