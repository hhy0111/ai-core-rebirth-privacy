# AI 코어 리버스 추가 이미지 프롬프트

작성일: 2026-05-06  
용도: 기존 `IMAGE_PROMPTS.md`와 분리된 신규 메인 화면 개선용 이미지 프롬프트  
범위: 이번에 추가한 메인 배경, 행성 4단계, 에너지 이펙트, AI 캐릭터 3종만 포함

## 0. 사용 기준

이 파일은 기존 마스터 프롬프트 파일을 대체하지 않는다.  
아래 프롬프트는 현재 추가된 신규 이미지와 1:1로 대응한다.

현재 적용 위치:

* Android: `app/src/main/res/drawable-nodpi`
* Web: `web/assets`

기존 아이콘, 업그레이드, 이벤트, 상점, 맵 이미지는 기존 프롬프트 파일과 기존 이미지 세트를 유지한다.

## 1. 공통 스타일

모든 프롬프트 끝에 붙일 공통 스타일:

```text
2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

공통 네거티브:

```text
text, letters, numbers, watermark, logo, brand mark, realistic human, copyrighted character, low resolution, blurry, muddy colors, dark unreadable subject, excessive tiny detail, noisy texture, cropped subject, photorealism, 3D render, horror gore, beige monochrome palette, purple-only palette
```

## 2. 신규 메인 화면 배경

### ADD-01. 메인 네온 우주 배경

* 파일명: `bg_nebula_main.png`
* 권장 규격: 1080x1920 또는 1440x2560
* 배경: 불투명 PNG
* 현재 용도: 게임 메인 화면 배경

```text
Portrait mobile game background for a neon sci-fi idle planet restoration game, deep navy space with subtle diagonal cyan energy grid lines, distant broken planet silhouettes, soft nebula clouds in cyan, lime, coral and gold, small readable stars, dark but not black, center area kept clean for a large circular planet and UI overlay, no text, no UI panels, no characters, no logos, 9:16 composition, polished indie mobile game quality, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 3. 신규 행성 이미지

공통 조건:

* 파일 형식: PNG
* 권장 규격: 1024x1024 또는 2048x2048
* 배경: 투명 배경
* 구도: 중앙 정렬, 원형 실루엣 명확
* 주의: UI 텍스트는 이미지에 넣지 않는다.

### ADD-02. 행성 붕괴 단계

* 파일명: `planet_collapsed.png`
* 현재 용도: 행성 단계 `붕괴`

```text
Transparent PNG sprite, top-down circular dead planet in collapsed state, dark basalt and navy crust, strong coral red cracks across the surface, faint dormant cyan river lines, broken plates, thin gold orbital fragment, subtle outer cyan energy haze, clear circular silhouette, centered with generous transparent padding, no cast shadow, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### ADD-03. 행성 활성화 단계

* 파일명: `planet_activated.png`
* 현재 용도: 행성 단계 `활성화`

```text
Transparent PNG sprite, top-down circular planet in activated state, dark blue restored crust with cyan core light returning, cracks partly sealed by glowing energy, clean cyan circular core ring at the center, several small energy nodes on the surface, thin gold orbital ring, early restoration feeling, centered with transparent background and generous padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### ADD-04. 행성 성장 단계

* 파일명: `planet_growth.png`
* 현재 용도: 행성 단계 `성장`

```text
Transparent PNG sprite, top-down circular planet in growth state, dark teal crust mixed with spreading neon green living regions, cyan rivers flowing across the surface, repaired plates, small restoration structures, gold resource veins, soft lime aura outside the planet, clear difference from collapsed and activated stages, centered with transparent background and generous padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### ADD-05. 행성 완성 단계

* 파일명: `planet_complete.png`
* 현재 용도: 행성 단계 `완성`

```text
Transparent PNG sprite, top-down circular planet in complete restored state, vibrant teal and green living world, bright cyan rivers, stable white AI core glow at the center, clean gold orbital halo, small luminous life islands, joyful rebuilt world, premium mobile game polish, centered with transparent background and generous padding, no text, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 4. 신규 이펙트 이미지

### ADD-06. 에너지 링 이펙트

* 파일명: `fx_energy_ring.png`
* 권장 규격: 1024x1024
* 배경: 투명 배경
* 현재 용도: 메인 행성 주변 회전 링/입자 이펙트

```text
Transparent PNG effect sprite, circular neon energy ring for a restored planet, multiple thin cyan orbital arcs, gold accent arcs, tiny glowing energy particles around the ring, clean center empty so a planet can sit underneath, soft bloom but crisp edges, centered with generous transparent padding, no text, no logo, no hard square border, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 5. 신규 AI 캐릭터 이미지

공통 조건:

* 파일 형식: PNG
* 권장 규격: 512x512 또는 1024x1024
* 배경: 투명 배경
* 용도: 우측 상단 AI 동료 캐릭터 표시
* 주의: 말풍선, 글자, UI 버튼은 이미지에 넣지 않는다.

### ADD-07. 생명형 AI

* 파일명: `ai_companion_life.png`
* 현재 용도: AI 성향 `생명형`

```text
Transparent PNG character icon, friendly sentient AI core companion for a mobile idle planet restoration game, circular floating hologram body, warm lime green main glow, cyan inner eye, small gold empathy core, soft protective aura, cute but not childish, readable at small size, centered with transparent padding, no text, no face like a human, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### ADD-08. 산업형 AI

* 파일명: `ai_companion_industrial.png`
* 현재 용도: AI 성향 `산업형`

```text
Transparent PNG character icon, efficient sentient AI core companion for a mobile idle planet restoration game, circular floating hologram body, cool cyan main glow, precise geometric inner eye, blue-white calculation rings, clean industrial energy pattern, calm and analytical personality, readable at small size, centered with transparent padding, no text, no human face, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

### ADD-09. 전투형 AI

* 파일명: `ai_companion_combat.png`
* 현재 용도: AI 성향 `전투형`

```text
Transparent PNG character icon, aggressive sentient AI core companion for a mobile idle planet restoration game, circular floating hologram body, coral red main glow, sharp cyan inner eye, gold overdrive core, angular energy rings, intense but not horror, risk-seeking personality, readable at small size, centered with transparent padding, no text, no human face, no logo, 2D top-down mobile idle game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouette, crisp polished raster illustration, soft bloom, cyan energy accents, lime life accents, coral danger accents, gold rare-resource accents, high contrast, mobile game production quality, no text, no letters, no numbers, no watermark, no logo, no copyrighted characters, no photorealism, no gritty realism
```

## 6. 선택 기준

신규 이미지를 다시 생성하거나 고를 때 우선순위:

1. 모바일 화면에서 축소해도 실루엣이 즉시 읽혀야 한다.
2. 행성 4단계는 색과 표면 패턴만 봐도 구분되어야 한다.
3. 배경은 예쁘더라도 중앙 행성과 UI를 방해하면 탈락이다.
4. AI 캐릭터 3종은 색상과 형태로 성향 차이가 보여야 한다.
5. 텍스트가 이미지 안에 들어가면 탈락이다.
