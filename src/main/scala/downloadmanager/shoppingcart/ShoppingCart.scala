package downloadmanager.shoppingcart

import scala.collection.mutable.ListBuffer
import MathImplicits.BigDecimalExtensions
case class PriceValidationException(message:String) extends Exception(message)
case class Item(name:String,quantity:Long,pricePerItem:BigDecimal) {
    pricePerItem match {
      case price if price > 0 => price
      case _ => throw PriceValidationException("price must be greater than zero")
    }
}
trait ShoppingCartComponent {

  private val cart = ListBuffer.empty[Item]
  private[shoppingcart] def addItem(item:Item) = {
     cart.append(item)
  }
  private[shoppingcart] def getCart = cart

  private[shoppingcart] def getTotalCartPrice = {
    cart.map{item =>
      item.quantity * item.pricePerItem
    }.sum.roundOffBigDecimal(2)
  }

}
object ShoppingCart extends ShoppingCartComponent


object MathImplicits {

  implicit class BigDecimalExtensions(value: BigDecimal) {
    def roundOffBigDecimal(precision: Int = 2): BigDecimal = {
      value.setScale(precision, BigDecimal.RoundingMode.HALF_UP)
    }

  }
}