package de.seuhd.campuscoffee.api.mapper

import de.seuhd.campuscoffee.api.dtos.FinancialTransactionDto
import de.seuhd.campuscoffee.domain.model.FinancialTransaction
import org.mapstruct.Mapper

/** Maps financial transaction domain objects to API responses. */
@Mapper(componentModel = "spring")
interface FinancialTransactionDtoMapper {
    /** Converts [source] to its API response.
     *
     * @param source the transaction to map
     */
    fun fromDomain(source: FinancialTransaction): FinancialTransactionDto
}
