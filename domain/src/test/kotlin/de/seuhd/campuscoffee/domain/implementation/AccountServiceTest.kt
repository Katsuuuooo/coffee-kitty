package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CashAccount
import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import de.seuhd.campuscoffee.domain.model.Role
import de.seuhd.campuscoffee.domain.model.User
import de.seuhd.campuscoffee.domain.ports.data.CashAccountDataService
import de.seuhd.campuscoffee.domain.ports.data.FinancialTransactionDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID

/** Unit tests for the financial account rules with mocked data ports. */
class AccountServiceTest {
    private val userData: UserDataService = mock()
    private val cashData: CashAccountDataService = mock()
    private val transactionData: FinancialTransactionDataService = mock()
    private val service = AccountServiceImpl(userData, cashData, transactionData)
    private val user =
        User(id = UUID(0, 1), loginName = "max", emailAddress = "max@se.de", firstName = "Max", lastName = "M")
    private val admin = user.copy(id = UUID(0, 2), loginName = "jane", role = Role.ADMIN)

    private fun stubUpserts() {
        whenever(userData.upsert(any())).thenAnswer { it.arguments[0] as User }
        whenever(cashData.upsert(any())).thenAnswer { it.arguments[0] as CashAccount }
        whenever(transactionData.upsert(any())).thenAnswer { it.arguments[0] as FinancialTransaction }
    }

    @Test
    fun `recordExpense credits the user's balance and stores an expense`() {
        whenever(userData.getByIdForUpdate(user.id!!)).thenReturn(user.copy(balance = BigDecimal("10.00")))
        stubUpserts()

        val result =
            service.recordExpense(
                user.id!!,
                BigDecimal("12.50"),
                ExpenseType.OTHER,
                null,
                null,
                "Filters",
                user
            )

        assertThat(result.type).isEqualTo(FinancialTransaction.Type.EXPENSE)
        assertThat(result.amount).isEqualByComparingTo("12.50")
        verify(userData).upsert(user.copy(balance = BigDecimal("22.50")))
    }

    @Test
    fun `recordExpense accepts the minimum and maximum allowed amounts`() {
        whenever(userData.getByIdForUpdate(user.id!!)).thenReturn(user)
        stubUpserts()

        service.recordExpense(user.id!!, BigDecimal("0.01"), ExpenseType.OTHER, null, null, null, user)
        service.recordExpense(user.id!!, BigDecimal("1000.00"), ExpenseType.OTHER, null, null, null, user)

        verify(transactionData, org.mockito.kotlin.times(2)).upsert(any())
    }

    @Test
    fun `recordExpense stores bean details`() {
        whenever(userData.getByIdForUpdate(user.id!!)).thenReturn(user)
        stubUpserts()

        val result =
            service.recordExpense(
                user.id!!,
                BigDecimal("12.50"),
                ExpenseType.BEANS,
                "Arabica",
                500,
                null,
                user
            )

        assertThat(result.beanName).isEqualTo("Arabica")
        assertThat(result.weightGrams).isEqualTo(500)
    }

    @Test
    fun `recordExpense rejects a user recording an expense for another user`() {
        assertThrows<ForbiddenException> {
            service.recordExpense(UUID(0, 3), BigDecimal("12.50"), ExpenseType.OTHER, null, null, null, user)
        }
        verify(userData, never()).upsert(any())
    }

    @Test
    fun `recordExpense rejects a zero amount`() {
        assertThrows<ValidationException> {
            service.recordExpense(user.id!!, BigDecimal.ZERO, ExpenseType.OTHER, null, null, null, user)
        }
        verify(userData, never()).upsert(any())
    }

    @Test
    fun `recordExpense rejects an amount over 1000 euros`() {
        assertThrows<ValidationException> {
            service.recordExpense(user.id!!, BigDecimal("1000.01"), ExpenseType.OTHER, null, null, null, user)
        }
        verify(userData, never()).upsert(any())
    }

    @Test
    fun `recordExpense rejects a bean weight outside 100 to 1000 grams`() {
        assertThrows<ValidationException> {
            service.recordExpense(user.id!!, BigDecimal("12.50"), ExpenseType.BEANS, "Arabica", 99, null, user)
        }
        verify(userData, never()).upsert(any())
    }

    @Test
    fun `recordPayment credits the user and the Kitty cash balance`() {
        whenever(userData.getByIdForUpdate(user.id!!)).thenReturn(user.copy(balance = BigDecimal("10.00")))
        whenever(cashData.getForUpdate()).thenReturn(CashAccount(CashAccount.ID, BigDecimal("5.00")))
        stubUpserts()

        val result = service.recordPayment(user.id!!, BigDecimal("12.50"), admin)

        assertThat(result.type).isEqualTo(FinancialTransaction.Type.PAYMENT)
        verify(userData).upsert(user.copy(balance = BigDecimal("22.50")))
        verify(cashData).upsert(CashAccount(CashAccount.ID, BigDecimal("17.50")))
    }

    @Test
    fun `recordPayment accepts the maximum allowed amount`() {
        whenever(userData.getByIdForUpdate(user.id!!)).thenReturn(user)
        whenever(cashData.getForUpdate()).thenReturn(CashAccount(CashAccount.ID, BigDecimal("0.00")))
        stubUpserts()

        service.recordPayment(user.id!!, BigDecimal("1000.00"), admin)

        verify(cashData).upsert(CashAccount(CashAccount.ID, BigDecimal("1000.00")))
    }

    @Test
    fun `recordPayment rejects a zero amount`() {
        assertThrows<ValidationException> { service.recordPayment(user.id!!, BigDecimal.ZERO, admin) }
        verify(cashData, never()).upsert(any())
    }

    @Test
    fun `recordPayment requires an admin`() {
        assertThrows<ForbiddenException> { service.recordPayment(user.id!!, BigDecimal("1.00"), user) }
        verify(userData, never()).upsert(any())
    }

    @Test
    fun `setCashBalance records the difference from the current balance`() {
        whenever(cashData.getForUpdate()).thenReturn(CashAccount(CashAccount.ID, BigDecimal("17.50")))
        stubUpserts()

        val result = service.setCashBalance(BigDecimal.ZERO, admin)

        assertThat(result.type).isEqualTo(FinancialTransaction.Type.CASH_ADJUSTMENT)
        assertThat(result.amount).isEqualByComparingTo("-17.50")
        verify(cashData).upsert(CashAccount(CashAccount.ID, BigDecimal.ZERO))
    }

    @Test
    fun `setCashBalance requires an admin and rejects a negative balance`() {
        assertThrows<ForbiddenException> { service.setCashBalance(BigDecimal("10.00"), user) }
        assertThrows<ValidationException> { service.setCashBalance(BigDecimal("-0.01"), admin) }
        verify(cashData, never()).upsert(any())
    }
}
