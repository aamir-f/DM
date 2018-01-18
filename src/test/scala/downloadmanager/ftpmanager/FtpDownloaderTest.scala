package downloadmanager.ftpmanager

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import downloadmanager.utilities.StartDownload
import org.specs2.mutable.SpecificationLike

class FtpDownloaderActorSpec extends TestKit(ActorSystem()) with SpecificationLike {

  "Http Download Actor" should {

    "return success message as download started" in {
      val url = "/Reports/"
      val fileName = "DailyCostReport.pdf"
      val actorProps = Props(new FtpDownloadActor(url, fileName, Some(testActor)))
      val actor = system.actorOf(actorProps, "HttpDownloadActor")
      actor ! StartDownload
      expectMsg("Download Started")
      success
    }
  }
}