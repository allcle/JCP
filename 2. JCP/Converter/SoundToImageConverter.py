import os
import getpass
import scipy.io as sio
import scipy.io.wavfile
import matplotlib.pyplot as plt
from matplotlib.pyplot import specgram
import numpy as np
import numpy.fft as fft
import scipy
import pylab
import glob
import time

# case1. 스팩트럼 모양
def Specgram(fileName, number):
    samplerate, sound_data = sio.wavfile.read(fileName + str(number) + ".wav")
    plt.specgram(sound_data, Fs=samplerate)
    plt.axis([0.25, 3, 0, 4000])
    #plt.show()
    plt.savefig(fileName + str(number) + '_Specgram.jpg')

# case2. 일반적인 주파수 모양. 이걸 발전시키는게 가장 좋을 듯
def AmplitudeTime(fileName, number):
    samplerate, sound_data = sio.wavfile.read(fileName + str(number) + ".wav") # 받아오는 데이터에 대한 정보. 샘플링된 음압값을 받아온다.
    print("Samplerate = ", samplerate) # 음압값을 샘플링하는 뎁스 값을 의미한다. 시간에 따라 몇 번 셈플링하는가.
    print("sound_data type = ", sound_data.dtype) # dtype는 그에 대한 음압값을 어느 정밀도로 샘플링하는가.
    print("샘플 갯수와 오디오 채널수 = ", sound_data.shape) # shape함수를 통해 해당 파일의 샘플링 갯수와 채널 수를 확인
    print("오디오 전체 재생시간 = ", sound_data.shape[0]/samplerate) # 오디오 전체 재생 시간. 샘플링된 갯수

    sound_data = sound_data/(2.**15) # 그래프에 표시하기 위해 음압값을 -1에서 1로 재매핑
    splitLength = (int(sound_data.shape[0]/samplerate))*5000

    for countTime in range(int(int(sound_data.shape[0] / samplerate) / 5) + 1):
        tempSoundData = sound_data[countTime * splitLength: (countTime + 1) * splitLength]
        timeArray = pylab.arange(0, tempSoundData.shape[0], 1)  # 그래프 x축에 할당할 시간값 배열을 만든다.
        timeArray = timeArray / samplerate
        timeArray = timeArray * 1000  # 단위를 밀리세컨드로 변환 (현재는 1초 간격)
        pylab.plot(timeArray, tempSoundData, color='#000000')
        pylab.ylabel('Amplitude')
        pylab.xlabel('Time(ms)')
        #pylab.show()
        pylab.savefig(fileName + str(number) + '[' + str(countTime) + ']_AT.png')

# case3. fft 푸리에 변환 그래프
def FrequencePower(fileName, number):
    samplerate, sound_data = sio.wavfile.read(fileName + str(number) + ".wav")
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
    pylab.axis([0, 4, -60, 20])
    pylab.xlabel('Frequency (kHz)')
    pylab.ylabel('Power (db)')
    #pylab.show()
    pylab.savefig(fileName + str(number) + '_FP.png')

#---main---
def mainCode():
    fileList = glob.glob("*.wav")
    index = input("input what you want")
    print("----------------------------------")
    if(index == '0'):
        Specgram('경찰차', 0)
    elif(index == '1'):
        AmplitudeTime('경찰차', 0)
    elif(index == '2'):
        FrequencePower('경찰차', 0)
    else:
        print("finish")

if __name__ == '__main__':
    print("----------------------------------")
    print("0 : Specgram Converter")
    print("1 : Amplitude & Time Converter")
    print("2 : Frequence & Power Converter")
    print("----------------------------------")
    mainCode()

#pylab.savefig('경찰차_AT2.png')

#----------reference url-------------------------------------------------------------
# ★ main ref url : https://blog.naver.com/kimsung4752/221063885241
# mp3 to wav convert url : https://convertio.co/kr/
# 'yes' 음성 데이터를 전처리 하는 블로그 : https://wdprogrammer.tistory.com/43
# 음성 요소에 대한 설명 블로그 : https://blog.naver.com/kimsung4752/221321201622
# 자료 많아보이는 깃허브! https://github.com/kuj0210/Smart-mobile/issues/43

# AT converter : https://towardsdatascience.com/urban-sound-classification-part-1-99137c6335f9
# Specgram ref url : https://blog.naver.com/slykid/221453948177
# Specgram : https://robot7887.blog.me/221289864698
# Specgram : https://blog.naver.com/bdh0727/221266874029
# FFT 변환에 대한 설명 블로그 : #https://blog.naver.com/swkim4610/221306083201
