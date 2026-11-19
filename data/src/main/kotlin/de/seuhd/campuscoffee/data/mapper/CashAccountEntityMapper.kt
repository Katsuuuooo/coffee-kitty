package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.CashAccountEntity
import de.seuhd.campuscoffee.domain.model.CashAccount
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/** Maps the Coffee Kitty cash account between the domain and JPA entity. */
@Mapper(componentModel = "spring")
interface CashAccountEntityMapper : EntityMapper<CashAccount, CashAccountEntity> {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    override fun toEntity(source: CashAccount): CashAccountEntity

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    override fun updateEntity(
        source: CashAccount,
        @MappingTarget target: CashAccountEntity
    )
}
