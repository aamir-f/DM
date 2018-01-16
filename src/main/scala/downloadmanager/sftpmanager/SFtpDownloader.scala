package downloadmanager.sftpmanager

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Properties

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.{BufferedOutputStream, File, FileOutputStream}

import downloadmanager.ftpmanager.FtpCredentials.getConfigurationProperty
import downloadmanager.utilities.{ConfigurationReaderComponent, Logger}
import org.apache.commons.net.ftp.{FTP, FTPClient}

import scala.util.Try

trait SFtpDownloader  extends Logger {

  val ftpServer = SFtpCredentials.serverIp
  val ftpPort = SFtpCredentials.serverPort
  val ftpUsername = SFtpCredentials.serverUsername
  val ftpPassword = SFtpCredentials.serverPassword
  val workingDirectory = SFtpCredentials.localFileSaveLocation


  def downloadCsvFiles(sFtpFileUrl: String, fileName: String): Try[String] = {
    Try {

   /*   val ftpClient = FTPClientGenerator.createFTPClient
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
      succMsg*/
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

      while ( {
        readCount = bis.read(buffer); readCount != null}) {
        System.out.println("Writing: ")
        bos.write(buffer, 0, readCount)
      }
      bis.close()
      bos.close()

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
  val localFileSaveLocation: String = getConfigurationProperty("sftpCredentials.slocalDownloadLocation")
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

object SFTPinJava {
  /**
    * @param args
    */
  def main(args: Array[String]): Unit = {
    var session = null
    var channel = null
    var channelSftp = null
    val SFTPHOST = "10.20.30.40"
    val SFTPPORT = 22
    val SFTPUSER = "username"
    val SFTPPASS = "password"
    val SFTPWORKINGDIR = "/export/home/kodehelp/"
    try {
      val jsch = new JSch
      val session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT)
      session.setPassword(SFTPPASS)
      val config = new Properties
      config.put("StrictHostKeyChecking", "no")
      session.setConfig(config)
      session.connect()
      val channel = session.openChannel("sftp")
      channel.connect()
      val channelSftp = channel.asInstanceOf[ChannelSftp]
      channelSftp.cd(SFTPWORKINGDIR)
      val buffer = new Array[Byte](1024)
      val bis = new BufferedInputStream(channelSftp.get("Test.java"))
      val newFile = new File("C:/Test.java")
      val os = new FileOutputStream(newFile)
      val bos = new BufferedOutputStream(os)
      var readCount = 0
      while ( {
        (readCount = bis.read(buffer)) > 0
      }) {
        System.out.println("Writing: ")
        bos.write(buffer, 0, readCount)
      }
      bis.close()
      bos.close()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }
}

class SFTPinJava()

/**
  *
  */ {
}