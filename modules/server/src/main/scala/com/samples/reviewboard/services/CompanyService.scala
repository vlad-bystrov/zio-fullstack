package com.samples.reviewboard.services

import com.samples.reviewboard.domain.data.Company

import scala.collection.mutable

trait CompanyService {

}

class CompanyServiceStub {
  private val db: mutable.Map[Long, Company] = mutable.Map()
}