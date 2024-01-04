package com.samples.reviewboard.repositories

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.syntax.*
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.{Scope, ZIO, ZLayer}

import java.sql.SQLException
import javax.sql.DataSource
import scala.util.Random

object CompanyRepositorySpec extends ZIOSpecDefault with RepositorySpec {

  private val testCompany = Company(1L, "test-co", "Test Co", "testco.org")

  override protected val initScript: String = "sql/companies.sql"

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("CompanyRepositoryTest")(
      test("create company") {
        val program = for {
          repo    <- ZIO.service[CompanyRepository]
          company <- repo.create(testCompany)
        } yield company

        program.assert("inspect result from 'create'") { company =>
          company.name == "Test Co" &&
          company.slug == "test-co" &&
          company.url == "testco.org"
        }
      },
      test("create a duplicate of a company should fail") {
        val program = for {
          repo  <- ZIO.service[CompanyRepository]
          _     <- repo.create(testCompany)
          error <- repo.create(testCompany).flip
        } yield error

        program.assert("inspect error result from 'create'")(_.isInstanceOf[SQLException])
      },
      test("get company by id") {
        val program = for {
          repo        <- ZIO.service[CompanyRepository]
          company     <- repo.create(testCompany)
          fetchedById <- repo.getById(company.id)
        } yield (company, fetchedById)

        program.assert("inspect result from 'getById'") { case (company, fetchedById) =>
          fetchedById.contains(company)
        }
      },
      test("update company") {
        val program = for {
          repo        <- ZIO.service[CompanyRepository]
          company     <- repo.create(testCompany)
          updated     <- repo.update(company.id, _.copy(url = "blog.testco.org"))
          fetchedById <- repo.getById(company.id)
        } yield (updated, fetchedById)

        program.assert("inspect result from 'update'") { case (updated, fetchedById) =>
          fetchedById.contains(updated)
        }
      },
      test("delete company") {
        val program = for {
          repo        <- ZIO.service[CompanyRepository]
          company     <- repo.create(testCompany)
          _           <- repo.delete(company.id)
          fetchedById <- repo.getById(company.id)
        } yield fetchedById

        program.assert("inspect result from 'delete'")(_.isEmpty)
      },
      test("get all companies") {
        val program = for {
          repo      <- ZIO.service[CompanyRepository]
          companies <- ZIO.foreach(1 to 10)(_ => repo.create(genCompany))
          fetched   <- repo.getAll
        } yield (companies, fetched)

        program.assert("inspect result from 'getAll'") { case (companies, fetched) =>
          fetched.toSet == companies.toSet
        }
      }
    ).provide(
      CompanyRepositoryLive.layer,
      Repository.quillLayer,
      dataSourceLayer,
      Scope.default
    )

  // generators
  private def genCompany: Company =
    Company(-1L, genString, genString, genString)

  private def genString: String =
    Random.alphanumeric.take(8).mkString
}
