package flocker.configuration

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions.asScalaBuffer

/**
 * Created by mglvl on 10/19/14.
 */
class ConfigReader(prefix: String) {

  protected val conf = ConfigFactory.load

  protected def path( key: String ): String = s"$prefix.$key"

  protected def getStringList( key: String ): List[String] = asScalaBuffer(conf.getStringList( path(key) )).toList

  protected def getString( key: String ): String = conf.getString( path(key) )

  protected def getInt( key: String ): Int = conf.getInt( path(key) )

}