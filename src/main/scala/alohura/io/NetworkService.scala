package alohura.io

import java.io.{ IOException, OutputStream }
import java.net.{ InetAddress, UnknownHostException, URL, Socket }

trait NetworkService extends BinaryService {
  def doSocket(host: String, port: Int): Either[String, String] = {
    var socket: Socket = null
    var out: OutputStream = null

    try {
      socket = new Socket(InetAddress.getByName(host), port)
      out = socket.getOutputStream()
      out.write(1)

      if (socket.isConnected) Right(socket.toString)
      else Left(s"socket not connected to $host:$port")
    } catch {
      case e: UnknownHostException ⇒
        Left(s"host $host is unknown")
      case e: IOException ⇒ Left(s"cannot send packet to $host:$port")
      case t: Throwable   ⇒ Left(s"cannot create socket to $host:$port")
    } finally {
      if (out != null) try { out close }
      if (socket != null) try { socket close }
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
