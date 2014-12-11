package flocker.infrastructure

import flocker.configuration.TwitterConfig
import twitter4j.conf.Configuration

/**
  * Created by mglvl on 10/19/14.
 */
object Twitter {
  import twitter4j.{TwitterFactory, Twitter}

  def apply(config: Configuration): Twitter = {
    val factory = new TwitterFactory(config)
    factory.getInstance()
  }

  def apply(): Twitter = {
    val config = TwitterConfig.defaultTwitterConfig
    apply(config)
  }

}
