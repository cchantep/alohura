package alohura.matcher

import java.sql.{ Connection, Driver, DriverManager }

import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration._

import org.specs2.matcher.{ Expectable, Matcher, MatchResult }

trait SqlMatcher {
  def connectDatabaseWith(user: String, password: String, driver: Option[Driver] = None, timeout: Int = 5)(implicit exec: ExecutionContext) =
    new Matcher[String] {
      def apply[S <: String](e: Expectable[S]) = try {
        val connectionUrl = e.value
        val prop = new java.util.Properties
        prop.put("user", user)
        prop.put("password", password)

        val c: Option[Connection] = driver orElse {
          Option(DriverManager.getDriver(connectionUrl))
        } map { d ⇒
          Await.result(Future { d.connect(connectionUrl, prop) },
            Duration(timeout, SECONDS))

        }
        val connected = c.fold(false) { x ⇒
          val connected = !x.isClosed()
          try { x.close() } catch { case _: Throwable ⇒ () }
          connected && x.isClosed() // was connected and is closed
        }

        result(connected,
          s"${e.description} succeed to connect to the database",
          s"${e.description} cannot connect to database: ${e.value}", e)

      } catch {
        case ex: Throwable ⇒
          result(false,
            "",
            s"${e.description} failed to connect to database: ${ex.getMessage}", e)
      }
    }

}
