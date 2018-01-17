package downloadmanager.utilities
import scala.concurrent.duration._
object HttpResponseTimeout {
  final lazy val timeout = 4.second
}