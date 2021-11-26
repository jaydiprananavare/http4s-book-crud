package com.example.http4sbookcrud.exceptions

sealed trait BusinessError

case class RuntimeError(e: Throwable) extends BusinessError

case class ResourceDoesNotExists() extends BusinessError

case class JsonParseError(parsingError: String) extends BusinessError
