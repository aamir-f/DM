package downloadmanager

import akka.actor.Props
import com.typesafe.scalalogging.{LazyLogging, Logger}
import downloadmanager.httpmanager.HttpDownloadActor
import downloadmanager.utilities._
import org.slf4j.LoggerFactory

trait Downloader {
  self:DownloadManagerFacadeComponent =>
  private[downloadmanager] def init(sourceList:List[String]) = {
    val splitList = sourceList.map(x => (x ,utilFacade.extractFileNameFromFTPUrl(x)))
    resolveSources(splitList)
  }

  private def resolveSources(urlFileName:List[(String,String)]) = {
    urlFileName foreach{ x =>
      if(x._1.startsWith("http")) {
        val httpRef = ActorSystemContainer.system.actorOf(Props(new HttpDownloadActor(x._1,x._2)),"HttpDownloader")
        httpRef ! StartHttpDownload
      }
    }
  }
}
object ImplDownloader extends Downloader with DownloadManagerFacade

object ll extends App /*with Logger*/ {

  val source1 = "http://www.java2s.com/Code/JarDownload/scalatest/scalatest-1.2.jar.zip"
  val source2 = "ftp://other.file.com/ftpfile"
  val source3 = "sftp://and.also.this/sftpfile"
 //logger.info("###########testing logger############")
  val sourceList = List(source1,source2,source3)
  ImplDownloader.init(sourceList)
}

class MyClass extends LazyLogging {

  logger.debug("This is very convenient ;-)")
}

object jj extends App {

    new MyClass
}