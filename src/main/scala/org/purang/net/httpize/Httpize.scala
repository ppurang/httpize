package org.purang.net.httpize

import java.util.concurrent.ScheduledThreadPoolExecutor
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.http4s.server.HttpService
import org.http4s.server.blaze.BlazeServer
import org.http4s.Request
import org.http4s.server.middleware.GZip

import org.http4s.dsl._


class Httpize(host: String, port: Int) extends StrictLogging {

  private val pool = new ScheduledThreadPoolExecutor(3)

  val routes = new Routes(pool).service
  val static = new StaticRoutes().service
  val gzipped = new GzipRoutes().service

  val service: HttpService =  { case req: Request =>
    val uri = req.uri.path
    if (uri.endsWith("html")) {
      logger.info(s"${req.remoteAddr.getOrElse("null")} -> ${req.method}: ${req.uri.path}")
    }

    routes orElse GZip(gzipped) orElse static applyOrElse (req, {_: Request => NotFound(req.uri.path)})
  }

  // Build the server instance and begin
  def run(): Unit = BlazeServer.newBuilder
    .withHost(host)
    .withPort(port)
    .mountService(service, "")
    .run()
}

object Httpize extends StrictLogging {
  val ip = Option(System.getenv("OPENSHIFT_DIY_IP")).getOrElse("0.0.0.0")
  val port = (Option(System.getenv("PORT")) orElse
              Option(System.getenv("HTTP_PORT")))
          .map(_.toInt)
          .getOrElse(8080)

  logger.info(s"Starting Http4s-blaze example on '$ip:$port'")
  println(s"Starting Http4s-blaze example on '$ip:$port'")

  def main(args: Array[String]): Unit = new Httpize(ip, port).run()
}

