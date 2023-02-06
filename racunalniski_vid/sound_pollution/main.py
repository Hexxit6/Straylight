import csv
import numpy as np
from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
import librosa
import scipy.stats as stats

def extract_features(filename):
    sound, sr = librosa.load(filename)
    mean = np.mean(sound)
    std = np.std(sound)
    skew = stats.skew(sound)
    kurtosis = stats.kurtosis(sound)
    zero_crossing_rate = librosa.zero_crossings(sound).mean()
    mfccs = librosa.feature.mfcc(y=sound, sr=sr, n_mfcc=13).mean()
    rms = librosa.feature.rms(y=sound).mean()
    return [mean, std, skew, kurtosis, zero_crossing_rate, mfccs, rms]

with open('sound_data.csv') as f:
    reader = csv.reader(f)
    headers = next(reader)
    data = list(reader)

X = [row[:-1] for row in data]
y = [row[-1] for row in data]

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

X_train = np.array(X_train).astype(float)
X_test = np.array(X_test).astype(float)

model = LogisticRegression()
model.fit(X_train, y_train)

accuracy = model.score(X_test, y_test)
print("Accuracy:", accuracy)

train_predict = model.predict(X_train)
test_predict = model.predict(X_test)

# np.save('X_train.npy', X_train)
# np.save('y_train.npy', y_train)

print("Training data classification report:")
print(classification_report(y_train, train_predict))
print("Test data classification report:")
print(classification_report(y_test, test_predict))

# --------------------------------------------------
my_sound = [extract_features('steps.mp3')]

prediction = model.predict(my_sound)
print("Prediction for my sound:", prediction)


