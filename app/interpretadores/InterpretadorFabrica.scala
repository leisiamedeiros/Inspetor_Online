package interpretadores

import java.io.{ File, PrintWriter }
import scala.language.postfixOps

import sys.process.stringToProcess

trait Interpretador {

  def process_stream(stream: Stream[String]): String = stream.mkString("\n")

  def interpretar(codigo: String, entrada: Option[String]): Option[String] = {
    val arquivoCodigo: File = File.createTempFile("codigo", ".tmp")
    var pw = new PrintWriter(arquivoCodigo)
    pw.write(codigo)
    pw.close
    var saida_stream: Stream[String] = null
    entrada match {
      case Some(e) => {
        val arquivoEntrada = File.createTempFile("entrada", ".tmp")
        pw = new PrintWriter(arquivoEntrada)
        pw.write(e)
        pw.close
        try {
          saida_stream = this.executar(arquivoCodigo.getPath(), arquivoEntrada)
        }
        finally {
          arquivoCodigo.delete()
          arquivoEntrada.delete()
        }
      }
      case None => {
        try {
          saida_stream = this.executar(arquivoCodigo.getPath())
        }
        finally {
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

  private object Scala extends Interpretador {
    override val exec = "scala"
  }

  private object Potigol extends Interpretador {
    override val exec = "java -jar potigol.jar"
  }

  private object Ruby extends Interpretador {
    override val exec = "ruby"
  }

  def apply(language: String): Interpretador = language match {
    case "scala"   => Scala
    case "potigol" => Potigol
    case "ruby"    => Ruby
  }
}
