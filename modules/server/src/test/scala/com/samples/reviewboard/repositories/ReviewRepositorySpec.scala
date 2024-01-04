package com.samples.reviewboard.repositories

import com.samples.reviewboard.domain.data.Review
import com.samples.reviewboard.syntax.*
import zio.{Scope, ZIO}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

import java.time.Instant

object ReviewRepositorySpec extends ZIOSpecDefault with RepositorySpec {

  private val goodReview = Review(
    id = 1L,
    companyId = 1L,
    userId = 1L,
    management = 5,
    culture = 5,
    salary = 5,
    benefits = 5,
    wouldRecommend = 10,
    review = "Good company to work",
    created = Instant.now,
    updated = Instant.now
  )

  override protected val initScript: String = "sql/reviews.sql"

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ReviewRepositoryTest")(
      test("create review") {
        val program = for {
          repo   <- ZIO.service[ReviewRepository]
          review <- repo.create(goodReview)
        } yield review

        program.assert("inspect result from 'create'") { review =>
          review.management == goodReview.management &&
          review.culture == goodReview.culture &&
          review.salary == goodReview.salary &&
          review.benefits == goodReview.benefits &&
          review.wouldRecommend == goodReview.wouldRecommend &&
          review.review == goodReview.review
        }
      },
      test("get review by ids (id, companyId, userId)") {
        val program = for {
          repo               <- ZIO.service[ReviewRepository]
          review             <- repo.create(goodReview)
          fetchedById        <- repo.getById(review.id)
          fetchedByCompanyId <- repo.findByCompanyId(review.companyId)
          fetchedByUserId    <- repo.findByUserId(review.userId)
        } yield (review, fetchedById, fetchedByCompanyId, fetchedByUserId)

        program.assert("inspect result from 'get/find by id'") {
          case (review, fetchedById, fetchedByCompanyId, fetchedByUserId) =>
            fetchedById.contains(review) &&
            fetchedByCompanyId.contains(review) &&
            fetchedByUserId.contains(review)
        }
      },
      test("update review") {
        val program = for {
          repo    <- ZIO.service[ReviewRepository]
          review  <- repo.create(goodReview)
          updated <- repo.update(review.id, _.copy(review = "Not bad company"))
          fetched <- repo.getById(review.id)
        } yield (updated, fetched)

        program.assert("inspect result from 'update'") { case (updated, fetched) =>
          fetched.contains(updated)
        }
      },
      test("delete review") {
        val program = for {
          repo    <- ZIO.service[ReviewRepository]
          review  <- repo.create(goodReview)
          _       <- repo.delete(review.id)
          fetched <- repo.getById(review.id)
        } yield fetched

        program.assert("inspect result from 'delete'")(_.isEmpty)
      }
    ).provide(
      ReviewRepositoryLive.layer,
      Repository.quillLayer,
      dataSourceLayer,
      Scope.default
    )
}
