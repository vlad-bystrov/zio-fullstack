package com.samples.reviewboard.domain.data

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.Instant

case class Review(
    id: Long,
    companyId: Long,
    userId: Long,
    management: Int,
    culture: Int,
    salary: Int,
    benefits: Int,
    wouldRecommend: Int,
    review: String,
    created: Instant,
    updated: Instant
)

object Review {
  given codec: JsonCodec[Review] = DeriveJsonCodec.gen[Review]
}
