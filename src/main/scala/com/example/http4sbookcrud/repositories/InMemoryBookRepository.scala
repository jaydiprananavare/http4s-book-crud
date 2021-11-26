package com.example.http4sbookcrud.repositories

import java.util.UUID

import cats.data.EitherT
import cats.effect.IO
import com.example.http4sbookcrud.exceptions.{BusinessError, ResourceDoesNotExists}
import com.example.http4sbookcrud.models.Book
import com.example.http4sbookcrud.{Complete, IoOps, ResultT}

import scala.collection.mutable


class InMemoryBookRepository extends BookRepository {

  private val bookDB = mutable.Map[String, Book]().empty

  override def addBook(book: Book): ResultT[String] = {
    val bookId = UUID.randomUUID().toString
    IO {
      bookDB.put(bookId, book.copy(id = Some(bookId)))
      bookId
    }.adaptErrorT()
  }

  override def getBook(id: String): ResultT[Book] = {
    bookDB.get(id) match {
      case Some(book) => IO.pure(book).adaptErrorT()
      case None => EitherT.leftT[IO, Book](ResourceDoesNotExists())
    }
  }

  override def getBooks: ResultT[List[Book]] = IO.pure(bookDB.values.toList).adaptErrorT()

  override def updateBook(id: String, book: Book): ResultT[Complete] = {
    for {
      _ <- getBook(id)
      updatedBook <- IO(bookDB.put(id, book.copy(id = Some(id)))).adaptErrorT()
    } yield {
      val _ = updatedBook match {
        case Some(_) => EitherT.rightT[IO, BusinessError](())
        case None => EitherT.leftT[IO, Complete](ResourceDoesNotExists())
      }
    }
  }

  override def deleteBook(id: String): ResultT[Complete] = {
    bookDB.remove(id) match {
      case Some(_) => EitherT.rightT[IO, BusinessError](())
      case None => EitherT.leftT[IO, Complete](ResourceDoesNotExists())
    }
  }
}
