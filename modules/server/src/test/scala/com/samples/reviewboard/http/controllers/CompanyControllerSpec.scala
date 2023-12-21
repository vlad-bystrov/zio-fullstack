package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.requests.CreateCompanyRequest
import sttp.client3.*
import sttp.client3.testing.SttpBackendStub
import sttp.monad.MonadError
import sttp.tapir.generic.auto.*
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.RIOMonadError
import zio.*
import zio.json.*
import zio.test.*

object CompanyControllerSpec extends ZIOSpecDefault {

  private given zioME: MonadError[Task] = RIOMonadError[Any]

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("CompanyControllerSpec") {
      test("create company") {
        val program = for {
          controller <- CompanyController.makeZIO
          backendStub <- ZIO.succeed(
            TapirStubInterpreter(SttpBackendStub(MonadError[Task]))
              .whenServerEndpointRunLogic(controller.create)
              .backend()
          )
          response <- basicRequest
            .post(uri"/companies")
            .body(CreateCompanyRequest("Sample Job", "sample.com").toJson)
            .send(backendStub)
        } yield response.body

        assertZIO(program) {
          Assertion.assertion("inspect http resonse from 'create'") { respBody =>
            respBody.toOption
              .flatMap(_.fromJson[Company].toOption)
              .contains(Company(1L, "sample-job", "Sample Job", "sample.com"))
          }
        }
      }
    }
}
