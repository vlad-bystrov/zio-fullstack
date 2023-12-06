package com.samples.reviewboard.http.endpoints

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.requests.CreateCompanyRequest
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.jsonBody

trait CompanyEndpoints {

  val createEndpoint: PublicEndpoint[CreateCompanyRequest, Unit, Company, Any] =
    endpoint
      .tag("companies")
      .name("create")
      .description("create a listing for a company")
      .post
      .in("companies")
      .in(jsonBody[CreateCompanyRequest])
      .out(jsonBody[Company])

  val getAllEndpoint: PublicEndpoint[Unit, Unit, List[Company], Any] =
    endpoint
      .tag("companies")
      .name("getAll")
      .description("get all company listings")
      .get
      .in("companies")
      .out(jsonBody[List[Company]])

  val getByIdEndpoint: PublicEndpoint[String, Unit, Option[Company], Any] =
    endpoint
      .tag("companies")
      .name("getById")
      .description("get company by id or slug") // todo: later
      .get
      .in("companies" / path[String]("id"))
      .out(jsonBody[Option[Company]])
}
