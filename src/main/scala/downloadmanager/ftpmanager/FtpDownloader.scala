package downloadmanager.ftpmanager

import java.io.File
import java.net.URL

import akka.actor.{Actor, ActorRef, PoisonPill}
import downloadmanager.utilities._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success}

class FtpDownloaderComponent(actorRef:Option[ActorRef]) extends Actor with Logger {

  override def preStart(): Unit = {
    logger.info("#################About to download ftp File#############")
  }
  override def receive: Receive = {
    case cmd: InitiateDownload => {
      val sender_ = sender()

      val result: Future[String] = ImplFtpDownloader.downloadFtpFile(cmd.url,cmd.fileName)
      result onComplete {
        case Success(_) => {
          val msg = s"###############ftp download completed###############################"
          sender_ ! SuccessResponse(msg)
        }
        case Failure(e) => {
          val errMsg =
            s"""
               |################################\n
               |ftp download failed, ${e.printStackTrace()}
               |################################\n
             """.stripMargin
          logger.error(errMsg)
          self ! PoisonPill
          actorRef.foreach(_ ! "error downloading resources, check your url")
        }
      }
    }
  }

  private[ftpmanager] def fileDownloader(url: String, fileName: String): Future[String] = {
    val localFileLoc = Utils.localDiskLocation
    val localFile = s"$localFileLoc$fileName"
    downloadFile(url, localFile)
  }

  private def downloadFile(url: String, localFileLoc: String): Future[String] = {
    Future(new URL(url) #> new File(localFileLoc) !!)
  }
}

object FtpDownloader extends FtpDownloaderComponent(None)

import java.io.{BufferedOutputStream, File, FileOutputStream}

import downloadmanager.utilities.ConfigurationReaderComponent
import org.apache.commons.net.ftp.{FTP, FTPClient}


trait FtpDownloader extends Logger {

  val ftpServer = FtpCredentials.serverIp
  val ftpPort = FtpCredentials.serverPort
  val ftpUsername = FtpCredentials.serverUsername
  val ftpPassword = FtpCredentials.serverPassword


  def downloadFtpFile(ftpRemotePath: String, fileName: String): Future[String] = {
    Future {

      val ftpClient = FTPClientGenerator.createFTPClient
      val localDownloadFtpPath = FtpCredentials.localFileSaveLocation

      new File(localDownloadFtpPath).mkdirs()

      connectToFtpServer(ftpClient)
      val fileDownloadable = s"$ftpRemotePath$fileName"
      val localCsvFile = s"$localDownloadFtpPath$fileName"
      val os = new BufferedOutputStream(new FileOutputStream(localCsvFile))
      ftpClient.retrieveFile(fileDownloadable, os)
      os.close()

      FTPClientGenerator.disconnect(ftpClient)
      val msg = "#####################downloading ftp resource completed##################"
      logger.info(msg)
      msg
    }
  }

  def connectToFtpServer(ftpClient: FTPClient) = {
    ftpClient.connect(ftpServer, ftpPort.toInt)
    ftpClient.enterLocalPassiveMode()
    ftpClient.login(ftpUsername, ftpPassword)
    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
    val msg = "ftp connection successful" + ftpClient.getReplyString
    logger.info(msg)
  }
}

object ImplFtpDownloader extends FtpDownloader

object FtpCredentials extends ConfigurationReaderComponent {
  def serverIp: String = getConfigurationProperty("ftpCredentials.ftpServer")
  def serverPort: String = getConfigurationProperty("ftpCredentials.ftpPort")
  def serverUsername: String = getConfigurationProperty("ftpCredentials.ftpUsername")
  def serverPassword: String = getConfigurationProperty("ftpCredentials.ftpPassword")
  val localFileSaveLocation: String = getConfigurationProperty("localDiskLocation")
}

object FTPClientGenerator {
  def createFTPClient: FTPClient = {
    new FTPClient()
  }

  def disconnect(ftpClient: FTPClient): Unit = {
    ftpClient.logout
    ftpClient.disconnect()
  }
}