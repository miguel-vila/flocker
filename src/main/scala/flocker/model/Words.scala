package flocker.model

import scala.collection.immutable.Stream.Empty

/**
 * Created by mglvl on 10/26/14.
 */
object Words {

  def asStream(w0: String, ws: String*): Stream[Word] = {
    Word(w0) #:: ws.map(Word.apply).toStream
  }

  def asList(w0: String, ws: String*): List[Word] = {
    Word(w0) :: ws.map(Word.apply).toList
  }

  def addStartAndEndTokens(words: Stream[Word]): Stream[Token] = {
    BeforePhraseStart #:: ( words ++ Stream(AfterPhraseEnd))
  }

  def filterWords(tokens: Stream[Token]): Stream[Word] = {
    tokens.foldRight(Empty: Stream[Word]) { (token, acc) =>
      token match {
        case w: Word => w #:: acc
        case _ => acc
      }
    }
  }

}
