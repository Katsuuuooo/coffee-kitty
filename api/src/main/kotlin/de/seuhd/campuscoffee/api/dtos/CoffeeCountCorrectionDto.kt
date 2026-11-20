package de.seuhd.campuscoffee.api.dtos

import jakarta.validation.constraints.NotNull

/** Request body of `PUT /api/users/{userId}/consumption` for an administrator correction. */
data class CoffeeCountCorrectionDto(
    @field:NotNull
    val total: Int?,
    val note: String?
)
