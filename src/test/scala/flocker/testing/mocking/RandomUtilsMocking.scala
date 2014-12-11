package flocker.testing.mocking

import flocker.module.{RandomUtilsFunctions, RandomUtils}
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar

/**
 * Created by mglvl on 10/25/14.
 */
trait RandomUtilsMocking extends MockitoSugar {

  def randomUtilsMockWithRandomAnswers[A](randomAnswers: Map[List[A],List[A]]): RandomUtilsFunctions = {
    val random = mock[RandomUtilsFunctions]
    randomAnswers.foreach{ case (elements, randomAnswers) =>
      when(random.selectRandomElement(elements)).thenAnswer(new Answer[A] {
        var i = 0
        override def answer(invocation: InvocationOnMock): A = {
          val response = randomAnswers(i)
          i+=1
          response
        }
      })
    }
    random
  }

}
