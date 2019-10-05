"""
<one line to give the program's name and an idea of what it does.>
Copyright (C) <2019> <JCP>

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
"""

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
