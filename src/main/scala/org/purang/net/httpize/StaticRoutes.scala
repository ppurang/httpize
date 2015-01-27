package org.purang.net.httpize

import org.http4s.server.HttpService
import org.http4s.dsl._


class StaticRoutes {

  val cache = new ResourceCache


  val service: HttpService = HttpService {

    case r if r.pathInfo.endsWith("form")  => cache.getResource("", r.pathInfo + ".html", r)

    case r if r.pathInfo.endsWith("moby")  => cache.getResource("", r.pathInfo + ".html", r)

    case r if (r.pathInfo.endsWith(".html") ||  r.pathInfo.endsWith(".txt") ) => cache.getResource("", r.pathInfo, r)

    case r if (r.pathInfo.startsWith("/js") || r.pathInfo.startsWith("/img") || r.pathInfo.startsWith("/css"))  => cache.getResource("", r.pathInfo, r)

    case r if r.pathInfo.endsWith("/") => cache.getResource("", r.pathInfo + "index.html", r)

    case r => NotFound("404 Not Found: " + r.pathInfo)
  }

}
