package de.seuhd.campuscoffee.tests.system

import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.tests.AccountRequests
import de.seuhd.campuscoffee.tests.ConsumptionRequests
import de.seuhd.campuscoffee.tests.UserRequests
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/** System tests for expense recording, payments, and the Coffee Kitty cash balance. */
class AccountSystemTests : AbstractSystemTest() {
    private fun userId(): java.util.UUID = fixtureUser("maxmustermann").persistedId

    private fun userBalance(): BigDecimal = UserRequests.retrieveById(userId()).body.balance!!

    @Test
    fun `recording an expense credits the acting user's balance and stores the transaction`() {
        val response = AccountRequests.expense(userId(), BigDecimal("12.50"), note = "Filters")

        assertThat(response.status).isEqualTo(201)
        assertThat(response.body.type.name).isEqualTo("EXPENSE")
        assertThat(userBalance()).isEqualByComparingTo("12.50")
    }

    @Test
    fun `recording a bean expense stores its name and weight`() {
        val response = AccountRequests.expense(userId(), BigDecimal("12.50"), ExpenseType.BEANS, "Arabica", 500)

        assertThat(response.status).isEqualTo(201)
        assertThat(response.body.beanName).isEqualTo("Arabica")
        assertThat(response.body.weightGrams).isEqualTo(500)
    }

    @Test
    fun `recording a zero expense returns 400 and leaves the balance unchanged`() {
        val response = AccountRequests.expense(userId(), BigDecimal.ZERO)

        assertThat(response.status).isEqualTo(400)
        assertThat(userBalance()).isEqualByComparingTo("0.00")
    }

    @Test
    fun `recording an expense over 1000 euros returns 400 and leaves the balance unchanged`() {
        val response = AccountRequests.expense(userId(), BigDecimal("1000.01"))

        assertThat(response.status).isEqualTo(400)
        assertThat(userBalance()).isEqualByComparingTo("0.00")
    }

    @Test
    fun `recording an expense for another user returns 403`() {
        val otherUser = fixtureUser("lisa_lee").persistedId

        val response = AccountRequests.expense(otherUser, BigDecimal("12.50"))

        assertThat(response.status).isEqualTo(403)
    }

    @Test
    fun `recording a payment credits the user and the cash balance`() {
        AccountRequests.expense(userId(), BigDecimal("10.00")).requireStatus(201)
        AccountRequests.setCashBalance(BigDecimal("5.00")).requireStatus(200)

        val response = AccountRequests.payment(userId(), BigDecimal("12.50"))

        assertThat(response.status).isEqualTo(201)
        assertThat(userBalance()).isEqualByComparingTo("22.50")
        assertThat(AccountRequests.cashBalance().body.balance).isEqualByComparingTo("17.50")
    }

    @Test
    fun `recording a zero payment returns 400`() {
        val response = AccountRequests.payment(userId(), BigDecimal.ZERO)

        assertThat(response.status).isEqualTo(400)
    }

    @Test
    fun `recording a payment over 1000 euros returns 400`() {
        val response = AccountRequests.payment(userId(), BigDecimal("1000.01"))

        assertThat(response.status).isEqualTo(400)
    }

    @Test
    fun `recording a payment as a non-admin returns 403`() {
        val response = AccountRequests.payment(userId(), BigDecimal("12.50"), "maxmustermann")

        assertThat(response.status).isEqualTo(403)
    }

    @Test
    fun `admin can reconcile the physical cash balance down to zero`() {
        AccountRequests.payment(userId(), BigDecimal("12.50")).requireStatus(201)

        val response = AccountRequests.setCashBalance(BigDecimal.ZERO)

        assertThat(response.status).isEqualTo(200)
        assertThat(AccountRequests.cashBalance().body.balance).isEqualByComparingTo("0.00")
    }

    @Test
    fun `admin can read the financial transaction history`() {
        AccountRequests.expense(userId(), BigDecimal("12.50")).requireStatus(201)

        val response = AccountRequests.transactions()

        assertThat(response.status).isEqualTo(200)
        assertThat(response.body).hasSize(1)
        assertThat(response.body.single().userId).isEqualTo(userId())
    }

    @Test
    fun `non-admin cannot read the financial transaction history`() {
        val response = AccountRequests.transactions("maxmustermann")

        assertThat(response.status).isEqualTo(403)
    }

    @Test
    fun `non-admin cannot reconcile the physical cash balance`() {
        val response = AccountRequests.setCashBalance(BigDecimal("5.00"), "maxmustermann")

        assertThat(response.status).isEqualTo(403)
    }
}
