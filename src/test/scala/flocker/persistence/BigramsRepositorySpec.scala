package flocker.persistence

import com.redis.RedisClient
import flocker.model.{Bigram, Word}
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import redis.embedded.RedisServer

/**
 * Created by mglvl on 11/9/14.
 */
@RunWith(classOf[JUnitRunner])
class BigramsRepositorySpec extends WordSpec
with Matchers
with BeforeAndAfterAll
with BeforeAndAfterEach {
/*
  val dbId = 0
  val port = 6500
  val redisServer = new RedisServer(port)
  var redisClient: RedisClient = _

  override def beforeAll() = {
    redisServer.start()
  }

  override def afterAll() = {
    redisClient.disconnect
    redisServer.stop()
  }

  override def beforeEach() = {
    redisClient = new RedisClient("localhost", port = port, database = dbId)
    redisClient.flushdb
  }

  def newBigramsRepo(): BigramsRepository = {
    new BigramsRepository(redisClient)
  }

  "Un BigramsRepository" should {

    "persiste los bigramas" in {
      val bigramsRepo = newBigramsRepo()

      bigramsRepo.putToken(Bigram.wordBigram("a","b"),Word("c"))
      bigramsRepo.putToken(Bigram.wordBigram("a","b"),Word("d"))
      bigramsRepo.putToken(Bigram.wordBigram("a","b"),Word("e"))

      bigramsRepo.getTokens(Bigram.wordBigram("a","b")) should equal (Some(List("c","d","e")))
    }

  }
*/
}
