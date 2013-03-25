package alohura
package matcher

import io.BinaryService

import java.io.File

import org.specs2.matcher._
import org.specs2.execute._

trait FileMatcher extends BinaryService {
  def beFile = new Matcher[String] {
    def apply[S <: String](e: Expectable[S]) = {
      val file = new File(e.value)

      result(file.exists && file.isFile,
        s"${e.description} is a file",
        s"${e.description} is not a file",
        e)
    }

    def which(f: File ⇒ MatchResult[_]) = this and functionMatcher(f)

    private def functionMatcher(f: File ⇒ MatchResult[_]) = new Matcher[String] {
      def apply[S <: String](e: Expectable[S]) = {
        val file = new File(e.value)
        val r =
          if (file.exists && file.isFile)
            f(file).toResult
          else
            Failure("not a file")

        result(r.isSuccess,
          s"${e.description} is a file and ${r.message}",
          s"${e.description} is a file but ${r.message}",
          e)
      }
    }
  }

  def beDirectory = new Matcher[String] {
    def apply[S <: String](e: Expectable[S]) = {
      val file = new File(e.value)

      result(file.exists && file.isDirectory,
        s"${e.description} is a directory",
        s"${e.description} is not a directory",
        e)
    }

    def which(f: File ⇒ MatchResult[_]) = this and functionMatcher(f)

    private def functionMatcher(f: File ⇒ MatchResult[_]) = new Matcher[String] {
      def apply[S <: String](e: Expectable[S]) = {
        val file = new File(e.value)
        val r =
          if (file.exists && file.isDirectory)
            f(file).toResult
          else
            Failure("not a directory")

        result(r.isSuccess,
          s"${e.description} is a file and ${r.message}",
          s"${e.description} is a file but ${r.message}",
          e)
      }
    }
  }

  def haveMD5EqualsTo(expected: String) = new Matcher[File] {
    def apply[S <: File](e: Expectable[S]) = getSignature(e.value) match {
      case Left(msg) ⇒ result(false, "", s"${e.description}: an error occured when calculating MD5 signature: $msg", e)
      case Right(signature) ⇒
        result(signature == expected,
          s"${e.description} MD5 signature matches",
          s"${e.description} MD5 doesn't match. found: $signature, expected: $expected",
          e)
    }
  }
}
