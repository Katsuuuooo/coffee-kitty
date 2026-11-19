package de.seuhd.campuscoffee.data.persistence.entities

import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

/** JPA entity that stores the audit history of expense, payment, and cash adjustment entries. */
@jakarta.persistence.Entity
@Table(name = "financial_transactions")
class FinancialTransactionEntity : Entity() {
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "type")
    var type: FinancialTransaction.Type? = null

    @field:Column(name = "amount", precision = 12, scale = 2)
    var amount: BigDecimal? = null

    @field:Column(name = "user_id")
    var userId: UUID? = null

    @field:Column(name = "acting_user_id")
    var actingUserId: UUID? = null

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "expense_type")
    var expenseType: ExpenseType? = null

    @field:Column(name = "bean_name")
    var beanName: String? = null

    @field:Column(name = "weight_grams")
    var weightGrams: Int? = null

    @field:Column(name = "note")
    var note: String? = null
}
