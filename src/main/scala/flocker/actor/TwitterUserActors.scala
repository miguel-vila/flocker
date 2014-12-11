package flocker.actor

import akka.actor.{Cancellable, ActorSystem}
import flocker.persistence.BigramsRepository
import twitter4j.Twitter

/**
 * Created by mglvl on 10/19/14.
 */
case class TwitterUserActors(screenName: String, retriever: TrackingActorRef, parser: ParsingActorRef, scheduledTweetsQueries: Cancellable)

trait TwitterUserActorsCreator extends UserActorsCreator {
  def createActorsRefs(screenName: String, repo: BigramsRepository, twitter: Twitter)(implicit as: ActorSystem): (TrackingActorRef, ParsingActorRef) = {
    val parsingActor = ParsingActor.actorRef(screenName, repo, s"$screenName-parsing-actor")
    val trackingActor = TrackingActor.actorRef(screenName, twitter, s"$screenName-tracking-actor")
    (trackingActor, parsingActor)
  }

}