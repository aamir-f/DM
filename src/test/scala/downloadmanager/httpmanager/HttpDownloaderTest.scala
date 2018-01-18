package downloadmanager.httpmanager

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import downloadmanager.utilities.{InitiateHttpDownload, StartHttpDownload}
import org.specs2.mutable.SpecificationLike

  class HttpDownloaderActorSpec extends TestKit(ActorSystem()) with SpecificationLike {

    "Http Download Actor" should {

      "return success message as download started" in {
        val url = "http://uat.reactore.com:8081/artifactory/lib-test/build.sbt"
        val fileName = "build.sbt"
        val actorProps = Props(new HttpDownloadActor(url, fileName, Some(testActor)))
        val actor = system.actorOf(actorProps, "HttpDownloadActor")
        actor ! StartHttpDownload
        expectMsg("Download Started")
        success
      }

      "return failure message as download failed" in {
        val url = ""
        val fileName = ""
        val actorProps = Props(new HttpDownloadActor(url, fileName, Some(testActor)))
        val actor = system.actorOf(actorProps, "HttpDownloadActor")
        actor ! StartHttpDownload
        expectMsg("Download Started")
        success
      }

      "return error failure message when download failed" in {

        val actorProps = Props(new HttpDownloaderComponent(Some(testActor)))
        val actor = system.actorOf(actorProps, "HttpDownloaderComponent")
        val url = ""
        val fileName = ""
        val expectedMessage = "error downloading resources, check your url"
        actor ! InitiateHttpDownload(url,fileName)
        expectMsg(expectedMessage)
        success
      }

    }
  }