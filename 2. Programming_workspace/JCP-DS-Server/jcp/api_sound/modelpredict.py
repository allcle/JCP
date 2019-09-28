class ModelPredict:
    def ensemble(models, x_train):

        # 6개의 모델을 앙상블 형태로 돌린다.
        classPredict = [0, 0, 0, 0, 0, 0]
        answer = 5
        maximum = 0
        maxIndex = 0

        print("##### Model predicting...")

        for model in models:
            tempPredict = model.predict_classes(x_train[0:1, :], verbose = 0)
            classPredict[int(tempPredict)] += 1

        for j in range(6):
            if(classPredict[j] > maximum):
                overlap = 0
                maximum = classPredict[j]
                maxIndex = j

        print("##### Model predict success the Result is...")
        print("##### ", end = "")
        print(classPredict, end = " : ")
        print(maxIndex)

        return maxIndex

        