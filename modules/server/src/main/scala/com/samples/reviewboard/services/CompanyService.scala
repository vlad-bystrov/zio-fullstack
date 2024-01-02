package com.samples.reviewboard.services

import com.samples.reviewboard.domain.data.Company
import com.samples.reviewboard.http.requests.CreateCompanyRequest
import com.samples.reviewboard.repositories.CompanyRepository
import zio.{Task, ULayer, URLayer, ZIO, ZLayer}

import scala.collection.mutable

trait CompanyService {
  def create(companyReq: CreateCompanyRequest): Task[Company]
  def getAll: Task[List[Company]]
  def getById(id: Long): Task[Option[Company]]
}

private class CompanyServiceLive(repo: CompanyRepository) extends CompanyService {

  override def create(companyReq: CreateCompanyRequest): Task[Company] =
    repo.create(companyReq.toCompany(-1L))

  override def getAll: Task[List[Company]] = repo.getAll

  override def getById(id: Long): Task[Option[Company]] = repo.getById(id)
}

object CompanyServiceLive {
  val layer: URLayer[CompanyRepository, CompanyService] = ZLayer {
    ZIO.serviceWith[CompanyRepository](repo => new CompanyServiceLive(repo))
  }
}
