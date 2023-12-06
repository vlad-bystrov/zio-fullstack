package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.http.endpoints.HealthEndpoint
import sttp.tapir.server.ServerEndpoint
import zio.{Task, UIO, ZIO}

class HealthController private extends HealthEndpoint {

  val health: ServerEndpoint[Any, Task] =
    healthEndpoint.serverLogicSuccess(_ => ZIO.succeed("All Good!"))
}

object HealthController {
  val makeZIO: UIO[HealthController] = ZIO.succeed(new HealthController)
}
