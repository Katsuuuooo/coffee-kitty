package de.seuhd.campuscoffee.domain.ports.api

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CoffeeConsumption
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import de.seuhd.campuscoffee.domain.model.User
import java.util.UUID

/** API port for administrator corrections of coffee counts and their audit records. */
interface CoffeeCorrectionService {
    /**
     * Corrects the coffee count of [userId] to [count], records [note], and returns the updated consumption.
     *
     * @param actingUser the user requesting the correction
     * @param userId the user whose count is corrected
     * @param count the corrected nonnegative coffee count
     * @param note the nonblank reason for the correction
     * @throws ForbiddenException if [actingUser] is not an admin
     * @throws ValidationException if [count] is negative or [note] is blank
     * @throws NotFoundException if [userId] does not identify a user
     */
    fun correctCoffeeCount(
        actingUser: User,
        userId: UUID,
        count: Int,
        note: String?
    ): CoffeeConsumption

    /**
     * Returns the correction records of [userId], newest first.
     *
     * @param userId the user whose corrections are returned
     */
    fun corrections(userId: UUID): List<CoffeeCorrection>
}
