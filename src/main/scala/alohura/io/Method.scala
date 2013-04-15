package alohura.io

import dispatch._

sealed trait Method {
  def apply(r: Req): Req
}

object GET extends Method {
  def apply(r: Req) = r.GET
}

object POST extends Method {
  def apply(r: Req) = r.POST
}

object HEAD extends Method {
  def apply(r: Req) = r.HEAD
}
