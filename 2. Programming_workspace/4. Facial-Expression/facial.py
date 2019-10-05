import numpy as np
import cv2 as cv
from firebase import firebase
import datetime
from keras.models import model_from_json
from video import create_capture
import time



def main():
    import sys, getopt

    args, video_src = getopt.getopt(sys.argv[1:], '', ['cascade=', 'nested-cascade='])
    try:
        video_src = video_src[0]
    except:
        video_src = 0
        
    cam = create_capture(video_src)
    json_file = open("model.json", "r")
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    loaded_model.load_weights("model_weights.h5")
    loaded_model.compile(loss='binary_crossentropy', optimizer='rmsprop', metrics=['accuracy'])
    fire = firebase.FirebaseApplication("https://here-f6a17.firebaseio.com/", None)
    while True:
        ret, img = cam.read()
        cv.imshow('facedetect', img)
        testing_data_list = []
        test_img_resize = cv.resize(img, (128, 128))
        testing_data_list.append(test_img_resize)
        testing_data = np.array(testing_data_list)
        testing_data = testing_data.astype('float32')
        testing_data /= 255
        
        results = loaded_model.predict_classes(testing_data)
        whattime = datetime.datetime.now()  
        state = 0
        if results[0] == 0 or results[0] == 2 or results[0] == 5:
            data = {'Time' : str(whattime), 'State' : 1}
        else:
            data = {'Time' : str(whattime), 'State' : 0}
        fire.put('/stateData', '-LpcjrVqfEXcf2KMyJLB', data)
        if cv.waitKey(5) == 27:
            break
        time.sleep(0.5)
        

if __name__ == '__main__':
    print('start')
    main()
    cv.destroyAllWindows()
