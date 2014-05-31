package org.purang.net

import org.http4s.{Header, SimpleWritable, CharacterSet, Request}
import argonaut._, Argonaut._
import org.http4s.Header.`Content-Type`
import scodec.bits.ByteVector

import Header.`Content-Type`.`application/json`


package object httpize {

  implicit def TWritable[T](implicit charset: CharacterSet = CharacterSet.`UTF-8`, encode: EncodeJson[T]) =
    new SimpleWritable[T] {
      def contentType: `Content-Type` = `application/json`.withCharset(charset)

      def asChunk(t: T) = ByteVector.view(encode(t).nospaces.getBytes(charset.charset))
    }

  case class IP(remote: String, `x-forwarded-for`: String)

  object IP {
    def apply(r: Request): IP = {
      IP(r.remoteAddr.getOrElse("xx.xx.xx.xx"),
        s"${r.headers.get(org.http4s.Header.`X-Forwarded-For`)}")
    }

    implicit def IPCodecJson: CodecJson[IP] =
      casecodec2(IP.apply, IP.unapply)("remote", "x-forwarded-for")
  }

  case class UserAgent(`user-agent`: String)

  object UserAgent {
    def apply(r: Request): UserAgent = {
      UserAgent(r.headers.get(org.http4s.Header.`User-Agent`).toString)
    }

    implicit def UserAgentCodecJson: CodecJson[UserAgent] = casecodec1(UserAgent.apply(_: String), UserAgent.unapply)("user-agent")
  }

}
