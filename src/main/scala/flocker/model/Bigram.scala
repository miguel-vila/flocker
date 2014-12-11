package flocker.model

/**
 * Created by mglvl on 10/19/14.
 */
case class Bigram(firstToken: Token, secondToken: Token) {
  def nextBigram(nextToken: Token): Bigram = Bigram(secondToken, nextToken)

  override def toString = s"${firstToken.toString}${Bigram.SEPARATOR}${secondToken.toString}"
}

object Bigram {
  val SEPARATOR = "_"

  def wordBigram(first: String, second: String): Bigram = {
    assert(!first.contains("\\s"))
    assert(!second.contains("\\s"))
    Bigram(Word(first), Word(second))
  }
}