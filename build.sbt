name := "scalawags-sitegen"
version := "0.1"
organization := "tv.scalawags"

enablePlugins(SbtTwirl)

TwirlKeys.templateImports += "tv.scalawags._"
libraryDependencies += "joda-time" % "joda-time" % "2.8.1"
libraryDependencies += "org.scala-sbt" %% "io" % "1.0.0-M1"
//libraryDependencies += "com.google.api-client" % "google-api-client-java6" % "1.20.0"