package de.seuhd.campuscoffee.api.mapper

import de.seuhd.campuscoffee.api.dtos.CoffeeCorrectionDto
import de.seuhd.campuscoffee.domain.model.CoffeeCorrection
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/** Maps correction audit records to their API representation. */
@Mapper(componentModel = "spring")
interface CoffeeCorrectionDtoMapper {
    /**
     * Converts [correction] to an API response.
     *
     * @param correction the correction to convert
     */
    @Mapping(target = "adminLoginName", source = "admin.loginName")
    fun toDto(correction: CoffeeCorrection): CoffeeCorrectionDto
}
