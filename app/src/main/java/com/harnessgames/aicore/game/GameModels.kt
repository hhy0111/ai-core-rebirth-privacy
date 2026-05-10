package com.harnessgames.aicore.game

enum class ResourceType {
    ENERGY,
    BIOMASS,
    AI_DATA,
}

enum class PlanetStage(val label: String) {
    COLLAPSED("붕괴"),
    ACTIVATED("활성화"),
    GROWTH("성장"),
    COMPLETE("완성"),
}

enum class AIAlignment(val label: String) {
    LIFE("생명형"),
    INDUSTRIAL("산업형"),
    COMBAT("전투형"),
}

enum class UpgradeEffect {
    ENERGY_PRODUCTION,
    BIOMASS_PRODUCTION,
    AI_DATA_PRODUCTION,
    TAP_POWER,
    RESTORE_SPEED,
    OFFLINE_GAIN,
}

enum class Panel(val label: String) {
    UPGRADE("업그레이드"),
    TRAVEL("이동"),
    AI("AI"),
    EVENT("이벤트"),
    SHOP("상점"),
}

data class ResourceWallet(
    val energy: Double = 0.0,
    val biomass: Double = 0.0,
    val aiData: Double = 0.0,
) {
    operator fun plus(other: ResourceWallet) = ResourceWallet(
        energy = energy + other.energy,
        biomass = biomass + other.biomass,
        aiData = aiData + other.aiData,
    )

    operator fun minus(other: ResourceWallet) = ResourceWallet(
        energy = energy - other.energy,
        biomass = biomass - other.biomass,
        aiData = aiData - other.aiData,
    )

    operator fun times(multiplier: Double) = ResourceWallet(
        energy = energy * multiplier,
        biomass = biomass * multiplier,
        aiData = aiData * multiplier,
    )

    fun canAfford(cost: ResourceWallet): Boolean {
        return energy >= cost.energy && biomass >= cost.biomass && aiData >= cost.aiData
    }
}

data class PlanetDef(
    val id: String,
    val name: String,
    val theme: String,
    val baseProduction: ResourceWallet,
    val travelCost: ResourceWallet,
    val stageThresholds: List<Double>,
)

data class UpgradeDef(
    val id: String,
    val name: String,
    val description: String,
    val effect: UpgradeEffect,
    val baseCost: ResourceWallet,
    val costScale: Double,
    val valuePerLevel: Double,
    val maxLevel: Int,
)

data class EvolutionDef(
    val alignment: AIAlignment,
    val title: String,
    val description: String,
    val rewardText: String,
)

data class EventOption(
    val label: String,
    val alignmentHint: AIAlignment,
    val reward: ResourceWallet,
    val progressDelta: Double,
    val message: String,
)

data class RandomEventDef(
    val id: String,
    val title: String,
    val body: String,
    val options: List<EventOption>,
)

data class OfflineReward(
    val seconds: Long,
    val gained: ResourceWallet,
)

data class GameState(
    val planetIndex: Int = 0,
    val stageProgress: Double = 0.0,
    val planetProgress: Map<Int, Double> = emptyMap(),
    val planetArrivalEpochMs: Map<Int, Long> = emptyMap(),
    val resources: ResourceWallet = ResourceWallet(energy = 20.0),
    val upgrades: Map<String, Int> = emptyMap(),
    val aiAlignment: AIAlignment = AIAlignment.LIFE,
    val alignmentScore: Map<AIAlignment, Int> = AIAlignment.entries.associateWith { 0 },
    val selectedEvolutionPlanets: Set<Int> = emptySet(),
    val completedPlanetCount: Int = 0,
    val prestigeCores: Int = 0,
    val activeEventIndex: Int? = null,
    val eventRewardBoosts: Int = 0,
    val adBoostUntilEpochMs: Long = 0L,
    val selectedPlanetSkinId: String = "default",
    val lastSavedEpochMs: Long = 0L,
) {
    fun stage(balance: GameBalance = GameBalance): PlanetStage {
        val thresholds = balance.planets[planetIndex].stageThresholds
        val progress = maxOf(stageProgress, planetProgress[planetIndex] ?: 0.0)
        return when {
            progress >= thresholds[2] -> PlanetStage.COMPLETE
            progress >= thresholds[1] -> PlanetStage.GROWTH
            progress >= thresholds[0] -> PlanetStage.ACTIVATED
            else -> PlanetStage.COLLAPSED
        }
    }
}
