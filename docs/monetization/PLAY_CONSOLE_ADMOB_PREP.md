# Play Console / AdMob 수익화 등록 준비

확인일: 2026-05-09  
대상 앱: `com.harnessgames.aicore`  
현재 코드 상태: 실제 AdMob Rewarded Ads ID, Google Play Billing Library 8.3.0 연동 코드 추가 완료  
중요: 실제 등록은 개발자 계정/결제 프로필/세금 정보가 필요하므로 사용자가 Play Console과 AdMob에서 직접 수행해야 한다.

## 1. 코드에 이미 반영된 항목

| 영역 | 파일 | 상태 |
|---|---|---|
| AdMob SDK | `app/build.gradle.kts` | `com.google.android.gms:play-services-ads:25.2.0` |
| Billing SDK | `app/build.gradle.kts` | `com.android.billingclient:billing-ktx:8.3.0` |
| AdMob 앱 ID | `app/src/main/res/values/monetization.xml` | 실제 앱 ID 적용: `ca-app-pub-4402708884038037~8835999992` |
| Rewarded Ad Unit IDs | `app/src/main/res/values/monetization.xml` | 실제 광고 단위 ID 5개 적용 |
| Billing 상품 ID | `StoreProduct` enum | Play Console에 동일 ID로 등록 필요 |
| Billing 권한 | `AndroidManifest.xml` | `com.android.vending.BILLING` |
| 광고 보상 흐름 | `AdsController.kt`, `GameView.kt` | 광고 시청 완료 콜백 때만 지급 |
| 결제 흐름 | `BillingController.kt` | 상품 조회, 구매, 복원, 승인, 소비 처리 |
| 구매 중복 방지 | `EntitlementRepository.kt` | owned product / processed token 저장 |

## 2. AdMob 등록 항목

AdMob에서 Android 앱을 만든 뒤 아래 광고 단위를 생성한다. MVP에서는 모두 Rewarded 형식이다. 현재 사용자가 제공한 실제 ID를 코드에 반영했다.

앱 ID:

```text
ca-app-pub-4402708884038037~8835999992
```

| 코드 배치 | AdMob 광고 단위 이름 | 앱 리소스 키 | 적용 값 |
|---|---|---|---|
| 생산 2배 10분 | `rewarded_production_boost` | `admob_rewarded_production_boost` | `ca-app-pub-4402708884038037/2987181050` |
| 오프라인 보상 2배 | `rewarded_offline_double` | `admob_rewarded_offline_double` | `ca-app-pub-4402708884038037/2466032766` |
| 다음 이벤트 보상 2배 | `rewarded_event_reward` | `admob_rewarded_event_reward` | `ca-app-pub-4402708884038037/1152951090` |
| 행성 복원 +8% | `rewarded_restore_speed` | `admob_rewarded_restore_speed` | `ca-app-pub-4402708884038037/6157865769` |
| 무료 상자 | `rewarded_free_chest` | `admob_rewarded_free_chest` | `ca-app-pub-4402708884038037/4844784094` |

반영된 파일:

```xml
<!-- app/src/main/res/values/monetization.xml -->
<string name="admob_app_id">ca-app-pub-4402708884038037~8835999992</string>
<string name="admob_rewarded_production_boost">ca-app-pub-4402708884038037/2987181050</string>
```

주의:

* 실제 광고 ID로 로컬 테스트할 때는 반드시 AdMob 테스트 기기 등록을 먼저 한다.
* 신규 앱/신규 광고 단위는 AdMob 전파와 심사 상태에 따라 한동안 광고 미노출 또는 no fill이 발생할 수 있다.
* 보상형 광고는 선택형 보상으로만 사용한다. 진행 차단용 강제 광고는 넣지 않는다.

## 3. Play Console 인앱 상품 등록 항목

모든 상품은 Play Console의 인앱 상품, one-time product로 등록한다. 코드의 `productId`와 Play Console 상품 ID가 정확히 같아야 상품 조회가 성공한다.

| 상품 ID | 코드 enum | 상품 유형 | 소비 처리 | 권장 가격 티어 | 게임 내 효과 |
|---|---|---|---|---:|---|
| `remove_ads` | `REMOVE_ADS` | Managed one-time | 비소비 | USD 2.99 | 향후 강제/배너 광고 제거 권한. 보상형 광고는 선택 유지 |
| `starter_pack` | `STARTER_PACK` | Managed one-time | 비소비 | USD 1.99 | 에너지 1200, 바이오매스 45, AI 데이터 18, 복원 진행도 160 |
| `ai_upgrade_pack` | `AI_UPGRADE_PACK` | Managed one-time | 비소비 | USD 3.99 | AI 스킨/편의 권한, AI 데이터 80 |
| `production_pack` | `PRODUCTION_PACK` | Consumable one-time | 소비 | USD 0.99 | 에너지 2500, 바이오매스 80, AI 데이터 30, 생산 1시간 부스트 |
| `planet_skin_pack` | `PLANET_SKIN_PACK` | Managed one-time | 비소비 | USD 2.99 | 행성 스킨 권한 |
| `premium_galaxy_pass` | `GALAXY_PASS` | Managed one-time | 비소비 | USD 4.99 | 프리미엄 패스 권한, 편의 보상 |

구현 기준:

* `production_pack`만 `consumeAsync()`로 소비 처리하여 반복 구매 가능하다.
* 나머지는 `acknowledgePurchase()`로 승인하고 로컬 entitlement에 저장한다.
* 서버 없는 MVP이므로 구매 검증은 클라이언트 수준이다. 정식 매출 규모가 생기면 Play Developer API와 서버 검증으로 올려야 한다.

## 4. Play Console 테스트 준비

1. Play Console에서 앱 생성.
2. 패키지 이름을 `com.harnessgames.aicore`로 등록.
3. 내부 테스트 트랙 생성.
4. 결제 프로필, 판매자 계정, 세금 정보 설정.
5. 위 6개 인앱 상품 생성 및 활성화.
6. 라이선스 테스터 이메일 등록.
7. 내부 테스트용 AAB 업로드.
8. 테스터 계정으로 Play Store 내부 테스트 설치.
9. 실제 구매 테스트 카드로 구매, 취소, pending 결제, 복원 테스트.

## 5. AdMob 테스트 준비

1. AdMob에서 앱 생성.
2. Android 앱 ID 발급.
3. Rewarded 광고 단위 5개 생성.
4. `monetization.xml`의 앱 ID와 광고 단위 ID가 실제 ID인지 확인.
5. 테스트 기기를 AdMob 테스트 기기로 등록.
6. 내부 테스트 설치본에서 광고 로드/시청/보상 지급 확인.

## 6. Data Safety / 개인정보 준비

AdMob과 Billing을 쓰면 Play Console Data safety와 개인정보 처리방침에 최소한 아래 항목을 검토해야 한다.

| 항목 | 필요 여부 | 메모 |
|---|---|---|
| 광고 ID | 필요 | Google Mobile Ads SDK 사용 |
| 구매 정보 | 필요 | Google Play Billing 사용 |
| 앱 활동/분석 | 현재 미사용 | Firebase Analytics 추가 시 갱신 |
| 위치 정보 | 현재 미사용 | 위치 권한 없음 |
| 계정 정보 | 현재 미사용 | 로그인 없음 |
| 개인정보 처리방침 URL | 필요 | Play Console 앱 콘텐츠에 등록 |
| EEA/UK 사용자 동의 | 권장/필요 가능 | AdMob UMP SDK 도입 검토 |

## 7. QA 체크리스트

### 광고

* 광고 미로드 상태에서 버튼 클릭 시 자원 지급이 되지 않는가?
* 광고 시청 완료 콜백 후에만 보상이 지급되는가?
* 광고 표시 실패 후 다음 광고가 재로딩되는가?
* 오프라인 보상 2배는 앱 시작 후 1회만 가능한가?
* 이벤트 보상 2배는 다음 이벤트 1회에만 적용되는가?

### 결제

* 상품 미등록 상태에서 구매 버튼이 실패 메시지를 보여주는가?
* 내부 테스트 트랙 설치본에서 상품 가격이 표시되는가?
* 비소비성 상품은 재구매 대신 `Owned` 상태가 되는가?
* 소비성 `production_pack`은 반복 구매 가능한가?
* pending 결제는 지급되지 않고 완료 후 지급되는가?
* 앱 삭제/재설치 후 `Restore purchases`로 비소비성 상품이 복원되는가?

## 8. 공식 문서

* AdMob Rewarded Ads: https://developers.google.com/admob/android/rewarded
* AdMob Android SDK setup: https://developers.google.com/admob/android/quick-start
* Google Play Billing integration: https://developer.android.com/google/play/billing/integrate
* Billing Library release notes: https://developer.android.com/google/play/billing/release-notes
* Billing test guide: https://developer.android.com/google/play/billing/test
