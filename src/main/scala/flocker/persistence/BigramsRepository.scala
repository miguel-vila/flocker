package flocker.persistence

import com.redis.RedisClient
import flocker.model.{Trigram, Bigram, Token}

/**
 * Created by mglvl on 10/19/14.
 */
class BigramsRepository(client: RedisClient) {
  import BigramSerialization.{ BigramParse }

  private[persistence] def lrangeStringValues(b: Bigram, start: Int, end: Int): Option[List[Token]] = {
    client.lrange( b.toString, start, end ).map(_.flatten.map(TokenParsing.fromString))
  }

  def getTokens(bigram: Bigram): Option[List[Token]] = lrangeStringValues(bigram, 0, -1)

  def putToken(bigram: Bigram, token: Token): Option[Long] = client.lpush( bigram.toString, token.toString)

  def storeTrigrams(trigrams: Iterable[Trigram]): Iterable[(Bigram,Option[Long])] = {
    trigrams.map { t =>
      val (bigram,word) = t.decomposeInBigramAndToken
      (bigram, putToken(bigram, word))
    }
  }

  def randomBigram(): Bigram = client.randomkey(BigramParse).get

}

object BigramsRepository {
  def apply(host: String, port: Int)(dbId: Int) = {
    new BigramsRepository( new RedisClient(host, port, dbId) )
  }
}