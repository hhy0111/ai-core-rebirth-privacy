# 2026-05-10 추가 이미지 프롬프트: 행성 스킨 패키지

이 파일은 기존 프롬프트 파일을 유지하고, `planet_skin_pack` 구매 후 적용할 신규 스킨 이미지만 분리한 추가 프롬프트입니다.

생성 후 Android 리소스에 넣을 권장 위치:

`app/src/main/res/drawable-nodpi/`

코드는 아래 파일명이 존재하면 자동으로 해당 이미지를 사용하고, 없으면 절차적 오라 효과로 대체합니다.

## 공통 조건

- 출력: PNG
- 크기: 1024x1024
- 배경: 투명 배경
- 구도: 중앙에 원형 행성 1개, UI 텍스트 없음
- 스타일: 2D top-down, cartoon, neon sci-fi, bright vivid color, high quality mobile game asset
- 테두리: 모바일 화면에서 잘 보이는 선명한 외곽광
- 금지: 글자, 로고, 워터마크, UI 버튼, 프레임, 어두운 실사풍, 과도한 배경

## 1. 네온 생명 스킨

파일명:

`planet_skin_life_neon.png`

프롬프트:

```
transparent background, single circular restored alien planet, top-down 2D cartoon neon sci-fi mobile game asset, vivid emerald green and cyan biome glow, lush glowing forests, small luminous rivers, soft pollen particles, friendly life-energy aura rings, clean readable silhouette, high contrast rim light, no text, no UI, no logo, no frame, centered composition, polished casual idle game quality
```

## 2. 산업 코어 스킨

파일명:

`planet_skin_industrial_forge.png`

프롬프트:

```
transparent background, single circular restored machine planet, top-down 2D cartoon neon sci-fi mobile game asset, golden industrial core circuits, teal metal continents, small orbital factory nodes embedded on surface, bright energy conduits, rotating mechanical ring impression, clean readable silhouette, high contrast rim light, no text, no UI, no logo, no frame, centered composition, polished casual idle game quality
```

## 3. 전투 균열 스킨

파일명:

`planet_skin_combat_void.png`

프롬프트:

```
transparent background, single circular battle-evolved alien planet, top-down 2D cartoon neon sci-fi mobile game asset, violet void crust, red magenta shield cracks, controlled plasma fissures, sharp defensive energy arcs, dangerous but premium-looking, clean readable silhouette, high contrast rim light, no text, no UI, no logo, no frame, centered composition, polished casual idle game quality
```

## 적용 후 확인 기준

- `planet_skin_pack` 구매 후 상점의 행성 스킨 패키지 행을 탭하면 스킨이 순환됩니다.
- 위 PNG 파일이 없을 때도 색상 오라와 입자 효과가 적용됩니다.
- 위 PNG 파일을 추가하면 메인 행성 렌더링에서 해당 이미지가 기본 행성 이미지보다 우선 표시됩니다.
