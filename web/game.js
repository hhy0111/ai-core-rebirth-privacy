(() => {
  'use strict';

  const canvas = document.getElementById('game');
  const ctx = canvas.getContext('2d');
  const W = canvas.width;
  const H = canvas.height;
  const saveKey = 'ai-core-rebirth-web-save-v1';

  if (new URLSearchParams(window.location.search).get('reset') === '1') {
    localStorage.removeItem(saveKey);
    window.history.replaceState(null, '', window.location.pathname);
  }

  const panels = ['UPGRADE', 'TRAVEL', 'AI', 'EVENT', 'SHOP'];
  const panelLabels = { UPGRADE: '업그레이드', TRAVEL: '이동', AI: 'AI', EVENT: '이벤트', SHOP: '상점' };
  const panelAccents = { UPGRADE: '#62f2ff', TRAVEL: '#ffd75a', AI: '#72f580', EVENT: '#ff5f73', SHOP: '#b68cff' };
  const panelDescriptions = {
    UPGRADE: '자원 생산과 복원 속도를 올려 행성을 빠르게 되살립니다.',
    TRAVEL: '행성을 완성하고 이동 비용을 모아 다음 행성으로 워프합니다.',
    AI: '행성마다 한 번, AI 성향을 선택해 플레이 방향을 바꿉니다.',
    EVENT: '이상 현상을 해석하고 보상과 리스크 중 하나를 선택합니다.',
    SHOP: '보상형 광고와 결제 테스트 상품으로 시간을 단축합니다.',
  };
  const effectColors = {
    ENERGY: '#62f2ff',
    BIOMASS: '#72f580',
    AI_DATA: '#ffd75a',
    TAP: '#ffffff',
    RESTORE: '#a0f6ff',
    OFFLINE: '#b68cff',
  };
  const stages = ['붕괴', '활성화', '성장', '완성'];
  const stageAssetNames = ['planet_collapsed.png', 'planet_activated.png', 'planet_growth.png', 'planet_complete.png'];
  const alignmentLabels = { Life: '생명형', Industrial: '산업형', Combat: '전투형' };
  const aiAssetNames = { Life: 'ai_companion_life.png', Industrial: 'ai_companion_industrial.png', Combat: 'ai_companion_combat.png' };
  const alignments = ['Life', 'Industrial', 'Combat'];
  const tutorialVersion = 2;
  const minTravelMs = 7 * 24 * 60 * 60 * 1000;

  const planets = [
    { id: 'ash_prime', name: '잿빛 프라임', theme: '청록빛 강이 잠든 갈라진 현무암 행성', base: { energy: 1.2, biomass: 0.03, aiData: 0.01 }, travel: { energy: 180000, biomass: 3500, aiData: 1100 }, thresholds: [80, 260, 620] },
    { id: 'verdant_zero', name: '녹원 제로', theme: '네온 정글이 되살아나는 복원 행성', base: { energy: 2.7, biomass: 0.14, aiData: 0.025 }, travel: { energy: 520000, biomass: 12000, aiData: 4200 }, thresholds: [260, 850, 2050] },
    { id: 'foundry_moon', name: '주조 위성', theme: '용융 자원맥을 품은 산업 고리 위성', base: { energy: 6, biomass: 0.23, aiData: 0.065 }, travel: { energy: 1800000, biomass: 35000, aiData: 15000 }, thresholds: [850, 2900, 7200] },
    { id: 'echo_ruin', name: '메아리 유적', theme: '부서진 신호 배열로 뒤덮인 고대 행성', base: { energy: 14, biomass: 0.42, aiData: 0.18 }, travel: { energy: 6200000, biomass: 98000, aiData: 52000 }, thresholds: [2800, 9800, 23000] },
    { id: 'aurora_nexus', name: '오로라 넥서스', theme: '오로라 바다를 품은 연결 행성', base: { energy: 34, biomass: 0.9, aiData: 0.52 }, travel: { energy: 22000000, biomass: 260000, aiData: 140000 }, thresholds: [8800, 31000, 76000] },
    { id: 'glass_nebula', name: '유리 성운', theme: '투명한 대륙 아래 별빛 수로가 흐르는 행성', base: { energy: 82, biomass: 1.8, aiData: 1.25 }, travel: { energy: 76000000, biomass: 680000, aiData: 380000 }, thresholds: [26000, 92000, 215000] },
    { id: 'abyss_station', name: '심연 정거장', theme: '버려진 궤도 정거장과 바다 행성이 맞물린 구역', base: { energy: 195, biomass: 3.5, aiData: 2.8 }, travel: { energy: 260000000, biomass: 1700000, aiData: 900000 }, thresholds: [76000, 260000, 640000] },
    { id: 'lumina_desert', name: '루미나 사막', theme: '빛나는 모래 폭풍과 매장된 태양판의 사막 행성', base: { energy: 460, biomass: 6.4, aiData: 6.2 }, travel: { energy: 880000000, biomass: 4200000, aiData: 2200000 }, thresholds: [210000, 720000, 1800000] },
    { id: 'titan_greenhouse', name: '타이탄 온실', theme: '거대 생체 돔이 대기권을 되살리는 중력 행성', base: { energy: 1080, biomass: 12.5, aiData: 13.5 }, travel: { energy: 2900000000, biomass: 9800000, aiData: 5100000 }, thresholds: [620000, 2100000, 5200000] },
    { id: 'chrono_ruin', name: '크로노 폐허', theme: '시간 균열 사이에 고대 도시가 반복되는 유적 행성', base: { energy: 2500, biomass: 24, aiData: 31 }, travel: { energy: 9200000000, biomass: 22500000, aiData: 12000000 }, thresholds: [1800000, 6200000, 15000000] },
    { id: 'sanctum_ring', name: '신성 고리', theme: '행성보다 큰 복원 고리가 궤도를 봉합하는 성역', base: { energy: 5800, biomass: 48, aiData: 70 }, travel: { energy: 28000000000, biomass: 52000000, aiData: 27000000 }, thresholds: [5200000, 18000000, 43000000] },
    { id: 'eternal_core', name: '이터널 코어', theme: '멸망 에너지의 중심에 남은 최종 감정 코어', base: { energy: 13500, biomass: 95, aiData: 160 }, travel: { energy: 0, biomass: 0, aiData: 0 }, thresholds: [15000000, 52000000, 125000000] },
  ];
  const planetBackgroundNames = planets.map((planet) => `bg_planet_${planet.id}.png`);

  const upgrades = [
    ['solar_lattice', '태양 격자', 'ENERGY', { energy: 15 }, 1.18, 0.12, 40],
    ['pulse_drill', '펄스 드릴', 'ENERGY', { energy: 55 }, 1.2, 0.18, 35],
    ['ion_roots', '이온 뿌리', 'BIOMASS', { energy: 120, biomass: 2 }, 1.22, 0.2, 30],
    ['memory_leaf', '기억 잎', 'AI_DATA', { energy: 180, aiData: 1 }, 1.24, 0.18, 30],
    ['tap_resonator', '탭 공명기', 'TAP', { energy: 90 }, 1.2, 0.22, 30],
    ['nano_weather', '나노 기상', 'RESTORE', { energy: 220, biomass: 4 }, 1.21, 0.16, 30],
    ['sleep_protocol', '수면 프로토콜', 'OFFLINE', { energy: 350, aiData: 2 }, 1.25, 0.1, 20],
    ['fusion_petal', '융합 꽃잎', 'ENERGY', { energy: 480, biomass: 8 }, 1.23, 0.2, 25],
    ['biosphere_mesh', '생물권 망', 'BIOMASS', { energy: 700, biomass: 12 }, 1.24, 0.22, 25],
    ['oracle_cache', '예언 캐시', 'AI_DATA', { energy: 950, aiData: 8 }, 1.25, 0.22, 25],
    ['core_overclock', '코어 오버클럭', 'ENERGY', { energy: 1400, aiData: 10 }, 1.27, 0.24, 20],
    ['healing_orbit', '치유 궤도', 'RESTORE', { energy: 1900, biomass: 28 }, 1.27, 0.24, 20],
    ['drone_swarm', '드론 군집', 'ENERGY', { energy: 2600, biomass: 35 }, 1.28, 0.26, 20],
    ['seed_vault', '씨앗 금고', 'BIOMASS', { energy: 3400, biomass: 48 }, 1.28, 0.26, 20],
    ['sentience_bus', '지성 버스', 'AI_DATA', { energy: 4400, aiData: 32 }, 1.29, 0.28, 20],
    ['warp_calibrator', '워프 보정기', 'RESTORE', { energy: 5600, aiData: 42 }, 1.3, 0.28, 18],
    ['quiet_factory', '정숙 공장', 'OFFLINE', { energy: 7200, biomass: 80 }, 1.31, 0.16, 18],
    ['risk_reactor', '위험 반응로', 'ENERGY', { energy: 9200, aiData: 60 }, 1.32, 0.34, 16],
    ['garden_mind', '정원 지성', 'BIOMASS', { energy: 11800, biomass: 120 }, 1.33, 0.34, 16],
    ['nexus_dream', '넥서스 꿈', 'AI_DATA', { energy: 15000, aiData: 120 }, 1.35, 0.4, 12],
    ['starlight_condenser', '별빛 응축기', 'ENERGY', { energy: 22000, aiData: 180 }, 1.36, 0.42, 12, 'core_overclock'],
    ['glass_bloom', '유리 꽃망', 'BIOMASS', { energy: 26000, biomass: 260 }, 1.35, 0.38, 12, 'garden_mind'],
    ['emotion_archive', '감정 기록소', 'AI_DATA', { energy: 32000, aiData: 220 }, 1.36, 0.42, 12, 'nexus_dream'],
    ['nebula_repair', '성운 수복기', 'RESTORE', { energy: 42000, biomass: 360 }, 1.34, 0.34, 12, 'warp_calibrator'],
    ['abyss_turbine', '심연 터빈', 'ENERGY', { energy: 64000, biomass: 520 }, 1.37, 0.46, 12, 'drone_swarm'],
    ['tidal_seed_bank', '조수 씨앗고', 'BIOMASS', { energy: 78000, biomass: 760 }, 1.36, 0.42, 12, 'seed_vault'],
    ['signal_bathysphere', '신호 잠항구', 'AI_DATA', { energy: 96000, aiData: 520 }, 1.37, 0.48, 12, 'sentience_bus'],
    ['pressure_protocol', '압력 프로토콜', 'OFFLINE', { energy: 120000, aiData: 700 }, 1.35, 0.22, 10, 'sleep_protocol'],
    ['solar_mirage', '태양 신기루', 'ENERGY', { energy: 170000, aiData: 900 }, 1.38, 0.52, 12, 'risk_reactor'],
    ['dune_roots', '사막 뿌리망', 'BIOMASS', { energy: 210000, biomass: 1400 }, 1.37, 0.48, 12, 'ion_roots'],
    ['prism_memory', '프리즘 기억', 'AI_DATA', { energy: 260000, aiData: 1250 }, 1.38, 0.54, 12, 'oracle_cache'],
    ['heat_suture', '열 봉합기', 'RESTORE', { energy: 330000, biomass: 1800 }, 1.36, 0.42, 12, 'healing_orbit'],
    ['gravity_orchard', '중력 과수원', 'BIOMASS', { energy: 460000, biomass: 2800 }, 1.39, 0.56, 12, 'fusion_petal'],
    ['dome_reactor', '돔 반응로', 'ENERGY', { energy: 580000, aiData: 2300 }, 1.39, 0.58, 12, 'solar_lattice'],
    ['symbiosis_bus', '공생 버스', 'AI_DATA', { energy: 720000, aiData: 3100 }, 1.4, 0.6, 12, 'memory_leaf'],
    ['greenhouse_restorer', '온실 복원자', 'RESTORE', { energy: 900000, biomass: 3600 }, 1.38, 0.5, 12, 'nano_weather'],
    ['chrono_battery', '시간 전지', 'ENERGY', { energy: 1300000, aiData: 5200 }, 1.41, 0.64, 12, 'core_overclock'],
    ['loop_composter', '순환 퇴비장', 'BIOMASS', { energy: 1650000, biomass: 6200 }, 1.4, 0.62, 12, 'biosphere_mesh'],
    ['ruin_oracle', '폐허 예언기', 'AI_DATA', { energy: 2050000, aiData: 7600 }, 1.42, 0.68, 12, 'oracle_cache'],
    ['time_latch', '시간 걸쇠', 'OFFLINE', { energy: 2600000, aiData: 9200 }, 1.39, 0.28, 10, 'quiet_factory'],
    ['ring_harvester', '고리 수확기', 'ENERGY', { energy: 3800000, biomass: 11000 }, 1.43, 0.72, 12, 'drone_swarm'],
    ['sanctum_garden', '성역 정원', 'BIOMASS', { energy: 4800000, biomass: 15000 }, 1.42, 0.7, 12, 'garden_mind'],
    ['choir_matrix', '합창 매트릭스', 'AI_DATA', { energy: 6100000, aiData: 18000 }, 1.43, 0.74, 12, 'sentience_bus'],
    ['orbit_mender', '궤도 봉합자', 'RESTORE', { energy: 7800000, aiData: 22000 }, 1.41, 0.58, 12, 'warp_calibrator'],
    ['eternal_furnace', '영원 화로', 'ENERGY', { energy: 11500000, aiData: 35000 }, 1.45, 0.82, 12, 'risk_reactor'],
    ['origin_grove', '기원 수림', 'BIOMASS', { energy: 14500000, biomass: 26000 }, 1.44, 0.78, 12, 'seed_vault'],
    ['sentient_singularity', '감정 특이점', 'AI_DATA', { energy: 18500000, aiData: 42000 }, 1.45, 0.86, 12, 'nexus_dream'],
    ['rebirth_engine', '재탄생 엔진', 'RESTORE', { energy: 24000000, biomass: 32000, aiData: 52000 }, 1.43, 0.68, 12, 'healing_orbit'],
  ].map(([id, name, effect, baseCost, scale, value, max, icon]) => ({ id, name, effect, baseCost, scale, value, max, icon: icon || id }));

  const evolutions = [
    { alignment: 'Life', title: '생명 프로토콜', reward: '바이오매스 +20%, 오프라인 +10%' },
    { alignment: 'Industrial', title: '산업 프로토콜', reward: '에너지 생산 +25%' },
    { alignment: 'Combat', title: '전투 프로토콜', reward: '터치 +35%, AI 데이터 +15%' },
  ];

  const randomEvents = [
    { id: 'collapse_warning', title: '붕괴 경고', body: '코어 아래 균열선이 빠르게 번지고 있다.', options: [{ label: '안정화', align: 'Life', reward: { biomass: 5 }, progress: 35 }, { label: '열 회수', align: 'Industrial', reward: { energy: 120 }, progress: -15 }] },
    { id: 'alien_signal', title: '외계 신호', body: '행성의 어두운 면에서 약한 패턴이 반복된다.', options: [{ label: '해독', align: 'Life', reward: { aiData: 5 }, progress: 20 }, { label: '활용', align: 'Industrial', reward: { energy: 180 }, progress: 0 }] },
    { id: 'ancient_structure', title: '고대 구조물', body: '네온 먼지 아래 묻혀 있던 탑이 깨어난다.', options: [{ label: '보존', align: 'Life', reward: { biomass: 8, aiData: 2 }, progress: 30 }, { label: '과부하', align: 'Combat', reward: { energy: 260, aiData: 4 }, progress: -25 }] },
    { id: 'resource_surge', title: '자원 폭주', body: '궤도 수집기에 에너지가 밀려든다.', options: [{ label: '조절', align: 'Industrial', reward: { energy: 240 }, progress: 10 }, { label: '돌파', align: 'Combat', reward: { energy: 420 }, progress: -35 }] },
    { id: 'ai_error', title: 'AI 오류', body: '메모리 루프가 불가능한 선택을 제안한다.', options: [{ label: '자가 점검', align: 'Life', reward: { aiData: 4 }, progress: 25 }, { label: '강제 확정', align: 'Combat', reward: { aiData: 8 }, progress: -20 }] },
    { id: 'unknown_life', title: '미확인 생명체', body: '작은 생명체들이 따뜻한 광맥 주변에 모인다.', options: [{ label: '보호', align: 'Life', reward: { biomass: 16 }, progress: 45 }, { label: '채집', align: 'Industrial', reward: { aiData: 6 }, progress: 5 }] },
    { id: 'warp_echo', title: '워프 메아리', body: '미래 항로가 3초 동안 나타난다.', options: [{ label: '지도화', align: 'Industrial', reward: { aiData: 7 }, progress: 20 }, { label: '탐사 도약', align: 'Combat', reward: { energy: 520 }, progress: -40 }] },
    { id: 'meteor_garden', title: '유성 정원', body: '유성우가 희귀 광물을 뿌린다.', options: [{ label: '재배', align: 'Life', reward: { biomass: 22 }, progress: 25 }, { label: '노천 채굴', align: 'Industrial', reward: { energy: 580 }, progress: -10 }] },
    { id: 'core_dream', title: '코어의 꿈', body: 'AI가 멸망 이전의 우주를 기억한다.', options: [{ label: '수용', align: 'Life', reward: { aiData: 9 }, progress: 30 }, { label: '무기화', align: 'Combat', reward: { aiData: 14 }, progress: -30 }] },
    { id: 'silent_orbit', title: '침묵 궤도', body: '빈 위성이 모든 명령을 거울처럼 되돌린다.', options: [{ label: '동기화', align: 'Industrial', reward: { energy: 760, aiData: 5 }, progress: 15 }, { label: '거울 파괴', align: 'Combat', reward: { energy: 1100 }, progress: -45 }] },
  ];

  const shopRewards = [
    { label: '광고: 10분간 생산 2배', icon: 'reward_ad_production_boost.png', action: () => { state.adBoostUntil = nowMs() + 10 * 60 * 1000; startScreenTransition('생산 부스트 점화', panelAccents.SHOP); toast('생산 부스트 활성화'); } },
    { label: '광고: 행성 복원 +8%', icon: 'reward_ad_restore_speed.png', action: () => { setCurrentProgress(state.progress + maxProgress() * 0.08); startScreenTransition('복원 파동 적용', panelAccents.SHOP); toast('복원 에너지 적용'); } },
    { label: '광고: 다음 이벤트 2배', icon: 'reward_ad_event_bonus.png', action: () => { state.eventBoosts = Math.min(3, state.eventBoosts + 1); startScreenTransition('이벤트 증폭 저장', panelAccents.SHOP); toast('다음 이벤트 보상 강화'); } },
    { label: '광고: 무료 상자', icon: 'reward_ad_free_chest.png', action: () => { const reward = freeChestReward(); addWallet(reward); startScreenTransition('보상 상자 개방', panelAccents.SHOP); startRewardBurst('무료 상자 획득', walletText(reward)); } },
  ];

  const products = [
    { id: 'remove_ads', title: '광고 제거', price: '₩3,900', icon: 'iap_remove_ads.png', reward: {} },
    { id: 'starter_pack', title: '스타터 패키지', price: '₩1,500', icon: 'iap_starter_pack.png', reward: { energy: 1200, biomass: 45, aiData: 18 }, progress: 160 },
    { id: 'ai_upgrade_pack', title: 'AI 업그레이드 팩', price: '₩4,900', icon: 'iap_ai_upgrade_pack.png', reward: { aiData: 80 } },
    { id: 'production_pack', title: '생산 가속 팩', price: '₩2,200', icon: 'iap_production_pack.png', reward: { energy: 2500, biomass: 80, aiData: 30 }, boost: 3600 },
    { id: 'planet_skin_pack', title: '행성 스킨 팩', price: '₩3,300', icon: 'iap_planet_skin_pack.png', reward: {} },
    { id: 'premium_galaxy_pass', title: '프리미엄 은하 패스', price: '₩9,900', icon: 'iap_galaxy_pass.png', reward: { energy: 4000, biomass: 120, aiData: 70 } },
  ];

  const encounterDefs = [
    { id: 'pass_ship', title: '통과선', asset: 'encounter_pass_ship.png', type: 'energy', seconds: 110 },
    { id: 'space_lifeform', title: '우주 생명체', asset: 'encounter_space_lifeform.png', type: 'biomass', seconds: 150 },
    { id: 'data_meteor', title: '데이터 유성', asset: 'encounter_data_meteor.png', type: 'aiData', seconds: 155, burst: 'fx_rare_reward_stardust.png' },
    { id: 'scout_ship', title: '정찰선', asset: 'encounter_scout_ship.png', type: 'energy', seconds: 90 },
    { id: 'cargo_drone', title: '화물 드론', asset: 'encounter_cargo_drone.png', type: 'mixed', seconds: 120 },
    { id: 'luminous_lifeform', title: '발광 생명체', asset: 'encounter_luminous_lifeform.png', type: 'biomass', seconds: 140 },
    { id: 'data_probe', title: '데이터 탐사체', asset: 'encounter_data_probe.png', type: 'aiData', seconds: 130 },
    { id: 'rare_meteor', title: '희귀 유성', asset: 'encounter_rare_meteor.png', type: 'rare', seconds: 160, burst: 'fx_rare_reward_stardust.png' },
    { id: 'ancient_satellite', title: '고대 위성', asset: 'encounter_ancient_satellite.png', type: 'aiData', seconds: 170 },
    { id: 'seed_swarm', title: '씨앗 군집', asset: 'encounter_seed_swarm.png', type: 'biomass', seconds: 160 },
    { id: 'warp_merchant', title: '워프 상인', asset: 'encounter_warp_merchant.png', type: 'mixed', seconds: 180 },
    { id: 'emotion_memory', title: '감정 기억', asset: 'encounter_emotion_memory.png', type: 'aiData', seconds: 200 },
    { id: 'unstable_rift', title: '불안정 균열', asset: 'encounter_unstable_rift.png', type: 'rift', seconds: 220, burst: 'fx_rift_resolve.png' },
  ];
  const idleVisualAssetNames = [
    'fx_upgrade_orbit_energy.png', 'fx_upgrade_orbit_bio.png', 'fx_upgrade_orbit_ai_data.png',
    'fx_restore_wave.png', 'fx_upgrade_burst.png', 'fx_tap_resonance_core.png',
    'prop_energy_satellite.png', 'prop_bio_seed_drone.png', 'prop_ai_memory_shard.png', 'prop_offline_storage_orb.png',
    'mask_surface_energy_veins.png', 'mask_surface_bio_growth.png', 'mask_surface_ai_city.png',
  ];

  const imageNames = [
    'bg_nebula_main.png', 'fx_energy_ring.png',
    ...planetBackgroundNames,
    ...idleVisualAssetNames,
    ...stageAssetNames,
    ...Object.values(aiAssetNames),
    ...panels.map((p) => `icon_nav_${p.toLowerCase() === 'travel' ? 'travel' : p.toLowerCase()}.png`),
    ...upgrades.map((u) => `icon_upgrade_${u.icon}.png`),
    ...randomEvents.map((e) => `event_${e.id}.png`),
    'map_node_locked_planet.png', 'map_node_current_planet.png', 'map_node_complete_planet.png',
    ...shopRewards.map((r) => r.icon),
    ...products.map((p) => p.icon),
    ...encounterDefs.map((e) => e.asset),
    'fx_encounter_collect_burst.png', 'fx_rare_reward_stardust.png', 'fx_rift_resolve.png',
  ];

  const assets = {};
  let state = loadState();
  let currentPanel = 'UPGRADE';
  let screenMode = 'OPENING';
  let screenElapsedMs = 0;
  let screenTransition = null;
  let targets = [];
  let lastTime = performance.now();
  let toastMessage = '';
  let toastUntil = 0;
  let rewardBurst = null;
  let upgradeBurst = null;
  let incomeFloaters = [];
  let incomeFxMs = 0;
  let incomeFxIndex = 0;
  let passingEncounter = null;
  let encounterBurst = null;
  let encounterSpawnMs = 18000;
  let travelMapScroll = 0;
  let travelMapBounds = null;
  let travelMapDrag = null;

  function defaultState() {
    return {
      planetIndex: 0,
      progress: 0,
      resources: { energy: 20, biomass: 0, aiData: 0 },
      planetProgress: { 0: 0 },
      planetArrivalTimes: { 0: Date.now() },
      upgrades: {},
      alignment: 'Life',
      scores: { Life: 0, Industrial: 0, Combat: 0 },
      chosenPlanets: [],
      completed: 0,
      prestige: 0,
      activeEvent: null,
      eventBoosts: 0,
      adBoostUntil: 0,
      owned: [],
      tutorialVersion: 0,
      lastSaved: Date.now(),
    };
  }

  function loadState() {
    try {
      const parsed = JSON.parse(localStorage.getItem(saveKey));
      return normalizeState({ ...defaultState(), ...parsed, resources: { ...defaultState().resources, ...(parsed.resources || {}) } });
    } catch {
      return normalizeState(defaultState());
    }
  }

  function normalizeState(nextState) {
    const progressMap = { ...(nextState.planetProgress || {}) };
    const arrivalMap = { ...(nextState.planetArrivalTimes || {}) };
    const maxUnlocked = Math.min(planets.length - 1, Math.max(nextState.completed || 0, nextState.planetIndex || 0));
    if (!Object.keys(progressMap).length) {
      for (let i = 0; i < maxUnlocked; i += 1) progressMap[i] = planets[i].thresholds[2];
    }
    progressMap[nextState.planetIndex || 0] = Number(progressMap[nextState.planetIndex || 0] ?? nextState.progress ?? 0);
    const currentProgress = Math.max(0, Math.min(planets[nextState.planetIndex || 0].thresholds[2], progressMap[nextState.planetIndex || 0]));
    progressMap[nextState.planetIndex || 0] = currentProgress;
    const fallbackArrival = Number(nextState.lastSaved || Date.now());
    for (let i = 0; i <= maxUnlocked; i += 1) {
      if (!Number(arrivalMap[i])) {
        arrivalMap[i] = i === (nextState.planetIndex || 0) ? fallbackArrival : Date.now() - minTravelMs;
      }
    }
    return {
      ...nextState,
      completed: maxUnlocked,
      progress: currentProgress,
      planetProgress: progressMap,
      planetArrivalTimes: arrivalMap,
    };
  }

  function saveState() {
    state.lastSaved = Date.now();
    localStorage.setItem(saveKey, JSON.stringify(state));
  }

  function loadAssets() {
    return Promise.all(imageNames.map((name) => new Promise((resolve) => {
      const img = new Image();
      img.onload = () => { assets[name] = img; resolve(); };
      img.onerror = () => resolve();
      img.src = `./assets/${name}`;
    })));
  }

  function nowMs() {
    return Date.now();
  }

  function enterLobby(label = '로비 연결') {
    screenMode = 'LOBBY';
    screenElapsedMs = 0;
    startScreenTransition(label, '#62f2ff');
  }

  function enterGame(panel = 'UPGRADE') {
    screenMode = 'GAME';
    screenElapsedMs = 0;
    currentPanel = panel;
    if (panel === 'EVENT' && state.activeEvent === null) openEvent();
    if ((state.tutorialVersion || 0) < tutorialVersion) {
      state.tutorialVersion = tutorialVersion;
      saveState();
    }
    startScreenTransition(panel === 'UPGRADE' ? '행성 복원 시작' : `${panelLabels[panel]} 진입`, panelAccents[panel]);
  }

  function switchPanel(panel) {
    const changed = currentPanel !== panel;
    currentPanel = panel;
    if (panel === 'TRAVEL') centerTravelMapOnCurrent();
    if (panel === 'EVENT' && state.activeEvent === null) openEvent();
    if (changed) startScreenTransition(`${panelLabels[panel]} 연결`, panelAccents[panel]);
  }

  function startScreenTransition(label, accent = '#62f2ff') {
    screenTransition = { label, accent, ageMs: 0 };
  }

  function maxUnlockedIndex() {
    return Math.min(planets.length - 1, Math.max(state.completed || 0, state.planetIndex || 0));
  }

  function progressFor(index = state.planetIndex) {
    if (index === state.planetIndex) return Number(state.progress || 0);
    const stored = state.planetProgress?.[index];
    if (stored !== undefined) return Number(stored);
    return index < maxUnlockedIndex() ? planets[index].thresholds[2] : 0;
  }

  function setCurrentProgress(value) {
    const clamped = clampProgress(value, state.planetIndex);
    state.progress = clamped;
    state.planetProgress = { ...(state.planetProgress || {}), [state.planetIndex]: clamped };
  }

  function currentPlanetArrivalMs() {
    const stored = state.planetArrivalTimes?.[state.planetIndex];
    if (Number(stored)) return Number(stored);
    const fallback = Number(state.lastSaved || nowMs());
    state.planetArrivalTimes = { ...(state.planetArrivalTimes || {}), [state.planetIndex]: fallback };
    return fallback;
  }

  function travelWaitRemainingMs() {
    if (state.planetIndex >= planets.length - 1) return 0;
    if (state.planetIndex + 1 <= maxUnlockedIndex()) return 0;
    return Math.max(0, minTravelMs - (nowMs() - currentPlanetArrivalMs()));
  }

  function travelTimeReady() {
    return travelWaitRemainingMs() <= 0;
  }

  function travelMapMetrics() {
    const viewport = { x: 52, w: W - 104 };
    const nodeSpacing = viewport.w / 3.8;
    const sidePad = nodeSpacing * 0.52;
    const contentW = sidePad * 2 + nodeSpacing * (planets.length - 1);
    return {
      viewport,
      nodeSpacing,
      sidePad,
      contentW,
      maxScroll: Math.max(0, contentW - viewport.w),
    };
  }

  function clampTravelMapScroll(value = travelMapScroll) {
    const { maxScroll } = travelMapMetrics();
    travelMapScroll = Math.max(0, Math.min(maxScroll, value));
    return travelMapScroll;
  }

  function centerTravelMapOnCurrent() {
    const { viewport, nodeSpacing, sidePad, maxScroll } = travelMapMetrics();
    const target = sidePad + nodeSpacing * state.planetIndex - viewport.w / 2;
    travelMapScroll = Math.max(0, Math.min(maxScroll, target));
  }

  function stageIndex(index = state.planetIndex) {
    const t = planets[index].thresholds;
    const progress = progressFor(index);
    if (progress >= t[2]) return 3;
    if (progress >= t[1]) return 2;
    if (progress >= t[0]) return 1;
    return 0;
  }

  function maxProgress(index = state.planetIndex) {
    return planets[index].thresholds[2];
  }

  function clampProgress(value, index = state.planetIndex) {
    return Math.max(0, Math.min(maxProgress(index), value));
  }

  function level(id) {
    return state.upgrades[id] || 0;
  }

  function cost(upgrade) {
    return scaleWallet(upgrade.baseCost, Math.pow(upgrade.scale, level(upgrade.id)));
  }

  function canAfford(wallet) {
    return state.resources.energy >= (wallet.energy || 0) && state.resources.biomass >= (wallet.biomass || 0) && state.resources.aiData >= (wallet.aiData || 0);
  }

  function addWallet(wallet) {
    state.resources.energy += wallet.energy || 0;
    state.resources.biomass += wallet.biomass || 0;
    state.resources.aiData += wallet.aiData || 0;
  }

  function spendWallet(wallet) {
    state.resources.energy -= wallet.energy || 0;
    state.resources.biomass -= wallet.biomass || 0;
    state.resources.aiData -= wallet.aiData || 0;
  }

  function scaleWallet(wallet, amount) {
    return { energy: (wallet.energy || 0) * amount, biomass: (wallet.biomass || 0) * amount, aiData: (wallet.aiData || 0) * amount };
  }

  function mergeWallet(a, b) {
    return {
      energy: (a.energy || 0) + (b.energy || 0),
      biomass: (a.biomass || 0) + (b.biomass || 0),
      aiData: (a.aiData || 0) + (b.aiData || 0),
    };
  }

  function multiplier(effect) {
    let value = 1;
    upgrades.filter((u) => u.effect === effect).forEach((u) => { value += level(u.id) * u.value; });
    return value;
  }

  function currentPlanetUpgrades(index = state.planetIndex) {
    return upgrades.slice(index * 4, index * 4 + 4);
  }

  function currentVisualStats(index = state.planetIndex) {
    const effectLevels = { ENERGY: 0, BIOMASS: 0, AI_DATA: 0, TAP: 0, RESTORE: 0, OFFLINE: 0 };
    const upgradeLevels = currentPlanetUpgrades(index).reduce((sum, upgrade) => {
      const lv = level(upgrade.id);
      effectLevels[upgrade.effect] = (effectLevels[upgrade.effect] || 0) + lv;
      return sum + lv;
    }, 0);
    const max = maxProgress(index);
    const progressRatio = max > 0 ? clampProgress(progressFor(index), index) / max : 0;
    const stage = stageIndex(index);
    const intensity = Math.min(1, progressRatio * 0.55 + stage * 0.12 + upgradeLevels * 0.018);
    const visualTier = Math.min(6, stage + Math.floor(upgradeLevels / 8));
    return { upgradeLevels, effectLevels, progressRatio, stage, intensity, visualTier };
  }

  function planetUpgradeFactor(index) {
    const totalLevels = currentPlanetUpgrades(index).reduce((sum, upgrade) => sum + level(upgrade.id), 0);
    return 1 + totalLevels * 0.025;
  }

  function colonyProduction() {
    const prestige = 1 + state.prestige * 0.05;
    let output = { energy: 0, biomass: 0, aiData: 0 };
    for (let i = 0; i <= maxUnlockedIndex(); i += 1) {
      if (i === state.planetIndex || stageIndex(i) !== 3) continue;
      const base = planets[i].base;
      const factor = 0.22 * planetUpgradeFactor(i) * prestige;
      output = mergeWallet(output, scaleWallet(base, factor));
    }
    return output;
  }

  function production() {
    const base = planets[state.planetIndex].base;
    const boost = state.adBoostUntil > nowMs() ? 2 : 1;
    const prestige = 1 + state.prestige * 0.05;
    const energyAlign = state.alignment === 'Industrial' ? 1.25 : 1;
    const biomassAlign = state.alignment === 'Life' ? 1.2 : 1;
    const aiAlign = state.alignment === 'Combat' ? 1.15 : 1;
    const activeProduction = {
      energy: base.energy * multiplier('ENERGY') * energyAlign * boost * prestige,
      biomass: base.biomass * multiplier('BIOMASS') * biomassAlign * prestige,
      aiData: base.aiData * multiplier('AI_DATA') * aiAlign * prestige,
    };
    return mergeWallet(activeProduction, colonyProduction());
  }

  function update(dt) {
    if (screenTransition) {
      screenTransition.ageMs += dt * 1000;
      if (screenTransition.ageMs > 900) screenTransition = null;
    }
    if (upgradeBurst) {
      upgradeBurst.ageMs += dt * 1000;
      if (upgradeBurst.ageMs > 2200) upgradeBurst = null;
    }
    screenElapsedMs += dt * 1000;
    if (screenMode === 'OPENING' && screenElapsedMs > 2800) {
      enterLobby('AI 코어 동기화');
      return;
    }
    if (screenMode === 'OPENING') return;
    const prod = production();
    addWallet(scaleWallet(prod, dt));
    setCurrentProgress(state.progress + (prod.energy * dt * 0.12 + dt * 0.8) * multiplier('RESTORE'));
    updateIncomeFloaters(dt, prod);
    updatePassingEncounter(dt);
    if (encounterBurst) {
      encounterBurst.ageMs += dt * 1000;
      if (encounterBurst.ageMs > 1200) encounterBurst = null;
    }
  }

  function tapPlanet() {
    const tapBonus = multiplier('TAP') * (state.alignment === 'Combat' ? 1.35 : 1);
    const gain = { energy: production().energy * 5 * tapBonus + 2 };
    addWallet(gain);
    setCurrentProgress(state.progress + 8 * multiplier('RESTORE'));
    startIncomeFloater(gain, '터치', W / 2 + 90, 820);
    toast('에너지 +');
    saveState();
  }

  function upgradeEffectLine(upgrade, next = false) {
    const lv = Math.min(upgrade.max, level(upgrade.id) + (next ? 1 : 0));
    const total = lv * upgrade.value;
    const value = `${Math.round(total * 100)}%`;
    const label = {
      ENERGY: '에너지 생산',
      BIOMASS: '바이오매스 생산',
      AI_DATA: 'AI 데이터 생산',
      TAP: '터치 보상',
      RESTORE: '복원 속도',
      OFFLINE: '오프라인 보상률',
    }[upgrade.effect] || '효과';
    if (upgrade.effect === 'OFFLINE') return `${label} +${value}p`;
    return `${label} +${value}`;
  }

  function upgradeNextDeltaLine(upgrade) {
    if (level(upgrade.id) >= upgrade.max) return '다음: 최대 레벨';
    const delta = `${Math.round(upgrade.value * 100)}%`;
    const label = {
      ENERGY: '에너지',
      BIOMASS: '바이오매스',
      AI_DATA: 'AI 데이터',
      TAP: '터치',
      RESTORE: '복원',
      OFFLINE: '오프라인',
    }[upgrade.effect] || '효과';
    return `다음 +${delta}${upgrade.effect === 'OFFLINE' ? 'p' : ''} ${label}`;
  }

  function purchaseUpgrade(upgrade, bounds = null) {
    if (level(upgrade.id) >= upgrade.max) return toast('최대 레벨입니다');
    const c = cost(upgrade);
    if (!canAfford(c)) return toast('자원이 부족합니다');
    spendWallet(c);
    state.upgrades[upgrade.id] = level(upgrade.id) + 1;
    setCurrentProgress(state.progress + (upgrade.effect === 'RESTORE' ? 25 : 8) + level(upgrade.id));
    if (bounds) startUpgradeBurst(upgrade.name, level(upgrade.id), bounds, upgrade.effect);
    toast('업그레이드 완료');
    saveState();
  }

  function chooseEvolution(evolution) {
    if (state.chosenPlanets.includes(state.planetIndex)) return toast('이미 선택 완료');
    state.scores[evolution.alignment] += 1;
    state.alignment = Object.entries(state.scores).sort((a, b) => b[1] - a[1])[0][0];
    if (evolution.alignment === 'Life') addWallet({ biomass: 10 });
    if (evolution.alignment === 'Industrial') addWallet({ energy: 200 });
    if (evolution.alignment === 'Combat') addWallet({ aiData: 8 });
    setCurrentProgress(state.progress + 60);
    state.chosenPlanets.push(state.planetIndex);
    toast(`${alignmentLabels[evolution.alignment]} 선택`);
    startScreenTransition(`${alignmentLabels[evolution.alignment]} 프로토콜`, panelAccents.AI);
    saveState();
  }

  function openEvent() {
    if (state.activeEvent !== null) return;
    state.activeEvent = Math.abs((state.planetIndex * 31 + state.completed * 7 + Math.floor(state.resources.energy))) % randomEvents.length;
    startScreenTransition('이상 현상 스캔', panelAccents.EVENT);
    saveState();
  }

  function resolveEvent(index) {
    if (state.activeEvent === null) return;
    const option = randomEvents[state.activeEvent].options[index];
    const reward = scaleWallet(option.reward, state.eventBoosts > 0 ? 2 : 1);
    addWallet(reward);
    setCurrentProgress(state.progress + option.progress);
    state.scores[option.align] += 1;
    state.alignment = Object.entries(state.scores).sort((a, b) => b[1] - a[1])[0][0];
    state.eventBoosts = Math.max(0, state.eventBoosts - 1);
    state.activeEvent = null;
    toast('이벤트 해결');
    startScreenTransition('이벤트 결과 적용', panelAccents.EVENT);
    saveState();
  }

  function travelOrPrestige() {
    if (state.planetIndex === planets.length - 1 && stageIndex() === 3) {
      state = normalizeState({ ...defaultState(), resources: { energy: 75, biomass: 0, aiData: 0 }, planetArrivalTimes: { 0: nowMs() }, prestige: state.prestige + 1, alignment: state.alignment, scores: state.scores });
      toast('프레스티지 완료');
      startScreenTransition('은하 재기동', panelAccents.TRAVEL);
      saveState();
      return;
    }
    const planet = planets[state.planetIndex];
    const nextIndex = state.planetIndex + 1;
    if (nextIndex > planets.length - 1) return toast('마지막 행성입니다');
    const alreadyUnlocked = nextIndex <= maxUnlockedIndex();
    if (!alreadyUnlocked) {
      if (stageIndex() !== 3 || !canAfford(planet.travel) || !travelTimeReady()) return toast('행성 완성/비용/7일 안정화 필요');
      spendWallet(planet.travel);
      state.completed = Math.max(state.completed, nextIndex);
      state.planetArrivalTimes = { ...(state.planetArrivalTimes || {}), [nextIndex]: nowMs() };
    }
    visitPlanet(nextIndex, alreadyUnlocked ? '항로 이동' : '워프 완료');
    centerTravelMapOnCurrent();
  }

  function visitPlanet(index, label = '행성 재방문') {
    if (index < 0 || index > maxUnlockedIndex()) return toast('잠긴 행성입니다');
    if (index === state.planetIndex) return toast('현재 행성입니다');
    setCurrentProgress(state.progress);
    const targetProgress = clampProgress(progressFor(index), index);
    state.planetIndex = index;
    state.progress = targetProgress;
    state.planetProgress = { ...(state.planetProgress || {}), [index]: state.progress };
    state.activeEvent = null;
    toast(label);
    startScreenTransition(`${planets[state.planetIndex].name} 연결`, panelAccents.TRAVEL);
    centerTravelMapOnCurrent();
    saveState();
  }

  function buyProduct(product) {
    const consumable = product.id === 'production_pack';
    if (!consumable && state.owned.includes(product.id)) return toast('이미 보유 중입니다');
    addWallet(product.reward);
    if (product.progress) setCurrentProgress(state.progress + product.progress);
    if (product.boost) state.adBoostUntil = nowMs() + product.boost * 1000;
    if (!consumable) state.owned.push(product.id);
    startRewardBurst(product.title, productRewardText(product));
    startScreenTransition('상품 보상 지급', panelAccents.SHOP);
    saveState();
  }

  function freeChestReward() {
    return mergeWallet(
      scaleWallet(production(), 180),
      { energy: 150 * (state.planetIndex + 1), biomass: 6, aiData: 2 },
    );
  }

  function walletText(wallet) {
    const parts = [];
    if ((wallet.energy || 0) > 0) parts.push(`E ${fmt(wallet.energy)}`);
    if ((wallet.biomass || 0) > 0) parts.push(`B ${fmt(wallet.biomass)}`);
    if ((wallet.aiData || 0) > 0) parts.push(`AI ${fmt(wallet.aiData)}`);
    return parts.length ? parts.join('  ') : '외형/편의 해금';
  }

  function shortageText(costWallet) {
    const shortage = {
      energy: Math.max(0, (costWallet.energy || 0) - state.resources.energy),
      biomass: Math.max(0, (costWallet.biomass || 0) - state.resources.biomass),
      aiData: Math.max(0, (costWallet.aiData || 0) - state.resources.aiData),
    };
    return walletText(shortage);
  }

  function productRewardText(product) {
    const parts = [];
    const wallet = walletText(product.reward || {});
    if (wallet !== '외형/편의 해금') parts.push(wallet);
    if (product.progress) parts.push(`복원 +${product.progress}`);
    if (product.boost) parts.push('생산 2배 60분');
    if (!parts.length) parts.push('외형/편의 해금');
    return parts.join('  ');
  }

  function startRewardBurst(title, detail) {
    rewardBurst = {
      title,
      detail,
      startedAt: performance.now(),
      particles: Array.from({ length: 34 }, (_, i) => ({
        angle: (i / 34) * Math.PI * 2,
        speed: 120 + (i % 7) * 18,
        size: 5 + (i % 5),
      })),
    };
  }

  function updateIncomeFloaters(dt, prod) {
    incomeFloaters = incomeFloaters
      .map((floater) => ({ ...floater, ageMs: floater.ageMs + dt * 1000 }))
      .filter((floater) => floater.ageMs < 1550);
    if (screenMode !== 'GAME') return;
    incomeFxMs += dt * 1000;
    if (incomeFxMs < 760) return;
    const gained = scaleWallet(prod, incomeFxMs / 1000);
    incomeFxMs = 0;
    startIncomeFloater(gained, '생산');
  }

  function startIncomeFloater(wallet, source = '생산', x = null, y = null) {
    const entries = [
      ['energy', 'E', '#62f2ff'],
      ['biomass', 'B', '#72f580'],
      ['aiData', 'AI', '#ffd75a'],
    ].filter(([key]) => (wallet[key] || 0) > 0);
    if (!entries.length) return;
    const [key, label, color] = entries[incomeFxIndex % entries.length];
    incomeFxIndex += 1;
    const cx = x ?? (W / 2 + ((incomeFxIndex % 5) - 2) * 48);
    const cy = y ?? (705 + (incomeFxIndex % 3) * 36);
    incomeFloaters.push({
      label: `+${fmt(wallet[key])} ${label}`,
      source,
      color,
      x: cx,
      y: cy,
      ageMs: 0,
    });
    if (incomeFloaters.length > 10) incomeFloaters.shift();
  }

  function updatePassingEncounter(dt) {
    if (screenMode !== 'GAME') return;
    if (passingEncounter) {
      passingEncounter.ageMs += dt * 1000;
      passingEncounter.x += passingEncounter.vx * dt;
      passingEncounter.y += Math.sin(passingEncounter.ageMs / 760) * passingEncounter.drift * dt;
      const offscreen = passingEncounter.vx > 0 ? passingEncounter.x > W + 180 : passingEncounter.x < -180;
      if (passingEncounter.ageMs > 26000 || offscreen) {
        passingEncounter = null;
        encounterSpawnMs = nextEncounterDelayMs();
      }
      return;
    }
    encounterSpawnMs -= dt * 1000;
    if (encounterSpawnMs <= 0) {
      spawnPassingEncounter();
    }
  }

  function nextEncounterDelayMs() {
    const seed = (state.planetIndex * 997 + state.completed * 83 + Math.floor(state.resources.energy)) % 23000;
    return 38000 + seed;
  }

  function spawnPassingEncounter(force = false) {
    if (passingEncounter && !force) return;
    const index = Math.abs((state.planetIndex * 5 + state.completed * 3 + Math.floor(state.resources.aiData + state.resources.biomass))) % encounterDefs.length;
    const def = encounterDefs[index];
    const fromLeft = ((Math.floor(nowMs() / 1000) + state.planetIndex) % 2) === 0;
    const baseY = 265 + ((index * 79 + state.planetIndex * 31) % 520);
    passingEncounter = {
      def,
      x: fromLeft ? -150 : W + 150,
      y: baseY,
      vx: (fromLeft ? 1 : -1) * (42 + (index % 5) * 9),
      drift: 18 + (index % 4) * 5,
      size: def.id === 'cargo_drone' || def.id === 'warp_merchant' ? 118 : 96,
      ageMs: 0,
      angle: fromLeft ? 0.08 : -0.08,
    };
    encounterSpawnMs = nextEncounterDelayMs();
  }

  function collectPassingEncounter() {
    if (!passingEncounter) return;
    const encounter = passingEncounter;
    const reward = encounterReward(encounter.def);
    addWallet(reward);
    if (encounter.def.type === 'rift') setCurrentProgress(state.progress + maxProgress() * 0.025);
    startIncomeFloater(reward, encounter.def.title, encounter.x, encounter.y);
    startRewardBurst(encounter.def.title, walletText(reward));
    encounterBurst = {
      x: encounter.x,
      y: encounter.y,
      asset: encounter.def.burst || 'fx_encounter_collect_burst.png',
      color: encounterColor(encounter.def.type),
      ageMs: 0,
    };
    passingEncounter = null;
    encounterSpawnMs = nextEncounterDelayMs();
    saveState();
  }

  function encounterReward(def) {
    const prod = production();
    const planetFactor = state.planetIndex + 1;
    const scaled = scaleWallet(prod, def.seconds);
    if (def.type === 'energy') return { energy: Math.max(90 * planetFactor, scaled.energy) };
    if (def.type === 'biomass') return { biomass: Math.max(8 * planetFactor, scaled.biomass + planetFactor * 4) };
    if (def.type === 'aiData') return { aiData: Math.max(4 * planetFactor, scaled.aiData + planetFactor * 2) };
    if (def.type === 'rare') return { energy: Math.max(140 * planetFactor, scaled.energy * 0.8), aiData: Math.max(5 * planetFactor, scaled.aiData + planetFactor * 2) };
    if (def.type === 'rift') return { energy: Math.max(180 * planetFactor, scaled.energy), biomass: Math.max(10 * planetFactor, scaled.biomass + planetFactor * 4), aiData: Math.max(6 * planetFactor, scaled.aiData + planetFactor * 2) };
    return { energy: Math.max(110 * planetFactor, scaled.energy * 0.6), biomass: Math.max(5 * planetFactor, scaled.biomass + planetFactor * 2), aiData: Math.max(2 * planetFactor, scaled.aiData + planetFactor) };
  }

  function encounterColor(type) {
    if (type === 'biomass') return '#72f580';
    if (type === 'aiData') return '#ffd75a';
    if (type === 'rift') return '#ff5f73';
    if (type === 'rare') return '#b68cff';
    return '#62f2ff';
  }

  function toast(message) {
    toastMessage = message;
    toastUntil = performance.now() + 1600;
  }

  function fmt(value) {
    if (value >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}m`;
    if (value >= 1_000) return `${(value / 1_000).toFixed(1)}k`;
    if (value >= 100) return `${value.toFixed(0)}`;
    if (value >= 10) return `${value.toFixed(1)}`;
    return `${value.toFixed(2)}`;
  }

  function formatDuration(ms) {
    const totalMinutes = Math.max(0, Math.ceil(ms / 60000));
    const days = Math.floor(totalMinutes / 1440);
    const hours = Math.floor((totalMinutes % 1440) / 60);
    const minutes = totalMinutes % 60;
    if (days > 0) return `${days}일 ${hours}시간`;
    if (hours > 0) return `${hours}시간 ${minutes}분`;
    return `${minutes}분`;
  }

  function rect(x, y, w, h, fill, stroke = '#62f2ff', radius = 16) {
    ctx.beginPath();
    ctx.roundRect(x, y, w, h, radius);
    ctx.fillStyle = fill;
    ctx.fill();
    ctx.lineWidth = 3;
    ctx.strokeStyle = stroke;
    ctx.stroke();
  }

  function drawImage(name, x, y, w, h, alpha = 1) {
    const img = assets[name];
    if (!img) return;
    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.drawImage(img, x, y, w, h);
    ctx.restore();
  }

  function drawImageCover(name, x, y, w, h, alpha = 1) {
    const img = assets[name];
    if (!img) return false;
    const scale = Math.max(w / img.width, h / img.height);
    const sw = w / scale;
    const sh = h / scale;
    const sx = (img.width - sw) / 2;
    const sy = (img.height - sh) / 2;
    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.drawImage(img, sx, sy, sw, sh, x, y, w, h);
    ctx.restore();
    return true;
  }

  function drawImageCenterCrop(name, x, y, w, h, alpha = 1) {
    const img = assets[name];
    if (!img) return false;
    const srcRatio = img.width / img.height;
    const dstRatio = w / h;
    let sx = 0;
    let sy = 0;
    let sw = img.width;
    let sh = img.height;
    if (srcRatio > dstRatio) {
      sw = img.height * dstRatio;
      sx = (img.width - sw) / 2;
    } else {
      sh = img.width / dstRatio;
      sy = (img.height - sh) / 2;
    }
    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.drawImage(img, sx, sy, sw, sh, x, y, w, h);
    ctx.restore();
    return true;
  }

  function drawImageRotated(name, cx, cy, size, angle, alpha = 1) {
    const img = assets[name];
    if (!img) return;
    const crop = Math.min(img.width, img.height);
    const sx = (img.width - crop) / 2;
    const sy = (img.height - crop) / 2;
    ctx.save();
    ctx.translate(cx, cy);
    ctx.rotate(angle);
    ctx.globalAlpha = alpha;
    ctx.drawImage(img, sx, sy, crop, crop, -size / 2, -size / 2, size, size);
    ctx.restore();
  }

  function text(value, x, y, size = 28, color = '#ffffff', align = 'left') {
    ctx.fillStyle = color;
    ctx.font = `700 ${size}px Arial`;
    ctx.textAlign = align;
    ctx.textBaseline = 'alphabetic';
    ctx.fillText(value, x, y);
  }

  function bodyText(value, x, y, maxChars = 40, maxLines = 3) {
    const words = value.split(' ');
    const lines = [];
    let line = '';
    words.forEach((word) => {
      const next = line ? `${line} ${word}` : word;
      if (next.length > maxChars && line) {
        lines.push(line);
        line = word;
      } else {
        line = next;
      }
    });
    if (line) lines.push(line);
    lines.slice(0, maxLines).forEach((l, i) => text(l, x, y + i * 34, 25, '#d2e8f0'));
  }

  function addTarget(x, y, w, h, action, label) {
    targets.push({ x, y, w, h, action, label });
  }

  function drawBackground() {
    const planetBg = planetBackgroundNames[state.planetIndex] || 'bg_nebula_main.png';
    if (!drawImageCover(planetBg, 0, 0, W, H, 1) && !drawImageCover('bg_nebula_main.png', 0, 0, W, H, 1)) {
      ctx.fillStyle = '#0c0f1c';
      ctx.fillRect(0, 0, W, H);
    }
    ctx.fillStyle = `rgba(3, 7, 16, ${planetBg === 'bg_nebula_main.png' ? 0.22 : 0.12})`;
    ctx.fillRect(0, 0, W, H);
    ctx.strokeStyle = 'rgba(98,242,255,0.14)';
    ctx.lineWidth = 2;
    for (let x = -120; x < W + 160; x += 90) {
      ctx.beginPath();
      ctx.moveTo(x, 0);
      ctx.lineTo(x + 650, H);
      ctx.stroke();
    }
  }

  function drawHud() {
    ctx.fillStyle = 'rgba(17,24,42,0.9)';
    ctx.fillRect(0, 0, W, 150);
    const planet = planets[state.planetIndex];
    const prod = production();
    text(planet.name, 34, 48, 32);
    text(`단계 ${stages[stageIndex()]}  ${Math.floor((state.progress / maxProgress()) * 100)}%`, 34, 88, 25, '#a0f6ff');
    text(`에너지 ${fmt(state.resources.energy)}  +${fmt(prod.energy)}/초`, 34, 126, 25, '#a0f6ff');
    text(`바이오 ${fmt(state.resources.biomass)}`, 600, 88, 25, '#a0f6ff');
    text(`AI ${fmt(state.resources.aiData)}  코어 ${state.prestige}`, 600, 126, 25, '#a0f6ff');
  }

  function drawPlanet() {
    const cx = W / 2;
    const cy = 655;
    const r = 245;
    const stage = stageIndex();
    const visualStats = currentVisualStats();
    const pulse = (performance.now() % 1600) / 1600;
    const angle = (performance.now() % 9000) / 9000 * Math.PI * 2;
    ctx.fillStyle = `rgba(98,242,255,${0.1 + pulse * 0.16 + visualStats.intensity * 0.08})`;
    ctx.beginPath();
    ctx.arc(cx, cy, r * (1.5 + pulse * 0.06 + visualStats.visualTier * 0.035), 0, Math.PI * 2);
    ctx.fill();
    drawImageRotated('fx_energy_ring.png', cx, cy, r * 3.08, angle, 0.9);
    drawImageRotated('fx_energy_ring.png', cx, cy, r * 2.55, -angle * 0.72, 0.55);
    drawPlanetVisualProgression(cx, cy, r, visualStats, angle, pulse, false);
    drawEnergyParticles(cx, cy, r, visualStats);
    drawImageCenterCrop(stageAssetNames[stage], cx - r * 1.23, cy - r * 1.23, r * 2.46, r * 2.46, 1);
    drawPlanetVisualProgression(cx, cy, r, visualStats, angle, pulse, true);
    drawPlanetUpgradePulse(cx, cy, r);
    drawResourceStreams(cx, cy, r, visualStats);
    text('코어 터치', cx, cy + r + 60, 28, '#ffffff', 'center');
    addTarget(cx - r, cy - r, r * 2, r * 2, tapPlanet, '행성 터치');
  }

  function drawPlanetVisualProgression(cx, cy, r, stats, angle, pulse, foreground) {
    const time = performance.now() / 1000;
    if (!foreground) {
      const ringCount = Math.min(7, 1 + stats.stage + Math.floor(stats.upgradeLevels / 5));
      for (let i = 0; i < ringCount; i += 1) {
        const keys = ['ENERGY', 'BIOMASS', 'AI_DATA', 'RESTORE', 'OFFLINE', 'TAP'];
        const key = keys[i % keys.length];
        const color = effectColors[key];
        const alpha = 0.18 + stats.intensity * 0.28 + 0.08 * Math.sin(time * 1.7 + i);
        const ringRadius = r * (1.12 + i * 0.105 + stats.visualTier * 0.012);
        const ringAsset = ringAssetForEffect(key);
        if (assets[ringAsset]) {
          drawImageRotated(ringAsset, cx, cy, ringRadius * 2.24, angle * (i % 2 === 0 ? 0.6 : -0.44) + i * 0.27, Math.min(0.72, alpha + 0.18));
        }
        ctx.save();
        ctx.translate(cx, cy);
        ctx.rotate(angle * (i % 2 === 0 ? 0.6 : -0.44) + i * 0.27);
        ctx.strokeStyle = hexToRgba(color, alpha);
        ctx.lineWidth = 2 + Math.min(8, stats.upgradeLevels * 0.12) + (i % 2);
        ctx.setLineDash([28 + i * 6, 18 + i * 4]);
        ctx.beginPath();
        ctx.ellipse(0, 0, ringRadius, ringRadius * (0.58 + (i % 3) * 0.055), 0, 0, Math.PI * 2);
        ctx.stroke();
        ctx.restore();
      }
      return;
    }

    drawPlanetSurfaceMasks(cx, cy, r, stats, angle, time);
    const nodeCount = Math.min(42, 8 + stats.stage * 4 + Math.floor(stats.upgradeLevels * 0.85));
    for (let i = 0; i < nodeCount; i += 1) {
      const key = strongestEffectKey(stats, i);
      const theta = i * 2.399 + time * (0.16 + (i % 5) * 0.008);
      const dist = r * (0.18 + ((i * 37) % 66) / 100);
      const x = cx + Math.cos(theta) * dist;
      const y = cy + Math.sin(theta) * dist * 0.72;
      const size = 3.2 + (i % 4) + stats.visualTier * 0.5;
      const alpha = 0.5 + 0.32 * Math.sin(time * 2.8 + i);
      ctx.save();
      ctx.shadowColor = effectColors[key];
      ctx.shadowBlur = 13 + stats.visualTier * 3;
      ctx.fillStyle = hexToRgba(effectColors[key], alpha);
      ctx.beginPath();
      ctx.arc(x, y, size, 0, Math.PI * 2);
      ctx.fill();
      if (stats.upgradeLevels > 10 && i % 5 === 0) {
        ctx.strokeStyle = hexToRgba(effectColors[key], alpha * 0.7);
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(x, y, size * 2.2, 0, Math.PI * 2);
        ctx.stroke();
      }
      ctx.restore();
    }

    const satelliteCount = Math.min(12, stats.stage + Math.floor(stats.upgradeLevels / 4));
    for (let i = 0; i < satelliteCount; i += 1) {
      const key = strongestEffectKey(stats, i + 3);
      const orbit = r * (1.18 + (i % 4) * 0.13);
      const theta = angle * (i % 2 === 0 ? 1.2 : -0.95) + i * (Math.PI * 2 / Math.max(1, satelliteCount));
      const x = cx + Math.cos(theta) * orbit;
      const y = cy + Math.sin(theta) * orbit * 0.62;
      const moduleSize = 10 + stats.visualTier * 2 + (i % 3) * 2;
      const propAsset = propAssetForEffect(key);
      if (assets[propAsset]) {
        ctx.save();
        ctx.shadowColor = effectColors[key];
        ctx.shadowBlur = 18;
        drawImageRotated(propAsset, x, y, moduleSize * 4.5, theta, 0.92);
        ctx.restore();
        continue;
      }
      ctx.save();
      ctx.shadowColor = effectColors[key];
      ctx.shadowBlur = 18;
      ctx.fillStyle = hexToRgba(effectColors[key], 0.82);
      ctx.strokeStyle = 'rgba(255,255,255,0.72)';
      ctx.lineWidth = 2;
      ctx.beginPath();
      if (key === 'BIOMASS') {
        ctx.ellipse(x, y, moduleSize * 0.65, moduleSize, theta, 0, Math.PI * 2);
      } else if (key === 'AI_DATA') {
        ctx.rect(x - moduleSize * 0.62, y - moduleSize * 0.62, moduleSize * 1.24, moduleSize * 1.24);
      } else {
        ctx.arc(x, y, moduleSize, 0, Math.PI * 2);
      }
      ctx.fill();
      ctx.stroke();
      ctx.restore();
    }
  }

  function drawPlanetSurfaceMasks(cx, cy, r, stats, angle, time) {
    const masks = [
      ['ENERGY', 'mask_surface_energy_veins.png', 0.13],
      ['BIOMASS', 'mask_surface_bio_growth.png', -0.08],
      ['AI_DATA', 'mask_surface_ai_city.png', 0.05],
    ];
    masks.forEach(([key, asset, speed], index) => {
      const lv = stats.effectLevels[key] || 0;
      const alpha = Math.min(0.64, 0.12 + lv * 0.035 + stats.intensity * 0.18);
      if (alpha <= 0.12 || !assets[asset]) return;
      drawImageRotated(asset, cx, cy, r * (1.72 + index * 0.04), angle * speed + time * speed, alpha);
    });
  }

  function drawPlanetUpgradePulse(cx, cy, r) {
    if (!upgradeBurst) return;
    const t = Math.min(1, upgradeBurst.ageMs / 1200);
    const alpha = Math.max(0, 1 - t);
    const color = effectColors[upgradeBurst.effect] || '#62f2ff';
    ctx.save();
    const pulseAsset = pulseAssetForEffect(upgradeBurst.effect);
    if (assets[pulseAsset]) {
      drawImageRotated(pulseAsset, cx, cy, r * (1.28 + t * 1.05), t * Math.PI, alpha * 0.86);
    }
    ctx.strokeStyle = hexToRgba(color, alpha * 0.95);
    ctx.lineWidth = 7 * alpha + 1;
    ctx.shadowColor = color;
    ctx.shadowBlur = 28 * alpha;
    ctx.beginPath();
    ctx.arc(cx, cy, r * (0.72 + t * 0.94), 0, Math.PI * 2);
    ctx.stroke();
    ctx.globalAlpha = alpha * 0.32;
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(cx, cy, r * (0.5 + t * 0.35), 0, Math.PI * 2);
    ctx.fill();
    ctx.restore();
  }

  function drawResourceStreams(cx, cy, r, stats) {
    const time = performance.now() / 1000;
    const streamCount = Math.min(10, 2 + stats.stage + Math.floor(stats.upgradeLevels / 6));
    const endpoints = [
      { x: 98, y: 120, key: 'ENERGY' },
      { x: 655, y: 86, key: 'BIOMASS' },
      { x: 680, y: 128, key: 'AI_DATA' },
    ];
    ctx.save();
    for (let i = 0; i < streamCount; i += 1) {
      const endpoint = endpoints[i % endpoints.length];
      const sourceAngle = -Math.PI * 0.75 + i * 0.48;
      const sx = cx + Math.cos(sourceAngle + time * 0.08) * r * 0.72;
      const sy = cy + Math.sin(sourceAngle + time * 0.08) * r * 0.48;
      const t = (time * (0.18 + i * 0.013) + i * 0.17) % 1;
      const x = sx + (endpoint.x - sx) * t + Math.sin(t * Math.PI) * (i % 2 === 0 ? 38 : -38);
      const y = sy + (endpoint.y - sy) * t - Math.sin(t * Math.PI) * (42 + (i % 3) * 12);
      ctx.strokeStyle = hexToRgba(effectColors[endpoint.key], 0.08 + stats.intensity * 0.12);
      ctx.lineWidth = 2;
      ctx.beginPath();
      ctx.moveTo(sx, sy);
      ctx.quadraticCurveTo((sx + endpoint.x) / 2, Math.min(sy, endpoint.y) - 85, endpoint.x, endpoint.y);
      ctx.stroke();
      ctx.shadowColor = effectColors[endpoint.key];
      ctx.shadowBlur = 12;
      ctx.fillStyle = hexToRgba(effectColors[endpoint.key], 0.58 + stats.intensity * 0.34);
      ctx.beginPath();
      ctx.arc(x, y, 4.5 + (i % 3), 0, Math.PI * 2);
      ctx.fill();
    }
    ctx.restore();
  }

  function drawEnergyParticles(cx, cy, r, stats = currentVisualStats()) {
    const time = performance.now() / 1000;
    const count = Math.min(54, 22 + stats.stage * 5 + Math.floor(stats.upgradeLevels * 0.8));
    for (let i = 0; i < count; i += 1) {
      const angle = time * (0.34 + i * 0.013) + i * 0.83;
      const dist = r * (1.02 + (i % 6) * 0.082 + stats.visualTier * 0.014);
      const x = cx + Math.cos(angle) * dist;
      const y = cy + Math.sin(angle) * dist * 0.72;
      const alpha = 0.36 + 0.28 * Math.sin(time * 2.1 + i);
      const key = strongestEffectKey(stats, i);
      ctx.fillStyle = hexToRgba(effectColors[key], alpha);
      ctx.beginPath();
      ctx.arc(x, y, 4 + (i % 4) + stats.visualTier * 0.35, 0, Math.PI * 2);
      ctx.fill();
    }
  }

  function strongestEffectKey(stats, offset = 0) {
    const ordered = Object.entries(stats.effectLevels)
      .filter(([, value]) => value > 0)
      .sort((a, b) => b[1] - a[1])
      .map(([key]) => key);
    if (!ordered.length) return offset % 3 === 0 ? 'ENERGY' : offset % 3 === 1 ? 'BIOMASS' : 'AI_DATA';
    return ordered[offset % ordered.length];
  }

  function ringAssetForEffect(effect) {
    if (effect === 'BIOMASS') return 'fx_upgrade_orbit_bio.png';
    if (effect === 'AI_DATA') return 'fx_upgrade_orbit_ai_data.png';
    if (effect === 'RESTORE') return 'fx_restore_wave.png';
    if (effect === 'TAP') return 'fx_tap_resonance_core.png';
    return 'fx_upgrade_orbit_energy.png';
  }

  function propAssetForEffect(effect) {
    if (effect === 'BIOMASS') return 'prop_bio_seed_drone.png';
    if (effect === 'AI_DATA') return 'prop_ai_memory_shard.png';
    if (effect === 'OFFLINE') return 'prop_offline_storage_orb.png';
    return 'prop_energy_satellite.png';
  }

  function pulseAssetForEffect(effect) {
    if (effect === 'RESTORE') return 'fx_restore_wave.png';
    if (effect === 'TAP') return 'fx_tap_resonance_core.png';
    return 'fx_upgrade_burst.png';
  }

  function hexToRgba(hex, alpha) {
    const clean = hex.replace('#', '');
    const value = parseInt(clean, 16);
    const r = (value >> 16) & 255;
    const g = (value >> 8) & 255;
    const b = value & 255;
    return `rgba(${r},${g},${b},${Math.max(0, Math.min(1, alpha))})`;
  }

  function drawAi() {
    const x = W - 178;
    const y = 186;
    rect(x, y, 132, 132, 'rgba(18,28,48,0.86)', '#a0f6ff', 14);
    drawImage(aiAssetNames[state.alignment], x + 5, y - 2, 122, 122, 1);
    text(alignmentLabels[state.alignment], x + 66, y + 112, 20, '#ffffff', 'center');
  }

  function drawPanel() {
    const top = 1050;
    const bottom = H - 170;
    rect(22, top, W - 44, bottom - top, 'rgba(17,24,42,0.92)', 'rgba(98,242,255,0.45)', 18);
    if (currentPanel === 'UPGRADE') drawUpgradePanel(top, bottom);
    if (currentPanel === 'TRAVEL') drawTravelPanel(top, bottom);
    if (currentPanel === 'AI') drawAiPanel(top, bottom);
    if (currentPanel === 'EVENT') drawEventPanel(top, bottom);
    if (currentPanel === 'SHOP') drawShopPanel(top, bottom);
  }

  function drawUpgradePanel(top, bottom) {
    drawPanelHeader('업그레이드', panelDescriptions.UPGRADE, top);
    upgrades.slice(state.planetIndex * 4, state.planetIndex * 4 + 4).forEach((u, i) => {
      const y = top + 106 + i * 130;
      rect(46, y, W - 92, 112, 'rgba(25,34,54,0.92)', 'rgba(160,246,255,0.45)', 14);
      drawImage(`icon_upgrade_${u.icon}.png`, 60, y + 20, 72, 72);
      text(`${u.name} Lv.${level(u.id)}/${u.max}`, 148, y + 31, 25);
      text(`현재: ${upgradeEffectLine(u)}`, 148, y + 60, 21, '#d2e8f0');
      text(upgradeNextDeltaLine(u), 148, y + 86, 21, '#ffd75a');
      const c = cost(u);
      text(`비용 ${walletText(c)}`, W - 56, y + 31, 19, '#a0f6ff', 'right');
      const bounds = { x: 46, y, w: W - 92, h: 112 };
      addTarget(bounds.x, bounds.y, bounds.w, bounds.h, () => purchaseUpgrade(u, bounds), `업그레이드 ${u.name}`);
    });
  }

  function drawTravelPanel(top, bottom) {
    drawPanelHeader('은하 지도', panelDescriptions.TRAVEL, top);
    bodyText(planets[state.planetIndex].theme, 52, top + 112, 42, 2);
    const planet = planets[state.planetIndex];
    const next = planets[state.planetIndex + 1];
    const nextUnlocked = !!next && state.planetIndex + 1 <= maxUnlockedIndex();
    const stageReady = stageIndex() === 3;
    const costReady = canAfford(planet.travel);
    const timeReady = travelTimeReady();
    const waitMs = travelWaitRemainingMs();
    const finalPlanet = !next;
    const canMove = finalPlanet ? stageReady : nextUnlocked || (stageReady && costReady && timeReady);
    const statusTitle = canMove ? (finalPlanet ? '프레스티지 가능' : nextUnlocked ? '재방문 항로 개방' : '이동 가능') : '이동 조건 확인';
    rect(52, top + 168, W - 104, 180, 'rgba(25,34,54,0.92)', canMove ? '#72f580' : '#ffd75a', 14);
    text(statusTitle, 74, top + 206, 28, canMove ? '#72f580' : '#ffd75a');
    text(`조건 1: 행성 완성  ${stageReady ? '완료' : `${stages[stageIndex()]} ${Math.floor((state.progress / maxProgress()) * 100)}%`}`, 74, top + 240, 22, stageReady ? '#72f580' : '#d2e8f0');
    if (finalPlanet) {
      text('조건 2: 최종 행성입니다. 완성 후 은하를 재시작합니다.', 74, top + 272, 22, stageReady ? '#72f580' : '#d2e8f0');
      text(stageReady ? '보상: 코어 +1, 일부 성향 유지' : `남은 복원도: ${fmt(maxProgress() - state.progress)}`, 74, top + 302, 20, '#a0f6ff');
      text('조건 3: 최종 코어는 항로 안정화 조건 없음', 74, top + 330, 20, '#a0f6ff');
    } else if (nextUnlocked) {
      text('조건 2: 이미 개척한 항로입니다. 비용 없이 이동합니다.', 74, top + 272, 22, '#72f580');
      text('완료/개척 행성 노드를 탭하면 언제든 재방문합니다.', 74, top + 302, 20, '#a0f6ff');
      text('조건 3: 재방문 항로는 즉시 사용 가능', 74, top + 330, 20, '#72f580');
    } else {
      text(`조건 2: 이동 비용  ${walletText(planet.travel)}`, 74, top + 272, 22, costReady ? '#72f580' : '#d2e8f0');
      text(costReady ? `보유 충분: ${walletText(state.resources)}` : `부족: ${shortageText(planet.travel)}`, 74, top + 302, 20, costReady ? '#72f580' : '#ff9aa8');
      text(`조건 3: 항로 안정화  ${timeReady ? '완료' : `남은 ${formatDuration(waitMs)}`}`, 74, top + 330, 20, timeReady ? '#72f580' : '#ff9aa8');
    }
    const y = top + 452;
    const map = travelMapMetrics();
    clampTravelMapScroll();
    travelMapBounds = { x: map.viewport.x, y: y - 84, w: map.viewport.w, h: 154 };
    text(`미니맵 ${state.planetIndex + 1}/${planets.length}`, 74, y - 76, 22, '#a0f6ff');
    text('좌우로 드래그해서 다른 행성을 봅니다', W - 74, y - 76, 18, '#ffd75a', 'right');
    rect(travelMapBounds.x, travelMapBounds.y, travelMapBounds.w, travelMapBounds.h, 'rgba(8,14,27,0.42)', 'rgba(98,242,255,0.28)', 14);
    ctx.save();
    ctx.beginPath();
    ctx.rect(travelMapBounds.x + 4, travelMapBounds.y + 4, travelMapBounds.w - 8, travelMapBounds.h - 8);
    ctx.clip();
    planets.forEach((p, i) => {
      const x = map.viewport.x + map.sidePad + i * map.nodeSpacing - travelMapScroll;
      const nodeY = y + (i % 2 ? 22 : -22);
      if (i > 0) {
        ctx.strokeStyle = '#62f2ff';
        ctx.lineWidth = 5;
        ctx.beginPath();
        const prevX = map.viewport.x + map.sidePad + (i - 1) * map.nodeSpacing - travelMapScroll;
        const prevY = y + ((i - 1) % 2 ? 22 : -22);
        ctx.moveTo(prevX, prevY);
        ctx.lineTo(x, nodeY);
        ctx.stroke();
      }
      const unlocked = i <= maxUnlockedIndex();
      const name = !unlocked ? 'map_node_locked_planet.png' : i === state.planetIndex ? 'map_node_current_planet.png' : 'map_node_complete_planet.png';
      const size = i === state.planetIndex ? 118 : 78;
      if (i === state.planetIndex) {
        const pulse = (performance.now() % 1300) / 1300;
        ctx.strokeStyle = `rgba(255,215,90,${0.9 - pulse * 0.55})`;
        ctx.lineWidth = 6;
        ctx.beginPath();
        ctx.arc(x, nodeY, 68 + pulse * 18, 0, Math.PI * 2);
        ctx.stroke();
        text('현재 위치', x, nodeY - 76, 22, '#ffd75a', 'center');
      }
      drawImage(name, x - size / 2, nodeY - size / 2, size, size, unlocked ? 1 : 0.62);
      if (unlocked && x > travelMapBounds.x - 80 && x < travelMapBounds.x + travelMapBounds.w + 80) {
        addTarget(x - size / 2, nodeY - size / 2, size, size, () => visitPlanet(i), `행성 재방문 ${p.name}`);
      }
    });
    ctx.restore();
    const thumbW = Math.max(80, map.viewport.w * (map.viewport.w / map.contentW));
    const thumbX = map.viewport.x + (map.viewport.w - thumbW) * (travelMapScroll / Math.max(1, map.maxScroll));
    rect(map.viewport.x, y + 70, map.viewport.w, 12, 'rgba(17,24,42,0.72)', 'rgba(160,246,255,0.25)', 6);
    rect(thumbX, y + 68, thumbW, 16, 'rgba(98,242,255,0.72)', '#a0f6ff', 8);
    rect(52, bottom - 88, W - 104, 68, canMove ? 'rgba(35,98,72,0.92)' : 'rgba(35,72,98,0.75)', canMove ? '#72f580' : '#62f2ff', 14);
    const buttonLabel = canMove ? (next ? `${nextUnlocked ? '재방문' : '워프 가능'}: ${next.name}` : '프레스티지 가능') : (next ? `다음 행성: ${next.name}` : '은하 프레스티지');
    text(buttonLabel, 74, bottom - 45, 26, '#ffffff');
    text(canMove ? '탭하면 즉시 이동합니다' : '위 조건을 모두 만족하면 이동 가능', W - 74, bottom - 45, 21, canMove ? '#72f580' : '#a0f6ff', 'right');
    addTarget(52, bottom - 88, W - 104, 68, travelOrPrestige, canMove ? '이동 가능' : '이동 조건 부족');
  }

  function drawAiPanel(top, bottom) {
    drawPanelHeader('AI 진화', panelDescriptions.AI, top);
    const locked = state.chosenPlanets.includes(state.planetIndex);
    if (locked) {
      bodyText(`이 행성의 지시는 확정되었습니다. 현재 성향: ${alignmentLabels[state.alignment]}.`, 52, top + 116, 44, 3);
      return;
    }
    evolutions.forEach((e, i) => {
      const y = top + 112 + i * 118;
      rect(52, y, W - 104, 94, 'rgba(35,72,98,0.9)', '#62f2ff', 14);
      ctx.fillStyle = e.alignment === 'Life' ? '#72f580' : e.alignment === 'Industrial' ? '#62f2ff' : '#ff5f73';
      ctx.beginPath();
      ctx.arc(92, y + 47, 26, 0, Math.PI * 2);
      ctx.fill();
      text(e.title, 142, y + 38, 27);
      text(e.reward, 142, y + 72, 22, '#a0f6ff');
      addTarget(52, y, W - 104, 94, () => chooseEvolution(e), `진화 ${alignmentLabels[e.alignment]}`);
    });
  }

  function drawEventPanel(top, bottom) {
    drawPanelHeader('랜덤 이벤트', panelDescriptions.EVENT, top);
    if (state.activeEvent === null) {
      bodyText('활성화된 이상 현상이 없습니다. 짧은 세션 시작 시 스캔하세요.', 52, top + 116, 44, 3);
      rect(52, bottom - 88, W - 104, 68, 'rgba(35,72,98,0.9)', '#62f2ff', 14);
      text('이상 현상 스캔', 74, bottom - 45, 26);
      addTarget(52, bottom - 88, W - 104, 68, openEvent, '이상 현상 스캔');
      return;
    }
    const event = randomEvents[state.activeEvent];
    drawImage(`event_${event.id}.png`, W - 282, top + 116, 220, 160, 0.76);
    bodyText(`${event.title}: ${event.body}`, 52, top + 116, 31, 4);
    event.options.forEach((o, i) => {
      const y = bottom - 172 + i * 82;
      rect(52, y, W - 104, 62, 'rgba(35,72,98,0.9)', '#62f2ff', 14);
      text(o.label, 74, y + 39, 25);
      addTarget(52, y, W - 104, 62, () => resolveEvent(i), `이벤트 선택 ${i + 1}`);
    });
  }

  function drawShopPanel(top, bottom) {
    drawPanelHeader('상점', panelDescriptions.SHOP, top);
    let y = top + 112;
    shopRewards.forEach((r) => {
      rect(52, y, W - 104, 66, 'rgba(35,72,98,0.9)', '#62f2ff', 14);
      drawImage(r.icon, 62, y + 10, 46, 46);
      text(r.label, 124, y + 28, 23);
      text(shopRewardDetail(r.label), 124, y + 54, 19, '#a0f6ff');
      text('무료', W - 70, y + 39, 21, '#ffd75a', 'right');
      addTarget(52, y, W - 104, 66, r.action, r.label);
      y += 76;
    });
    products.slice(0, 4).forEach((p) => {
      rect(52, y, W - 104, 66, 'rgba(25,34,54,0.92)', '#ffd75a', 14);
      drawImage(p.icon, 62, y + 10, 46, 46);
      text(`${p.title}${state.owned.includes(p.id) ? '  보유' : ''}`, 124, y + 28, 22);
      text(productRewardText(p), 124, y + 54, 18, '#a0f6ff');
      text(state.owned.includes(p.id) ? '보유' : p.price, W - 70, y + 39, 20, '#ffd75a', 'right');
      addTarget(52, y, W - 104, 66, () => buyProduct(p), p.title);
      y += 76;
    });
  }

  function shopRewardDetail(label) {
    if (label.includes('생산')) return '10분간 에너지 생산 2배';
    if (label.includes('복원')) return `즉시 복원 +${Math.floor(maxProgress() * 0.08)}`;
    if (label.includes('이벤트')) return '다음 이벤트 보상 2배';
    return `상자 보상 ${walletText(freeChestReward())}`;
  }

  function drawPanelHeader(title, description, top) {
    text(title, 52, top + 50, 28);
    text(description, 52, top + 82, 21, '#a0f6ff');
  }

  function drawNav() {
    const top = H - 150;
    const itemW = W / 5;
    panels.forEach((panel, i) => {
      const x = i * itemW + 8;
      rect(x, top + 12, itemW - 16, 118, panel === currentPanel ? 'rgba(35,72,98,0.95)' : 'rgba(25,34,54,0.9)', panel === currentPanel ? '#62f2ff' : 'rgba(160,246,255,0.45)', 14);
      const file = `icon_nav_${panel.toLowerCase()}.png`;
      drawImage(file, x + itemW / 2 - 28, top + 24, 56, 56);
      text(panelLabels[panel], x + itemW / 2, top + 104, 20, '#ffffff', 'center');
      addTarget(x, top + 12, itemW - 16, 118, () => switchPanel(panel), `패널 ${panelLabels[panel]}`);
    });
  }

  function drawOpeningScreen() {
    drawBackground();
    const t = Math.min(1, screenElapsedMs / 2800);
    const pulse = 0.5 + Math.sin(screenElapsedMs / 180) * 0.5;
    const cx = W / 2;
    const cy = 725;
    for (let i = 0; i < 16; i += 1) {
      const y = 245 + i * 58 + t * 120;
      ctx.strokeStyle = `rgba(98,242,255,${0.1 + (i % 3) * 0.04})`;
      ctx.lineWidth = 2 + (i % 4);
      ctx.beginPath();
      ctx.moveTo(80 + i * 38, y);
      ctx.lineTo(W - 160 + i * 12, y + 170);
      ctx.stroke();
    }
    drawImageRotated('fx_energy_ring.png', cx, cy, 560 + pulse * 36, screenElapsedMs / 1000, 0.95);
    drawImageRotated('fx_energy_ring.png', cx, cy, 410, -screenElapsedMs / 1300, 0.52);
    drawImageCenterCrop(stageAssetNames[stageIndex()], cx - 180, cy - 180, 360, 360, 0.98);
    drawImage(aiAssetNames[state.alignment], cx - 88, cy - 118, 176, 176, 0.96);
    ctx.fillStyle = `rgba(98,242,255,${0.1 + pulse * 0.22})`;
    ctx.beginPath();
    ctx.arc(cx, cy, 310 + pulse * 18, 0, Math.PI * 2);
    ctx.fill();
    text('AI 코어 리버스', cx, 310, 62, '#ffffff', 'center');
    text('멸망 이후 마지막 감정 코어 재기동', cx, 370, 26, '#a0f6ff', 'center');
    text('행성 복원 루프 초기화', cx, 1070, 28, '#ffd75a', 'center');
    const barW = 560;
    rect(cx - barW / 2, 1115, barW, 22, 'rgba(17,24,42,0.9)', 'rgba(160,246,255,0.5)', 11);
    ctx.fillStyle = '#62f2ff';
    ctx.fillRect(cx - barW / 2 + 4, 1119, (barW - 8) * t, 14);
    rect(236, 1548, W - 472, 76, 'rgba(35,72,98,0.88)', '#ffd75a', 18);
    text('탭하여 로비 진입', cx, 1598, 28, '#ffffff', 'center');
    addTarget(0, 0, W, H, () => enterLobby('오프닝 스킵'), '오프닝 스킵');
  }

  function drawLobbyScreen() {
    drawBackground();
    const cx = W / 2;
    const time = performance.now() / 1000;
    text('AI 코어 리버스', cx, 130, 48, '#ffffff', 'center');
    text('감정 AI로 행성을 복원하고 다음 은하로 확장하세요.', cx, 180, 24, '#a0f6ff', 'center');
    drawImageRotated('fx_energy_ring.png', cx, 465, 520, time * 0.45, 0.86);
    drawImageCenterCrop(stageAssetNames[stageIndex()], cx - 158, 305, 316, 316, 1);
    drawImage(aiAssetNames[state.alignment], cx + 150, 300, 150, 150, 0.98);
    text(planets[state.planetIndex].name, cx, 690, 34, '#ffffff', 'center');
    text(`복원 ${Math.floor((state.progress / maxProgress()) * 100)}%  |  ${alignmentLabels[state.alignment]} AI`, cx, 730, 24, '#ffd75a', 'center');

    rect(76, 790, W - 152, 248, 'rgba(17,24,42,0.9)', 'rgba(98,242,255,0.42)', 18);
    text('기능 브리핑', 106, 840, 30);
    panels.forEach((panel, i) => {
      const y = 890 + i * 28;
      drawImage(`icon_nav_${panel.toLowerCase()}.png`, 110, y - 22, 30, 30, 0.95);
      text(`${panelLabels[panel]}: ${panelDescriptions[panel]}`, 154, y, 20, i === 0 ? '#ffffff' : '#d2e8f0');
    });

    const buttons = [
      { label: '게임 시작', detail: '행성 복원 화면으로 진입', panel: 'UPGRADE' },
      { label: 'AI 브리핑', detail: '진화 선택과 성향 확인', panel: 'AI' },
      { label: '상점 보기', detail: '광고 보상과 유료 상품 확인', panel: 'SHOP' },
    ];
    buttons.forEach((button, i) => {
      const y = 1130 + i * 112;
      rect(128, y, W - 256, 82, 'rgba(35,72,98,0.94)', i === 0 ? '#ffd75a' : '#62f2ff', 18);
      text(button.label, 166, y + 35, 28);
      text(button.detail, 166, y + 65, 20, '#a0f6ff');
      addTarget(128, y, W - 256, 82, () => enterGame(button.panel), `로비 ${button.label}`);
    });
    text('로비에서는 생산이 계속 누적됩니다.', cx, 1510, 22, '#a0f6ff', 'center');
  }

  function colorToRgb(hex) {
    const raw = hex.replace('#', '');
    const n = parseInt(raw, 16);
    return `${(n >> 16) & 255},${(n >> 8) & 255},${n & 255}`;
  }

  function drawScreenTransition() {
    if (!screenTransition) return;
    const life = 900;
    const t = Math.min(1, screenTransition.ageMs / life);
    const rgb = colorToRgb(screenTransition.accent);
    const alpha = t < 0.5 ? 0.55 * (1 - t * 0.8) : 0.26 * (1 - t);
    const cx = W / 2;
    const cy = H / 2;
    ctx.save();
    ctx.fillStyle = `rgba(2,6,14,${alpha})`;
    ctx.fillRect(0, 0, W, H);
    for (let i = 0; i < 24; i += 1) {
      const angle = (i / 24) * Math.PI * 2 + t * 1.8;
      const inner = 90 + t * 260;
      const outer = 620 + t * 520;
      ctx.strokeStyle = `rgba(${rgb},${0.56 * (1 - t)})`;
      ctx.lineWidth = 2 + (i % 4);
      ctx.beginPath();
      ctx.moveTo(cx + Math.cos(angle) * inner, cy + Math.sin(angle) * inner * 0.72);
      ctx.lineTo(cx + Math.cos(angle) * outer, cy + Math.sin(angle) * outer * 0.72);
      ctx.stroke();
    }
    ctx.strokeStyle = `rgba(${rgb},${0.82 * (1 - t)})`;
    ctx.lineWidth = 8;
    ctx.beginPath();
    ctx.arc(cx, cy, 140 + t * 620, 0, Math.PI * 2);
    ctx.stroke();
    if (t < 0.72) {
      rect(cx - 260, cy - 42, 520, 84, 'rgba(17,24,42,0.92)', screenTransition.accent, 18);
      text(screenTransition.label, cx, cy + 9, 30, '#ffffff', 'center');
    }
    ctx.restore();
  }

  function render() {
    targets = [];
    if (screenMode === 'OPENING') {
      drawOpeningScreen();
      drawScreenTransition();
      return;
    }
    if (screenMode === 'LOBBY') {
      drawLobbyScreen();
      drawScreenTransition();
      return;
    }
    drawBackground();
    drawHud();
    drawPlanet();
    drawAi();
    drawPassingEncounter();
    drawPanel();
    drawNav();
    if (toastMessage && performance.now() < toastUntil) {
      rect(220, 910, W - 440, 66, 'rgba(17,24,42,0.96)', '#ffd75a', 18);
      text(toastMessage, W / 2, 953, 27, '#ffffff', 'center');
    }
    drawIncomeFloaters();
    drawEncounterBurst();
    drawRewardBurst();
    drawUpgradeBurst();
    if ((state.tutorialVersion || 0) < tutorialVersion) drawIntroOverlay();
    drawScreenTransition();
  }

  function startUpgradeBurst(title, levelValue, bounds, effect = 'ENERGY') {
    upgradeBurst = {
      title,
      levelValue,
      bounds: { ...bounds },
      effect,
      ageMs: 0,
      particles: Array.from({ length: 30 }, (_, i) => ({
        angle: -Math.PI * 0.9 + (i / 29) * Math.PI * 0.8,
        speed: 70 + (i % 6) * 14,
        size: 4 + (i % 4),
      })),
    };
  }

  function drawUpgradeBurst() {
    if (!upgradeBurst) return;
    const life = 2200;
    const t = Math.min(1, upgradeBurst.ageMs / life);
    const alpha = t < 0.74 ? 1 : Math.max(0, (1 - t) / 0.26);
    const b = upgradeBurst.bounds;
    const cx = b.x + b.w * 0.5;
    const cy = b.y + b.h * 0.5;
    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.shadowColor = '#62f2ff';
    ctx.shadowBlur = 28 * (1 - t * 0.4);
    rect(b.x - 6, b.y - 6, b.w + 12, b.h + 12, `rgba(98,242,255,${0.12 + 0.2 * alpha})`, '#ffd75a', 16);
    ctx.shadowBlur = 0;
    ctx.strokeStyle = `rgba(98,242,255,${0.9 * alpha})`;
    ctx.lineWidth = 6;
    ctx.beginPath();
    ctx.arc(cx, cy, 32 + t * 165, 0, Math.PI * 2);
    ctx.stroke();
    upgradeBurst.particles.forEach((p, i) => {
      const dist = p.speed * Math.sin(Math.min(1, t) * Math.PI * 0.84);
      const x = cx + Math.cos(p.angle) * dist;
      const y = cy + Math.sin(p.angle) * dist - t * 46;
      ctx.fillStyle = `rgba(${i % 3 === 0 ? '255,215,90' : i % 3 === 1 ? '98,242,255' : '114,245,128'},${alpha})`;
      ctx.beginPath();
      ctx.arc(x, y, p.size, 0, Math.PI * 2);
      ctx.fill();
    });
    rect(cx - 188, cy - 30 - t * 22, 376, 58, 'rgba(17,24,42,0.96)', '#ffd75a', 15);
    text('LEVEL UP', cx, cy + 5 - t * 22, 27, '#ffffff', 'center');
    text(`Lv.${upgradeBurst.levelValue}`, cx + 146, cy + 5 - t * 22, 21, '#72f580', 'center');
    ctx.restore();
  }

  function drawIncomeFloaters() {
    if (!incomeFloaters.length) return;
    ctx.save();
    incomeFloaters.forEach((floater) => {
      const t = floater.ageMs / 1550;
      const alpha = t < 0.72 ? 1 : Math.max(0, (1 - t) / 0.28);
      const x = floater.x + Math.sin(t * Math.PI * 2) * 16;
      const y = floater.y - t * 92;
      ctx.globalAlpha = alpha;
      ctx.shadowColor = floater.color;
      ctx.shadowBlur = 12;
      rect(x - 92, y - 34, 184, 44, 'rgba(17,24,42,0.78)', floater.color, 16);
      text(floater.label, x, y - 6, 24, '#ffffff', 'center');
      text(floater.source, x, y + 15, 15, floater.color, 'center');
    });
    ctx.restore();
  }

  function drawPassingEncounter() {
    if (!passingEncounter) return;
    const e = passingEncounter;
    const wobble = Math.sin(e.ageMs / 420) * 0.08;
    const size = e.size * (1 + Math.sin(e.ageMs / 600) * 0.025);
    ctx.save();
    ctx.shadowColor = encounterColor(e.def.type);
    ctx.shadowBlur = 22;
    drawImageRotated(e.def.asset, e.x, e.y, size, e.angle + wobble, 1);
    ctx.restore();
    ctx.strokeStyle = hexToRgba(encounterColor(e.def.type), 0.55 + 0.25 * Math.sin(e.ageMs / 230));
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.arc(e.x, e.y, size * 0.48, 0, Math.PI * 2);
    ctx.stroke();
    rect(e.x - 64, e.y + size * 0.42, 128, 34, 'rgba(17,24,42,0.76)', encounterColor(e.def.type), 12);
    text('탭 보상', e.x, e.y + size * 0.42 + 23, 18, '#ffffff', 'center');
    addTarget(e.x - size * 0.58, e.y - size * 0.58, size * 1.16, size * 1.16, collectPassingEncounter, e.def.title);
  }

  function drawEncounterBurst() {
    if (!encounterBurst) return;
    const t = Math.min(1, encounterBurst.ageMs / 1200);
    const alpha = Math.max(0, 1 - t);
    const size = 180 + t * 280;
    ctx.save();
    ctx.globalAlpha = alpha;
    ctx.shadowColor = encounterBurst.color;
    ctx.shadowBlur = 30 * alpha;
    drawImageRotated(encounterBurst.asset, encounterBurst.x, encounterBurst.y, size, t * Math.PI, 0.82);
    ctx.strokeStyle = hexToRgba(encounterBurst.color, alpha * 0.85);
    ctx.lineWidth = 5;
    ctx.beginPath();
    ctx.arc(encounterBurst.x, encounterBurst.y, 42 + t * 180, 0, Math.PI * 2);
    ctx.stroke();
    ctx.restore();
  }

  function drawRewardBurst() {
    if (!rewardBurst) return;
    const elapsed = performance.now() - rewardBurst.startedAt;
    const life = 1650;
    if (elapsed > life) {
      rewardBurst = null;
      return;
    }
    const t = elapsed / life;
    const cx = W / 2;
    const cy = 880;
    ctx.save();
    rewardBurst.particles.forEach((p, i) => {
      const dist = p.speed * Math.sin(Math.min(1, t) * Math.PI * 0.82);
      const x = cx + Math.cos(p.angle) * dist;
      const y = cy + Math.sin(p.angle) * dist * 0.72 - t * 45;
      ctx.fillStyle = `rgba(${i % 3 === 0 ? '255,215,90' : i % 3 === 1 ? '98,242,255' : '114,245,128'},${1 - t})`;
      ctx.beginPath();
      ctx.arc(x, y, p.size, 0, Math.PI * 2);
      ctx.fill();
    });
    const alpha = t < 0.82 ? 1 : (1 - t) / 0.18;
    ctx.globalAlpha = Math.max(0, alpha);
    rect(cx - 250, cy - 74 - t * 36, 500, 112, 'rgba(17,24,42,0.96)', '#ffd75a', 18);
    text(rewardBurst.title, cx, cy - 30 - t * 36, 29, '#ffffff', 'center');
    text(rewardBurst.detail, cx, cy + 8 - t * 36, 21, '#a0f6ff', 'center');
    ctx.restore();
  }

  function drawIntroOverlay() {
    ctx.fillStyle = 'rgba(2,6,14,0.76)';
    ctx.fillRect(0, 0, W, H);
    rect(70, 470, W - 140, 610, 'rgba(17,24,42,0.98)', '#62f2ff', 22);
    drawImage(aiAssetNames[state.alignment], 84, 500, 150, 150, 1);
    text('AI 코어 기동', 258, 538, 34);
    text('행성을 되살리고 은하를 확장하세요.', 258, 578, 24, '#a0f6ff');
    const lines = [
      '중앙 행성: 터치하면 에너지와 복원도가 즉시 증가합니다.',
      '업그레이드: 자동 생산, 터치 보상, 오프라인 보상을 강화합니다.',
      '이동: 행성을 완성하고 비용을 모아 다음 행성으로 워프합니다.',
      'AI/이벤트: 선택이 누적되어 AI 성향과 보상 방향을 바꿉니다.',
      '상점: 광고 보상과 결제 테스트 상품은 진행 시간을 줄입니다.',
    ];
    lines.forEach((line, i) => text(line, 110, 710 + i * 48, 24, i === 0 ? '#ffffff' : '#d2e8f0'));
    rect(222, 990, W - 444, 70, 'rgba(35,72,98,0.95)', '#ffd75a', 16);
    text('시작', W / 2, 1036, 28, '#ffffff', 'center');
    addTarget(0, 0, W, H, () => {}, '튜토리얼 배경');
    addTarget(222, 990, W - 444, 70, () => {
      state.tutorialVersion = tutorialVersion;
      saveState();
    }, '튜토리얼 시작');
  }

  function loop(time) {
    const dt = Math.min(0.25, (time - lastTime) / 1000);
    lastTime = time;
    update(dt);
    render();
    requestAnimationFrame(loop);
  }

  function pointerToCanvas(event) {
    const bounds = canvas.getBoundingClientRect();
    const x = (event.clientX - bounds.left) * (W / bounds.width);
    const y = (event.clientY - bounds.top) * (H / bounds.height);
    return { x, y };
  }

  function pointInRect(p, r) {
    return !!r && p.x >= r.x && p.x <= r.x + r.w && p.y >= r.y && p.y <= r.y + r.h;
  }

  canvas.addEventListener('pointerdown', (event) => {
    const p = pointerToCanvas(event);
    if (screenMode === 'GAME' && currentPanel === 'TRAVEL' && pointInRect(p, travelMapBounds)) {
      travelMapDrag = { lastX: p.x, totalX: 0, moved: false };
      canvas.setPointerCapture?.(event.pointerId);
      event.preventDefault();
    }
  });

  canvas.addEventListener('pointermove', (event) => {
    if (!travelMapDrag) return;
    const p = pointerToCanvas(event);
    const dx = p.x - travelMapDrag.lastX;
    travelMapDrag.lastX = p.x;
    travelMapDrag.totalX += dx;
    if (Math.abs(travelMapDrag.totalX) > 6) travelMapDrag.moved = true;
    clampTravelMapScroll(travelMapScroll - dx);
    event.preventDefault();
    render();
  });

  canvas.addEventListener('pointerup', (event) => {
    const p = pointerToCanvas(event);
    if (travelMapDrag) {
      const wasDragging = travelMapDrag.moved;
      travelMapDrag = null;
      if (wasDragging) {
        event.preventDefault();
        return;
      }
    }
    const target = [...targets].reverse().find((t) => p.x >= t.x && p.x <= t.x + t.w && p.y >= t.y && p.y <= t.y + t.h);
    if (target) target.action();
  });

  canvas.addEventListener('pointercancel', () => {
    travelMapDrag = null;
  });

  canvas.addEventListener('wheel', (event) => {
    const bounds = canvas.getBoundingClientRect();
    const p = {
      x: (event.clientX - bounds.left) * (W / bounds.width),
      y: (event.clientY - bounds.top) * (H / bounds.height),
    };
    if (screenMode === 'GAME' && currentPanel === 'TRAVEL' && pointInRect(p, travelMapBounds)) {
      clampTravelMapScroll(travelMapScroll + event.deltaY + event.deltaX);
      event.preventDefault();
      render();
    }
  }, { passive: false });

  window.addEventListener('keydown', (event) => {
    if (event.key.toLowerCase() === 'f') {
      if (!document.fullscreenElement) canvas.requestFullscreen?.();
      else document.exitFullscreen?.();
    }
    if (event.key === 'Enter' && screenMode !== 'GAME') enterGame('UPGRADE');
    if (event.key >= '1' && event.key <= '5') {
      const panel = panels[Number(event.key) - 1];
      if (screenMode !== 'GAME') enterGame(panel);
      else switchPanel(panel);
    }
  });

  setInterval(saveState, 5000);

  window.advanceTime = (ms) => {
    const steps = Math.max(1, Math.round(ms / (1000 / 60)));
    for (let i = 0; i < steps; i += 1) update(1 / 60);
    render();
  };

  window.forceEncounterForTest = () => {
    if (screenMode !== 'GAME') enterGame('UPGRADE');
    spawnPassingEncounter(true);
    render();
  };

  window.render_game_to_text = () => JSON.stringify({
    note: '캔버스 좌표: 원점은 왼쪽 위, x는 오른쪽, y는 아래쪽입니다.',
    screen: screenMode === 'OPENING' ? '오프닝' : screenMode === 'LOBBY' ? '로비' : '게임',
    panel: panelLabels[currentPanel],
    planet: planets[state.planetIndex].name,
    stage: stages[stageIndex()],
    progressPercent: Math.floor((state.progress / maxProgress()) * 100),
    resources: {
      energy: Number(state.resources.energy.toFixed(2)),
      biomass: Number(state.resources.biomass.toFixed(2)),
      aiData: Number(state.resources.aiData.toFixed(2)),
    },
    productionPerSecond: production(),
    colonyProductionPerSecond: colonyProduction(),
    aiAlignment: alignmentLabels[state.alignment],
    activeEvent: state.activeEvent === null ? null : randomEvents[state.activeEvent].title,
    travel: {
      nextPlanet: planets[state.planetIndex + 1]?.name || null,
      planetCount: planets.length,
      unlockedPlanetMax: maxUnlockedIndex(),
      stageReady: stageIndex() === 3,
      cost: walletText(planets[state.planetIndex].travel),
      costReady: canAfford(planets[state.planetIndex].travel),
      shortage: shortageText(planets[state.planetIndex].travel),
      travelTimeReady: travelTimeReady(),
      travelWaitRemaining: formatDuration(travelWaitRemainingMs()),
      currentLocation: `${state.planetIndex + 1}/${planets.length}`,
      mapScroll: Number(travelMapScroll.toFixed(1)),
      visibleApprox: `${Math.floor(travelMapScroll / travelMapMetrics().nodeSpacing) + 1}-${Math.min(planets.length, Math.floor(travelMapScroll / travelMapMetrics().nodeSpacing) + 4)}/${planets.length}`,
      canMove: state.planetIndex === planets.length - 1 ? stageIndex() === 3 : state.planetIndex + 1 <= maxUnlockedIndex() || (stageIndex() === 3 && canAfford(planets[state.planetIndex].travel) && travelTimeReady()),
    },
    visualGrowth: (() => {
      const visual = currentVisualStats();
      return {
        upgradeLevels: visual.upgradeLevels,
        visualTier: visual.visualTier,
        intensity: Number(visual.intensity.toFixed(2)),
        effectLevels: visual.effectLevels,
      };
    })(),
    encounter: passingEncounter ? {
      title: passingEncounter.def.title,
      type: passingEncounter.def.type,
      x: Number(passingEncounter.x.toFixed(1)),
      y: Number(passingEncounter.y.toFixed(1)),
    } : null,
    levelUpEffect: upgradeBurst ? `${upgradeBurst.title} Lv.${upgradeBurst.levelValue}` : null,
    incomeFloaters: incomeFloaters.map((f) => f.label).slice(0, 5),
    tutorialVisible: screenMode === 'GAME' && (state.tutorialVersion || 0) < tutorialVersion,
    transition: screenTransition ? screenTransition.label : null,
    visibleTargets: targets.map((t) => t.label).slice(0, 14),
  });

  loadAssets().then(() => {
    const offlineSeconds = Math.min(8 * 60 * 60, Math.max(0, Math.floor((Date.now() - (state.lastSaved || Date.now())) / 1000)));
    if (offlineSeconds > 60) {
      addWallet(scaleWallet(production(), offlineSeconds * 0.35));
      toast(`오프라인 ${Math.floor(offlineSeconds / 60)}분`);
    }
    saveState();
    requestAnimationFrame((t) => {
      lastTime = t;
      loop(t);
    });
  });
})();
