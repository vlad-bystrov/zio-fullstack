package com.samples.reviewboard.http.endpoints

import sttp.tapir.*

trait HealthEndpoints extends BaseEndpoints {
  val healthEndpoint: PublicEndpoint[Unit, Throwable, String, Any] =
    baseEndpoint
      .tag("health")
      .name("health")
      .description("health check")
      .get
      .in("health")
      .out(plainBody[String])

  val errorEndpoint: PublicEndpoint[Unit, Throwable, String, Any] =
    baseEndpoint
      .tag("health")
      .name("error health")
      .description("helth should fail")
      .get
      .in("health" / "error")
      .out(plainBody[String])
}
