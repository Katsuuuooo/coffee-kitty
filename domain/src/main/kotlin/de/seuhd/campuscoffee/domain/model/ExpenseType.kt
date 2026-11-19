package de.seuhd.campuscoffee.domain.model

/** The kind of purchase recorded as an expense. */
enum class ExpenseType {
    /** Coffee beans with a name and weight. */
    BEANS,

    /** Any other purchase for the Coffee Kitty. */
    OTHER
}
