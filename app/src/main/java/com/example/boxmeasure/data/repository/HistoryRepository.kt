package com.example.boxmeasure.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.boxmeasure.data.model.BoxMeasureHistory
import com.example.boxmeasure.domain.model.AreaUnit
import com.example.boxmeasure.domain.model.DimensionType
import com.example.boxmeasure.domain.model.LengthUnit
import com.example.boxmeasure.domain.model.SolveMode
import com.example.boxmeasure.domain.model.VolumeUnit

class HistoryRepository(
    private val prefs: SharedPreferences
) {
    constructor(context: Context) : this(
        context.getSharedPreferences("boxmeasure_history", Context.MODE_PRIVATE)
    )

    companion object {
        const val MAX_HISTORY = 10
        private const val KEY_HISTORY_JSON = "key_history_json"
    }

    fun getHistory(): List<BoxMeasureHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY_JSON, null) ?: return emptyList()
        return deserializeList(jsonStr)
    }

    /**
     * Saves an item to history.
     * Prevents consecutive duplicates.
     * Keeps at most 10 items (removes oldest when exceeding).
     * Returns true if saved, false if rejected as duplicate.
     */
    fun save(item: BoxMeasureHistory): Boolean {
        val currentList = getHistory().toMutableList()

        // Check consecutive duplicate:
        if (currentList.isNotEmpty() && currentList.first().isDuplicateOf(item)) {
            return false
        }

        // Add to front (most recent)
        currentList.add(0, item)

        // Trim to max 10
        while (currentList.size > MAX_HISTORY) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveList(currentList)
        return true
    }

    fun delete(id: String) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveList(currentList)
    }

    fun clear() {
        prefs.edit().remove(KEY_HISTORY_JSON).apply()
    }

    private fun saveList(list: List<BoxMeasureHistory>) {
        val sb = StringBuilder()
        sb.append("[")
        list.forEachIndexed { index, item ->
            sb.append(serializeItem(item))
            if (index < list.size - 1) sb.append(",")
        }
        sb.append("]")
        prefs.edit().putString(KEY_HISTORY_JSON, sb.toString()).apply()
    }

    private fun serializeItem(item: BoxMeasureHistory): String {
        return buildString {
            append("{")
            append("\"id\":\"").append(item.id).append("\",")
            append("\"solveMode\":\"").append(item.solveMode.name).append("\",")
            append("\"missingDimension\":\"").append(item.missingDimension?.name ?: "").append("\",")
            append("\"length\":\"").append(item.length).append("\",")
            append("\"width\":\"").append(item.width).append("\",")
            append("\"height\":\"").append(item.height).append("\",")
            append("\"volume\":\"").append(item.volume ?: "").append("\",")
            append("\"lengthUnit\":\"").append(item.lengthUnit.name).append("\",")
            append("\"widthUnit\":\"").append(item.widthUnit.name).append("\",")
            append("\"heightUnit\":\"").append(item.heightUnit.name).append("\",")
            append("\"resultLengthUnit\":\"").append(item.resultLengthUnit.name).append("\",")
            append("\"resultAreaUnit\":\"").append(item.resultAreaUnit.name).append("\",")
            append("\"resultVolumeUnit\":\"").append(item.resultVolumeUnit.name).append("\",")
            append("\"savedAt\":").append(item.savedAt)
            append("}")
        }
    }

    private fun deserializeList(jsonStr: String): List<BoxMeasureHistory> {
        val trimmed = jsonStr.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val content = trimmed.substring(1, trimmed.length - 1).trim()
        if (content.isEmpty()) return emptyList()

        val list = mutableListOf<BoxMeasureHistory>()
        val itemRegex = "\\{([^{}]+)\\}".toRegex()
        val matches = itemRegex.findAll(content)

        for (match in matches) {
            val block = match.value
            val item = parseItem(block)
            if (item != null) {
                list.add(item)
            }
        }
        return list
    }

    private fun parseItem(json: String): BoxMeasureHistory? {
        fun extractString(key: String): String {
            val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
            return pattern.find(json)?.groupValues?.get(1) ?: ""
        }

        fun extractLong(key: String): Long {
            val pattern = "\"$key\"\\s*:\\s*(\\d+)".toRegex()
            return pattern.find(json)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        }

        return try {
            val id = extractString("id")
            val modeStr = extractString("solveMode")
            val missingStr = extractString("missingDimension")
            val length = extractString("length")
            val width = extractString("width")
            val height = extractString("height")
            val volumeStr = extractString("volume").takeIf { it.isNotEmpty() }
            val lengthUnit = LengthUnit.valueOf(extractString("lengthUnit"))
            val widthUnit = LengthUnit.valueOf(extractString("widthUnit"))
            val heightUnit = LengthUnit.valueOf(extractString("heightUnit"))
            val resLengthUnit = LengthUnit.valueOf(extractString("resultLengthUnit"))
            val resAreaUnit = AreaUnit.valueOf(extractString("resultAreaUnit"))
            val resVolumeUnit = VolumeUnit.valueOf(extractString("resultVolumeUnit"))
            val savedAt = extractLong("savedAt")

            val missingDim = if (missingStr.isNotEmpty()) {
                runCatching { DimensionType.valueOf(missingStr) }.getOrNull()
            } else null

            BoxMeasureHistory(
                id = id,
                solveMode = SolveMode.valueOf(modeStr),
                missingDimension = missingDim,
                length = length,
                width = width,
                height = height,
                volume = volumeStr,
                lengthUnit = lengthUnit,
                widthUnit = widthUnit,
                heightUnit = heightUnit,
                resultLengthUnit = resLengthUnit,
                resultAreaUnit = resAreaUnit,
                resultVolumeUnit = resVolumeUnit,
                savedAt = savedAt
            )
        } catch (e: Exception) {
            null
        }
    }
}
