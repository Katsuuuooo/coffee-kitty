package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.FinancialTransactionEntity
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/** Maps financial transaction history between the domain and JPA entity. */
@Mapper(componentModel = "spring")
interface FinancialTransactionEntityMapper : EntityMapper<FinancialTransaction, FinancialTransactionEntity> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    override fun updateEntity(
        source: FinancialTransaction,
        @MappingTarget target: FinancialTransactionEntity
    )
}
