package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.CoffeeConsumption
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import de.seuhd.campuscoffee.domain.model.Role
import de.seuhd.campuscoffee.domain.model.User
import de.seuhd.campuscoffee.domain.ports.data.CoffeeConsumptionDataService
import de.seuhd.campuscoffee.domain.ports.data.CoffeeCorrectionDataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.UUID

class CoffeeCorrectionServiceTest {
    private val consumptionDataService: CoffeeConsumptionDataService = mock()
    private val correctionDataService: CoffeeCorrectionDataService = mock()
    private val service = CoffeeCorrectionServiceImpl(consumptionDataService, correctionDataService)
    private val target =
        User(
            id = UUID(0L, 1L),
            loginName = "maxmustermann",
            emailAddress = "max@se.de",
            firstName = "Max",
            lastName = "Mustermann"
        )
    private val admin =
        User(
            id = UUID(0L, 2L),
            loginName = "jane_doe",
            emailAddress = "jane@se.de",
            firstName = "Jane",
            lastName = "Doe",
            role = Role.ADMIN
        )
    private val consumption =
        CoffeeConsumption(
            id = UUID(0L, 3L),
            user = target,
            count = 3
        )

    @Test
    fun `correction to a negative count throws ValidationException and writes nothing`() {
        assertThrows<ValidationException> {
            service.correctCoffeeCount(admin, target.id!!, -2, "Double tap on 16.11.")
        }

        verify(consumptionDataService, never()).upsert(any())
        verify(correctionDataService, never()).upsert(any())
    }

    @Test
    fun `correction with a blank note throws ValidationException and writes nothing`() {
        assertThrows<ValidationException> {
            service.correctCoffeeCount(admin, target.id!!, 2, " ")
        }

        verify(consumptionDataService, never()).upsert(any())
        verify(correctionDataService, never()).upsert(any())
    }

    @Test
    fun `non-admin correction throws ForbiddenException and writes nothing`() {
        assertThrows<ForbiddenException> {
            service.correctCoffeeCount(target, target.id!!, 2, "Double tap on 16.11.")
        }

        verify(consumptionDataService, never()).upsert(any())
        verify(correctionDataService, never()).upsert(any())
    }

    @Test
    fun `lowering the count removes recent times even when expired times were already discarded`() {
        val recentTime = LocalDateTime.parse("2026-11-16T10:00:00")
        val current = consumption.copy(coffeeTimes = listOf(recentTime))
        whenever(consumptionDataService.getByUserIdForUpdate(target.id!!)).thenReturn(current)
        whenever(consumptionDataService.upsert(any())).thenAnswer { it.arguments[0] as CoffeeConsumption }

        service.correctCoffeeCount(admin, target.id!!, 2, "Double tap on 16.11.")

        verify(consumptionDataService).upsert(current.copy(count = 2, coffeeTimes = emptyList()))
    }

    @Test
    fun `lowering the count removes the newest coffee times`() {
        val times =
            listOf(
                LocalDateTime.parse("2026-11-16T08:00:00"),
                LocalDateTime.parse("2026-11-16T09:00:00"),
                LocalDateTime.parse("2026-11-16T10:00:00")
            )
        val current = consumption.copy(coffeeTimes = times)
        whenever(consumptionDataService.getByUserIdForUpdate(target.id!!)).thenReturn(current)
        whenever(consumptionDataService.upsert(any())).thenAnswer { it.arguments[0] as CoffeeConsumption }

        service.correctCoffeeCount(admin, target.id!!, 2, "Double tap on 16.11.")

        verify(consumptionDataService).upsert(current.copy(count = 2, coffeeTimes = times.take(2)))
    }

    @Test
    fun `admin correction stores the new count and its audit details`() {
        whenever(consumptionDataService.getByUserIdForUpdate(target.id!!)).thenReturn(consumption)
        whenever(consumptionDataService.upsert(any())).thenAnswer { it.arguments[0] as CoffeeConsumption }

        val result = service.correctCoffeeCount(admin, target.id!!, 2, "Double tap on 16.11.")

        assertThat(result.count).isEqualTo(2)
        verify(consumptionDataService).upsert(consumption.copy(count = 2))
        verify(correctionDataService).upsert(
            CoffeeCorrection(
                user = target,
                countBefore = 3,
                countAfter = 2,
                note = "Double tap on 16.11.",
                admin = admin
            )
        )
    }
}
