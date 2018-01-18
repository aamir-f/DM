package downloadmanager.utilities

import akka.actor.ActorSystem

trait UtilsComponent {
  self: DownloadManagerFacadeComponent =>
  def extractFileNameFromFTPUrl(url: String): String = url.substring(url.lastIndexOf("/") + 1)
  def localDiskLocation: String = configFacade.getConfigurationProperty("localDiskLocation")
  def maxDownloadRetries: String = configFacade.getConfigurationProperty("maxRetries")
  def httpIP: String = configFacade.getConfigurationProperty("httpCredentials.ip")
  def httpPort: String = configFacade.getConfigurationProperty("httpCredentials.port")
  def httpPath: String = configFacade.getConfigurationProperty("httpCredentials.path")
  def httpFileName: String = configFacade.getConfigurationProperty("httpCredentials.file")

  def buildHttpUrl: String = {
    val ip = httpIP
    val port = httpPort
    val serverPath = httpPath
    val filename = httpFileName
    s"http://$ip:$port$serverPath$filename"
  }
}

object Utils extends UtilsComponent with DownloadManagerFacade

object ActorSystemContainer {
  lazy val system: ActorSystem = ActorSystem("DownloadingActorSystem")
}