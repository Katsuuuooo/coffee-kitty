package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import java.util.UUID

/** Data port for the audit records of corrected coffee counts. */
interface CoffeeCorrectionDataService : CrudDataService<CoffeeCorrection, UUID> {
    /**
     * Returns correction records for [userId], newest first.
     *
     * @param userId the user whose corrections are returned
     */
    fun getByUserId(userId: UUID): List<CoffeeCorrection>
}
