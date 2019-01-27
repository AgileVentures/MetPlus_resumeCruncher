package org.metplus.cruncher.rating

interface Matcher<From, To> {
    fun getName(): String
    fun match(from: From, allList: List<To>): List<To>
    fun matchInverse(from: To, allList: List<From>): List<From>
    fun similarityRating(left: From, right: To): Double
}