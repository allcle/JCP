# ★ main ref url : https://blog.naver.com/kimsung4752/221063885241
# mp3 to wav convert url : https://convertio.co/kr/
# ref url : https://towardsdatascience.com/urban-sound-classification-part-1-99137c6335f9
# ref url : https://blog.naver.com/slykid/221453948177

import scipy.io as sio
import scipy.io.wavfile
import matplotlib.pyplot as plt
from matplotlib.pyplot import specgram

import scipy
import pylab

samplerate, sound_data = scipy.io.wavfile.read("sample.wav")

# 받아오는 데이터에 대한 정보. 샘플링된 음압값을 받아온다.
print("Samplerate = ", samplerate)
# 음압값을 샘플링하는 뎁스 값을 의미한다. 시간에 따라 몇 번 셈플링하는가.
# dtype는 그에 대한 음압값을 어느 정밀도로 샘플링하는가.
print("sound_data type = ", sound_data.dtype)
# shape함수를 통해 해당 파일의 샘플링 갯수와 채널 수를 확인할 수 있다.
print("샘플 갯수와 오디오 채널수 = ", sound_data.shape)
# 오디오 전체 재생 시간. 샘플링된 갯수
print("오디오 전체 재생시간 = ", sound_data.shape[0]/samplerate)

# 재매핑
# 그래프에 표시하기 위해 음압값을 -1에서 1로 재매핑
sound_data = sound_data/(2.**15)
print(sound_data)
# 2개의 오디오 채널 중 왼쪽 데이터만 저장
# 여기서 에러 발생;;

# 그래프 x축에 할당할 시간값 배열을 만든다.
timeArray = pylab.arange(0, sound_data.shape[0], 1)

timeArray = timeArray/samplerate
print(timeArray)

# 단위를 밀리세컨드로 변환 (현재는 1초 간격)
TimeArray = timeArray * 1000
print(TimeArray)

pylab.plot(timeArray, sound_data, color = 'b')
pylab.ylabel('Amplitude')
pylab.xlabel('Time(ms)')

#pylab.show()

"""
* 해결할 것
1. 단순한 파형 모양만으로는 이미지 선별기 개발이 어렵다. 정형화된 이미지 형태가 필요
2. 적절하게 프레임을 나눠서 분류할 필요가 있다. 4초가 저정도면 1초 혹은 0.5초 단위로 분류해야할 듯?
3. 아니면, 저정도를 안고 가되, 어느정도 표본을 추출하는 방법도 좋을듯
4. 왜냐면 사이렌같은건 n초가 하나의 주기니까?
"""

# ---추가적인 코드---
import numpy as np
import numpy.fft as fft

n = len(sound_data)
p = fft.fft(sound_data)
nUniquePts = int(np.ceil((n+1)/2.0))
p = p[0:nUniquePts]
p = abs(p)
p = p/float(n)
p = p**2
if n%2 > 0 :
    p[1:len(p)] = p[1:len(p) - 1] * 2
else:
    p[1:len(p) -1] = p[1:len(p) -1]*2
freqArray = np.arange(0, nUniquePts, 1.0) * (samplerate/n)
pylab.plot(freqArray/1000, 10*np.log10(p), color = 'k')
pylab.xlabel('Frequency (kHz)')
pylab.ylabel('Power (db)')

pylab.show()