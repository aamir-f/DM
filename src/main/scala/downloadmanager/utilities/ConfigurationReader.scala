package downloadmanager.utilities

import com.typesafe.config.ConfigFactory

trait ConfigurationReaderComponent {
  val configuration = ConfigFactory.load()

  def getConfigurationProperty(propertyName: String): String = {
    configuration.getString(propertyName)
  }
}
object ConfigurationReader extends ConfigurationReaderComponent
