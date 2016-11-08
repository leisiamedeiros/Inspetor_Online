package interpretadores

import sys.process._
import java.io.{ File, PrintWriter }

import scala.language.postfixOps

trait Interpretador {
  def interpretar(codigo: String, entrada: Option[String]): Option[String] = {
    val arquivoCodigo: File = File.createTempFile("codigo", ".tmp")
    var pw = new PrintWriter(arquivoCodigo)
    pw.write(codigo)
    pw.close
    var saida: Option[String] = None
    entrada match {
      case Some(e) => {
        val arquivoEntrada: File = File.createTempFile("entrada", ".tmp")
        pw = new PrintWriter(arquivoEntrada)
        pw.write(e)
        pw.close
        try {
          saida = Some(this.executar(arquivoCodigo.getPath(), arquivoEntrada.getPath()))
        } finally {
          arquivoCodigo.delete();
          arquivoEntrada.delete();
        }
      }
      case None => {
        try {
          saida = Some(this.executar(arquivoCodigo.getPath()))
        } finally {
          arquivoCodigo.delete();
        }
      }
    }
    return saida
  }

  def executar(caminho_codigo: String): String
  def executar(caminho_codigo: String, caminho_entrada: String): String

}

object InterpretadorFabrica {

  private class Scala extends Interpretador {
    override def executar(caminho_codigo: String) = f"scala ${caminho_codigo}" !!
    override def executar(caminho_codigo: String, caminho_entrada: String) = f"scala ${caminho_codigo} < ${caminho_entrada}" !!
  }

  private class Potigol extends Interpretador {
    override def executar(caminho_codigo: String) = f"java -jar potigol.jar ${caminho_codigo}" !!
    override def executar(caminho_codigo: String, caminho_entrada: String) = f"java -jar potigol.jar ${caminho_codigo} < ${caminho_entrada}" !!
  }

  private class Ruby extends Interpretador {
    override def executar(caminho_codigo: String) = f"ruby ${caminho_codigo}" !!
    override def executar(caminho_codigo: String, caminho_entrada: String) = f"ruby ${caminho_codigo} < ${caminho_entrada}" !!
  }

  def apply(language: String): Interpretador = {
    language match {
      case "scala" => return new Scala
      case "potigol" => return new Potigol
      case "ruby" => return new Ruby
    }
  }
}
