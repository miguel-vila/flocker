package flocker.actor

import akka.actor.ActorSystem
import flocker.persistence.BigramsRepository
import twitter4j.Twitter

/**
 * Created by mglvl on 11/4/14.
 * Interface para poder testear TopicActor
 */
trait UserActorsCreator {

  def createActorsRefs(screenName: String, repo: BigramsRepository, twitter: Twitter)(implicit as: ActorSystem): (TrackingActorRef, ParsingActorRef)

  def createActorRefsFromScreenNames(screenNames: Iterable[String], repo: BigramsRepository, twitter: Twitter)(implicit as: ActorSystem): Iterable[(String, TrackingActorRef, ParsingActorRef)] = {
    for {
      screenName <- screenNames
      (trackingActor, parsingActor) = createActorsRefs(screenName,repo,twitter)
    } yield (screenName, trackingActor, parsingActor)
  }

}