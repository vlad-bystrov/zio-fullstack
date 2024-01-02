package com.samples.reviewboard

import com.samples.reviewboard.http.HttpApi
import com.samples.reviewboard.repositories.{CompanyRepositoryLive, Repository}
import com.samples.reviewboard.services.{CompanyService, CompanyServiceLive}
import io.getquill.SnakeCase
import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.http.Server

object Application extends ZIOAppDefault {

  private val serverProgram = for {
    endpoints <- HttpApi.endpointsZIO
    _ <- Server.serve(
      ZioHttpInterpreter().toHttp(endpoints)
    )
    _ <- Console.printLine("Server started...")
  } yield ()

  override def run: Task[Any] =
    serverProgram.provide(
      Server.default,
      CompanyServiceLive.layer,
      CompanyRepositoryLive.layer,
      Repository.dataLayer
    )
}
