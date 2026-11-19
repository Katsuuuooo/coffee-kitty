package de.seuhd.campuscoffee.data.persistence.repositories

import de.seuhd.campuscoffee.data.persistence.entities.CashAccountEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID

/** Repository for the singleton Coffee Kitty cash account. */
interface CashAccountRepository : JpaRepository<CashAccountEntity, UUID> {
    /** Loads and locks the cash account row.
     *
     * @param id the id of the singleton cash account
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from CashAccountEntity account where account.id = :id")
    fun findForUpdateById(id: UUID): CashAccountEntity?
}
