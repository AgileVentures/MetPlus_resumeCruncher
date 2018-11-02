package org.metplus.cruncher.settings

class SettingsRepositoryFakeTest : SettingsRepositoryTest() {
    private val repo: SettingsRepository = SettingsRepositoryFake()
    override fun getRepository(): SettingsRepository {
        return repo
    }
}