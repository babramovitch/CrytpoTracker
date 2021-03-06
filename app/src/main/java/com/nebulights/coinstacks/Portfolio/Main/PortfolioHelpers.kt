package com.nebulights.coinstacks.Portfolio.Main

import com.nebulights.coinstacks.Extensions.isNumber
import java.math.BigDecimal
import java.text.DecimalFormat

/**
* Created by babramovitch on 11/6/2017.
*/

class PortfolioHelpers {

    companion object {
        fun stringSafeBigDecimal(value: String): BigDecimal =
                if (value.isNumber()) BigDecimal(value) else BigDecimal(0.00)

        fun currencyFormatter(): DecimalFormat = DecimalFormat("$###,###,##0.00")

        fun smallCurrencyFormatter(): DecimalFormat = DecimalFormat("$###,###,##0.00000")
    }
}