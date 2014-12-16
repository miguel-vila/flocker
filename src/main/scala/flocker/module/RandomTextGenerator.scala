package flocker.module

import flocker.model._
import flocker.persistence.BigramsRepository
import flocker.infrastructure.ops.token_stream._
import scala.collection.immutable.Stream.Empty

/**
 * Created by mglvl on 10/24/14.
 */
case class RandomTextGenerator(repo: BigramsRepository) {

  val random: RandomUtilsFunctions = RandomUtils //Para podear testear con mocks

  def generateRandomText(): String = {
    val tokens = generateRandomTextStartingWith( repo.randomBigram() )
    tokens.concatenateToLessThan140Chars()
  }

  private def generateRandomTextStartingWith(bigram: Bigram): Stream[Token] = {
    if(bigram.secondToken == AfterPhraseEnd) {
      Stream(bigram.firstToken)
    } else {
      val generated = for {
        randomWordAfterBigram <- selectRandomTokenFromBigram(bigram)
        nextBigram = bigram.nextBigram( randomWordAfterBigram )
      } yield bigram.firstToken #:: generateRandomTextStartingWith(nextBigram)

      generated getOrElse Empty
    }
  }

  def selectRandomTokenFromBigram(bigram: Bigram): Option[Token] = {
    for {
      words <- repo.getTokens(bigram)
    } yield random.selectRandomElement(words)
  }
}
