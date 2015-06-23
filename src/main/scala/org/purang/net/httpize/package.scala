package org.purang.net

import argonaut._
import argonaut.Argonaut._
import org.http4s._
import org.http4s.headers.`X-Forwarded-For`
import org.purang.net.httpize.Headerz

import scalaz.concurrent.{Strategy, Task}
import java.util.concurrent.ExecutorService


package object httpize {

  implicit def jsonEncoderOf[A](implicit encoder: EncodeJson[A]): EntityEncoder[A] =
    argonaut.jsonEncoderOf(encoder)

  //IP address of remote client:
  case class IP(remote: String, `x-forwarded-for`: String)

  object IP {
    def apply(r: Request): IP = {
      IP(r.remoteAddr.getOrElse("xx.xx.xx.xx"),
        s"${r.headers.get(`X-Forwarded-For`)}")
    }

    implicit def IPCodecJson: CodecJson[IP] =
      casecodec2(IP.apply, IP.unapply)("remote", "x-forwarded-for")
  }

  //User agent of client:
  case class UserAgent(`user-agent`: String)

  object UserAgent {
    def apply(r: Request): UserAgent = {
      UserAgent(r.headers.get(org.http4s.headers.`User-Agent`).toString)
    }

    implicit def UserAgentCodecJson: CodecJson[UserAgent] = casecodec1(UserAgent.apply(_: String), UserAgent.unapply)("user-agent")
  }

  //All headers sent by the client:
  case class HeadersContainer(headers: Headers)

  implicit def HeaderEncodeJson: EncodeJson[Header] = EncodeJson(
    (h: Header) => (h.name.toString := h.value) ->: jEmptyObject
  )

  implicit def HeadersContainerEncodeJson: EncodeJson[HeadersContainer] = EncodeJson(
    (hc: HeadersContainer) =>  ("headers" := hc.headers.toList)    ->: jEmptyObject
  )

  implicit def MethodCodecJson: EncodeJson[Method] = EncodeJson(
    (m: Method) =>  jString(m.name)
  )

  case class All(method: Method, headers: HeadersContainer, origin: IP)

  object All {
    def apply(r: Request): All = All(r.method, HeadersContainer(r.headers), IP(r))

    implicit def AllCodecJson: EncodeJson[All] = jencode3L((a:All) => (a.method, a.headers, a.origin))("method", "headers", "origin")

  }

  object Tasks {
    import scalaz._, Scalaz._

    def apply[A](req: Request, pool: ExecutorService = Strategy.DefaultExecutorService): (=> A) => Task[A] = a => req.headers.get(
      Headerz.`Execution-Mode`
    ).fold(Task.now(a))(h => {
      val hv = h.value.toLowerCase
      if (hv === "delay") {
        Task.delay(a)
      } else if(hv === "now") {
        Task.now(a)
      } else if(hv === "fork") {
        Task.fork(Task(a)) //https://groups.google.com/forum/#!topic/scalaz/4y_p56w2AOo
      } else {
        Task(a)
      }
    })
  }

}
