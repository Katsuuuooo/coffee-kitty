package de.seuhd.campuscoffee.api.dtos

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/** Request body for recording a cash payment made by a user. */
data class PaymentRequestDto(
    @field:NotNull
    val amount: BigDecimal?
)
