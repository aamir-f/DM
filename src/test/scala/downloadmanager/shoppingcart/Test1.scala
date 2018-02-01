package downloadmanager.shoppingcart

import org.scalatest.{Matchers, WordSpec}

class ShoppingCartUtilitiesTest extends WordSpec with Matchers {

  "ShoppingCartUtilities Test" should {
    "check that no items are there in cart at start" in {
      val result = ShoppingCart.getCart
      val expectedResult = Nil
      result shouldBe expectedResult
    }
    "throw PriceValidationException if trying to create item with price less than zero" in {
      assertThrows[PriceValidationException] {
        Item  ("Dove Soap",1L,-20)
      }
    }
    "get total cart price after adding 5  Dave Soaps to the cart" in {
      val daveSoap = Item("Dove Soap",5L,BigDecimal(39.99))
        ShoppingCart.addItem(daveSoap)
      val result = ShoppingCart.getTotalCartPrice
      val expectedResult = BigDecimal(199.95)
      result shouldBe expectedResult
    }
  }
}
