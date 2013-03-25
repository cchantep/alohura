package alohura
package io

import java.io.File

trait FileSystemService {
  private def proc(base: Option[File], path: String, f: File ⇒ Boolean)(h: File ⇒ String): Either[String, File] = {
    val entity = base.fold(new File(path))(new File(_, path))

    if (entity.exists && f(entity))
      Right(entity)
    else
      Left(h(entity))
  }

  def doFile(base: Option[File], path: String): Either[String, File] =
    proc(base, path, _.isFile)(_ + " doesn't exist or isn't a file")

  def doDirectory(base: Option[File], path: String): Either[String, File] =
    proc(base, path, _.isDirectory)(_ + " doesn't exist or isn't a directory")
}
