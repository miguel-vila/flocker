package flocker.actor

import akka.actor.{ActorSystem, ActorContext, Props, Actor}
import flocker.infrastructure.ops.status_stream._
import flocker.model.Tokens
import flocker.module.TweetParsing
import flocker.persistence.BigramsRepository
import twitter4j.Status

/**
 * Created by mglvl on 10/18/14.
 */
class ParsingActor(userScreenName: String, repo: BigramsRepository) extends Actor {
  import ParsingActor._

  def receive = {
    case TweetsQuery(tweets) =>
      val tweetsTexts = tweets.map( s => Tokens( s ) )
      val trigrams = for {
        tweetText <- tweetsTexts
        validTrigram <- tweetText.validTrigrams
      } yield validTrigram
      repo.storeTrigrams(trigrams)
  }

}

object ParsingActor {

  /*********************
   * Incoming messages *
   ********************/
  sealed trait ParsingActorMessage
  final case class TweetsQuery(tweets: Stream[String]) extends ParsingActorMessage

  /**
   * Props
   */
  def props(userScreenName: String, repo: BigramsRepository): Props = Props(new ParsingActor(userScreenName, repo))

  /**
   * ActorRef factory function
   */
  def actorRef(userScreenName: String, repo: BigramsRepository, actorName: String)(implicit as: ActorSystem): ParsingActorRef = {
    val actorRef = as.actorOf( props(userScreenName, repo), actorName )
    new ParsingActorRef( actorRef )
  }

}