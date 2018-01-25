package downloadmanager.httpmanager

import java.io.File
import java.net.URL

import akka.actor.{Actor, ActorRef, PoisonPill}
import downloadmanager.utilities._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success}

class HttpDownloaderComponent(actorRef:Option[ActorRef]) extends Actor with Logger with HttpDownloaderRepo {

  override def preStart(): Unit = {
    logger.info("#################About to download Http File#############")
  }
  override def receive: Receive = {
    case cmd: InitiateDownload => {
      val sender_ = sender()

      val result: Future[String] = fileDownloader(cmd.url, cmd.fileName)
      result onComplete {
        case Success(_) => {
          val msg = s"###############Http download completed###############################"
          sender_ ! SuccessResponse(msg)
        }
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
}

trait HttpDownloaderRepo {
  private[httpmanager] def fileDownloader(url: String, fileName: String): Future[String] = {
    val localFileLoc = Utils.localDiskLocation
    val localFile = s"$localFileLoc$fileName"
    downloadFile(url, localFile)
  }

  private[httpmanager] def downloadFile(url: String, localFileLoc: String): Future[String] = {
    Future(new URL(url) #> new File(localFileLoc) !!)
  }
}
object ImplHttpDownloaderRepo extends HttpDownloaderRepo