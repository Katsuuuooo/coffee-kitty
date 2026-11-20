package de.seuhd.campuscoffee.domain.model

import java.time.LocalDateTime
import java.util.UUID

/** Audit record of an administrator's correction of a user's coffee count. */
data class CoffeeCorrection(
    override val id: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val user: User,
    val countBefore: Int,
    val countAfter: Int,
    val note: String,
    val admin: User
) : DomainModel<UUID>
