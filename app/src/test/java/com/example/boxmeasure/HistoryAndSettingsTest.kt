package com.example.boxmeasure

import android.content.SharedPreferences
import com.example.boxmeasure.data.model.BoxMeasureHistory
import com.example.boxmeasure.data.repository.AppSettings
import com.example.boxmeasure.data.repository.AppTheme
import com.example.boxmeasure.data.repository.HistoryRepository
import com.example.boxmeasure.data.repository.SettingsRepository
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.RoundingMode
import java.util.UUID

class HistoryAndSettingsTest {

    private lateinit var fakeSettingsPrefs: FakeSharedPreferences
    private lateinit var fakeHistoryPrefs: FakeSharedPreferences
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        fakeSettingsPrefs = FakeSharedPreferences()
        fakeHistoryPrefs = FakeSharedPreferences()
        settingsRepository = SettingsRepository(fakeSettingsPrefs)
        historyRepository = HistoryRepository(fakeHistoryPrefs)
    }

    private fun createSampleHistory(
        l: String = "10",
        w: String = "5",
        h: String = "4",
        mode: SolveMode = SolveMode.BOX_MEASUREMENTS,
        missing: DimensionType? = null
    ): BoxMeasureHistory {
        return BoxMeasureHistory(
            id = UUID.randomUUID().toString(),
            solveMode = mode,
            missingDimension = missing,
            length = l,
            width = w,
            height = h,
            volume = if (mode == SolveMode.FIND_MISSING_DIMENSION) "200" else null,
            lengthUnit = LengthUnit.CENTIMETER,
            widthUnit = LengthUnit.CENTIMETER,
            heightUnit = LengthUnit.CENTIMETER,
            resultLengthUnit = LengthUnit.CENTIMETER,
            resultAreaUnit = AreaUnit.SQUARE_CENTIMETER,
            resultVolumeUnit = VolumeUnit.CUBIC_CENTIMETER,
            savedAt = System.currentTimeMillis()
        )
    }

    // 43. History save
    @Test
    fun testHistorySave() {
        val item = createSampleHistory()
        val saved = historyRepository.save(item)
        assertTrue(saved)

        val historyList = historyRepository.getHistory()
        assertEquals(1, historyList.size)
        assertEquals("10", historyList[0].length)
        assertEquals("5", historyList[0].width)
        assertEquals("4", historyList[0].height)
    }

    // 44. History restore
    @Test
    fun testHistoryRestore() {
        val item = BoxMeasureHistory(
            id = "test-id-1",
            solveMode = SolveMode.FIND_MISSING_DIMENSION,
            missingDimension = DimensionType.HEIGHT,
            length = "10",
            width = "5",
            height = "4",
            volume = "200",
            lengthUnit = LengthUnit.METER,
            widthUnit = LengthUnit.CENTIMETER,
            heightUnit = LengthUnit.MILLIMETER,
            resultLengthUnit = LengthUnit.INCH,
            resultAreaUnit = AreaUnit.SQUARE_METER,
            resultVolumeUnit = VolumeUnit.CUBIC_METER,
            savedAt = 123456789L
        )
        historyRepository.save(item)

        val retrieved = historyRepository.getHistory().first()
        assertEquals(SolveMode.FIND_MISSING_DIMENSION, retrieved.solveMode)
        assertEquals(DimensionType.HEIGHT, retrieved.missingDimension)
        assertEquals("10", retrieved.length)
        assertEquals("5", retrieved.width)
        assertEquals("4", retrieved.height)
        assertEquals("200", retrieved.volume)
        assertEquals(LengthUnit.METER, retrieved.lengthUnit)
        assertEquals(LengthUnit.CENTIMETER, retrieved.widthUnit)
        assertEquals(LengthUnit.MILLIMETER, retrieved.heightUnit)
        assertEquals(LengthUnit.INCH, retrieved.resultLengthUnit)
        assertEquals(AreaUnit.SQUARE_METER, retrieved.resultAreaUnit)
        assertEquals(VolumeUnit.CUBIC_METER, retrieved.resultVolumeUnit)
    }

    // 45. History limit (Save 10 -> 10. Save 11th -> oldest removed)
    @Test
    fun testHistoryLimit() {
        for (i in 1..10) {
            val item = createSampleHistory(l = "$i", w = "5", h = "4")
            historyRepository.save(item)
        }
        assertEquals(10, historyRepository.getHistory().size)

        // Save 11th
        val item11 = createSampleHistory(l = "11", w = "5", h = "4")
        historyRepository.save(item11)

        val list = historyRepository.getHistory()
        assertEquals(10, list.size)
        assertEquals("11", list.first().length)
        // Oldest (item with l="1") should be dropped
        assertFalse(list.any { it.length == "1" })
    }

    // 46. History delete (Single delete & Clear all)
    @Test
    fun testHistoryDelete() {
        val item1 = createSampleHistory(l = "10")
        val item2 = createSampleHistory(l = "20")
        historyRepository.save(item1)
        historyRepository.save(item2)

        assertEquals(2, historyRepository.getHistory().size)

        // Delete item1
        historyRepository.delete(item1.id)
        val listAfterSingleDelete = historyRepository.getHistory()
        assertEquals(1, listAfterSingleDelete.size)
        assertEquals("20", listAfterSingleDelete.first().length)

        // Clear all
        historyRepository.clear()
        assertTrue(historyRepository.getHistory().isEmpty())
    }

    // 47. Duplicate history (Reject exact consecutive calculation save)
    @Test
    fun testDuplicateHistory() {
        val item1 = createSampleHistory(l = "10", w = "5", h = "4")
        val saved1 = historyRepository.save(item1)
        assertTrue(saved1)

        // Exact same calculation consecutive save
        val duplicate = item1.copy(id = UUID.randomUUID().toString(), savedAt = System.currentTimeMillis() + 1000)
        val saved2 = historyRepository.save(duplicate)
        assertFalse("Expected duplicate save to return false", saved2)

        assertEquals(1, historyRepository.getHistory().size)
    }

    // 48. Settings persistence
    @Test
    fun testSettingsPersistence() {
        val defaultSettings = settingsRepository.loadSettings()
        assertEquals(AppTheme.SYSTEM, defaultSettings.theme)
        assertEquals(6, defaultSettings.decimalPlaces)
        assertEquals(RoundingMode.HALF_UP, defaultSettings.roundingMode)

        // Modify settings (Dark mode, 4 decimals, HALF_EVEN rounding)
        val updated = defaultSettings.copy(
            theme = AppTheme.DARK,
            decimalPlaces = 4,
            roundingMode = RoundingMode.HALF_EVEN,
            dynamicColor = false,
            hapticFeedback = false
        )
        settingsRepository.saveSettings(updated)

        // Simulate app restart by reloading from repository
        val reloaded = settingsRepository.loadSettings()
        assertEquals(AppTheme.DARK, reloaded.theme)
        assertEquals(4, reloaded.decimalPlaces)
        assertEquals(RoundingMode.HALF_EVEN, reloaded.roundingMode)
        assertFalse(reloaded.dynamicColor)
        assertFalse(reloaded.hapticFeedback)
    }

    // 49. Reset (Reset settings to defaults, ensure history remains intact)
    @Test
    fun testResetSettingsAndDefaults() {
        val item = createSampleHistory()
        historyRepository.save(item)

        // Change settings
        settingsRepository.saveSettings(
            settingsRepository.loadSettings().copy(
                decimalPlaces = 2,
                theme = AppTheme.LIGHT
            )
        )
        assertEquals(2, settingsRepository.loadSettings().decimalPlaces)

        // Reset settings
        settingsRepository.resetSettings()
        val afterReset = settingsRepository.loadSettings()
        assertEquals(6, afterReset.decimalPlaces)
        assertEquals(AppTheme.SYSTEM, afterReset.theme)

        // Verify history is untouched
        assertEquals(1, historyRepository.getHistory().size)
    }
}

/**
 * In-memory FakeSharedPreferences for lightning-fast, zero-dependency unit testing.
 */
class FakeSharedPreferences : SharedPreferences {
    private val data = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = data.toMutableMap()
    override fun getString(key: String?, defValue: String?): String? = data[key] as? String ?: defValue
    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = (data[key] as? Set<*>)?.filterIsInstance<String>()?.toMutableSet() ?: defValues
    override fun getInt(key: String?, defValue: Int): Int = data[key] as? Int ?: defValue
    override fun getLong(key: String?, defValue: Long): Long = data[key] as? Long ?: defValue
    override fun getFloat(key: String?, defValue: Float): Float = data[key] as? Float ?: defValue
    override fun getBoolean(key: String?, defValue: Boolean): Boolean = data[key] as? Boolean ?: defValue
    override fun contains(key: String?): Boolean = data.containsKey(key)
    override fun edit(): SharedPreferences.Editor = FakeEditor(data)
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    class FakeEditor(private val target: MutableMap<String, Any?>) : SharedPreferences.Editor {
        private val temp = mutableMapOf<String, Any?>()
        private var clearFlag = false

        override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply { temp[key ?: ""] = value }
        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = apply { temp[key ?: ""] = values }
        override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply { temp[key ?: ""] = value }
        override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply { temp[key ?: ""] = value }
        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply { temp[key ?: ""] = value }
        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply { temp[key ?: ""] = value }
        override fun remove(key: String?): SharedPreferences.Editor = apply { temp[key ?: ""] = null }
        override fun clear(): SharedPreferences.Editor = apply { clearFlag = true }
        override fun commit(): Boolean { apply(); return true }
        override fun apply() {
            if (clearFlag) target.clear()
            for ((k, v) in temp) {
                if (v == null) target.remove(k) else target[k] = v
            }
        }
    }
}
