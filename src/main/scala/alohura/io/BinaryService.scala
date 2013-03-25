package alohura
package io

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.ByteArrayOutputStream

import java.security.MessageDigest

import scala.annotation.tailrec

trait BinaryService {
  private val BUFFER_LENGTH = 8192
  private val HEX_DIGITS = "0123456789abcdef";

  final def readBytes(file: File): Either[String, Array[Byte]] =
    readBytes(new FileInputStream(file))

  final def readBytes(input: ⇒ InputStream): Either[String, Array[Byte]] = {
    lazy val buffer = new Array[Byte](BUFFER_LENGTH)
    lazy val output = new ByteArrayOutputStream

    @tailrec
    def loop(buffer: Array[Byte], input: InputStream, output: ByteArrayOutputStream): Array[Byte] = {
      val i = input.read(buffer)

      if (i > 0) {
        output.write(buffer, 0, i)
        loop(buffer, input, output)
      } else
        output.toByteArray
    }

    try {
      val bytes = loop(buffer, input, output)

      Right(bytes)
    } catch {
      case e: Throwable ⇒ Left(s"Error when reading inputstream: ${e.getMessage}")
    } finally {
      try {
        Option(input).foreach(_.close())
      } catch {
        case e: Throwable ⇒ Left(s"Error during disposal of resources: ${e.getMessage}")
      }
    }
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
