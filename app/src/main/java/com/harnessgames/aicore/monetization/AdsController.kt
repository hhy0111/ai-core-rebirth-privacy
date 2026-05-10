package com.harnessgames.aicore.monetization

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.harnessgames.aicore.R

enum class RewardedPlacement(val label: String) {
    PRODUCTION_BOOST("생산 2배"),
    OFFLINE_DOUBLE("오프라인 2배"),
    EVENT_REWARD("이벤트 2배"),
    RESTORE_SPEED("복원 가속"),
    FREE_CHEST("무료 상자"),
    TRAVEL_SUPPLY("항로 보급"),
    ENCOUNTER_CLAIM("조우 보상"),
}

class AdsController(private val activity: Activity) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val loadedAds = mutableMapOf<RewardedPlacement, RewardedAd>()
    private val loadedAtMs = mutableMapOf<RewardedPlacement, Long>()
    private val loadingPlacements = mutableSetOf<RewardedPlacement>()

    fun preloadPrimaryReward() {
        RewardedPlacement.entries.forEach(::loadRewarded)
    }

    fun isReady(placement: RewardedPlacement): Boolean {
        clearExpiredAd(placement)
        return loadedAds[placement] != null
    }

    fun showRewarded(
        placement: RewardedPlacement,
        onReward: () -> Unit,
        onUnavailable: (String) -> Unit,
    ) {
        mainHandler.post {
            clearExpiredAd(placement)
            val ad = loadedAds[placement]
            if (ad == null) {
                loadRewarded(placement)
                onUnavailable("${placement.label} 광고를 불러오는 중입니다. 잠시 후 다시 시도하세요.")
                return@post
            }

            loadedAds.remove(placement)
            loadedAtMs.remove(placement)
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    loadRewarded(placement)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    loadRewarded(placement)
                    onUnavailable("광고 표시 실패: ${adError.message}")
                }
            }

            ad.show(
                activity,
                OnUserEarnedRewardListener {
                    onReward()
                },
            )
        }
    }

    private fun loadRewarded(placement: RewardedPlacement) {
        if (loadingPlacements.contains(placement)) return
        loadingPlacements += placement

        RewardedAd.load(
            activity,
            adUnitIdFor(placement),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    loadingPlacements -= placement
                    loadedAds[placement] = ad
                    loadedAtMs[placement] = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadingPlacements -= placement
                    loadedAds.remove(placement)
                    loadedAtMs.remove(placement)
                }
            },
        )
    }

    private fun clearExpiredAd(placement: RewardedPlacement) {
        val loadedAt = loadedAtMs[placement] ?: return
        val ageMs = System.currentTimeMillis() - loadedAt
        if (ageMs > REWARDED_AD_EXPIRY_MS) {
            loadedAds.remove(placement)
            loadedAtMs.remove(placement)
            loadRewarded(placement)
        }
    }

    private fun adUnitIdFor(placement: RewardedPlacement): String {
        val resId = when (placement) {
            RewardedPlacement.PRODUCTION_BOOST -> R.string.admob_rewarded_production_boost
            RewardedPlacement.OFFLINE_DOUBLE -> R.string.admob_rewarded_offline_double
            RewardedPlacement.EVENT_REWARD -> R.string.admob_rewarded_event_reward
            RewardedPlacement.RESTORE_SPEED -> R.string.admob_rewarded_restore_speed
            RewardedPlacement.FREE_CHEST -> R.string.admob_rewarded_free_chest
            RewardedPlacement.TRAVEL_SUPPLY -> R.string.admob_rewarded_travel_supply
            RewardedPlacement.ENCOUNTER_CLAIM -> R.string.admob_rewarded_encounter_claim
        }
        return activity.getString(resId)
    }

    companion object {
        private const val REWARDED_AD_EXPIRY_MS = 55L * 60L * 1000L
    }
}
