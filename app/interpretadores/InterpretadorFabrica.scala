package interpretadores

import sys.process._
import java.io.{ File, PrintWriter }

import scala.language.postfixOps

trait Interpretador {

  def process_stream(stream: Stream[String]): String = stream.toList.mkString("\n")

  def interpretar(codigo: String, entrada: Option[String]): Option[String] = {
    val arquivoCodigo: File = File.createTempFile("codigo", ".tmp")
    var pw = new PrintWriter(arquivoCodigo)
    pw.write(codigo)
    pw.close
    var saida_stream: Stream[String] = null
    entrada match {
      case Some(e) => {
        val arquivoEntrada: File = File.createTempFile("entrada", ".tmp")
        pw = new PrintWriter(arquivoEntrada)
        pw.write(e)
        pw.close
        try {
          saida_stream = this.executar(arquivoCodigo.getPath(), arquivoEntrada)
        } finally {
          arquivoCodigo.delete()
          arquivoEntrada.delete()
        }
      }
      case None => {
        try {
          saida_stream = this.executar(arquivoCodigo.getPath())
        } finally {
          arquivoCodigo.delete()
        }
      }
    }
    val saida = Some(this.process_stream(saida_stream))
    return saida
  }
  val exec: String
  def executar(caminho_codigo: String): Stream[String] = s"${exec} ${caminho_codigo}" lineStream_!
  def executar(caminho_codigo: String, entrada: File): Stream[String] = s"${exec} ${caminho_codigo}" #< entrada lineStream_!
}

object InterpretadorFabrica {

  private class Scala extends Interpretador {
    val exec = "scala"
  }

  private class Potigol extends Interpretador {
    val exec = "java -jar potigol.jar"
  }

  private class Ruby extends Interpretador {
    val exec = "ruby"
  }

  def apply(language: String): Interpretador = {
    language match {
      case "scala" => return new Scala
      case "potigol" => return new Potigol
      case "ruby" => return new Ruby
    }
  }
}
