package de.seuhd.campuscoffee.tests

import de.seuhd.campuscoffee.api.dtos.CashBalanceDto
import de.seuhd.campuscoffee.api.dtos.FinancialTransactionDto
import de.seuhd.campuscoffee.api.security.CurrentUserProvider
import de.seuhd.campuscoffee.domain.model.ExpenseType
import de.seuhd.campuscoffee.tests.SystemTestUtils.client
import org.springframework.http.MediaType
import java.math.BigDecimal
import java.util.UUID

/** Sends account requests for system tests without constructing HTTP calls at each test site. */
object AccountRequests {
    /** Records an expense for [userId]. */
    fun expense(
        userId: UUID,
        amount: BigDecimal,
        type: ExpenseType = ExpenseType.OTHER,
        beanName: String? = null,
        weightGrams: Int? = null,
        note: String? = null,
        actingUser: String = "maxmustermann"
    ): Response<FinancialTransactionDto> =
        client()
            .post()
            .uri("/api/users/{userId}/expenses", userId)
            .header(CurrentUserProvider.ACTING_USER_HEADER, actingUser)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                mapOf(
                    "amount" to amount,
                    "type" to type,
                    "beanName" to beanName,
                    "weightGrams" to weightGrams,
                    "note" to note
                )
            ).exchange()
            .toResponse()

    /** Records a cash payment for [userId] as [actingUser]. */
    fun payment(
        userId: UUID,
        amount: BigDecimal,
        actingUser: String = ConsumptionRequests.ADMIN_LOGIN_NAME
    ): Response<FinancialTransactionDto> =
        client()
            .post()
            .uri("/api/users/{userId}/payments", userId)
            .header(CurrentUserProvider.ACTING_USER_HEADER, actingUser)
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapOf("amount" to amount))
            .exchange()
            .toResponse()

    /** Reads the Coffee Kitty cash balance as [actingUser]. */
    fun cashBalance(actingUser: String = "jane_doe"): Response<CashBalanceDto> =
        client()
            .get()
            .uri("/api/kitty/balance")
            .header(CurrentUserProvider.ACTING_USER_HEADER, actingUser)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .toResponse()

    /** Sets the physical cash balance to [amount] as [actingUser]. */
    fun setCashBalance(
        amount: BigDecimal,
        actingUser: String = ConsumptionRequests.ADMIN_LOGIN_NAME
    ): Response<FinancialTransactionDto> =
        client()
            .put()
            .uri("/api/kitty/balance")
            .header(CurrentUserProvider.ACTING_USER_HEADER, actingUser)
            .contentType(MediaType.APPLICATION_JSON)
            .body(CashBalanceDto(amount))
            .exchange()
            .toResponse()

    /** Reads the financial transaction history as [actingUser]. */
    fun transactions(
        actingUser: String = ConsumptionRequests.ADMIN_LOGIN_NAME
    ): Response<List<FinancialTransactionDto>> =
        client()
            .get()
            .uri("/api/kitty/transactions")
            .header(CurrentUserProvider.ACTING_USER_HEADER, actingUser)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .toResponse()
}
