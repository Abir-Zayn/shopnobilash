package com.shopnobilash.app.constants

import com.shopnobilash.app.R

/**
 * App-wide UI constants.
 */
object AppConstants {

    /**
     * "Cards Star" Material Symbols Outlined icon
     * (https://fonts.google.com/icons?icon.set=Material+Symbols&icon.query=cards_star).
     *
     * Held as a vector drawable resource ([R.drawable.ic_cards_star]) because this
     * glyph is not present in the material-icons-extended version bundled with the
     * project. The drawable uses the official Google path data. Use it via
     * `painterResource(AppConstants.CardsStarIcon)`.
     */
    val CardsStarIcon: Int = R.drawable.ic_cards_star
}
