package downloadmanager.sftpmanager

import java.io.File

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorRef, OneForOneStrategy, PoisonPill, Props, ReceiveTimeout, Terminated}
import downloadmanager.utilities.HttpResponseTimeout._
import downloadmanager.utilities._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}

class SFtpDownloadActor(url: String, fileName: String, actorRef:Option[ActorRef])
  extends Actor with Logger {
  var SftpDownloaderActorRef = Actor.noSender
  var RETRY_ATTEMPT = IntValues.ZERO

  override def receive: PartialFunction[Any, Unit] = {
    case StartDownload => {
      if(RETRY_ATTEMPT < Utils.maxDownloadRetries.toInt) {
        RETRY_ATTEMPT += 1
        /**
          * Here timeout is configurable depending upon network speed, content size
          */
        context.setReceiveTimeout(timeout)
        val downloadActor = context.actorOf(Props(new SFtpDownloaderComponent(None)), "SFtpDownloader")
        SftpDownloaderActorRef = downloadActor
        downloadActor ! InitiateDownload(url, fileName)
        context.become(waitingForResponse)
        actorRef.foreach(_ ! "Download Started")
      } else {
        val msg = s"\n#########SFtpClient:Unable to download resource after max tries,please check your url#############"
        logger.error(msg)
        self ! PoisonPill
      }
    }
  }

  def waitingForResponse: Receive = {
    case ReceiveTimeout => {
      val msg = s"#########SFtpClient:::Download taking too much time, aborting and retrying download#############"
      cancelReceiveTimeOut
      context.stop(SftpDownloaderActorRef)
      removePartialDownloadLocalFile
      context.become(receive)
      context.system.scheduler.scheduleOnce(4.second,self,StartDownload)
      logger.error(msg)
    }
    case Terminated => {
      val errMsg = "######Unexpected error:SFtpClient is down#######"
      logger.error(errMsg)
    }
    case cmd: SuccessResponse => {
      logger.info(cmd.msg)
      cancelReceiveTimeOut
      context.stop(SftpDownloaderActorRef)
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