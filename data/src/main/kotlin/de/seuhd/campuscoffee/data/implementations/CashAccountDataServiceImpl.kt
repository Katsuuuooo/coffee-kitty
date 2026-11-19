package de.seuhd.campuscoffee.data.implementations

import de.seuhd.campuscoffee.data.mapper.CashAccountEntityMapper
import de.seuhd.campuscoffee.data.persistence.entities.CashAccountEntity
import de.seuhd.campuscoffee.data.persistence.repositories.CashAccountRepository
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException
import de.seuhd.campuscoffee.domain.model.CashAccount
import de.seuhd.campuscoffee.domain.ports.data.CashAccountDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/** Data adapter for the singleton Coffee Kitty cash account. */
@Service
class CashAccountDataServiceImpl(
    repository: CashAccountRepository,
    entityMapper: CashAccountEntityMapper
) : CrudDataServiceImpl<CashAccount, CashAccountEntity, CashAccountRepository, UUID>(
        repository,
        entityMapper,
        CashAccount::class.java
    ),
    CashAccountDataService {
    @Transactional(propagation = Propagation.MANDATORY)
    override fun getForUpdate(): CashAccount =
        repository.findForUpdateById(CashAccount.ID)?.let { mapper.fromEntity(it) }
            ?: throw NotFoundException(CashAccount::class.java, CashAccount.ID)
}
