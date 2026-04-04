import openpyxl
import json

def xlsx_to_json_list():
    workbook = openpyxl.load_workbook("input.xlsx")
    sheet = workbook.active

    data = []

    # Get headers from the first row
    headers = []
    for cell in sheet[1]:
        headers.append(cell.value)

    # Iterate over the remaining rows
    row_count = 1
    for row in sheet.iter_rows(min_row=2, values_only=True):
        row_dict = {"count": row_count}
        row_count+=1
        for key, value in zip(headers, row):
            row_dict[key] = value
        data.append(row_dict)

    return data

if __name__ == "__main__":
    result = xlsx_to_json_list()

    with open("output.json", "w", encoding="utf-8") as f:
        json.dump(result, f, indent=4, ensure_ascii=False)