package de.seuhd.campuscoffee.domain.model

import java.math.BigDecimal
import java.util.UUID

/** The Coffee Kitty's physical cash balance. */
data class CashAccount(
    override val id: UUID,
    val balance: BigDecimal
) : DomainModel<UUID> {
    companion object {
        /** Stable id of the single cash account. */
        val ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}
