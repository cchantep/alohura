package tests

class NetworkMatcherSpec extends org.specs2.mutable.Specification
    with alohura.matcher.NetworkMatcher {

  "Network matcher" title

  "GitHub" should {
    "be available on port 80" in {
      ("github.com", 80) must haveAvailability()() { (_, _) ⇒ ok }
    }

    "not be available on port 8080" in {
      ("www.yahoo.fr", 8080) must haveAvailability()(min = 0) { (_, _) ⇒ ok }
    }
  }
}
