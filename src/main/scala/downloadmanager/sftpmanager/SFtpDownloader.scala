
package downloadmanager.sftpmanager

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileOutputStream}
import java.util.Properties
import com.jcraft.jsch.{ChannelSftp, JSch, Session}
import downloadmanager.utilities.{ConfigurationReaderComponent, Logger}
import org.apache.commons.net.ftp.FTPClient
import scala.util.Try

trait SFtpDownloader  extends Logger {

  val ftpServer = SFtpCredentials.serverIp
  val ftpPort = SFtpCredentials.serverPort
  val ftpUsername = SFtpCredentials.serverUsername
  val ftpPassword = SFtpCredentials.serverPassword
  val workingDirectory = SFtpCredentials.localFileSaveLocation


  def downloadCsvFiles(sFtpFileUrl: String, fileName: String): Try[String] = {
    Try {
       val session = SFTPClientGenerator.createSFTPClient
      connectToSFtpServer(session)
      val channel = session.openChannel("sftp")
      channel.connect()
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      channelSftp.cd(workingDirectory)
      val buffer = new Array[Byte](1024)
      val bis = new BufferedInputStream(channelSftp.get(s"$sFtpFileUrl/$fileName"))
      val newFile = new File(s"$workingDirectory/$fileName")
      val os = new FileOutputStream(newFile)
      val bos = new BufferedOutputStream(os)
      var readCount = 0

      while ({readCount = bis.read(buffer); readCount > 0}) {
        System.out.println("Writing: ")
        bos.write(buffer, 0, readCount)
      }
      bis.close()
      bos.close()
      val succMsg = "downloading sftp resource completed"
      logger.info(succMsg)
      succMsg
    }
  }

  def connectToSFtpServer(session: Session) = {
    session.setPassword(ftpPassword)
    val config = new Properties
    config.put("StrictHostKeyChecking", "no")
    session.setConfig(config)
    session.connect()
    val channel = session.openChannel("sftp")
    channel.connect()
  }
}

object ImplSFtpDownloader extends SFtpDownloader


object SFtpCredentials extends ConfigurationReaderComponent {
  def serverIp: String = getConfigurationProperty("sftpCredentials.sftpServer")
  def serverPort: String = getConfigurationProperty("sftpCredentials.sftpPort")
  def serverUsername: String = getConfigurationProperty("sftpCredentials.sftpUsername")
  def serverPassword: String = getConfigurationProperty("sftpCredentials.sftpPassword")
  val localFileSaveLocation: String = getConfigurationProperty("")
}

object SFTPClientGenerator {
  import SFtpCredentials._
  def createSFTPClient: Session = {
    val jsch = new JSch
    jsch.getSession(serverUsername, serverIp, serverPort.toInt)
  }

  def disconnect(ftpClient: FTPClient): Unit = {
    ftpClient.logout
    ftpClient.disconnect()
  }
}
