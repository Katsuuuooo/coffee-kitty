package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.CoffeeCorrectionEntity
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/** Maps correction audit records to and from persistence entities. */
@Mapper(componentModel = "spring", uses = [UserEntityMapper::class, EntityReferenceResolver::class])
interface CoffeeCorrectionEntityMapper : EntityMapper<CoffeeCorrection, CoffeeCorrectionEntity> {
    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "admin", source = "admin.id")
    override fun toEntity(source: CoffeeCorrection): CoffeeCorrectionEntity

    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "admin", source = "admin.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    override fun updateEntity(
        source: CoffeeCorrection,
        @MappingTarget target: CoffeeCorrectionEntity
    )
}
