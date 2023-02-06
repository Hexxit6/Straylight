import numpy as np
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

def predict_sound(filename):
    sound_features = [extract_features(filename)]
    prediction = model.predict(sound_features)

    if prediction == ['good']:
        return "Good"
    else:
        return "Polluted"


X_train = np.load('X_train.npy')
y_train = np.load('y_train.npy')

model = LogisticRegression()
model.fit(X_train, y_train)

print("The sound is: " + predict_sound('steps.mp3'))



