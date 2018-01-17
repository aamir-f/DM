package downloadmanager.utilities

import akka.actor.ActorSystem

trait UtilsComponent {
  def extractFileNameFromFTPUrl(url: String): String = {
    url.substring(url.lastIndexOf("/") + 1)
  }
}

object Utils extends UtilsComponent

object ActorSystemContainer {
  lazy val system: ActorSystem = ActorSystem("ReactorActorSystem")
}