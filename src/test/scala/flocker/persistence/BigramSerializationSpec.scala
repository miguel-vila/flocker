package flocker.persistence

import flocker.model.Bigram
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
class BigramSerializationSpec extends PropSpec with Checkers {

  import flocker.testing.generator.bigramGen
  import BigramSerialization.{ toByteArray , fromBytes }

  implicit val bigramArb = Arbitrary( bigramGen )

  property("fromBytes es la función inversa de toByteArray") {
    check(forAll { b: Bigram =>
      b == fromBytes( toByteArray(b) )
    })
  }

}