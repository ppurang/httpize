package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._
import org.http4s.Status.{EntityResponseGenerator, Ok}

import com.typesafe.scalalogging.slf4j.LazyLogging
import scalaz.concurrent.Task
import Header.`Content-Type`.`application/json`


class Routes extends LazyLogging {

  val service: HttpService = {
    case Get -> Root / "hello" => Ok("Hello world!")

    case r @ Get -> Root / "ip" =>  Ok(IP(r))
  }

}
