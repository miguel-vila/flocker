package flocker.api.rest.route

/**
 * Created by mglvl on 12/16/14.
 */
object WebObjects {

  case class CreateTopic(
                          topicId: String,
                          OAuthConsumerKey: String,
                          OAuthConsumerSecret: String,
                          OAuthAccessToken: String,
                          OAuthAccessTokenSecret: String)

}
