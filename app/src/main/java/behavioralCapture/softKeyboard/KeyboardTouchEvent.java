package behavioralCapture.softKeyboard;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;

import com.google.gson.Gson;

import behavioralCapture.Utils.Jsonable;

/**
 * Created by user1 on 25/08/2015.
 */
// TODO: Create a table for this class
public class KeyboardTouchEvent implements Jsonable {

    private final int action;
    private final String action_str;
    private final float x;
    private final float y;
    private final long downTime;
    private final long eventTime;
    private final float pressure;
    private final float size;

    public KeyboardTouchEvent(int action, String action_str, float x, float y, long eventTime, long downTime, float pressure, float size) {
        this.action = action;
        this.action_str = action_str;
        this.x = x;
        this.y = y;
        this.downTime = downTime;
        this.eventTime = eventTime;
        this.pressure = pressure;
        this.size = size;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public KeyboardTouchEvent(MotionEvent event){
        this.action = event.getAction();
        this.action_str = MotionEvent.actionToString(event.getAction());
        this.x = event.getRawX();
        this.y = event.getRawY();
        this.pressure = event.getPressure();
        this.size = event.getSize();
        this.downTime = event.getDownTime();
        this.eventTime = event.getEventTime();
    }

    @Override
    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public long getTime() {
        return downTime;
    }

    public int getAction() {
        return action;
    }

    public String getAction_str() {
        return action_str;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getDownTime() {
        return downTime;
    }

    public float getPressure() {
        return pressure;
    }

    public float getSize() {
        return size;
    }

    public long getEventTime() {
        return eventTime;
    }


}