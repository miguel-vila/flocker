package flocker.actor

import akka.actor.Actor
import flocker.module.RandomTextGenerator
import flocker.persistence.BigramsRepository

/**
 * Created by mglvl on 10/26/14.
 */
class RandomTextGeneratorActor(repo: BigramsRepository) extends Actor {
  import RandomTextGeneratorActor._

  val txtGen = RandomTextGenerator(repo)

  def receive = {
    case GenerateRandomText => txtGen.generateRandomText()
  }

}

object RandomTextGeneratorActor {
  /*********************
    * Incoming messages *
    ********************/
  sealed trait RandomTextGeneratorActorMessage
  final case object GenerateRandomText extends RandomTextGeneratorActorMessage
}
