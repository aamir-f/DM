package downloadmanager.ftpmanager

import java.io.File

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorRef, OneForOneStrategy, PoisonPill, Props, ReceiveTimeout, Terminated}
import downloadmanager.utilities.HttpResponseTimeout._
import downloadmanager.utilities._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}

class FtpDownloadActor(url: String, fileName: String, actorRef:Option[ActorRef])
  extends Actor with Logger {
  var ftpDownloaderActorRef = Actor.noSender
  var RETRY_ATTEMPT = IntValues.ZERO

  override def receive: PartialFunction[Any, Unit] = {
    case StartHttpDownload => {
      if(RETRY_ATTEMPT < Utils.maxDownloadRetries.toInt) {
        RETRY_ATTEMPT += 1
        println("**************************")
        /**
          * Here timeout is configurable depending upon network speed, content size
          */
        context.setReceiveTimeout(timeout)
        val downloadActor = context.actorOf(Props( new HttpDownloaderComponent(None)), "HttpDownloader")
        ftpDownloaderActorRef = downloadActor
        downloadActor ! InitiateHttpDownload(url, fileName)
        context.become(waitingForResponse)
        actorRef.foreach(_ ! "Download Started")
      } else {
        val msg = s"#########FtpClient:Unable to download resource after max tries,please check your url#############"
        logger.error(msg)
        self ! PoisonPill
      }
    }
  }

  def waitingForResponse: Receive = {
    case ReceiveTimeout => {
      val msg = s"#########Dowload taking too much time, aborting and retrying download#############"
      cancelReceiveTimeOut
      context.stop(ftpDownloaderActorRef)
      removePartialDownloadLocalFile
      context.become(receive)
      context.system.scheduler.scheduleOnce(4.second,self,StartHttpDownload)
      logger.error(msg)
    }
    case Terminated => {
      val errMsg = "######Unexpected error:FtpClient is down#######"
      logger.error(errMsg)
    }
    case cmd: SuccessResponse => {
      logger.info(cmd.msg)
      cancelReceiveTimeOut
      context.stop(ftpDownloaderActorRef)
    }
  }

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case _: OutOfMemoryError => Resume
  }

  def removePartialDownloadLocalFile = {
    val localFileLoc = Utils.localDiskLocation
    val localFile = s"$localFileLoc$fileName"
    val file = new File(localFile)
    file.delete()
  }

def cancelReceiveTimeOut = context.setReceiveTimeout(Duration.Undefined)
}