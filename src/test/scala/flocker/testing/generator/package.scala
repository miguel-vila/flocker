package flocker.testing

import flocker.model._
import org.scalacheck.Gen

/**
 * Created by mglvl on 11/9/14.
 */
package object generator {

  val wordGen: Gen[Word] = Gen.alphaStr.suchThat(_.length>0).map(s => Word(s))

  val anyTokenGen: Gen[Token] = Gen.oneOf( wordGen,  Gen.const(BeforePhraseStart), Gen.const(AfterPhraseEnd) )

  val bigramGen: Gen[Bigram] =
    for {
      t1 <- Gen.oneOf( wordGen, Gen.const(BeforePhraseStart))
      t2 <- if(t1 == BeforePhraseStart) {
        wordGen
      } else {
        Gen.oneOf( wordGen, Gen.const(AfterPhraseEnd))
      }
    } yield Bigram(t1, t2)

}
