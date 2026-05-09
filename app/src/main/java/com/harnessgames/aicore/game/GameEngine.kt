package com.harnessgames.aicore.game

import kotlin.math.min
import kotlin.math.pow

class GameEngine(
    private val balance: GameBalance = GameBalance,
    private val nowMs: () -> Long = { System.currentTimeMillis() },
) {
    fun initialState(): GameState = GameState(
        planetProgress = mapOf(0 to 0.0),
        planetArrivalEpochMs = mapOf(0 to nowMs()),
        lastSavedEpochMs = nowMs(),
    )

    fun tick(state: GameState, elapsedSeconds: Double): GameState {
        if (elapsedSeconds <= 0.0) return state
        val gain = productionPerSecond(state) * elapsedSeconds
        return applyGainAndProgress(state, gain, elapsedSeconds)
    }

    fun productionPerSecond(state: GameState): ResourceWallet {
        val planet = balance.planets[state.planetIndex]
        val energyMultiplier = multiplierFor(state, UpgradeEffect.ENERGY_PRODUCTION) *
            alignmentEnergyMultiplier(state) *
            adBoostMultiplier(state)
        val biomassMultiplier = multiplierFor(state, UpgradeEffect.BIOMASS_PRODUCTION) *
            alignmentBiomassMultiplier(state)
        val aiDataMultiplier = multiplierFor(state, UpgradeEffect.AI_DATA_PRODUCTION) *
            alignmentAiDataMultiplier(state)
        val prestigeMultiplier = 1.0 + state.prestigeCores * 0.05

        val activeProduction = ResourceWallet(
            energy = planet.baseProduction.energy * energyMultiplier * prestigeMultiplier,
            biomass = planet.baseProduction.biomass * biomassMultiplier * prestigeMultiplier,
            aiData = planet.baseProduction.aiData * aiDataMultiplier * prestigeMultiplier,
        )
        return activeProduction + colonyProductionPerSecond(state)
    }

    fun colonyProductionPerSecond(state: GameState): ResourceWallet {
        var output = ResourceWallet()
        val prestigeMultiplier = 1.0 + state.prestigeCores * 0.05
        for (index in 0..maxUnlockedPlanetIndex(state)) {
            if (index == state.planetIndex || stageFor(state, index) != PlanetStage.COMPLETE) continue
            val base = balance.planets[index].baseProduction
            val upgradeFactor = 1.0 + planetUpgradeLevels(state, index) * 0.025
            output += base * (0.22 * upgradeFactor * prestigeMultiplier)
        }
        return output
    }

    fun tapPlanet(state: GameState): GameState {
        val tapMultiplier = multiplierFor(state, UpgradeEffect.TAP_POWER) *
            if (state.aiAlignment == AIAlignment.COMBAT) 1.35 else 1.0
        val gain = ResourceWallet(energy = productionPerSecond(state).energy * 5.0 * tapMultiplier + 2.0)
        return applyGainAndProgress(state, gain, elapsedSeconds = 0.25)
    }

    fun costFor(upgrade: UpgradeDef, level: Int): ResourceWallet {
        return upgrade.baseCost * upgrade.costScale.pow(level.toDouble())
    }

    fun purchaseUpgrade(state: GameState, upgradeId: String): GameState {
        val upgrade = balance.upgradeById(upgradeId)
        val level = state.upgrades[upgradeId] ?: 0
        if (level >= upgrade.maxLevel) return state

        val cost = costFor(upgrade, level)
        if (!state.resources.canAfford(cost)) return state

        val updatedLevels = state.upgrades + (upgradeId to level + 1)
        val progressBonus = when (upgrade.effect) {
            UpgradeEffect.RESTORE_SPEED -> 25.0 + level * 2.0
            else -> 8.0 + level
        }
        return withCurrentProgress(
            state.copy(
                resources = state.resources - cost,
                upgrades = updatedLevels,
            ),
            state.stageProgress + progressBonus,
        )
    }

    fun canTravel(state: GameState): Boolean {
        if (state.planetIndex >= balance.planets.lastIndex) return false
        val nextIndex = state.planetIndex + 1
        if (nextIndex <= maxUnlockedPlanetIndex(state)) return true
        return state.stage(balance) == PlanetStage.COMPLETE &&
            state.resources.canAfford(balance.planets[state.planetIndex].travelCost) &&
            travelTimeReady(state)
    }

    fun travelToNextPlanet(state: GameState): GameState {
        if (!canTravel(state)) return state
        val nextIndex = state.planetIndex + 1
        if (nextIndex <= maxUnlockedPlanetIndex(state)) return visitPlanet(state, nextIndex)
        val cost = balance.planets[state.planetIndex].travelCost
        val saved = withCurrentProgress(state, state.stageProgress)
        return visitPlanet(
            saved.copy(
                resources = saved.resources - cost,
                completedPlanetCount = maxOf(state.completedPlanetCount, nextIndex),
                planetArrivalEpochMs = saved.planetArrivalEpochMs + (nextIndex to nowMs()),
            ),
            nextIndex,
        )
    }

    fun canVisitPlanet(state: GameState, planetIndex: Int): Boolean {
        return planetIndex in 0..maxUnlockedPlanetIndex(state)
    }

    fun visitPlanet(state: GameState, planetIndex: Int): GameState {
        if (!canVisitPlanet(state, planetIndex)) return state
        if (planetIndex == state.planetIndex) return state
        val saved = withCurrentProgress(state, state.stageProgress)
        val targetProgress = progressFor(saved, planetIndex)
        return saved.copy(
            planetIndex = planetIndex,
            stageProgress = targetProgress,
            planetProgress = saved.planetProgress + (planetIndex to targetProgress),
            activeEventIndex = null,
        )
    }

    fun maxUnlockedPlanetIndex(state: GameState): Int {
        return min(balance.planets.lastIndex, maxOf(state.completedPlanetCount, state.planetIndex))
    }

    fun travelWaitRemainingSeconds(state: GameState): Long {
        if (state.planetIndex >= balance.planets.lastIndex) return 0L
        if (state.planetIndex + 1 <= maxUnlockedPlanetIndex(state)) return 0L
        val arrival = currentPlanetArrivalMs(state)
        val elapsedSeconds = ((nowMs() - arrival) / 1000L).coerceAtLeast(0L)
        return (GameBalance.minTravelSeconds - elapsedSeconds).coerceAtLeast(0L)
    }

    fun travelTimeReady(state: GameState): Boolean {
        return travelWaitRemainingSeconds(state) <= 0L
    }

    fun canPrestige(state: GameState): Boolean {
        return state.planetIndex == balance.planets.lastIndex && state.stage(balance) == PlanetStage.COMPLETE
    }

    fun prestige(state: GameState): GameState {
        if (!canPrestige(state)) return state
        val gainedCores = 1 + state.completedPlanetCount / balance.planets.size
        return GameState(
            planetProgress = mapOf(0 to 0.0),
            planetArrivalEpochMs = mapOf(0 to nowMs()),
            resources = ResourceWallet(energy = 50.0 + gainedCores * 25.0),
            prestigeCores = state.prestigeCores + gainedCores,
            aiAlignment = state.aiAlignment,
            alignmentScore = state.alignmentScore,
            lastSavedEpochMs = nowMs(),
        )
    }

    fun chooseEvolution(state: GameState, alignment: AIAlignment): GameState {
        if (state.planetIndex in state.selectedEvolutionPlanets) return state
        val newScores = state.alignmentScore + (alignment to ((state.alignmentScore[alignment] ?: 0) + 1))
        val dominant = newScores.maxBy { it.value }.key
        val reward = when (alignment) {
            AIAlignment.LIFE -> ResourceWallet(biomass = 10.0)
            AIAlignment.INDUSTRIAL -> ResourceWallet(energy = 200.0)
            AIAlignment.COMBAT -> ResourceWallet(aiData = 8.0)
        }
        return state.copy(
            resources = state.resources + reward,
            aiAlignment = dominant,
            alignmentScore = newScores,
            selectedEvolutionPlanets = state.selectedEvolutionPlanets + state.planetIndex,
        ).let { withCurrentProgress(it, state.stageProgress + 60.0) }
    }

    fun openRandomEvent(state: GameState): GameState {
        if (state.activeEventIndex != null) return state
        val index = ((state.planetIndex * 31 + state.completedPlanetCount * 7 + state.resources.energy.toInt()) %
            balance.randomEvents.size).coerceAtLeast(0)
        return state.copy(activeEventIndex = index)
    }

    fun resolveEvent(state: GameState, optionIndex: Int): GameState {
        val eventIndex = state.activeEventIndex ?: return state
        val event = balance.randomEvents[eventIndex]
        val option = event.options.getOrNull(optionIndex) ?: return state
        val score = state.alignmentScore[option.alignmentHint] ?: 0
        val updatedScores = state.alignmentScore + (option.alignmentHint to score + 1)
        val dominant = updatedScores.maxBy { it.value }.key
        val rewardMultiplier = if (state.eventRewardBoosts > 0) 2.0 else 1.0
        return withCurrentProgress(
            state.copy(
            resources = state.resources + option.reward * rewardMultiplier,
            activeEventIndex = null,
            eventRewardBoosts = (state.eventRewardBoosts - 1).coerceAtLeast(0),
            alignmentScore = updatedScores,
            aiAlignment = dominant,
            ),
            state.stageProgress + option.progressDelta,
        )
    }

    fun applyRewardedAdBoost(state: GameState): GameState {
        return state.copy(adBoostUntilEpochMs = nowMs() + GameBalance.adBoostSeconds * 1000L)
    }

    fun applyRewardedEventBoost(state: GameState): GameState {
        return state.copy(eventRewardBoosts = (state.eventRewardBoosts + 1).coerceAtMost(3))
    }

    fun applyRewardedRestoreBurst(state: GameState): GameState {
        val max = balance.planets[state.planetIndex].stageThresholds.last()
        return withCurrentProgress(state, state.stageProgress + max * 0.08)
    }

    fun grantResources(
        state: GameState,
        reward: ResourceWallet,
        progressDelta: Double = 0.0,
        boostSeconds: Long = 0L,
    ): GameState {
        val boostUntil = if (boostSeconds > 0L) {
            maxOf(state.adBoostUntilEpochMs, nowMs() + boostSeconds * 1000L)
        } else {
            state.adBoostUntilEpochMs
        }
        return withCurrentProgress(
            state.copy(
                resources = state.resources + reward,
                adBoostUntilEpochMs = boostUntil,
            ),
            state.stageProgress + progressDelta,
        )
    }

    fun claimOffline(state: GameState, currentEpochMs: Long = nowMs()): Pair<GameState, OfflineReward?> {
        if (state.lastSavedEpochMs <= 0L || currentEpochMs <= state.lastSavedEpochMs) {
            return state.copy(lastSavedEpochMs = currentEpochMs) to null
        }
        val elapsedSeconds = min((currentEpochMs - state.lastSavedEpochMs) / 1000L, GameBalance.offlineCapSeconds)
        if (elapsedSeconds < 60L) {
            return state.copy(lastSavedEpochMs = currentEpochMs) to null
        }
        val offlineRate = GameBalance.offlineBaseRate + offlineBonus(state)
        val gain = productionPerSecond(state) * elapsedSeconds.toDouble() * offlineRate
        val updated = applyGainAndProgress(state, gain, elapsedSeconds.toDouble() * 0.25)
            .copy(lastSavedEpochMs = currentEpochMs)
        return updated to OfflineReward(elapsedSeconds, gain)
    }

    fun markSaved(state: GameState): GameState = state.copy(lastSavedEpochMs = nowMs())

    private fun applyGainAndProgress(state: GameState, gain: ResourceWallet, elapsedSeconds: Double): GameState {
        val restoreMultiplier = multiplierFor(state, UpgradeEffect.RESTORE_SPEED)
        val progressGain = (gain.energy * 0.12 + elapsedSeconds * 0.8) * restoreMultiplier
        return withCurrentProgress(
            state.copy(
                resources = state.resources + gain,
            ),
            state.stageProgress + progressGain,
        )
    }

    private fun withCurrentProgress(state: GameState, progress: Double): GameState {
        val clamped = clampProgress(state, progress)
        return state.copy(
            stageProgress = clamped,
            planetProgress = state.planetProgress + (state.planetIndex to clamped),
        )
    }

    private fun progressFor(state: GameState, planetIndex: Int): Double {
        if (planetIndex == state.planetIndex) {
            return maxOf(state.stageProgress, state.planetProgress[planetIndex] ?: 0.0)
        }
        state.planetProgress[planetIndex]?.let { return it }
        return if (planetIndex < maxUnlockedPlanetIndex(state)) {
            balance.planets[planetIndex].stageThresholds.last()
        } else {
            0.0
        }
    }

    private fun currentPlanetArrivalMs(state: GameState): Long {
        return state.planetArrivalEpochMs[state.planetIndex]
            ?: state.lastSavedEpochMs.takeIf { it > 0L }
            ?: nowMs()
    }

    private fun stageFor(state: GameState, planetIndex: Int): PlanetStage {
        val progress = progressFor(state, planetIndex)
        val thresholds = balance.planets[planetIndex].stageThresholds
        return when {
            progress >= thresholds[2] -> PlanetStage.COMPLETE
            progress >= thresholds[1] -> PlanetStage.GROWTH
            progress >= thresholds[0] -> PlanetStage.ACTIVATED
            else -> PlanetStage.COLLAPSED
        }
    }

    private fun planetUpgradeLevels(state: GameState, planetIndex: Int): Int {
        return balance.upgrades
            .drop(planetIndex * 4)
            .take(4)
            .sumOf { upgrade -> state.upgrades[upgrade.id] ?: 0 }
    }

    private fun multiplierFor(state: GameState, effect: UpgradeEffect): Double {
        var multiplier = 1.0
        balance.upgrades.filter { it.effect == effect }.forEach { upgrade ->
            val level = state.upgrades[upgrade.id] ?: 0
            multiplier += level * upgrade.valuePerLevel
        }
        return multiplier
    }

    private fun offlineBonus(state: GameState): Double {
        val upgradeBonus = balance.upgrades
            .filter { it.effect == UpgradeEffect.OFFLINE_GAIN }
            .sumOf { upgrade -> (state.upgrades[upgrade.id] ?: 0) * upgrade.valuePerLevel }
        val lifeBonus = if (state.aiAlignment == AIAlignment.LIFE) 0.1 else 0.0
        return upgradeBonus + lifeBonus
    }

    private fun alignmentEnergyMultiplier(state: GameState): Double =
        if (state.aiAlignment == AIAlignment.INDUSTRIAL) 1.25 else 1.0

    private fun alignmentBiomassMultiplier(state: GameState): Double =
        if (state.aiAlignment == AIAlignment.LIFE) 1.2 else 1.0

    private fun alignmentAiDataMultiplier(state: GameState): Double =
        if (state.aiAlignment == AIAlignment.COMBAT) 1.15 else 1.0

    private fun adBoostMultiplier(state: GameState): Double =
        if (state.adBoostUntilEpochMs > nowMs()) GameBalance.adBoostMultiplier else 1.0

    private fun clampProgress(state: GameState, progress: Double): Double {
        val max = balance.planets[state.planetIndex].stageThresholds.last()
        return progress.coerceIn(0.0, max)
    }
}
