package org.metplus.cruncher.rating

interface Matcher<From, To> {
    fun match(from: From, allList: List<To>): List<To>
}