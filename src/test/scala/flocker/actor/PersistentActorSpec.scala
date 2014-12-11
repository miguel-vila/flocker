package flocker.actor

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.{Config, ConfigFactory}
import de.flapdoodle.embed.process.runtime.Network
import flocker.testing.mocking.MockPersistence
import org.scalatest.Suite

/**
 * Created by mglvl on 11/3/14.
 */
abstract class PersistentActorSpec(actorSystemName: String, freePort: Int = PersistentActorSpec.freePort, config: Config = PersistentActorSpec.config(PersistentActorSpec.freePort)) extends TestKit(ActorSystem(actorSystemName, config)) {
  this : Suite =>

  import PersistentActorSpec._

  lazy val mockPersistence = MockPersistence( freePort )
}

object PersistentActorSpec {
  def config(port: Int) = ConfigFactory.parseString(
    s"""
      |akka.persistence.journal.plugin = "casbah-journal"
      |akka.persistence.snapshot-store.plugin = "casbah-snapshot-store"
      |akka.persistence.journal.max-deletion-batch-size = 3
      |akka.persistence.publish-plugin-commands = on
      |akka.persistence.publish-confirmations = on
      |akka.persistence.view.auto-update-interval = 1s
      |casbah-journal.mongo-journal-url = "mongodb://localhost:$port/store.messages"
      |casbah-journal.mongo-journal-write-concern = "acknowledged"
      |casbah-journal.mongo-journal-write-concern-timeout = 10000
      |casbah-snapshot-store.mongo-snapshot-url = "mongodb://localhost:$port/store.snapshots"
      |casbah-snapshot-store.mongo-snapshot-write-concern = "acknowledged"
      |casbah-snapshot-store.mongo-snapshot-write-concern-timeout = 10000
    """.stripMargin)

  lazy val freePort = Network.getFreeServerPort
}