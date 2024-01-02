package com.samples.reviewboard.repositories

import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{TaskLayer, URLayer, ZLayer}

import javax.sql.DataSource

object Repository {

  val quillLayer: URLayer[DataSource, Quill.Postgres[SnakeCase.type]] =
    Quill.Postgres.fromNamingStrategy(SnakeCase)

  val dataSourceLayer: TaskLayer[DataSource] = Quill.DataSource.fromPrefix("reviewboard.db")

  val dataLayer: TaskLayer[Quill.Postgres[SnakeCase.type]] = dataSourceLayer >>> quillLayer
}
