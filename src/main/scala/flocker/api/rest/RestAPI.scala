package flocker.api.rest

import akka.actor.Actor
import flocker.api.rest.route.TopicRoute
import spray.routing.HttpService

/**
 * Created by mglvl on 12/16/14.
 */
class RestAPI extends Actor with HttpService with TopicRoute {

  def actorRefFactory = context

  val route = topicRoute

  def receive = runRoute(route)

}
