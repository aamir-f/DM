package downloadmanager.ftpmanager

import java.io.{BufferedOutputStream, File, FileOutputStream}

import downloadmanager.utilities.{ConfigurationReaderComponent, Logger}
import org.apache.commons.net.ftp.{FTP, FTPClient}

import scala.util.Try

trait FtpDownloaderDM  extends Logger {

  val ftpServer = FtpCredentialsDM.serverIp
  val ftpPort = FtpCredentialsDM.serverPort
  val ftpUsername = FtpCredentialsDM.serverUsername
  val ftpPassword = FtpCredentialsDM.serverPassword


  def downloadCsvFiles(ftpFileUrl: String, fileName: String): Try[String] = {
    Try {

      val ftpClient = FTPClientGeneratorDM.createFTPClient
      val localDownloadFtpPath = FtpCredentialsDM.localFileSaveLocation

      new File(localDownloadFtpPath).mkdirs()

      connectToFtpServer(ftpClient)
      val fileDownloadable = ftpFileUrl
      val localCsvFile = s"$localDownloadFtpPath$fileName"
      val os = new BufferedOutputStream(new FileOutputStream(localCsvFile))
      ftpClient.retrieveFile(fileDownloadable, os)
      os.close()

      FTPClientGeneratorDM.disconnect(ftpClient)
      val succMsg = "downloading ftp resource completed"
      logger.info(succMsg)
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

object ImplFtpDownloaderDM extends FtpDownloaderDM


object FtpCredentialsDM extends ConfigurationReaderComponent {
  def serverIp: String = getConfigurationProperty("ftpCredentials.ftpServer")
  def serverPort: String = getConfigurationProperty("ftpCredentials.ftpServer")
  def serverUsername: String = getConfigurationProperty("ftpCredentials.ftpServer")
  def serverPassword: String = getConfigurationProperty("ftpCredentials.ftpServer")
  val localFileSaveLocation: String = getConfigurationProperty("ftpCredentials.localDownloadLocation")
}

object FTPClientGeneratorDM {
  def createFTPClient: FTPClient = {
    new FTPClient()
  }

  def disconnect(ftpClient: FTPClient): Unit = {
    ftpClient.logout
    ftpClient.disconnect()
  }
}