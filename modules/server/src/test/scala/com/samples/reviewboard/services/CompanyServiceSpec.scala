package com.samples.reviewboard.services

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.requests.CreateCompanyRequest
import com.samples.reviewboard.repositories.CompanyRepository
import com.samples.reviewboard.syntax.*
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.{Scope, Task, ZIO, ZLayer}

import scala.collection.mutable

object CompanyServiceSpec extends ZIOSpecDefault {

  private val service = ZIO.serviceWithZIO[CompanyService]

  private val stubRepoLayer = ZLayer.succeed(
    new CompanyRepository {
      private val db = mutable.Map[Long, Company]()

      override def create(company: Company): Task[Company] =
        ZIO.succeed {
          val newId  = db.keys.maxOption.getOrElse(0L) + 1
          val entity = company.copy(id = newId)
          db += (newId -> entity)
          entity
        }

      override def update(id: Long, op: Company => Company): Task[Company] =
        ZIO.attempt {
          val company = db(id)
          db += (id -> op(company))
          company
        }

      override def delete(id: Long): Task[Company] =
        ZIO.attempt {
          val company = db(id)
          db -= id
          company
        }

      override def getAll: Task[List[Company]] =
        ZIO.succeed(db.values.toList)

      override def getById(id: Long): Task[Option[Company]] =
        ZIO.succeed(db.get(id))
    }
  )

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("CompanyServiceTest")(
      test("create company") {
        val companyZIO = service(_.create(CreateCompanyRequest("Test Co", "testco.org")))

        companyZIO.assert("inspect result from 'create'") { company =>
          company.name == "Test Co" &&
          company.slug == "test-co" &&
          company.url == "testco.org"
        }
      },
      test("get company by id") {
        val program = for {
          company    <- service(_.create(CreateCompanyRequest("Test Co", "testco.org")))
          companyOpt <- service(_.getById(company.id))
        } yield (company, companyOpt)

        program.assert("inspect result from 'getById'") {
          case (company, Some(companyRes)) =>
            companyRes.name == company.name &&
            companyRes.slug == company.slug &&
            companyRes.url == company.url
          case _ => false
        }
      },
      test("get all companies") {
        val program = for {
          _ <- service(_.create(CreateCompanyRequest("Test Co 1", "testco1.org")))
          _ <- service(_.create(CreateCompanyRequest("Test Co 2", "testco2.org")))
          companies <- service(_.getAll)
        } yield companies
        
        program.assert("inspect result from 'getAll'") { companies =>
          companies.size == 2
        }
      }
    ).provide(CompanyServiceLive.layer, stubRepoLayer)
}
