package flocker.infrastructure.ops

import flocker.model.Word
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by mglvl on 12/14/14.
 */
class TokenStreamOpsSpec extends FlatSpec with Matchers with ToTokenStreamOps {

  "Un TokenStreamOps" should "concatenar un stream infinito hasta que se tengan 140 caracteres o menos" in {
    val tokens1 = Stream.continually(Word("OMG_WTF"))
    tokens1.concatenateToLessThan140Chars() should equal ("OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF OMG_WTF")
  }

  it should "concatenar un stream finito" in {
    val tokens1 = Stream("ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL","ABC","DEF","GHI","JKL").map(Word.apply)
    tokens1.concatenateToLessThan140Chars() should equal ("ABC DEF GHI JKL ABC DEF GHI JKL ABC DEF GHI JKL ABC DEF GHI JKL ABC DEF GHI JKL ABC DEF GHI JKL ABC DEF GHI JKL")
    val tokens2 = Stream("ABC","DEF","GHI").map(Word.apply)
    tokens2.concatenateToLessThan140Chars() should equal("ABC DEF GHI")
  }

}
