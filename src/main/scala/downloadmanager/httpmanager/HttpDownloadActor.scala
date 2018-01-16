package downloadmanager.httpmanager

import akka.actor.{Actor, ReceiveTimeout, Terminated}
import downloadmanager.utilities.{Logger, StartHttpDownload, SuccessResponse}

import scala.concurrent.duration._
class HttpDownloadActor extends Actor with Logger {
  override def receive = {
    case StartHttpDownload => {
      context.setReceiveTimeout(5.minute)
     //ask for download
      context.become(waitingForResponse)
    }
  }

  def waitingForResponse:Receive = {
    case ReceiveTimeout => {
      logger.error("")
    }
    case Terminated => {
      //watch child downloader
    }
    case cmd:SuccessResponse => {
      logger.info(cmd.msg)
    }
  }
}