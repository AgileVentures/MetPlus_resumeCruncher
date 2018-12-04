package org.metplus.cruncher.rating

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.metplus.cruncher.settings.*

internal class TrainCruncherTest {
    lateinit var settingsRepository: SettingsRepositoryFake
    lateinit var cruncher: CruncherStub
    @BeforeEach
    fun setup() {
        settingsRepository = SettingsRepositoryFake()
        cruncher = CruncherStub()
    }

    @Test
    fun `when train database is present and successfully train the cruncher, it calls the success observer`() {
        var successWasCalled = false
        val trainer = TrainCruncher(settingsRepository, cruncher, emptySettingsConstructor(1).cruncherSettings)
        val settings = Settings(1, ApplicationSettings(hashMapOf()), CruncherSettings(hashMapOf("feature" to listOf("some value")), listOf("a")))
        settingsRepository.save(settings)

        trainer.process(observer = object : TrainCruncherObserver {
            override fun onSuccess() {
                successWasCalled = true
            }
        })
        assertThat(successWasCalled).isTrue()
        assertThat(cruncher.trainWasCalledWith).isEqualTo(
                mapOf("feature" to listOf("some value"))
        )
    }

    @Test
    fun `when train database is NOT present and successfully train the cruncher, it loads default configuration and calls the success observer`() {
        var successWasCalled = false
        val cruncherSettings = CruncherSettings(mapOf("some feature" to listOf("Some other value")), listOf("clean", "this"))
        val trainer = TrainCruncher(settingsRepository, cruncher, cruncherSettings)

        trainer.process(observer = object : TrainCruncherObserver {
            override fun onSuccess() {
                successWasCalled = true
            }
        })

        assertThat(successWasCalled).isTrue()
        assertThat(cruncher.trainWasCalledWith).isEqualTo(
                mapOf("some feature" to listOf("Some other value"))
        )
        val savedSettings = settingsRepository.getAll().first().cruncherSettings
        assertThat(savedSettings).isEqualToComparingFieldByField(CruncherSettings(
                mapOf("some feature" to listOf("Some other value")), listOf("clean", "this")
        ))
    }
}