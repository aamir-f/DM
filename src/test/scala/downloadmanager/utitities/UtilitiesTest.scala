package downloadmanager.utitities

import downloadmanager.utilities.Utils
import org.scalatest.{Matchers, WordSpec}

class UtilitiesTest extends WordSpec with Matchers {

  "Utilities Test" should {
    "split the url and retrieve file name from  it" in {
      val url = "http://uat.somedomain.com:8081/artifactory/lib-test/build.sbt"
      val expectedResult = "build.sbt"
      val result = Utils.extractFileNameFromFTPUrl(url)
      result shouldBe expectedResult
    }
    "retrieve max number of retries used" in {
      val expectedResult = "6"
      val result = Utils.maxDownloadRetries
      result shouldBe expectedResult
    }
  }
}
