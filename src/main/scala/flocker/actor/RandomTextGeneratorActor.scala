package flocker.actor

import akka.actor.{ActorRef, ActorSystem, Props, Actor}
import flocker.module.RandomTextGenerator

/**
 * Created by mglvl on 10/26/14.
 */
class RandomTextGeneratorActor(txtGen: RandomTextGenerator) extends Actor {
  import RandomTextGeneratorActor._

  def receive = {
    case GenerateRandomText =>
      sender ! txtGen.generateRandomText()
  }

}

object RandomTextGeneratorActor {
  /*********************
    * Incoming messages *
    ********************/
  sealed trait RandomTextGeneratorActorMessage
  final case object GenerateRandomText extends RandomTextGeneratorActorMessage
  
  def props(txtGen: RandomTextGenerator): Props = Props( new RandomTextGeneratorActor(txtGen) )
  
  def actorRef(txtGen: RandomTextGenerator, actorName: String)(implicit as: ActorSystem): ActorRef = {
    as.actorOf(props(txtGen), actorName)
  }
  
}
