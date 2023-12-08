package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.endpoints.CompanyEndpoints
import sttp.tapir.server.ServerEndpoint
import zio.{Task, UIO, ZIO}

import scala.collection.mutable

class CompanyController private extends BaseController with CompanyEndpoints {

  private val db: mutable.Map[Long, Company] = mutable.Map(
    -1L -> Company(-1L, "", "", "", None, None, None, None, List())
  )

  val create: ServerEndpoint[Any, Task] = createEndpoint.serverLogicSuccess { req =>
    ZIO.succeed {
      val newId  = db.keys.max + 1
      val entity = req.toCompany(newId)
      db += (newId -> entity)
      entity
    }
  }

  val getAll: ServerEndpoint[Any, Task] =
    getAllEndpoint.serverLogicSuccess(_ => ZIO.succeed(db.values.toList))

  val getById: ServerEndpoint[Any, Task] = getByIdEndpoint.serverLogicSuccess { id =>
    ZIO.attempt(id.toLong).map(db.get)
  }

  override val routes: List[ServerEndpoint[Any, Task]] = List(create, getAll, getById)
}

object CompanyController {
  val makeZIO: UIO[CompanyController] = ZIO.succeed(new CompanyController)
}
