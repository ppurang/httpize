package org.purang.net.httpize

import org.http4s.{HttpService, Request}
import org.http4s.server.syntax._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.GZip
import org.log4s.getLogger
import java.util.concurrent.Executors

import org.http4s.server.{Server, ServerApp}

import scalaz.concurrent.Task


class Httpize(host: String, port: Int) {

  private val logger = getLogger

  private val pool = Executors.newScheduledThreadPool(3)

  val routes = new Routes(pool).service
  val static = new StaticRoutes().service
  val gzipped = new GzipRoutes().service

  val service: HttpService = routes orElse GZip(gzipped) orElse static
  // TODO: fix contramap
  //val service: HttpService =  (routes orElse GZip(gzipped) orElse static).contramap { req: Request =>
  //  val uri = req.uri.path
  //  if (uri.endsWith("html")) {
  //    logger.info(s"${req.remoteAddr.getOrElse("null")} -> ${req.method}: ${req.uri.path}")
  //  }
  //  req
  //}

  // Build the server instance and begin
  def run(): Task[Server] = BlazeBuilder
    .bindHttp(port, host)
    .mountService(service)
    .start
}

object Httpize extends ServerApp {
  private val logger = getLogger
  val ip = Option(System.getenv("OPENSHIFT_DIY_IP")).getOrElse("0.0.0.0")
  val port = (Option(System.getenv("PORT")) orElse
              Option(System.getenv("HTTP_PORT")))
          .map(_.toInt)
          .getOrElse(8080)

  logger.info(s"Starting Http4s-blaze example on '$ip:$port'")
  println(s"Starting Http4s-blaze example on '$ip:$port'")

  override def server(args: List[String]): Task[Server] = new Httpize(ip, port).run()
}

