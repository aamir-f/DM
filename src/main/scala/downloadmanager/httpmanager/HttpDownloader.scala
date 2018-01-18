package downloadmanager.httpmanager

import java.io.File
import java.net.URL

import akka.actor.{Actor, ActorRef, PoisonPill}
import downloadmanager.utilities._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success}

class HttpDownloaderComponent(actorRef:Option[ActorRef]) extends Actor with Logger {

  override def preStart(): Unit = {
    logger.info("#################About to download Http File#############")
  }
  override def receive: Receive = {
    case cmd: InitiateHttpDownload => {
      val sender_ = sender()

      val result: Future[String] = fileDownloader(cmd.url, cmd.fileName)
      //Thread.sleep(10000)
      result onComplete {
        case Success(_) => sender_ ! SuccessResponse("http download completed")
        case Failure(e) => {
          val errMsg =
            s"""
               |################################\n
               |http download failed, ${e.printStackTrace()}
               |################################\n
             """.stripMargin
          logger.error(errMsg)
          self ! PoisonPill
          actorRef.foreach(_ ! "error downloading resources, check your url")
        }
      }
    }
  }

  private[httpmanager] def fileDownloader(url: String, fileName: String): Future[String] = {
    val localFileLoc = Utils.localDiskLocation
    val localFile = s"$localFileLoc$fileName"
    downloadFile(url, localFile)
  }

  private def downloadFile(url: String, localFileLoc: String): Future[String] = {
    Future(new URL(url) #> new File(localFileLoc) !!)
  }
}

object HttpDownloader extends HttpDownloaderComponent(None  )
