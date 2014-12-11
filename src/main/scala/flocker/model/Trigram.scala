package flocker.model

/**
 * Created by mglvl on 10/21/14.
 */
case class Trigram(t1: Token, t2: Token, t3: Token) {
  assert(!( (t1 == BeforePhraseStart && t2 == AfterPhraseEnd) || (t2 == BeforePhraseStart && t3 == AfterPhraseEnd) ))
  lazy val tokens = Stream(t1,t2,t3)
  lazy val words = Words.filterWords(tokens)

  /**
   * Determina si la lista de palabras parte una frase
   * Es decir si contiene un punto a menos que:
   * * ese punto esté al final
   * * ese punto sirva para numeración. por ej: "2."
   */
  def splitsAPhrase: Boolean = {
    val wordsWithDot = words.zipWithIndex.filter{ case (w,_) => w.text.contains('.') }
    lazy val dotIsAtTheEnd = wordsWithDot.forall{ case (w,i) =>
      w.text.indexOf('.') == w.text.length - 1 && /*El punto está al final de la palabra*/
        i == words.length - 1 /*Es la última palabra*/
    }
    lazy val dotIsForNumeration = wordsWithDot.forall{ case (w,i) =>
      i == 0 && /*solo se permite tener el número de la enumeración al principio*/
        w.text.indexOf('.') == w.text.length - 1 && /*el punto está al final de la palabra*/
        w.text.subSequence(0, w.text.length-1).toString.toList.forall( c => '0'<=c && c<='9' ) /* lo que va antes del punto solo son números */
    }
    !(wordsWithDot.isEmpty || dotIsAtTheEnd || dotIsForNumeration)
  }

  def containsAMention: Boolean = words.exists(_.text.startsWith("@"))

  def decomposeInBigramAndToken: (Bigram,Token) = (Bigram(t1,t2),t3)
}

object Trigram {
  def fromTuple(t: ((Token, Token), Token)): Trigram = Trigram(t._1._1, t._1._2, t._2)
}