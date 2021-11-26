package com.example.http4sbookcrud.routes

import cats.effect.IO
import com.example.http4sbookcrud.ResultT
import com.example.http4sbookcrud.exceptions.ResourceDoesNotExists
import com.example.http4sbookcrud.models.Book
import com.example.http4sbookcrud.repositories.BookRepository
import io.circe.Json
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}

object BookRoutes {

  def routes(bookRepo: BookRepository): HttpRoutes[IO] = {

    val dsl = new Http4sDsl[IO] {}
    import dsl._


    implicit class ResultTOps[A](result: ResultT[A]) {

      def handleErrorOrRespondWith(f: A => IO[Response[IO]]): IO[Response[IO]] = {
        result
          .value.flatMap {
          case Right(result) => f(result)
          case Left(ResourceDoesNotExists()) => NotFound()
          case Left(_) => ExpectationFailed()
        }
      }
    }

    HttpRoutes.of[IO] {
      case req@POST -> Root / "books" =>
        req.decode[Book] { book =>
          bookRepo.addBook(book).handleErrorOrRespondWith {
            bookId =>
              Created(Json.obj(("id", Json.fromString(bookId))))
          }
        }

      case GET -> Root / "books" / id =>
        bookRepo.getBook(id).handleErrorOrRespondWith(Ok(_))

      case GET -> Root / "books" =>
        bookRepo.getBooks.handleErrorOrRespondWith(Ok(_))

      case req@PUT -> Root / "books" / id =>
        req.decode[Book] { book =>
          bookRepo.updateBook(id, book).handleErrorOrRespondWith(_ => Ok())
        }

      case DELETE -> Root / "books" / id =>
        bookRepo.deleteBook(id).handleErrorOrRespondWith(_ => NoContent())
    }
  }

}
