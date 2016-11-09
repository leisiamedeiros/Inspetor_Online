package interpretadores

import sys.process._
import java.io.{ File, PrintWriter }

import scala.language.postfixOps

trait Interpretador {

  def process_stream(stream: Stream[String]): String = {
    var resultado = ""
    val linhas = stream.toIterator
    var linha = linhas.next
    while (linhas.hasNext) {
      resultado += linha + "\n"
      linha = linhas.next
    }
    resultado += linha
    return resultado
  }

  def interpretar(codigo: String, entrada: Option[String]): Option[String] = {
    val arquivoCodigo: File = File.createTempFile("codigo", ".tmp")
    var pw = new PrintWriter(arquivoCodigo)
    pw.write(codigo)
    pw.close
    var saida: Option[String] = None
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
    saida = Some(this.process_stream(saida_stream))
    return saida
  }

  def executar(caminho_codigo: String): Stream[String]
  def executar(caminho_codigo: String, entrada: File): Stream[String]

}

object InterpretadorFabrica {

  private class Scala extends Interpretador {
    override def executar(caminho_codigo: String) = f"scala ${caminho_codigo}" lineStream_!
    override def executar(caminho_codigo: String, entrada: File) = f"scala ${caminho_codigo}" #< entrada lineStream_!
  }

  private class Potigol extends Interpretador {
    override def executar(caminho_codigo: String) = f"java -jar potigol.jar ${caminho_codigo}" lineStream_!
    override def executar(caminho_codigo: String, entrada: File) = f"java -jar potigol.jar ${caminho_codigo}" #< entrada lineStream_!
  }

  private class Ruby extends Interpretador {
    override def executar(caminho_codigo: String) = f"ruby ${caminho_codigo}" lineStream_!
    override def executar(caminho_codigo: String, entrada: File) = f"ruby ${caminho_codigo}" #< entrada lineStream_!
  }

  def apply(language: String): Interpretador = {
    language match {
      case "scala" => return new Scala
      case "potigol" => return new Potigol
      case "ruby" => return new Ruby
    }
  }
}
