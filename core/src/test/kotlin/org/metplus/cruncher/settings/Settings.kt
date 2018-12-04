package org.metplus.cruncher.settings

fun emptySettingsConstructor(id: Int) = Settings(id, ApplicationSettings(hashMapOf()), CruncherSettings(hashMapOf(), listOf()))