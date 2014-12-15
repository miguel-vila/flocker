package flocker.actor

import akka.actor._
import akka.persistence.PersistentActor
import flocker.infrastructure.Delay
import flocker.persistence.BigramsRepository
import twitter4j.Twitter
import scala.concurrent.duration._
import scala.collection.immutable.HashMap

/**
 * Created by mglvl on 10/17/14.
 */
class TopicActor(topicId: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration) extends PersistentActor {
  this: UserActorsCreator =>

  import flocker.actor.TopicActor._
  import context._

  val persistenceId = s"topic-$topicId"

  var userActorsRefs: Map[String,TwitterUserActors] = new HashMap()
  var delay: Delay = Delay(200 millis)

  def receiveRecover: Receive = {
    case event: TopicActorEvent => handleEvent(event)
  }

  def handleEvent(event: TopicActorEvent): Unit = event match {
    case NewUsersTrackedEvent(topicId, userScreenNames) => trackNewUsers(userScreenNames)
  }

  val receiveCommand: Receive = trackingUsers

  /**
   * Inicializa los actores extractores
   * @param initialDelay el initialDelay inicial con el que se inicializan los actores de tracking del usuario
   * @param newUserActors la lista de actores de retrieving de un usuario
   * @return el initialDelay final después de haber inicializado todos los actores de tracking
   */
  def startRetrieverActors(initialDelay: Delay, newUserActors: Iterable[(String, TrackingActorRef, ParsingActorRef)]): (Delay,Map[String,TwitterUserActors]) = {
    newUserActors.foldLeft((initialDelay,HashMap()): (Delay,Map[String,TwitterUserActors])){ case ((d,usersActors),(screenName,trackingActor, parsingActor)) =>
      val scheduledQuery = system.scheduler.schedule(d.delay, timeBetweenQueries, trackingActor.ref, TrackingActor.RetrieveNewUserTweets)
      val userActorsRefs = TwitterUserActors(screenName, trackingActor, parsingActor, scheduledQuery)
      (d.next(), usersActors + (screenName -> userActorsRefs))
    }
  }

  def trackNewUsers(notAlreadyTrackedUsernames: Iterable[String]): Unit = {
    val newUserActors = createActorRefsFromScreenNames(notAlreadyTrackedUsernames, repo, twitter)
    val (newDelay, newUserActorsRefs) = startRetrieverActors(delay, newUserActors)
    delay = newDelay
    userActorsRefs ++= newUserActorsRefs
  }

  /**
   * Estado durante el cuál el actor está listo para recibir nuevos 'screenNames' de usuarios para hacerle tracking
   * @return si le llegan nuevos usuarios entonces solicita su tracking y "actualiza" los atributos 'userActorsRefs' y 'initialDelay'
   */
  def trackingUsers: Receive = {
    case TrackUsers(userScreenNames) =>
      val notTracked = userScreenNames.toStream.filterNot(userActorsRefs.isDefinedAt)
      persist(NewUsersTrackedEvent(topicId, notTracked)) { evt =>
        trackNewUsers(evt.userScreenNames)
      }
  }

}

object TopicActor {

  /*********************
   * Incoming messages *
   ********************/
  sealed trait TopicActorMessage
  final case class TrackUsers(userScreenNames: Iterable[String]) extends TopicActorMessage

  /**********
   * Events *
    *********/
  sealed trait TopicActorEvent {
    def topicId: String
  }
  final case class NewUsersTrackedEvent(topicId: String, userScreenNames: Iterable[String]) extends TopicActorEvent

  /**
   * Props
   */
  def props(topicId: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration): Props = Props(new TopicActor(topicId, repo, twitter, timeBetweenQueries) with TwitterUserActorsCreator)

  /**
   * ActorRef factory function
   */
  def actorRef(topicId: String, repo: BigramsRepository, twitter: Twitter, timeBetweenQueries: FiniteDuration, actorName: String)(implicit as: ActorSystem): ActorRef = {
    as.actorOf( props(topicId, repo, twitter, timeBetweenQueries), actorName )
  }
}