package flocker.actor

import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import flocker.module.RandomTextGenerator
import flocker.persistence.BigramsRepository
import flocker.testing.mocking.RandomUtilsMocking
import org.scalatest.WordSpecLike
import scala.concurrent.duration._
import org.mockito.Mockito._

/**
 * Created by mglvl on 12/15/14.
 */
class RandomTextGeneratorActorSpec extends TestKit(ActorSystem("RandomTextGeneratorActorSpec"))
with ImplicitSender
with WordSpecLike
with RandomUtilsMocking
{

  "RandomTextGeneratorActor" should {
    "Responde con un texto aleatoriamente generado cuando se le solicita" in {
      val repoMock = mock[BigramsRepository]
      val txtGenSpy = mock[RandomTextGenerator]

      val randomTxt = "random teeext"
      when(txtGenSpy.generateRandomText).thenReturn(randomTxt)

      val testActor = RandomTextGeneratorActor.actorRef(txtGenSpy,"randomTextGeneratorActorTest1")

      testActor ! RandomTextGeneratorActor.GenerateRandomText

      expectMsg(1 second, randomTxt)
    }
  }

}
