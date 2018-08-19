package com.ttomovcik.pedometer;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener
{

    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "";
    private int numSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.stepCount);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        setOnClickListeners();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setOnClickListeners()
    {
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startCountingSteps();
            }
        });
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopCountingSteps();
            }
        });
        Button button = findViewById(R.id.button_action_openAlertDialog);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    public void startCountingSteps()
    {
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setVisibility(View.GONE);
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setVisibility(View.VISIBLE);
        TextView helper = findViewById(R.id.helper_text);
        helper.setVisibility(View.GONE);
        numSteps = 0;
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopCountingSteps()
    {
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setVisibility(View.VISIBLE);
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setVisibility(View.GONE);
        TextView helper = findViewById(R.id.helper_text);
        helper.setVisibility(View.VISIBLE);
        sensorManager.unregisterListener(MainActivity.this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void step(long timeNs)
    {
        numSteps++;
        textView.setText(TEXT_NUM_STEPS + numSteps);
    }
}