package alohura.io

import java.io.{ IOException, OutputStream }
import java.net.{ InetAddress, UnknownHostException, URL, Socket }

import resource.managed

trait NetworkService extends BinaryService {
  private def dummySocketWrite(s: Socket): Either[String, String] =
    managed(s.getOutputStream) acquireAndGet { out ⇒
      out.write(1)

      if (s.isConnected) Right(s.toString)
      else Left(s"socket not connected to ${s.getInetAddress.getHostName}:${s.getPort}")
    }

  def doSocket(host: String, port: Int)(f: Socket ⇒ Either[String, String] = dummySocketWrite): Either[String, String] = {
    var socket: Socket = null

    try {
      f(new Socket(InetAddress.getByName(host), port))
    } catch {
      case e: UnknownHostException ⇒
        Left(s"host $host is unknown")
      case e: IOException ⇒ Left(s"cannot send packet to $host:$port")
      case t: Throwable   ⇒ Left(s"cannot create socket to $host:$port")
    } finally {
      if (socket != null) try { socket close } catch { case _: Throwable ⇒ () }
    }
  }

  def doPing(host: String, timeout: Int): Either[String, String] = try {
    val inet = InetAddress.getByName(host)

    if (inet.isReachable(timeout))
      Right(inet.getHostAddress)
    else
      Left(s"Host $host is not reachable")
  } catch {
    case e: UnknownHostException ⇒ Left(s"host $host is unknown")
  }

  def doCurl(addr: String, h: Throwable ⇒ String = handler): Either[String, Array[Byte]] = readBytes(new URL(addr).openStream, h)

  def doRequest[A](location: String, method: HttpMethod)(implicit toContent: ToContent[A]): dispatch.Future[A] = {
    import dispatch._, Defaults._

    val svc = dispatch.url(location)

    dispatch.Http(method(svc) OK toContent)
  }
}
