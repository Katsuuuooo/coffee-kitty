package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.model.CashAccount
import de.seuhd.campuscoffee.domain.ports.data.CashAccountDataService
import de.seuhd.campuscoffee.domain.ports.data.CoffeeConsumptionDataService
import de.seuhd.campuscoffee.domain.ports.data.CoffeeCorrectionDataService
import de.seuhd.campuscoffee.domain.ports.data.FinancialTransactionDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import de.seuhd.campuscoffee.domain.ports.internal.DataResetService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Domain implementation of [DataResetService]. It deletes transaction history, resets the cash balance, then deletes
 * consumptions and users in foreign-key order. Delete the rows of a referencing entity before the rows that it
 * references.
 */
@Service
class DataResetServiceImpl(
    private val coffeeConsumptionDataService: CoffeeConsumptionDataService,
    private val coffeeCorrectionDataService: CoffeeCorrectionDataService,
    private val userDataService: UserDataService,
    private val cashAccountDataService: CashAccountDataService,
    private val financialTransactionDataService: FinancialTransactionDataService
) : DataResetService {
    override fun clearAll() {
        log.warn { "Clearing all data." }
        financialTransactionDataService.clear()
        cashAccountDataService.upsert(CashAccount(CashAccount.ID, BigDecimal("0.00")))
        coffeeCorrectionDataService.clear()
        coffeeConsumptionDataService.clear()
        userDataService.clear()
    }

    private companion object {
        private val log = KotlinLogging.logger {}
    }
}
