package de.seuhd.campuscoffee.api.dtos

import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/** Response body for a recorded financial transaction. */
data class FinancialTransactionDto(
    val id: UUID,
    val type: FinancialTransaction.Type,
    val amount: BigDecimal,
    val userId: UUID?,
    val actingUserId: UUID,
    val expenseType: ExpenseType?,
    val beanName: String?,
    val weightGrams: Int?,
    val note: String?,
    val createdAt: LocalDateTime?
)
