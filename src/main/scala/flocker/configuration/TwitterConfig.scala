package flocker.configuration

import twitter4j.conf.{Configuration, ConfigurationBuilder}

/**
 * Created by mglvl on 10/19/14.
 */
object TwitterConfig extends ConfigReader("twitter") {

  private lazy val OAuthConsumerKey = getString("OAuthConsumerKey")
  private lazy val OAuthConsumerSecret = getString("OAuthConsumerSecret")
  private lazy val OAuthAccessToken = getString("OAuthAccessToken")
  private lazy val OAuthAccessTokenSecret = getString("OAuthAccessTokenSecret")

  lazy val defaultTwitterConfig: Configuration = {
    (new ConfigurationBuilder).setOAuthConsumerKey( OAuthConsumerKey )
                              .setOAuthConsumerSecret( OAuthConsumerSecret )
                              .setOAuthAccessToken( OAuthAccessToken )
                              .setOAuthAccessTokenSecret( OAuthAccessTokenSecret )
                              .build()
  }

}
