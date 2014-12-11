package flocker.actor

import akka.actor.ActorSystem
import akka.testkit.TestKit
import flocker.actor.ParsingActor.TweetsQuery
import flocker.model.Trigram
import flocker.persistence.BigramsRepository
import flocker.testing.mocking.TwitterMocking
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, WordSpecLike, MustMatchers}
import org.mockito.Mockito._
import org.mockito.Matchers._

/**
 * Created by mglvl on 10/24/14.
 */
@RunWith(classOf[JUnitRunner])
class ParsingActorSpec extends TestKit(ActorSystem("ParsingActorSpec"))
with MustMatchers
with WordSpecLike
with BeforeAndAfterAll
with TwitterMocking{

  override def afterAll() = { system.shutdown() }

  "Un ParsingActor" should {

    "Persistir trigramas después de haber recibido tweets" in {
      val repo = mock[BigramsRepository]
      val parsingActor = ParsingActor.actorRef("hmmm",repo, "parsing-actor-test-1")

      val t1 = "RT @SRV18: 2. NEXOS de @ANNCOL2 y TERRORISTAS FARC. Correos de \" Ra\u00fal Reyes\". @_El_Patriota @FBI @TheJusticeDept @FBIMiamiFL http://t.co/oc\u2026"
      val t2 = "Esto es progreso. Inversi\u00f3n. Empleo. Impuestos. Bienes p\u00fablicos de calidad. Cero demagogia. Capitalismo puro. http://t.co/BMPaTNCqsp"

      parsingActor ! TweetsQuery(Stream(t1,t2))

      verify(repo, timeout(200).times(1) ).storeTrigrams(any(classOf[Iterable[Trigram]]))
    }

  }

}
