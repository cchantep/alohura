package alohura.io

import java.io.IOException
import java.net.{
  InetAddress,
  InetSocketAddress,
  UnknownHostException,
  URL,
  Socket,
  SocketAddress,
  SocketTimeoutException
}

import scala.util.control.NonFatal

import resource.managed

trait NetworkService extends BinaryService {
  private def dummySocketWrite(s: Socket): Either[String, String] =
    managed(s.getOutputStream) acquireAndGet { out ⇒
      out.write(1)

      if (s.isConnected) Right(s.toString)
      else Left(s"socket not connected to ${s.getInetAddress.getHostName}:${s.getPort}")
    }

  def doSocket(host: String, port: Int, timeout: Int = 0)(f: Socket ⇒ Either[String, String] = dummySocketWrite): Either[String, String] = withSocket(new InetSocketAddress(host, port), timeout)(f)

  def doDummySocket(host: String, port: Int, timeout: Int = 0): Either[String, String] =
    doSocket(host, port, timeout)(dummySocketWrite _)

  def withDummySocket(addr: ⇒ SocketAddress, timeout: Int = 0): Either[String, String] = withSocket(addr, timeout)(dummySocketWrite _)

  @SuppressWarnings(Array("NullAssignment", "NullParameter"))
  def withSocket(addr: ⇒ SocketAddress, timeout: Int = 0)(f: Socket ⇒ Either[String, String]): Either[String, String] = {
    var socket: Socket = null

    try {
      socket = new Socket()

      socket.connect(addr, timeout)
      socket.setSoTimeout(timeout)

      f(socket)
    } catch {
      case _: SocketTimeoutException ⇒
        Left(s"connection timeout to ${socket.getInetAddress} ($timeout)")

      case e: UnknownHostException ⇒
        Left(s"${socket.getInetAddress} is unknown (${e.getMessage})")

      case e: IOException ⇒ Left(
        s"cannot send packet to ${socket.getInetAddress} (${e.getMessage})")

      case NonFatal(t) ⇒ Left(
        s"cannot create socket to ${socket.getInetAddress} (${t.getMessage})")
    } finally {
      if (socket != null) try {
        socket.close()
      } catch { case NonFatal(_) ⇒ () }
    }
  }

  def doPing(host: String, timeout: Int): Either[String, String] = try {
    val inet = InetAddress.getByName(host)

    if (inet.isReachable(timeout)) Right(inet.getHostAddress)
    else Left(s"Host $host is not reachable")
  } catch {
    case _: UnknownHostException ⇒ Left(s"host $host is unknown")
  }

  def doCurl(addr: String): Either[String, Array[Byte]] = readBytes(new URL(addr).openStream)

  def doRequest[A](location: String, method: HttpMethod)(implicit toContent: ToContent[A]): dispatch.Future[A] = {
    import dispatch._, Defaults._

    val svc = dispatch.url(location)

    dispatch.Http(method(svc) OK toContent)
  }
}
