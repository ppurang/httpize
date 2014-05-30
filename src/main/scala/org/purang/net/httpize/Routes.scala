package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._
import org.http4s.Status.Ok

import com.typesafe.scalalogging.slf4j.LazyLogging


class Routes extends LazyLogging {

  val service: HttpService = {
    case Get -> Root / "hello" => Ok("Hello world!")
  }

}
