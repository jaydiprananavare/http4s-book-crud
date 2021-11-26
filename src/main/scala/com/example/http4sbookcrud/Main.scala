package com.example.http4sbookcrud

import cats.effect.{ExitCode, IO, IOApp}
import com.example.http4sbookcrud.repositories.{BookRepository, InMemoryBookRepository}
import com.example.http4sbookcrud.routes.BookRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext


object Main extends IOApp {

  private val bookRepo: BookRepository = new InMemoryBookRepository()

  val httpRoutes = Router[IO](
    "/" -> BookRoutes.routes(bookRepo)
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {

    BlazeServerBuilder[IO](ExecutionContext.global)
      .bindHttp(9000, "0.0.0.0")
      .withHttpApp(httpRoutes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
