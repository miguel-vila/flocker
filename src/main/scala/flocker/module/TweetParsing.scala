package flocker.module

import flocker.model._

import scala.collection.immutable.Stream.Empty

/**
 * Created by mglvl on 10/21/14.
 */
trait TweetParsing {

  def splitIntoWords(s: String): Stream[Token] = {
    Words.addStartAndEndTokens( s.split("[\\s]").toStream.map(Word.apply) )
  }

}

object TweetParsing extends TweetParsing