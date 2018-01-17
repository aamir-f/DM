package downloadmanager.utilities

import akka.actor.ActorSystem

trait UtilsComponent {
  self: DownloadManagerFacadeComponent =>
  def extractFileNameFromFTPUrl(url: String): String = url.substring(url.lastIndexOf("/") + 1)
  def localDiskLocation: String = configFacade.getConfigurationProperty("localDiskLocation")
}

object Utils extends UtilsComponent with DownloadManagerFacade

object ActorSystemContainer {
  lazy val system: ActorSystem = ActorSystem("DownloadingActorSystem")
}