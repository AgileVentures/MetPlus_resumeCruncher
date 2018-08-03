package org.metplus.cruncher.settings

interface SettingsRepository {
    fun getAll(): List<Settings>
}