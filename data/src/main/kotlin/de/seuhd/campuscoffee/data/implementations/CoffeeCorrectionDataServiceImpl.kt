package de.seuhd.campuscoffee.data.implementations

import de.seuhd.campuscoffee.data.mapper.CoffeeCorrectionEntityMapper
import de.seuhd.campuscoffee.data.persistence.entities.CoffeeCorrectionEntity
import de.seuhd.campuscoffee.data.persistence.repositories.CoffeeCorrectionRepository
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import de.seuhd.campuscoffee.domain.ports.data.CoffeeCorrectionDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/** Data adapter for correction records in `coffee_corrections`. */
@Service
class CoffeeCorrectionDataServiceImpl(
    repository: CoffeeCorrectionRepository,
    entityMapper: CoffeeCorrectionEntityMapper
) : CrudDataServiceImpl<CoffeeCorrection, CoffeeCorrectionEntity, CoffeeCorrectionRepository, UUID>(
        repository,
        entityMapper,
        CoffeeCorrection::class.java
    ),
    CoffeeCorrectionDataService {
    /** Reads and maps the correction records while the eager user references are available. */
    @Transactional(readOnly = true)
    override fun getByUserId(userId: UUID): List<CoffeeCorrection> =
        repository.findByUserIdOrderByCreatedAtDescIdDesc(userId).map { mapper.fromEntity(it) }
}
