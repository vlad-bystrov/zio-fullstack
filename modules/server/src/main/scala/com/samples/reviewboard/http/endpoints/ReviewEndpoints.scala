package com.samples.reviewboard.http.endpoints

import com.samples.reviewboard.domain.data.Review
import com.samples.reviewboard.http.requests.CreateReviewRequest
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.jsonBody

trait ReviewEndpoints {

  val createEndpoint: PublicEndpoint[CreateReviewRequest, Unit, Review, Any] =
    endpoint
      .tag("reviews")
      .name("create")
      .description("create a review for a company")
      .post
      .in("reviews")
      .in(jsonBody[CreateReviewRequest])
      .out(jsonBody[Review])

  val getByIdEndpoint: PublicEndpoint[Long, Unit, Option[Review], Any] =
    endpoint
      .tag("reviews")
      .name("getById")
      .description("get review by id")
      .get
      .in("reviews" / path[Long]("id"))
      .out(jsonBody[Option[Review]])

  val getByCompanyIdEndpoint: PublicEndpoint[Long, Unit, List[Review], Any] =
    endpoint
      .tag("reviews")
      .name("getByCompanyId")
      .description("get reviews by companyId")
      .get
      .in("reviews" / "company" / path[Long]("id"))
      .out(jsonBody[List[Review]])
}
