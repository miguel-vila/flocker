package flocker.infrastructure

import org.scalatest.{Matchers, FlatSpec}
import scala.concurrent.duration._
/**
 * Created by mglvl on 10/22/14.
 */
class DelaySpec extends FlatSpec with Matchers {

  "Un Delay" should "crecer la duración del initialDelay exponencialmente" in {
    val d0 = Delay(1 second)
    d0.delay should equal (1 second)
    d0.totalTime should equal (0 seconds)
    val d1 = d0.next()
    d1.delay should equal (2 seconds)
    d1.totalTime should equal (1 seconds)
    val d2 = d1.next()
    d2.delay should equal (4 seconds)
    d2.totalTime should equal (3 seconds)
    val d3 = d2.next()
    d3.delay should equal (8 seconds)
    d3.totalTime should equal (7 seconds)
  }

  it should s"resetearse cuando se alcanzan mas de 5 minutos" in {
    val d0 = Delay(1 minute)
    d0.delay should equal (1 minute)
    d0.totalTime should equal (0 minutes)
    val d1 = d0.next()
    d1.delay should equal (2 minutes)
    d1.totalTime should equal (1 minutes)
    val d2 = d1.next()
    d2.delay should equal (4 minutes)
    d2.totalTime should equal (3 minutes)
    val d3 = d2.next()
    d3.delay should equal (1 minutes)
    d3.totalTime should equal (0 minutes)
  }

}
