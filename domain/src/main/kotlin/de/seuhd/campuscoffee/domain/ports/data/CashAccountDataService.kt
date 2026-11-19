package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.CashAccount
import java.util.UUID

/** Data port for the single Coffee Kitty cash account. */
interface CashAccountDataService : CrudDataService<CashAccount, UUID> {
    /** Loads and locks the account row until the current transaction ends. */
    fun getForUpdate(): CashAccount
}
