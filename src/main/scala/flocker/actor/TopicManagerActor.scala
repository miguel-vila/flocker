package flocker.actor

import akka.actor.{ActorRef, Actor}
import flocker.api.rest.route.WebObjects.CreateTopic
import flocker.infrastructure.Twitter
import flocker.persistence.BigramsRepository
import scala.concurrent.duration._

/**
 * Created by mglvl on 12/16/14.
 */
class TopicManagerActor(
                         dbHost: String,
                         dbPort: Int) extends Actor {

  val createBigramRepo: Int => BigramsRepository = BigramsRepository(dbHost, dbPort)

  /**
   * Start dbId in 0
   */
  var dbId = 0

  def getOrCreateTopicActor(createTopic: CreateTopic): ActorRef = {
    context.child(createTopic.topicId) getOrElse {
      createTopicActor(createTopic)
    }
  }

  def createTopicActor(createTopic: CreateTopic): ActorRef = {
    val bigramRepo = createBigramRepo(dbId)
    val twitter = Twitter(createTopic.OAuthConsumerKey, createTopic.OAuthConsumerSecret, createTopic.OAuthAccessToken, createTopic.OAuthAccessTokenSecret)
    dbId = dbId+1
    context.actorOf(TopicActor.props(createTopic.topicId, bigramRepo, twitter, 30 minutes))
  }

  def receive = {
    case createTopic: CreateTopic =>
  }
}
