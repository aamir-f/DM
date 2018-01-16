package downloadmanager.ftpmanager

import java.io.{BufferedOutputStream, File, FileOutputStream}

import downloadmanager.utilities.{ConfigurationReaderComponent, Logger}
import org.apache.commons.net.ftp.{FTP, FTPClient}

import scala.util.Try

trait FtpDownloader  extends Logger {

  val ftpServer = FtpCredentials.serverIp
  val ftpPort = FtpCredentials.serverPort
  val ftpUsername = FtpCredentials.serverUsername
  val ftpPassword = FtpCredentials.serverPassword


  def downloadFtpFile(ftpFileUrl: String, fileName: String): Try[String] = {
    Try {

      val ftpClient = FTPClientGenerator.createFTPClient
      val localDownloadFtpPath = FtpCredentials.localFileSaveLocation

      new File(localDownloadFtpPath).mkdirs()

      connectToFtpServer(ftpClient)
      val fileDownloadable = ftpFileUrl
      val localCsvFile = s"$localDownloadFtpPath$fileName"
      val os = new BufferedOutputStream(new FileOutputStream(localCsvFile))
      ftpClient.retrieveFile(fileDownloadable, os)
      os.close()

      FTPClientGenerator.disconnect(ftpClient)
      val succMsg = "downloading ftp resource completed"
      logger.info(succMsg)
      succMsg
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
  val localFileSaveLocation: String = getConfigurationProperty("localFilePath")
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