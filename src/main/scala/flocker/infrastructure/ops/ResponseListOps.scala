package flocker.infrastructure.ops

import twitter4j.{ResponseList, Status}

/**
 * Created by mglvl on 10/18/14.
 */
final case class ResponseListOps(self: ResponseList[Status]) extends AnyVal {

  final def toStream: Stream[Status] = (0 until self.size()).toStream.map(self.get)

}

trait ToResponseListOps {
  implicit def ToResponseListOps(response: ResponseList[Status]): ResponseListOps = ResponseListOps(response)
}
