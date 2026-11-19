package de.seuhd.campuscoffee.api.dtos

import de.seuhd.campuscoffee.domain.model.ExpenseType
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/** Request body for recording an expense made for the Coffee Kitty. */
data class ExpenseRequestDto(
    @field:NotNull
    val amount: BigDecimal?,
    @field:NotNull
    val type: ExpenseType?,
    val beanName: String? = null,
    val weightGrams: Int? = null,
    val note: String? = null
)
