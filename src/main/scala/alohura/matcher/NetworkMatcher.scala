package alohura.matcher

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.InetSocketAddress

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

import org.specs2.matcher.{ Expectable, Matcher, MatchResult }

import resource.managed

import alohura.io.{ NetworkService, ToContent, HttpMethod }

trait NetworkMatcher extends NetworkService {
  def beListeningOn(port: Int) = new Matcher[String] {
    @SuppressWarnings(Array("BoundedByFinalType"))
    def apply[S <: String](e: Expectable[S]) = doSocket(e.value, port)() match {
      case Right(_) ⇒ result(true, s"${e.value} is listening on $port", "", e)
      case Left(msg) ⇒
        result(false, "",
          s"${e.value} is not listening on $port: $msg",
          e)
    }
  }

  def beRespondingSmtp: Matcher[String] = beRespondingSmtp(25)

  /**
   * @param timeout the timeout in seconds
   */
  def beRespondingSmtp(port: Int, timeout: Long = 5L)(implicit exec: ExecutionContext = ExecutionContext.Implicits.global): Matcher[String] = new Matcher[String] {
    @SuppressWarnings(Array("BoundedByFinalType"))
    def apply[S <: String](e: Expectable[S]) = doSocket(e.value, port)({ s ⇒
      val io = for {
        is ← managed(s.getInputStream)
        ir ← managed(new InputStreamReader(is))
        br ← managed(new BufferedReader(ir))
        os ← managed(s.getOutputStream)
        pw ← managed(new PrintWriter(os))
      } yield (br, pw)

      io acquireAndGet { x ⇒
        val (in, out) = x

        out.println("HELO github.com")
        out.flush()

        val f: Future[String] = Future {
          val line = in.readLine()
          val l = line.length

          if (l < 3) sys.error(s"No SMTP status: $line")
          else line.substring(0, 3)
        }

        try {
          Right(Await.result(f, Duration(timeout, SECONDS)))
        } catch {
          case t: Exception ⇒ Left(t.getMessage)
        }
      }
    }) match {
      case Right(_) ⇒ result(
        true,
        s"${e.value} is responding to SMTP on $port", "", e)

      case Left(msg) ⇒
        result(false, "",
          s"${e.value} is not responding to SMTP on $port: $msg",
          e)

    }
  }

  def beResolvedAs(f: String ⇒ MatchResult[_]) = beResolvedWithin(5000)(f)

  def beResolvedWithin(timeout: Int)(f: String ⇒ MatchResult[_]) =
    new Matcher[String] {
      @SuppressWarnings(Array("BoundedByFinalType"))
      def apply[S <: String](e: Expectable[S]) =
        doPing(e.value, timeout) match {
          case Right(addr) ⇒ {
            val r = f(addr).toResult

            result(
              r.isSuccess,
              s"${e.description} is resolved at $addr and ${r.message}",
              s"${e.description} is resolved at $addr but ${r.message}",
              e)
          }

          case Left(msg) ⇒
            result(
              false,
              "",
              s"${e.description} can't be resolved: $msg",
              e)
        }
    }

  def haveAvailability(timeout: Int = 1000, count: Int = 10)(min: Int = count * 99 / 100)(f: (String, Int) ⇒ MatchResult[_]) = new Matcher[(String, Int)] {
    def apply[S <: (String, Int)](e: Expectable[S]) = {
      val (host, port) = e.value

      def test(n: Int, err: Int): MatchResult[S] =
        if (n == count) {
          val success = count - err

          if (success < min) {
            result(false, "", s"Availability of ${e.description} is not sufficient: $success < $min", e)
          } else {
            val r = f(host, port).toResult

            result(
              r.isSuccess,
              s"${e.value} is available and ${r.message}",
              s"${e.value} is available but ${r.message}",
              e)
          }
        } else withDummySocket(
          new InetSocketAddress(host, port), timeout) match {
            case Right(_) ⇒ test(n + 1, err)
            case _ ⇒ test(n + 1, err + 1)
          }

      test(0, 0)
    }
  }

  def beRespondedWith[A](method: HttpMethod, duration: Duration = Duration(5, SECONDS))(f: A ⇒ MatchResult[_])(implicit T: ToContent[A]) = new Matcher[String] {
    @SuppressWarnings(Array("BoundedByFinalType"))
    def apply[S <: String](e: Expectable[S]) = try {
      val a = Await.result(doRequest(e.value, method), duration)
      val r = f(a).toResult

      result(
        r.isSuccess,
        s"url respond and ${r.message}",
        s"url respond but ${r.message}",
        e)
    } catch {
      case ex: Exception ⇒
        result(
          false,
          "",
          s"url doesn't respond: ${e.value} (${ex.getMessage})",
          e)
    }
  }
}
