package de.seuhd.campuscoffee.api.controller

import de.seuhd.campuscoffee.api.dtos.CashBalanceDto
import de.seuhd.campuscoffee.api.dtos.ExpenseRequestDto
import de.seuhd.campuscoffee.api.dtos.FinancialTransactionDto
import de.seuhd.campuscoffee.api.dtos.PaymentRequestDto
import de.seuhd.campuscoffee.api.mapper.FinancialTransactionDtoMapper
import de.seuhd.campuscoffee.api.security.CurrentUserProvider
import de.seuhd.campuscoffee.domain.ports.api.AccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import io.swagger.v3.oas.annotations.parameters.RequestBody as ApiRequestBody

/** REST endpoints for user expenses, payments, and the Coffee Kitty cash balance. */
@Tag(name = "Accounts", description = "Recording expenses and payments and managing the cash balance.")
@RestController
@RequestMapping
class AccountController(
    private val accountService: AccountService,
    private val currentUserProvider: CurrentUserProvider,
    private val transactionDtoMapper: FinancialTransactionDtoMapper
) {
    /** Records an expense for the acting user.
     *
     * @param userId the id of the user who made the purchase
     * @param dto the expense details
     */
    @Operation(summary = "Record an expense for a user.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/expenses")
    fun recordExpense(
        @Parameter(description = "Unique identifier of the user.", required = true)
        @PathVariable userId: UUID,
        @ApiRequestBody(description = "The expense to record.", required = true)
        @RequestBody
        @Valid dto: ExpenseRequestDto
    ): ResponseEntity<FinancialTransactionDto> {
        val actingUser = currentUserProvider.currentUser()
        val transaction =
            accountService.recordExpense(
                userId,
                requireNotNull(dto.amount),
                requireNotNull(dto.type),
                dto.beanName,
                dto.weightGrams,
                dto.note,
                actingUser
            )
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDtoMapper.fromDomain(transaction))
    }

    /** Records a user's cash payment; only an admin may use this endpoint.
     *
     * @param userId the id of the user who paid
     * @param dto the payment amount
     */
    @Operation(summary = "Record a payment from a user (admin).")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/payments")
    fun recordPayment(
        @Parameter(description = "Unique identifier of the user.", required = true)
        @PathVariable userId: UUID,
        @ApiRequestBody(description = "The payment amount in EUR.", required = true)
        @RequestBody
        @Valid dto: PaymentRequestDto
    ): ResponseEntity<FinancialTransactionDto> {
        val transaction =
            accountService.recordPayment(userId, requireNotNull(dto.amount), currentUserProvider.currentUser())
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDtoMapper.fromDomain(transaction))
    }

    /** Returns the physical cash balance of the Coffee Kitty. */
    @Operation(summary = "Get the Coffee Kitty cash balance.")
    @GetMapping("/kitty/balance")
    fun getCashBalance(): ResponseEntity<CashBalanceDto> =
        ResponseEntity.ok(CashBalanceDto(accountService.getCashAccount().balance))

    /** Sets the physical cash balance to the amount counted by an admin.
     *
     * @param dto the physically counted balance
     */
    @Operation(summary = "Set the physically counted Coffee Kitty cash balance (admin).")
    @PutMapping("/kitty/balance")
    fun setCashBalance(
        @ApiRequestBody(description = "The physically counted cash balance in EUR.", required = true)
        @RequestBody
        @Valid dto: CashBalanceDto
    ): ResponseEntity<FinancialTransactionDto> {
        val transaction = accountService.setCashBalance(dto.balance, currentUserProvider.currentUser())
        return ResponseEntity.ok(transactionDtoMapper.fromDomain(transaction))
    }

    /** Returns all financial transactions; only an admin may read the history. */
    @Operation(summary = "Get the financial transaction history (admin).")
    @GetMapping("/kitty/transactions")
    fun getTransactions(): ResponseEntity<List<FinancialTransactionDto>> {
        val actingUser = currentUserProvider.currentUser()
        val transactions = accountService.getTransactions(actingUser).map(transactionDtoMapper::fromDomain)
        return ResponseEntity.ok(transactions)
    }
}
