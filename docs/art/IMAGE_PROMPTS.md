# AI Core Rebirth 이미지 프롬프트 마스터 파일

작성일: 2026-05-06  
용도: Google Play Android MVP 출시 전까지 필요한 이미지 제작용 단일 프롬프트 문서  
게임 방향: 2D 탑다운 방치형, 카툰 스타일, 네온 SF, 밝고 선명한 색감, 감정을 가진 AI 코어와 행성 복원

## 0. 사용 원칙

* 모든 프롬프트는 원본 생성용이다. 특정 기존 게임, 영화, 캐릭터, 브랜드, 작가 스타일을 참조하지 않는다.
* AI 생성 이미지에는 텍스트를 넣지 않는다. 앱명, 카피, 가격, 버튼 문구는 후편집으로 넣는다.
* Google Play 제출용 스크린샷은 실제 앱 화면 캡처를 기본으로 한다. 이 문서의 스크린샷 프롬프트는 배경, 캡션 프레임, 스토어용 보조 그래픽 제작에만 사용한다.
* 아이콘, feature graphic, 스크린샷에는 Google Play 배지, 순위, 할인, 다운로드 유도 문구, 과장 수상 표현을 넣지 않는다.
* 최종 파일명은 소문자, 숫자, 언더스코어만 사용한다.

## 1. 공통 아트 바이블

### 1-1. 전역 스타일 프롬프트

모든 이미지 프롬프트 끝에 붙인다.

```text
2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### 1-2. 전역 네거티브 프롬프트

```text
text, letters, numbers, watermark, logo, Google Play badge, app store badge, ranking badge, sale badge, discount badge, realistic human, copyrighted character, third-party logo, low resolution, blurry, noisy, muddy colors, dark gray dominant palette, horror gore, excessive detail, tiny unreadable UI, cropped subject, distorted perspective, 3D render, photorealistic, beige monochrome palette, purple-only palette
```

### 1-3. 팔레트

| 용도 | 색상 |
|---|---|
| 우주 배경 | `#0C0F1C`, `#11182A` |
| 에너지 | `#62F2FF` |
| 생명형 AI | `#72F580` |
| 산업형 AI | `#5ED7FF` |
| 전투형 AI | `#FF5F73` |
| 희귀 자원 | `#FFD75A` |
| 패널 | `#11182A`, `#182238` |
| 텍스트 후편집 | `#FFFFFF`, `#A0F6FF` |

## 2. Google Play 출시 이미지

### GP-01. Google Play 앱 아이콘

* 파일명: `gp_app_icon_512.png`
* 규격: 512x512, 32-bit PNG, sRGB, Google Play가 둥근 마스크와 그림자를 적용하므로 원본에 둥근 모서리와 외부 그림자 금지
* 목적: Play 스토어 앱 아이콘

```text
Square app icon for a mobile idle game called AI Core Rebirth, a glowing emotional AI core inside a small restored planet, circular cyan core eye, cracked planet crust turning into green living land, neon energy ring, bold readable silhouette at small size, full square artwork background, no text, no outer drop shadow, no rounded corners, original symbol, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-02. Feature Graphic

* 파일명: `gp_feature_graphic_1024x500.png`
* 규격: 1024x500, JPEG 또는 24-bit PNG, 알파 없음
* 목적: Play 스토어 상단/추천 노출/영상 커버
* 후편집: 좌측 또는 상단에 앱명만 수동 배치 가능. AI 생성 텍스트 금지.

```text
Wide Google Play feature graphic, a dead planet being revived by a sentient AI core, left side shows cracked dark planet surface, center shows cyan AI energy beam, right side shows bright restored green neon ecosystem and orbital rings, small galaxy map dots in the background, clear story of planetary rebirth, large empty safe area for manually added title text, no generated text, no device mockup, no store badge, no ranking badge, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-03. YouTube 프리뷰 영상 썸네일

* 파일명: `gp_preview_video_thumbnail_1280x720.png`
* 규격: 1280x720 권장
* 목적: 선택 사항인 프리뷰 영상 썸네일

```text
YouTube preview video thumbnail for a neon sci-fi idle mobile game, actual gameplay-inspired composition with central restored planet, resource particles flowing upward, AI hologram companion on the side, galaxy warp path behind, dramatic but readable, large clean space for manual title text, no generated text, no device mockup, no store badge, 16:9 composition, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-04. 스토어 스크린샷 배경 프레임 1 - 행성 복원

* 파일명: `gp_screenshot_frame_01_rebirth_1080x1920.png`
* 규격: 1080x1920, 실제 게임 캡처를 중앙에 얹기 위한 배경/프레임
* 목적: 첫 스토어 스크린샷 보조 그래픽

```text
Portrait store screenshot background frame for a mobile game, central transparent-safe vertical area for real gameplay capture, surrounding neon space border, small cyan energy particles, subtle restored planet motifs at the corners, clean mobile marketing layout, no text, no phone device frame, no characters covering the center, 9:16 composition, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-05. 스토어 스크린샷 배경 프레임 2 - AI 선택

* 파일명: `gp_screenshot_frame_02_ai_choice_1080x1920.png`

```text
Portrait store screenshot background frame for AI evolution choice, three subtle holographic color arcs around the edges representing life green, industrial cyan, combat coral, center left clear for real gameplay capture, small AI core motifs, no generated text, no device mockup, no store badge, 9:16 composition, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-06. 스토어 스크린샷 배경 프레임 3 - 은하 확장

* 파일명: `gp_screenshot_frame_03_galaxy_1080x1920.png`

```text
Portrait store screenshot background frame for galaxy expansion, neon star map paths, five planet nodes arranged diagonally, warp lines and soft cyan glow, large empty center for real gameplay screenshot, no generated text, no device mockup, no store badge, 9:16 composition, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### GP-07. 스토어 스크린샷 배경 프레임 4 - 이벤트

* 파일명: `gp_screenshot_frame_04_event_1080x1920.png`

```text
Portrait store screenshot background frame for random event decisions, subtle warning banner shapes, coral alert glow, unknown alien signal waveform in the border, center area clean for real gameplay capture, no generated text, no device mockup, no store badge, 9:16 composition, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

## 3. 앱 내부 핵심 이미지

### CORE-01. 시작 화면 배경

* 파일명: `bg_title_rebirth_1440x2560.png`
* 규격: 1440x2560
* 목적: 앱 첫 실행/타이틀/로딩 화면

```text
Portrait title screen background for AI Core Rebirth, a lonely sentient AI core awakening above a cracked dead planet, faint galaxy ruins in the distance, one bright restored green land patch beginning to glow, cinematic but readable for mobile, empty upper-middle space for manually added title, no generated text, no UI, no logo, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### CORE-02. 메인 우주 배경

* 파일명: `bg_main_space_loop_1440x2560.png`
* 규격: 1440x2560
* 목적: 기본 게임 화면 배경

```text
Portrait seamless-style mobile game background, deep space with subtle diagonal energy grid, distant broken planet silhouettes, small neon stars, dark navy base but not black, readable low-detail center so UI and planet remain clear, no text, no objects blocking center, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### CORE-03. 은하 맵 배경

* 파일명: `bg_galaxy_map_1440x2560.png`
* 규격: 1440x2560
* 목적: 행성 이동 화면

```text
Portrait galaxy map background for an idle mobile game, five major planet nodes connected by neon warp paths, dark blue space, bright cyan navigation lines, small gold rare-resource markers, enough empty areas for UI labels added later, no text, no numbers, no device mockup, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

## 4. 행성 이미지 세트

공통 규격:

* 파일 형식: PNG, 투명 배경
* 기본 크기: 2048x2048 원본, 앱 내 리사이즈
* 카메라: 탑다운 정면
* 구성: 행성은 원형 실루엣이 명확해야 하며 단계별 변화가 축소 화면에서도 구분되어야 한다.

### PLANET-01. Ash Prime - 붕괴

* 파일명: `planet_ash_prime_01_collapsed.png`

```text
Transparent PNG sprite, top-down circular planet Ash Prime in collapsed state, dark basalt crust, large red coral cracks, dormant cyan river lines barely glowing, broken surface plates, dead but not horror, strong silhouette, centered with transparent background, no shadow outside sprite, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-02. Ash Prime - 활성화

* 파일명: `planet_ash_prime_02_activated.png`

```text
Transparent PNG sprite, top-down circular planet Ash Prime activated state, basalt crust with cyan core light returning, cracks partly sealed with glowing blue energy, small energy nodes on the surface, first orbital ring fragment, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-03. Ash Prime - 성장

* 파일명: `planet_ash_prime_03_growth.png`

```text
Transparent PNG sprite, top-down circular planet Ash Prime growth state, dark basalt land now mixed with green neon moss regions, cyan rivers flowing, repaired surface plates, small restoration structures, warm gold resource veins, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-04. Ash Prime - 완성

* 파일명: `planet_ash_prime_04_complete.png`

```text
Transparent PNG sprite, top-down circular planet Ash Prime complete state, restored basalt planet with bright cyan rivers, green living regions, clean orbital halo, stable AI core glow at the center, gold accents, joyful rebuilt world, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-05. Verdant Zero - 붕괴

* 파일명: `planet_verdant_zero_01_collapsed.png`

```text
Transparent PNG sprite, top-down circular planet Verdant Zero collapsed state, dead jungle world with dried dark green land, broken root canyons, faded lime spores, cyan core buried under dead vines, cracked ecosystem, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-06. Verdant Zero - 활성화

* 파일명: `planet_verdant_zero_02_activated.png`

```text
Transparent PNG sprite, top-down circular planet Verdant Zero activated state, first neon jungle roots waking up, cyan irrigation lines, small green glowing sprouts, dark dead areas still visible, clear transition from dead to alive, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-07. Verdant Zero - 성장

* 파일명: `planet_verdant_zero_03_growth.png`

```text
Transparent PNG sprite, top-down circular planet Verdant Zero growth state, lush neon jungle patches spreading across the planet, glowing green canopy islands, cyan streams, small bio-domes, gold pollen particles, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-08. Verdant Zero - 완성

* 파일명: `planet_verdant_zero_04_complete.png`

```text
Transparent PNG sprite, top-down circular planet Verdant Zero complete state, vibrant restored neon jungle planet, green canopy oceans, clean cyan rivers, protective life aura, small harmonious AI garden structures, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-09. Foundry Moon - 붕괴

* 파일명: `planet_foundry_moon_01_collapsed.png`

```text
Transparent PNG sprite, top-down circular moon Foundry Moon collapsed state, broken industrial ring moon, cold metal plates, dark molten cracks, disabled factories, coral danger fissures, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-10. Foundry Moon - 활성화

* 파일명: `planet_foundry_moon_02_activated.png`

```text
Transparent PNG sprite, top-down circular moon Foundry Moon activated state, industrial plates lighting up with cyan circuits, a few factories online, controlled molten resource veins glowing gold, ring segments repairing, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-11. Foundry Moon - 성장

* 파일명: `planet_foundry_moon_03_growth.png`

```text
Transparent PNG sprite, top-down circular moon Foundry Moon growth state, balanced industrial recovery world, clean factories, cyan conveyor energy paths, gold resource cores, small green restoration parks among metal districts, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-12. Foundry Moon - 완성

* 파일명: `planet_foundry_moon_04_complete.png`

```text
Transparent PNG sprite, top-down circular moon Foundry Moon complete state, polished industrial moon with safe neon foundries, cyan orbital production ring, gold resource veins contained, small green life zones proving balance, clear complete aura, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-13. Echo Ruin - 붕괴

* 파일명: `planet_echo_ruin_01_collapsed.png`

```text
Transparent PNG sprite, top-down circular planet Echo Ruin collapsed state, ancient signal arrays shattered across a dusty violet-blue world, broken antenna rings, dead cyan signal lines, mysterious but readable, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-14. Echo Ruin - 활성화

* 파일명: `planet_echo_ruin_02_activated.png`

```text
Transparent PNG sprite, top-down circular planet Echo Ruin activated state, ancient arrays reawakening, cyan signal arcs, repaired antenna nodes, faint gold data crystals, dead ruins still visible, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-15. Echo Ruin - 성장

* 파일명: `planet_echo_ruin_03_growth.png`

```text
Transparent PNG sprite, top-down circular planet Echo Ruin growth state, repaired signal temples, cyan data streams orbiting the planet, green restoration patches around ancient structures, gold memory crystals, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-16. Echo Ruin - 완성

* 파일명: `planet_echo_ruin_04_complete.png`

```text
Transparent PNG sprite, top-down circular planet Echo Ruin complete state, radiant restored ancient signal planet, stable cyan data halo, green life surrounding temples, gold memory constellation above the surface, serene and complete, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-17. Aurora Nexus - 붕괴

* 파일명: `planet_aurora_nexus_01_collapsed.png`

```text
Transparent PNG sprite, top-down circular planet Aurora Nexus collapsed state, final nexus world with dark frozen aurora oceans, cracked luminous ice, broken cyan core, dim rainbow aurora remnants, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-18. Aurora Nexus - 활성화

* 파일명: `planet_aurora_nexus_02_activated.png`

```text
Transparent PNG sprite, top-down circular planet Aurora Nexus activated state, frozen aurora oceans waking with cyan and lime light, central AI nexus core returning, repaired crystal paths, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-19. Aurora Nexus - 성장

* 파일명: `planet_aurora_nexus_03_growth.png`

```text
Transparent PNG sprite, top-down circular planet Aurora Nexus growth state, bright aurora oceans, green life islands, cyan crystal bridges, gold nexus nodes, visible final-world importance without excessive detail, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### PLANET-20. Aurora Nexus - 완성

* 파일명: `planet_aurora_nexus_04_complete.png`

```text
Transparent PNG sprite, top-down circular planet Aurora Nexus complete state, spectacular restored nexus planet, aurora oceans, green life islands, cyan AI core halo, gold prestige core shining, elegant final MVP completion aura, centered with transparent background, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

## 5. AI 캐릭터 인터페이스

공통 규격:

* 파일 형식: PNG, 투명 배경
* 기본 크기: 1024x1024
* 목적: AI 패널, 이벤트 안내, 진화 선택 화면

### AI-01. 생명형 AI 기본

* 파일명: `ai_life_idle.png`

```text
Transparent PNG portrait sprite, friendly sentient AI hologram core, circular face made of green and cyan light, gentle eye shape, small leaf-like energy fins, warm calm companion personality, readable at small mobile UI size, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, gold rare-resource accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-02. 생명형 AI 기쁨

* 파일명: `ai_life_happy.png`

```text
Transparent PNG portrait sprite, friendly sentient AI hologram core smiling with bright green pulse, soft cyan halo, small life particles, reassuring companion expression, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-03. 생명형 AI 경고

* 파일명: `ai_life_warning.png`

```text
Transparent PNG portrait sprite, life-aligned AI hologram core showing gentle warning, green face with small coral alert rim, concerned but not scary, clear mobile UI expression, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and lime energy accents, coral warning accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-04. 산업형 AI 기본

* 파일명: `ai_industrial_idle.png`

```text
Transparent PNG portrait sprite, efficient industrial sentient AI hologram core, cyan geometric face, precise rectangular light segments, small metallic orbit pieces, calm analytical expression, readable mobile UI companion, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and gold accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-05. 산업형 AI 분석

* 파일명: `ai_industrial_analyze.png`

```text
Transparent PNG portrait sprite, industrial AI hologram core analyzing data, cyan scanning rings, gold micro nodes, focused precise eye, clean mechanical companion personality, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and gold accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-06. 산업형 AI 경고

* 파일명: `ai_industrial_warning.png`

```text
Transparent PNG portrait sprite, industrial AI hologram core giving a cold warning, cyan face with coral diagnostic alert ring, precise angular expression, readable mobile UI, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, cyan and coral accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-07. 전투형 AI 기본

* 파일명: `ai_combat_idle.png`

```text
Transparent PNG portrait sprite, aggressive combat sentient AI hologram core, coral magenta face, sharp triangular eye, cyan inner core, angular armor-like energy fins, risk-seeking personality, readable mobile UI companion, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, coral and cyan accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-08. 전투형 AI 흥분

* 파일명: `ai_combat_excited.png`

```text
Transparent PNG portrait sprite, combat AI hologram core excited by high-risk reward, sharp coral energy flare, cyan core eye, bold angular expression, energetic but friendly enough for mobile game, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, coral and cyan accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

### AI-09. 전투형 AI 경고

* 파일명: `ai_combat_warning.png`

```text
Transparent PNG portrait sprite, combat AI hologram core warning about danger, intense coral alert ring, sharp cyan eye, aggressive but not horror, readable mobile UI expression, no text, 2D top-down mobile game art, original cartoon neon sci-fi style, bright saturated colors, clean readable silhouettes, high contrast, crisp vector-like raster illustration, soft bloom, coral and cyan accents, polished indie mobile game quality, centered composition, no copyrighted characters, no brand logos, no photorealism, no gritty realism
```

## 6. 자원/재화 아이콘

공통 규격:

* 파일 형식: PNG, 투명 배경
* 기본 크기: 512x512

### RES-01. 에너지

* 파일명: `icon_resource_energy.png`

```text
Transparent PNG mobile game resource icon, cyan energy crystal flame, simple strong silhouette, bright inner glow, readable at 32 pixels, no text, no number, 2D cartoon neon sci-fi icon, high contrast, polished mobile UI, centered, no brand logo
```

### RES-02. 바이오매스

* 파일명: `icon_resource_biomass.png`

```text
Transparent PNG mobile game resource icon, green living biomass seed pod with cyan circuit veins, soft glow, simple readable silhouette at 32 pixels, no text, no number, 2D cartoon neon sci-fi icon, high contrast, polished mobile UI, centered, no brand logo
```

### RES-03. AI 데이터

* 파일명: `icon_resource_ai_data.png`

```text
Transparent PNG mobile game resource icon, small holographic data cube with cyan and gold pixels, AI memory shard, readable at 32 pixels, no text, no number, 2D cartoon neon sci-fi icon, high contrast, polished mobile UI, centered, no brand logo
```

### RES-04. Prestige 코어

* 파일명: `icon_resource_prestige_core.png`

```text
Transparent PNG mobile game prestige currency icon, rare gold and cyan AI core gem, small galaxy swirl inside, premium but not casino-like, readable at 32 pixels, no text, no number, 2D cartoon neon sci-fi icon, high contrast, polished mobile UI, centered, no brand logo
```

## 7. 하단 메뉴/기능 아이콘

공통 규격: 512x512 PNG, 투명 배경, 단색 실루엣 + 네온 포인트.

| 파일명 | 프롬프트 |
|---|---|
| `icon_nav_upgrade.png` | `Transparent PNG icon, mobile game upgrade button, upward arrow fused with small gear, cyan neon line art, simple readable silhouette, no text, 2D cartoon sci-fi UI icon` |
| `icon_nav_travel.png` | `Transparent PNG icon, mobile game travel button, small planet with warp arrow orbit, cyan and gold neon line art, simple readable silhouette, no text, 2D cartoon sci-fi UI icon` |
| `icon_nav_ai.png` | `Transparent PNG icon, mobile game AI button, circular hologram face core, cyan neon line art, simple readable silhouette, no text, 2D cartoon sci-fi UI icon` |
| `icon_nav_event.png` | `Transparent PNG icon, mobile game event button, alert diamond with small signal wave, coral and cyan neon line art, simple readable silhouette, no text, 2D cartoon sci-fi UI icon` |
| `icon_nav_shop.png` | `Transparent PNG icon, mobile game shop button, small crate with star core, cyan and gold neon line art, simple readable silhouette, no text, 2D cartoon sci-fi UI icon` |

## 8. 업그레이드 아이콘 20종

공통 규격: 512x512 PNG, 투명 배경, 32px에서도 읽히는 단순한 상징.

| 파일명 | 프롬프트 |
|---|---|
| `icon_upgrade_solar_lattice.png` | `Transparent PNG upgrade icon, solar lattice panel collecting cyan energy, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_pulse_drill.png` | `Transparent PNG upgrade icon, compact pulse drill emitting cyan beam into planet crust, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_ion_roots.png` | `Transparent PNG upgrade icon, glowing green roots with cyan ion veins, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_memory_leaf.png` | `Transparent PNG upgrade icon, leaf shaped like AI circuit memory shard, green and gold, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_tap_resonator.png` | `Transparent PNG upgrade icon, circular tap pulse resonator ring with cyan shockwave, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_nano_weather.png` | `Transparent PNG upgrade icon, tiny cloud of nanobots raining green restoration particles, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_sleep_protocol.png` | `Transparent PNG upgrade icon, crescent moon around small AI core, offline reward symbol, clean mobile UI, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_fusion_petal.png` | `Transparent PNG upgrade icon, glowing petal fused with energy reactor, green and cyan, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_biosphere_mesh.png` | `Transparent PNG upgrade icon, hexagonal biosphere mesh dome with green nodes, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_oracle_cache.png` | `Transparent PNG upgrade icon, gold data cache cube with cyan eye, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_core_overclock.png` | `Transparent PNG upgrade icon, AI core with speed ring and cyan sparks, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_healing_orbit.png` | `Transparent PNG upgrade icon, green orbit ring healing cracked mini planet, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_drone_swarm.png` | `Transparent PNG upgrade icon, three small drones orbiting cyan core, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_seed_vault.png` | `Transparent PNG upgrade icon, secure seed vault capsule glowing green, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_sentience_bus.png` | `Transparent PNG upgrade icon, AI data bus with linked cyan nodes, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_warp_calibrator.png` | `Transparent PNG upgrade icon, warp compass ring with gold star, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_quiet_factory.png` | `Transparent PNG upgrade icon, compact silent factory with cyan smoke-free glow, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_risk_reactor.png` | `Transparent PNG upgrade icon, unstable reactor with coral warning ring and cyan core, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_garden_mind.png` | `Transparent PNG upgrade icon, AI core sprouting green garden leaves, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |
| `icon_upgrade_nexus_dream.png` | `Transparent PNG upgrade icon, dreamlike nexus AI core with gold and cyan constellation, clean mobile UI symbol, no text, 2D cartoon neon sci-fi` |

## 9. 랜덤 이벤트 이미지 10종

공통 규격: 1024x768 또는 1024x1024 PNG, 패널 일러스트용.

| 파일명 | 프롬프트 |
|---|---|
| `event_collapse_warning.png` | `Event panel illustration, cracked planet surface spreading coral warning fissures under a cyan AI scan, tense but readable, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_alien_signal.png` | `Event panel illustration, mysterious alien signal waveform rising from the dark side of a small planet, cyan and gold hologram, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_ancient_structure.png` | `Event panel illustration, buried ancient tower waking under neon dust, cyan lines lighting up, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_resource_surge.png` | `Event panel illustration, orbital collector overloaded with bright cyan energy surge, gold sparks, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_ai_error.png` | `Event panel illustration, AI core caught in a looping hologram glitch, coral warning fragments and cyan memory shards, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_unknown_life.png` | `Event panel illustration, tiny unknown organisms gathering near warm glowing vein on restored planet soil, green and cyan, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_warp_echo.png` | `Event panel illustration, temporary future warp route opening in space, cyan tunnel path and gold destination star, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_meteor_garden.png` | `Event panel illustration, meteor shower planting glowing green and gold minerals into a small planet, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_core_dream.png` | `Event panel illustration, AI core dreaming of a previous universe, soft cyan memory planets and green rebirth aura, no text, 2D cartoon neon sci-fi mobile game art` |
| `event_silent_orbit.png` | `Event panel illustration, silent moon mirroring AI commands with twin cyan rings, eerie but not horror, no text, 2D cartoon neon sci-fi mobile game art` |

## 10. 진화 선택 카드 이미지

공통 규격: 1024x1400 PNG, 카드 배경용. 카드 제목/설명은 후편집.

### EVO-01. 생명형 카드

* 파일명: `card_evolution_life.png`

```text
Vertical evolution choice card art, Life Protocol, AI core connecting to green restored roots and soft biosphere halo, stable growth, gentle companion feeling, empty top area for manual title, no generated text, 2D cartoon neon sci-fi mobile game art, bright green and cyan accents, polished UI card background
```

### EVO-02. 산업형 카드

* 파일명: `card_evolution_industrial.png`

```text
Vertical evolution choice card art, Industrial Protocol, AI core powering clean geometric production grid, cyan circuits, gold efficiency nodes, precise and calm, empty top area for manual title, no generated text, 2D cartoon neon sci-fi mobile game art, polished UI card background
```

### EVO-03. 전투형 카드

* 파일명: `card_evolution_combat.png`

```text
Vertical evolution choice card art, Combat Protocol, AI core overclocking a risky reactor, coral energy arcs, cyan inner core, high reward and danger, exciting but not violent, empty top area for manual title, no generated text, 2D cartoon neon sci-fi mobile game art, polished UI card background
```

## 11. 광고 보상/상점/IAP 이미지

공통 규격: 1024x1024 PNG, 투명 배경 또는 패널 배경.

| 파일명 | 프롬프트 |
|---|---|
| `reward_ad_production_boost.png` | `Transparent PNG reward icon, cyan production boost reactor with x2-like twin energy streams but no numbers or text, polished mobile game reward art, 2D cartoon neon sci-fi` |
| `reward_ad_offline_double.png` | `Transparent PNG reward icon, sleeping AI core under crescent orbit with doubled energy trail implied by two parallel cyan streams, no text, 2D cartoon neon sci-fi` |
| `reward_ad_event_bonus.png` | `Transparent PNG reward icon, event signal chest with gold and cyan reward burst, no text, polished mobile game reward art, 2D cartoon neon sci-fi` |
| `reward_ad_restore_speed.png` | `Transparent PNG reward icon, fast restoration ring around cracked mini planet, green and cyan speed lines, no text, 2D cartoon neon sci-fi` |
| `reward_ad_free_chest.png` | `Transparent PNG reward icon, free reward chest with AI core gem, cyan and gold glow, no text, no currency symbol, 2D cartoon neon sci-fi` |
| `iap_remove_ads.png` | `Transparent PNG shop icon, clean shield protecting a small AI core from interruption symbols, no text, no ad logo, 2D cartoon neon sci-fi` |
| `iap_starter_pack.png` | `Transparent PNG shop bundle icon, compact starter crate with energy crystal, biomass pod, AI data cube, no text, 2D cartoon neon sci-fi` |
| `iap_ai_upgrade_pack.png` | `Transparent PNG shop bundle icon, AI hologram core with cosmetic upgrade halo and small data shards, no text, 2D cartoon neon sci-fi` |
| `iap_production_pack.png` | `Transparent PNG shop bundle icon, efficient reactor pack with cyan energy streams and gold nodes, no text, 2D cartoon neon sci-fi` |
| `iap_planet_skin_pack.png` | `Transparent PNG shop bundle icon, three mini planet skins fanned like cards, green cyan gold, no text, 2D cartoon neon sci-fi` |
| `iap_galaxy_pass.png` | `Transparent PNG shop bundle icon, premium galaxy pass emblem with star map and prestige core, no text, no ticket text, 2D cartoon neon sci-fi` |

## 12. 이펙트 스프라이트 시트

공통 규격:

* 파일 형식: PNG, 투명 배경
* 권장: 4x4 또는 8x4 프레임 시트
* 주의: 엔진에서 애니메이션으로 사용할 수 있도록 배경은 완전 투명하게 유지한다.

### FX-01. 에너지 생산 파티클

* 파일명: `fx_energy_particles_sheet.png`

```text
Transparent PNG sprite sheet, 16 frames in a 4 by 4 grid, cyan energy particles spawning from a planet and moving upward, small glowing orbs, clean separated frames, no background, no text, 2D cartoon neon sci-fi game effect
```

### FX-02. 행성 복원 링

* 파일명: `fx_restore_ring_sheet.png`

```text
Transparent PNG sprite sheet, 16 frames in a 4 by 4 grid, green and cyan restoration ring expanding outward, cracks filling with light, clean readable mobile effect, no background, no text, 2D cartoon neon sci-fi game effect
```

### FX-03. 업그레이드 완료 플래시

* 파일명: `fx_upgrade_flash_sheet.png`

```text
Transparent PNG sprite sheet, 16 frames in a 4 by 4 grid, small cyan gold UI upgrade flash burst, radial lines and sparkles, designed for button feedback, no background, no text, 2D cartoon neon sci-fi game effect
```

### FX-04. 워프 전환

* 파일명: `fx_warp_transition_sheet.png`

```text
Transparent PNG sprite sheet, 24 frames in a 6 by 4 grid, cyan warp tunnel ring contracting then expanding, star streaks, bright center, mobile readable, no background, no text, 2D cartoon neon sci-fi game effect
```

### FX-05. 진화 선택 빔

* 파일명: `fx_evolution_beam_sheet.png`

```text
Transparent PNG sprite sheet, 16 frames in a 4 by 4 grid, three-color evolution beam connecting AI core to planet, green cyan coral variants in the same sheet, clean separated frames, no background, no text, 2D cartoon neon sci-fi game effect
```

### FX-06. 랜덤 이벤트 경고

* 파일명: `fx_event_warning_pulse_sheet.png`

```text
Transparent PNG sprite sheet, 16 frames in a 4 by 4 grid, coral warning pulse ring with cyan scan line, alert effect for random event panel, no background, no text, no warning letters, 2D cartoon neon sci-fi game effect
```

## 13. 은하 노드/맵 소품

공통 규격: 512x512 PNG, 투명 배경.

| 파일명 | 프롬프트 |
|---|---|
| `map_node_locked_planet.png` | `Transparent PNG galaxy map node, locked dark planet dot with faint cyan outline, no lock text, no number, 2D cartoon neon sci-fi UI` |
| `map_node_current_planet.png` | `Transparent PNG galaxy map node, current planet marker with bright cyan ring and small green pulse, no text, no number, 2D cartoon neon sci-fi UI` |
| `map_node_complete_planet.png` | `Transparent PNG galaxy map node, completed planet marker with green aura and gold check-like star shape but no checkmark text, 2D cartoon neon sci-fi UI` |
| `map_warp_path_segment.png` | `Transparent PNG map path segment, cyan dotted warp line with soft glow, horizontal, no text, 2D cartoon neon sci-fi UI` |

## 14. UI 패널/버튼 텍스처

공통 규격: 1024x1024 또는 2048x2048, 9-slice 편집 가능하게 단순 패턴.

### UI-01. 기본 패널 텍스처

* 파일명: `ui_panel_neon_dark_1024.png`

```text
Square seamless UI panel texture for mobile idle game, dark navy translucent sci-fi surface, subtle cyan edge glow, very low detail center for text readability, no text, no symbols, no logo, 2D cartoon neon sci-fi UI material
```

### UI-02. 활성 버튼 텍스처

* 파일명: `ui_button_active_1024.png`

```text
Square seamless UI button texture, dark cyan sci-fi glass, bright cyan border glow, subtle inner highlight, clean and readable for mobile UI, no text, no symbols, no logo, 2D cartoon neon sci-fi UI material
```

### UI-03. 비활성 버튼 텍스처

* 파일명: `ui_button_disabled_1024.png`

```text
Square seamless UI button texture, muted dark navy sci-fi glass, low cyan edge, disabled state, clean mobile UI, no text, no symbols, no logo, 2D cartoon neon sci-fi UI material
```

## 15. 로딩/빈 상태/오류 이미지

### STATE-01. 로딩 코어

* 파일명: `state_loading_core.png`

```text
Transparent PNG loading illustration, small AI core charging with rotating cyan dots, friendly mobile game loading symbol, no text, no numbers, 2D cartoon neon sci-fi UI icon
```

### STATE-02. 저장 복구 안내 이미지

* 파일명: `state_save_recovery.png`

```text
Transparent PNG UI illustration, AI core repairing a cracked data cube with cyan light, calm recovery feeling, no text, no warning letters, 2D cartoon neon sci-fi mobile game art
```

### STATE-03. 광고 사용 불가 안내 이미지

* 파일명: `state_ad_unavailable.png`

```text
Transparent PNG UI illustration, reward chest temporarily closed with soft cyan shield, calm unavailable state, no text, no ad logo, no store badge, 2D cartoon neon sci-fi mobile game art
```

## 16. 제작 우선순위

### MVP 필수

1. `gp_app_icon_512.png`
2. `gp_feature_graphic_1024x500.png`
3. 행성 5개 x 4단계, 총 20개
4. AI 기본 3종: `ai_life_idle.png`, `ai_industrial_idle.png`, `ai_combat_idle.png`
5. 자원 아이콘 4개
6. 하단 메뉴 아이콘 5개
7. 업그레이드 아이콘 20개
8. 이벤트 이미지 10개
9. 기본 이펙트 6개
10. 실제 앱 캡처 기반 Google Play 스크린샷 최소 3장

### 출시 완성도 향상

1. 스토어 스크린샷 배경 프레임 4장
2. AI 표정 변형 6장
3. 진화 카드 3장
4. 상점/IAP/광고 보상 이미지 11장
5. YouTube 프리뷰 영상 썸네일

## 17. 검수 체크리스트

* 축소했을 때 행성 4단계가 확실히 구분되는가?
* AI 성향 3종이 색, 실루엣, 표정으로 구분되는가?
* 아이콘은 32px에서도 무엇인지 대략 알아볼 수 있는가?
* 모든 Play 제출 이미지는 텍스트, 배지, 로고, 순위/할인 표현이 없는가?
* 스토어 이미지가 실제 게임 화면과 과도하게 다르지 않은가?
* 배경이 UI 가독성을 해치지 않는가?
* 네온 효과가 과해서 숫자와 버튼을 가리지 않는가?
* 동일 팔레트와 동일 렌더링 질감이 유지되는가?

## 18. 공식 규격 참고

확인일: 2026-05-06

* Google Play preview assets: https://support.google.com/googleplay/android-developer/answer/9866151
* Google Play icon design specifications: https://developer.android.com/distribute/google-play/resources/icon-design-specifications

