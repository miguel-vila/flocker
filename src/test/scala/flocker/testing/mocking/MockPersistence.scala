package flocker.testing.mocking

import de.flapdoodle.embed.mongo.{MongodStarter, Command}
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.extract.UUIDTempNaming
import de.flapdoodle.embed.process.io.{NullProcessor, Processors}
import de.flapdoodle.embed.process.io.directories.PlatformTempDir
import de.flapdoodle.embed.process.runtime.Network

/**
 * Created by mglvl on 11/7/14.
 */
case class MockPersistence(freePort: Int) {
  lazy val host = "localhost"
  lazy val port = freePort
  lazy val localHostIPV6 = Network.localhostIsIPv6()

  val artifactStorePath = new PlatformTempDir()
  val executableNaming = new UUIDTempNaming()
  val command = Command.MongoD
  val version = Version.Main.PRODUCTION

  // Used to filter out console output messages.
  val processOutput = new ProcessOutput(
    Processors.named("[mongod>]", new NullProcessor),
    Processors.named("[MONGOD>]", new NullProcessor),
    Processors.named("[console>]", new NullProcessor))

  val runtimeConfig: IRuntimeConfig =
    new RuntimeConfigBuilder()
      .defaults(command)
      .processOutput(processOutput)
      .artifactStore(new ArtifactStoreBuilder()
      .defaults(command)
      .download(new DownloadConfigBuilder()
      .defaultsForCommand(command)
      .artifactStorePath(artifactStorePath))
      .executableNaming(executableNaming))
      .build()

  val mongodConfig =
    new MongodConfigBuilder()
      .version(version)
      .net(new Net(port, localHostIPV6))
      .build()

  lazy val mongodStarter = MongodStarter.getInstance(runtimeConfig)
  lazy val mongod = mongodStarter.prepare(mongodConfig)
  lazy val mongodExe = mongod.start()

  def startMongo() = {
    mongodExe
  }

  def stopMongo() = {
    mongod.stop()
    mongodExe.stop()
  }
}
