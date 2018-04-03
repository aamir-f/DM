package downloadmanager


import org.scalatest.FunSuite

class FactorialSpec extends FunSuite {

  test("Factorial object calculate factorial of a number passed")  {
    val num = 5
    val result = CalculateFactorial.Factorial(num)
    val expectedResult = 120
    assert(result == expectedResult)
  }

  test("IfTest returns valid user when status is true") {
    val expectedResult = "user is valid"
    val someValue = true
    val result = IfTests.checkUserStatus(someValue)
    assert(result == expectedResult)
  }
  test("IfTest throw exception when status is false") {
    val someValue = false
    intercept[Exception] {
      IfTests.checkUserStatus(someValue)    }
  }
}


object FactorialExample {
  def main(args: Array[String]): Unit = {
    println("Facotrial of 120 is : " +CalculateFactorial.Factorial(5))
  }


}

object CalculateFactorial {
  def Factorial(d: BigInt): BigInt ={
    if(d==0)1
    else d * Factorial(d-1)
  }
}

object IfTests {
  val status = true
  def checkUserStatus(status:Boolean): String =
  if(status) "user is valid" else {
    throw new Exception("invalid user")
  }
}