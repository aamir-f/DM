package downloadmanager.httpmanager

import java.io.File
import java.net.URL

import akka.actor.{Actor, PoisonPill}
import downloadmanager.utilities.{InitiateHttpDownload, LocalFileLocations/*, Logger*/, SuccessResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success}

class HttpDownloaderComponent extends Actor /*with Logger*/{

  override def receive: Receive = {
    case cmd:InitiateHttpDownload => {
      val result: Future[String] = fileDownloader(cmd.url,cmd.fileName)
      result onComplete {
        case Success(_) => sender ! SuccessResponse("http download completed")
        case Failure(e) => {
          val errMsg =
            s"""
               |################################\n
               |http download failed, ${e.printStackTrace()}
               |################################\n
             """.stripMargin
          //logger.error(errMsg)
          self ! PoisonPill
        }
      }
    }
  }

  def fileDownloader(url: String, filename: String): Future[String] = {
    val localFileLoc = s"${LocalFileLocations.localHttpFileLocation}/$filename"
    Future(downloadFile(url, localFileLoc))
  }

  private[httpmanager] def downloadFile(url: String, localFileLoc: String) = {
    new URL(url) #> new File(localFileLoc) !!
  }
}
object HttpDownloader extends HttpDownloaderComponent
