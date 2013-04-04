package alohura
package io

import java.net.InetAddress
import java.net.UnknownHostException
import java.net.URL

trait NetworkService extends BinaryService {
  def doPing(host: String, timeout: Int): Either[String, String] = try {
    val inet = InetAddress.getByName(host)

    if (inet.isReachable(timeout))
      Right(inet.getHostAddress)
    else
      Left(s"Host $host is not reachable")
  } catch {
    case e: UnknownHostException ⇒ Left(s"host $host is unknown")
  }

  def doCurl(addr: String, h: Throwable ⇒ String = handler): Either[String, Array[Byte]] =
    readBytes(new URL(addr).openStream, h)
}
