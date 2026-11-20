package de.seuhd.campuscoffee.data.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/** JPA audit row for a correction, including the target count, previous count, note, and admin. */
@jakarta.persistence.Entity
@Table(name = "coffee_corrections")
class CoffeeCorrectionEntity : Entity() {
    @field:ManyToOne(fetch = FetchType.EAGER)
    @field:JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @field:Column(name = "count_before", nullable = false)
    var countBefore: Int? = null

    @field:Column(name = "count_after", nullable = false)
    var countAfter: Int? = null

    @field:Column(name = "note", nullable = false, columnDefinition = "text")
    var note: String? = null

    @field:ManyToOne(fetch = FetchType.EAGER)
    @field:JoinColumn(name = "admin_id", nullable = false)
    var admin: UserEntity? = null
}
