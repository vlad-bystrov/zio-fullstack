package com.samples.reviewboard.repositories

import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import zio.{RLayer, Scope, TaskLayer, ZIO, ZLayer}

import javax.sql.DataSource

trait RepositorySpec {

  private def createContainer(): PostgreSQLContainer[Nothing] = {
    val container: PostgreSQLContainer[Nothing] =
      PostgreSQLContainer("postgres").withInitScript("sql/companies.sql")

    container.start()
    container
  }

  private def createDataSource(container: PostgreSQLContainer[Nothing]): DataSource = {
    val dataSource = PGSimpleDataSource()
    dataSource.setUrl(container.getJdbcUrl)
    dataSource.setUser(container.getUsername)
    dataSource.setPassword(container.getPassword)
    dataSource
  }

  val dataSourceLayer: RLayer[Scope, DataSource] = ZLayer {
    for {
      container <-
        ZIO.acquireRelease(ZIO.attempt(createContainer()))(container =>
          ZIO.attempt(container.stop()).ignoreLogged
        )
      dataSource <- ZIO.attempt(createDataSource(container))
    } yield dataSource
  }
}
