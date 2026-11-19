package de.seuhd.campuscoffee.api.dtos

import java.math.BigDecimal

/** The physical cash balance of the Coffee Kitty in EUR. */
data class CashBalanceDto(
    val balance: BigDecimal
)
