package com.samples.reviewboard.http.endpoints

import sttp.tapir.*

trait HealthEndpoint {
  val healthEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint
      .tag("health")
      .name("health")
      .description("health check")
      .get
      .in("health")
      .out(plainBody[String])
}
