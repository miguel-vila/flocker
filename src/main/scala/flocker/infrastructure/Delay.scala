package flocker.infrastructure

import scala.concurrent.duration._

/**
 * Created by mglvl on 10/19/14.
 */
case class Delay private[infrastructure](initial: FiniteDuration, delay: FiniteDuration, totalTime: FiniteDuration) {
  import Delay._

  def reset(): Delay = apply(initial)

  def next(): Delay = {
    val newTotalTime = totalTime + delay
    if( newTotalTime >= (5 minutes) ) { //@TODO? -> hardwired reset condition
      reset()
    } else {
      Delay(initial, newDelayDuration, newTotalTime)
    }
  }

  private def newDelayDuration: FiniteDuration = 2 * delay //@TODO? hardwired new initialDelay duration

}


object Delay {

  def apply(initial: FiniteDuration): Delay = {
    Delay(initial, initial, 0 millis)
  }

}