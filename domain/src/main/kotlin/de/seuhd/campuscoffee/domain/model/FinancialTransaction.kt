package de.seuhd.campuscoffee.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/** A recorded expense, payment, or cash balance correction. */
data class FinancialTransaction(
    override val id: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val type: Type,
    val amount: BigDecimal,
    val userId: UUID? = null,
    val actingUserId: UUID,
    val expenseType: ExpenseType? = null,
    val beanName: String? = null,
    val weightGrams: Int? = null,
    val note: String? = null
) : DomainModel<UUID> {
    /** The kind of financial transaction. */
    enum class Type {
        /** An expense paid by a user for the Coffee Kitty. */
        EXPENSE,

        /** A cash payment made by a user to the admin. */
        PAYMENT,

        /** An admin's correction to match the physically counted cash. */
        CASH_ADJUSTMENT
    }
}
