package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.endpoints.CompanyEndpoints
import sttp.tapir.server.ServerEndpoint
import zio.{Task, ZIO}

import scala.collection.mutable

class CompanyController extends CompanyEndpoints {

  private val db: mutable.Map[Long, Company] = mutable.Map()

  val create: ServerEndpoint[Any, Task] = createEndpoint.serverLogicSuccess { req =>
    ZIO.succeed {
      val newId = db.keys.max + 1
      val slug  = ""
      val entity =
        Company(newId, slug, req.name, req.url, req.location, req.country, req.industry, req.image, req.tags)

      db += (newId -> entity)

      entity
    }
  }
}
