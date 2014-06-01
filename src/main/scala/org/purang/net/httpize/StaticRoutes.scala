package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.http4s.dsl./
import org.http4s.Status._


class StaticRoutes extends LazyLogging {

  val cache = new ResourceCache


  val service: HttpService = {

    case r if r.pathInfo.endsWith("form")  => cache.getResource("", r.pathInfo + ".html", r)

    case r if r.pathInfo.endsWith("moby")  => cache.getResource("", r.pathInfo + ".html", r)

    case r if (r.pathInfo.endsWith(".html") ||  r.pathInfo.endsWith(".txt") ) => cache.getResource("", r.pathInfo, r)

    case r if (r.pathInfo.startsWith("/js") || r.pathInfo.startsWith("/img") || r.pathInfo.startsWith("/css"))  => cache.getResource("", r.pathInfo, r)

    case r if r.pathInfo.endsWith("/") => service(r.withPathInfo(r.pathInfo + "index.html"))

    case r => NotFound("404 Not Found: " + r.pathInfo)
  }

}
