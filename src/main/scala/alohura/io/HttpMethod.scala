package alohura.io

import dispatch._

sealed trait HttpMethod extends (Req => Req)

object GET extends HttpMethod {
  def apply(r: Req) = r.GET
}

object POST extends HttpMethod {
  def apply(r: Req) = r.POST
}

object HEAD extends HttpMethod {
  def apply(r: Req) = r.HEAD
}
