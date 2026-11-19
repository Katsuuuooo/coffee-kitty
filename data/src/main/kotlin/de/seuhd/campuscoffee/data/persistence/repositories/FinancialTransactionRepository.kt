package de.seuhd.campuscoffee.data.persistence.repositories

import de.seuhd.campuscoffee.data.persistence.entities.FinancialTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/** Repository for financial transaction history. */
interface FinancialTransactionRepository : JpaRepository<FinancialTransactionEntity, UUID>
