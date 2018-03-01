package com.example.admin.sensordata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //get base working directory
    private static final File DATA_FOLDER = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/sensorData");

    //init textviews
    private TextView textViewGyro;
    private TextView textViewAcc;
    private TextView textViewLinAcc;

    //init sensor manager
    private SensorManager mSensorManager;

    String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewGyro = findViewById(R.id.textViewGyro);
        textViewAcc = findViewById(R.id.textViewAcc);
        textViewLinAcc = findViewById(R.id.textViewLinAcc);
        Button buttonStart = findViewById(R.id.buttonStart);
        Button buttonStop = findViewById(R.id.buttonStop);
        Button button10s = findViewById(R.id.button10s);

        //get a hook to the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(MainActivity.this);
                date = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DATE) + "_" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "-" + Calendar.getInstance().get(Calendar.MINUTE) + "-" + Calendar.getInstance().get(Calendar.SECOND);
                //register listeners
                mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
                mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
            }
        });

        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //unregister listeners
                mSensorManager.unregisterListener(MainActivity.this);
            }
        });

        button10s.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Starting in 5 seconds!" , Toast.LENGTH_SHORT ).show();
                mSensorManager.unregisterListener(MainActivity.this);

                //register listeners in 5 seconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        date = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DATE) + "_" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "-" + Calendar.getInstance().get(Calendar.MINUTE) + "-" + Calendar.getInstance().get(Calendar.SECOND);
                        mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                        mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
                        mSensorManager.registerListener(MainActivity.this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
                    }
                }, 5000);

                //unregister listeners after 15 seconds
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mSensorManager.unregisterListener(MainActivity.this);
                    }
                }, 15000);
            }
        });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        //mSensorManager.unregisterListener(this);
        super.onStop();
    }*/

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    private void writeToCsvGyro(String x, String y, String z) throws IOException {
        boolean success = true;
        File currentFolder = new File(DATA_FOLDER + "/" + date);
        String csvGyro = currentFolder + "/gyro.csv";
        
        if (!currentFolder.exists()) {
            success = currentFolder.mkdirs();
        }

        if (success) {
            FileWriter fileWriter = new FileWriter(csvGyro, true);

            String tmp = System.currentTimeMillis() + "," + x + "," + y + "," + z + "\n";

            fileWriter.append(tmp);
            fileWriter.close();
        }
    }

    private void writeToCsvAcc(String x, String y, String z) throws IOException {
        boolean success = true;
        File currentFolder = new File(DATA_FOLDER + "/" + date);
        String csvAcc = currentFolder + "/acc.csv";

        if (!currentFolder.exists()) {
            success = currentFolder.mkdirs();
        }

        if (success) {
            FileWriter fileWriter = new FileWriter(csvAcc, true);
            String tmp = System.currentTimeMillis() + "," + x + "," + y + "," + z + "\n";
            fileWriter.append(tmp);
            fileWriter.close();
        }
    }

    private void writeToCsvLinAcc(String x, String y, String z) throws IOException {
        boolean success = true;
        File currentFolder = new File(DATA_FOLDER + "/" + date);
        String csvLinAcc = currentFolder + "/linAcc.csv";

        if (!currentFolder.exists()) {
            success = currentFolder.mkdirs();
        }

        if (success) {
            FileWriter fileWriter = new FileWriter(csvLinAcc, true);
            String tmp = System.currentTimeMillis() + "," + x + "," + y + "," + z + "\n";
            fileWriter.append(tmp);
            fileWriter.close();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            textViewGyro.setText("\nDir: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/sensorData\n\nGyroscope\nOrientation X (Roll): " + Float.toString(event.values[0]) +
                    "\nOrientation Y (Pitch): " + Float.toString(event.values[1]) +
                    "\nOrientation Z (Yaw): " + Float.toString(event.values[2]));

            try {
                writeToCsvGyro(Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            textViewAcc.setText("\nAccelerometer\nAcceleration X: " + Float.toString(event.values[0]) +
                    "\nAcceleration Y: " + Float.toString(event.values[1]) +
                    "\nAcceleration Z: " + Float.toString(event.values[2]));

            try {
                writeToCsvAcc(Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        //this sensor exist only on selected devices and will be omitted if it wasn't found
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            textViewLinAcc.setText("\nLinear acceleration\nLinear Acc. X: " + Float.toString(event.values[0]) +
                    "\nLinear Acc. Y: " + Float.toString(event.values[1]) +
                    "\nLinear Acc. Z: " + Float.toString(event.values[2]));

            try {
                writeToCsvLinAcc(Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}