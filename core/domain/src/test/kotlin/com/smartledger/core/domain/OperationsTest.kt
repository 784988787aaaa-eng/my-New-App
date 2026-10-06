package com.smartledger.core.domain
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test
class OperationsTest {
 @Test fun expenseRejectsZero() {
  try { Expense("e","office",Money.zero(),null,0); org.junit.Assert.fail("expected") } catch (_: IllegalArgumentException) {}
 }
 @Test fun purchaseOutstandingIsExact() {
  val p=PurchaseReceipt("p",null,listOf(PurchaseLine("x",2,Money.fromDecimal(BigDecimal("10.00")))),Money.fromDecimal(BigDecimal("5.00")))
  assertEquals(Money.fromDecimal(BigDecimal("15.00")),p.outstanding())
 }
}
