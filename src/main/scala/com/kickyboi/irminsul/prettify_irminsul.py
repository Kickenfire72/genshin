import json
from data import *

"""
        NOTES
  You can choose between default and custom section order and sorting at lines 49 and 52
Default will keep the Irminsul output json as is, and only prettify it
Custom redefines them as described in the code

"""
# default_sorting_map or custom_sorting_map
used_sorting_map = custom_sorting_map

# default_section_order or custom_section_order
used_section_order = custom_section_order

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
        f"\"level\": {level:>3}, "
        f"\"constellation\": {const}, "
        f"\"ascension\": {asc}, "
        f"\"talent\": {{ "
        f"\"auto\": {auto:>2}, "
        f"\"skill\": {skill:>2}, "
        f"\"burst\": {burst:>2} "
        f"}} "
        f"}}"
    )

def format_weapon(weapon):
    key = "\"" + weapon['key'] + "\""
    level = weapon['level']
    ascension = weapon['ascension']
    refinement = weapon['refinement']
    location = "\"" + weapon['location'] + "\""
    lock = "true" if weapon["lock"] else "false"

    return (
        f"{{ "
        f"\"key\": {key:<{max_weapon_name_length + 2}}, "
        f"\"level\": {level:>2}, "
        f"\"ascension\": {ascension}, "
        f"\"refinement\": {refinement}, "
        f"\"location\": {location:<{max_character_name_length + 2}}, "
        f"\"lock\": {lock:<5}"
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
    totalRolls = artifact['totalRolls']
    rarity = artifact['rarity']
    location = "\"" + artifact['location'] + "\""
    lock = "true" if artifact["lock"] else "false"
    astralMark = "true" if artifact["astralMark"] else "false"
    elixerCrafted = "true" if artifact["elixerCrafted"] else "false"
    substats = ', '.join([format_sub(sub) for sub in used_sorting_map["substats"](artifact['substats'])])
    unactivatedSubstats = ', '.join([format_sub(sub) for sub in used_sorting_map["substats"](artifact['unactivatedSubstats'])])

    return (
        f"{{ "
        f"\"setKey\": {setKey:<{max_artifact_set_name_length + 2}}, "
        f"\"slotKey\": {slotKey:<9}, "
        f"\"rarity\": {rarity}, "
        f"\"mainStatKey\": {mainStatKey:<15}, "
        f"\"level\": {level:>2}, "
        f"\"totalRolls\": {totalRolls:>1}, "
        f"\"substats\": [ {substats:<154} ], "
        f"\"unactivatedSubstats\": [ {unactivatedSubstats:<37} ], "
        f"\"location\": {location:<{max_character_name_length + 2}}, "
        f"\"lock\": {lock:<5}, "
        f"\"astralMark\": {astralMark:<5}, "
        f"\"elixerCrafted\": {elixerCrafted:<5}"
        f" }}"
    )

handler_map = {
    "characters": format_character,
    "weapons": format_weapon,
    "artifacts": format_artifact,
}

def output_section(section):
    data[section] = used_sorting_map[section](data[section])
    return "  \"" + section + "\": [\n" + ',\n'.join(["    " + handler_map[section](item) for item in data[section]]) + '\n' + "  ]"

with open('output.json', 'w') as file:
    file.write(
        f"{{\n"
        f"  \"format\": \"{data['format']}\",\n"
        f"  \"version\": {data['version']},\n"
        f"  \"source\": \"{data['source']}\",\n"
    )

    sections = list(filter(lambda section: section in data, used_section_order))
    file.write(',\n'.join([output_section(section) for section in sections]))

    file.write(f"\n}}\n")
