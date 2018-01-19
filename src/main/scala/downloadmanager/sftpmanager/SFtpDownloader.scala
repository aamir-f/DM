
package downloadmanager.sftpmanager

import java.io._

import com.jcraft.jsch.{ChannelSftp, JSch}
import downloadmanager.utilities.{ConfigurationReaderComponent, Logger}

import scala.util.Try

trait SFtpDownloader  extends Logger {

  val sFtpServer = SFtpCredentials.serverIp
  val SFtpPort = SFtpCredentials.serverPort
  val SFtpUsername = SFtpCredentials.serverUsername
  val sFtpPassword = SFtpCredentials.serverPassword
  val serverPath = SFtpCredentials.serverPath
  val localWorkingDirectory = SFtpCredentials.localFileSaveLocation


  def downloadSFtpFile(sFtpFileUrl: String, fileName: String): Try[String] = {
    Try {
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

      import java.io.BufferedInputStream
      import java.io.BufferedOutputStream
      import java.io.FileOutputStream
      import java.io.OutputStream
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
