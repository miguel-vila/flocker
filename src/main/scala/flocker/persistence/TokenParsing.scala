package flocker.persistence

import flocker.model.{Word, AfterPhraseEnd, BeforePhraseStart, Token}

/**
 * Created by mglvl on 10/24/14.
 */
object TokenParsing {

  def fromString(s: String): Token = {
    s match {
      case Token.BEFORE_TAG => BeforePhraseStart
      case Token.AFTER_TAG => AfterPhraseEnd
      case s => Word(s)
    }
  }

}
