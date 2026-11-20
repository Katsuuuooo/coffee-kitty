package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CoffeeConsumption
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import de.seuhd.campuscoffee.domain.model.User
import de.seuhd.campuscoffee.domain.ports.api.CoffeeCorrectionService
import de.seuhd.campuscoffee.domain.ports.data.CoffeeConsumptionDataService
import de.seuhd.campuscoffee.domain.ports.data.CoffeeCorrectionDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/** Applies administrator corrections to a user's coffee count. */
@Service
class CoffeeCorrectionServiceImpl(
    private val coffeeConsumptionDataService: CoffeeConsumptionDataService,
    private val coffeeCorrectionDataService: CoffeeCorrectionDataService
) : CoffeeCorrectionService {
    /**
     * Corrects the coffee count of [userId] to [count] and returns the stored consumption.
     *
     * @param actingUser the user who requests the correction
     * @param userId the id of the user whose count is corrected
     * @param count the corrected coffee count
     * @param note the reason for the correction
     * @throws de.seuhd.campuscoffee.domain.exceptions.ForbiddenException if [actingUser] is not an admin
     * @throws ValidationException if [count] is negative or [note] is blank
     */
    @Transactional
    override fun correctCoffeeCount(
        actingUser: User,
        userId: UUID,
        count: Int,
        note: String?
    ): CoffeeConsumption {
        requireAdmin(actingUser, "correct a coffee count")
        if (count < 0) {
            throw ValidationException("The coffee count must not be negative.")
        }
        if (note.isNullOrBlank()) {
            throw ValidationException("The correction note must not be blank.")
        }
        val current = coffeeConsumptionDataService.getByUserIdForUpdate(userId)
        val removedCount = current.count - count
        val coffeeTimes = if (removedCount > 0) current.coffeeTimes.dropLast(removedCount) else current.coffeeTimes
        val updated = coffeeConsumptionDataService.upsert(current.copy(count = count, coffeeTimes = coffeeTimes))
        coffeeCorrectionDataService.upsert(
            CoffeeCorrection(
                user = current.user,
                countBefore = current.count,
                countAfter = count,
                note = note,
                admin = actingUser
            )
        )
        return updated
    }

    /** Returns the correction records of [userId], newest first. */
    override fun corrections(userId: UUID): List<CoffeeCorrection> = coffeeCorrectionDataService.getByUserId(userId)
}
