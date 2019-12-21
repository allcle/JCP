import time


class ModelPredict:

    def ensemble(self, models, x_train):

        # 6개의 모델을 앙상블 형태로 돌린다.
        classPredict = [0, 0, 0, 0, 0, 0]
        answer = 5
        maximum = 0
        maxIndex = 0

        print("##### Model predicting... at ")
        print(time.time())

        idx = 0
        for model in models:
                tempPredict = model.predict_classes(x_train[0:1, :], verbose=0)
                classPredict[idx] = int(tempPredict)
                idx += 1

        resultPredict = [0, 0, 0, 0, 0, 0]
        for j in range(6):
            resultPredict[classPredict[j]] += 1
            if(resultPredict[classPredict[j]] > maximum):
                overlap = 0
                maximum = resultPredict[classPredict[j]]
                maxIndex = classPredict[j]

        print("##### Model predict success the Result is... at ")
        print(time.time())
        print("##### ", end="")
        print(resultPredict, end=" : ")
        print(maxIndex)

        return maxIndex
