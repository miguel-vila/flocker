package flocker.actor

import akka.actor.{Props, ActorSystem}
import akka.testkit.TestProbe
import flocker.module.RandomTextGenerator
import flocker.persistence.BigramsRepository
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import twitter4j.Twitter
import scala.collection.mutable
import scala.concurrent.duration._
import org.mockito.Mockito._

/**
 * Created by mglvl on 10/22/14.
 */
@RunWith(classOf[JUnitRunner])
class TopicActorSpec extends PersistentActorSpec( "TopicActorSpec" )
with Matchers
//with MustMatchers
with WordSpecLike
with BeforeAndAfterAll
with MockitoSugar
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

  def getTopicActor(topicId: String, actorId: String): (TopicActorSubactors,TopicActorRef) = {
    val repo = mock[BigramsRepository]
    val twitter = mock[Twitter]
    val topicActorSubactors = new TopicActorSubactors

    val topicActor = topicActorSubactors.createTestTopicActor(topicId, repo, twitter, 30 minutes, actorId)
    (topicActorSubactors, topicActor)
  }

  def getTopicActorAndGeneratingRandomText(topicId: String, randomText: String, twitter: Twitter, actorId: String): (TopicActorSubactors,TopicActorRef) = {
    val repo = mock[BigramsRepository]
    val topicActorSubactors = new TopicActorSubactors

    val topicActor = topicActorSubactors.createTestTopicActorGeneratingRandomText(topicId, randomText, repo, twitter, 30 minutes, actorId)
    (topicActorSubactors, topicActor)
  }

  "Un TopicActor" should {

    "enviar mensajes de consulta a los actores de extracción" in {
      val (topicActorSubactors, topicActor) = getTopicActor("topic-1", "Test-Topic-Actor-1")

      val users = List("a","b","c")
      topicActor ! TopicActor.TrackUsers(users)

      awaitAssert({
        topicActorSubactors.actors.size should equal (3)
        topicActorSubactors.allTrackingProbes.foreach { testProbe =>
          testProbe.expectMsg(5 seconds, TrackingActor.RetrieveNewUserTweets)
        }
      }, 5 seconds)
    }

    "enviar mensajes de consulta a los nuevos actores de extracción" in {
      val (topicActorSubactors, topicActor) = getTopicActor("topic-2", "Test-Topic-Actor-2")

      val users1 = List("a","b","c")
      topicActor ! TopicActor.TrackUsers(users1)

      awaitAssert({
        topicActorSubactors.actors.size should equal (3)
        topicActorSubactors.allTrackingProbes.foreach { testProbe =>
          testProbe.expectMsg(5 seconds, TrackingActor.RetrieveNewUserTweets)
        }
      }, 5 seconds)

      val users2 = List("c","d")

      topicActor ! TopicActor.TrackUsers(users2)

      val newUsers = users2.filterNot(users1.contains)
      val usersAlreadyTracked = users2.filter(users1.contains)
      awaitAssert({
        topicActorSubactors.actors.size should equal (users1.size + newUsers.size)
        topicActorSubactors.trackingProbesWithUserNames(newUsers).foreach { testProbe =>
          testProbe.expectMsg(5 seconds, TrackingActor.RetrieveNewUserTweets)
        }
        topicActorSubactors.trackingProbesWithUserNames(usersAlreadyTracked).foreach { testProbe =>
          testProbe.expectNoMsg(5 seconds)
        }
      }, 5 seconds)
    }

    "publicar tweets aleatorios cuando se lo solicita" in {
      val randomText = "raaaandom"
      val twitter = mock[Twitter]
      val (_, topicActor) = getTopicActorAndGeneratingRandomText("topic-3", randomText, twitter, "Test-Topic-Actor-3")
      topicActor ! TopicActor.PublishRandomTweet
      awaitAssert({
        verify(twitter,times(1)).updateStatus(randomText)
      }, 1 second)
    }
  }

}

/**
 * Clase utilitaria para poder probar un TopicActor
 * Permite reemplazar el comportamiento de un TopicActor para que no instancie
 * actores de tracking y parsing de verdad sino TestProbes y permite acumular
 * esas instancias en un HashMap
 */
class TopicActorSubactors extends MockitoSugar {

  def propsReturningRandomText(topicId: String, randomText: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration): Props = Props(new TopicActor(topicId, repo, twitter, timeBetweenQueries) with TestUserActorsCreator {
    override val randomTextGen: RandomTextGenerator = mock[RandomTextGenerator]
    when(randomTextGen.generateRandomText()).thenReturn(randomText)
  })

  def props(topicId: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration): Props = Props(new TopicActor(topicId, repo, twitter, timeBetweenQueries) with TestUserActorsCreator {
    override val randomTextGen: RandomTextGenerator = mock[RandomTextGenerator]
  })

  def createTestTopicActor(topicId: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration, actorName: String)(implicit as: ActorSystem): TopicActorRef = {
    new TopicActorRef( as.actorOf( props(topicId, repo, twitter, timeBetweenQueries), actorName ) )
  }

  def createTestTopicActorGeneratingRandomText(topicId: String, randomText: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration, actorName: String)(implicit as: ActorSystem): TopicActorRef = {
    new TopicActorRef( as.actorOf( propsReturningRandomText(topicId, randomText, repo, twitter, timeBetweenQueries), actorName ) )
  }

  case class UserActorProbes(trackingProbe: TestProbe, parsingProbe: TestProbe)

  val actors: mutable.Map[String, UserActorProbes] = new mutable.HashMap()

  def allTrackingProbes = actors.values.map(_.trackingProbe)

  def allParsingProbes = actors.values.map(_.parsingProbe)

  def trackingProbesWithUserNames(usernames: Iterable[String]): Iterable[TestProbe] = {
    usernames.map( username => actors(username).trackingProbe )
  }

  /**
   * Implementación de UserActorsCreator que crea sub
   */
  trait TestUserActorsCreator extends UserActorsCreator {

    def createActorsRefs(screenName: String, repo: BigramsRepository, twitter: Twitter)(implicit as: ActorSystem): (TrackingActorRef, ParsingActorRef) = {
      val trackingProbe = TestProbe()
      val trackingActor = new TrackingActorRef(trackingProbe.ref)
      val parsingProbe = TestProbe()
      val parsingActor = new ParsingActorRef(parsingProbe.ref)

      actors.put(screenName, UserActorProbes(trackingProbe,parsingProbe))
      (trackingActor, parsingActor)
    }
  }
}