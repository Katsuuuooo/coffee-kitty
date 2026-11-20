package de.seuhd.campuscoffee.data.persistence.repositories

import de.seuhd.campuscoffee.data.persistence.entities.CoffeeCorrectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/** Spring Data repository for correction audit records. */
interface CoffeeCorrectionRepository : JpaRepository<CoffeeCorrectionEntity, UUID> {
    /**
     * Returns the correction records of [userId], newest first.
     *
     * @param userId the target user's id
     */
    fun findByUserIdOrderByCreatedAtDescIdDesc(userId: UUID): List<CoffeeCorrectionEntity>
}
