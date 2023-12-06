package com.samples

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

object QuillDemo extends ZIOAppDefault {

  val program: RIO[JobRepository, Unit] = for {
    repo <- ZIO.service[JobRepository]
    _    <- repo.create(Job(-1L, "Developer", "whisk.com", "Whisk"))
    _    <- repo.create(Job(-1L, "Manager", "samsung.com", "Samsung"))
  } yield ()

  override def run: Task[Unit] =
    program.provide(
      JobRepositoryLive.layer,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      Quill.DataSource.fromPrefix("mydbconf")
    )

}

trait JobRepository {
  def create(job: Job): Task[Job]
  def update(id: Long, op: Job => Job): Task[Job]
  def delete(id: Long): Task[Job]
  def getById(id: Long): Task[Option[Job]]
  def get: Task[List[Job]]
}

class JobRepositoryLive(quill: Quill.Postgres[SnakeCase]) extends JobRepository {
  // step 1
  import quill.*

  // step 2 - define schema
  inline given schema: SchemaMeta[Job]  = schemaMeta[Job]("jobs")
  inline given insMeta: InsertMeta[Job] = insertMeta[Job](_.id)
  inline given upMeta: UpdateMeta[Job]  = updateMeta[Job](_.id)

  override def create(job: Job): Task[Job] =
    run {
      query[Job]
        .insertValue(lift(job))
        .returning(j => j)
    }

  override def update(id: Long, op: Job => Job): Task[Job] = for {
    job <- getById(id).someOrFail(new RuntimeException(s"Can not update: missing key id = $id"))
    result <- run {
      query[Job]
        .filter(_.id == lift(id))
        .updateValue(lift(op(job)))
        .returning(j => j)
    }
  } yield result

  override def delete(id: Long): Task[Job] =
    run {
      query[Job]
        .filter(_.id == lift(id))
        .delete
        .returning(j => j)
    }

  override def getById(id: Long): Task[Option[Job]] =
    run {
      query[Job]
        .filter(_.id == lift(id))
    }.map(_.headOption)

  override def get: Task[List[Job]] =
    run(query[Job])
}

object JobRepositoryLive {
  val layer: URLayer[Quill.Postgres[SnakeCase], JobRepositoryLive] = ZLayer {
    ZIO.service[Quill.Postgres[SnakeCase]].map(quill => JobRepositoryLive(quill))
  }
}
