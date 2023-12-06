package com.samples.reviewboard

import com.samples.reviewboard.http.controllers.HealthController
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.Server

object Application extends ZIOAppDefault {

  private val serverProgram = for {
    controller <- HealthController.makeZIO
    _ <- Server.serve(
      ZioHttpInterpreter().toHttp(List(controller.health))
    )
    _ <- Console.printLine("Server started...")
  } yield ()

  override def run: Task[Any] = serverProgram.provide(Server.default)
}
