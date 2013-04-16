package alohura.io

import dispatch.as

trait ToContent[A] extends (Res ⇒ A)

object ToContent {
  implicit val identity = new ToContent[Res] {
    def apply(r: Res) = r
  }

  implicit val stringToContent = new ToContent[String] {
    def apply(r: Res) = as.String(r)
  }

  implicit val bytesToContent = new ToContent[Array[Byte]] {
    def apply(r: Res) = as.Bytes(r)
  }
}
