package alohura.matcher

import java.io.{ File, FileInputStream, ByteArrayInputStream, InputStream }

trait ToInputStream[-A] {
  def apply[B <: A](input: B): InputStream
}

object ToInputStream {
  def apply[A](implicit T: ToInputStream[A]) = T

  implicit val fileToInputStream = new ToInputStream[File] {
    def apply[B <: File](input: B) = new FileInputStream(input)
  }

  implicit val bytesToInputStream = new ToInputStream[Array[Byte]] {
    def apply[B <: Array[Byte]](input: B) = new ByteArrayInputStream(input)
  }

  implicit val inputStreamToInputStream = new ToInputStream[InputStream] {
    def apply[B <: InputStream](input: B) = input
  }

  implicit val stringToInputStream = new ToInputStream[String] {
    def apply[B <: String](input: B) = new ByteArrayInputStream(input.getBytes("UTF-8"))
  }
}
