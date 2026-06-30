limited_5_stars = ["Venti", "Klee", "Tartaglia", "Zhongli", "Albedo", "Ganyu", "Xiao", "HuTao", "Eula", "KaedeharaKazuha",
                   "KamisatoAyaka", "Yoimiya", "RaidenShogun", "SangonomiyaKokomi", "AratakiItto", "Shenhe", "YaeMiko", "KamisatoAyato", "Yelan",
                   "Cyno", "Nilou", "Nahida", "Wanderer", "Alhaitham", "Baizhu",
                   "Lyney", "Neuvillette", "Wriotheslay", "Furina", "Navia", "Xianyun", "Chiori", "Arlecchino", "Clorinde", "Sigewinne", "Emilie",
                   "Mualani", "Kinich", "Xilonen", "Chasca", "Citlali", "Mavuika", "Varesa", "Escoffier", "Skirk", "Ineffa",
                   "Lauma", "Flins", "Nefer", "Durin", "Columbina", "Zibai", "Varka", "Linnea", "Nicole", "Lohen", "Sandrone"
                   ]
standard_5_stars = ["Aloy", "Jean", "Diluc", "Qiqi", "Mona", "Keqing", "Tighnari", "Dehya", "YumemizukiMizuki"]

artifact_sets_5 = ["Instructor", "TheExile",
                   "GladiatorsFinale", "WanderersTroupe",
                   "NoblesseOblige", "BloodstainedChivalry",
                   "CrimsonWitchOfFlames", "Lavawalker",
                   "ThunderingFury", "Thundersoother",
                   "ViridescentVenerer", "MaidenBeloved",
                   "ArchaicPetra", "RetracingBolide",
                   "BlizzardStrayer", "HeartOfDepth",
                   "TenacityOfTheMillelith", "PaleFlame",
                   "ShimenawasReminiscence", "EmblemOfSeveredFate",
                   "HuskOfOpulentDreams", "OceanHuedClam",
                   "VermillionHereafter", "EchoesOfAnOffering",
                   "DeepwoodMemories", "GildedDreams",
                   "DesertPavilionChronicle", "FlowerOfParadiseLost",
                   "NymphsDream", "VourukashasGlow",
                   "MarechausseeHunter", "GoldenTroupe",
                   "SongOfDaysPast", "NighttimeWhispersInTheEchoingWoods",
                   "FragmentOfHarmonicWhimsy", "UnfinishedReverie",
                   "ScrollOfTheHeroOfCinderCity", "ObsidianCodex",
                   "LongNightsOath", "FinaleOfTheDeepGalleries",
                   "NightOfTheSkysUnveiling", "SilkenMoonsSerenade",
                   "AubadeOfMorningstarAndMoon", "ADayCarvedFromRisingWinds",
                   "CelestialGift", "DisenchantmentInDeepShadow"
                   ]

category_order_map = {}
for name in limited_5_stars:
    category_order_map[name] = 0
for name in standard_5_stars:
    category_order_map[name] = 1

custom_slot_order = ['flower', 'plume', 'sands', 'goblet', 'circlet']
slot_order = {key: i for i, key in enumerate(custom_slot_order)}

custom_main_stat_order = ['pyro_dmg_', 'hydro_dmg_', 'electro_dmg_', 'cryo_dmg_', 'geo_dmg_', 'anemo_dmg_', 'dendro_dmg_', 'physical_dmg_', 'critRate_', 'critDMG_', 'atk_', 'atk', 'hp_', 'hp', 'def_', 'def', 'enerRech_', 'em']
main_stat_order = {k: i for i, k in enumerate(custom_main_stat_order)}

custom_sub_order = ['critRate_', 'critDMG_', 'atk_', 'atk', 'hp_', 'hp', 'def_', 'def', 'enerRech_', 'em']
sub_order = {k: i for i, k in enumerate(custom_sub_order)}

sorting_map = {
    "characters": lambda x: (-x['level'], category_order_map.get(x['key'], 2), -x['constellation']),
    "weapons": lambda x: (-x['level'], x['key']),
    "artifacts": lambda x: (x['setKey'], slot_order.get(x['slotKey'], float('inf')), main_stat_order.get(x['mainStatKey'], float('inf')), -x['level']),
    "substats": lambda x: sub_order.get(x['key'], float('inf'))
}

#custom_sorting_map = {topic: (lambda y         : sorted(y, key=sorting_map[topic])) for topic in sorting_map} // doesn't work for funny python reasons
custom_sorting_map = {topic: (lambda y, t=topic: sorted(y, key=sorting_map[t])) for topic in sorting_map}
default_sorting_map = {key: lambda x: x for key in custom_sorting_map}

custom_section_order = ["characters", "weapons", "artifacts"]
default_section_order = ["weapons", "artifacts", "characters"]
