def get_severity(attack):

    if attack == "BENIGN":
        return "Low"

    elif attack in ["PortScan", "BruteForce"]:
        return "Medium"

    else:
        return "High"