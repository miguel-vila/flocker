package flocker.actor

import akka.testkit.ImplicitSender
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.typesafe.config.ConfigFactory
import flocker.model.Topic
import flocker.persistence.BigramsRepository
import org.junit.runner.RunWith
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, BeforeAndAfterAll, WordSpecLike}
import org.scalatest.junit.JUnitRunner
import twitter4j.Twitter
import scala.concurrent.duration._
import de.flapdoodle.embed.process.runtime.Network
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

/**
 * Created by mglvl on 11/7/14.
 */
object TopicPersistentViewSpec {

  def config(port: Int) = ConfigFactory.parseString(
    s"""
      |akka.persistence.journal.plugin = "casbah-journal"
      |akka.persistence.snapshot-store.plugin = "casbah-snapshot-store"
      |akka.persistence.journal.max-deletion-batch-size = 3
      |akka.persistence.publish-plugin-commands = on
      |akka.persistence.publish-confirmations = on
      |akka.persistence.view.auto-update-interval = 1s
      |casbah-journal.mongo-journal-url = "mongodb://localhost:$port/store2.messages"
      |casbah-journal.mongo-journal-write-concern = "acknowledged"
      |casbah-journal.mongo-journal-write-concern-timeout = 10000
      |casbah-snapshot-store.mongo-snapshot-url = "mongodb://localhost:$port/store2.snapshots"
      |casbah-snapshot-store.mongo-snapshot-write-concern = "acknowledged"
      |casbah-snapshot-store.mongo-snapshot-write-concern-timeout = 10000
      |topics-view.mongo-url = "mongodb://localhost:$port/hr2.topics"
      |topics-view.channel = "topics-channel"
      |topics-view.destination = "topics-destination"
    """.stripMargin)

  lazy val freePort = Network.getFreeServerPort
}

@RunWith(classOf[JUnitRunner])
class TopicPersistentViewSpec extends PersistentActorSpec( "TopicPersistentViewSpec" , TopicPersistentViewSpec.freePort, TopicPersistentViewSpec.config(TopicPersistentViewSpec.freePort) )
with ImplicitSender
with Matchers
with WordSpecLike
with BeforeAndAfterAll
with BeforeAndAfterEach
with MockitoSugar
{

  override def beforeAll() = {
    mockPersistence.startMongo()
  }

  override def afterAll() = {
    system.shutdown()
    val duration = 10.seconds
    system.awaitTermination(duration)
    client.close()
    mockPersistence.stopMongo()
  }

  lazy val uri = MongoClientURI(TopicPersistentViewSpec.config(mockPersistence.freePort).getString("topics-view.mongo-url"))
  lazy val client =  MongoClient(uri)
  lazy val db = client(uri.database.get)
  lazy val coll = db(uri.collection.get)

  override def beforeEach() = {
    coll.drop()
  }

  def getTopicActors(topicId: String, persistentActorId: String, persistentViewId: String): (TopicActorRef, TopicPersistentViewRef) = {
    val repo = mock[BigramsRepository]
    val twitter = mock[Twitter]
    val topicActorSubactors = new TopicActorSubactors

    val topicView = TopicPersistentView.actorRef( topicId, persistentViewId )
    val topicActor = topicActorSubactors.createTestTopicActor(topicId, repo, twitter, 2 seconds, persistentActorId)
    (topicActor, topicView)
  }

  def awaitTopicObjectToBe(topicId: String, expected: Topic, msg: String): Unit = {
    awaitCond({
      val topicBSON = coll.findOne( MongoDBObject("topic_id" -> topicId) )
      topicBSON match {
        case None => false
        case Some(bson) =>
          grater[Topic].asObject(bson) == expected
      }
    }, 5 seconds, 500 millis, msg)
  }

  "Un TopicPersistentViewSpec" should {

    "Crea un Topic que no existía cuando se le mandan los primeros users" in {
      val topicId = "topic-1"
      val (topicActor, topicView) = getTopicActors( topicId, "topic-view-1", "test-Topic-Actor-1" )

      val users1 = List("a","b","c")
      topicActor ! TopicActor.TrackUsers(users1)

      val expected = Topic(topicId, users1)

      awaitTopicObjectToBe(topicId, expected, "El Topic no se almacenó")
    }

    "Actualiza un Topic que existía sin usuarios" in {
      val topicId = "topic-2"
      //coll.insert(MongoDBObject("topic_id" -> topicId))
      coll.insert(grater[Topic].asDBObject(Topic(topicId, List()))) //@TODO probar con un MongoDBObject("topic_id" -> topicId) ?
      val (topicActor, topicView) = getTopicActors( topicId, "topic-view-2", "test-Topic-Actor-2" )

      val newUsers = List("a","b","c")
      topicActor ! TopicActor.TrackUsers(newUsers)

      val expected = Topic(topicId, List("a","b","c"))

      awaitTopicObjectToBe(topicId, expected, "El Topic no se actualizó con los usuarios")
    }

    "Actualiza un Topic cuando se le mandan nuevos users" in {
      val topicId = "topic-3"
      coll.insert(grater[Topic].asDBObject(Topic(topicId, List("a","b"))))
      val (topicActor, topicView) = getTopicActors( topicId, "topic-view-3", "test-Topic-Actor-3" )

      val newUsers = List("c","d","e")
      topicActor ! TopicActor.TrackUsers(newUsers)

      val expected = Topic(topicId, List("a","b","c","d","e"))

      awaitTopicObjectToBe(topicId, expected, "El Topic no se actualizó")
    }

    "Actualiza un Topic solamente incluyendo nuevos users" in {
      val topicId = "topic-4"
      val (topicActor, topicView) = getTopicActors( topicId, "topic-view-4", "test-Topic-Actor-4" )

      val users1 = List("a","b","c")
      topicActor ! TopicActor.TrackUsers(users1)

      val users2 = List("b","c","d")
      topicActor ! TopicActor.TrackUsers(users2)

      val expected = Topic(topicId, List("a","b","c","d"))

      awaitTopicObjectToBe(topicId, expected, "El Topic no se actualizó correctamente")
    }
  }

}
