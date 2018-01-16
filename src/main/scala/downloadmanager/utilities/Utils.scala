package downloadmanager.utilities

trait UtilsComponent {
  def extractFileNameFromFTPUrl(url: String): String = {
    url.substring(url.lastIndexOf("/") + 1)
  }
}
object Util extends UtilsComponent