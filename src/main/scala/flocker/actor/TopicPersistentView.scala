package flocker.actor

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentView
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import flocker.actor.TopicActor.{RandomTweetPublished, NewUsersTrackedEvent, TopicActorEvent}
import com.mongodb.casbah.Imports.$push

/**
 * Created by mglvl on 11/7/14.
 */
class TopicPersistentView(topicId: String) extends PersistentView {

  override val persistenceId = s"topic-$topicId"
  override val viewId = s"topic-view-$topicId"

  def config = context.system.settings.config.getConfig("topics-view")

  private val uri = MongoClientURI(config.getString("mongo-url"))
  private val client =  MongoClient(uri)
  private val db = client(uri.database.get)
  private val coll = db(uri.collection.get)

  def receive = {
    case topicEvent: TopicActorEvent if isPersistent => handleTopicEvent(topicEvent)
  }

  def handleTopicEvent(topicEvent: TopicActorEvent) = topicEvent match {
    case NewUsersTrackedEvent(topicId, newUserNames) =>
      coll.update(
        MongoDBObject("topic_id" -> topicId),
        $push("usernames").$each(newUserNames.toSeq:_*),
        upsert = true
      )
    case RandomTweetPublished(topicId, tweet) =>
      coll.update(
        MongoDBObject("topic_id" -> topicId),
        $push("tweets").$each(tweet),
        upsert = true
      )
  }

  override def postStop(): Unit = {
    client.close()
    super.postStop()
  }

}

object TopicPersistentView {

  sealed trait TopicPersistentViewMessage

  def props(topicId: String): Props = Props( new TopicPersistentView(topicId) )

  def actorRef(topicId: String, actorName: String)(implicit as: ActorSystem): TopicPersistentViewRef = {
    new TopicPersistentViewRef( as.actorOf(props(topicId), actorName) )
  }

}