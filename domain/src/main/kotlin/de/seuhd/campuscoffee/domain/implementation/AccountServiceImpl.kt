package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CashAccount
import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import de.seuhd.campuscoffee.domain.model.User
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.domain.ports.api.AccountService
import de.seuhd.campuscoffee.domain.ports.data.CashAccountDataService
import de.seuhd.campuscoffee.domain.ports.data.FinancialTransactionDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/** Implements financial account rules and records every change in the transaction history. */
@Service
class AccountServiceImpl(
    private val userDataService: UserDataService,
    private val cashAccountDataService: CashAccountDataService,
    private val financialTransactionDataService: FinancialTransactionDataService
) : AccountService {
    @Transactional
    override fun recordExpense(
        userId: UUID,
        amount: BigDecimal,
        type: ExpenseType,
        beanName: String?,
        weightGrams: Int?,
        note: String?,
        actingUser: User
    ): FinancialTransaction {
        validateAmount(amount)
        validateExpense(type, beanName, weightGrams, note)
        if (actingUser.id != userId) {
            throw ForbiddenException("A user may record expenses only for themselves.")
        }
        val user = userDataService.getByIdForUpdate(userId)
        userDataService.upsert(user.copy(balance = user.balance.add(amount)))
        return financialTransactionDataService.upsert(
            FinancialTransaction(
                type = FinancialTransaction.Type.EXPENSE,
                amount = amount,
                userId = userId,
                actingUserId = actingUser.persistedId,
                expenseType = type,
                beanName = beanName,
                weightGrams = weightGrams,
                note = note
            )
        )
    }

    @Transactional
    override fun recordPayment(
        userId: UUID,
        amount: BigDecimal,
        actingUser: User
    ): FinancialTransaction {
        requireAdmin(actingUser, "record a payment")
        validateAmount(amount)
        val user = userDataService.getByIdForUpdate(userId)
        val cashAccount = cashAccountDataService.getForUpdate()
        userDataService.upsert(user.copy(balance = user.balance.add(amount)))
        cashAccountDataService.upsert(cashAccount.copy(balance = cashAccount.balance.add(amount)))
        return financialTransactionDataService.upsert(
            FinancialTransaction(
                type = FinancialTransaction.Type.PAYMENT,
                amount = amount,
                userId = userId,
                actingUserId = actingUser.persistedId
            )
        )
    }

    override fun getCashAccount(): CashAccount = cashAccountDataService.getById(CashAccount.ID)

    @Transactional
    override fun setCashBalance(
        amount: BigDecimal,
        actingUser: User
    ): FinancialTransaction {
        requireAdmin(actingUser, "adjust the Coffee Kitty cash balance")
        if (amount < BigDecimal.ZERO || amount.scale() > 2) {
            throw ValidationException("The cash balance must be non-negative and have at most two decimal places.")
        }
        val account = cashAccountDataService.getForUpdate()
        val difference = amount.subtract(account.balance)
        cashAccountDataService.upsert(account.copy(balance = amount))
        return financialTransactionDataService.upsert(
            FinancialTransaction(
                type = FinancialTransaction.Type.CASH_ADJUSTMENT,
                amount = difference,
                actingUserId = actingUser.persistedId
            )
        )
    }

    override fun getTransactions(actingUser: User): List<FinancialTransaction> {
        requireAdmin(actingUser, "read the financial transaction history")
        return financialTransactionDataService.getAll()
    }

    /** Checks the currency range and precision of [amount].
     *
     * @param amount the amount in EUR
     * @throws ValidationException if the amount is outside the accepted range or has fractional cents
     */
    private fun validateAmount(amount: BigDecimal) {
        val message =
            when {
                amount < MIN_AMOUNT -> "The amount must be at least 0.01 EUR."
                amount > MAX_AMOUNT -> "The amount must be at most 1000.00 EUR."
                amount.scale() > 2 -> "The amount must have at most two decimal places."
                else -> null
            }
        message?.let { throw ValidationException(it) }
    }

    /** Checks the purchase details of an expense.
     *
     * @param type the kind of purchase
     * @param beanName the bean name for a bean purchase
     * @param weightGrams the bean weight for a bean purchase
     * @param note the optional note
     * @throws ValidationException if the details do not match the purchase type
     */
    private fun validateExpense(
        type: ExpenseType,
        beanName: String?,
        weightGrams: Int?,
        note: String?
    ) {
        val message =
            when {
                note != null && note.length > MAX_NOTE_LENGTH -> {
                    "A note must have at most $MAX_NOTE_LENGTH characters."
                }

                type == ExpenseType.BEANS &&
                    (beanName.isNullOrBlank() || beanName.length > MAX_BEAN_NAME_LENGTH) -> {
                    "A bean purchase needs a bean name of 1 to $MAX_BEAN_NAME_LENGTH characters."
                }

                type == ExpenseType.BEANS &&
                    (weightGrams == null || weightGrams !in MIN_BEAN_WEIGHT..MAX_BEAN_WEIGHT) -> {
                    "A bean purchase needs a weight of 100 to 1000 grams."
                }

                type == ExpenseType.OTHER && (beanName != null || weightGrams != null) -> {
                    "Only a bean purchase may include a bean name or weight."
                }

                else -> {
                    null
                }
            }
        message?.let { throw ValidationException(it) }
    }

    private companion object {
        val MIN_AMOUNT: BigDecimal = BigDecimal("0.01")
        val MAX_AMOUNT: BigDecimal = BigDecimal("1000.00")
        const val MAX_NOTE_LENGTH = 500
        const val MAX_BEAN_NAME_LENGTH = 255
        const val MIN_BEAN_WEIGHT = 100
        const val MAX_BEAN_WEIGHT = 1000
    }
}
