
package downloadmanager.sftpmanager

import java.io._
import java.net.URL
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorRef, PoisonPill}
import com.jcraft.jsch.{ChannelSftp, JSch}
import downloadmanager.utilities._
import scala.concurrent.Future
import scala.sys.process._
import scala.util.{Failure, Success}

class SFtpDownloaderComponent(actorRef:Option[ActorRef]) extends Actor with Logger {

  override def preStart(): Unit = {
    logger.info("#################About to download sftp File#############")
  }
  override def receive: Receive = {
    case cmd: InitiateDownload => {
      val sender_ = sender()

      val result: Future[String] = ImplSFtpDownloader.downloadSFtpFile(cmd.url,cmd.fileName)
      result onComplete {
        case Success(_) => {
          val msg = s"###############sftp download completed###############################"
          sender_ ! SuccessResponse(msg)
        }
        case Failure(e) => {
          val errMsg =
            s"""
               |################################\n
               |sftp download failed, ${e.printStackTrace()}
               |################################\n
             """.stripMargin
          logger.error(errMsg)
          self ! PoisonPill
          actorRef.foreach(_ ! "error downloading resources, check your url")
        }
      }
    }
  }

  private[sftpmanager] def fileDownloader(url: String, fileName: String): Future[String] = {
    val localFileLoc = Utils.localDiskLocation
    val localFile = s"$localFileLoc$fileName"
    downloadFile(url, localFile)
  }

  private def downloadFile(url: String, localFileLoc: String): Future[String] = {
    Future(new URL(url) #> new File(localFileLoc) !!)
  }
}
trait SFtpDownloader  extends Logger {

  val sFtpServer = SFtpCredentials.serverIp
  val SFtpPort = SFtpCredentials.serverPort
  val SFtpUsername = SFtpCredentials.serverUsername
  val sFtpPassword = SFtpCredentials.serverPassword
  val serverPath = SFtpCredentials.serverPath
  val localWorkingDirectory = SFtpCredentials.localFileSaveLocation


  def downloadSFtpFile(sFtpFileUrl: String, fileName: String): Future[String] = {
    Future {
      val jsch = new JSch()
      val session = jsch.getSession(SFtpUsername, sFtpServer, SFtpPort.toInt)
      session.setPassword(sFtpPassword)
      val config = new java.util.Properties()
      config.put("StrictHostKeyChecking", "no")
      session.connect()
      val channel = session.openChannel("sftp")
      channel.connect()
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      channelSftp.cd(serverPath)

      import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream, OutputStream}
      val buffer: Array[Byte] = new Array[Byte](1024)
      val bis = new BufferedInputStream(channelSftp.get(fileName))
      val localFilePath = s"$localWorkingDirectory$fileName"
      val newFile = new File(localFilePath)
      val os: OutputStream = new FileOutputStream(newFile)
      val bos: BufferedOutputStream = new BufferedOutputStream(os)
      var readCount = 0
      logger.info("###from sFTP Client#####")
      while ({readCount = bis.read(buffer);readCount > 0})
      {
        logger.info("Writing: ")
        bos.write(buffer, 0,readCount)
      }

      bis.close()
      bos.close()
      val msg = "############sFtp download completed#############"
      logger.info("############sFtp download completed#############")
      msg
    }
  }
}

object ImplSFtpDownloader extends SFtpDownloader


object SFtpCredentials extends ConfigurationReaderComponent {
  def serverIp: String = getConfigurationProperty("sftpCredentials.sftpServer")
  def serverPort: String = getConfigurationProperty("sftpCredentials.sftpPort")
  def serverUsername: String = getConfigurationProperty("sftpCredentials.sftpUsername")
  def serverPassword: String = getConfigurationProperty("sftpCredentials.sftpPassword")
  def serverPath: String = getConfigurationProperty("sftpCredentials.path")
  val localFileSaveLocation: String = getConfigurationProperty("localDiskLocation")
}
