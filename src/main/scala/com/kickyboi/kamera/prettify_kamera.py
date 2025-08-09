import json

limited_5_stars = ["Venti", "Klee", "Tartaglia", "Zhongli", "Albedo", "Ganyu", "Xiao", "HuTao", "Eula", "KaedeharaKazuha", "KamisatoAyaka", "Yoimiya", "RaidenShogun", "SangonomiyaKokomi",
                   "AratakiItto", "Shenhe", "YaeMiko", "KamisatoAyato", "Yelan", "Cyno", "Nilou", "Nahida", "Wanderer", "Alhaitham", "Baizhu", "Lyney", "Neuvillette", "Wriotheslay", "Furina", "Navia",
                   "Xianyun", "Chiori", "Arlecchino", "Clorinde", "Sigewinne", "Emilie", "Mualani", "Kinich", "Xilonen", "Chasca", "Citlali", "Mavuika", "Varesa", "Escoffier", "Skirk", "Ineffa"]
standard_5_stars = ["Aloy", "Jean", "Diluc", "Qiqi", "Mona", "Keqing", "Tighnari", "Dehya", "YumemizukiMizuki"]

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

with open('input.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

max_character_name_length = max(len(character["key"]) for character in data["characters"])
max_weapon_name_length = max(len(weapon["key"]) for weapon in data["weapons"])
max_artifact_set_name_length = max(len(artifact["setKey"]) for artifact in data["artifacts"])

def format_character(character):
    key = "\"" + character['key'] + "\""
    level = character['level']
    const = character['constellation']
    asc = character['ascension']
    talent = character['talent']

    auto  = talent['auto']
    skill = talent['skill']
    burst = talent['burst']

    return (
        f"{{ "
        f"\"key\": {key:<{max_character_name_length + 2}}, "
        f"\"level\": {level:>2}, "
        f"\"constellation\": {const}, "
        f"\"talent\": {{ "
        f"\"auto\": {auto:>2}, "
        f"\"skill\": {skill:>2}, "
        f"\"burst\": {burst:>2} "
        f"}}, "
        f"\"ascension\": {asc} "
        f"}}"
    )

def format_weapon(weapon):
    key = "\"" + weapon['key'] + "\""
    level = weapon['level']
    ascension = weapon['ascension']
    refinement = weapon['refinement']
    location = "\"" + weapon['location'] + "\""
    lock = "true" if weapon["lock"] else "false"
    id = weapon['id']

    return (
        f"{{ "
        f"\"key\": {key:<{max_weapon_name_length + 2}}, "
        f"\"level\": {level:>2}, "
        f"\"ascension\": {ascension}, "
        f"\"refinement\": {refinement}, "
        f"\"location\": {location:<{max_character_name_length + 2}}, "
        f"\"lock\": {lock:<5}, "
        f"\"id\": {id:>4}"
        f" }}"
    )

def format_artifact(artifact):

    def format_sub(sub):
        sub_name = "\"" + sub["key"]  + "\""
        return f"{{\"key\" :{sub_name:<11}, \"value\" :{sub['value']:>6}}}"

    slotKey = "\"" + artifact['slotKey'] + "\""
    setKey = "\"" + artifact['setKey'] + "\""
    mainStatKey = "\"" + artifact['mainStatKey'] + "\""
    level = artifact['level']
    rarity = artifact['rarity']
    location = "\"" + artifact['location'] + "\""
    lock = "true" if artifact["lock"] else "false"
    id = artifact['id']
    substats = ', '.join([format_sub(sub) for sub in sorted(artifact['substats'], key=lambda x: sub_order.get(x['key'], float('inf')))])
    #substats = ', '.join([format_sub(sub) for sub in artifact['substats']])

    return (
        f"{{ "
        f"\"setKey\": {setKey:<{max_artifact_set_name_length + 2}}, "
        f"\"slotKey\": {slotKey:<9}, "
        f"\"mainStatKey\": {mainStatKey:<15}, "
        f"\"rarity\": {rarity}, "
        f"\"level\": {level:>2}, "
        f"\"substats\": [ {substats:<154} ], "
        f"\"location\": {location:<{max_character_name_length + 2}}, "
        f"\"lock\": {lock:<5}, "
        f"\"id\": {id:>4}"
        f" }}"
    )

handler_map = {
    "characters": format_character,
    "weapons": format_weapon,
    "artifacts": format_artifact,
}

def output_section(section):
    data[section] = sorted(data[section], key=sorting_map[section])
    return "  \"" + section + "\": [\n" + ',\n'.join(["    " + handler_map[section](item) for item in data[section]]) + '\n' + "  ]"

with open('output.json', 'w') as file:
    file.write(
        f"{{\n"
        f"  \"format\": \"{data['format']}\",\n"
        f"  \"version\": {data['version']},\n"
        f"  \"kamera_version\": \"{data['kamera_version']}\",\n"
        f"  \"source\": \"{data['source']}\",\n"
    )

    sections = list(filter(lambda section: section in data, ["characters", "weapons", "artifacts"]))
    file.write(',\n'.join([output_section(section) for section in sections]))

    file.write(f"\n}}\n")
