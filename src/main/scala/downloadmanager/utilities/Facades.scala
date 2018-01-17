package downloadmanager.utilities

trait DownloadManagerFacadeComponent {
  def utilFacade:UtilsComponent
  def configFacade:ConfigurationReaderComponent
}
trait DownloadManagerFacade extends DownloadManagerFacadeComponent {
  override def utilFacade: UtilsComponent = Utils
  override def configFacade: ConfigurationReaderComponent = ConfigurationReader
}