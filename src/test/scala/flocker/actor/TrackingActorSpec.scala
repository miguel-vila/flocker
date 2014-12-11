package flocker.actor

import akka.testkit.{ImplicitSender, TestProbe, TestKit}
import flocker.actor.ParsingActor.TweetsQuery
import flocker.testing.mocking.TwitterMocking
import org.scalatest._
import org.mockito.Mockito._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.concurrent.duration._

/**
 * Created by mglvl on 10/23/14.
 */
@RunWith(classOf[JUnitRunner])
class TrackingActorSpec extends PersistentActorSpec( "TrackingActorSpec" )
with ImplicitSender
with MustMatchers
with WordSpecLike
with BeforeAndAfterAll
with TwitterMocking
{

  override def beforeAll() = {
    mockPersistence.startMongo()
  }

  override def afterAll() = {
    system.shutdown()
    val duration = 10.seconds
    system.awaitTermination(duration)
    mockPersistence.stopMongo()
  }

  "Un TrackingActor" should {


    "consulta los tweets del usuario después de que se inicializa" in {
      val twitter = mockTwitterWithSomeStatuses()

      val userName = "hmmmm2"

      val trackingActor = TrackingActor.actorRef(userName, twitter, "test-tracking-actor-1" )

      trackingActor ! TrackingActor.RetrieveNewUserTweets
      verify(twitter, timeout(200).times(1)).getUserTimeline(userName)
    }

    "enviar un mensaje con los tweets a sus watchers" in {
      val twitter = mockTwitterWithSomeStatuses()
      val testWatcher1 = TestProbe()
      val testWatcher2 = TestProbe()

      val userName = "hmmmm3"
      val trackingActor = TrackingActor.actorRef(userName, twitter, "test-tracking-actor-2" )

      trackingActor ! TrackingActor.AddWatcher(testWatcher1.ref)
      trackingActor ! TrackingActor.AddWatcher(testWatcher2.ref)

      trackingActor ! TrackingActor.RetrieveNewUserTweets
      testWatcher1.expectMsgType[TweetsQuery](200 millis)
      testWatcher2.expectMsgType[TweetsQuery](200 millis)
    }

    "de-suscribe un actor y no le envía mensajes después" in {
      val twitter = mockTwitterWithSomeStatuses()
      val testWatcher1 = TestProbe()
      val testWatcher2 = TestProbe()

      val userName = "hmmmm4"
      val trackingActor = TrackingActor.actorRef(userName, twitter, "test-tracking-actor-3" )

      trackingActor ! TrackingActor.AddWatcher(testWatcher1.ref)
      trackingActor ! TrackingActor.AddWatcher(testWatcher2.ref)

      trackingActor ! TrackingActor.UnsuscribeWatcher(testWatcher2.ref)

      trackingActor ! TrackingActor.RetrieveNewUserTweets
      testWatcher1.expectMsgType[TweetsQuery](200 millis)
      testWatcher2.expectNoMsg(200 millis)
    }

  }

}