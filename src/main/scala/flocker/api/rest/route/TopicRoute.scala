package flocker.api.rest.route

import flocker.api.rest.route.WebObjects.CreateTopic
import spray.routing.HttpService
import spray.http.StatusCodes.Created
/**
 * Created by mglvl on 12/16/14.
 */
trait TopicRoute extends HttpService {
  import flocker.api.rest.marshalling.Json4sProtocol._

  val topicPrefix =  pathPrefix("topics")

  val topicRoute = topicPrefix {
    post {
      respondWithStatus(Created) {
        entity(as[CreateTopic]) { createTopic =>
          complete(createTopic)
        }
      }
    }
  }

}
