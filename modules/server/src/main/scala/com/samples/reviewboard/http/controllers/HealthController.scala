package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.http.endpoints.HealthEndpoints
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.*
import zio.{Task, UIO, ZIO}

private class HealthController private extends BaseController with HealthEndpoints {

  val health: ServerEndpoint[Any, Task] =
    healthEndpoint.serverLogicSuccess(_ => ZIO.succeed("All Good!"))

  val error: ServerEndpoint[Any, Task] =
    errorEndpoint.serverLogic[Task](_ => ZIO.fail(new RuntimeException("Error!")).either)

  override val routes: List[ServerEndpoint[Any, Task]] = List(health, error)
}

object HealthController {
  val makeZIO: UIO[HealthController] = ZIO.succeed(new HealthController)
}
