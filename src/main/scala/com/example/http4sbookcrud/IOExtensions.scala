package com.example.http4sbookcrud

import cats.data
import cats.effect.IO
import com.example.http4sbookcrud.exceptions.BusinessError

trait IOExtensions {

  implicit class IoOps[A](ioa: IO[A]) {

    def adaptErrorT(
                     pf: PartialFunction[Throwable, Either[BusinessError, A]] = PartialFunction.empty): ResultT[A] =
      data.EitherT(
        ioa.attempt
          .flatMap {
            case Right(success) => IO(Right(success))
            case Left(t) if pf.isDefinedAt(t) => IO(pf(t))
            case Left(t) => IO(Left(exceptions.RuntimeError(t)))
          })
  }

}
