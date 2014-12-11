package flocker.actor

import java.util.Date

import akka.persistence.{SnapshotOffer, PersistentActor}
import flocker.actor.ParsingActor.TweetsQuery
import flocker.infrastructure.ops.response_list._
import flocker.infrastructure.ops.status_stream._
import akka.actor._
import twitter4j.{Status, Twitter}
import scala.collection.mutable

/**
  * Created by mglvl on 10/17/14.
 */
class TrackingActor(userScreenName: String, twitter: Twitter) extends PersistentActor {
  import TrackingActor._
  import context._

  val persistenceId = s"tracking-actor-$userScreenName"

  var lastTweetDate: Option[Date] = None
  var watchers: mutable.Set[ActorRef] = mutable.HashSet.empty

  val receiveRecover: Receive = {
    case event: TrackingActorEvent => handleEvent(event)
    case SnapshotOffer(_,Snapshot(date,snapshotWatchers)) =>
      lastTweetDate = Some(date)
      watchers = snapshotWatchers
  }

  def handleEvent(event: TrackingActorEvent) = {
    event match {
      case LastTweetDateUpdatedEvent(date) => lastTweetDate = Some(date)
      case WatcherAdded(watcher) => addWatcher( watcher )
      case WatcherUnsuscribed(watcher) => removeWatcher( watcher )
    }
  }

  def addWatcher(watcher: ActorRef): Unit = {
    watchers += watcher
  }

  def removeWatcher(watcher: ActorRef): Unit = {
    watchers -= watcher
  }

  def addWatcherAndPersistEvent(watcher: ActorRef): Unit = {
    persist(WatcherAdded(watcher)) { event =>
      addWatcher( event.watcher )
    }
  }

  def removeWatcherAndPersistEvent(watcher: ActorRef): Unit = {
    persist(WatcherUnsuscribed(watcher)) { event =>
      removeWatcher( event.watcher )
    }
  }

  val receiveCommand: Receive = readyToRetrieveNewTweets

  /**
   * Consulta los tweets del usuario
   * filtra los retweets y los tweets viejos
   * y los manda al actor de parseo
   */
  def readyToRetrieveNewTweets: Receive = {
    case RetrieveNewUserTweets =>
      val tweets = retrieveTweets().filterOutRetweeted
      val (filteredTweets, newMostRecentTweetDate) = tweets.filterByMostRecentDate( lastTweetDate )
      persist(LastTweetDateUpdatedEvent(newMostRecentTweetDate)) { evt =>
        lastTweetDate = Some( evt.date )
        val tweetsText = filteredTweets.map(_.getText)
        watchers foreach { _ ! TweetsQuery( tweetsText ) }
      }
    case AddWatcher(watcher) => addWatcherAndPersistEvent(watcher)
    case UnsuscribeWatcher(watcher) => removeWatcherAndPersistEvent(watcher)
  }

  /**
   * Consulta los tweets del usuario
   */
  def retrieveTweets(): Stream[Status] = twitter.getUserTimeline(userScreenName).toStream

}

object TrackingActor {

  /**********************
    * Incoming messages *
    *********************/
  sealed trait TrackingActorMessage
  final case class AddWatcher(ref: ActorRef) extends TrackingActorMessage
  final case class UnsuscribeWatcher(ref: ActorRef) extends TrackingActorMessage
  final case object RetrieveNewUserTweets extends TrackingActorMessage

  /**********
   * Events *
   **********/
  sealed trait TrackingActorEvent
  final case class LastTweetDateUpdatedEvent(date: Date) extends TrackingActorEvent
  final case class WatcherAdded(watcher: ActorRef) extends TrackingActorEvent
  final case class WatcherUnsuscribed(watcher: ActorRef) extends TrackingActorEvent

  case class Snapshot(lastTweetDate: Date, watchers: mutable.Set[ActorRef])

  /**
   * Props
   */
  def props(userScreenName: String, twitter: Twitter): Props = Props(new TrackingActor(userScreenName, twitter))

  /**
   * ActorRef factory function
   */
  def actorRef(userScreenName: String, twitter: Twitter, actorName: String)(implicit as: ActorSystem): TrackingActorRef = {
    val actorRef = as.actorOf( props(userScreenName, twitter), actorName )
    new TrackingActorRef( actorRef )
  }
}