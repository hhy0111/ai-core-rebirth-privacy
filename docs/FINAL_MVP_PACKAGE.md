# AI Core Rebirth MVP Package

문서 상태: 개발 착수 기준 통합본  
확인일: 2026-05-05  
플랫폼: Google Play Android 전용

## 1. 게임 한 줄 소개

감정을 가진 AI 코어가 죽은 행성을 복원하거나 활용하며 은하를 확장하는 2D 방치형 하이브리드 캐주얼 게임.

## 2. 핵심 재미 요소

* 행성이 붕괴 상태에서 완성 상태로 눈에 띄게 살아나는 시각 변화.
* 짧은 접속에서도 자원 생산, 업그레이드, 터치 가속, 이벤트 선택 중 하나가 발생하는 1~3분 세션 리듬.
* 생명형, 산업형, 전투형 AI 선택이 말투와 생산 보너스에 누적 반영되는 동료감.
* 다음 행성의 색, 자원 조건, 이벤트가 달라지는 반복 확장.

## 3. 전체 게임 루프

자동 생산 -> 터치 가속 -> 업그레이드 구매 -> 행성 복원 진행 -> 진화 선택 또는 랜덤 이벤트 -> 행성 완성 -> 다음 행성 이동 -> 5개 행성 완료 후 Prestige.

## 4. MVP 범위

* 포함: 행성 5개, 상태 4단계, AI 성향 3종, 자원 3종, 업그레이드 20종, 진화 선택 3계열, 이벤트 10종, 광고 보상 5종, 인앱 상품 6종, 오프라인 보상, 간단 Prestige.
* 제외: 멀티플레이, 서버 랭킹, 길드, PvP, 대형 서버, 과도한 컷신.

## 5. 행성 시스템

행성은 `Collapsed`, `Activated`, `Growth`, `Complete` 4단계로 진행한다. 단계는 누적 복원 진행도 기준으로 전환되며, 현재 구현은 중앙 행성의 균열, 발광, 식생, 오라로 구분한다.

MVP 행성: Ash Prime, Verdant Zero, Foundry Moon, Echo Ruin, Aurora Nexus.

## 6. AI 시스템

AI는 우측 홀로그램 코어로 표시한다. 성향은 생명형, 산업형, 전투형 3종이며 선택 누적 점수가 높은 성향이 현재 AI 말투와 보너스를 결정한다.

현재 구현 보너스:

* 생명형: 바이오매스 생산 증가, 오프라인 보상 보정.
* 산업형: 에너지 생산 증가.
* 전투형: 터치 보상과 AI 데이터 생산 증가.

## 7. 자원/업그레이드 시스템

자원은 에너지, 바이오매스, AI 데이터 3종이다. 업그레이드는 행성당 4개씩 총 20개로 구성하고, 비용은 레벨별 지수 증가로 관리한다. 생산, 터치, 복원 속도, 오프라인 보상 계열로 나뉜다.

## 8. 진화 선택 시스템

각 행성에서 1회 선택한다. 선택지는 생명형, 산업형, 전투형이며 즉시 보상과 누적 성향 점수를 함께 제공한다. 같은 행성에서 중복 선택할 수 없다.

## 9. 이벤트 시스템

랜덤 이벤트는 10종이며 각 이벤트는 2개 선택지를 가진다. 선택지는 보상, 복원 진행도 증감, AI 성향 점수 변화를 동반한다. MVP에서는 짧은 선택과 즉시 피드백을 우선한다.

## 10. 광고 보상 설계

보상형 광고만 사용한다. MVP 광고 보상은 생산 2배, 오프라인 2배, 이벤트 보상 강화, 복원 속도 증가, 무료 상자 5종으로 제한한다. 현재 앱 구현은 debug 빌드에서 보상형 광고를 즉시 성공 처리하는 테스트 어댑터를 둔다.

## 11. Google Play Billing 상품 설계

인앱 상품 6종:

* 광고 제거
* 스타터 패키지
* AI 업그레이드 패키지
* 생산 가속 패키지
* 행성 스킨 패키지
* 프리미엄 은하 패스

원칙은 진행 차단 금지, 시간 단축과 외형 중심, 무료 유저 완주 가능이다.

## 12. 아트 디렉션

2D 탑다운, 카툰, 네온 SF. 중앙 행성이 첫 화면의 주인공이어야 하며, UI는 어두운 패널과 밝은 cyan, green, gold, coral 포인트를 혼합한다. 행성 변화는 색상만이 아니라 균열, 링, 식생, 오라 실루엣으로 구분한다.

## 13. 개발 구조

현재 구현은 단일 Android `:app` 모듈이다. 빌드 도구는 Gradle Wrapper 8.11.1, AGP 8.10.1, Kotlin 2.2.10으로 정렬했다.

* `game`: 순수 Kotlin 게임 모델, 밸런스, 엔진.
* `ui`: Android Canvas 기반 MVP 화면.
* `storage`: SharedPreferences JSON 저장과 백업 복구.
* `monetization`: 광고/결제 어댑터 인터페이스 초안.

출시 전에는 광고 단위 ID, Billing 상품 ID, 개인정보 처리방침, 릴리즈 서명을 실제 값으로 교체해야 한다.

## 14. QA 체크리스트

* QA 1차: 자원 생산, 업그레이드, 행성 단계 변화, 저장/불러오기, 오프라인 보상.
* QA 2차: 1~3분 세션 UX, AI 선택 몰입감, 이벤트 선택 피로도, UI 가독성.
* QA 3차: 광고 보상 실패 처리, 결제 실패/복원 처리, 저장 데이터 손상 복구, 릴리즈 빌드 검증.

출시 차단: 결제/광고 보상 오류, 저장 데이터 손실, 진행 불가, UI 이해 불가, 과도한 반복 피로.

## 15. 밸런스 체크리스트

* 초반 3분 안에 업그레이드 2회 이상 체감.
* 첫 행성 완료 시간이 지루하지 않은지 확인.
* 광고 보상이 기본 생산을 무력화하지 않는지 확인.
* 무료 유저가 모든 행성과 Prestige에 접근 가능한지 확인.
* 첫 Prestige 목표는 무료 기준 5~7일을 초안으로 두고 플레이 로그로 재조정.

## 16. Google Play 출시 전 점검표

상세 점검표는 `docs/qa/04_google_play_release_checklist.md`에 분리한다. 현재 빌드 설정은 `targetSdk = 35`, Android App Bundle 생성 가능 구조, Billing Library 8.3.0, Google Mobile Ads SDK 25.2.0을 기준으로 시작하며, `bundleRelease` 생성까지 검증했다.

## 승인 상태

* Command Agent / PM: Approved for MVP start
* Communication Agent: Approved with integration notes
* Game Design Lead: Approved for prototype, balance requires QA data
* Visual Design Lead: Approved for MVP art direction
* Dev Lead: Approved for local prototype, release SDK flows pending
* QA Lead: Rejected for final release until 3 QA cycles pass
* Balance Lead: Approved for first simulation, final tuning pending
