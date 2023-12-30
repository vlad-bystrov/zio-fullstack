package com.samples.reviewboard.syntax

import zio.*
import zio.test.*

extension [R, E, A](zio: ZIO[R, E, A]) {
  def assertA(assertion: Assertion[A]): ZIO[R, E, TestResult] = assertZIO(zio)(assertion)

  def assert(name: String)(predicate: (=> A) => Boolean): ZIO[R, E, TestResult] =
    assertA(Assertion.assertion(name)(predicate))
}
