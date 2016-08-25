package views

/**
  * Created by weizhang on 8/25/16.
  */
object Helpers {
  def displayAccountNumber(s: String) = {
    s.split("(?<=\\G.{4})").toList.mkString(".")
  }
}
