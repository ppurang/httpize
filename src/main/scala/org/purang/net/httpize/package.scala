package org.purang.net

import org.http4s._
import argonaut._, Argonaut._
import org.http4s.Header.`Content-Type`
import scodec.bits.ByteVector

import Header.`Content-Type`.`application/json`
import org.http4s
import org.http4s.Cookie
import org.http4s.Status.EntityResponseGenerator
import scalaz.concurrent.Task


package object httpize {
  implicit class ComposePartial[A, B](pf: PartialFunction[A, B]) {
    def andThenPartial[C](that: PartialFunction[B, C]): PartialFunction[A, C] =
      Function.unlift(pf.lift(_) flatMap that.lift)
  }

  implicit def TWritable[T](implicit charset: CharacterSet = CharacterSet.`UTF-8`, encode: EncodeJson[T]) =
    new SimpleWritable[T] {
      def contentType: `Content-Type` = `application/json`.withCharset(charset)

      def asChunk(t: T) = ByteVector.view(encode(t).nospaces.getBytes(charset.charset))
    }

  val Okk = new Status(200, "OK") with EntityResponseGenerator {
    self: Status =>
    def apply[A](body: A, contentType: `Content-Type`, mheaders: Headers = Headers.empty)(implicit w: Writable[A]): Task[Response] = {
      var headers: Headers = Headers.empty
      // tuple assignment runs afoul of https://issues.scala-lang.org/browse/SI-5301
      headers :+= contentType
      w.toBody(body).map { case (proc, len) =>
        len foreach {
          headers :+= Header.`Content-Length`(_)
        }
        Response(self, headers ++ mheaders, proc)
      }
    }
  }


  //IP address of remote client:
  case class IP(remote: String, `x-forwarded-for`: String)

  object IP {
    def apply(r: Request): IP = {
      IP(r.remoteAddr.getOrElse("xx.xx.xx.xx"),
        s"${r.headers.get(org.http4s.Header.`X-Forwarded-For`)}")
    }

    implicit def IPCodecJson: CodecJson[IP] =
      casecodec2(IP.apply, IP.unapply)("remote", "x-forwarded-for")
  }

  //User agent of client:
  case class UserAgent(`user-agent`: String)

  object UserAgent {
    def apply(r: Request): UserAgent = {
      UserAgent(r.headers.get(org.http4s.Header.`User-Agent`).toString)
    }

    implicit def UserAgentCodecJson: CodecJson[UserAgent] = casecodec1(UserAgent.apply(_: String), UserAgent.unapply)("user-agent")
  }

  //All headers sent by the client:
  case class HeadersContainer(headers: Headers)

  implicit def HeaderEncodeJson: EncodeJson[Header] = EncodeJson(
    (h: Header) => (h.toRaw.name.toString := h.toRaw.value) ->: jEmptyObject
  )

  implicit def HeadersContainerEncodeJson: EncodeJson[HeadersContainer] = EncodeJson(
    (hc: HeadersContainer) =>  ("headers" := hc.headers.toList)    ->: jEmptyObject
  )

  implicit def MethodCodecJson: EncodeJson[Method] = EncodeJson(
    (m: Method) =>  jString(m.name)
  )

  case class All(method: Method, headers: HeadersContainer, origin: IP)

  object All {
    def apply(r: Request): All = All(r.requestMethod, HeadersContainer(r.headers), IP(r))
      Cookie

    implicit def AllCodecJson: EncodeJson[All] = jencode3L((a:All) => (a.method, a.headers, a.origin))("method", "headers", "origin")

  }






}
