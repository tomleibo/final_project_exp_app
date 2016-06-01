package behavioralCapture.events;

import android.hardware.SensorEvent;

import com.google.gson.Gson;

import java.util.Arrays;

import behavioralCapture.Utils.Jsonable;

/**
 * Created by thinkPAD on 8/18/2015.
 */
public class MotionSensorEventWrapper implements Jsonable {
    private int type;
    private float[] values;
    private long timestamp;
    private int accuracy;

    public int getType() {
        return type;
    }

    public float[] getValues() {
        return values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public MotionSensorEventWrapper(int type, float[] values, long timestamp, int accuracy) {
        this.type = type;
        this.values = values;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }

    public MotionSensorEventWrapper(SensorEvent event) {
        this.type = event.sensor.getType();
        this.values = event.values;
        this.timestamp = event.timestamp;
        this.accuracy = event.accuracy;
    }

    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public long getTime() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MotionSensorEventWrapper{" +
                ", timestamp=" + timestamp +
                "type=" + type +
                ", values=" + Arrays.toString(values) +
                ", accuracy=" + accuracy +
                '}';
    }
}
