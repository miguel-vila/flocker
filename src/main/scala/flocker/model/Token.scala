package flocker.model

/**
 * Created by mglvl on 10/24/14.
 */
sealed trait Token
final case object BeforePhraseStart extends Token {
  override def toString = Token.BEFORE_TAG
}
final case class Word(text: String) extends Token {
  assert(!text.contains("\\s"))
  override def toString = text
}
final case object AfterPhraseEnd extends Token {
  override def toString = Token.AFTER_TAG
}

object Token {
  val BEFORE_TAG = "%BEFORE%"
  val AFTER_TAG = "%AFTER%"
}