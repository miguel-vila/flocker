package flocker.persistence

import flocker.model.Token
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.PropSpec

/**
 * Created by mglvl on 11/9/14.
 */
@RunWith(classOf[JUnitRunner])
class TokenParsingSpec extends PropSpec with Checkers {

  import flocker.testing.generator.anyTokenGen
  import TokenParsing.fromString

  implicit val tokenArb = Arbitrary( anyTokenGen )

  property("fromString es la función inversa de .toString") {
    check(forAll { t: Token =>
      t == fromString( t.toString )
    })
  }

}
