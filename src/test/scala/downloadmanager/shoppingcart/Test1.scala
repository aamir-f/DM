package downloadmanager.shoppingcart

import org.scalatest.{Matchers, WordSpec}

class ShoppingCartUtilitiesTest extends WordSpec with Matchers {

  "ShoppingCartUtilities Test" should {

    "check that no items are there in cart at start" in {
      val result = MockShoppingCart1.getCart
      val expectedResult = Nil
      result shouldBe expectedResult
    }
    "throw PriceValidationException if trying to create item with price less than zero" in {
      assertThrows[PriceValidationException] {
        Item("Dove Soap",1L,-20)
      }
    }
    "get total cart price after adding 5 all at once Dave Soaps to the cart" in {
      val daveSoap = Item("Dove Soap",5L,BigDecimal(39.99))
      MockShoppingCart2.addItem(daveSoap)
      val result = MockShoppingCart2.getTotalCartPrice
      val expectedResult = BigDecimal(199.95)
      result shouldBe expectedResult
    }
    "get total cart price after adding 5 separately Dave Soaps to the cart" in {
      val daveSoap1 = Item("Dove Soap",1L,BigDecimal(39.99))
      MockShoppingCart3.addItem(daveSoap1)
      val daveSoap2 = Item("Dove Soap",1L,BigDecimal(39.99))
      MockShoppingCart3.addItem(daveSoap2)
      val daveSoap3 = Item("Dove Soap",1L,BigDecimal(39.99))
      MockShoppingCart3.addItem(daveSoap3)
      val daveSoap4 = Item("Dove Soap",1L,BigDecimal(39.99))
      MockShoppingCart3.addItem(daveSoap4)
      val daveSoap5 = Item("Dove Soap",1L,BigDecimal(39.99))
      MockShoppingCart3.addItem(daveSoap5)

      val result = MockShoppingCart3.getTotalCartPrice
      val expectedResult = BigDecimal(199.95)
      result shouldBe expectedResult
    }
  }
}

object MockShoppingCart1 extends ShoppingCartComponent
object MockShoppingCart2 extends ShoppingCartComponent
object MockShoppingCart3 extends ShoppingCartComponent
