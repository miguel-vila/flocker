package flocker.infrastructure

import flocker.configuration.TwitterConfig
import twitter4j.conf.{ConfigurationBuilder, Configuration}

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

  def apply(
             OAuthConsumerKey: String,
             OAuthConsumerSecret: String,
             OAuthAccessToken: String,
             OAuthAccessTokenSecret: String): Twitter = {
    val config = (new ConfigurationBuilder).setOAuthConsumerKey( OAuthConsumerKey )
      .setOAuthConsumerSecret( OAuthConsumerSecret )
      .setOAuthAccessToken( OAuthAccessToken )
      .setOAuthAccessTokenSecret( OAuthAccessTokenSecret )
      .build()
    apply(config)
  }

}
