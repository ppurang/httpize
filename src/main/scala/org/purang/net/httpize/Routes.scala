package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._
import org.http4s.Status._

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.http4s.dsl./
import scalaz.concurrent.Task

class Routes extends LazyLogging {

  val service: HttpService = {
    case Get -> Root / "hello" => Ok("Hello world!")

    case Get -> Root / "hello" / rest => Ok(s"Hello, $rest!")

    case r @ Get -> Root / "ip" =>  Ok(IP(r))

    case r @ Get -> Root / "user-agent" =>  Ok(UserAgent(r))

    case r @ Get -> Root / "headers" =>  Ok(HeadersContainer(r.headers))

    case r@Get -> Root / "cookies" / "set" => CookiesSetter.addFromParams(Ok(s"Setting cookies ${r.multiParams}"), r)

    case r@Get -> Root / "cookies" / "delete" => val ok = Ok(s"Deleting cookies ${r.multiParams}")
      r.multiParams.map(x => ok.removeCookie(x._1))
      ok
  }

}


class GzipRoutes extends LazyLogging {

  val service: HttpService = {
    case r @ Get -> Root / "gzip" =>  Ok(All(r))
  }

}



