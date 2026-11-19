package de.seuhd.campuscoffee.data.implementations

import de.seuhd.campuscoffee.data.mapper.FinancialTransactionEntityMapper
import de.seuhd.campuscoffee.data.persistence.entities.FinancialTransactionEntity
import de.seuhd.campuscoffee.data.persistence.repositories.FinancialTransactionRepository
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import de.seuhd.campuscoffee.domain.ports.data.FinancialTransactionDataService
import org.springframework.stereotype.Service
import java.util.UUID

/** Data adapter for the financial transaction history. */
@Service
class FinancialTransactionDataServiceImpl(
    repository: FinancialTransactionRepository,
    entityMapper: FinancialTransactionEntityMapper
) : CrudDataServiceImpl<FinancialTransaction, FinancialTransactionEntity, FinancialTransactionRepository, UUID>(
        repository,
        entityMapper,
        FinancialTransaction::class.java
    ),
    FinancialTransactionDataService
