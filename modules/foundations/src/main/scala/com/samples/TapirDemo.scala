package com.samples

import sttp.tapir.PublicEndpoint
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.*
import zio.http.{HttpApp, Server}
import zio.json.{DeriveJsonCodec, JsonCodec}

import scala.collection.mutable

object TapirDemo extends ZIOAppDefault {

  val simplestEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint
      .tag("simple")
      .name("simple")
      .description("simple endpoint")
      // ^^ for documentation
      .get                    // http method
      .in("simple")           // path
      .out(plainBody[String]) // output

  val app1: HttpApp[Any] =
    ZioHttpInterpreter(ZioHttpServerOptions.default).toHttp(
      simplestEndpoint.zServerLogic[Any](_ => ZIO.succeed("Hello, There!"))
    )

  // simulate job board
  val db: mutable.Map[Long, Job] = mutable.Map(
    1L -> Job(1L, "Dev", "test.com", "Test Co")
  )

  // create
  val createJobEndpoint: ServerEndpoint[Any, Task] =
    endpoint
      .tag("jobs")
      .name("create")
      .description("create job")
      .post
      .in("jobs")
      .in(jsonBody[CreateJobRequest])
      .out(jsonBody[Job])
      .serverLogicSuccess { req =>
        val newId  = db.keys.max + 1
        val newJob = Job(newId, req.title, req.url, req.company)
        db += (newId -> newJob)
        ZIO.succeed(newJob)
      }

  // get by id
  val getJobByIdEndpoint: ServerEndpoint[Any, Task] =
    endpoint
      .tag("jobs")
      .name("getById")
      .description("get job by id")
      .get
      .in("jobs" / path[Long]("id"))
      .out(jsonBody[Option[Job]])
      .serverLogicSuccess(id => ZIO.succeed(db.get(id)))

  // get all
  val getAllEndpoint: ServerEndpoint[Any, Task] =
    endpoint
      .tag("jobs")
      .name("getAll")
      .description("get all jobs")
      .get
      .in("jobs")
      .out(jsonBody[List[Job]])
      .serverLogicSuccess(_ => ZIO.succeed(db.values.toList))

  val app: HttpApp[Any] =
    ZioHttpInterpreter().toHttp(List(getAllEndpoint, createJobEndpoint, getJobByIdEndpoint))

  override def run: ZIO[Any, Throwable, Nothing] =
    Server.serve(app).provide(Server.default)
}

case class Job(id: Long, title: String, url: String, company: String)

object Job {
  given codec: JsonCodec[Job] = DeriveJsonCodec.gen[Job]
}

case class CreateJobRequest(title: String, url: String, company: String)

object CreateJobRequest {
  given codec: JsonCodec[CreateJobRequest] = DeriveJsonCodec.gen[CreateJobRequest]
}
