package com.example.http4sbookcrud.repositories

import com.example.http4sbookcrud.ResultT
import com.example.http4sbookcrud.models.Book

trait BookRepository {

  def addBook(book: Book): ResultT[String]

  def getBook(id: String): ResultT[Book]

  def getBooks: ResultT[List[Book]]

  def deleteBook(id: String): ResultT[Unit]

  def updateBook(id: String, book: Book): ResultT[Unit]
}
