from PIL import Image
import numpy as np
from keras.preprocessing.image import img_to_array


class PreProcessor:
    def processImageToXTrain(pil_image, image_width, image_height, channels):

        # img to array
        pil_image = pil_image.resize((image_width, image_height))

        x = img_to_array(pil_image)
        x = x.reshape((image_width, image_height, channels))
        x = (x-128.0)/128.0
        
        x_test = np.ndarray(shape = (1, image_width, image_height, channels), dtype = np.float32)
        x_test[0] = x

        print("##### PIL Image converted to np.ndarray !")

        return x_test