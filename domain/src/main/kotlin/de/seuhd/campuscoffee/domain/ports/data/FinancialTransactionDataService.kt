package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import java.util.UUID

/** Data port for financial transaction history. */
interface FinancialTransactionDataService : CrudDataService<FinancialTransaction, UUID>
