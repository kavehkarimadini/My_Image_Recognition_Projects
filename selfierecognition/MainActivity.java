package com.example.selfierecognition;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int INPUT_SIZE = 224;
    private static final int NUM_CHANNELS = 3;
    private static final float[] IMAGE_MEAN = {123.68f, 116.779f, 103.939f};
    private static final float[] IMAGE_STD = {1, 1, 1};
    private Interpreter tflite; // declare the TFLite interpreter as a class variable


    ImageView imageView;
    Button selectImageButton;
    private static final int ReqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageViewMain);
        Button btn = findViewById(R.id.buttonPredict);
        selectImageButton = findViewById(R.id.buttonSaveImage);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), ReqCode);

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pintent = new Intent(MainActivity.this, Result.class);
                startActivity(pintent);
                finish();
            }
        });
//        try {
//            Interpreter.Options options = new Interpreter.Options();
//            options.setNumThreads(2); // set the number of threads used for inference
//            tflite = new Interpreter(loadModelFile(), options);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ReqCode && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);

            // Preprocess the image and run inference on the model
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                Log.d(TAG, "bitmap width " + bitmap.getWidth());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
            if (bitmap == null){
                Log.d(TAG, "bitmap width after create: " + bitmap.getWidth());
            }else {
                Log.d(TAG, "bitmap width after create: " + bitmap.getWidth());
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] byteArray = baos.toByteArray();
//                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                Log.d(TAG, "Bitmap: " + encoded);

            }
            //Log.d(TAG, "message to be logged: " + bitmap);
            //ByteBuffer input = convertBitmapToByteBuffer(bitmap);
            float output = runInference(bitmap);
            String label;
            // Set the predicted label
            if (output > 0.50f) {
                label = "NKaveh";

            } else {
                label = "Kaveh";
            }
            if (bitmap != null) {
                bitmap.recycle();
                //bitmap = null;
            }
            //String label = output > 0.5f ? "Kaveh" : "Not Kaveh";
            Button btn = findViewById(R.id.buttonPredict);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent(MainActivity.this, Result.class);
                    resultIntent.putExtra("label", label);

                    startActivity(resultIntent);
                    finish();
                }
            });
        }
    }

    private float runInference(Bitmap bitmap) {
        try {
            Interpreter tflite = new Interpreter(loadModelFile(), null);
            //int[] inputShape = tflite.getInputTensor(0).shape();
            DataType newInputType = tflite.getInputTensor(0).dataType();

            // Resize the input image to the model input shape
            Log.d(TAG, "I was here " + bitmap);
            //Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, inputShape[1], inputShape[2], true);
            //Log.d(TAG, "runInference: resized image"+resizedImage);
            Log.d(TAG, "I was here 01 " + bitmap);

            // Convert the resized image to a normalized float array
            int batchNum = 0;
            float[][][][] input = new float[1][224][224][3];
            //Log.d(TAG, "Input shape: " + Arrays.toString(inputShape));
            for (int x = 0; x < 224; x++) {
                for (int y = 0; y < 224; y++) {
                    int pixel = bitmap.getPixel(x, y);
                    // Normalize channel values to between 0 and 1
                    input[batchNum][x][y][0] = Color.red(pixel) / 255.0f;
                    input[batchNum][x][y][1] = Color.green(pixel) / 255.0f;
                    input[batchNum][x][y][2] = Color.blue(pixel) / 255.0f;
                    Log.d(TAG, "Pixel " + y +"||||" + x +": " + input[batchNum][x][y][0] + ", " + input[batchNum][x][y][1] + ", " + input[batchNum][x][y][2]);
                }
            }

            // Run inference on the input data
            float[][] output = new float[1][1];
            tflite.run(input, output);
            Log.d(TAG, "runInference: output"+output[0][0]);
            // Clean up resources
            tflite.close();

            return output[0][0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1.0f;
    }

//    private float runInference(Bitmap bitmap) {
//        // Load the TFLite model from the assets folder
//        try {
//            Interpreter tflite = new Interpreter(loadModelFile(), null);
//            // Get the input and output tensor buffers from the model
//            int[] inputShape = tflite.getInputTensor(0).shape();
//            DataType newInputType = tflite.getInputTensor(0).dataType();
//            TensorBuffer inputBuffer = TensorBuffer.createFixedSize(inputShape, newInputType);
//
//            // Resize the input image to the model input shape
//            Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, inputShape[1], inputShape[2], true);
//
//            // Convert the resized image to a Tensor
//            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//            tensorImage.load(resizedImage);
//
//            // Load the pixel values into the input tensor buffer
//            int[] pixels = new int[inputShape[1] * inputShape[2]];
//            tensorImage.getBitmap().getPixels(pixels, 0, inputShape[1], 0, 0, inputShape[1], inputShape[2]);
//            for (int i = 0; i < pixels.length; i++) {
//                float pixelValue = ((pixels[i] >> 16) & 0xFF) / 255.0f;
//                inputBuffer.getFloatArray()[i] = pixelValue;
//            }
//            System.out.println(inputBuffer);
//
//            int[] outputShape = tflite.getOutputTensor(0).shape();
//            ByteBuffer outputBuffer = ByteBuffer.allocateDirect(outputShape[0] * Float.SIZE / Byte.SIZE);
//            outputBuffer.order(ByteOrder.nativeOrder());
//            // Run inference on the input data
//            tflite.run(inputBuffer.getBuffer(), outputBuffer);
//            // Get the output as a float value
//            float output = outputBuffer.getFloat(0);
//            // Clean up resources
//            tflite.close();
//            return output;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return -1.0f;
//    }

    // Define the methods used above
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("my_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        // Change input tensor shape to 224x224x3
        Interpreter interpreter = new Interpreter(modelBuffer);
        int inputSize = 224;
        int[] inputShape = {1, inputSize, inputSize, 3};
        interpreter.resizeInput(0, inputShape);
        Log.d(TAG, "Input shape of resizeInput: " + Arrays.toString(interpreter.getInputTensor(0).shape()));
        return modelBuffer;
    }
}
//    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * NUM_CHANNELS);
//        byteBuffer.order(ByteOrder.nativeOrder());
//
//        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
//        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);
//
//        int pixel = 0;
//        for (int i = 0; i < INPUT_SIZE; ++i) {
//            for (int j = 0; j < INPUT_SIZE; ++j) {
//                final int val = pixels[pixel++];
//                byteBuffer.put((byte)((val >> 16) & 0xFF));
//                byteBuffer.put((byte)((val >> 8) & 0xFF));
//                byteBuffer.put((byte)(val & 0xFF));
//            }
//        }
//
//        return byteBuffer;
//    }
//    private Bitmap normalizeBitmap(Bitmap bitmap) {
//        // Normalize pixel values to [-1,1]
//        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//        float[] pixels = new float[3];
//        float[] meanValues = {123.68f, 116.78f, 103.94f}; // mean RGB values of training dataset
//        float std = 1.0f;
//        for (int row = 0; row < bitmap.getWidth(); row++) {
//            for (int col = 0; col < bitmap.getHeight(); col++) {
//                int pixel = bitmap.getPixel(row, col);
//                pixels[0] = ((pixel >> 16) & 0xff) - meanValues[0];
//                pixels[1] = ((pixel >> 8) & 0xff) - meanValues[1];
//                pixels[2] = (pixel & 0xff) - meanValues[2];
//                for (int i = 0; i < 3; i++) {
//                    pixels[i] /= std;
//                }
//                result.setPixel(row, col, Color.rgb((int) pixels[0], (int) pixels[1], (int) pixels[2]));
//            }
//        }
//        return result;
//    }
//
//}