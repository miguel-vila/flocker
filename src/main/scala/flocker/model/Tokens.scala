package flocker.model

import flocker.module.TweetParsing

/**
 * Created by mglvl on 10/21/14.
 */
case class Tokens protected[model](tokens: Stream[Token]) {

  lazy val words: Stream[Word] = Words.filterWords(tokens)

  def filterOutInvalidWords: Tokens = {
    val newWords = words
      .map(_.text.replaceAll("\"",""))
      .filter(_.length>0)
      .filterNot(_.startsWith("RT"))
      .filterNot(_.startsWith("http"))
      .filterNot(_ == "\"")
      .map(Word.apply)
    Tokens( Words.addStartAndEndTokens( newWords ) )
  }

  private[model] def trigrams: Stream[Trigram] = {
    val tokens2 = tokens.tail
    val tokens3 = tokens2.tail
    (tokens zip tokens2 zip tokens3).map(Trigram.fromTuple)
  }

  def validTrigrams: Stream[Trigram] = {
    trigrams
      .filterNot(_.splitsAPhrase)
      .filterNot(_.containsAMention)
  }

}

object Tokens {

  def apply(s: String): Tokens = Tokens( TweetParsing.splitIntoWords(s) )

}