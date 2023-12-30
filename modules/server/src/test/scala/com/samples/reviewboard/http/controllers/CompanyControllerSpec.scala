package com.samples.reviewboard.http.controllers

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.requests.CreateCompanyRequest
import com.samples.reviewboard.syntax.*
import sttp.client3.*
import sttp.client3.testing.SttpBackendStub
import sttp.monad.MonadError
import sttp.tapir.generic.auto.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.RIOMonadError
import zio.*
import zio.json.*
import zio.test.*

object CompanyControllerSpec extends ZIOSpecDefault {

  private given zioME: MonadError[Task] = RIOMonadError[Any]

  private def backendStubZIO(endpointFun: CompanyController => ServerEndpoint[Any, Task]) = for {
    controller <- CompanyController.makeZIO
    backendStub <- ZIO.succeed(
      TapirStubInterpreter(SttpBackendStub(MonadError[Task]))
        .whenServerEndpointRunLogic(endpointFun(controller))
        .backend()
    )
  } yield backendStub

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("CompanyControllerSpec")(
      test("create company") {
        val program = for {
          backendStub <- backendStubZIO(_.create)
          response <- basicRequest
            .post(uri"/companies")
            .body(CreateCompanyRequest("Sample Job", "sample.com").toJson)
            .send(backendStub)
        } yield response.body

        program.assert("inspect http response from 'create'") { respBody =>
          respBody.toOption
            .flatMap(_.fromJson[Company].toOption)
            .contains(Company(1L, "sample-job", "Sample Job", "sample.com"))
        }
      },
      test("get all companies") {
        val program = for {
          backendStub <- backendStubZIO(_.getAll)
          response <- basicRequest
            .get(uri"/companies")
            .send(backendStub)
        } yield response.body

        program.assert("inspect http response from 'getAll'") { respBody =>
          respBody.toOption
            .flatMap(_.fromJson[List[Company]].toOption)
            .contains(List())
        }
      },
      test("get company by Id") {
        val program = for {
          backendStub <- backendStubZIO(_.getById)
          response <- basicRequest
            .get(uri"/companies/1")
            .send(backendStub)
        } yield response.body

        program.assert("inspect http response from 'getById'") { respBody =>
          respBody.toOption
            .flatMap(_.fromJson[Company].toOption)
            .isEmpty
        }
      }
    )
}
