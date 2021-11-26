package com.example

import cats.data.EitherT
import cats.effect.IO
import com.example.http4sbookcrud.exceptions.BusinessError

package object http4sbookcrud extends IOExtensions {
  type Result[A] = Either[BusinessError, A]
  type ResultT[A] = EitherT[IO, BusinessError, A]
  type Complete = Unit

  implicit class ResultOps[A](result: Result[A]) {
    def toResultT: ResultT[A] = EitherT.fromEither[IO](result)
  }

}
