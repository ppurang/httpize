package org.purang.net.httpize

import org.http4s.dsl._
import org.http4s._
import org.http4s.Status._

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.http4s.dsl./
import org.http4s.Header.`Content-Type`
import org.http4s.util.jodaTime.UnixEpoch
import java.lang.{Process => _}
import scalaz.stream.Process
import concurrent.duration._
import Duration._
import scala.util.Try
import scalaz.concurrent.Task

class Routes extends LazyLogging {

  val service: HttpService = {
    case Get -> Root / "hello" => Ok("Hello world!")

    case Get -> Root / "hello" / rest => Ok(s"Hello, $rest!")

    case r @ Get -> Root / "ip" =>  Ok(IP(r))

    case r @ Get -> Root / "user-agent" =>  Ok(UserAgent(r))

    case r @ Get -> Root / "headers" =>  Ok(HeadersContainer(r.headers))

    case r @ Get -> Root / "cookies" / "set" => Okk(s"Bought some cookies ${r.multiParams}", `Content-Type`.`text/plain`, Headers((for (c <- r.multiParams) yield Header.`Set-Cookie`(Cookie(c._1, c._2.mkString(","), path = Some("/")))).toList))

    case r @ Get -> Root / "cookies" / "delete" => Okk(s"Ate all the cookies ${r.multiParams}", `Content-Type`.`text/plain`, Headers((for (c <- r.multiParams) yield Header.`Set-Cookie`(Cookie(c._1, "", path = Some("/"), expires = Some(UnixEpoch), maxAge = Some(0)))).toList))

    case r @ Get -> Root / "cookies"  => Ok(r.headers.get(Header.`Cookie`).map(_.value).getOrElse("What Cookies?"))

    case r@Get -> Root / "delay" / time => Try(time.toInt).map{t =>
      if(t > 10) {
        BadRequest(s"From n to 10 seconds and $time is not in it.")
      } else {
        Ok(Process.sleepUntil(Process.sleep(t seconds) fby Process(true))(Process(s"Phew! Done after $t seconds.")))
      }
    }.getOrElse(BadRequest(s"$time needs to be an int"))

    //the following is the original and was exposed under /delay now it has been
    // exposed under /sdelay
    case r@Get -> Root / "sdelay" / time => Try(time.toInt).map{t =>
      if(t > 10) {
        BadRequest(s"From n to 10 seconds and $time is not in it.")
      } else {
        Process.sleep(t seconds).run.run
        Ok(s"Phew! Done after $t seconds.")
      }
    }.getOrElse(BadRequest(s"$time needs to be an int"))

    case req @ (Post -> Root / "post" | Put -> Root / "put" | Delete -> Root / "delete" | Patch -> Root / "patch" ) =>
          Task.now(Response(body = req.body))

    case Get -> Root / "stream" / n => Ok(Process.range(0, n.toInt).map(i => s"This is string number $i"))

  }

}


class GzipRoutes extends LazyLogging {

  val service: HttpService = {
    case r @ Get -> Root / "gzip" =>  Ok(All(r))
  }

}

