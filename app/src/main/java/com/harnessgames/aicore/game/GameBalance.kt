package com.harnessgames.aicore.game

object GameBalance {
    const val offlineCapSeconds: Long = 8 * 60 * 60
    const val offlineBaseRate: Double = 0.35
    const val adBoostMultiplier: Double = 2.0
    const val adBoostSeconds: Long = 10 * 60
    const val minTravelSeconds: Long = 7 * 24 * 60 * 60

    val planets = listOf(
        PlanetDef(
            id = "ash_prime",
            name = "잿빛 프라임",
            theme = "청록빛 강이 잠든 갈라진 현무암 행성",
            baseProduction = ResourceWallet(energy = 1.2, biomass = 0.03, aiData = 0.01),
            travelCost = ResourceWallet(energy = 180000.0, biomass = 3500.0, aiData = 1100.0),
            stageThresholds = listOf(80.0, 260.0, 620.0),
        ),
        PlanetDef(
            id = "verdant_zero",
            name = "녹원 제로",
            theme = "네온 정글이 되살아나는 복원 행성",
            baseProduction = ResourceWallet(energy = 2.7, biomass = 0.14, aiData = 0.025),
            travelCost = ResourceWallet(energy = 520000.0, biomass = 12000.0, aiData = 4200.0),
            stageThresholds = listOf(260.0, 850.0, 2050.0),
        ),
        PlanetDef(
            id = "foundry_moon",
            name = "주조 위성",
            theme = "용융 자원맥을 품은 산업 고리 위성",
            baseProduction = ResourceWallet(energy = 6.0, biomass = 0.23, aiData = 0.065),
            travelCost = ResourceWallet(energy = 1800000.0, biomass = 35000.0, aiData = 15000.0),
            stageThresholds = listOf(850.0, 2900.0, 7200.0),
        ),
        PlanetDef(
            id = "echo_ruin",
            name = "메아리 유적",
            theme = "부서진 신호 배열로 뒤덮인 고대 행성",
            baseProduction = ResourceWallet(energy = 14.0, biomass = 0.42, aiData = 0.18),
            travelCost = ResourceWallet(energy = 6200000.0, biomass = 98000.0, aiData = 52000.0),
            stageThresholds = listOf(2800.0, 9800.0, 23000.0),
        ),
        PlanetDef(
            id = "aurora_nexus",
            name = "오로라 넥서스",
            theme = "오로라 바다를 품은 연결 행성",
            baseProduction = ResourceWallet(energy = 34.0, biomass = 0.9, aiData = 0.52),
            travelCost = ResourceWallet(energy = 22000000.0, biomass = 260000.0, aiData = 140000.0),
            stageThresholds = listOf(8800.0, 31000.0, 76000.0),
        ),
        PlanetDef(
            id = "glass_nebula",
            name = "유리 성운",
            theme = "투명한 대륙 아래 별빛 수로가 흐르는 행성",
            baseProduction = ResourceWallet(energy = 82.0, biomass = 1.8, aiData = 1.25),
            travelCost = ResourceWallet(energy = 76000000.0, biomass = 680000.0, aiData = 380000.0),
            stageThresholds = listOf(26000.0, 92000.0, 215000.0),
        ),
        PlanetDef(
            id = "abyss_station",
            name = "심연 정거장",
            theme = "버려진 궤도 정거장과 바다 행성이 맞물린 구역",
            baseProduction = ResourceWallet(energy = 195.0, biomass = 3.5, aiData = 2.8),
            travelCost = ResourceWallet(energy = 260000000.0, biomass = 1700000.0, aiData = 900000.0),
            stageThresholds = listOf(76000.0, 260000.0, 640000.0),
        ),
        PlanetDef(
            id = "lumina_desert",
            name = "루미나 사막",
            theme = "빛나는 모래 폭풍과 매장된 태양판의 사막 행성",
            baseProduction = ResourceWallet(energy = 460.0, biomass = 6.4, aiData = 6.2),
            travelCost = ResourceWallet(energy = 880000000.0, biomass = 4200000.0, aiData = 2200000.0),
            stageThresholds = listOf(210000.0, 720000.0, 1800000.0),
        ),
        PlanetDef(
            id = "titan_greenhouse",
            name = "타이탄 온실",
            theme = "거대 생체 돔이 대기권을 되살리는 중력 행성",
            baseProduction = ResourceWallet(energy = 1080.0, biomass = 12.5, aiData = 13.5),
            travelCost = ResourceWallet(energy = 2900000000.0, biomass = 9800000.0, aiData = 5100000.0),
            stageThresholds = listOf(620000.0, 2100000.0, 5200000.0),
        ),
        PlanetDef(
            id = "chrono_ruin",
            name = "크로노 폐허",
            theme = "시간 균열 사이에 고대 도시가 반복되는 유적 행성",
            baseProduction = ResourceWallet(energy = 2500.0, biomass = 24.0, aiData = 31.0),
            travelCost = ResourceWallet(energy = 9200000000.0, biomass = 22500000.0, aiData = 12000000.0),
            stageThresholds = listOf(1800000.0, 6200000.0, 15000000.0),
        ),
        PlanetDef(
            id = "sanctum_ring",
            name = "신성 고리",
            theme = "행성보다 큰 복원 고리가 궤도를 봉합하는 성역",
            baseProduction = ResourceWallet(energy = 5800.0, biomass = 48.0, aiData = 70.0),
            travelCost = ResourceWallet(energy = 28000000000.0, biomass = 52000000.0, aiData = 27000000.0),
            stageThresholds = listOf(5200000.0, 18000000.0, 43000000.0),
        ),
        PlanetDef(
            id = "eternal_core",
            name = "이터널 코어",
            theme = "멸망 에너지의 중심에 남은 최종 감정 코어",
            baseProduction = ResourceWallet(energy = 13500.0, biomass = 95.0, aiData = 160.0),
            travelCost = ResourceWallet(energy = 0.0, biomass = 0.0, aiData = 0.0),
            stageThresholds = listOf(15000000.0, 52000000.0, 125000000.0),
        ),
    )

    val upgrades = listOf(
        UpgradeDef("solar_lattice", "태양 격자", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 15.0), 1.18, 0.12, 40),
        UpgradeDef("pulse_drill", "펄스 드릴", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 55.0), 1.2, 0.18, 35),
        UpgradeDef("ion_roots", "이온 뿌리", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 120.0, biomass = 2.0), 1.22, 0.2, 30),
        UpgradeDef("memory_leaf", "기억 잎", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 180.0, aiData = 1.0), 1.24, 0.18, 30),
        UpgradeDef("tap_resonator", "탭 공명기", "터치 보상 증가", UpgradeEffect.TAP_POWER, ResourceWallet(energy = 90.0), 1.2, 0.22, 30),
        UpgradeDef("nano_weather", "나노 기상", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 220.0, biomass = 4.0), 1.21, 0.16, 30),
        UpgradeDef("sleep_protocol", "수면 프로토콜", "오프라인 보상 증가", UpgradeEffect.OFFLINE_GAIN, ResourceWallet(energy = 350.0, aiData = 2.0), 1.25, 0.1, 20),
        UpgradeDef("fusion_petal", "융합 꽃잎", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 480.0, biomass = 8.0), 1.23, 0.2, 25),
        UpgradeDef("biosphere_mesh", "생물권 망", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 700.0, biomass = 12.0), 1.24, 0.22, 25),
        UpgradeDef("oracle_cache", "예언 캐시", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 950.0, aiData = 8.0), 1.25, 0.22, 25),
        UpgradeDef("core_overclock", "코어 오버클럭", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 1400.0, aiData = 10.0), 1.27, 0.24, 20),
        UpgradeDef("healing_orbit", "치유 궤도", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 1900.0, biomass = 28.0), 1.27, 0.24, 20),
        UpgradeDef("drone_swarm", "드론 군집", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 2600.0, biomass = 35.0), 1.28, 0.26, 20),
        UpgradeDef("seed_vault", "씨앗 금고", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 3400.0, biomass = 48.0), 1.28, 0.26, 20),
        UpgradeDef("sentience_bus", "지성 버스", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 4400.0, aiData = 32.0), 1.29, 0.28, 20),
        UpgradeDef("warp_calibrator", "워프 보정기", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 5600.0, aiData = 42.0), 1.3, 0.28, 18),
        UpgradeDef("quiet_factory", "정숙 공장", "오프라인 보상 증가", UpgradeEffect.OFFLINE_GAIN, ResourceWallet(energy = 7200.0, biomass = 80.0), 1.31, 0.16, 18),
        UpgradeDef("risk_reactor", "위험 반응로", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 9200.0, aiData = 60.0), 1.32, 0.34, 16),
        UpgradeDef("garden_mind", "정원 지성", "바이오매스와 복원 보정", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 11800.0, biomass = 120.0), 1.33, 0.34, 16),
        UpgradeDef("nexus_dream", "넥서스 꿈", "AI 데이터 체계 강화", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 15000.0, aiData = 120.0), 1.35, 0.4, 12),
        UpgradeDef("starlight_condenser", "별빛 응축기", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 22000.0, aiData = 180.0), 1.36, 0.42, 12),
        UpgradeDef("glass_bloom", "유리 꽃망", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 26000.0, biomass = 260.0), 1.35, 0.38, 12),
        UpgradeDef("emotion_archive", "감정 기록소", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 32000.0, aiData = 220.0), 1.36, 0.42, 12),
        UpgradeDef("nebula_repair", "성운 수복기", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 42000.0, biomass = 360.0), 1.34, 0.34, 12),
        UpgradeDef("abyss_turbine", "심연 터빈", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 64000.0, biomass = 520.0), 1.37, 0.46, 12),
        UpgradeDef("tidal_seed_bank", "조수 씨앗고", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 78000.0, biomass = 760.0), 1.36, 0.42, 12),
        UpgradeDef("signal_bathysphere", "신호 잠항구", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 96000.0, aiData = 520.0), 1.37, 0.48, 12),
        UpgradeDef("pressure_protocol", "압력 프로토콜", "오프라인 보상 증가", UpgradeEffect.OFFLINE_GAIN, ResourceWallet(energy = 120000.0, aiData = 700.0), 1.35, 0.22, 10),
        UpgradeDef("solar_mirage", "태양 신기루", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 170000.0, aiData = 900.0), 1.38, 0.52, 12),
        UpgradeDef("dune_roots", "사막 뿌리망", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 210000.0, biomass = 1400.0), 1.37, 0.48, 12),
        UpgradeDef("prism_memory", "프리즘 기억", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 260000.0, aiData = 1250.0), 1.38, 0.54, 12),
        UpgradeDef("heat_suture", "열 봉합기", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 330000.0, biomass = 1800.0), 1.36, 0.42, 12),
        UpgradeDef("gravity_orchard", "중력 과수원", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 460000.0, biomass = 2800.0), 1.39, 0.56, 12),
        UpgradeDef("dome_reactor", "돔 반응로", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 580000.0, aiData = 2300.0), 1.39, 0.58, 12),
        UpgradeDef("symbiosis_bus", "공생 버스", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 720000.0, aiData = 3100.0), 1.4, 0.6, 12),
        UpgradeDef("greenhouse_restorer", "온실 복원자", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 900000.0, biomass = 3600.0), 1.38, 0.5, 12),
        UpgradeDef("chrono_battery", "시간 전지", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 1300000.0, aiData = 5200.0), 1.41, 0.64, 12),
        UpgradeDef("loop_composter", "순환 퇴비장", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 1650000.0, biomass = 6200.0), 1.4, 0.62, 12),
        UpgradeDef("ruin_oracle", "폐허 예언기", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 2050000.0, aiData = 7600.0), 1.42, 0.68, 12),
        UpgradeDef("time_latch", "시간 걸쇠", "오프라인 보상 증가", UpgradeEffect.OFFLINE_GAIN, ResourceWallet(energy = 2600000.0, aiData = 9200.0), 1.39, 0.28, 10),
        UpgradeDef("ring_harvester", "고리 수확기", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 3800000.0, biomass = 11000.0), 1.43, 0.72, 12),
        UpgradeDef("sanctum_garden", "성역 정원", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 4800000.0, biomass = 15000.0), 1.42, 0.7, 12),
        UpgradeDef("choir_matrix", "합창 매트릭스", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 6100000.0, aiData = 18000.0), 1.43, 0.74, 12),
        UpgradeDef("orbit_mender", "궤도 봉합자", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 7800000.0, aiData = 22000.0), 1.41, 0.58, 12),
        UpgradeDef("eternal_furnace", "영원 화로", "초당 에너지 증가", UpgradeEffect.ENERGY_PRODUCTION, ResourceWallet(energy = 11500000.0, aiData = 35000.0), 1.45, 0.82, 12),
        UpgradeDef("origin_grove", "기원 수림", "초당 바이오매스 증가", UpgradeEffect.BIOMASS_PRODUCTION, ResourceWallet(energy = 14500000.0, biomass = 26000.0), 1.44, 0.78, 12),
        UpgradeDef("sentient_singularity", "감정 특이점", "초당 AI 데이터 증가", UpgradeEffect.AI_DATA_PRODUCTION, ResourceWallet(energy = 18500000.0, aiData = 42000.0), 1.45, 0.86, 12),
        UpgradeDef("rebirth_engine", "재탄생 엔진", "복원 속도 증가", UpgradeEffect.RESTORE_SPEED, ResourceWallet(energy = 24000000.0, biomass = 32000.0, aiData = 52000.0), 1.43, 0.68, 12),
    )

    val evolutions = listOf(
        EvolutionDef(
            alignment = AIAlignment.LIFE,
            title = "생명 프로토콜",
            description = "행성을 살아 있는 동료로 복원한다.",
            rewardText = "바이오매스 +20%, 오프라인 보상 +10%",
        ),
        EvolutionDef(
            alignment = AIAlignment.INDUSTRIAL,
            title = "산업 프로토콜",
            description = "복원망을 효율적인 생산 엔진으로 전환한다.",
            rewardText = "에너지 생산 +25%",
        ),
        EvolutionDef(
            alignment = AIAlignment.COMBAT,
            title = "전투 프로토콜",
            description = "불안정한 반응로를 받아들여 더 빠르게 확장한다.",
            rewardText = "터치 보상 +35%, AI 데이터 +15%",
        ),
    )

    val randomEvents = listOf(
        RandomEventDef("collapse_warning", "붕괴 경고", "코어 아래 균열선이 빠르게 번지고 있다.", listOf(
            EventOption("안정화", AIAlignment.LIFE, ResourceWallet(biomass = 5.0), 35.0, "지각이 살아 있는 리듬으로 가라앉았다."),
            EventOption("열 회수", AIAlignment.INDUSTRIAL, ResourceWallet(energy = 120.0), -15.0, "행성은 흔들렸지만 전력망은 가득 찼다."),
        )),
        RandomEventDef("alien_signal", "외계 신호", "행성의 어두운 면에서 약한 패턴이 반복된다.", listOf(
            EventOption("해독", AIAlignment.LIFE, ResourceWallet(aiData = 5.0), 20.0, "신호가 코어에게 절제를 가르쳤다."),
            EventOption("활용", AIAlignment.INDUSTRIAL, ResourceWallet(energy = 180.0), 0.0, "파형이 연료로 변환되었다."),
        )),
        RandomEventDef("ancient_structure", "고대 구조물", "네온 먼지 아래 묻혀 있던 탑이 깨어난다.", listOf(
            EventOption("보존", AIAlignment.LIFE, ResourceWallet(biomass = 8.0, aiData = 2.0), 30.0, "구조물이 생물권과 연결되었다."),
            EventOption("과부하", AIAlignment.COMBAT, ResourceWallet(energy = 260.0, aiData = 4.0), -25.0, "탑이 밝고 불안정하게 타올랐다."),
        )),
        RandomEventDef("resource_surge", "자원 폭주", "궤도 수집기에 에너지가 밀려든다.", listOf(
            EventOption("조절", AIAlignment.INDUSTRIAL, ResourceWallet(energy = 240.0), 10.0, "폭주 에너지가 안정적인 출력으로 바뀌었다."),
            EventOption("돌파", AIAlignment.COMBAT, ResourceWallet(energy = 420.0), -35.0, "코어가 급상승을 견뎌냈다."),
        )),
        RandomEventDef("ai_error", "AI 오류", "메모리 루프가 불가능한 선택을 제안한다.", listOf(
            EventOption("자가 점검", AIAlignment.LIFE, ResourceWallet(aiData = 4.0), 25.0, "코어가 인내를 학습했다."),
            EventOption("강제 확정", AIAlignment.COMBAT, ResourceWallet(aiData = 8.0), -20.0, "루프는 끊겼지만 흔적이 남았다."),
        )),
        RandomEventDef("unknown_life", "미확인 생명체", "작은 생명체들이 따뜻한 광맥 주변에 모인다.", listOf(
            EventOption("보호", AIAlignment.LIFE, ResourceWallet(biomass = 16.0), 45.0, "행성이 색으로 응답했다."),
            EventOption("채집", AIAlignment.INDUSTRIAL, ResourceWallet(aiData = 6.0), 5.0, "표본이 예측 정확도를 높였다."),
        )),
        RandomEventDef("warp_echo", "워프 메아리", "미래 항로가 3초 동안 나타난다.", listOf(
            EventOption("지도화", AIAlignment.INDUSTRIAL, ResourceWallet(aiData = 7.0), 20.0, "은하 지도가 더 선명해졌다."),
            EventOption("탐사 도약", AIAlignment.COMBAT, ResourceWallet(energy = 520.0), -40.0, "탐사체가 과부하 상태로 돌아왔다."),
        )),
        RandomEventDef("meteor_garden", "유성 정원", "유성우가 희귀 광물을 뿌린다.", listOf(
            EventOption("재배", AIAlignment.LIFE, ResourceWallet(biomass = 22.0), 25.0, "충돌 지점마다 새 생장이 뒤따랐다."),
            EventOption("노천 채굴", AIAlignment.INDUSTRIAL, ResourceWallet(energy = 580.0), -10.0, "유성우가 재고 목록으로 바뀌었다."),
        )),
        RandomEventDef("core_dream", "코어의 꿈", "AI가 멸망 이전의 우주를 기억한다.", listOf(
            EventOption("수용", AIAlignment.LIFE, ResourceWallet(aiData = 9.0), 30.0, "꿈이 공감으로 바뀌었다."),
            EventOption("무기화", AIAlignment.COMBAT, ResourceWallet(aiData = 14.0), -30.0, "꿈이 명령으로 바뀌었다."),
        )),
        RandomEventDef("silent_orbit", "침묵 궤도", "빈 위성이 모든 명령을 거울처럼 되돌린다.", listOf(
            EventOption("동기화", AIAlignment.INDUSTRIAL, ResourceWallet(energy = 760.0, aiData = 5.0), 15.0, "위성이 중계기로 전환되었다."),
            EventOption("거울 파괴", AIAlignment.COMBAT, ResourceWallet(energy = 1100.0), -45.0, "궤도가 거칠게 정리되었다."),
        )),
    )

    fun upgradeById(id: String): UpgradeDef = upgrades.first { it.id == id }
}
