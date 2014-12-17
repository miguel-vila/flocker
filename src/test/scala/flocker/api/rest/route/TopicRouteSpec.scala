package flocker.api.rest.route

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import com.typesafe.config.ConfigFactory
import de.flapdoodle.embed.process.runtime.Network
import flocker.model.Topic
import flocker.persistence.WithMockPersistence
import spray.http.StatusCodes.Created
import flocker.api.rest.route.WebObjects.CreateTopic
import org.scalatest.{Ignore, BeforeAndAfterEach, WordSpec, Matchers}
import spray.testkit.ScalatestRouteTest
import flocker.api.rest.marshalling.Json4sProtocol._
import scala.concurrent.duration._

/**
 * Created by mglvl on 12/16/14.
 */
@Ignore
class TopicRouteSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with WithMockPersistence with TopicRoute {

  override def beforeAll() = {
    mockPersistence.startMongo()
  }

  override def afterAll() = {
    system.shutdown()
    val duration = 10.seconds
    system.awaitTermination(duration)
    mockPersistence.stopMongo()
  }

  val freePort = TopicRouteSpecConfig.freePort
  val config = TopicRouteSpecConfig.config(freePort)

  override protected def createActorSystem(): ActorSystem = {
    ActorSystem("TopicRouteSpec", config)
  }

  val testKit = new TestKit(system)

  val actorRefFactory = system

  lazy val uri = MongoClientURI(config.getString("topics-view.mongo-url"))
  lazy val client =  MongoClient(uri)
  lazy val db = client(uri.database.get)
  lazy val coll = db(uri.collection.get)

  def awaitTopicObjectToBe(topicId: String, expected: Topic, msg: String): Unit = {
    testKit.awaitCond({
      val topicBSON = coll.findOne( MongoDBObject("topic_id" -> topicId) )
      topicBSON match {
        case None => false
        case Some(bson) =>
          grater[Topic].asObject(bson) == expected
      }
    }, 5 seconds, 500 millis, msg)
  }

  override def beforeEach() = {
    coll.drop()
  }

  "La ruta de topics" should {

    "Aceptar la creación de un topic" in {

      val createTopic = CreateTopic("my topic","OAuthConsumerKey", "OAuthConsumerSecret", "OAuthAccessToken", "OAuthAccessTokenSecret")
      Post("/topics", createTopic) ~> topicRoute ~> check {
        status should equal (Created)
        responseAs[CreateTopic] should equal (createTopic)
        awaitTopicObjectToBe(createTopic.topicId, Topic(createTopic.topicId), "El topic no se persistió")
      }

    }

  }
}

object TopicRouteSpecConfig {
  def config(port: Int) = ConfigFactory.parseString(
    s"""
      |akka.persistence.journal.plugin = "casbah-journal"
      |akka.persistence.snapshot-store.plugin = "casbah-snapshot-store"
      |akka.persistence.journal.max-deletion-batch-size = 3
      |akka.persistence.publish-plugin-commands = on
      |akka.persistence.publish-confirmations = on
      |akka.persistence.view.auto-update-interval = 1s
      |casbah-journal.mongo-journal-url = "mongodb://localhost:$port/store.messages"
      |casbah-journal.mongo-journal-write-concern = "acknowledged"
      |casbah-journal.mongo-journal-write-concern-timeout = 10000
      |casbah-snapshot-store.mongo-snapshot-url = "mongodb://localhost:$port/store.snapshots"
      |casbah-snapshot-store.mongo-snapshot-write-concern = "acknowledged"
      |casbah-snapshot-store.mongo-snapshot-write-concern-timeout = 10000
      |topics-view.mongo-url = "mongodb://localhost:$port/hr2.topics"
      |topics-view.channel = "topics-channel"
      |topics-view.destination = "topics-destination"
    """.stripMargin)

  lazy val freePort = Network.getFreeServerPort
}