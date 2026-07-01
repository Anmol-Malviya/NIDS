import pandas as pd
import joblib

model = joblib.load("model/model.pkl")
scaler = joblib.load("model/scaler.pkl")

dataset = pd.read_csv("dataset/cleaned_dataset.csv")

FEATURE_COLUMNS = dataset.columns[:-1]

def simulate_attack(class_id):

    rows = dataset[dataset["Label"] == class_id]

    sample = rows.sample(1)

    X = sample[FEATURE_COLUMNS]

    X_scaled = scaler.transform(X)

    prediction = model.predict(X_scaled)[0]

    return {

        "prediction": int(prediction),

        "source_port": int(sample["Source Port"].values[0]),

        "destination_port": int(sample["Destination Port"].values[0]),

        "protocol": int(sample["Protocol"].values[0]),

        "flow_duration": float(sample["Flow Duration"].values[0])

    }