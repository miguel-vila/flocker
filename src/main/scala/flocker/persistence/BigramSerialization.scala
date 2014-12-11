package flocker.persistence

import com.redis.serialization.Parse
import flocker.model.Bigram

/**
 * Created by mglvl on 10/24/14.
 */
object BigramSerialization {

  def fromBytes(bytes: Array[Byte]): Bigram = {
    val spl = new String(bytes).split( Bigram.SEPARATOR )
    assert(spl.length==2)
    Bigram(TokenParsing.fromString(spl(0)),TokenParsing.fromString(spl(1)))
  }

  def toByteArray(bigram: Bigram): Array[Byte] = bigram.toString.getBytes

  object BigramParse extends Parse[Bigram](fromBytes)
}