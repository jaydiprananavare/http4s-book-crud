package com.example.http4sbookcrud.repositories

import com.example.http4sbookcrud.{Complete, ResultT}
import com.example.http4sbookcrud.models.Book

trait BookRepository {

  def addBook(book: Book): ResultT[String]

  def getBook(id: String): ResultT[Book]

  def getBooks: ResultT[List[Book]]

  def deleteBook(id: String): ResultT[Complete]

  def updateBook(id: String, book: Book): ResultT[Complete]
}
