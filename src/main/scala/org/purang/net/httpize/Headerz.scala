package org.purang.net.httpize

import org.http4s.{Header, HeaderKey}
import org.http4s.util.CaseInsensitiveString._

object Headerz {
  object `Execution-Mode` extends HeaderKey.Singleton {
    override type HeaderT = Header

    override val name = "Execution-Mode".ci

    override def matchHeader(header: Header): Option[Header] =
      if (header.name == name) Some(header)
      else None

    // TODO: should be implemented
    override def parse(s: String) = ???
  }
}
