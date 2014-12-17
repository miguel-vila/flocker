package flocker.api.rest

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http

/**
 * Created by mglvl on 12/16/14.
 */
object Boot extends App {

  implicit val actorSystem = ActorSystem("actorSystem")

  val restService = actorSystem.actorOf(Props[RestAPI], "rest_service_actor")

  val appHost = "0.0.0.0"
  val appPort = Option(System.getenv("PORT")).getOrElse("9090").toInt

  IO(Http) ! Http.Bind(restService, interface = appHost, port = appPort)

}
