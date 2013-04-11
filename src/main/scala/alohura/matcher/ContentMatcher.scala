package alohura
package matcher

import scala.xml.Source
import scala.xml.XML
import scala.xml.Elem

import org.specs2.matcher._
import org.specs2.execute._

import javax.xml.parsers.SAXParserFactory

trait ContentMatcher {

  lazy val parser = {
    val inst = SAXParserFactory.newInstance

    inst.setValidating(true)
    inst.newSAXParser
  }

  def beValidXMLWith[A](f: Elem ⇒ MatchResult[_])(implicit T: ToInputStream[A]) = new Matcher[A] {
    def apply[S <: A](e: Expectable[S]) = try {
      val xml = XML.loadXML(Source.fromInputStream(T(e.value)), parser)
      val r = f(xml).toResult

      result(r.isSuccess,
        s"xml is valid and ${r.message}",
        s"xml is valid but ${r.message}",
        e)
    } catch {
      case err: Throwable ⇒
        result(false,
          "",
          s"Failed to valid xml: ${err.getMessage}",
          e)
    }
  }
}
