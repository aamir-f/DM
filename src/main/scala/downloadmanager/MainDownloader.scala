package downloadmanager

import akka.actor.Props
import downloadmanager.ftpmanager.FtpDownloadActor
import downloadmanager.httpmanager.HttpDownloadActor
import downloadmanager.utilities._

trait Downloader {
  self:DownloadManagerFacadeComponent =>
  private[downloadmanager] def init(sourceList:List[String]) = {
    val splitList = sourceList.map(x => (x ,utilFacade.extractFileNameFromFTPUrl(x)))
    resolveSources(splitList)
  }

  private def resolveSources(urlFileName:List[(String,String)]) = {
    urlFileName foreach{ x =>
      if(x._1.startsWith("http")) {
        val httpRef = ActorSystemContainer.system.actorOf(Props(new HttpDownloadActor(x._1,x._2,None)),"HttpDownloadStarter")
        httpRef ! StartDownload
      }
      if(x._1.startsWith("ftp")) {
        val basePath = x._1.substring(x._1.indexOf("/") + 1)
        val ftpRef = ActorSystemContainer.system.actorOf(Props(new FtpDownloadActor(basePath,x._2,None)),"FtpDownloadStarter")
        ftpRef ! StartDownload
      }

    }
  }
}
object ImplDownloader extends Downloader with DownloadManagerFacade

object ll extends App /*with Logger*/ {

  val source1 = Utils.buildHttpUrl
  val source2 = Utils.buildFtpUrl
  val source3 = "sftp://and.also.this/sftpfile"
  val sourceList = List(source1,source2,source3)
  ImplDownloader.init(sourceList)
}