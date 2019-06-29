# Alohura

Environment specification framework (or on the Discworld, the lightning goddess of the beTrobi people).

## Requirements

* Scala 2.12
* Java 1.8+
* SBT 1.0+

## Usage

Alohura can be used in SBT projects adding dependency `"alohura" %% "alohura" % "VERSION"` 
and having `"Tatami Releases" at "https://raw.github.com/cchantep/tatami/master/releases/"` in resolvers.

### Specs matchers

Class:
- `"name.of.class.A" must beInstantiated[a.super.class.of.A](jarUrl)` (class A must have a no-arg constructor)
- `"name.of.class.A" must beInstantiatedLike[a.super.class.of.A](jarUrl) { instance => ... }`

Network:
- `"host" must beResolvedAs { host => ... }`
- `"host" must beListeningOn(80)`
- `"host" must beRespondingSmtp` or `"host" must beRespondingSmtp(25)`

SQL/Database:
- `"jdbc:url" must connectDatabaseWith("user", "password"[, secTimeout, Some(java.sql.Driver)])`

## Build

Alohura can be built from these sources using SBT: `sbt publish`
