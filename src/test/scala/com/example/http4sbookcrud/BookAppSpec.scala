package com.example.http4sbookcrud

import cats.data.EitherT
import cats.effect.IO
import com.example.http4sbookcrud.models.Book
import com.example.http4sbookcrud.repositories.BookRepository
import com.example.http4sbookcrud.routes.BookRoutes
import io.circe.generic.auto.exportEncoder
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.implicits._

class BookAppSpec extends CatsEffectSuite {

  private val bookId: String = "a1b2c3"
  private val book: Book = Book("Scala", "Martin", Some(bookId))

  val bookRepository: BookRepository = new BookRepository {
    override def addBook(book: Book): ResultT[String] = EitherT.pure("some-id")

    override def getBook(id: String): ResultT[Book] = EitherT.pure(book)

    override def getBooks: ResultT[List[Book]] = EitherT.pure(List(book))

    override def deleteBook(id: String): ResultT[Complete] = EitherT.pure(())

    override def updateBook(id: String, book: Book): ResultT[Complete] = EitherT.pure(())
  }

  test("Book App saves book and returns book id") {
    val addBook = Request[IO](Method.POST, uri"/books").withEntity(book)
    val response = BookRoutes.routes(bookRepository).orNotFound(addBook)
    assertIO(response.map(_.status), Status.Created)
    assertIO(response.flatMap(_.as[String]), "{\"id\":\"some-id\"}")
  }

  test("Book App returns a books") {
    val getBooks = Request[IO](Method.GET, uri"/books/a1b2c3")
    val response = BookRoutes.routes(bookRepository).orNotFound(getBooks)
    assertIO(response.flatMap(_.as[String]), "{\"title\":\"Scala\",\"author\":\"Martin\",\"id\":\"a1b2c3\"}")
  }

  test("Book App returns all books") {
    val getBooks = Request[IO](Method.GET, uri"/books")
    val response = BookRoutes.routes(bookRepository).orNotFound(getBooks)
    assertIO(response.flatMap(_.as[String]), "[{\"title\":\"Scala\",\"author\":\"Martin\",\"id\":\"a1b2c3\"}]")
  }

}
