package downloadmanager


import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

// a very simple User class
case class User(name: String)

// a LoginService must have a 'login' method
trait LoginService {
  def login(name: String, password: String): Option[User]
}

// the code for our real/live LoginService
class RealLoginService extends LoginService {
  // implementation here ...
  override def login(name: String, password: String): Option[User] = {
    Some(User(name))
  }
}

class LoginServiceTests extends FunSuite with BeforeAndAfter with MockitoSugar {

  test ("test login service") {

    // (1) init
    val service = mock[LoginService]

    // (2) setup: when someone logs in as "johndoe", the service should work;
    //            when they try to log in as "joehacker", it should fail.
    when(service.login("johndoe", "secret")).thenReturn(Some(User("johndoe")))
    when(service.login("joehacker", "secret")).thenReturn(None)

    // (3) access the service
    val johndoe = service.login("johndoe", "secret")
    val joehacker = service.login("joehacker", "secret")

    // (4) verify the results
    assert(johndoe.get == User("johndoe"))
    assert(joehacker.isEmpty)


  }

}