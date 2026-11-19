package de.seuhd.campuscoffee.data.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Table
import java.math.BigDecimal

/** JPA entity for the single Coffee Kitty cash account. */
@jakarta.persistence.Entity
@Table(name = "cash_accounts")
class CashAccountEntity : Entity() {
    @field:Column(name = "balance", precision = 12, scale = 2)
    var balance: BigDecimal? = null
}
