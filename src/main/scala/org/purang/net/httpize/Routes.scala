package org.purang.net.httpize

import java.util.concurrent.ScheduledThreadPoolExecutor

import org.http4s._
import org.http4s.dsl._

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.http4s.Header.{`Content-Type`}
import org.http4s.server.HttpService
import java.lang.{Process => _}
import scalaz.stream.Process
import concurrent.duration._
import scala.util.Try

class Routes(ec: ScheduledThreadPoolExecutor) extends LazyLogging {

  private implicit def _ec = ec

  val service: HttpService = {
    case GET -> Root / "hello" => Ok("Hello world!")

    case GET -> Root / "hello" / rest => Ok(s"Hello, $rest!")

    case r @ GET -> Root / "ip" =>  Ok(IP(r))

    case r @ GET -> Root / "user-agent" =>  Ok(UserAgent(r))

    case r @ GET -> Root / "headers" =>  Ok(HeadersContainer(r.headers))

    case r @ GET -> Root / "cookies" / "set" =>
      val ok = Ok(s"Bought some cookies ${r.multiParams}")
      val cookies = for (c <- r.multiParams) yield Cookie(c._1, c._2.mkString(","), path = Some("/"))
      cookies.foldLeft(ok)((r,c) => r.addCookie(c))


    case r @ GET -> Root / "cookies" / "delete" =>
      val h = Headers((for (c <- r.multiParams) yield Header.`Set-Cookie`(Cookie(c._1, "", path = Some("/"), expires = Some(DateTime.UnixEpoch), maxAge = Some(0)))).toList)
                .put(`Content-Type`.`text/plain`)

      Ok(s"Ate all the cookies ${r.multiParams}")
          .withHeaders(h)

    case r @ GET -> Root / "cookies"  => Ok(r.headers.get(Header.`Cookie`).fold("What Cookies?")(_.value))

    case r@GET -> Root / "delay" / time => Try(time.toInt).map{t =>
      if(t > 10) {
        BadRequest(s"From n to 10 seconds and $time is not in it.")
      } else {
        Ok(Process.sleepUntil(Process.sleep(t seconds) fby Process(true))(Process(s"Phew! Done after $t seconds.")))
      }
    }.getOrElse(BadRequest(s"$time needs to be an int"))

    //the following is the original and was exposed under /delay now it has been
    // exposed under /sdelay
    case r@GET -> Root / "sdelay" / time => Try(time.toInt).map{t =>
      if(t > 10) {
        BadRequest(s"From n to 10 seconds and $time is not in it.")
      } else {
        Process.sleep(t seconds).run.run
        Ok(s"Phew! Done after $t seconds.")
      }
    }.getOrElse(BadRequest(s"$time needs to be an int"))

    case req @ (POST -> Root / "post" | PUT -> Root / "put" | DELETE -> Root / "delete" | PATCH -> Root / "patch" ) => Tasks(req){Response(body = req.body)}

    case GET -> Root / "stream" / n => Ok(Process.range(0, n.toInt).map(i => s"This is string number $i"))

  }

}


class GzipRoutes extends LazyLogging {

  val service: HttpService = {
    case r @ GET -> Root / "gzip" =>  Ok(All(r))
  }

}

