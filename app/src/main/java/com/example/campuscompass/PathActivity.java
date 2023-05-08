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
import android.view.View;
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
    ImageButton up;
    ImageButton down;
    ImageButton stairs;
    float []arrowsAngles={0,180,-90,90};
    Location current;
    Location previousCurrent=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int pivotX = displayMetrics.widthPixels / 8;
        int pivotY = displayMetrics.heightPixels/4;
        current=CurrentPointer.current;


        v = findViewById(R.id.image);
        arrows[0]=findViewById(R.id.front);
        arrows[1]=findViewById(R.id.back);
        arrows[2]=findViewById(R.id.left);
        arrows[3]=findViewById(R.id.right);
        stairs=findViewById(R.id.stair);
        up=findViewById(R.id.upStairs);
        down=findViewById(R.id.downStairs);


        for(int i=0;i<4;i++)
        {
            arrows[i].setPivotX(pivotX);
            arrows[i].setPivotY(pivotY);
            arrows[i].setRotation(arrowsAngles[i]);
            arrows[i].setRotationX(20);
            arrows[i].setVisibility(View.INVISIBLE);
        }
        stairs.setPivotX(pivotX);
        stairs.setPivotY(pivotY);
        stairs.setRotationX(20);
        stairs.setVisibility(View.INVISIBLE);

        up.setPivotX(pivotX);
        up.setPivotY(pivotY);
        up.setRotationX(20);
        up.setVisibility(View.INVISIBLE);

        down.setPivotX(pivotX);
        down.setPivotY(pivotY);
        down.setRotationX(20);
        down.setVisibility(View.INVISIBLE);

        if(current.getFront()!=null && current.getFront().getInRoute()){
            arrows[0].setVisibility(View.VISIBLE);
        }
        if(current.getBack()!=null && current.getBack().getInRoute()){
            arrows[1].setVisibility(View.VISIBLE);
        }
        if(current.getLeft()!=null && current.getLeft().getInRoute()){
            arrows[2].setVisibility(View.VISIBLE);
        }
        if(current.getRight()!=null && current.getRight().getInRoute()){
            arrows[3].setVisibility(View.VISIBLE);
        }
        if(current.getStairs()!=null && current.getStairs().getInRoute())
        {
            stairs.setVisibility(View.VISIBLE);
            stairs.setRotation(current.getStairsAngle());
        }
        if(current.getUp()!=null && current.getUp().getInRoute())
        {
            up.setVisibility(View.VISIBLE);
            up.setRotation(current.getUpAngle());
        }
        if(current.getDown()!=null && current.getDown().getInRoute())
        {
            down.setVisibility(View.VISIBLE);
            down.setRotation(current.getDownAngle());
        }

        arrows[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getFront();
                reCalibrate();
            }
        });
        arrows[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getBack();
                reCalibrate();
            }
        });
        arrows[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getLeft();
                reCalibrate();
            }
        });
        arrows[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getRight();
                reCalibrate();
            }
        });
        stairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getStairs();
                Log.d("skf", "onClick: stairs");
                reCalibrate();
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getUp();
                Log.d("skf", "onClick: up");
                reCalibrate();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current=current.getDown();
                Log.d("skf", "onClick: down");
                reCalibrate();
            }
        });


        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        VrPanoramaView.Options options = new VrPanoramaView.Options();
        try {
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            v.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), current.getImage()), options);
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
                float []yawAndPitch=new float[2];
                v.getHeadRotation(yawAndPitch);
                for(int i=0;i<4;i++)
                {
                    arrows[i].setRotation(arrowsAngles[i]-yawAndPitch[0]+current.getAngle());
                }
                stairs.setRotation(current.getStairsAngle()-yawAndPitch[0]+current.getAngle());
                up.setRotation(current.getUpAngle()-yawAndPitch[0]+current.getAngle());
                down.setRotation(current.getDownAngle()-yawAndPitch[0]+current.getAngle());
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void reCalibrate(){
        for(int i=0;i<4;i++)
        {
            arrows[i].setVisibility(View.INVISIBLE);
        }
        down.setVisibility(View.INVISIBLE);
        up.setVisibility(View.INVISIBLE);
        stairs.setVisibility(View.INVISIBLE);

        if(current.getFront()!=null && current.getFront().getInRoute()){
            arrows[0].setVisibility(View.VISIBLE);
        }
        if(current.getBack()!=null && current.getBack().getInRoute()){
            arrows[1].setVisibility(View.VISIBLE);
        }
        if(current.getLeft()!=null && current.getLeft().getInRoute()){
            arrows[2].setVisibility(View.VISIBLE);
        }
        if(current.getRight()!=null && current.getRight().getInRoute()){
            arrows[3].setVisibility(View.VISIBLE);
        }

        if(current.getStairs()!=null && current.getStairs().getInRoute())
        {
            stairs.setVisibility(View.VISIBLE);
            stairs.setRotation(current.getStairsAngle()+current.getAngle());
        }
        if(current.getUp()!=null && current.getUp().getInRoute())
        {
            up.setVisibility(View.VISIBLE);
            up.setRotation(current.getUpAngle()+current.getAngle());
        }
        if(current.getDown()!=null && current.getDown().getInRoute())
        {
            down.setVisibility(View.VISIBLE);
            down.setRotation(current.getDownAngle()+current.getAngle());
        }

        VrPanoramaView.Options options = new VrPanoramaView.Options();
        try {
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            v.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), current.getImage()), options);
        } catch (Exception e) {

        }
    }
}
