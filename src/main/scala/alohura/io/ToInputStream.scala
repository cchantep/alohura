package alohura.io

import java.io.{ File, FileInputStream, ByteArrayInputStream, InputStream }

trait ToInputStream[-A] {
  def apply[B <: A](input: B): InputStream
}

object ToInputStream {
  def apply[A](implicit T: ToInputStream[A]) = T

  implicit object FileToInputStream extends ToInputStream[File] {
    def apply[B <: File](input: B) = new FileInputStream(input)
  }

  implicit object BytesToInputStream extends ToInputStream[Array[Byte]] {
    @SuppressWarnings(Array("BoundedByFinalType"))
    def apply[B <: Array[Byte]](input: B) = new ByteArrayInputStream(input)
  }

  implicit object InputStreamToInputStream extends ToInputStream[InputStream] {
    def apply[B <: InputStream](input: B) = input
  }

  implicit object StringToInputStream extends ToInputStream[String] {
    @SuppressWarnings(Array("BoundedByFinalType"))
    def apply[B <: String](input: B) = new ByteArrayInputStream(input.getBytes("UTF-8"))
  }
}
