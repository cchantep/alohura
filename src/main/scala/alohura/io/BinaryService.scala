package alohura.io

import java.io.{ ByteArrayOutputStream, File, FileInputStream, InputStream }

import java.security.MessageDigest

import resource.managed

trait BinaryService {
  private val BUFFER_LENGTH = 8192
  private val HEX_DIGITS = "0123456789abcdef";

  protected val handler: Throwable ⇒ String =
    e ⇒ s"Error when reading inputstream: ${e.getMessage}"

  final def readBytes(file: File): Either[String, Array[Byte]] = readBytes(new FileInputStream(file))

  final def readBytes(open: ⇒ InputStream): Either[String, Array[Byte]] = {
    lazy val buffer = new Array[Byte](BUFFER_LENGTH)
    lazy val output = new ByteArrayOutputStream

    val result = managed(open).acquireFor { input ⇒
      @annotation.tailrec
      def loop(buffer: Array[Byte], input: InputStream, output: ByteArrayOutputStream): Array[Byte] = {
        val i = input.read(buffer)

        if (i > 0) {
          output.write(buffer, 0, i)
          loop(buffer, input, output)
        } else
          output.toByteArray
      }

      loop(buffer, input, output)
    }

    result.left.map(e ⇒ "Error during disposal of resources: %s" format {
      e.map(_.getMessage).mkString
    })
  }

  final def getSignature(file: File): Either[String, String] = {
    def go(bytes: Array[Byte]) = {
      val inst = MessageDigest.getInstance("MD5")

      inst.update(bytes)
      val r = inst.digest.foldLeft(new StringBuffer) {
        (buffer, byte) ⇒
          val b: Int = byte & 0xff

          buffer.append(HEX_DIGITS.charAt(b >>> 4))
            .append(HEX_DIGITS.charAt(b & 0xf))
      }

      r.toString
    }

    readBytes(file).right.map(go)
  }
}
