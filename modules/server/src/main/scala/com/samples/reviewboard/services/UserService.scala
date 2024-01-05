package com.samples.reviewboard.services

import com.samples.reviewboard.domain.data.User
import com.samples.reviewboard.repositories.UserRepository
import zio.{Task, URLayer, ZIO, ZLayer}

trait UserService {
  def registerUser(email: String, password: String): Task[User]
  def verifyPassword(email: String, password: String): Task[Boolean]
}

private class UserServiceLive(repo: UserRepository) extends UserService {

  override def registerUser(email: String, password: String): Task[User] = ???

  override def verifyPassword(email: String, password: String): Task[Boolean] = ???
}

object UserServiceLive {
  val layer: URLayer[UserRepository, UserService] = ZLayer {
    ZIO.serviceWith[UserRepository](repo => new UserServiceLive(repo))
  }
}
