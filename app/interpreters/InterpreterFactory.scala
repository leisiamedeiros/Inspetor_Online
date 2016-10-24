package compilers

import sys.process._
import java.io.File

import scala.language.postfixOps

trait Interpreter {
  def run(file: File): String
}

object InterpreterFactory {

  private class Scala extends Interpreter {
    override def run(file: File): String = {
      return f"scala ${file.getPath()}" !!
    }
  }

  private class Potigol extends Interpreter {
    override def run(file: File): String = {
      return ""
    }
  }

  private class Ruby extends Interpreter {
    override def run(file: File): String = {
      return f"ruby ${file.getPath()}" !!
    }
  }

  def apply(language: String): Interpreter = {
    language match {
      case "scala" => return new Scala
      case "potigol" => return new Potigol
      case "ruby" => return new Ruby
    }
  }
}
