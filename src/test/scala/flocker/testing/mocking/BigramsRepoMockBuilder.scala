package flocker.testing.mocking

import flocker.model.{Bigram, Token}
import flocker.persistence.BigramsRepository
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

/**
 * Created by mglvl on 10/25/14.
 */
case class BigramsRepoMockBuilder(private val repo: BigramsRepository = mock(classOf[BigramsRepository])) {

  def withData(data: Map[Bigram,List[Token]]): BigramsRepoMockBuilder = {
    data.foreach{ case (bigram,tokens) =>
      when(repo.getTokens(bigram)).thenReturn(Some(tokens))
    }
    BigramsRepoMockBuilder(repo)
  }

  def withReturningRandomsInOrder(randomBigrams: Bigram*): BigramsRepoMockBuilder = {
    when(repo.randomBigram()).thenAnswer(new Answer[Option[Bigram]] {
      var i = 0
      override def answer(invocation: InvocationOnMock): Option[Bigram] = {
        val response = randomBigrams(i)
        i+=1
        Some(response)
      }
    })
    BigramsRepoMockBuilder(repo)
  }

  def build(): BigramsRepository = repo

}