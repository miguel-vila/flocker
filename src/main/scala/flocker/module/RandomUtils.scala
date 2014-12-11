package flocker.module

import flocker.model.Token

import scala.util.Random

/**
 * Created by mglvl on 10/25/14.
 */
trait RandomUtilsFunctions {

  def selectRandomElement[A](elements: List[A]): A = {
    elements( Random.nextInt( elements.size ) )
  }

}

object RandomUtils extends RandomUtilsFunctions