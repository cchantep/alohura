package alohura.matcher

import java.net.{ URL, URLClassLoader }

import org.specs2.matcher.{ Expectable, Matcher, MatchResult }

trait ClassMatcher {
  def beInstantiated[A](jar: URL, cl: ClassLoader = getClass.getClassLoader): Matcher[String] = beInstantiated[A](jar, cl, None)

  def beInstantiatedLike[A](jar: URL, cl: ClassLoader = getClass.getClassLoader)(then: A ⇒ MatchResult[_]): Matcher[String] = beInstantiated[A](jar, cl, Some(then))

  private def beInstantiated[A](jar: URL, cl: ClassLoader, then: Option[A ⇒ MatchResult[_]]): Matcher[String] = new Matcher[String] {
    def apply[S <: String](e: Expectable[S]) = try {
      val loader = URLClassLoader.newInstance(Array(jar), cl)
      val c = loader.loadClass(e.value)

      (then, Option(c.newInstance.asInstanceOf[A])) match {
        case (Some(m), Some(i)) ⇒
          val r = m(i).toResult
          result(r.isSuccess,
            s"${e.description} succeed to instantiate ${e.value}",
            s"${e.description} cannot instantiate class: ${e.value}: ${r.message}", e)

        case (None, Some(i)) ⇒ result(true,
          s"${e.description} succeed to instantiate ${e.value}", "", e)

        case _ ⇒ result(false,
          "", s"${e.description} cannot instantiate class: ${e.value}", e)

      }
    } catch {
      case ex: Throwable ⇒
        result(false,
          "",
          s"${e.description} failed to instantiate class: ${ex.getMessage}",
          e)
    }
  }

}
