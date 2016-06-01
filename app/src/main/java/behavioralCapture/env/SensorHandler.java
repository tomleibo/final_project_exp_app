package behavioralCapture.env;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Map;
import java.util.TreeMap;

import behavioralCapture.events.MotionSensorEventWrapper;
import behavioralCapture.Utils.SensorTimeCalibrator;

/**
 * Created by thinkPAD on 8/18/2015.
 */
public class SensorHandler implements SensorEventListener{
    public static final int DELAY_SECOND = 1000000;
    public static final int DELAY_BETWEEN_EVENTS = SensorManager.SENSOR_DELAY_FASTEST;

    private static final int SENSORS_TYPES[] = {
            //Sensor.TYPE_GRAVITY,
            Sensor.TYPE_GYROSCOPE,
            //Sensor.TYPE_ROTATION_VECTOR,
            //Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_LINEAR_ACCELERATION,
            //Sensor.TYPE_GAME_ROTATION_VECTOR,
            //Sensor.TYPE_MAGNETIC_FIELD
            };

    public static final int STATE_SAVE_ONLY = 0;
    public static final int STATE_SAVE_AND_SEND_ALL = 1;
    public  static final int STATE_SAVE_AND_SEND_NEW = 2;
    private static final String URL = "";

    private Env env;
    private Map<Integer, SensorTimeCalibrator> calibrators;
    private boolean isRegistered=false;


    public SensorHandler(Env env) {
        this.env=env;
        calibrators = new TreeMap<>();
        for (int i : SENSORS_TYPES) {
            calibrators.put(i,new SensorTimeCalibrator(env.getContext()));
        }
    }

    public void handleEvent(SensorEvent event) throws InterruptedException {

        SensorTimeCalibrator sensorTimeCalibrator = calibrators.get(event.sensor.getType());
        if (!sensorTimeCalibrator.isCalibrated()){
            sensorTimeCalibrator.setOffsetFromSensorToUptime(event.timestamp);
            return;
        }
        else{
            MotionSensorEventWrapper wrap = new MotionSensorEventWrapper(event);
            wrap.setTimestamp(sensorTimeCalibrator.getUptimeMillis(event.timestamp));
            env.getDb().insert(MotionSensorEventWrapper.class, wrap);
        }
    }

    public String translateEventToJson(SensorEvent event) {
        //possibly all sensor data translation would occur here.
        MotionSensorEventWrapper wrap = new MotionSensorEventWrapper(event);
        String json = wrap.toJson(env.getGson());
        return json;

    }

    public String getActiveSensorsStatus(SensorManager mSensorManager) {
        StringBuilder sb= new StringBuilder("");
        for (int i=0; i<SENSORS_TYPES.length;i++) {
            sb.append("sensor type number: "+i);
            Sensor s =mSensorManager.getDefaultSensor(SENSORS_TYPES[i]);
            if (s!=null) {
                sb.append(getSensorSpec(s));
            }
            else {
                sb.append("disabled");
            }
        }
        return sb.toString();
    }

    private String getSensorSpec(Sensor s) {
        StringBuilder sb =new StringBuilder("\n");
        sb.append("vendor: " + s.getVendor() + "\n");
        sb.append("power: " + s.getPower() + "\n");
        return sb.toString();
    }

    public void registerMotionSensors(SensorManager sensorManager,boolean register, int delay) {
        Sensor sensor;
        for (int type : SENSORS_TYPES) {
            sensor = sensorManager.getDefaultSensor(type);
            if (sensor!=null) {
                if (register) {
                    sensorManager.registerListener(this, sensor, delay);
                }
                else {
                    sensorManager.unregisterListener(this, sensor);
                }
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (!Env.isFinished)
                handleEvent(event);
        }
        catch (InterruptedException e) {
            for (StackTraceElement ele : e.getStackTrace()) {
                Log.wtf("Sensor Handler", ele.toString());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
