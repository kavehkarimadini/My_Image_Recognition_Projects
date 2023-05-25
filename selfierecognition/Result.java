package com.example.selfierecognition;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Result extends AppCompatActivity {
    //ImageView imageView;
//    protected Interpreter tflite;
//    private ImageProcessor imageProcessor;
//    private TensorImage tensorImage;
//    private TensorBuffer outputBuffer;
//    private ByteBuffer inputBuffer;
//    private int imageSizeX;
//    private int imageSizeY;
//    private int[] imageShape;
//    private int[] intValues = new int[imageSizeX * imageSizeY];
//    private float[] floatValues = new float[imageSizeX * imageSizeY * 3];

    //private static final int ReqCode=1;
    ImageView img;
    TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result = findViewById(R.id.textViewResult);
        img = findViewById(R.id.imageViewResult);
        // Load the TensorFlow Lite model from the assets folder
//        try {
//            tflite = new Interpreter(loadModelFile(Result.this));
//        } catch (IOException e) {
//            Log.e(TAG, "Error loading model", e);
//        }
//        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        String imageUriString = prefs.getString("imageUri", "");
//        Uri imageUri = Uri.parse(imageUriString);
//        Bitmap bitmap = null;
//
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int[] inputShape = tflite.getInputTensor(0).shape();
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputShape[1], inputShape[2], true);
//        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1 * inputShape[1] * inputShape[2] * 3 * Float.SIZE / Byte.SIZE);
//        inputBuffer.order(ByteOrder.nativeOrder());
//        int[] pixels = new int[inputShape[1] * inputShape[2]];
//        scaledBitmap.getPixels(pixels, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
//        int pixel = 0;
//        for (int i = 0; i < inputShape[1]; i++) {
//            for (int j = 0; j < inputShape[2]; j++) {
//                int value = pixels[pixel++];
//                inputBuffer.putFloat((value >> 16) & 0xFF);
//                inputBuffer.putFloat((value >> 8) & 0xFF);
//                inputBuffer.putFloat(value & 0xFF);
//            }
//        }
//        int[] outputShape = tflite.getOutputTensor(0).shape();
//        DataType outputDataType = tflite.getOutputTensor(0).dataType();
//        Log.d(TAG, "Output tensor data type: " + outputDataType.toString());
//        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType);
//        tflite.run(inputBuffer, outputBuffer);
//        //int[] outputShape = tflite.getOutputTensor(0).shape();
//        float[] output = new float[4];
//        output = outputBuffer.getFloatArray();
        //float output = tflite.getOutputTensor(0);
        Intent getResult = getIntent();
        String label = getResult.getStringExtra("label");
        if (label.equals("Kaveh")){
            result.setTextColor(Color.GREEN);
            img.setImageResource(R.mipmap.tick);
            result.setText("You are Kaveh Karimadini");
        }else if (label.equals("NKaveh")){
            result.setText("You are not KavehKarimadini");
        }
    }

//    private MappedByteBuffer loadModelFile(Context context) throws IOException {
//        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("my_model.tflite");
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//    }

}