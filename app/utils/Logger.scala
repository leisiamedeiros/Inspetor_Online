package utils

trait Logger {
  val logger = play.api.Logger(this.getClass)
}
