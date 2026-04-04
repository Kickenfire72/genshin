import json

def compute_sliding_averages(file_path, window_size=400):
    # Load JSON data
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    base4 = [item["Pity"] for item in data if item["Star"] == 4]
    base5 = [item["Pity"] for item in data if item["Star"] == 5]
    averages = []

    # Compute sliding window averages
    for i in range(len(data) - window_size + 1):
        window = data[i:i + window_size]
        s4pities = [item["Pity"] for item in window if item["Star"] == 4]
        avg = sum(s4pities) / len(s4pities)

        averages.append(avg)
    return averages, base4, base5


if __name__ == "__main__":
    input_file = "output.json"   # your JSON file
    output_file = "averages.json"

    result, base4, base5 = compute_sliding_averages(input_file)

    print("4* pity average                   ", sum(base4) / len(base4))
    print("5* pity average                   ", sum(base5) / len(base5))
    print("4* min pity average over 400 pulls", min(result))
    print("4* max pity average over 400 pulls", max(result))
    print("4* avg pity average over 400 pulls", sum(result) / len(result))