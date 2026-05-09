package com.harnessgames.aicore.storage

import android.content.Context
import com.harnessgames.aicore.game.AIAlignment
import com.harnessgames.aicore.game.GameState
import com.harnessgames.aicore.game.ResourceWallet
import org.json.JSONArray
import org.json.JSONObject

class SaveRepository(context: Context) {
    private val prefs = context.getSharedPreferences("ai_core_save", Context.MODE_PRIVATE)

    fun load(): GameState? {
        val raw = prefs.getString(KEY_STATE, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            val planetIndex = json.optInt("planetIndex", 0)
            val stageProgress = json.optDouble("stageProgress", 0.0)
            val completedPlanetCount = json.optInt("completedPlanetCount", 0)
            val lastSavedEpochMs = json.optLong("lastSavedEpochMs", 0L)
            GameState(
                planetIndex = planetIndex,
                stageProgress = stageProgress,
                planetProgress = json.optJSONObject("planetProgress")?.toIntDoubleMap()
                    ?: legacyPlanetProgress(planetIndex, stageProgress, completedPlanetCount),
                planetArrivalEpochMs = json.optJSONObject("planetArrivalEpochMs")?.toIntLongMap()
                    ?: legacyPlanetArrivals(planetIndex, completedPlanetCount, lastSavedEpochMs),
                resources = json.optJSONObject("resources")?.toWallet() ?: ResourceWallet(energy = 20.0),
                upgrades = json.optJSONObject("upgrades")?.toStringIntMap() ?: emptyMap(),
                aiAlignment = json.optString("aiAlignment", AIAlignment.LIFE.name).toAlignment(),
                alignmentScore = json.optJSONObject("alignmentScore")?.toAlignmentScore()
                    ?: AIAlignment.entries.associateWith { 0 },
                selectedEvolutionPlanets = json.optJSONArray("selectedEvolutionPlanets")?.toIntSet() ?: emptySet(),
                completedPlanetCount = completedPlanetCount,
                prestigeCores = json.optInt("prestigeCores", 0),
                activeEventIndex = if (json.has("activeEventIndex")) json.optInt("activeEventIndex") else null,
                eventRewardBoosts = json.optInt("eventRewardBoosts", 0),
                adBoostUntilEpochMs = json.optLong("adBoostUntilEpochMs", 0L),
                lastSavedEpochMs = lastSavedEpochMs,
            )
        }.getOrNull()
    }

    fun save(state: GameState) {
        val json = JSONObject()
            .put("planetIndex", state.planetIndex)
            .put("stageProgress", state.stageProgress)
            .put("planetProgress", state.planetProgress.toProgressJson())
            .put("planetArrivalEpochMs", state.planetArrivalEpochMs.toArrivalJson())
            .put("resources", state.resources.toJson())
            .put("upgrades", state.upgrades.toUpgradeJson())
            .put("aiAlignment", state.aiAlignment.name)
            .put("alignmentScore", state.alignmentScore.toAlignmentJson())
            .put("selectedEvolutionPlanets", JSONArray(state.selectedEvolutionPlanets.toList()))
            .put("completedPlanetCount", state.completedPlanetCount)
            .put("prestigeCores", state.prestigeCores)
            .put("eventRewardBoosts", state.eventRewardBoosts)
            .put("adBoostUntilEpochMs", state.adBoostUntilEpochMs)
            .put("lastSavedEpochMs", state.lastSavedEpochMs)

        state.activeEventIndex?.let { json.put("activeEventIndex", it) }

        prefs.edit()
            .putString(KEY_STATE, json.toString())
            .putString(KEY_BACKUP_STATE, prefs.getString(KEY_STATE, null))
            .apply()
    }

    fun loadBackup(): GameState? {
        val backup = prefs.getString(KEY_BACKUP_STATE, null) ?: return null
        prefs.edit().putString(KEY_STATE, backup).apply()
        return load()
    }

    private fun JSONObject.toWallet(): ResourceWallet = ResourceWallet(
        energy = optDouble("energy", 0.0),
        biomass = optDouble("biomass", 0.0),
        aiData = optDouble("aiData", 0.0),
    )

    private fun ResourceWallet.toJson(): JSONObject = JSONObject()
        .put("energy", energy)
        .put("biomass", biomass)
        .put("aiData", aiData)

    private fun JSONObject.toStringIntMap(): Map<String, Int> {
        val output = mutableMapOf<String, Int>()
        keys().forEach { key -> output[key] = optInt(key, 0) }
        return output
    }

    private fun JSONObject.toIntDoubleMap(): Map<Int, Double> {
        val output = mutableMapOf<Int, Double>()
        keys().forEach { key ->
            key.toIntOrNull()?.let { index -> output[index] = optDouble(key, 0.0) }
        }
        return output
    }

    private fun JSONObject.toIntLongMap(): Map<Int, Long> {
        val output = mutableMapOf<Int, Long>()
        keys().forEach { key ->
            key.toIntOrNull()?.let { index -> output[index] = optLong(key, 0L) }
        }
        return output
    }

    private fun Map<Int, Double>.toProgressJson(): JSONObject {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key.toString(), value) }
        return json
    }

    private fun Map<Int, Long>.toArrivalJson(): JSONObject {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key.toString(), value) }
        return json
    }

    private fun legacyPlanetProgress(planetIndex: Int, stageProgress: Double, completedPlanetCount: Int): Map<Int, Double> {
        val output = mutableMapOf<Int, Double>()
        val maxCompleted = completedPlanetCount.coerceAtLeast(0).coerceAtMost(com.harnessgames.aicore.game.GameBalance.planets.lastIndex)
        for (index in 0 until maxCompleted) {
            output[index] = com.harnessgames.aicore.game.GameBalance.planets[index].stageThresholds.last()
        }
        output[planetIndex] = stageProgress
        return output
    }

    private fun legacyPlanetArrivals(planetIndex: Int, completedPlanetCount: Int, lastSavedEpochMs: Long): Map<Int, Long> {
        val output = mutableMapOf<Int, Long>()
        val now = System.currentTimeMillis()
        val currentArrival = if (lastSavedEpochMs > 0L) lastSavedEpochMs else now
        val maxUnlocked = completedPlanetCount.coerceAtLeast(planetIndex).coerceAtMost(com.harnessgames.aicore.game.GameBalance.planets.lastIndex)
        for (index in 0..maxUnlocked) {
            output[index] = if (index == planetIndex) {
                currentArrival
            } else {
                now - com.harnessgames.aicore.game.GameBalance.minTravelSeconds * 1000L
            }
        }
        return output
    }

    private fun Map<String, Int>.toUpgradeJson(): JSONObject {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key, value) }
        return json
    }

    private fun JSONObject.toAlignmentScore(): Map<AIAlignment, Int> {
        return AIAlignment.entries.associateWith { alignment -> optInt(alignment.name, 0) }
    }

    private fun Map<AIAlignment, Int>.toAlignmentJson(): JSONObject {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key.name, value) }
        return json
    }

    private fun JSONArray.toIntSet(): Set<Int> {
        val output = mutableSetOf<Int>()
        for (index in 0 until length()) {
            output += optInt(index)
        }
        return output
    }

    private fun String.toAlignment(): AIAlignment =
        AIAlignment.entries.firstOrNull { it.name == this } ?: AIAlignment.LIFE

    companion object {
        private const val KEY_STATE = "state_json"
        private const val KEY_BACKUP_STATE = "state_json_backup"
    }
}
