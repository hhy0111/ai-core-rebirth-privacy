package com.harnessgames.aicore.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    private var now = 1_000_000L
    private val engine = GameEngine(nowMs = { now })

    @Test
    fun tickAddsProductionAndProgress() {
        val state = engine.initialState()

        val updated = engine.tick(state, 10.0)

        assertTrue(updated.resources.energy > state.resources.energy)
        assertTrue(updated.stageProgress > state.stageProgress)
    }

    @Test
    fun purchaseUpgradeSpendsResourcesAndRaisesLevel() {
        val state = engine.initialState().copy(resources = ResourceWallet(energy = 1_000.0))

        val updated = engine.purchaseUpgrade(state, "solar_lattice")

        assertEquals(1, updated.upgrades["solar_lattice"])
        assertTrue(updated.resources.energy < state.resources.energy)
        assertTrue(engine.productionPerSecond(updated).energy > engine.productionPerSecond(state).energy)
    }

    @Test
    fun offlineRewardIsCappedAtEightHours() {
        val saved = engine.initialState().copy(lastSavedEpochMs = now)
        now += 24L * 60L * 60L * 1000L

        val (_, reward) = engine.claimOffline(saved)

        assertEquals(GameBalance.offlineCapSeconds, reward?.seconds)
    }

    @Test
    fun travelRequiresCompletePlanetAndCost() {
        val complete = engine.initialState().copy(
            stageProgress = GameBalance.planets[0].stageThresholds.last(),
            planetArrivalEpochMs = mapOf(0 to now - GameBalance.minTravelSeconds * 1000L),
            resources = ResourceWallet(energy = 1_000_000.0, biomass = 100_000.0, aiData = 50_000.0),
        )

        val next = engine.travelToNextPlanet(complete)

        assertEquals(1, next.planetIndex)
        assertEquals(1, next.completedPlanetCount)
    }

    @Test
    fun travelRequiresSevenDayStabilizationForNewPlanet() {
        val completeButFresh = engine.initialState().copy(
            stageProgress = GameBalance.planets[0].stageThresholds.last(),
            resources = ResourceWallet(energy = 1_000_000.0, biomass = 100_000.0, aiData = 50_000.0),
        )

        assertTrue(!engine.canTravel(completeButFresh))
        assertTrue(engine.travelWaitRemainingSeconds(completeButFresh) > 0L)
    }

    @Test
    fun prestigeResetsRunAndKeepsCoreBonus() {
        val finalPlanet = GameBalance.planets.lastIndex
        val ready = engine.initialState().copy(
            planetIndex = finalPlanet,
            stageProgress = GameBalance.planets[finalPlanet].stageThresholds.last(),
            completedPlanetCount = 4,
            prestigeCores = 1,
        )

        val restarted = engine.prestige(ready)

        assertEquals(0, restarted.planetIndex)
        assertEquals(2, restarted.prestigeCores)
        assertTrue(restarted.resources.energy > 50.0)
    }

    @Test
    fun eventRewardBoostDoublesNextResolvedEventOnly() {
        val eventState = engine.openRandomEvent(engine.initialState())
        val boosted = engine.applyRewardedEventBoost(eventState)

        val resolved = engine.resolveEvent(boosted, 0)

        assertEquals(0, resolved.eventRewardBoosts)
        assertTrue(resolved.resources.energy + resolved.resources.biomass + resolved.resources.aiData > eventState.resources.energy)
    }

    @Test
    fun grantResourcesCanApplyStoreBoost() {
        val state = engine.initialState()

        val rewarded = engine.grantResources(
            state = state,
            reward = ResourceWallet(energy = 100.0),
            progressDelta = 10.0,
            boostSeconds = 60L,
        )

        assertTrue(rewarded.resources.energy >= state.resources.energy + 100.0)
        assertTrue(rewarded.stageProgress >= 10.0)
        assertTrue(rewarded.adBoostUntilEpochMs > now)
    }
}
