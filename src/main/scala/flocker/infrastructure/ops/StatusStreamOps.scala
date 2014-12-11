package flocker.infrastructure.ops

import java.util.Date
import flocker.model.{Tokens, Trigram}
import twitter4j.Status

/**
 * Created by mglvl on 10/18/14.
 */
final case class StatusStreamOps(self: Stream[Status]) extends AnyVal {

  final def getMostRecentStatus: Date = {
    self.tail.foldLeft(self.head.getCreatedAt) { (r, s) =>
      val statusDate = s.getCreatedAt
      if (statusDate.after(r))
        statusDate
      else
        r
    }
  }

  final def filterOutRetweeted: Stream[Status] = self.filterNot(_.isRetweet)

  final def filterByMostRecentDate(mostRecentTweetDate: Option[Date]): (Stream[Status], Date) = {
    mostRecentTweetDate match {
      case None => (self, getMostRecentStatus)
      case Some(mostRecentTweetDate) =>
        val filtered = self.filter(_.getCreatedAt.after(mostRecentTweetDate))
        (filtered, StatusStreamOps(filtered).getMostRecentStatus)
    }
  }

  final def extractValidTrigrams(tweetsTexts: Stream[Tokens]): Stream[Trigram] = {
    tweetsTexts
      .map(_.filterOutInvalidWords)
      .flatMap(_.validTrigrams)
  }

}

trait ToStatusStreamOps {
  implicit def ToStatusStreamOps(s: Stream[Status]): StatusStreamOps = StatusStreamOps(s)
}