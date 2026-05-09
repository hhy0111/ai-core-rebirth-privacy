package com.harnessgames.aicore

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.harnessgames.aicore.game.GameEngine
import com.harnessgames.aicore.monetization.AdsController
import com.harnessgames.aicore.monetization.BillingController
import com.harnessgames.aicore.monetization.EntitlementRepository
import com.harnessgames.aicore.storage.SaveRepository
import com.harnessgames.aicore.ui.GameView

class MainActivity : Activity() {
    private lateinit var gameView: GameView
    private lateinit var adsController: AdsController
    private lateinit var billingController: BillingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsController = AdsController(this)
        MobileAds.initialize(this) {
            adsController.preloadPrimaryReward()
        }

        val engine = GameEngine()
        val repository = SaveRepository(this)
        billingController = BillingController(this, EntitlementRepository(this))
        val loaded = repository.load() ?: repository.loadBackup() ?: engine.initialState()
        val (stateWithOffline, offlineReward) = engine.claimOffline(loaded)

        gameView = GameView(
            context = this,
            engine = engine,
            initialState = stateWithOffline,
            initialOfflineReward = offlineReward,
            adsController = adsController,
            billingController = billingController,
            saveState = { repository.save(it) },
        )
        setContentView(gameView)
        billingController.start()

        offlineReward?.let {
            Toast.makeText(
                this,
                "오프라인 ${it.seconds / 60}분: 에너지 +${GameView.formatNumber(it.gained.energy)}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onPause() {
        gameView.persistNow()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::billingController.isInitialized) {
            billingController.start()
            billingController.restorePurchases()
        }
    }

    override fun onDestroy() {
        if (::billingController.isInitialized) {
            billingController.stop()
        }
        super.onDestroy()
    }
}
