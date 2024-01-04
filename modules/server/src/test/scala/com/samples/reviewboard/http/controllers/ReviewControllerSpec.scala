package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.domain.data.Review
import com.samples.reviewboard.http.requests.CreateReviewRequest
import com.samples.reviewboard.services.ReviewService
import com.samples.reviewboard.syntax.*
import sttp.client3.*
import sttp.client3.testing.SttpBackendStub
import sttp.monad.MonadError
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.RIOMonadError
import zio.*
import zio.json.*
import zio.test.*

import java.time.Instant

object ReviewControllerSpec extends ZIOSpecDefault {

  private given zioME: MonadError[Task] = RIOMonadError[Any]

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

  private val stubService = new ReviewService {

    override def create(reviewReq: CreateReviewRequest, userId: Long): Task[Review] =
      ZIO.succeed(goodReview)

    override def getById(id: Long): Task[Option[Review]] =
      ZIO.succeed {
        if (id == 1L) Some(goodReview)
        else None
      }

    override def findByCompanyId(companyId: Long): Task[List[Review]] =
      ZIO.succeed {
        if (companyId == 1L) List(goodReview)
        else List()
      }

    override def findByUserId(userId: Long): Task[List[Review]] =
      ZIO.succeed {
        if (userId == 1L) List(goodReview)
        else List()
      }
  }

  private def backendStubZIO(endpointFun: ReviewController => ServerEndpoint[Any, Task]) = for {
    controller <- ReviewController.makeZIO
    backendStub <- ZIO.succeed(
      TapirStubInterpreter(SttpBackendStub(MonadError[Task]))
        .whenServerEndpointRunLogic(endpointFun(controller))
        .backend()
    )
  } yield backendStub

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ReviewControllerTest")(
      test("create review") {
        val createReq = CreateReviewRequest(
          companyId = 1L,
          management = 5,
          culture = 5,
          salary = 5,
          benefits = 5,
          wouldRecommend = 10,
          review = "Good company to work"
        )

        val program = for {
          backendStub <- backendStubZIO(_.create)
          response <- basicRequest
            .post(uri"/reviews")
            .body(createReq.toJson)
            .send(backendStub)
        } yield response.body

        program.assert("inspect http response from 'create'") { respBody =>
          respBody.toOption
            .flatMap(_.fromJson[Review].toOption)
            .contains(goodReview)
        }
      }
    ).provide(ZLayer.succeed(stubService))
}
