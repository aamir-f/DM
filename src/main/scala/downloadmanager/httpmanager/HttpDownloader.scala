package downloadmanager.httpmanager

import java.io.File
import java.net.URL

import downloadmanager.utilities.LocalFileLocations

import scala.sys.process._

trait HttpDownloaderComponent {

  def   fileDownloader(url: String, filename: String): String = {
    val localFileLoc = s"${LocalFileLocations.localHttpFileLocation}/$filename"
    downloadFile(url, localFileLoc)
  }

  private[httpmanager] def downloadFile(url: String, localFileLoc: String) = {
    new URL(url) #> new File(localFileLoc) !!
  }
}
object HttpDownloader extends HttpDownloaderComponent
