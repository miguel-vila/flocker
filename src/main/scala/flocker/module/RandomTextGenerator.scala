package flocker.module

import flocker.model.{Word, Words, Token, Bigram}
import flocker.persistence.BigramsRepository

import scala.collection.immutable.Stream.Empty
import scalaz.std.stream

/**
 * Created by mglvl on 10/24/14.
 */
case class RandomTextGenerator(repo: BigramsRepository) {

  val random: RandomUtilsFunctions = RandomUtils //Para podear testear con mocks

  def generateRandomText(): Stream[Token] = {
    generateRandomTextStartingWith( repo.randomBigram() )
  }

  private def generateRandomTextStartingWith(bigramOpt: Option[Bigram]): Stream[Token] = {
    val generated = for {
      bigram <- bigramOpt
      randomWordAfterBigram <- selectRandomTokenFromBigram(bigram)
      nextBigram = bigram.nextBigram( randomWordAfterBigram )
    } yield bigram.firstToken #:: generateRandomTextStartingWith(Some(nextBigram))

    generated getOrElse Empty
  }

  def selectRandomTokenFromBigram(bigram: Bigram): Option[Token] = {
    for {
      words <- repo.getTokens(bigram)
    } yield random.selectRandomElement(words)
  }
}
