package alohura
package matcher

import io.NetworkService

import org.specs2.matcher._

trait NetworkMatcher extends NetworkService {

  def beResolvedAs(f: String ⇒ MatchResult[_]) = beResolvedWithin(5000)(f)

  def beResolvedWithin(timeout: Int)(f: String ⇒ MatchResult[_]) = new Matcher[String] {
    def apply[S <: String](e: Expectable[S]) = doPing(e.value, timeout) match {
      case Right(addr) ⇒
        val r = f(addr).toResult

        result(r.isSuccess,
          s"${e.description} is resolved at $addr and ${r.message}",
          s"${e.description} is resolved at $addr but ${r.message}",
          e)
      case Left(msg) ⇒
        result(false,
          "",
          s"${e.description} can't be resolved: $msg",
          e)
    }
  }

  def beRespondedWith(f: Array[Byte] ⇒ MatchResult[_]) = new Matcher[String] {
    def apply[S <: String](e: Expectable[S]) = doCurl(e.value) match {
      case Right(bytes) ⇒
        val r = f(bytes).toResult

        result(r.isSuccess,
          s"url respond and ${r.message}",
          s"url respond but ${r.message}",
          e)
      case Left(msg) ⇒
        result(false,
          "",
          s"url doesn't respond: ${e.value} ($msg)",
          e)
    }
  }
}
