package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._
import org.http4s.Status._

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.http4s.dsl./
import scalaz.concurrent.Task

class Routes extends LazyLogging {

  val cache = new ResourceCache


  val service: HttpService = {
    case Get -> Root / "hello" => Ok("Hello world!")

    case Get -> Root / "hello" / rest => Ok(s"Hello, $rest!")

    case r @ Get -> Root / "ip" =>  Ok(IP(r))

    case r @ Get -> Root / "user-agent" =>  Ok(UserAgent(r))

    case r @ Get -> Root / "headers" =>  Ok(HeadersContainer(r.headers))

    case r if r.pathInfo.endsWith(".html")  => cache.getResource("", r.pathInfo, r)

    case r if (r.pathInfo.startsWith("/js") || r.pathInfo.startsWith("/img") || r.pathInfo.startsWith("/css"))  => cache.getResource("", r.pathInfo, r)

    case r if r.pathInfo.endsWith("/") => service(r.withPathInfo(r.pathInfo + "index.html"))

    case r => NotFound("404 Not Found: " + r.pathInfo)
  }

}
