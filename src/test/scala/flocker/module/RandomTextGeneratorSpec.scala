package flocker.module

import flocker.model.{Token, AfterPhraseEnd, Words, Bigram}
import flocker.testing.mocking.{RandomUtilsMocking, BigramsRepoMockBuilder}
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by mglvl on 10/25/14.
 */
class RandomTextGeneratorSpec
  extends FlatSpec
  with Matchers
  with RandomUtilsMocking {

  "Un RandomTextGenerator" should "generar texto aleatorio usando los datos que están en el repositorio" in {
    val data = Map(
      Bigram.wordBigram("The", "quick") -> Words.asList("brown"),
      Bigram.wordBigram("brown", "fox") -> Words.asList("jumps", "who", "who"),
      Bigram.wordBigram("fox", "jumps") -> Words.asList("over"),
      Bigram.wordBigram("fox", "who") -> Words.asList("is", "is"),
      Bigram.wordBigram("is", "slow") -> Words.asList("jumps"),
      Bigram.wordBigram("jumps", "over") -> Words.asList("the", "the"),
      Bigram.wordBigram("over", "the") -> Words.asList("brown", "brown"),
      Bigram.wordBigram("quick", "brown") -> Words.asList("fox"),
      Bigram.wordBigram("slow", "jumps") -> List(AfterPhraseEnd),
      Bigram.wordBigram("the", "brown") -> Words.asList("fox", "fox"),
      Bigram.wordBigram("who", "is") -> Words.asList("slow", "dead.")
    )
    val mockRepo = BigramsRepoMockBuilder().withData(data).withReturningRandomsInOrder(
      Bigram.wordBigram("brown", "fox"),
      Bigram.wordBigram("jumps", "over"),
      Bigram.wordBigram("the", "brown")
    ).build()

    class RandomTextGeneratorTest extends RandomTextGenerator(mockRepo) {
      override val random = randomUtilsMockWithRandomAnswers[Token](Map(
        Words.asList("brown") -> Words.asList("brown"),
        Words.asList("jumps", "who", "who") -> Words.asList("who"),
        List(AfterPhraseEnd) -> List(AfterPhraseEnd),
        Words.asList("is", "is") -> Words.asList("is"),
        Words.asList("jumps") -> Words.asList("jumps"),
        Words.asList("the","the") -> Words.asList("the"),
        Words.asList("brown","brown") -> Words.asList("brown"),
        Words.asList("fox") -> Words.asList("fox"),
        Words.asList("over") -> Words.asList("over"),
        Words.asList("fox", "fox") -> Words.asList("fox"),
        Words.asList("slow", "dead.") -> Words.asList("slow")
      ))
    }

    val randomTextGenerator = new RandomTextGeneratorTest()
    randomTextGenerator.generateRandomText() should equal ("brown fox who is slow jumps")
  }

}
