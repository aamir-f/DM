package downloadmanager.utilities

import org.slf4j.LoggerFactory

trait Logger {
  lazy val logger = LoggerFactory.getLogger(this.getClass.getName)
}