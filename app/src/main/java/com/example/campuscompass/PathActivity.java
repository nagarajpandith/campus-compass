package com.example.campuscompass;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;;
public class PathActivity extends AppCompatActivity implements SensorEventListener {
    VrPanoramaView v;
    private SensorManager sensorManager;
    private Sensor gyroSensor;
    private long previousTimestamp;


    ImageButton []arrows=new ImageButton[4];
    float []arrowsAngles={0,180,-90,90};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int pivotX = displayMetrics.widthPixels / 8;
        int pivotY = displayMetrics.heightPixels/4;


        v = findViewById(R.id.image);
        arrows[0]=findViewById(R.id.front);
        arrows[1]=findViewById(R.id.back);
        arrows[2]=findViewById(R.id.left);
        arrows[3]=findViewById(R.id.right);

        for(int i=0;i<4;i++)
        {
            Log.d("ajsd", "onCreate: "+pivotX+" "+pivotY);
            arrows[i].setPivotX(pivotX);
            arrows[i].setPivotY(pivotY);
            arrows[i].setRotation(arrowsAngles[i]);
            arrows[i].setRotationX(20);
        }
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        try {
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            v.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lh312), options);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,gyroSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType()==Sensor.TYPE_GYROSCOPE)
            {
//                float x= sensorEvent.values[0];
//                float y= sensorEvent.values[1];
//                float z= sensorEvent.values[2];
//                float deltaAngle=y*(sensorEvent.timestamp-previousTimestamp)/1000000000.0f;
                float []yawAndPitch=new float[2];
                v.getHeadRotation(yawAndPitch);
                for(int i=0;i<4;i++)
                {
                    arrows[i].setRotation(arrowsAngles[i]-yawAndPitch[0]);
                }
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
