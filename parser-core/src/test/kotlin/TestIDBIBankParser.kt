package com.pennywiseai.parser.core.bank

import com.pennywiseai.parser.core.TransactionType
import com.pennywiseai.parser.core.test.ExpectedTransaction
import com.pennywiseai.parser.core.test.ParserTestCase
import com.pennywiseai.parser.core.test.ParserTestUtils
import org.junit.jupiter.api.*
import java.math.BigDecimal

class IDBIBankParserTest {
    @TestFactory
    fun `test IDBI Bank Parser comprehensive test suite`(): List<DynamicTest> {
        val parser = IDBIBankParser()

        val testCases = listOf(
            // UPI Debit with "Bal Rs <bal> <merchant> credited" and "To Block UPI"
            ParserTestCase(
                name = "UPI Debit with merchant and block disclaimer",
                message = "IDBI Bank Acct XX608 debited for Rs 500.00 on 31-Oct-26; Bal Rs 50544.65 sruthisugan3025 credited. UPI:667076509289. To Block UPI send SMS UPIBLOCK <Mob. No> to 07799726511  or call 18002094324-IDBI Bank",
                sender = "BZ-IDBIBK-S",
                expected = ExpectedTransaction(
                    amount = BigDecimal("500.00"),
                    currency = "INR",
                    type = TransactionType.EXPENSE,
                    merchant = "sruthisugan3025",
                    accountLast4 = "608",
                    balance = BigDecimal("50544.65"),
                    reference = "667076509289",
                    isFromCard = false
                )
            ),

            // UPI Debit with merchant name Jayachandran T
            ParserTestCase(
                name = "UPI Debit to Jayachandran T",
                message = "IDBI Bank Acct XX608 debited for Rs 40.00 on 28-Oct-26; Bal Rs 18.65 JAYACHANDRAN T credited. UPI:630178402571. To Block UPI send SMS UPIBLOCK <Mob. No> to 07799726511  or call 18002094324-IDBI Bank",
                sender = "BZ-IDBIBK-S",
                expected = ExpectedTransaction(
                    amount = BigDecimal("40.00"),
                    currency = "INR",
                    type = TransactionType.EXPENSE,
                    merchant = "JAYACHANDRAN T",
                    accountLast4 = "608",
                    balance = BigDecimal("18.65"),
                    reference = "630178402571",
                    isFromCard = false
                )
            ),

            // Standard Debit with "debited with Rs 59.00"
            ParserTestCase(
                name = "Standard Debit with Rs",
                message = "Your account has been successfully debited with Rs 59.00 towards Netflix for AutoPay",
                sender = "IDBIBK",
                expected = ExpectedTransaction(
                    amount = BigDecimal("59.00"),
                    currency = "INR",
                    type = TransactionType.EXPENSE,
                    merchant = "Netflix",
                    isFromCard = false
                )
            ),

            // UPI Credit transaction
            ParserTestCase(
                name = "UPI Credit with sender",
                message = "Dear Customer, Acct XX608 is credited with Rs 500.00 on 24-Oct-26 from ARUNBALAJIRAMAL. UPI:203619224803 Avl Bal Rs 506.65-IDBI Bank",
                sender = "BZ-IDBIBK-S",
                expected = ExpectedTransaction(
                    amount = BigDecimal("500.00"),
                    currency = "INR",
                    type = TransactionType.INCOME,
                    merchant = "ARUNBALAJIRAMAL",
                    accountLast4 = "608",
                    balance = BigDecimal("506.65"),
                    reference = "203619224803",
                    isFromCard = false
                )
            ),

            // Negative test case: OTP
            ParserTestCase(
                name = "OTP Message Should Not Parse",
                message = "Your OTP for IDBI Bank net banking transaction is 654321. Do not share with anyone.",
                sender = "IDBIBK",
                shouldParse = false
            ),

            // Negative test case: Promo
            ParserTestCase(
                name = "Promotional Message Should Not Parse",
                message = "IDBI Bank: Enjoy 10% discount offer on shopping this festive season! T&C apply.",
                sender = "IDBIBK",
                shouldParse = false
            )
        )

        val handleCases: List<Pair<String, Boolean>> = listOf(
            "BZ-IDBIBK-S" to true,
            "IDBIBK" to true,
            "IDBIBANK" to true,
            "AX-IDBI-S" to true,
            "HDFC" to false,
            "" to false
        )

        return ParserTestUtils.runTestSuite(
            parser,
            testCases,
            handleCases,
            "IDBI Bank Parser Tests"
        )
    }
}
