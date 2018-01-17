package downloadmanager.httpmanager

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, ReceiveTimeout, Terminated}
import downloadmanager.utilities.HttpResponseTimeout._
import downloadmanager.utilities.{InitiateHttpDownload/*, Logger*/, StartHttpDownload, SuccessResponse}
class HttpDownloadActor(url:String,fileName:String) extends Actor /*with Logger*/ {

  override def receive:PartialFunction[Any,Unit] = {

    case StartHttpDownload => {
      context.setReceiveTimeout(timeout)
       val downloadActor = context.actorOf(Props[HttpDownloaderComponent],"HttpDownloaderComponent")
        downloadActor.tell(InitiateHttpDownload(url,fileName),ActorRef.noSender)
      context.become(waitingForResponse)
    }
  }

  def waitingForResponse:Receive = {
    case ReceiveTimeout => {
      val msg = s"#########Dowload taking too much time, aborting and restarting#############"
      //logger.info(msg)
    }
    case Terminated => {
      val errMsg = "######Unexpected error:HttpClient is down#######"
      //logger.error(errMsg)
    }
    case cmd:SuccessResponse => {
      //logger.info(cmd.msg)
    }
  }

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case _:OutOfMemoryError => Resume
  }
}