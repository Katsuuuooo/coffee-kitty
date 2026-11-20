package de.seuhd.campuscoffee.api.dtos

/** Public view of a recorded coffee count correction. */
data class CoffeeCorrectionDto(
    val countBefore: Int,
    val countAfter: Int,
    val note: String,
    val adminLoginName: String
)
