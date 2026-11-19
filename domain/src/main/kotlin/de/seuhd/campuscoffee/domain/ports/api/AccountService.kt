package de.seuhd.campuscoffee.domain.ports.api

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CashAccount
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import de.seuhd.campuscoffee.domain.model.User
import java.math.BigDecimal
import java.util.UUID

/** API port for recording expenses, payments, and cash balance corrections. */
interface AccountService {
    /** Records an expense for the acting user.
     *
     * @param userId the id of the user whose account receives the credit
     * @param amount the expense amount in EUR
     * @param type the kind of purchase
     * @param beanName the bean name for a bean purchase
     * @param weightGrams the bean weight for a bean purchase
     * @param note the optional note
     * @param actingUser the user who sends the request
     */
    fun recordExpense(
        userId: UUID,
        amount: BigDecimal,
        type: de.seuhd.campuscoffee.domain.model.ExpenseType,
        beanName: String?,
        weightGrams: Int?,
        note: String?,
        actingUser: User
    ): FinancialTransaction

    /** Records a payment from a user; only an admin may record it.
     *
     * @param userId the id of the user who paid
     * @param amount the payment amount in EUR
     * @param actingUser the user who sends the request
     */
    fun recordPayment(
        userId: UUID,
        amount: BigDecimal,
        actingUser: User
    ): FinancialTransaction

    /** Returns the current Coffee Kitty cash balance. */
    fun getCashAccount(): CashAccount

    /** Sets the physical cash balance; only an admin may perform this correction.
     *
     * @param amount the physically counted balance in EUR
     * @param actingUser the user who sends the request
     */
    fun setCashBalance(
        amount: BigDecimal,
        actingUser: User
    ): FinancialTransaction

    /** Returns transaction history; only an admin may read it.
     *
     * @param actingUser the user who sends the request
     */
    fun getTransactions(actingUser: User): List<FinancialTransaction>
}
