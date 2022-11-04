package com.example.ugd89_a_10650_project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.hardware.Camera
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.FlowPreview

class MainActivity : AppCompatActivity() {
    lateinit var sensorStatusTv: TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private var mCamera:Camera? = null
    private var mCameraView: CameraView? = null
    lateinit var camera: Sensor
    lateinit var currentCameraId: TextView
    lateinit var inPreview: Sensor
    lateinit var stopPreview: Sensor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorStatusTv = findViewById(R.id.idTVSensorStatus)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        try{
            mCamera = Camera.open()
        }catch (e: Exception){
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if(mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{ view: View? -> System.exit(0)}
        if(proximitySensor == null){
            Toast.makeText(this,"No Proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener{
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if(event.sensor.type == Sensor.TYPE_PROXIMITY){
                if (inPreview) {
                    camera.stopPreview();
                }
//NB: if you don't release the current camera before switching, you app will crash
                camera.release();

//swap the id of the camera to be used
                if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                camera = Camera.open(currentCameraId);

                setCameraDisplayOrientation(CameraActivity.this, currentCameraId, camera);
                try {

                    camera.setPreviewDisplay(previewHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
        }
    }
}