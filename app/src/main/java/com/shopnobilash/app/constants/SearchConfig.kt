package com.shopnobilash.app.constants

/**
 * Tunable search/filter business rules. Kept out of the repository so a product change
 * (e.g. "newly added = 14 days") is a one-line config edit, not buried in query code.
 * Promote to a remote config row when runtime tuning without a release is needed.
 */
object SearchConfig {
    /** "Newly added" = listed within this many days. */
    const val NEWLY_ADDED_WINDOW_DAYS = 30L

    /** Max rows fetched per search. Replace with cursor pagination when catalog outgrows it. */
    const val SEARCH_PAGE_SIZE = 100
}
