package downloadmanager

import akka.actor.Props
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
        val httpRef = ActorSystemContainer.system.actorOf(Props(new HttpDownloadActor(x._1,x._2)),"HttpDownloadStarter")
        httpRef ! StartHttpDownload
      }
    }
  }
}
object ImplDownloader extends Downloader with DownloadManagerFacade

object ll extends App /*with Logger*/ {

  val source1 = "http://uat.reactore.com:22/artifactory/lib-test/build.sbt"
  val source2 = "ftp://other.file.com/ftpfile"
  val source3 = "sftp://and.also.this/sftpfile"
  val sourceList = List(source1,source2,source3)
  ImplDownloader.init(sourceList)
}