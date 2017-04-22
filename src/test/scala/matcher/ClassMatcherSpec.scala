package matcher

class ClassMatcherSpec extends org.specs2.mutable.Specification
    with alohura.matcher.ClassMatcher with ClassMatcherFixtures {

  "Class matcher" title

  "Class alohura.matcher.TestObject" should {
    "instantiated as java.lang.Object" in {
      ("alohura.matcher.TestObject" must beInstantiated[Object](testJar)).
        and("alohura.matcher.TestObject".
          aka("test class") must beInstantiatedLike[Object](testJar) { o ⇒
            true must beTrue
          })
    }

    "not be instantiated with extra matcher" in {
      "alohura.matcher.TestObject".
        aka("test class") must not(beInstantiatedLike[Object](testJar) { o ⇒
          false must beTrue
        })
    }
  }
}

sealed trait ClassMatcherFixtures {
  lazy val testJar = getClass.getResource("test.jar")
}
