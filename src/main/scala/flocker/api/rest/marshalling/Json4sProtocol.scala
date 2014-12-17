package flocker.api.rest.marshalling

import org.json4s.{Formats, DefaultFormats}
import spray.httpx.Json4sSupport

/**
 * Created by mglvl on 12/16/14.
 */
object Json4sProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
}
