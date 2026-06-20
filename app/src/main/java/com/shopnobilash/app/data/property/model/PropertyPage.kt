package com.shopnobilash.app.data.property.model

/**
 * One page of search results plus the server-reported [total] match count.
 * [total] drives the honest "showing X of N" contract — it is the count of all rows
 * matching the filter on the server, not just the rows returned in [items].
 */
data class PropertyPage(
    val items: List<Property>,
    val total: Long,
)

/** Typed search failure so the UI can distinguish offline from misconfig from unknown. */
sealed interface SearchError {
    /** No network / server unreachable / timeout. */
    data object Network : SearchError

    /** A required Appwrite index (e.g. fulltext on `house_name`) is missing — a config bug. */
    data object MissingIndex : SearchError

    /** Anything else; keep the cause for logging. */
    data class Unknown(val cause: Throwable) : SearchError

    /** Default user-facing copy. */
    val message: String
        get() = when (this) {
            Network -> "You're offline. Check your connection and try again."
            MissingIndex -> "Search is temporarily unavailable."
            is Unknown -> "Couldn't load properties. Please try again."
        }
}

/** Carries a [SearchError] through a [Result] failure. */
class SearchException(val error: SearchError) : Exception(error.message)
