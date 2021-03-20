import java.io.{
  BufferedReader,
  InputStream,
  InputStreamReader,
  OutputStream,
  PrintWriter
}

import scala.reflect.{ Manifest, ManifestFactory }

package object alohura {
  implicit def bufferedReaderManifest: Manifest[BufferedReader] =
    ManifestFactory.classType(classOf[BufferedReader])

  implicit def inputStreamManifest: Manifest[InputStream] =
    ManifestFactory.classType(classOf[InputStream])

  implicit def inputStreamReaderManifest: Manifest[InputStreamReader] =
    ManifestFactory.classType(classOf[InputStreamReader])

  implicit def outputStreamManifest: Manifest[OutputStream] =
    ManifestFactory.classType(classOf[OutputStream])

  implicit def printWriterManifest: Manifest[PrintWriter] =
    ManifestFactory.classType(classOf[PrintWriter])

}
