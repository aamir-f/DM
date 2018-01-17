package downloadmanager.utilities

trait DownloadManagerFacadeComponent {
  def utilFacade:UtilsComponent
}
trait DownloadManagerFacade extends DownloadManagerFacadeComponent {
  override def utilFacade: UtilsComponent = Utils
}