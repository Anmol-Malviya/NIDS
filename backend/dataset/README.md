    # Dataset folder

`cleaned_dataset.csv` is not committed to this repo (too large).

To regenerate it:
1. Download the CICIDS2017 dataset from https://www.unb.ca/cic/datasets/ids-2017.html
2. Run the preprocessing pipeline in `ml-training/notebooks/nids_training.ipynb`
3. Export the cleaned dataframe here as `cleaned_dataset.csv`

This file is used by the Flask backend's simulation endpoint to sample realistic traffic vectors for demo purposes.