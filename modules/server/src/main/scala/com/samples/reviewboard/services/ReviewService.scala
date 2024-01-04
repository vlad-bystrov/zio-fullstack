package com.samples.reviewboard.services

import com.samples.reviewboard.domain.data.Review
import com.samples.reviewboard.http.requests.CreateReviewRequest
import com.samples.reviewboard.repositories.ReviewRepository
import zio.{Task, URLayer, ZIO, ZLayer}

import java.time.Instant

trait ReviewService {
  def create(reviewReq: CreateReviewRequest, userId: Long): Task[Review]
  def getById(id: Long): Task[Option[Review]]
  def findByCompanyId(companyId: Long): Task[List[Review]]
  def findByUserId(userId: Long): Task[List[Review]]
}

private class ReviewServiceLive(repo: ReviewRepository) extends ReviewService {

  override def create(reviewReq: CreateReviewRequest, userId: Long): Task[Review] = {
    val review = Review(
      id = -1L,
      companyId = reviewReq.companyId,
      userId = userId,
      management = reviewReq.management,
      culture = reviewReq.culture,
      salary = reviewReq.salary,
      benefits = reviewReq.benefits,
      wouldRecommend = reviewReq.wouldRecommend,
      review = reviewReq.review,
      created = Instant.now,
      updated = Instant.now
    )

    repo.create(review)
  }

  override def getById(id: Long): Task[Option[Review]] =
    repo.getById(id)

  override def findByCompanyId(companyId: Long): Task[List[Review]] =
    repo.findByCompanyId(companyId)

  override def findByUserId(userId: Long): Task[List[Review]] =
    repo.findByUserId(userId)
}

object ReviewServiceLive {
  val layer: URLayer[ReviewRepository, ReviewService] = ZLayer {
    ZIO.serviceWith[ReviewRepository](repo => new ReviewServiceLive(repo))
  }
}
