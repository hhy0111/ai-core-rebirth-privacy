package com.harnessgames.aicore.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.harnessgames.aicore.R
import com.harnessgames.aicore.game.AIAlignment
import com.harnessgames.aicore.game.GameBalance
import com.harnessgames.aicore.game.GameEngine
import com.harnessgames.aicore.game.GameState
import com.harnessgames.aicore.game.OfflineReward
import com.harnessgames.aicore.game.Panel
import com.harnessgames.aicore.game.PlanetStage
import com.harnessgames.aicore.game.ResourceWallet
import com.harnessgames.aicore.game.UpgradeDef
import com.harnessgames.aicore.game.UpgradeEffect
import com.harnessgames.aicore.monetization.AdsController
import com.harnessgames.aicore.monetization.BillingController
import com.harnessgames.aicore.monetization.BillingListener
import com.harnessgames.aicore.monetization.RewardedPlacement
import com.harnessgames.aicore.monetization.StoreProduct
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

class GameView(
    context: Context,
    private val engine: GameEngine,
    initialState: GameState,
    initialOfflineReward: OfflineReward?,
    private val adsController: AdsController,
    private val billingController: BillingController,
    private val saveState: (GameState) -> Unit,
) : View(context) {
    private var state = initialState
    private var currentPanel = Panel.UPGRADE
    private var screenMode = ScreenMode.OPENING
    private var screenModeStartedMs = SystemClock.elapsedRealtime()
    private var lastFrameMs = SystemClock.elapsedRealtime()
    private var lastPersistMs = lastFrameMs
    private var pendingOfflineDoubleReward = initialOfflineReward?.gained
    private var showIntroOverlay = true
    private var rewardBurst: RewardBurst? = null
    private var upgradeBurst: UpgradeBurst? = null
    private val incomeFloaters = mutableListOf<IncomeFloater>()
    private var incomeFxElapsedMs = 0L
    private var incomeFxIndex = 0
    private var passingEncounter: PassingEncounter? = null
    private var encounterBurst: EncounterBurst? = null
    private var encounterSpawnMs = 18_000L
    private var travelMapScrollPx = 0f
    private var travelMapBounds: RectF? = null
    private var travelMapDragLastX: Float? = null
    private var travelMapDragDistance = 0f
    private var screenTransition: ScreenTransition? = null
    private val describedPanels = mutableSetOf<Panel>()
    private val handler = Handler(Looper.getMainLooper())
    private val touchTargets = mutableListOf<TouchTarget>()
    private val bitmapCache = mutableMapOf<Int, Bitmap>()

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(2f)
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = sp(14f)
    }
    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        isDither = true
    }
    private val encounterDefs = listOf(
        EncounterDef("pass_ship", "통과선", R.drawable.encounter_pass_ship, EncounterRewardType.ENERGY, 110L),
        EncounterDef("space_lifeform", "우주 생명체", R.drawable.encounter_space_lifeform, EncounterRewardType.BIOMASS, 150L),
        EncounterDef("data_meteor", "데이터 유성", R.drawable.encounter_data_meteor, EncounterRewardType.AI_DATA, 155L, R.drawable.fx_rare_reward_stardust),
        EncounterDef("scout_ship", "정찰선", R.drawable.encounter_scout_ship, EncounterRewardType.ENERGY, 90L),
        EncounterDef("cargo_drone", "화물 드론", R.drawable.encounter_cargo_drone, EncounterRewardType.MIXED, 120L),
        EncounterDef("luminous_lifeform", "발광 생명체", R.drawable.encounter_luminous_lifeform, EncounterRewardType.BIOMASS, 140L),
        EncounterDef("data_probe", "데이터 탐사체", R.drawable.encounter_data_probe, EncounterRewardType.AI_DATA, 130L),
        EncounterDef("rare_meteor", "희귀 유성", R.drawable.encounter_rare_meteor, EncounterRewardType.RARE, 160L, R.drawable.fx_rare_reward_stardust),
        EncounterDef("ancient_satellite", "고대 위성", R.drawable.encounter_ancient_satellite, EncounterRewardType.AI_DATA, 170L),
        EncounterDef("seed_swarm", "씨앗 군집", R.drawable.encounter_seed_swarm, EncounterRewardType.BIOMASS, 160L),
        EncounterDef("warp_merchant", "워프 상인", R.drawable.encounter_warp_merchant, EncounterRewardType.MIXED, 180L),
        EncounterDef("emotion_memory", "감정 기억", R.drawable.encounter_emotion_memory, EncounterRewardType.AI_DATA, 200L),
        EncounterDef("unstable_rift", "불안정 균열", R.drawable.encounter_unstable_rift, EncounterRewardType.RIFT, 220L, R.drawable.fx_rift_resolve),
    )

    private val ticker = object : Runnable {
        override fun run() {
            val now = SystemClock.elapsedRealtime()
            val frameDeltaMs = (now - lastFrameMs).coerceAtMost(1000L)
            val elapsed = frameDeltaMs.toDouble() / 1000.0
            lastFrameMs = now
            if (screenMode == ScreenMode.OPENING && now - screenModeStartedMs > 2800L) {
                enterLobby("AI 코어 동기화")
            }
            if (screenMode != ScreenMode.OPENING) {
                state = engine.tick(state, elapsed)
                tickIncomeFloaters(frameDeltaMs, engine.productionPerSecond(state))
            }
            if (screenMode == ScreenMode.GAME) {
                tickPassingEncounter(frameDeltaMs)
            }
            if (now - lastPersistMs > 5000L) {
                persistNow()
                lastPersistMs = now
            }
            invalidate()
            handler.postDelayed(this, 250L)
        }
    }

    init {
        isFocusable = true
        billingController.listener = object : BillingListener {
            override fun onBillingCatalogUpdated() {
                invalidate()
            }

            override fun onPurchaseGranted(product: StoreProduct) {
                applyPurchasedProduct(product)
            }

            override fun onBillingMessage(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                invalidate()
            }
        }
        handler.post(ticker)
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(ticker)
        persistNow()
        super.onDetachedFromWindow()
    }

    fun persistNow() {
        state = engine.markSaved(state)
        saveState(state)
    }

    private fun enterLobby(label: String = "로비 연결") {
        screenMode = ScreenMode.LOBBY
        screenModeStartedMs = SystemClock.elapsedRealtime()
        startScreenTransition(label, Color.rgb(98, 242, 255))
        invalidate()
    }

    private fun enterGame(panel: Panel = Panel.UPGRADE) {
        screenMode = ScreenMode.GAME
        screenModeStartedMs = SystemClock.elapsedRealtime()
        currentPanel = panel
        showIntroOverlay = false
        describedPanels += panel
        if (panel == Panel.EVENT && state.activeEventIndex == null) {
            state = engine.openRandomEvent(state)
        }
        startScreenTransition(if (panel == Panel.UPGRADE) "행성 복원 시작" else "${panel.label} 진입", panelAccent(panel))
        invalidate()
    }

    private fun switchPanel(panel: Panel) {
        val changed = currentPanel != panel
        currentPanel = panel
        if (panel == Panel.TRAVEL) {
            centerTravelMapOnCurrent()
        }
        if (describedPanels.add(panel)) {
            Toast.makeText(context, panelDescription(panel), Toast.LENGTH_SHORT).show()
        }
        if (panel == Panel.EVENT && state.activeEventIndex == null) {
            state = engine.openRandomEvent(state)
            startScreenTransition("이상 현상 스캔", panelAccent(panel))
        } else if (changed) {
            startScreenTransition("${panel.label} 연결", panelAccent(panel))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        touchTargets.clear()
        when (screenMode) {
            ScreenMode.OPENING -> drawOpeningScreen(canvas)
            ScreenMode.LOBBY -> drawLobbyScreen(canvas)
            ScreenMode.GAME -> {
                drawBackground(canvas)
                drawTopHud(canvas)
                drawPlanet(canvas)
                drawAiCompanion(canvas)
                drawPassingEncounter(canvas)
                drawPanel(canvas)
                drawBottomNav(canvas)
                drawIncomeFloaters(canvas)
                drawEncounterBurst(canvas)
                drawRewardBurst(canvas)
                drawUpgradeBurst(canvas)
                if (showIntroOverlay) {
                    drawIntroOverlay(canvas)
                }
            }
        }
        drawScreenTransition(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                travelMapBounds?.let { bounds ->
                    if (screenMode == ScreenMode.GAME && currentPanel == Panel.TRAVEL && bounds.contains(event.x, event.y)) {
                        travelMapDragLastX = event.x
                        travelMapDragDistance = 0f
                        return true
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val lastX = travelMapDragLastX
                if (lastX != null) {
                    val dx = event.x - lastX
                    travelMapDragLastX = event.x
                    travelMapDragDistance += kotlin.math.abs(dx)
                    travelMapScrollPx = clampTravelMapScroll(travelMapScrollPx - dx)
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                travelMapDragLastX = null
                travelMapDragDistance = 0f
                return true
            }
        }
        if (event.action != MotionEvent.ACTION_UP) return true
        if (travelMapDragLastX != null) {
            val dragged = travelMapDragDistance > dp(6f)
            travelMapDragLastX = null
            travelMapDragDistance = 0f
            if (dragged) {
                invalidate()
                return true
            }
        }
        val target = touchTargets.lastOrNull { it.bounds.contains(event.x, event.y) }
        if (target != null) {
            target.action()
            invalidate()
            return true
        }

        if (screenMode != ScreenMode.GAME) return true

        val planetCenterY = height * 0.35f
        val planetRadius = min(width * 0.28f, height * 0.17f)
        val dx = event.x - width * 0.5f
        val dy = event.y - planetCenterY
        if (dx * dx + dy * dy <= planetRadius * planetRadius) {
            val before = state
            state = engine.tapPlanet(state)
            startIncomeFloater(state.resources - before.resources, "터치", event.x, event.y)
            invalidate()
            return true
        }
        return true
    }

    private fun drawBackground(canvas: Canvas) {
        val covered = drawBitmapCover(
            canvas = canvas,
            resId = planetBackgroundRes(state.planetIndex),
            bounds = RectF(0f, 0f, width.toFloat(), height.toFloat()),
        ) || drawBitmapCover(
            canvas = canvas,
            resId = R.drawable.bg_nebula_main,
            bounds = RectF(0f, 0f, width.toFloat(), height.toFloat()),
        )
        if (!covered) {
            canvas.drawColor(Color.rgb(12, 15, 28))
        }
        fill.color = Color.argb(36, 3, 7, 16)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fill)
        stroke.color = Color.argb(50, 98, 242, 255)
        stroke.strokeWidth = dp(1f)
        val step = dp(42f)
        var x = -step
        while (x < width + step) {
            canvas.drawLine(x, 0f, x + height * 0.35f, height.toFloat(), stroke)
            x += step
        }
    }

    private fun drawOpeningScreen(canvas: Canvas) {
        drawBackground(canvas)
        val now = SystemClock.elapsedRealtime()
        val t = ((now - screenModeStartedMs).toFloat() / 2800f).coerceIn(0f, 1f)
        val pulse = 0.5f + 0.5f * sin(now.toFloat() / 180f)
        val cx = width * 0.5f
        val cy = height * 0.38f
        for (i in 0 until 14) {
            stroke.color = Color.argb(36 + (i % 3) * 12, 98, 242, 255)
            stroke.strokeWidth = dp(1f + (i % 4) * 0.35f)
            val y = height * 0.13f + i * dp(30f) + t * dp(70f)
            canvas.drawLine(dp(30f + i * 14f), y, width - dp(55f) + i * dp(3f), y + dp(90f), stroke)
        }
        canvas.save()
        canvas.rotate((now % 9000L).toFloat() / 9000f * 360f, cx, cy)
        drawBitmapCenterCrop(canvas, R.drawable.fx_energy_ring, RectF(cx - dp(142f), cy - dp(142f), cx + dp(142f), cy + dp(142f)), alpha = 230)
        canvas.restore()
        drawBitmapCenterCrop(canvas, planetStageRes(state.stage()), RectF(cx - dp(94f), cy - dp(94f), cx + dp(94f), cy + dp(94f)))
        drawBitmapFit(canvas, aiCompanionRes(state.aiAlignment), RectF(cx - dp(46f), cy - dp(68f), cx + dp(46f), cy + dp(38f)), alpha = 245)
        fill.color = Color.argb((28 + pulse * 50).toInt(), 98, 242, 255)
        canvas.drawCircle(cx, cy, dp(130f + pulse * 8f), fill)

        text.textAlign = Paint.Align.CENTER
        text.color = Color.WHITE
        text.textSize = sp(25f)
        canvas.drawText("AI 코어 리버스", cx, height * 0.17f, text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(11.5f)
        canvas.drawText("멸망 이후 마지막 감정 코어 재기동", cx, height * 0.205f, text)
        text.color = Color.rgb(255, 215, 90)
        text.textSize = sp(12.5f)
        canvas.drawText("행성 복원 루프 초기화", cx, height * 0.62f, text)

        val bar = RectF(width * 0.18f, height * 0.645f, width * 0.82f, height * 0.657f)
        fill.color = Color.argb(220, 17, 24, 42)
        canvas.drawRoundRect(bar, dp(8f), dp(8f), fill)
        fill.color = Color.rgb(98, 242, 255)
        canvas.drawRoundRect(RectF(bar.left + dp(2f), bar.top + dp(2f), bar.left + dp(2f) + (bar.width() - dp(4f)) * t, bar.bottom - dp(2f)), dp(6f), dp(6f), fill)

        val button = RectF(width * 0.22f, height * 0.81f, width * 0.78f, height * 0.86f)
        drawButton(canvas, button, true)
        text.color = Color.WHITE
        text.textSize = sp(13f)
        canvas.drawText("탭하여 로비 진입", button.centerX(), button.top + button.height() * 0.62f, text)
        text.textAlign = Paint.Align.LEFT
        touchTargets += TouchTarget(RectF(0f, 0f, width.toFloat(), height.toFloat())) {
            enterLobby("오프닝 스킵")
        }
    }

    private fun drawLobbyScreen(canvas: Canvas) {
        drawBackground(canvas)
        val cx = width * 0.5f
        val now = SystemClock.elapsedRealtime()
        text.textAlign = Paint.Align.CENTER
        text.color = Color.WHITE
        text.textSize = sp(20f)
        canvas.drawText("AI 코어 리버스", cx, dp(58f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(10.5f)
        canvas.drawText("행성을 복원하고 다음 은하로 확장하세요.", cx, dp(82f), text)

        val planetY = height * 0.255f
        canvas.save()
        canvas.rotate((now % 9000L).toFloat() / 9000f * 360f, cx, planetY)
        drawBitmapCenterCrop(canvas, R.drawable.fx_energy_ring, RectF(cx - dp(122f), planetY - dp(122f), cx + dp(122f), planetY + dp(122f)), alpha = 220)
        canvas.restore()
        drawBitmapCenterCrop(canvas, planetStageRes(state.stage()), RectF(cx - dp(76f), planetY - dp(76f), cx + dp(76f), planetY + dp(76f)))
        drawBitmapFit(canvas, aiCompanionRes(state.aiAlignment), RectF(cx + dp(70f), planetY - dp(76f), cx + dp(132f), planetY - dp(12f)))
        text.color = Color.WHITE
        text.textSize = sp(13f)
        canvas.drawText(GameBalance.planets[state.planetIndex].name, cx, planetY + dp(102f), text)
        text.color = Color.rgb(255, 215, 90)
        text.textSize = sp(10.5f)
        canvas.drawText("복원 ${progressPercent()}%  |  ${state.aiAlignment.label} AI", cx, planetY + dp(122f), text)
        text.textAlign = Paint.Align.LEFT

        val briefing = RectF(dp(22f), height * 0.43f, width - dp(22f), height * 0.61f)
        fill.color = Color.argb(226, 17, 24, 42)
        canvas.drawRoundRect(briefing, dp(8f), dp(8f), fill)
        stroke.color = Color.argb(120, 98, 242, 255)
        stroke.strokeWidth = dp(1.5f)
        canvas.drawRoundRect(briefing, dp(8f), dp(8f), stroke)
        text.color = Color.WHITE
        text.textSize = sp(12f)
        canvas.drawText("기능 브리핑", briefing.left + dp(12f), briefing.top + dp(24f), text)
        val panels = listOf(Panel.UPGRADE, Panel.TRAVEL, Panel.AI, Panel.EVENT, Panel.SHOP)
        panels.forEachIndexed { index, panel ->
            val y = briefing.top + dp(48f + index * 20f)
            drawBitmapFit(canvas, navIconRes(panel), RectF(briefing.left + dp(12f), y - dp(13f), briefing.left + dp(26f), y + dp(1f)), alpha = 230)
            text.color = if (index == 0) Color.WHITE else Color.rgb(210, 232, 240)
            text.textSize = sp(8.8f)
            canvas.drawText("${panel.label}: ${panelDescription(panel)}", briefing.left + dp(34f), y, text)
        }

        val buttons = listOf(
            Triple("게임 시작", "행성 복원 화면으로 진입", Panel.UPGRADE),
            Triple("AI 브리핑", "진화 선택과 성향 확인", Panel.AI),
            Triple("상점 보기", "광고 보상과 유료 상품 확인", Panel.SHOP),
        )
        buttons.forEachIndexed { index, button ->
            val top = height * 0.65f + index * dp(58f)
            val bounds = RectF(dp(42f), top, width - dp(42f), top + dp(44f))
            drawButton(canvas, bounds, true)
            text.color = Color.WHITE
            text.textSize = sp(12f)
            canvas.drawText(button.first, bounds.left + dp(14f), bounds.top + dp(18f), text)
            text.color = Color.rgb(160, 246, 255)
            text.textSize = sp(8.8f)
            canvas.drawText(button.second, bounds.left + dp(14f), bounds.top + dp(34f), text)
            touchTargets += TouchTarget(bounds) { enterGame(button.third) }
        }
        text.textAlign = Paint.Align.CENTER
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(9.2f)
        canvas.drawText("로비에서도 생산은 계속 누적됩니다.", cx, height - dp(36f), text)
        text.textAlign = Paint.Align.LEFT
    }

    private fun drawTopHud(canvas: Canvas) {
        val h = dp(84f)
        fill.color = Color.argb(220, 17, 24, 42)
        canvas.drawRect(0f, 0f, width.toFloat(), h, fill)
        val planet = GameBalance.planets[state.planetIndex]
        val eps = engine.productionPerSecond(state)
        text.color = Color.WHITE
        text.textSize = sp(15f)
        canvas.drawText(planet.name, dp(16f), dp(24f), text)
        text.textSize = sp(12f)
        text.color = Color.rgb(160, 246, 255)
        canvas.drawText("단계 ${state.stage().label}  ${progressPercent()}%", dp(16f), dp(45f), text)
        canvas.drawText("E ${formatNumber(state.resources.energy)}  +${formatNumber(eps.energy)}/s", dp(16f), dp(66f), text)
        canvas.drawText("B ${formatNumber(state.resources.biomass)}", width * 0.55f, dp(45f), text)
        canvas.drawText("AI ${formatNumber(state.resources.aiData)}", width * 0.55f, dp(66f), text)
    }

    private fun drawPlanet(canvas: Canvas) {
        val cx = width * 0.5f
        val cy = height * 0.35f
        val radius = min(width * 0.28f, height * 0.17f)
        val stage = state.stage()
        val visualStats = currentVisualStats()
        val pulse = ((SystemClock.elapsedRealtime() % 1600L).toFloat() / 1600f)
        val spin = ((SystemClock.elapsedRealtime() % 9000L).toFloat() / 9000f) * 360f

        fill.color = Color.argb((70 + pulse * 60 + visualStats.intensity * 36f).toInt().coerceIn(50, 180), 98, 242, 255)
        canvas.drawCircle(cx, cy, radius * (1.35f + pulse * 0.08f + visualStats.visualTier * 0.035f), fill)
        canvas.save()
        canvas.rotate(spin, cx, cy)
        drawBitmapCenterCrop(
            canvas,
            R.drawable.fx_energy_ring,
            RectF(cx - radius * 1.55f, cy - radius * 1.55f, cx + radius * 1.55f, cy + radius * 1.55f),
            alpha = 230,
        )
        canvas.restore()
        canvas.save()
        canvas.rotate(-spin * 0.72f, cx, cy)
        drawBitmapCenterCrop(
            canvas,
            R.drawable.fx_energy_ring,
            RectF(cx - radius * 1.28f, cy - radius * 1.28f, cx + radius * 1.28f, cy + radius * 1.28f),
            alpha = 140,
        )
        canvas.restore()
        drawPlanetVisualProgression(canvas, cx, cy, radius, visualStats, spin, false)
        drawEnergyParticles(canvas, cx, cy, radius, visualStats)
        drawBitmapCenterCrop(
            canvas,
            planetStageRes(stage),
            RectF(cx - radius * 1.24f, cy - radius * 1.24f, cx + radius * 1.24f, cy + radius * 1.24f),
        )
        drawPlanetDetails(canvas, cx, cy, radius, stage)
        drawPlanetVisualProgression(canvas, cx, cy, radius, visualStats, spin, true)
        drawPlanetUpgradePulse(canvas, cx, cy, radius)
        drawResourceStreams(canvas, cx, cy, radius, visualStats)

        text.textAlign = Paint.Align.CENTER
        text.textSize = sp(13f)
        text.color = Color.WHITE
        canvas.drawText("코어 터치", cx, cy + radius + dp(28f), text)
        text.textAlign = Paint.Align.LEFT
    }

    private fun drawPlanetVisualProgression(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        stats: VisualStats,
        spin: Float,
        foreground: Boolean,
    ) {
        val time = SystemClock.elapsedRealtime().toFloat() / 1000f
        if (!foreground) {
            val ringCount = min(7, 1 + stats.stageIndex + stats.upgradeLevels / 5)
            for (i in 0 until ringCount) {
                val effect = listOf(
                    UpgradeEffect.ENERGY_PRODUCTION,
                    UpgradeEffect.BIOMASS_PRODUCTION,
                    UpgradeEffect.AI_DATA_PRODUCTION,
                    UpgradeEffect.RESTORE_SPEED,
                    UpgradeEffect.OFFLINE_GAIN,
                    UpgradeEffect.TAP_POWER,
                )[i % 6]
                val alpha = (52 + stats.intensity * 90f + 18f * sin(time * 1.7f + i)).toInt().coerceIn(28, 165)
                val ringRadius = radius * (1.12f + i * 0.105f + stats.visualTier * 0.012f)
                val ringAlpha = (alpha + 44).coerceIn(40, 190)
                canvas.save()
                canvas.rotate(spin * (if (i % 2 == 0) 0.6f else -0.44f) + i * 15f, cx, cy)
                drawBitmapCenterCrop(
                    canvas,
                    ringAssetForEffect(effect),
                    RectF(cx - ringRadius * 1.12f, cy - ringRadius * 1.12f, cx + ringRadius * 1.12f, cy + ringRadius * 1.12f),
                    alpha = ringAlpha,
                )
                canvas.restore()
                stroke.color = colorWithAlpha(effectColor(effect), alpha)
                stroke.strokeWidth = dp(1.2f + min(4.5f, stats.upgradeLevels * 0.045f) + (i % 2) * 0.35f)
                canvas.save()
                canvas.rotate(spin * (if (i % 2 == 0) 0.6f else -0.44f) + i * 15f, cx, cy)
                canvas.drawOval(
                    RectF(cx - ringRadius, cy - ringRadius * (0.58f + (i % 3) * 0.055f), cx + ringRadius, cy + ringRadius * (0.58f + (i % 3) * 0.055f)),
                    stroke,
                )
                canvas.restore()
            }
            return
        }

        drawPlanetSurfaceMasks(canvas, cx, cy, radius, stats, spin, time)
        val nodeCount = min(42, 8 + stats.stageIndex * 4 + (stats.upgradeLevels * 0.85f).toInt())
        for (i in 0 until nodeCount) {
            val effect = strongestEffect(stats, i)
            val theta = i * 2.399f + time * (0.16f + (i % 5) * 0.008f)
            val dist = radius * (0.18f + ((i * 37) % 66) / 100f)
            val x = cx + cos(theta) * dist
            val y = cy + sin(theta) * dist * 0.72f
            val size = dp(1.8f + (i % 4) * 0.35f + stats.visualTier * 0.22f)
            val alpha = (130 + 82f * sin(time * 2.8f + i)).toInt().coerceIn(60, 220)
            fill.color = colorWithAlpha(effectColor(effect), alpha)
            canvas.drawCircle(x, y, size, fill)
            if (stats.upgradeLevels > 10 && i % 5 == 0) {
                stroke.color = colorWithAlpha(effectColor(effect), (alpha * 0.7f).toInt())
                stroke.strokeWidth = dp(0.8f)
                canvas.drawCircle(x, y, size * 2.2f, stroke)
            }
        }

        val satelliteCount = min(12, stats.stageIndex + stats.upgradeLevels / 4)
        for (i in 0 until satelliteCount) {
            val effect = strongestEffect(stats, i + 3)
            val orbit = radius * (1.18f + (i % 4) * 0.13f)
            val theta = Math.toRadians((spin * (if (i % 2 == 0) 1.2f else -0.95f) + i * (360f / maxOf(1, satelliteCount))).toDouble()).toFloat()
            val x = cx + cos(theta) * orbit
            val y = cy + sin(theta) * orbit * 0.62f
            val moduleSize = dp(4.6f + stats.visualTier * 0.8f + (i % 3) * 0.6f)
            val propBounds = RectF(x - moduleSize * 2.15f, y - moduleSize * 2.15f, x + moduleSize * 2.15f, y + moduleSize * 2.15f)
            canvas.save()
            canvas.rotate(Math.toDegrees(theta.toDouble()).toFloat(), x, y)
            drawBitmapFit(canvas, propAssetForEffect(effect), propBounds, 235)
            canvas.restore()
            fill.color = colorWithAlpha(effectColor(effect), 214)
            stroke.color = Color.argb(185, 255, 255, 255)
            stroke.strokeWidth = dp(0.9f)
            when (effect) {
                UpgradeEffect.BIOMASS_PRODUCTION -> {
                    canvas.drawOval(RectF(x - moduleSize * 0.65f, y - moduleSize, x + moduleSize * 0.65f, y + moduleSize), fill)
                    canvas.drawOval(RectF(x - moduleSize * 0.65f, y - moduleSize, x + moduleSize * 0.65f, y + moduleSize), stroke)
                }
                UpgradeEffect.AI_DATA_PRODUCTION -> {
                    val box = RectF(x - moduleSize * 0.62f, y - moduleSize * 0.62f, x + moduleSize * 0.62f, y + moduleSize * 0.62f)
                    canvas.drawRect(box, fill)
                    canvas.drawRect(box, stroke)
                }
                else -> {
                    canvas.drawCircle(x, y, moduleSize, fill)
                    canvas.drawCircle(x, y, moduleSize, stroke)
                }
            }
        }
    }

    private fun drawPlanetSurfaceMasks(canvas: Canvas, cx: Float, cy: Float, radius: Float, stats: VisualStats, spin: Float, time: Float) {
        val masks = listOf(
            Triple(UpgradeEffect.ENERGY_PRODUCTION, R.drawable.mask_surface_energy_veins, 0.13f),
            Triple(UpgradeEffect.BIOMASS_PRODUCTION, R.drawable.mask_surface_bio_growth, -0.08f),
            Triple(UpgradeEffect.AI_DATA_PRODUCTION, R.drawable.mask_surface_ai_city, 0.05f),
        )
        masks.forEachIndexed { index, mask ->
            val level = stats.effectLevels[mask.first] ?: 0
            val alpha = (30 + level * 9 + stats.intensity * 46f).toInt().coerceIn(0, 164)
            if (alpha <= 32) return@forEachIndexed
            val size = radius * (1.72f + index * 0.04f)
            canvas.save()
            canvas.rotate(spin * mask.third + time * mask.third * 12f, cx, cy)
            drawBitmapCenterCrop(canvas, mask.second, RectF(cx - size * 0.5f, cy - size * 0.5f, cx + size * 0.5f, cy + size * 0.5f), alpha)
            canvas.restore()
        }
    }

    private fun drawPlanetUpgradePulse(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val burst = upgradeBurst ?: return
        val elapsed = SystemClock.elapsedRealtime() - burst.startedMs
        val t = (elapsed.toFloat() / 1200f).coerceIn(0f, 1f)
        val alpha = ((1f - t) * 220f).toInt().coerceIn(0, 220)
        val pulseSize = radius * (1.28f + t * 1.05f)
        canvas.save()
        canvas.rotate(t * 180f, cx, cy)
        drawBitmapCenterCrop(canvas, pulseAssetForEffect(burst.effect), RectF(cx - pulseSize * 0.5f, cy - pulseSize * 0.5f, cx + pulseSize * 0.5f, cy + pulseSize * 0.5f), (alpha * 0.86f).toInt().coerceIn(0, 220))
        canvas.restore()
        stroke.color = colorWithAlpha(effectColor(burst.effect), alpha)
        stroke.strokeWidth = dp(2.4f + 3.2f * (1f - t))
        canvas.drawCircle(cx, cy, radius * (0.72f + t * 0.94f), stroke)
        fill.color = colorWithAlpha(effectColor(burst.effect), (alpha * 0.28f).toInt())
        canvas.drawCircle(cx, cy, radius * (0.5f + t * 0.35f), fill)
    }

    private fun drawResourceStreams(canvas: Canvas, cx: Float, cy: Float, radius: Float, stats: VisualStats) {
        val time = SystemClock.elapsedRealtime().toFloat() / 1000f
        val streamCount = min(10, 2 + stats.stageIndex + stats.upgradeLevels / 6)
        val endpoints = listOf(
            Triple(dp(62f), dp(66f), UpgradeEffect.ENERGY_PRODUCTION),
            Triple(width * 0.64f, dp(45f), UpgradeEffect.BIOMASS_PRODUCTION),
            Triple(width * 0.66f, dp(66f), UpgradeEffect.AI_DATA_PRODUCTION),
        )
        for (i in 0 until streamCount) {
            val endpoint = endpoints[i % endpoints.size]
            val sourceAngle = -Math.PI.toFloat() * 0.75f + i * 0.48f
            val sx = cx + cos(sourceAngle + time * 0.08f) * radius * 0.72f
            val sy = cy + sin(sourceAngle + time * 0.08f) * radius * 0.48f
            val t = (time * (0.18f + i * 0.013f) + i * 0.17f) % 1f
            val curve = if (i % 2 == 0) dp(16f) else -dp(16f)
            val x = sx + (endpoint.first - sx) * t + sin(t * Math.PI.toFloat()) * curve
            val y = sy + (endpoint.second - sy) * t - sin(t * Math.PI.toFloat()) * dp(25f + (i % 3) * 5f)
            stroke.color = colorWithAlpha(effectColor(endpoint.third), (22 + stats.intensity * 34f).toInt().coerceIn(18, 72))
            stroke.strokeWidth = dp(0.8f)
            canvas.drawLine(sx, sy, endpoint.first, endpoint.second, stroke)
            fill.color = colorWithAlpha(effectColor(endpoint.third), (150 + stats.intensity * 70f).toInt().coerceIn(110, 230))
            canvas.drawCircle(x, y, dp(1.8f + (i % 3) * 0.35f), fill)
        }
    }

    private fun drawEnergyParticles(canvas: Canvas, cx: Float, cy: Float, radius: Float, stats: VisualStats) {
        val time = SystemClock.elapsedRealtime().toFloat() / 1000f
        val count = min(54, 18 + stats.stageIndex * 5 + (stats.upgradeLevels * 0.8f).toInt())
        for (i in 0 until count) {
            val angle = time * (0.34f + i * 0.013f) + i * 0.83f
            val dist = radius * (1.02f + (i % 6) * 0.082f + stats.visualTier * 0.014f)
            val x = cx + cos(angle) * dist
            val y = cy + sin(angle) * dist * 0.72f
            val alpha = (120 + 70 * sin(time * 2.1f + i)).toInt().coerceIn(45, 190)
            fill.color = colorWithAlpha(effectColor(strongestEffect(stats, i)), alpha)
            canvas.drawCircle(x, y, dp(2.1f + (i % 4) * 0.45f + stats.visualTier * 0.22f), fill)
        }
    }

    private fun tickPassingEncounter(deltaMs: Long) {
        encounterBurst = encounterBurst?.let { burst ->
            val next = burst.copy(ageMs = burst.ageMs + deltaMs)
            if (next.ageMs > 1200L) null else next
        }
        val encounter = passingEncounter
        if (encounter != null) {
            val deltaSeconds = deltaMs / 1000f
            val next = encounter.copy(
                x = encounter.x + encounter.vx * deltaSeconds,
                y = encounter.y + sin((encounter.ageMs + deltaMs) / 760f) * encounter.drift * deltaSeconds,
                ageMs = encounter.ageMs + deltaMs,
            )
            val offscreen = if (next.vx > 0f) next.x > width + dp(80f) else next.x < -dp(80f)
            passingEncounter = if (next.ageMs > 26000L || offscreen) {
                encounterSpawnMs = nextEncounterDelayMs()
                null
            } else {
                next
            }
            return
        }
        encounterSpawnMs -= deltaMs
        if (encounterSpawnMs <= 0L) {
            spawnPassingEncounter()
        }
    }

    private fun nextEncounterDelayMs(): Long {
        val seed = ((state.planetIndex * 997 + state.completedPlanetCount * 83 + state.resources.energy.toInt()) % 23_000).toLong()
        return 38_000L + seed
    }

    private fun spawnPassingEncounter() {
        val index = ((state.planetIndex * 5 + state.completedPlanetCount * 3 + (state.resources.aiData + state.resources.biomass).toInt()) % encounterDefs.size)
        val def = encounterDefs[index]
        val fromLeft = (((SystemClock.elapsedRealtime() / 1000L) + state.planetIndex) % 2L) == 0L
        val baseY = dp(124f) + ((index * 79 + state.planetIndex * 31) % 260)
        passingEncounter = PassingEncounter(
            def = def,
            x = if (fromLeft) -dp(70f) else width + dp(70f),
            y = baseY,
            vx = (if (fromLeft) 1f else -1f) * dp(20f + (index % 5) * 4f),
            drift = dp(8f + (index % 4) * 2.2f),
            size = dp(if (def.id == "cargo_drone" || def.id == "warp_merchant") 56f else 47f),
            angle = if (fromLeft) 0.08f else -0.08f,
        )
        encounterSpawnMs = nextEncounterDelayMs()
    }

    private fun drawPassingEncounter(canvas: Canvas) {
        val encounter = passingEncounter ?: return
        val wobble = sin(encounter.ageMs / 420f) * 4.5f
        val size = encounter.size * (1f + sin(encounter.ageMs / 600f) * 0.025f)
        val bounds = RectF(encounter.x - size * 0.5f, encounter.y - size * 0.5f, encounter.x + size * 0.5f, encounter.y + size * 0.5f)
        canvas.save()
        canvas.rotate((encounter.angle * 57.2958f) + wobble, encounter.x, encounter.y)
        drawBitmapFit(canvas, encounter.def.resId, bounds)
        canvas.restore()
        stroke.color = colorWithAlpha(encounterColor(encounter.def.type), (145 + 50f * sin(encounter.ageMs / 230f)).toInt().coerceIn(85, 210))
        stroke.strokeWidth = dp(1.2f)
        canvas.drawCircle(encounter.x, encounter.y, size * 0.48f, stroke)
        val label = RectF(encounter.x - dp(31f), encounter.y + size * 0.36f, encounter.x + dp(31f), encounter.y + size * 0.36f + dp(18f))
        fill.color = Color.argb(190, 17, 24, 42)
        canvas.drawRoundRect(label, dp(7f), dp(7f), fill)
        stroke.color = encounterColor(encounter.def.type)
        stroke.strokeWidth = dp(0.8f)
        canvas.drawRoundRect(label, dp(7f), dp(7f), stroke)
        text.textAlign = Paint.Align.CENTER
        text.color = Color.WHITE
        text.textSize = sp(7.5f)
        canvas.drawText("탭 보상", encounter.x, label.top + dp(12.5f), text)
        text.textAlign = Paint.Align.LEFT
        touchTargets += TouchTarget(RectF(encounter.x - size * 0.58f, encounter.y - size * 0.58f, encounter.x + size * 0.58f, encounter.y + size * 0.58f)) {
            collectPassingEncounter()
        }
    }

    private fun drawEncounterBurst(canvas: Canvas) {
        val burst = encounterBurst ?: return
        val t = (burst.ageMs / 1200f).coerceIn(0f, 1f)
        val alpha = ((1f - t) * 220).toInt().coerceIn(0, 220)
        val size = dp(86f) + dp(130f) * t
        val bounds = RectF(burst.x - size * 0.5f, burst.y - size * 0.5f, burst.x + size * 0.5f, burst.y + size * 0.5f)
        canvas.save()
        canvas.rotate(t * 180f, burst.x, burst.y)
        drawBitmapFit(canvas, burst.resId, bounds, alpha)
        canvas.restore()
        stroke.color = colorWithAlpha(burst.color, (alpha * 0.85f).toInt())
        stroke.strokeWidth = dp(2f)
        canvas.drawCircle(burst.x, burst.y, dp(20f) + t * dp(86f), stroke)
    }

    private fun collectPassingEncounter() {
        val encounter = passingEncounter ?: return
        val reward = encounterReward(encounter.def)
        val progressDelta = if (encounter.def.type == EncounterRewardType.RIFT) {
            GameBalance.planets[state.planetIndex].stageThresholds.last() * 0.025
        } else {
            0.0
        }
        state = engine.grantResources(state, reward, progressDelta = progressDelta)
        startIncomeFloater(reward, encounter.def.title, encounter.x, encounter.y)
        startRewardBurst(encounter.def.title, walletText(reward))
        encounterBurst = EncounterBurst(
            x = encounter.x,
            y = encounter.y,
            resId = encounter.def.burstResId,
            color = encounterColor(encounter.def.type),
        )
        passingEncounter = null
        encounterSpawnMs = nextEncounterDelayMs()
        persistNow()
        invalidate()
    }

    private fun encounterReward(def: EncounterDef): ResourceWallet {
        val production = engine.productionPerSecond(state)
        val planetFactor = (state.planetIndex + 1).toDouble()
        val scaled = production * def.seconds.toDouble()
        return when (def.type) {
            EncounterRewardType.ENERGY -> ResourceWallet(energy = maxOf(90.0 * planetFactor, scaled.energy))
            EncounterRewardType.BIOMASS -> ResourceWallet(biomass = maxOf(8.0 * planetFactor, scaled.biomass + planetFactor * 4.0))
            EncounterRewardType.AI_DATA -> ResourceWallet(aiData = maxOf(4.0 * planetFactor, scaled.aiData + planetFactor * 2.0))
            EncounterRewardType.RARE -> ResourceWallet(
                energy = maxOf(140.0 * planetFactor, scaled.energy * 0.8),
                aiData = maxOf(5.0 * planetFactor, scaled.aiData + planetFactor * 2.0),
            )
            EncounterRewardType.RIFT -> ResourceWallet(
                energy = maxOf(180.0 * planetFactor, scaled.energy),
                biomass = maxOf(10.0 * planetFactor, scaled.biomass + planetFactor * 4.0),
                aiData = maxOf(6.0 * planetFactor, scaled.aiData + planetFactor * 2.0),
            )
            EncounterRewardType.MIXED -> ResourceWallet(
                energy = maxOf(110.0 * planetFactor, scaled.energy * 0.6),
                biomass = maxOf(5.0 * planetFactor, scaled.biomass + planetFactor * 2.0),
                aiData = maxOf(2.0 * planetFactor, scaled.aiData + planetFactor),
            )
        }
    }

    private fun drawPlanetDetails(canvas: Canvas, cx: Float, cy: Float, radius: Float, stage: PlanetStage) {
        stroke.strokeWidth = dp(3f)
        when (stage) {
            PlanetStage.COLLAPSED -> {
                stroke.color = Color.rgb(255, 95, 115)
                for (i in 0 until 7) {
                    val angle = i * 0.9f
                    val sx = cx + cos(angle) * radius * 0.18f
                    val sy = cy + sin(angle) * radius * 0.15f
                    val ex = cx + cos(angle + 0.5f) * radius * 0.82f
                    val ey = cy + sin(angle + 0.4f) * radius * 0.72f
                    canvas.drawLine(sx, sy, ex, ey, stroke)
                }
            }
            PlanetStage.ACTIVATED -> {
                stroke.color = Color.rgb(98, 242, 255)
                canvas.drawCircle(cx, cy, radius * 0.45f, stroke)
                drawArcLine(canvas, cx, cy, radius, -40f, 120f, Color.rgb(98, 242, 255))
                drawArcLine(canvas, cx, cy, radius * 0.7f, 160f, 100f, Color.rgb(255, 215, 90))
            }
            PlanetStage.GROWTH -> {
                fill.color = Color.rgb(114, 245, 128)
                canvas.drawOval(RectF(cx - radius * 0.55f, cy - radius * 0.5f, cx + radius * 0.05f, cy - radius * 0.05f), fill)
                fill.color = Color.rgb(255, 215, 90)
                canvas.drawOval(RectF(cx - radius * 0.1f, cy + radius * 0.05f, cx + radius * 0.62f, cy + radius * 0.48f), fill)
                drawArcLine(canvas, cx, cy, radius, 20f, 220f, Color.rgb(98, 242, 255))
            }
            PlanetStage.COMPLETE -> {
                fill.color = Color.rgb(114, 245, 128)
                canvas.drawOval(RectF(cx - radius * 0.72f, cy - radius * 0.55f, cx + radius * 0.2f, cy + radius * 0.05f), fill)
                fill.color = Color.rgb(255, 215, 90)
                canvas.drawOval(RectF(cx - radius * 0.08f, cy - radius * 0.2f, cx + radius * 0.78f, cy + radius * 0.55f), fill)
                stroke.color = Color.rgb(255, 255, 255)
                stroke.strokeWidth = dp(2f)
                canvas.drawCircle(cx, cy, radius * 0.55f, stroke)
                drawArcLine(canvas, cx, cy, radius * 1.18f, 210f, 90f, Color.rgb(255, 215, 90))
            }
        }
    }

    private fun drawArcLine(canvas: Canvas, cx: Float, cy: Float, radius: Float, start: Float, sweep: Float, color: Int) {
        stroke.color = color
        stroke.strokeWidth = dp(4f)
        canvas.drawArc(RectF(cx - radius, cy - radius, cx + radius, cy + radius), start, sweep, false, stroke)
    }

    private fun drawAiCompanion(canvas: Canvas) {
        val size = dp(70f)
        val left = width - size - dp(16f)
        val top = dp(96f)
        fill.color = Color.argb(210, 18, 28, 48)
        canvas.drawRoundRect(RectF(left, top, left + size, top + size), dp(8f), dp(8f), fill)
        drawBitmapFit(canvas, aiCompanionRes(state.aiAlignment), RectF(left + dp(3f), top - dp(3f), left + size - dp(3f), top + size - dp(8f)))
        text.textSize = sp(10f)
        text.color = Color.WHITE
        text.textAlign = Paint.Align.CENTER
        canvas.drawText(state.aiAlignment.label, left + size * 0.5f, top + size * 0.84f, text)
        text.textAlign = Paint.Align.LEFT
    }

    private fun drawPanel(canvas: Canvas) {
        val top = height * 0.55f
        val bottom = height - dp(86f)
        fill.color = Color.argb(226, 17, 24, 42)
        canvas.drawRoundRect(RectF(dp(10f), top, width - dp(10f), bottom), dp(8f), dp(8f), fill)
        when (currentPanel) {
            Panel.UPGRADE -> drawUpgradePanel(canvas, top, bottom)
            Panel.TRAVEL -> drawTravelPanel(canvas, top, bottom)
            Panel.AI -> drawAiPanel(canvas, top, bottom)
            Panel.EVENT -> drawEventPanel(canvas, top, bottom)
            Panel.SHOP -> drawShopPanel(canvas, top, bottom)
        }
    }

    private fun drawUpgradePanel(canvas: Canvas, top: Float, bottom: Float) {
        drawPanelTitle(canvas, "업그레이드", panelDescription(Panel.UPGRADE), top)
        val start = state.planetIndex * 4
        val upgrades = GameBalance.upgrades.drop(start).take(4)
        val rowH = (bottom - top - dp(60f)) / 4f
        upgrades.forEachIndexed { index, upgrade ->
            val y = top + dp(54f) + index * rowH
            val bounds = RectF(dp(18f), y, width - dp(18f), y + rowH - dp(8f))
            drawButton(canvas, bounds, false)
            val iconSize = minOf(dp(38f), bounds.height() - dp(8f))
            val iconBounds = RectF(
                bounds.left + dp(6f),
                bounds.top + (bounds.height() - iconSize) * 0.5f,
                bounds.left + dp(6f) + iconSize,
                bounds.top + (bounds.height() - iconSize) * 0.5f + iconSize,
            )
            drawBitmapFit(canvas, upgradeIconRes(upgrade), iconBounds)
            val level = state.upgrades[upgrade.id] ?: 0
            val cost = engine.costFor(upgrade, level)
            val textLeft = iconBounds.right + dp(8f)
            text.color = Color.WHITE
            text.textSize = sp(11.5f)
            canvas.drawText("${upgrade.name} Lv.$level/${upgrade.maxLevel}", textLeft, bounds.top + dp(19f), text)
            text.color = Color.rgb(210, 232, 240)
            text.textSize = sp(9.5f)
            canvas.drawText("현재: ${upgradeEffectLine(upgrade, level)}", textLeft, bounds.top + dp(35f), text)
            text.color = Color.rgb(255, 215, 90)
            canvas.drawText(upgradeNextDeltaLine(upgrade, level), textLeft, bounds.top + dp(50f), text)
            text.color = Color.rgb(160, 246, 255)
            text.textSize = sp(9.5f)
            text.textAlign = Paint.Align.RIGHT
            canvas.drawText("비용 ${walletText(cost)}", bounds.right - dp(8f), bounds.top + dp(19f), text)
            text.textAlign = Paint.Align.LEFT
            touchTargets += TouchTarget(bounds) {
                val before = state
                state = engine.purchaseUpgrade(state, upgrade.id)
                if (before != state) {
                    startUpgradeBurst(upgrade.name, (state.upgrades[upgrade.id] ?: 0), bounds, upgrade.effect)
                }
                showChangeToast(before != state, "업그레이드 완료", "자원이 부족합니다")
            }
        }
    }

    private fun drawTravelPanel(canvas: Canvas, top: Float, bottom: Float) {
        drawPanelTitle(canvas, "은하 지도", panelDescription(Panel.TRAVEL), top)
        val planet = GameBalance.planets[state.planetIndex]
        val next = GameBalance.planets.getOrNull(state.planetIndex + 1)
        val maxUnlocked = engine.maxUnlockedPlanetIndex(state)
        val nextUnlocked = next != null && state.planetIndex + 1 <= maxUnlocked
        val stageReady = state.stage() == PlanetStage.COMPLETE
        val costReady = state.resources.canAfford(planet.travelCost)
        val timeReady = engine.travelTimeReady(state)
        val waitSeconds = engine.travelWaitRemainingSeconds(state)
        val canAct = engine.canTravel(state) || engine.canPrestige(state)
        drawBodyText(canvas, planet.theme, top + dp(58f))

        val statusBounds = RectF(dp(22f), top + dp(94f), width - dp(22f), top + dp(182f))
        drawButton(canvas, statusBounds, canAct)
        text.color = if (canAct) Color.rgb(114, 245, 128) else Color.rgb(255, 215, 90)
        text.textSize = sp(10.5f)
        val statusLabel = when {
            nextUnlocked -> "재방문 항로 개방"
            canAct -> "이동 가능"
            else -> "이동 조건"
        }
        canvas.drawText(statusLabel, statusBounds.left + dp(10f), statusBounds.top + dp(17f), text)
        text.color = if (stageReady) Color.rgb(114, 245, 128) else Color.rgb(210, 232, 240)
        text.textSize = sp(8.8f)
        canvas.drawText("조건 1: 행성 완성 ${if (stageReady) "완료" else "${state.stage().label} ${progressPercent()}%"}", statusBounds.left + dp(10f), statusBounds.top + dp(35f), text)
        text.color = if (next == null || nextUnlocked || costReady) Color.rgb(114, 245, 128) else Color.rgb(210, 232, 240)
        val costLine = when {
            next == null -> "조건 2: 최종 행성 완성 후 프레스티지"
            nextUnlocked -> "조건 2: 이미 개척한 항로 / 비용 없음"
            else -> "조건 2: 이동 비용 ${walletText(planet.travelCost)}"
        }
        canvas.drawText(costLine.take(46), statusBounds.left + dp(10f), statusBounds.top + dp(51f), text)
        text.color = if (next == null || nextUnlocked || costReady) Color.rgb(160, 246, 255) else Color.rgb(255, 154, 168)
        val subLine = when {
            next == null -> if (stageReady) "보상: 코어 +1, 일부 성향 유지" else "남은 복원도 ${formatNumber(GameBalance.planets[state.planetIndex].stageThresholds.last() - state.stageProgress)}"
            nextUnlocked -> "지도 노드를 탭하면 완료 행성을 다시 관리합니다."
            costReady -> "보유 충분: ${walletText(state.resources)}"
            else -> "부족: ${shortageText(planet.travelCost)}"
        }
        canvas.drawText(subLine.take(48), statusBounds.left + dp(10f), statusBounds.top + dp(66f), text)
        text.color = when {
            next == null || nextUnlocked || timeReady -> Color.rgb(114, 245, 128)
            else -> Color.rgb(255, 154, 168)
        }
        val timeLine = when {
            next == null -> "조건 3: 최종 코어는 안정화 조건 없음"
            nextUnlocked -> "조건 3: 재방문 항로 즉시 사용 가능"
            timeReady -> "조건 3: 항로 안정화 완료"
            else -> "조건 3: 항로 안정화 남은 ${formatDuration(waitSeconds)}"
        }
        canvas.drawText(timeLine.take(48), statusBounds.left + dp(10f), statusBounds.top + dp(81f), text)

        drawGalaxyNodes(canvas, top + dp(198f), bottom - dp(68f))
        val bounds = RectF(dp(24f), bottom - dp(58f), width - dp(24f), bottom - dp(14f))
        drawButton(canvas, bounds, canAct)
        text.textSize = sp(13f)
        text.color = Color.WHITE
        val label = if (next == null) {
            if (engine.canPrestige(state)) "프레스티지: 은하를 재시작하고 코어 획득" else "최종 행성을 완성하면 프레스티지 가능"
        } else if (nextUnlocked) {
            "재방문: ${next.name}"
        } else if (canAct) {
            "워프 가능: ${next.name}"
        } else {
            "다음 행성: ${next.name}"
        }
        canvas.drawText(label, bounds.left + dp(12f), bounds.top + dp(28f), text)
        touchTargets += TouchTarget(bounds) {
            val before = state
            state = if (engine.canPrestige(state)) engine.prestige(state) else engine.travelToNextPlanet(state)
            if (before != state) {
                centerTravelMapOnCurrent()
                startScreenTransition(if (next == null) "은하 재기동" else "${GameBalance.planets[state.planetIndex].name} 도착", panelAccent(Panel.TRAVEL))
            }
            showChangeToast(before != state, if (next == null) "프레스티지 완료" else "워프 완료", "행성 완성, 이동 비용, 7일 안정화가 필요합니다")
        }
    }

    private fun travelMapMetrics(): TravelMapMetrics {
        val viewportWidth = (width - dp(48f)).coerceAtLeast(dp(240f))
        val spacing = viewportWidth / 3.8f
        val sidePad = spacing * 0.52f
        val contentWidth = sidePad * 2f + spacing * (GameBalance.planets.size - 1).coerceAtLeast(0)
        val maxScroll = (contentWidth - viewportWidth).coerceAtLeast(0f)
        return TravelMapMetrics(viewportWidth, spacing, sidePad, contentWidth, maxScroll)
    }

    private fun clampTravelMapScroll(value: Float = travelMapScrollPx): Float {
        val maxScroll = travelMapMetrics().maxScroll
        return value.coerceIn(0f, maxScroll).also { travelMapScrollPx = it }
    }

    private fun centerTravelMapOnCurrent() {
        if (width <= 0) return
        val metrics = travelMapMetrics()
        val target = metrics.sidePad + metrics.spacing * state.planetIndex - metrics.viewportWidth * 0.5f
        travelMapScrollPx = target.coerceIn(0f, metrics.maxScroll)
    }

    private fun drawGalaxyNodes(canvas: Canvas, top: Float, bottom: Float) {
        val count = GameBalance.planets.size
        if (count <= 0 || bottom <= top) return
        val maxUnlocked = engine.maxUnlockedPlanetIndex(state)
        val areaHeight = bottom - top
        val metrics = travelMapMetrics()
        travelMapScrollPx = clampTravelMapScroll()
        val viewport = RectF(dp(24f), top + dp(14f), width - dp(24f), bottom - dp(20f))
        travelMapBounds = RectF(viewport)
        val centerY = top + areaHeight * 0.5f
        var previousX = 0f
        var previousY = centerY
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(8.5f)
        canvas.drawText("미니맵 ${state.planetIndex + 1}/${GameBalance.planets.size}", dp(24f), top + dp(10f), text)
        text.textAlign = Paint.Align.RIGHT
        text.color = Color.rgb(255, 215, 90)
        canvas.drawText("좌우 드래그", width - dp(24f), top + dp(10f), text)
        text.textAlign = Paint.Align.LEFT
        fill.color = Color.argb(92, 8, 14, 27)
        canvas.drawRoundRect(viewport, dp(8f), dp(8f), fill)
        stroke.color = Color.argb(70, 98, 242, 255)
        stroke.strokeWidth = dp(1f)
        canvas.drawRoundRect(viewport, dp(8f), dp(8f), stroke)
        canvas.save()
        canvas.clipRect(viewport)
        for (index in 0 until count) {
            val x = viewport.left + metrics.sidePad + metrics.spacing * index - travelMapScrollPx
            val y = centerY + if (index % 2 == 0) -dp(13f) else dp(13f)
            if (index > 0) {
                stroke.color = Color.argb(150, 98, 242, 255)
                stroke.strokeWidth = dp(2.2f)
                canvas.drawLine(previousX, previousY, x, y, stroke)
            }
            val unlocked = index <= maxUnlocked
            val resId = when {
                index == state.planetIndex -> R.drawable.map_node_current_planet
                unlocked -> R.drawable.map_node_complete_planet
                else -> R.drawable.map_node_locked_planet
            }
            val nodeSize = if (index == state.planetIndex) {
                minOf(dp(58f), areaHeight * 0.7f)
            } else {
                minOf(dp(40f), areaHeight * 0.54f)
            }
            val nodeBounds = RectF(x - nodeSize * 0.5f, y - nodeSize * 0.5f, x + nodeSize * 0.5f, y + nodeSize * 0.5f)
            if (index == state.planetIndex) {
                val pulse = ((SystemClock.elapsedRealtime() % 1300L).toFloat() / 1300f)
                stroke.color = Color.argb((220 - pulse * 110).toInt(), 255, 215, 90)
                stroke.strokeWidth = dp(2.4f)
                canvas.drawCircle(x, y, nodeSize * (0.76f + pulse * 0.38f), stroke)
                text.textAlign = Paint.Align.CENTER
                text.color = Color.rgb(255, 215, 90)
                text.textSize = sp(8.6f)
                canvas.drawText("현재", x, y - nodeSize * 0.78f, text)
                text.textAlign = Paint.Align.LEFT
            }
            drawBitmapFit(canvas, resId, nodeBounds, alpha = if (unlocked) 255 else 145)
            if (unlocked && nodeBounds.right >= viewport.left && nodeBounds.left <= viewport.right) {
                touchTargets += TouchTarget(
                    RectF(nodeBounds.left - dp(8f), nodeBounds.top - dp(8f), nodeBounds.right + dp(8f), nodeBounds.bottom + dp(8f)),
                ) {
                    visitPlanet(index)
                }
            }
            previousX = x
            previousY = y
        }
        canvas.restore()
        val thumbWidth = maxOf(dp(34f), metrics.viewportWidth * (metrics.viewportWidth / metrics.contentWidth))
        val thumbX = viewport.left + (viewport.width() - thumbWidth) * (travelMapScrollPx / maxOf(1f, metrics.maxScroll))
        val track = RectF(viewport.left, viewport.bottom + dp(4f), viewport.right, viewport.bottom + dp(9f))
        fill.color = Color.argb(130, 17, 24, 42)
        canvas.drawRoundRect(track, dp(3f), dp(3f), fill)
        fill.color = Color.rgb(98, 242, 255)
        canvas.drawRoundRect(RectF(thumbX, track.top - dp(1f), thumbX + thumbWidth, track.bottom + dp(1f)), dp(4f), dp(4f), fill)
    }

    private fun drawAiPanel(canvas: Canvas, top: Float, bottom: Float) {
        drawPanelTitle(canvas, "AI 진화", panelDescription(Panel.AI), top)
        val hasChosen = state.planetIndex in state.selectedEvolutionPlanets
        if (hasChosen) {
            drawBodyText(canvas, "이 행성의 지시는 확정되었습니다. 현재 성향: ${state.aiAlignment.label}.", top + dp(64f))
            return
        }
        val rowH = (bottom - top - dp(68f)) / 3f
        GameBalance.evolutions.forEachIndexed { index, evolution ->
            val y = top + dp(58f) + index * rowH
            val bounds = RectF(dp(22f), y, width - dp(22f), y + rowH - dp(8f))
            drawButton(canvas, bounds, true)
            text.color = Color.WHITE
            text.textSize = sp(12f)
            canvas.drawText(evolution.title, bounds.left + dp(10f), bounds.top + dp(18f), text)
            text.color = Color.rgb(160, 246, 255)
            canvas.drawText(evolution.rewardText, bounds.left + dp(10f), bounds.top + dp(38f), text)
            touchTargets += TouchTarget(bounds) {
                val before = state
                state = engine.chooseEvolution(state, evolution.alignment)
                if (before != state) {
                    startScreenTransition("${evolution.alignment.label} 프로토콜", panelAccent(Panel.AI))
                }
                Toast.makeText(context, "${evolution.alignment.label} 프로토콜 선택", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawEventPanel(canvas: Canvas, top: Float, bottom: Float) {
        drawPanelTitle(canvas, "랜덤 이벤트", panelDescription(Panel.EVENT), top)
        val index = state.activeEventIndex
        if (index == null) {
            drawBodyText(canvas, "활성 이상 현상이 없습니다. 다음 짧은 세션에서 스캔하세요.", top + dp(64f))
            val bounds = RectF(dp(24f), bottom - dp(58f), width - dp(24f), bottom - dp(14f))
            drawButton(canvas, bounds, true)
            text.color = Color.WHITE
            text.textSize = sp(13f)
            canvas.drawText("이상 현상 스캔", bounds.left + dp(12f), bounds.top + dp(28f), text)
            touchTargets += TouchTarget(bounds) {
                state = engine.openRandomEvent(state)
                startScreenTransition("이상 현상 스캔", panelAccent(Panel.EVENT))
            }
            return
        }

        val event = GameBalance.randomEvents[index]
        val imageBounds = RectF(width - dp(134f), top + dp(58f), width - dp(24f), top + dp(138f))
        drawBitmapFit(canvas, eventImageRes(event.id), imageBounds, alpha = 185)
        drawBodyText(canvas, "${event.title}: ${event.body}", top + dp(64f))
        val rowH = dp(46f)
        event.options.forEachIndexed { optionIndex, option ->
            val y = bottom - dp(18f) - (event.options.size - optionIndex) * (rowH + dp(8f))
            val bounds = RectF(dp(24f), y, width - dp(24f), y + rowH)
            drawButton(canvas, bounds, true)
            text.color = Color.WHITE
            text.textSize = sp(12f)
            canvas.drawText(option.label, bounds.left + dp(12f), bounds.top + dp(28f), text)
            touchTargets += TouchTarget(bounds) {
                val before = state
                state = engine.resolveEvent(state, optionIndex)
                if (before != state) {
                    startScreenTransition("이벤트 결과 적용", panelAccent(Panel.EVENT))
                }
                Toast.makeText(context, option.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawShopPanel(canvas: Canvas, top: Float, bottom: Float) {
        drawPanelTitle(canvas, "상점", panelDescription(Panel.SHOP), top)
        var y = top + dp(58f)
        val rowH = dp(42f)
        val gap = dp(6f)

        pendingOfflineDoubleReward?.let {
            drawRewardRow(
                canvas = canvas,
                y = y,
                label = "광고: 오프라인 보상 2배",
                placement = RewardedPlacement.OFFLINE_DOUBLE,
                detail = "방금 받은 오프라인 보상 ${walletText(it)} 추가 지급",
            ) {
                state = engine.grantResources(state, it)
                pendingOfflineDoubleReward = null
                persistNow()
                startRewardBurst("오프라인 2배", walletText(it))
                startScreenTransition("오프라인 보상 증폭", panelAccent(Panel.SHOP))
                Toast.makeText(context, "오프라인 보상 2배 지급", Toast.LENGTH_SHORT).show()
            }
            y += rowH + gap
        }

        drawRewardRow(canvas, y, "광고: 10분간 생산 2배", RewardedPlacement.PRODUCTION_BOOST, "가격: 광고 시청 / 보상: 생산 2배 10분") {
            state = engine.applyRewardedAdBoost(state)
            persistNow()
            startScreenTransition("생산 부스트 점화", panelAccent(Panel.SHOP))
            Toast.makeText(context, "생산 부스트 활성화", Toast.LENGTH_SHORT).show()
        }
        y += rowH + gap
        drawRewardRow(canvas, y, "광고: 행성 복원 +8%", RewardedPlacement.RESTORE_SPEED, "가격: 광고 시청 / 보상: 즉시 복원 +8%") {
            state = engine.applyRewardedRestoreBurst(state)
            persistNow()
            startScreenTransition("복원 파동 적용", panelAccent(Panel.SHOP))
            Toast.makeText(context, "복원 가속 적용", Toast.LENGTH_SHORT).show()
        }
        y += rowH + gap
        drawRewardRow(canvas, y, "광고: 다음 이벤트 보상 2배", RewardedPlacement.EVENT_REWARD, "가격: 광고 시청 / 보상: 다음 이벤트 보상 2배") {
            state = engine.applyRewardedEventBoost(state)
            persistNow()
            startScreenTransition("이벤트 증폭 저장", panelAccent(Panel.SHOP))
            Toast.makeText(context, "다음 이벤트 보상 강화", Toast.LENGTH_SHORT).show()
        }
        y += rowH + gap
        val chestReward = freeChestReward()
        drawRewardRow(canvas, y, "광고: 무료 자원 상자", RewardedPlacement.FREE_CHEST, "가격: 광고 시청 / 보상: ${walletText(chestReward)}") {
            val reward = freeChestReward()
            state = engine.grantResources(state, reward, progressDelta = 30.0)
            persistNow()
            startRewardBurst("무료 상자 획득", walletText(reward))
            startScreenTransition("보상 상자 개방", panelAccent(Panel.SHOP))
            Toast.makeText(context, "무료 상자 획득", Toast.LENGTH_SHORT).show()
        }

        y += rowH + dp(12f)
        text.color = Color.WHITE
        text.textSize = sp(12f)
        canvas.drawText("인앱 상품", dp(24f), y + dp(16f), text)
        y += dp(24f)

        billingController.products.forEach { product ->
            if (y + rowH > bottom - dp(36f)) return@forEach
            drawProductRow(canvas, y, product)
            y += rowH + gap
        }

        val restoreBounds = RectF(dp(24f), bottom - dp(34f), width - dp(24f), bottom - dp(6f))
        drawButton(canvas, restoreBounds, billingController.isPurchaseAvailable())
        text.color = Color.WHITE
        text.textSize = sp(11f)
        canvas.drawText("구매 복원", restoreBounds.left + dp(10f), restoreBounds.top + dp(19f), text)
        touchTargets += TouchTarget(restoreBounds) {
            billingController.restorePurchases()
        }
    }

    private fun drawRewardRow(
        canvas: Canvas,
        y: Float,
        label: String,
        placement: RewardedPlacement,
        detail: String,
        onReward: () -> Unit,
    ) {
        val bounds = RectF(dp(24f), y, width - dp(24f), y + dp(42f))
        drawButton(canvas, bounds, true)
        val iconBounds = RectF(bounds.left + dp(6f), bounds.top + dp(7f), bounds.left + dp(32f), bounds.top + dp(33f))
        drawBitmapFit(canvas, rewardIconRes(placement), iconBounds)
        text.color = Color.WHITE
        text.textSize = sp(10.5f)
        canvas.drawText(label, iconBounds.right + dp(8f), bounds.top + dp(16f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(8.4f)
        canvas.drawText(detail.take(50), iconBounds.right + dp(8f), bounds.top + dp(32f), text)
        val status = if (adsController.isReady(placement)) "준비" else "로딩"
        text.textAlign = Paint.Align.RIGHT
        canvas.drawText(status, bounds.right - dp(8f), bounds.top + dp(21f), text)
        text.textAlign = Paint.Align.LEFT
        touchTargets += TouchTarget(bounds) {
            adsController.showRewarded(
                placement = placement,
                onReward = onReward,
                onUnavailable = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() },
            )
        }
    }

    private fun drawProductRow(canvas: Canvas, y: Float, product: StoreProduct) {
        val owned = billingController.hasEntitlement(product)
        val bounds = RectF(dp(24f), y, width - dp(24f), y + dp(42f))
        drawButton(canvas, bounds, !owned)
        val iconBounds = RectF(bounds.left + dp(6f), bounds.top + dp(7f), bounds.left + dp(32f), bounds.top + dp(33f))
        drawBitmapFit(canvas, productIconRes(product), iconBounds)
        text.color = if (owned) Color.rgb(175, 190, 198) else Color.WHITE
        text.textSize = sp(10f)
        canvas.drawText(product.title, iconBounds.right + dp(8f), bounds.top + dp(14f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(8.4f)
        canvas.drawText(productRewardText(product).take(48), iconBounds.right + dp(8f), bounds.top + dp(31f), text)
        text.color = if (owned) Color.rgb(114, 245, 128) else Color.rgb(255, 215, 90)
        text.textSize = sp(10f)
        val price = if (owned) "보유" else billingController.priceLabel(product)
        text.textAlign = Paint.Align.RIGHT
        canvas.drawText(price, bounds.right - dp(8f), bounds.top + dp(21f), text)
        text.textAlign = Paint.Align.LEFT
        touchTargets += TouchTarget(bounds) {
            billingController.purchase(product)
        }
    }

    private fun applyPurchasedProduct(product: StoreProduct) {
        val before = state
        state = engine.grantResources(
            state = state,
            reward = product.reward,
            progressDelta = product.progressReward,
            boostSeconds = product.productionBoostSeconds,
        )
        persistNow()
        val message = if (before == state) {
            "${product.title} 해금"
        } else {
            "${product.title} 지급 완료"
        }
        startRewardBurst(product.title, productRewardText(product))
        startScreenTransition("상품 보상 지급", panelAccent(Panel.SHOP))
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        invalidate()
    }

    private fun startRewardBurst(title: String, detail: String) {
        rewardBurst = RewardBurst(
            title = title,
            detail = detail,
            startedMs = SystemClock.elapsedRealtime(),
        )
    }

    private fun startUpgradeBurst(title: String, level: Int, bounds: RectF, effect: UpgradeEffect) {
        upgradeBurst = UpgradeBurst(
            title = title,
            level = level,
            bounds = RectF(bounds),
            effect = effect,
            startedMs = SystemClock.elapsedRealtime(),
        )
    }

    private fun startScreenTransition(label: String, accent: Int) {
        screenTransition = ScreenTransition(
            label = label,
            accent = accent,
            startedMs = SystemClock.elapsedRealtime(),
        )
    }

    private fun visitPlanet(index: Int) {
        if (!engine.canVisitPlanet(state, index)) {
            Toast.makeText(context, "아직 잠긴 행성입니다", Toast.LENGTH_SHORT).show()
            return
        }
        val before = state
        state = engine.visitPlanet(state, index)
        if (before != state) {
            centerTravelMapOnCurrent()
            startScreenTransition("${GameBalance.planets[index].name} 재방문", panelAccent(Panel.TRAVEL))
            Toast.makeText(context, "${GameBalance.planets[index].name} 재방문", Toast.LENGTH_SHORT).show()
            persistNow()
        }
    }

    private fun tickIncomeFloaters(deltaMs: Long, production: ResourceWallet) {
        val now = SystemClock.elapsedRealtime()
        incomeFloaters.removeAll { now - it.startedMs > 1550L }
        if (screenMode != ScreenMode.GAME || width <= 0 || height <= 0) return

        incomeFxElapsedMs += deltaMs
        if (incomeFxElapsedMs < 720L) return
        val gained = production * (incomeFxElapsedMs.toDouble() / 1000.0)
        incomeFxElapsedMs = 0L
        startIncomeFloater(gained, "생산")
    }

    private fun startIncomeFloater(wallet: ResourceWallet, source: String, x: Float? = null, y: Float? = null) {
        val entries = mutableListOf<WalletFxPart>()
        if (wallet.energy > 0.001) entries += WalletFxPart("E", wallet.energy, Color.rgb(98, 242, 255))
        if (wallet.biomass > 0.001) entries += WalletFxPart("B", wallet.biomass, Color.rgb(114, 245, 128))
        if (wallet.aiData > 0.001) entries += WalletFxPart("AI", wallet.aiData, Color.rgb(255, 215, 90))
        if (entries.isEmpty()) return

        val part = entries[incomeFxIndex % entries.size]
        incomeFxIndex += 1
        val offset = ((incomeFxIndex % 5) - 2) * dp(18f)
        val originX = x ?: (width * 0.5f + offset)
        val originY = y ?: (height * 0.43f + ((incomeFxIndex % 3) - 1) * dp(14f))
        incomeFloaters += IncomeFloater(
            label = "+${formatNumber(part.amount)} ${part.label}",
            source = source,
            color = part.color,
            x = originX,
            y = originY,
            startedMs = SystemClock.elapsedRealtime(),
        )
        while (incomeFloaters.size > 10) incomeFloaters.removeAt(0)
    }

    private fun drawIncomeFloaters(canvas: Canvas) {
        val now = SystemClock.elapsedRealtime()
        val iterator = incomeFloaters.iterator()
        while (iterator.hasNext()) {
            val floater = iterator.next()
            val elapsed = now - floater.startedMs
            val life = 1550L
            if (elapsed > life) {
                iterator.remove()
                continue
            }
            val t = elapsed.toFloat() / life.toFloat()
            val alpha = ((1f - t) * 225).toInt().coerceIn(0, 225)
            val wobble = sin(t * Math.PI.toFloat() * 2.2f + floater.x * 0.013f) * dp(8f)
            val cx = floater.x + wobble
            val cy = floater.y - dp(62f) * t

            text.textAlign = Paint.Align.CENTER
            text.textSize = sp(11f)
            val w = maxOf(text.measureText(floater.label), text.measureText(floater.source)) + dp(34f)
            val h = dp(35f)
            val box = RectF(cx - w * 0.5f, cy - h * 0.5f, cx + w * 0.5f, cy + h * 0.5f)
            fill.color = Color.argb((180 * (1f - t * 0.45f)).toInt().coerceIn(0, 180), 17, 24, 42)
            canvas.drawRoundRect(box, dp(8f), dp(8f), fill)
            stroke.color = colorWithAlpha(floater.color, alpha)
            stroke.strokeWidth = dp(1.2f)
            canvas.drawRoundRect(box, dp(8f), dp(8f), stroke)

            text.color = colorWithAlpha(floater.color, alpha)
            text.textSize = sp(11.5f)
            canvas.drawText(floater.label, cx, box.top + dp(15f), text)
            text.color = Color.argb((alpha * 0.82f).toInt().coerceIn(0, 210), 210, 232, 240)
            text.textSize = sp(8.2f)
            canvas.drawText(floater.source, cx, box.top + dp(28f), text)
            text.textAlign = Paint.Align.LEFT
        }
    }

    private fun drawRewardBurst(canvas: Canvas) {
        val burst = rewardBurst ?: return
        val elapsed = SystemClock.elapsedRealtime() - burst.startedMs
        val life = 1650L
        if (elapsed > life) {
            rewardBurst = null
            return
        }
        val t = elapsed.toFloat() / life.toFloat()
        val cx = width * 0.5f
        val cy = height * 0.46f
        for (i in 0 until 28) {
            val angle = (i / 28f) * Math.PI.toFloat() * 2f
            val dist = dp(42f + (i % 7) * 6f) * sin(t * Math.PI.toFloat() * 0.82f)
            val x = cx + cos(angle) * dist
            val y = cy + sin(angle) * dist * 0.72f - dp(18f) * t
            val alpha = ((1f - t) * 230).toInt().coerceIn(0, 230)
            fill.color = when (i % 3) {
                0 -> Color.argb(alpha, 255, 215, 90)
                1 -> Color.argb(alpha, 98, 242, 255)
                else -> Color.argb(alpha, 114, 245, 128)
            }
            canvas.drawCircle(x, y, dp(2.4f + (i % 4)), fill)
        }

        val panelAlpha = if (t < 0.82f) 235 else ((1f - t) / 0.18f * 235f).toInt().coerceIn(0, 235)
        val box = RectF(cx - dp(118f), cy - dp(44f) - dp(18f) * t, cx + dp(118f), cy + dp(18f) - dp(18f) * t)
        fill.color = Color.argb(panelAlpha, 17, 24, 42)
        canvas.drawRoundRect(box, dp(10f), dp(10f), fill)
        stroke.color = Color.argb(panelAlpha, 255, 215, 90)
        stroke.strokeWidth = dp(1.5f)
        canvas.drawRoundRect(box, dp(10f), dp(10f), stroke)
        text.textAlign = Paint.Align.CENTER
        text.color = Color.WHITE
        text.textSize = sp(13f)
        canvas.drawText(burst.title, cx, box.top + dp(24f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(9.5f)
        canvas.drawText(burst.detail.take(34), cx, box.top + dp(43f), text)
        text.textAlign = Paint.Align.LEFT
    }

    private fun drawUpgradeBurst(canvas: Canvas) {
        val burst = upgradeBurst ?: return
        val elapsed = SystemClock.elapsedRealtime() - burst.startedMs
        val life = 1800L
        if (elapsed > life) {
            upgradeBurst = null
            return
        }
        val t = elapsed.toFloat() / life.toFloat()
        val heldAlpha = if (t < 0.74f) 1f else ((1f - t) / 0.26f).coerceIn(0f, 1f)
        val alpha = (heldAlpha * 235).toInt().coerceIn(0, 235)
        val cx = burst.bounds.centerX()
        val cy = burst.bounds.centerY()
        fill.color = Color.argb((42 * (1f - t)).toInt().coerceIn(0, 42), 98, 242, 255)
        canvas.drawRoundRect(
            RectF(burst.bounds.left - dp(3f), burst.bounds.top - dp(3f), burst.bounds.right + dp(3f), burst.bounds.bottom + dp(3f)),
            dp(8f),
            dp(8f),
            fill,
        )
        stroke.color = Color.argb(alpha, 255, 215, 90)
        stroke.strokeWidth = dp(2.2f)
        canvas.drawRoundRect(
            RectF(burst.bounds.left - dp(3f), burst.bounds.top - dp(3f), burst.bounds.right + dp(3f), burst.bounds.bottom + dp(3f)),
            dp(8f),
            dp(8f),
            stroke,
        )
        stroke.color = Color.argb(alpha, 98, 242, 255)
        stroke.strokeWidth = dp(2.5f)
        canvas.drawCircle(cx, cy, dp(16f) + dp(78f) * t, stroke)
        for (i in 0 until 26) {
            val angle = -Math.PI.toFloat() * 0.9f + (i / 25f) * Math.PI.toFloat() * 0.8f
            val dist = dp(34f + (i % 6) * 5f) * sin(t * Math.PI.toFloat() * 0.84f)
            val x = cx + cos(angle) * dist
            val y = cy + sin(angle) * dist - dp(18f) * t
            fill.color = when (i % 3) {
                0 -> Color.argb(alpha, 255, 215, 90)
                1 -> Color.argb(alpha, 98, 242, 255)
                else -> Color.argb(alpha, 114, 245, 128)
            }
            canvas.drawCircle(x, y, dp(1.8f + (i % 4) * 0.45f), fill)
        }
        val label = RectF(cx - dp(82f), cy - dp(17f) - dp(10f) * t, cx + dp(82f), cy + dp(17f) - dp(10f) * t)
        fill.color = Color.argb((225 * heldAlpha).toInt().coerceIn(0, 225), 17, 24, 42)
        canvas.drawRoundRect(label, dp(8f), dp(8f), fill)
        stroke.color = Color.argb(alpha, 255, 215, 90)
        stroke.strokeWidth = dp(1.4f)
        canvas.drawRoundRect(label, dp(8f), dp(8f), stroke)
        text.textAlign = Paint.Align.CENTER
        text.color = Color.WHITE
        text.textSize = sp(10.5f)
        canvas.drawText("LEVEL UP  Lv.${burst.level}", cx, label.top + dp(21f), text)
        text.textAlign = Paint.Align.LEFT
    }

    private fun drawScreenTransition(canvas: Canvas) {
        val transition = screenTransition ?: return
        val elapsed = SystemClock.elapsedRealtime() - transition.startedMs
        val life = 900L
        if (elapsed > life) {
            screenTransition = null
            return
        }
        val t = elapsed.toFloat() / life.toFloat()
        val cx = width * 0.5f
        val cy = height * 0.5f
        fill.color = Color.argb(((if (t < 0.5f) 150f * (1f - t * 0.8f) else 70f * (1f - t))).toInt().coerceIn(0, 150), 2, 6, 14)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fill)
        for (i in 0 until 24) {
            val angle = (i / 24f) * Math.PI.toFloat() * 2f + t * 1.8f
            val inner = dp(36f) + t * dp(110f)
            val outer = dp(260f) + t * dp(220f)
            stroke.color = colorWithAlpha(transition.accent, ((1f - t) * 145).toInt().coerceIn(0, 145))
            stroke.strokeWidth = dp(1f + (i % 4) * 0.35f)
            canvas.drawLine(
                cx + cos(angle) * inner,
                cy + sin(angle) * inner * 0.72f,
                cx + cos(angle) * outer,
                cy + sin(angle) * outer * 0.72f,
                stroke,
            )
        }
        stroke.color = colorWithAlpha(transition.accent, ((1f - t) * 210).toInt().coerceIn(0, 210))
        stroke.strokeWidth = dp(3f)
        canvas.drawCircle(cx, cy, dp(54f) + t * dp(250f), stroke)
        if (t < 0.72f) {
            val box = RectF(cx - dp(118f), cy - dp(28f), cx + dp(118f), cy + dp(28f))
            fill.color = Color.argb(230, 17, 24, 42)
            canvas.drawRoundRect(box, dp(9f), dp(9f), fill)
            stroke.color = transition.accent
            stroke.strokeWidth = dp(1.5f)
            canvas.drawRoundRect(box, dp(9f), dp(9f), stroke)
            text.textAlign = Paint.Align.CENTER
            text.color = Color.WHITE
            text.textSize = sp(12.5f)
            canvas.drawText(transition.label, cx, cy + dp(4f), text)
            text.textAlign = Paint.Align.LEFT
        }
    }

    private fun freeChestReward(): ResourceWallet {
        val production = engine.productionPerSecond(state)
        val planetFactor = (state.planetIndex + 1).toDouble()
        return production * 180.0 + ResourceWallet(
            energy = 150.0 * planetFactor,
            biomass = 6.0 * planetFactor,
            aiData = 2.0 * planetFactor,
        )
    }

    private fun walletText(wallet: ResourceWallet): String {
        val parts = mutableListOf<String>()
        if (wallet.energy > 0.0) parts += "E ${formatNumber(wallet.energy)}"
        if (wallet.biomass > 0.0) parts += "B ${formatNumber(wallet.biomass)}"
        if (wallet.aiData > 0.0) parts += "AI ${formatNumber(wallet.aiData)}"
        return if (parts.isEmpty()) "외형/편의 해금" else parts.joinToString(" ")
    }

    private fun shortageText(cost: ResourceWallet): String {
        return walletText(
            ResourceWallet(
                energy = maxOf(0.0, cost.energy - state.resources.energy),
                biomass = maxOf(0.0, cost.biomass - state.resources.biomass),
                aiData = maxOf(0.0, cost.aiData - state.resources.aiData),
            ),
        )
    }

    private fun productRewardText(product: StoreProduct): String {
        val parts = mutableListOf<String>()
        val wallet = walletText(product.reward)
        if (wallet != "외형/편의 해금") parts += wallet
        if (product.progressReward > 0.0) parts += "복원 +${formatNumber(product.progressReward)}"
        if (product.productionBoostSeconds > 0L) parts += "생산 2배 ${product.productionBoostSeconds / 60}분"
        if (parts.isEmpty()) parts += "외형/편의 해금"
        return parts.joinToString(" ")
    }

    private fun upgradeEffectLine(upgrade: UpgradeDef, level: Int): String {
        val total = level * upgrade.valuePerLevel
        val percent = (total * 100.0).roundToInt()
        val label = when (upgrade.effect) {
            UpgradeEffect.ENERGY_PRODUCTION -> "에너지 생산"
            UpgradeEffect.BIOMASS_PRODUCTION -> "바이오매스 생산"
            UpgradeEffect.AI_DATA_PRODUCTION -> "AI 데이터 생산"
            UpgradeEffect.TAP_POWER -> "터치 보상"
            UpgradeEffect.RESTORE_SPEED -> "복원 속도"
            UpgradeEffect.OFFLINE_GAIN -> "오프라인 보상률"
        }
        val suffix = if (upgrade.effect == UpgradeEffect.OFFLINE_GAIN) "%p" else "%"
        return "$label +$percent$suffix"
    }

    private fun upgradeNextDeltaLine(upgrade: UpgradeDef, level: Int): String {
        if (level >= upgrade.maxLevel) return "다음: 최대 레벨"
        val delta = (upgrade.valuePerLevel * 100.0).roundToInt()
        val label = when (upgrade.effect) {
            UpgradeEffect.ENERGY_PRODUCTION -> "에너지"
            UpgradeEffect.BIOMASS_PRODUCTION -> "바이오매스"
            UpgradeEffect.AI_DATA_PRODUCTION -> "AI 데이터"
            UpgradeEffect.TAP_POWER -> "터치"
            UpgradeEffect.RESTORE_SPEED -> "복원"
            UpgradeEffect.OFFLINE_GAIN -> "오프라인"
        }
        val suffix = if (upgrade.effect == UpgradeEffect.OFFLINE_GAIN) "%p" else "%"
        return "다음 +$delta$suffix $label"
    }

    private fun drawBottomNav(canvas: Canvas) {
        val navTop = height - dp(76f)
        val itemW = width / 5f
        val panels = listOf(Panel.UPGRADE, Panel.TRAVEL, Panel.AI, Panel.EVENT, Panel.SHOP)
        panels.forEachIndexed { index, panel ->
            val left = index * itemW
            val bounds = RectF(left + dp(4f), navTop + dp(6f), left + itemW - dp(4f), height - dp(8f))
            drawButton(canvas, bounds, panel == currentPanel)
            val iconSize = dp(24f)
            val iconBounds = RectF(
                bounds.centerX() - iconSize * 0.5f,
                bounds.top + dp(6f),
                bounds.centerX() + iconSize * 0.5f,
                bounds.top + dp(6f) + iconSize,
            )
            drawBitmapFit(canvas, navIconRes(panel), iconBounds)
            text.textAlign = Paint.Align.CENTER
            text.textSize = sp(11f)
            text.color = Color.WHITE
            canvas.drawText(panel.label, bounds.centerX(), bounds.bottom - dp(10f), text)
            text.textAlign = Paint.Align.LEFT
            touchTargets += TouchTarget(bounds) {
                switchPanel(panel)
            }
        }
    }

    private fun drawIntroOverlay(canvas: Canvas) {
        fill.color = Color.argb(205, 2, 6, 14)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fill)
        val card = RectF(dp(22f), height * 0.22f, width - dp(22f), height * 0.72f)
        fill.color = Color.argb(248, 17, 24, 42)
        canvas.drawRoundRect(card, dp(10f), dp(10f), fill)
        stroke.color = Color.rgb(98, 242, 255)
        stroke.strokeWidth = dp(1.5f)
        canvas.drawRoundRect(card, dp(10f), dp(10f), stroke)

        val aiSize = dp(78f)
        drawBitmapFit(
            canvas,
            aiCompanionRes(state.aiAlignment),
            RectF(card.left + dp(12f), card.top + dp(12f), card.left + dp(12f) + aiSize, card.top + dp(12f) + aiSize),
        )
        text.color = Color.WHITE
        text.textSize = sp(17f)
        canvas.drawText("AI 코어 기동", card.left + dp(104f), card.top + dp(35f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(11f)
        canvas.drawText("행성을 되살리고 은하를 확장하세요.", card.left + dp(104f), card.top + dp(58f), text)

        val lines = listOf(
            "중앙 행성을 터치해 에너지와 복원도를 얻습니다.",
            "업그레이드는 자동 생산과 보상을 강화합니다.",
            "이동은 완성된 행성을 다음 행성으로 보냅니다.",
            "AI/이벤트 선택은 성향과 보상을 바꿉니다.",
            "상점은 광고 보상과 결제 상품 영역입니다.",
        )
        text.color = Color.rgb(230, 242, 248)
        text.textSize = sp(10.5f)
        lines.forEachIndexed { index, line ->
            canvas.drawText(line, card.left + dp(18f), card.top + dp(126f + index * 24f), text)
        }

        val button = RectF(card.left + dp(58f), card.bottom - dp(58f), card.right - dp(58f), card.bottom - dp(18f))
        drawButton(canvas, button, true)
        text.color = Color.WHITE
        text.textSize = sp(13f)
        text.textAlign = Paint.Align.CENTER
        canvas.drawText("시작", button.centerX(), button.top + dp(25f), text)
        text.textAlign = Paint.Align.LEFT

        touchTargets += TouchTarget(RectF(0f, 0f, width.toFloat(), height.toFloat())) {}
        touchTargets += TouchTarget(button) {
            showIntroOverlay = false
            describedPanels += currentPanel
        }
    }

    private fun drawPanelTitle(canvas: Canvas, title: String, subtitle: String, top: Float) {
        text.color = Color.WHITE
        text.textSize = sp(13f)
        canvas.drawText(title, dp(24f), top + dp(23f), text)
        text.color = Color.rgb(160, 246, 255)
        text.textSize = sp(9.5f)
        canvas.drawText(subtitle, dp(24f), top + dp(41f), text)
    }

    private fun drawBodyText(canvas: Canvas, value: String, y: Float) {
        text.color = Color.rgb(210, 232, 240)
        text.textSize = sp(12f)
        val maxChars = 38
        val lines = value.chunked(maxChars).take(4)
        lines.forEachIndexed { index, line ->
            canvas.drawText(line, dp(24f), y + index * dp(20f), text)
        }
    }

    private fun drawButton(canvas: Canvas, bounds: RectF, active: Boolean) {
        fill.color = if (active) Color.argb(230, 35, 72, 98) else Color.argb(210, 25, 34, 54)
        canvas.drawRoundRect(bounds, dp(7f), dp(7f), fill)
        stroke.color = if (active) Color.rgb(98, 242, 255) else Color.argb(100, 160, 246, 255)
        stroke.strokeWidth = dp(1.5f)
        canvas.drawRoundRect(bounds, dp(7f), dp(7f), stroke)
    }

    private fun drawBitmapFit(canvas: Canvas, resId: Int, bounds: RectF, alpha: Int = 255) {
        val bitmap = bitmapFor(resId) ?: return
        val previousAlpha = imagePaint.alpha
        imagePaint.alpha = alpha
        canvas.drawBitmap(bitmap, null as Rect?, bounds, imagePaint)
        imagePaint.alpha = previousAlpha
    }

    private fun drawBitmapCenterCrop(canvas: Canvas, resId: Int, bounds: RectF, alpha: Int = 255): Boolean {
        val bitmap = bitmapFor(resId) ?: return false
        val sourceRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val targetRatio = bounds.width() / bounds.height()
        var sourceWidth = bitmap.width
        var sourceHeight = bitmap.height
        if (sourceRatio > targetRatio) {
            sourceWidth = (bitmap.height * targetRatio).toInt().coerceIn(1, bitmap.width)
        } else {
            sourceHeight = (bitmap.width / targetRatio).toInt().coerceIn(1, bitmap.height)
        }
        val sourceLeft = ((bitmap.width - sourceWidth) / 2).coerceAtLeast(0)
        val sourceTop = ((bitmap.height - sourceHeight) / 2).coerceAtLeast(0)
        val previousAlpha = imagePaint.alpha
        imagePaint.alpha = alpha
        canvas.drawBitmap(
            bitmap,
            Rect(sourceLeft, sourceTop, sourceLeft + sourceWidth, sourceTop + sourceHeight),
            bounds,
            imagePaint,
        )
        imagePaint.alpha = previousAlpha
        return true
    }

    private fun drawBitmapCover(canvas: Canvas, resId: Int, bounds: RectF, alpha: Int = 255): Boolean {
        val bitmap = bitmapFor(resId) ?: return false
        val scale = maxOf(bounds.width() / bitmap.width.toFloat(), bounds.height() / bitmap.height.toFloat())
        val sourceWidth = (bounds.width() / scale).toInt().coerceAtMost(bitmap.width)
        val sourceHeight = (bounds.height() / scale).toInt().coerceAtMost(bitmap.height)
        val sourceLeft = ((bitmap.width - sourceWidth) / 2).coerceAtLeast(0)
        val sourceTop = ((bitmap.height - sourceHeight) / 2).coerceAtLeast(0)
        val previousAlpha = imagePaint.alpha
        imagePaint.alpha = alpha
        canvas.drawBitmap(
            bitmap,
            Rect(sourceLeft, sourceTop, sourceLeft + sourceWidth, sourceTop + sourceHeight),
            bounds,
            imagePaint,
        )
        imagePaint.alpha = previousAlpha
        return true
    }

    private fun bitmapFor(resId: Int): Bitmap? {
        bitmapCache[resId]?.let { return it }
        val decoded = BitmapFactory.decodeResource(resources, resId) ?: return null
        bitmapCache[resId] = decoded
        return decoded
    }

    private fun navIconRes(panel: Panel): Int = when (panel) {
        Panel.UPGRADE -> R.drawable.icon_nav_upgrade
        Panel.TRAVEL -> R.drawable.icon_nav_travel
        Panel.AI -> R.drawable.icon_nav_ai
        Panel.EVENT -> R.drawable.icon_nav_event
        Panel.SHOP -> R.drawable.icon_nav_shop
    }

    private fun planetStageRes(stage: PlanetStage): Int = when (stage) {
        PlanetStage.COLLAPSED -> R.drawable.planet_collapsed
        PlanetStage.ACTIVATED -> R.drawable.planet_activated
        PlanetStage.GROWTH -> R.drawable.planet_growth
        PlanetStage.COMPLETE -> R.drawable.planet_complete
    }

    private fun aiCompanionRes(alignment: AIAlignment): Int = when (alignment) {
        AIAlignment.LIFE -> R.drawable.ai_companion_life
        AIAlignment.INDUSTRIAL -> R.drawable.ai_companion_industrial
        AIAlignment.COMBAT -> R.drawable.ai_companion_combat
    }

    private fun panelDescription(panel: Panel): String = when (panel) {
        Panel.UPGRADE -> "생산, 터치, 오프라인 보상을 강화합니다."
        Panel.TRAVEL -> "완성한 행성에서 다음 행성으로 워프합니다."
        Panel.AI -> "행성마다 한 번 AI 성향을 선택합니다."
        Panel.EVENT -> "이상 현상의 보상과 리스크를 선택합니다."
        Panel.SHOP -> "광고 보상과 결제 상품을 관리합니다."
    }

    private fun upgradeIconRes(upgrade: UpgradeDef): Int = when (upgrade.id) {
        "solar_lattice" -> R.drawable.icon_upgrade_solar_lattice
        "pulse_drill" -> R.drawable.icon_upgrade_pulse_drill
        "ion_roots" -> R.drawable.icon_upgrade_ion_roots
        "memory_leaf" -> R.drawable.icon_upgrade_memory_leaf
        "tap_resonator" -> R.drawable.icon_upgrade_tap_resonator
        "nano_weather" -> R.drawable.icon_upgrade_nano_weather
        "sleep_protocol" -> R.drawable.icon_upgrade_sleep_protocol
        "fusion_petal" -> R.drawable.icon_upgrade_fusion_petal
        "biosphere_mesh" -> R.drawable.icon_upgrade_biosphere_mesh
        "oracle_cache" -> R.drawable.icon_upgrade_oracle_cache
        "core_overclock" -> R.drawable.icon_upgrade_core_overclock
        "healing_orbit" -> R.drawable.icon_upgrade_healing_orbit
        "drone_swarm" -> R.drawable.icon_upgrade_drone_swarm
        "seed_vault" -> R.drawable.icon_upgrade_seed_vault
        "sentience_bus" -> R.drawable.icon_upgrade_sentience_bus
        "warp_calibrator" -> R.drawable.icon_upgrade_warp_calibrator
        "quiet_factory" -> R.drawable.icon_upgrade_quiet_factory
        "risk_reactor" -> R.drawable.icon_upgrade_risk_reactor
        "garden_mind" -> R.drawable.icon_upgrade_garden_mind
        "nexus_dream" -> R.drawable.icon_upgrade_nexus_dream
        else -> when (upgrade.effect) {
            UpgradeEffect.ENERGY_PRODUCTION -> R.drawable.icon_upgrade_core_overclock
            UpgradeEffect.BIOMASS_PRODUCTION -> R.drawable.icon_upgrade_garden_mind
            UpgradeEffect.AI_DATA_PRODUCTION -> R.drawable.icon_upgrade_nexus_dream
            UpgradeEffect.TAP_POWER -> R.drawable.icon_upgrade_tap_resonator
            UpgradeEffect.RESTORE_SPEED -> R.drawable.icon_upgrade_warp_calibrator
            UpgradeEffect.OFFLINE_GAIN -> R.drawable.icon_upgrade_sleep_protocol
        }
    }

    private fun eventImageRes(eventId: String): Int = when (eventId) {
        "collapse_warning" -> R.drawable.event_collapse_warning
        "alien_signal" -> R.drawable.event_alien_signal
        "ancient_structure" -> R.drawable.event_ancient_structure
        "resource_surge" -> R.drawable.event_resource_surge
        "ai_error" -> R.drawable.event_ai_error
        "unknown_life" -> R.drawable.event_unknown_life
        "warp_echo" -> R.drawable.event_warp_echo
        "meteor_garden" -> R.drawable.event_meteor_garden
        "core_dream" -> R.drawable.event_core_dream
        "silent_orbit" -> R.drawable.event_silent_orbit
        else -> R.drawable.event_collapse_warning
    }

    private fun rewardIconRes(placement: RewardedPlacement): Int = when (placement) {
        RewardedPlacement.PRODUCTION_BOOST -> R.drawable.reward_ad_production_boost
        RewardedPlacement.OFFLINE_DOUBLE -> R.drawable.reward_ad_offline_double
        RewardedPlacement.EVENT_REWARD -> R.drawable.reward_ad_event_bonus
        RewardedPlacement.RESTORE_SPEED -> R.drawable.reward_ad_restore_speed
        RewardedPlacement.FREE_CHEST -> R.drawable.reward_ad_free_chest
    }

    private fun productIconRes(product: StoreProduct): Int = when (product) {
        StoreProduct.REMOVE_ADS -> R.drawable.iap_remove_ads
        StoreProduct.STARTER_PACK -> R.drawable.iap_starter_pack
        StoreProduct.AI_UPGRADE_PACK -> R.drawable.iap_ai_upgrade_pack
        StoreProduct.PRODUCTION_PACK -> R.drawable.iap_production_pack
        StoreProduct.PLANET_SKIN_PACK -> R.drawable.iap_planet_skin_pack
        StoreProduct.GALAXY_PASS -> R.drawable.iap_galaxy_pass
    }

    private fun planetBackgroundRes(index: Int): Int = when (index.coerceIn(0, GameBalance.planets.lastIndex)) {
        0 -> R.drawable.bg_planet_ash_prime
        1 -> R.drawable.bg_planet_verdant_zero
        2 -> R.drawable.bg_planet_foundry_moon
        3 -> R.drawable.bg_planet_echo_ruin
        4 -> R.drawable.bg_planet_aurora_nexus
        5 -> R.drawable.bg_planet_glass_nebula
        6 -> R.drawable.bg_planet_abyss_station
        7 -> R.drawable.bg_planet_lumina_desert
        8 -> R.drawable.bg_planet_titan_greenhouse
        9 -> R.drawable.bg_planet_chrono_ruin
        10 -> R.drawable.bg_planet_sanctum_ring
        else -> R.drawable.bg_planet_eternal_core
    }

    private fun encounterColor(type: EncounterRewardType): Int = when (type) {
        EncounterRewardType.ENERGY -> Color.rgb(98, 242, 255)
        EncounterRewardType.BIOMASS -> Color.rgb(114, 245, 128)
        EncounterRewardType.AI_DATA -> Color.rgb(255, 215, 90)
        EncounterRewardType.RARE -> Color.rgb(182, 140, 255)
        EncounterRewardType.RIFT -> Color.rgb(255, 95, 115)
        EncounterRewardType.MIXED -> Color.rgb(160, 246, 255)
    }

    private fun ringAssetForEffect(effect: UpgradeEffect): Int = when (effect) {
        UpgradeEffect.BIOMASS_PRODUCTION -> R.drawable.fx_upgrade_orbit_bio
        UpgradeEffect.AI_DATA_PRODUCTION -> R.drawable.fx_upgrade_orbit_ai_data
        UpgradeEffect.RESTORE_SPEED -> R.drawable.fx_restore_wave
        UpgradeEffect.TAP_POWER -> R.drawable.fx_tap_resonance_core
        else -> R.drawable.fx_upgrade_orbit_energy
    }

    private fun propAssetForEffect(effect: UpgradeEffect): Int = when (effect) {
        UpgradeEffect.BIOMASS_PRODUCTION -> R.drawable.prop_bio_seed_drone
        UpgradeEffect.AI_DATA_PRODUCTION -> R.drawable.prop_ai_memory_shard
        UpgradeEffect.OFFLINE_GAIN -> R.drawable.prop_offline_storage_orb
        else -> R.drawable.prop_energy_satellite
    }

    private fun pulseAssetForEffect(effect: UpgradeEffect): Int = when (effect) {
        UpgradeEffect.RESTORE_SPEED -> R.drawable.fx_restore_wave
        UpgradeEffect.TAP_POWER -> R.drawable.fx_tap_resonance_core
        else -> R.drawable.fx_upgrade_burst
    }

    private fun currentVisualStats(): VisualStats {
        val effectLevels = linkedMapOf(
            UpgradeEffect.ENERGY_PRODUCTION to 0,
            UpgradeEffect.BIOMASS_PRODUCTION to 0,
            UpgradeEffect.AI_DATA_PRODUCTION to 0,
            UpgradeEffect.TAP_POWER to 0,
            UpgradeEffect.RESTORE_SPEED to 0,
            UpgradeEffect.OFFLINE_GAIN to 0,
        )
        val start = state.planetIndex * 4
        var upgradeLevels = 0
        GameBalance.upgrades.drop(start).take(4).forEach { upgrade ->
            val level = state.upgrades[upgrade.id] ?: 0
            upgradeLevels += level
            effectLevels[upgrade.effect] = (effectLevels[upgrade.effect] ?: 0) + level
        }
        val max = GameBalance.planets[state.planetIndex].stageThresholds.last().coerceAtLeast(1.0)
        val progressRatio = (state.stageProgress / max).toFloat().coerceIn(0f, 1f)
        val stageIndex = stageIndex(state.stage())
        val intensity = (progressRatio * 0.55f + stageIndex * 0.12f + upgradeLevels * 0.018f).coerceIn(0f, 1f)
        val visualTier = (stageIndex + upgradeLevels / 8).coerceIn(0, 6)
        return VisualStats(
            upgradeLevels = upgradeLevels,
            effectLevels = effectLevels,
            progressRatio = progressRatio,
            stageIndex = stageIndex,
            intensity = intensity,
            visualTier = visualTier,
        )
    }

    private fun stageIndex(stage: PlanetStage): Int = when (stage) {
        PlanetStage.COLLAPSED -> 0
        PlanetStage.ACTIVATED -> 1
        PlanetStage.GROWTH -> 2
        PlanetStage.COMPLETE -> 3
    }

    private fun strongestEffect(stats: VisualStats, offset: Int): UpgradeEffect {
        val ordered = stats.effectLevels.entries
            .filter { it.value > 0 }
            .sortedByDescending { it.value }
            .map { it.key }
        if (ordered.isNotEmpty()) return ordered[offset % ordered.size]
        return when (offset % 3) {
            0 -> UpgradeEffect.ENERGY_PRODUCTION
            1 -> UpgradeEffect.BIOMASS_PRODUCTION
            else -> UpgradeEffect.AI_DATA_PRODUCTION
        }
    }

    private fun effectColor(effect: UpgradeEffect): Int = when (effect) {
        UpgradeEffect.ENERGY_PRODUCTION -> Color.rgb(98, 242, 255)
        UpgradeEffect.BIOMASS_PRODUCTION -> Color.rgb(114, 245, 128)
        UpgradeEffect.AI_DATA_PRODUCTION -> Color.rgb(255, 215, 90)
        UpgradeEffect.TAP_POWER -> Color.WHITE
        UpgradeEffect.RESTORE_SPEED -> Color.rgb(160, 246, 255)
        UpgradeEffect.OFFLINE_GAIN -> Color.rgb(182, 140, 255)
    }

    private fun progressPercent(): Int {
        val max = GameBalance.planets[state.planetIndex].stageThresholds.last()
        return ((state.stageProgress / max) * 100.0).toInt().coerceIn(0, 100)
    }

    private fun formatDuration(seconds: Long): String {
        val totalMinutes = ((seconds + 59L) / 60L).coerceAtLeast(0L)
        val days = totalMinutes / (24L * 60L)
        val hours = (totalMinutes % (24L * 60L)) / 60L
        val minutes = totalMinutes % 60L
        return when {
            days > 0L -> "${days}일 ${hours}시간"
            hours > 0L -> "${hours}시간 ${minutes}분"
            else -> "${minutes}분"
        }
    }

    private fun showChangeToast(changed: Boolean, success: String, failure: String) {
        Toast.makeText(context, if (changed) success else failure, Toast.LENGTH_SHORT).show()
    }

    private fun panelAccent(panel: Panel): Int = when (panel) {
        Panel.UPGRADE -> Color.rgb(98, 242, 255)
        Panel.TRAVEL -> Color.rgb(255, 215, 90)
        Panel.AI -> Color.rgb(114, 245, 128)
        Panel.EVENT -> Color.rgb(255, 95, 115)
        Panel.SHOP -> Color.rgb(182, 140, 255)
    }

    private fun colorWithAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun sp(value: Float): Float = value * resources.displayMetrics.scaledDensity

    private data class TouchTarget(
        val bounds: RectF,
        val action: () -> Unit,
    )

    private data class RewardBurst(
        val title: String,
        val detail: String,
        val startedMs: Long,
    )

    private enum class EncounterRewardType {
        ENERGY,
        BIOMASS,
        AI_DATA,
        MIXED,
        RARE,
        RIFT,
    }

    private data class EncounterDef(
        val id: String,
        val title: String,
        val resId: Int,
        val type: EncounterRewardType,
        val seconds: Long,
        val burstResId: Int = R.drawable.fx_encounter_collect_burst,
    )

    private data class PassingEncounter(
        val def: EncounterDef,
        val x: Float,
        val y: Float,
        val vx: Float,
        val drift: Float,
        val size: Float,
        val angle: Float,
        val ageMs: Long = 0L,
    )

    private data class EncounterBurst(
        val x: Float,
        val y: Float,
        val resId: Int,
        val color: Int,
        val ageMs: Long = 0L,
    )

    private data class UpgradeBurst(
        val title: String,
        val level: Int,
        val bounds: RectF,
        val effect: UpgradeEffect,
        val startedMs: Long,
    )

    private data class VisualStats(
        val upgradeLevels: Int,
        val effectLevels: Map<UpgradeEffect, Int>,
        val progressRatio: Float,
        val stageIndex: Int,
        val intensity: Float,
        val visualTier: Int,
    )

    private data class WalletFxPart(
        val label: String,
        val amount: Double,
        val color: Int,
    )

    private data class IncomeFloater(
        val label: String,
        val source: String,
        val color: Int,
        val x: Float,
        val y: Float,
        val startedMs: Long,
    )

    private data class TravelMapMetrics(
        val viewportWidth: Float,
        val spacing: Float,
        val sidePad: Float,
        val contentWidth: Float,
        val maxScroll: Float,
    )

    private data class ScreenTransition(
        val label: String,
        val accent: Int,
        val startedMs: Long,
    )

    private enum class ScreenMode {
        OPENING,
        LOBBY,
        GAME,
    }

    companion object {
        fun formatNumber(value: Double): String {
            return when {
                value >= 1_000_000.0 -> String.format(Locale.US, "%.1fm", value / 1_000_000.0)
                value >= 1_000.0 -> String.format(Locale.US, "%.1fk", value / 1_000.0)
                value >= 100.0 -> String.format(Locale.US, "%.0f", value)
                value >= 10.0 -> String.format(Locale.US, "%.1f", value)
                else -> String.format(Locale.US, "%.2f", value)
            }
        }
    }
}
