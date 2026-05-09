# AI 코어 리버스 추가 이미지 프롬프트 - 행성별 배경과 터치 인카운터

작성일: 2026-05-09  
용도: 기존 프롬프트 파일을 유지한 상태에서, 행성 이동 체감 강화와 랜덤 터치 보너스 오브젝트 제작을 위한 추가 프롬프트  
범위: 행성별 배경 12종, 지나가는 우주선/생명체/자원 오브젝트 10종, 보상 이펙트 3종

## 0. 사용 기준

이 파일은 기존 파일을 대체하지 않는다.

기존 유지 파일:

* `docs/art/IMAGE_PROMPTS.md`
* `docs/art/IMAGE_PROMPTS_ADDED_MAIN_VISUALS.md`

이번 추가 파일의 목적:

* 행성을 이동했을 때 "다른 장소에 왔다"는 느낌을 즉시 전달한다.
* 중앙 행성 이미지를 방해하지 않는 행성별 배경을 만든다.
* 가끔 지나가는 우주선/생명체/자원체를 터치하면 소량 보상을 주는 짧은 세션 재미를 만든다.

권장 적용 위치:

* Web: `web/assets`
* Android: `app/src/main/res/drawable-nodpi`

## 1. 공통 스타일

모든 프롬프트 끝에 붙일 공통 스타일:

```text
2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

공통 네거티브:

```text
text, letters, numbers, watermark, logo, brand mark, UI panel, button, realistic human, copyrighted character, low resolution, blurry, muddy colors, dark unreadable subject, excessive tiny detail, noisy texture, cropped subject, photorealism, 3D render, horror gore, beige monochrome palette, purple-only palette
```

## 2. 구현 고려사항

### 2-1. 행성별 배경

* 권장 규격: `1080x1920` 또는 `1440x2560`
* 파일 형식: 불투명 PNG
* 중앙 45% 영역은 행성 오브젝트가 올라오므로 너무 밝거나 복잡한 디테일을 피한다.
* 행성마다 색상, 주변 천체, 파편, 성운 형태가 달라야 한다.
* 배경 이미지 안에 큰 중앙 행성을 넣지 않는다. 중앙 행성은 게임 오브젝트가 담당한다.

### 2-2. 지나가는 터치 보너스 오브젝트

* 권장 규격: `512x512` 또는 `768x768`
* 파일 형식: 투명 PNG
* 코드에서 좌우 반전과 이동 속도를 처리할 예정이므로 한 방향 기준으로 제작한다.
* 터치 보상은 진행을 깨지 않는 소량 보상으로 유지한다.
* 권장 등장 주기: 45~120초 사이 랜덤
* 권장 화면 체류 시간: 5~9초
* 권장 보상량: 현재 초당 생산량의 30~120초 분량 또는 희귀 자원 소량
* 광고 보상보다 약해야 한다. 이 기능은 "보너스 발견"이지 필수 성장 루트가 아니다.

## 3. 행성별 배경 프롬프트

### PB-01. 잿빛 프라임 배경

* 파일명: `bg_planet_ash_prime.png`
* 연결 행성: `ash_prime`

```text
Portrait mobile game background, dead basalt planet orbit zone after cosmic collapse, deep navy space, distant cracked moon fragments on the left, thin cyan dormant river lines across debris, subtle coral crack glow in far asteroids, clean center area for large gameplay planet, diagonal faint energy grid, lonely first-world mood, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-02. 녹원 제로 배경

* 파일명: `bg_planet_verdant_zero.png`
* 연결 행성: `verdant_zero`

```text
Portrait mobile game background, neon jungle restoration orbit, dark teal space filled with floating seed pods, lime green biosphere mist, cyan rain-like energy streams, small restored leaf-shaped satellites in the distance, warm hopeful second-world mood, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-03. 주조 위성 배경

* 파일명: `bg_planet_foundry_moon.png`
* 연결 행성: `foundry_moon`

```text
Portrait mobile game background, industrial foundry moon orbit, dark blue space with molten orange resource belts far away, cyan factory beacon lines, small broken mining rings, gold sparks drifting through orbit, efficient mechanical mood but still bright and readable, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-04. 메아리 유적 배경

* 파일명: `bg_planet_echo_ruin.png`
* 연결 행성: `echo_ruin`

```text
Portrait mobile game background, ancient alien ruin orbit, broken signal towers floating as silhouettes, cyan holographic wave patterns, coral warning shards, gold relic dust, mysterious but not dark, faint repeating circular scan marks in deep navy space, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-05. 오로라 넥서스 배경

* 파일명: `bg_planet_aurora_nexus.png`
* 연결 행성: `aurora_nexus`

```text
Portrait mobile game background, aurora ocean nexus orbit, sweeping cyan and lime aurora bands across deep space, soft gold tide particles, distant liquid-like moons, restored network nodes connected by thin light lines, uplifting mid-game expansion mood, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-06. 유리 성운 배경

* 파일명: `bg_planet_glass_nebula.png`
* 연결 행성: `glass_nebula`

```text
Portrait mobile game background, glass nebula region, translucent crystal continents floating in space, prism cyan refractions, lime light trapped inside glass shards, gold star dust, clean sparkling atmosphere, premium new-sector mood, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-07. 심연 정거장 배경

* 파일명: `bg_planet_abyss_station.png`
* 연결 행성: `abyss_station`

```text
Portrait mobile game background, abandoned orbital station above a deep ocean planet region, silhouettes of broken station arcs around the edges, dark cobalt space, cyan pressure lights, slow lime bio-luminescent traces, coral emergency beacons far away, tense exploration mood, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-08. 루미나 사막 배경

* 파일명: `bg_planet_lumina_desert.png`
* 연결 행성: `lumina_desert`

```text
Portrait mobile game background, luminous desert planet orbit, gold sand-like particle streams in space, buried solar panel fragments as distant silhouettes, cyan heat mirage arcs, small coral storm sparks, bright dry atmosphere without beige dominance, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-09. 타이탄 온실 배경

* 파일명: `bg_planet_titan_greenhouse.png`
* 연결 행성: `titan_greenhouse`

```text
Portrait mobile game background, giant greenhouse orbital region, massive transparent bio-domes floating at the edges, lime oxygen clouds, cyan gravity field lines, gold pollen-like resource particles, distant heavy moon shadows, lush but sci-fi, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-10. 크로노 폐허 배경

* 파일명: `bg_planet_chrono_ruin.png`
* 연결 행성: `chrono_ruin`

```text
Portrait mobile game background, time fractured ancient city orbit, repeated ghost silhouettes of ruined towers fading into space, cyan clock-like orbit arcs without numbers, coral instability seams, gold memory particles, strange time loop atmosphere, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-11. 신성 고리 배경

* 파일명: `bg_planet_sanctum_ring.png`
* 연결 행성: `sanctum_ring`

```text
Portrait mobile game background, colossal sanctum ring orbit, huge partial gold and cyan restoration ring crossing the edges of the screen, lime sanctuary light, distant clean planet nodes connected by thin beams, calm final-sector grandeur, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### PB-12. 이터널 코어 배경

* 파일명: `bg_planet_eternal_core.png`
* 연결 행성: `eternal_core`

```text
Portrait mobile game background, final emotional AI core region at the center of collapsed cosmic energy, deep navy space with controlled coral singularity seams, powerful cyan and gold core rays around the edges, lime rebirth particles, dramatic final world mood but readable for mobile, clean center area for large gameplay planet, no text, no UI, no central large planet, 9:16 composition, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 4. 지나가는 터치 보너스 오브젝트 프롬프트

공통 조건:

* 투명 PNG
* 중앙 정렬
* 작은 크기에서도 실루엣이 읽혀야 한다.
* 생성 이미지에 텍스트, 숫자, 로고를 넣지 않는다.

### EN-01. 정찰 우주선

* 파일명: `encounter_scout_ship.png`
* 권장 보상: 에너지 60초분

```text
Transparent PNG sprite, small friendly scout spaceship crossing a mobile sci-fi game screen, sleek cyan cockpit, compact white and navy hull, tiny gold engine particles, readable side-facing silhouette, no weapon focus, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-02. 자원 화물 드론

* 파일명: `encounter_cargo_drone.png`
* 권장 보상: 에너지 45초분 + 바이오매스 소량

```text
Transparent PNG sprite, compact resource cargo drone for a neon sci-fi idle game, boxy but cute hovering silhouette, cyan anti-gravity rings, small gold cargo pods, lime status lights, side-facing travel pose, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-03. 발광 생명체

* 파일명: `encounter_luminous_lifeform.png`
* 권장 보상: 바이오매스 90초분

```text
Transparent PNG sprite, harmless luminous alien lifeform drifting through space, manta-ray-like abstract silhouette but original, lime green bio glow, cyan translucent fins, soft gold spores trailing behind, friendly and collectible, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-04. 데이터 탐사 프로브

* 파일명: `encounter_data_probe.png`
* 권장 보상: AI 데이터 45초분

```text
Transparent PNG sprite, small AI data exploration probe, spherical cyan core with three tiny orbiting antenna fins, gold memory shard inside, clean readable silhouette, slow drifting scan pose, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-05. 희귀 유성 파편

* 파일명: `encounter_rare_meteor.png`
* 권장 보상: 에너지 30초분 + 희귀 보너스 확률

```text
Transparent PNG sprite, rare meteor shard flying across screen, dark rock core with bright gold resource vein, cyan heat trail, small coral spark marks, collectible object silhouette, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-06. 고대 위성 조각

* 파일명: `encounter_ancient_satellite.png`
* 권장 보상: AI 데이터 60초분 또는 이벤트 게이지 소량

```text
Transparent PNG sprite, ancient broken satellite fragment from a ruined civilization, circular cyan signal core, cracked navy metal panels, gold relic inlay, faint coral warning spark, side drifting pose, readable at small size, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-07. 씨앗 군집

* 파일명: `encounter_seed_swarm.png`
* 권장 보상: 바이오매스 45초분 + 복원도 소량

```text
Transparent PNG sprite, small swarm of glowing restoration seeds, five to seven lime seed pods grouped in a loose arc, cyan tiny trails, gold pollen sparkles, friendly collectible cluster, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-08. 워프 상인선

* 파일명: `encounter_warp_merchant.png`
* 권장 보상: 무작위 자원 90초분, 낮은 확률로 무료 상자

```text
Transparent PNG sprite, small colorful warp merchant ship for a casual mobile sci-fi game, rounded friendly hull, cyan front window, gold cargo fins, lime and coral accent lights, playful readable silhouette, no brand marks, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-09. 감정 메모리 조각

* 파일명: `encounter_emotion_memory.png`
* 권장 보상: AI 데이터 30초분 + AI 메시지 1회

```text
Transparent PNG sprite, floating emotional memory crystal for a sentient AI game, small faceted cyan and gold crystal heart-core shape, lime inner pulse, soft circular hologram ripple, collectible readable silhouette, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### EN-10. 불안정 균열체

* 파일명: `encounter_unstable_rift.png`
* 권장 보상: 리스크형 보상, 에너지 120초분 또는 복원도 -소량

```text
Transparent PNG sprite, unstable cosmic rift object crossing a sci-fi mobile game screen, compact oval tear in space, coral and cyan crack energy, gold particles spilling out, dangerous but non-horror, readable collectible warning silhouette, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 5. 터치 보상 이펙트 프롬프트

### FX-01. 터치 획득 파동

* 파일명: `fx_encounter_collect_burst.png`
* 용도: 우주선/생명체 터치 성공 시 짧게 터지는 이펙트

```text
Transparent PNG effect sprite, collectible reward burst for a mobile idle game, circular cyan pulse with gold particles and small lime sparks, clean center, soft bloom, readable on dark space background, no hard square border, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### FX-02. 희귀 보상 별가루

* 파일명: `fx_rare_reward_stardust.png`
* 용도: 희귀 보상 또는 상인선 터치 보상

```text
Transparent PNG effect sprite, rare reward stardust burst, gold star particles mixed with cyan circular shockwave and tiny lime glints, premium reward feeling, clean readable particles, no hard border, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### FX-03. 리스크 균열 소멸

* 파일명: `fx_rift_resolve.png`
* 용도: 불안정 균열체 터치 후 결과 연출

```text
Transparent PNG effect sprite, unstable rift resolving into energy, coral crack fragments folding into cyan light, small gold particles, compact circular burst, dangerous but clean mobile game readability, no hard border, centered with transparent padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 6. 기능 설계 메모

### 6-1. 행성 이동 체감

권장 연출:

* 이동 직후 1초 워프 링
* 배경 즉시 교체
* 상단 행성명 강조
* AI 한 줄 코멘트 변경
* 미니맵 현재 위치 링 1회 펄스

### 6-2. 터치 인카운터 규칙

권장 MVP 규칙:

* 화면을 지나가는 오브젝트는 동시에 최대 1개
* 등장 주기: 45~120초 랜덤
* 터치 가능 시간: 화면 안에 보이는 동안만
* 터치 성공 시 `fx_encounter_collect_burst.png`와 자원 플로터 표시
* 보상형 광고와 충돌하지 않도록 보상량은 광고 보상보다 낮게 유지
* 오프라인 중에는 발생하지 않음

### 6-3. 행성별 추천 인카운터 풀

| 행성 | 추천 인카운터 |
|---|---|
| 잿빛 프라임 | 정찰 우주선, 희귀 유성 파편, 데이터 탐사 프로브 |
| 녹원 제로 | 발광 생명체, 씨앗 군집, 자원 화물 드론 |
| 주조 위성 | 자원 화물 드론, 고대 위성 조각, 희귀 유성 파편 |
| 메아리 유적 | 데이터 탐사 프로브, 고대 위성 조각, 감정 메모리 조각 |
| 오로라 넥서스 | 워프 상인선, 발광 생명체, 감정 메모리 조각 |
| 유리 성운 | 감정 메모리 조각, 데이터 탐사 프로브, 희귀 유성 파편 |
| 심연 정거장 | 고대 위성 조각, 자원 화물 드론, 불안정 균열체 |
| 루미나 사막 | 희귀 유성 파편, 정찰 우주선, 자원 화물 드론 |
| 타이탄 온실 | 씨앗 군집, 발광 생명체, 워프 상인선 |
| 크로노 폐허 | 감정 메모리 조각, 불안정 균열체, 고대 위성 조각 |
| 신성 고리 | 워프 상인선, 데이터 탐사 프로브, 씨앗 군집 |
| 이터널 코어 | 불안정 균열체, 감정 메모리 조각, 워프 상인선 |

