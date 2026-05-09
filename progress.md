Original prompt: 웹으로 실행하도록 해줘

2026-05-06:
- Android 네이티브 앱을 브라우저에서 실행할 수 없으므로, 같은 루프/데이터/이미지를 쓰는 정적 웹 빌드를 `web/`에 추가하기로 결정.
- 필수 테스트 훅: `window.render_game_to_text`, `window.advanceTime(ms)` 추가 예정.
- 이미지 원본은 `docs/image`, 앱 최적화본은 `app/src/main/res/drawable-nodpi`; 웹에는 앱 최적화본을 복사해 사용.
- 웹 정적 서버 시작: http://localhost:5174
- 웹 빌드 추가: `web/index.html`, `web/styles.css`, `web/game.js`.
- `web/assets`에 Android 최적화 이미지 50개 복사.
- Playwright 스킬 클라이언트로 기본 렌더링과 상호작용 테스트 완료. 콘솔 오류 파일 없음.
- 로컬 서버 URL: http://localhost:5174
- TODO: 행성/AI/배경 이미지가 추가되면 Canvas 도형 렌더링을 이미지 기반 렌더링으로 교체.
- TODO: 웹 상점은 테스트 보상 흐름이다. 실제 결제/광고는 Android 앱에서만 SDK 연동됨.
- 한국어 버전 작업: Android Canvas UI, 광고/결제 메시지, 앱 이름, 웹 Canvas UI의 사용자 노출 문구를 한국어로 변경. 내부 저장 키, 상품 ID, 패널 ID, AI 성향 enum 값은 기존 저장 데이터 호환을 위해 유지.
- 검증 예정: Android 단위 테스트/디버그 빌드, 웹 Playwright 렌더링 및 주요 패널 상호작용 확인.
- 검증 완료: `node --check web/game.js`, Playwright 기본 렌더/상호작용, `./gradlew.bat testDebugUnitTest`, `./gradlew.bat assembleDebug`, `./gradlew.bat bundleRelease` 모두 통과. Android 빌드에는 기존 `scaledDensity` deprecation 경고 1건만 있음.
- 산출물: `app/build/outputs/apk/debug/app-debug.apk`, `app/build/outputs/bundle/release/app-release.aab`, 웹 서버 `http://localhost:5174`.
- 비주얼 개선 작업: `tools/generate_visual_assets.ps1`로 메인 배경, 에너지 링, 행성 4단계, AI 성향 3종 PNG를 생성해 Android `drawable-nodpi`와 `web/assets`에 추가. 웹/Android 메인 화면이 새 자산을 사용하도록 변경 중.
- UX 설명 개선 작업: 시작 안내 오버레이와 각 탭 기능 설명 문구 추가 중. 웹 테스트 액션 `web/test_visual_onboarding.json` 추가.
- 비주얼/설명 개선 검증 완료: 웹 Playwright로 시작 안내 오버레이와 상호작용 후 상점 화면을 확인. `node --check web/game.js`, `./gradlew.bat testDebugUnitTest`, `./gradlew.bat assembleDebug`, `./gradlew.bat bundleRelease` 통과.
- 최신 산출물: `app/build/outputs/apk/debug/app-debug.apk`, `app/build/outputs/bundle/release/app-release.aab`. 새 이미지 자산은 `bg_nebula_main.png`, `fx_energy_ring.png`, `planet_collapsed/activated/growth/complete.png`, `ai_companion_life/industrial/combat.png`.
- 2026-05-07 UX 개선: 업그레이드 행에 현재 효과/다음 레벨 추가 효과/비용을 표시. 상점 광고 보상은 무료/보상량을 표시하고, 유료 상품은 fallback 가격과 지급 보상을 표시. 무료 상자와 상품 지급 시 중앙 보상 버스트 이펙트 추가.
- 검증 완료: `node --check web/game.js`, 웹 Playwright 업그레이드/상점/무료 상자 이펙트 캡처, `./gradlew.bat compileDebugKotlin --rerun-tasks`, `./gradlew.bat assembleDebug`, `./gradlew.bat bundleRelease` 통과.

- 2026-05-07 오프닝/로비 작업: 웹에 오프닝 화면, 로비 브리핑, 게임 진입 버튼, 탭/워프/AI/이벤트/상점 보상 전환 이펙트를 추가. 
ode --check web/game.js 통과.

- Android 오프닝/로비 작업: GameView에 오프닝, 로비, 게임 진입, 패널/워프/진화/이벤트/광고/상품 전환 이펙트를 추가. ./gradlew.bat compileDebugKotlin --rerun-tasks 통과(기존 scaledDensity 경고만 유지).
- 웹 테스트 액션 추가: web/test_opening_lobby_flow.json로 오프닝 스킵, 로비 시작, 이벤트/상점 전환을 검증할 수 있음.

- 웹 테스트 액션 보강: web/test_opening_idle.json는 오프닝 정지 캡처, web/test_lobby_screen.json은 오프닝 스킵 후 로비 캡처용.

- 웹 검증: Playwright 스킬 클라이언트로 오프닝(output/web-opening-rerun/shot-0.png), 로비(output/web-lobby/shot-0.png), 게임 진입 전환 이펙트(output/web-start-transition/shot-0.png), 로비→게임→이벤트→상점 흐름(output/web-opening-lobby-flow/shot-0.png) 캡처 완료. 빈 favicon 지정 후 오프닝 콘솔 오류 없음.

- Android 빌드 검증 완료: ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 모두 통과. 산출물은 app/build/outputs/apk/debug/app-debug.apk, app/build/outputs/bundle/release/app-release.aab. 기존 scaledDensity deprecation 경고만 유지.

- 2026-05-07 신규 이미지 적용: root image/의 9개 PNG를 web/assets와 pp/src/main/res/drawable-nodpi에 매핑 적용. 배경=01, 행성 4단계=02~05, 링=06, AI 생명/산업/전투=07~09. 세로형 행성/링 PNG가 찌그러지지 않도록 웹 drawImageCenterCrop, Android drawBitmapCenterCrop를 추가해 주요 렌더 경로에 적용.
- 검증 완료: 
ode --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright 캡처 output/web-new-assets-lobby/shot-0.png, output/web-new-assets-game/shot-0.png, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. 첫 bundleRelease는 이전 타임아웃 Gradle/R8 프로세스 파일 잠금으로 실패했으나 ./gradlew.bat --stop 후 재실행 통과.

- 2026-05-07 이동 조건 UI 개선: 웹/Android 이동 탭에 이동 가능 여부, 조건 1(행성 완성), 조건 2(이동 비용), 보유 충분/부족 자원을 표시. 웹 ender_game_to_text에도 	ravel 상태를 추가. 검증: 
ode --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright output/web-travel-conditions/shot-0.png, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과.

- 2026-05-07 업그레이드 레벨업 이펙트 추가: 업그레이드 성공 시 버튼 행에 노란 테두리/네온 링/입자/LEVEL UP Lv.n 문구가 표시되도록 웹 startUpgradeBurst/drawUpgradeBurst, Android UpgradeBurst/drawUpgradeBurst 추가. 웹 자동 테스트 액션 web/test_upgrade_levelup_effect.json, web/test_upgrade_levelup_effect_fast.json 추가. 검증: 
ode --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright 클라이언트 실행, 클릭 직후 수동 캡처 output/web-upgrade-levelup-effect-manual/shot-0.png, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과.

- 2026-05-08 행성 재방문/패시브 생산/실시간 획득 이펙트 작업: 웹과 Android 저장 구조에 planetProgress를 추가해 행성별 복원도를 따로 보존. 이미 개척한 행성은 은하 지도 노드로 재방문 가능하고, 완료된 비활성 행성은 colonyProduction으로 낮은 비율의 패시브 자원을 생산. 자동 생산/터치 보상은 캔버스 중앙에 자원 획득 플로터로 표시.
- 검증 완료: node --check web/game.js, Playwright 스킬 클라이언트 output/web-income-floaters/shot-0.png 및 state-0.json, 시드 기반 재방문 검증 output/web-revisit-seeded/state-revisited.json, ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug 통과. 추가 테스트 액션 web/test_income_floaters.json 추가.
- TODO: 다음 밸런스 단계에서 완료 행성 패시브 비율 22%와 행성별 업그레이드 계수 2.5%/Lv가 초반 진행을 너무 빠르게 만들지 확인.

- 2026-05-09 이동/은하 확장 밸런스 작업: 행성 수를 5개에서 12개로 확장하고, 신규 행성 7개와 행성별 업그레이드 28종을 추가. 신규 행성 이동에는 7일 항로 안정화 조건을 추가했으며, 이미 개척한 행성 재방문은 즉시 가능하게 유지. 웹/Android 이동 화면 미니맵에 현재 위치 링과 `현재 위치`, `n/12` 표기를 추가.
- 검증 완료: node --check web/game.js, Playwright 스킬 클라이언트 output/web-travel-minimap-week-clear/shot-0.png 및 state-0.json, ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. bundleRelease에서는 기존 scaledDensity deprecation 경고만 유지.
- TODO: 실제 플레이 데이터 기준으로 7일 고정 게이트가 너무 강하면 `GameBalance.minTravelSeconds`를 행성 티어별로 조정하거나 광고/이벤트로 최대 10~20%만 단축하는 구조 검토.

- 2026-05-09 이동 미니맵 가독성 개선: 웹/Android 은하 미니맵을 전체 12개 압축 표시에서 가로 스크롤 뷰포트로 변경. 첫 화면에는 약 4개 행성만 보이고, 현재 행성 노드는 크게 표시되며 스크롤바와 드래그 안내를 추가. 웹은 마우스/터치 드래그 및 휠 스크롤, Android는 손가락 좌우 드래그로 이동.
- 검증 완료: node --check web/game.js, Playwright 스킬 클라이언트 output/web-travel-scroll-minimap-4nodes/shot-0.png 및 state-0.json, 별도 Playwright 드래그 검증 output/web-travel-minimap-drag/state-after.json(mapScroll 590), ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. bundleRelease에서는 기존 scaledDensity deprecation 경고만 유지.

- 2026-05-09 방치형 보는맛 기획/프롬프트/초기 구현: 업그레이드가 수치만 오르는 문제가 있어 행성을 성장 보드로 쓰는 기획 문서 `docs/design/06_idle_visual_progression_plan.md`를 추가. 기존 프롬프트는 유지하고 `docs/art/IMAGE_PROMPTS_ADDED_IDLE_VISUAL_EFFECTS.md`에 업그레이드 링, 표면 성장 마스크, 위성/드론, 관찰 보상 오브젝트 추가 프롬프트를 분리 작성. 웹/Android 행성 렌더링에는 업그레이드 레벨과 효과 종류에 따라 궤도 링, 표면 노드, 위성 모듈, 자원 흐름, 행성 레벨업 파동이 증가하는 절차적 이펙트를 추가 중.
- 검증 완료: node --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright 스킬 클라이언트 output/web-idle-visual-growth-basic/shot-0.png 및 state-0.json, 고레벨 시드 캡처 output/web-idle-visual-growth-seeded/shot-0.png, 레벨업 파동 캡처 output/web-idle-visual-levelup-pulse/shot-0.png, ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. bundleRelease에서는 기존 scaledDensity deprecation 경고만 유지.
- TODO: 이미지 생성 후 `fx_upgrade_orbit_*`, `prop_*`, `mask_surface_*` 자산을 실제 PNG 레이어로 교체하면 절차적 도형의 거친 느낌을 더 줄일 수 있음. 고레벨 상태는 의도적으로 화려하지만 실제 폰에서 과밀하면 링 개수/알파를 15~25% 줄이는 튜닝 권장.

- 2026-05-09 행성별 배경/지나가는 보상 오브젝트 이미지 적용: `image/IMAGE_PROMPTS_ADDED_PLANET_BACKGROUNDS_AND_ENCOUNTERS`의 25개 PNG를 숫자 접두어 없는 리소스명으로 `web/assets`와 `app/src/main/res/drawable-nodpi`에 복사. 웹/Android 모두 행성별 배경(`bg_planet_*`)을 현재 행성에 맞게 표시하고, 일정 시간마다 정찰선/생명체/데이터 탐사체 등 `encounter_*` 오브젝트가 지나가며 터치 보상을 지급하도록 연결. 수집 시 `fx_encounter_collect_burst`, 희귀 보상/균열 전용 이펙트를 표시.
- 검증 완료: node --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright 스킬 클라이언트 output/web-planet-backgrounds-applied/shot-0.png 및 state-0.json, 시드 기반 루미나 사막+인카운터 캡처 output/web-encounter-assets-applied-visible/shot-encounter.png, 수집 후 캡처 output/web-encounter-assets-applied-visible/shot-collected.png, ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. bundleRelease에서는 기존 scaledDensity deprecation 경고만 유지.

- 2026-05-09 업그레이드 시각 성장 이미지 적용 및 image 정리: `image/IMAGE_PROMPTS_ADDED_IDLE_VISUAL_EFFECTS`의 16개 PNG를 숫자 접두어 없는 리소스명으로 `web/assets`와 `app/src/main/res/drawable-nodpi`에 복사. 웹/Android 행성 성장 레이어가 실제 이미지 기반 궤도 링(`fx_upgrade_orbit_*`), 표면 마스크(`mask_surface_*`), 위성/드론(`prop_*`), 레벨업 파동(`fx_upgrade_burst`, `fx_restore_wave`, `fx_tap_resonance_core`)을 사용하도록 연결. 추가 인카운터 3종(`encounter_pass_ship`, `encounter_space_lifeform`, `encounter_data_meteor`)도 보상 오브젝트 풀에 추가. 적용 완료 후 `image/` 아래 원본 PNG 50개와 빈 하위 폴더를 삭제해 `image/` 폴더를 비움.
- 검증 완료: node --check web/game.js, ./gradlew.bat compileDebugKotlin --rerun-tasks, Playwright 스킬 클라이언트 output/web-idle-effect-assets-basic/shot-0.png 및 고레벨 시드 캡처 output/web-idle-effect-assets-seeded/shot-visual-assets.png, 수집 후 캡처 output/web-idle-effect-assets-seeded/shot-collected.png, ./gradlew.bat testDebugUnitTest, ./gradlew.bat assembleDebug, ./gradlew.bat bundleRelease 통과. bundleRelease에서는 기존 scaledDensity deprecation 경고만 유지.

- 2026-05-09 실제 AdMob 광고 적용: 사용자가 제공한 실제 AdMob 앱 ID `ca-app-pub-4402708884038037~8835999992`와 Rewarded 광고 단위 5개를 `app/src/main/res/values/monetization.xml`에 반영. `AdsController`는 앱 시작 시 생산/오프라인/이벤트/복원/무료상자 보상형 광고를 모두 프리로드하도록 수정. 수익화 준비 문서와 출시 체크리스트도 실제 ID 적용 상태로 갱신.
- 검증 완료: `rg`로 Google 공식 테스트 광고 ID 잔여 없음 확인, `./gradlew.bat compileDebugKotlin --rerun-tasks`, `./gradlew.bat testDebugUnitTest`, `./gradlew.bat assembleDebug`, `./gradlew.bat bundleRelease` 통과. compileDebugKotlin에서는 기존 `scaledDensity` deprecation 경고만 유지.

- 2026-05-09 웹 테스트 저장 데이터 초기화 지원: `http://localhost:5174/?reset=1`로 접속하면 `ai-core-rebirth-web-save-v1` 로컬 저장 데이터를 삭제하고 기본 상태로 시작하도록 추가. 현재 열린 `AI 코어 리버스 웹` Chrome 창을 reset URL로 이동해 테스트 진행 데이터를 초기화.
- 검증 완료: `node --check web/game.js` 통과.

- 2026-05-09 개인정보처리방침 HTML 작성: Google Play 등록용 개인정보처리방침 초안을 `web/privacy-policy.html`과 `docs/legal/privacy-policy.html`에 추가. AdMob 광고 ID, Google Play Billing 구매 처리, 로컬 게임 저장 데이터, 삭제 방법, 아동 개인정보, 문의처 항목을 포함. 개발자 이메일은 실제 공개 연락처로 교체 필요.

- 2026-05-09 개인정보처리방침 GitHub 배포 준비: `web/privacy-policy.html`, `docs/legal/privacy-policy.html`, 배포 저장소 사본의 문의 이메일을 `hhy0111@hotmail.com`으로 교체. `output/ai-core-rebirth-privacy`에 `index.html`과 `privacy-policy.html`만 포함한 별도 Git 저장소를 만들고 `https://github.com/hhy0111/ai-core-rebirth-privacy.git`의 `main` 브랜치로 푸시. raw 파일 접근은 200 응답 확인, GitHub Pages는 아직 API 기준 404라 저장소 Settings > Pages에서 `main` / root 배포 설정 필요.

- 2026-05-09 전체 소스 GitHub 업로드: 사용자가 요청해 기존 `https://github.com/hhy0111/ai-core-rebirth-privacy.git` 저장소를 개인정보처리방침 전용이 아니라 전체 프로젝트 저장소로 확장. `.gitignore`, `.gitattributes`, `README.md`를 추가하고 Android 소스, 웹 프로토타입, 문서, 이미지 자산, Gradle wrapper, 도구 스크립트를 `main` 브랜치에 푸시. `app/build`, `output`, `.gradle`, `.kotlin`, `local.properties`, APK/AAB/서명 키는 제외.

- 2026-05-09 앱 아이콘/스토어 그래픽 적용 및 임시 업로드 AAB 생성: `image/`의 신규 정사각형 이미지를 Android 런처 아이콘으로 변환해 `mipmap-mdpi`~`mipmap-xxxhdpi`의 `ic_launcher.png`, `ic_launcher_round.png`로 추가하고 `AndroidManifest.xml`에 `android:icon`, `android:roundIcon`을 연결. 가로형 이미지는 Play Console 기능 그래픽 `docs/store/feature-graphic-1024x500.png`, 정사각형 이미지는 `docs/store/app-icon-512.png`로 저장. `bundleRelease` 통과 후 로컬 업로드 키 `local/ai-core-rebirth-upload.jks`로 `app/build/outputs/bundle/release/app-release-upload-signed.aab` 생성. `local/`과 `app/build/`은 `.gitignore`로 제외.

- 2026-05-10 인앱 상품 지급 흐름 점검: Play Console에 등록된 6개 상품 ID와 `StoreProduct` 코드 ID가 일치함을 확인. 구매 성공 시 `BillingController.onPurchasesUpdated -> processPurchased -> onPurchaseGranted -> GameView.applyPurchasedProduct -> GameEngine.grantResources -> persistNow`로 이어져 상품별 자원/복원/부스트 보상이 저장됨. 비소비성 상품은 owned product로 중복 구매를 막고, `production_pack`은 consume 처리 후 반복 구매 가능. Play Console 등록 가격과 코드 fallback 가격이 달라 보이는 문제를 수정하고 `testDebugUnitTest`, `compileDebugKotlin --rerun-tasks`, `bundleRelease` 통과. `app-release-upload-signed.aab` 재생성 및 `jarsigner -verify` 통과.
