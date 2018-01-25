package downloadmanager.httpmanager

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import downloadmanager.utilities.{InitiateDownload, StartDownload}
import org.scalatest.concurrent.ScalaFutures
import org.specs2.mutable.SpecificationLike

class HttpDownloaderActorSpec extends TestKit(ActorSystem())
  with SpecificationLike with ScalaFutures {

  "Http Download Actor" should {

    "return success message as download started" in {
      val url = "http://www.sample-videos.com/text/Sample-text-file-10kb.txt"
      val fileName = "build.sbt"
      val actorProps = Props(new HttpDownloadActor(url, fileName, Some(testActor)))
      val actor = system.actorOf(actorProps, "HttpDownloadActor")
      actor ! StartDownload
      expectMsg("Download Started")
      success
    }

    "return failure message as download failed" in {
      val url = ""
      val fileName = ""
      val actorProps = Props(new HttpDownloadActor(url, fileName, Some(testActor)))
      val actor = system.actorOf(actorProps, "HttpDownloadActor")
      actor ! StartDownload
      expectMsg("Download Started")
      success
    }

    "return error failure message when download failed" in {

      val actorProps = Props(new HttpDownloaderComponent(Some(testActor)))
      val actor = system.actorOf(actorProps, "HttpDownloaderComponent")
      val url = ""
      val fileName = ""
      val expectedMessage = "error downloading resources, check your url"
      actor ! InitiateDownload(url, fileName)
      expectMsg(expectedMessage)
      success
    }
  }
}

