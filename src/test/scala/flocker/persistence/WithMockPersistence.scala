package flocker.persistence

import flocker.testing.mocking.MockPersistence

/**
 * Created by mglvl on 12/16/14.
 */
trait WithMockPersistence {
  def freePort: Int
  lazy val mockPersistence = MockPersistence( freePort )
}
