package downloadmanager.httpmanager

import akka.util.Timeout
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration._

class HttpDownloaderClosureSpec extends WordSpec with MockitoSugar with Matchers with ScalaFutures {

  val to = Timeout(5.seconds)

  "Ftp Downloader closure" must {
    "download the Ftp resource from remote location" in {
      val downloadResult = "file download completed"
      when(MockHttpDownloader.ftpDownloader.fileDownloader(any[String],any[String])).thenReturn(downloadResult)
    }
  }

}

object MockHttpDownloader extends HttpDownloaderComponent with MockitoSugar {
  lazy val ftpDownloader = mock[HttpDownloaderComponent]
}