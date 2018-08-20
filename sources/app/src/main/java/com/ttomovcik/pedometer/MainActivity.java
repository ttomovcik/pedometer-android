package com.ttomovcik.pedometer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener,
        StepListener
{
    private static final String TEXT_NUM_STEPS = "";
    private InterstitialAd mInterstitialAd;
    private Sensor accel;
    private SensorManager sensorManager;
    private StepDetector simpleStepDetector;
    private TextView textView;
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

        prepareMobileAds();
        setOnClickListeners();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.v("onAccuracyChanged", String.valueOf(accuracy));
    }

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

    public void setOnClickListeners()
    {
        // onClickListener for imageButton_start
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startCountingSteps();
            }
        });

        // onClickListener for imageButton_stop
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopCountingSteps();
            }
        });

        // onClickListener for imageButton_help
        ImageButton imageButton_help = findViewById(R.id.imageButton_help);
        imageButton_help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.ttomovcik.pedometer"));
                startActivity(intent);
            }
        });
    }

    public void startCountingSteps()
    {
        /*
        * imageButton_start will be set to GONE;
        * imageButton_stop will be set to VISIBLE and centered in RelativeLayout @activity_main.xml
        * helper (the get started text) will be set to GONE
        * */
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setVisibility(View.GONE);
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setVisibility(View.VISIBLE);
        TextView helper = findViewById(R.id.helper_text);
        helper.setVisibility(View.GONE);
        RelativeLayout.LayoutParams relativeLayout = (RelativeLayout.LayoutParams)imageButton_stop.getLayoutParams();
        relativeLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageButton_stop.setLayoutParams(relativeLayout);

        // Reset step count to 0
        numSteps = 0;
        // Register Listener for accelerometer to count steps
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stopCountingSteps()
    {
        /*
         * imageButton_start will be set to VISIBLE;
         * imageButton_stop will be set to GONE and centered in RelativeLayout @activity_main.xml
         * helper (the get started text) will be set to VISIBLE
         * */
        ImageButton imageButton_start = findViewById(R.id.imageButton_start);
        imageButton_start.setVisibility(View.VISIBLE);
        ImageButton imageButton_stop = findViewById(R.id.imageButton_stop);
        imageButton_stop.setVisibility(View.GONE);
        TextView helper = findViewById(R.id.helper_text);
        helper.setVisibility(View.VISIBLE);

        // Unregister Listener for accelerometer
        sensorManager.unregisterListener(MainActivity.this);

        // Show ad once user cancels step counter
        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
    }

    public void prepareMobileAds()
    {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                Log.e("MobileAds","Failed to load Ads. Maybe the user is not online?");
            }

            @Override
            public void onAdOpened()
            {
                Log.i("MobileAds","Good boy");
            }

            @Override
            public void onAdClosed()
            {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}