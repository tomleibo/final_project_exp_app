package behavioralCapture.events;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;

import com.google.gson.Gson;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.tables.Table;

/**
 * Created by user1 on 25/08/2015.
 */
public class SingleTouchEvent implements Jsonable {

    private final int action;
    private final String action_str;
    private final float x;
    private final float y;
    private final long downTime;
    private final long timestamp;
    private final float pressure;
    private final float size;
    private String tag;
    private final String activity;
    private final int type;

    public SingleTouchEvent(int action, String action_str, float x, float y, long timestamp, long downTime, float pressure, float size, String tag, String activity) {
        this.type= Table.TOUCH+action;
        this.action = action;
        this.action_str = action_str;
        this.x = x;
        this.y = y;
        this.downTime = downTime;
        this.timestamp = timestamp;
        this.pressure = pressure;
        this.size = size;
        this.tag = tag;
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public SingleTouchEvent(MotionEvent event, boolean isKeyboardOriginated, String tag, String activity) {
        this.type= Table.TOUCH+event.getAction();
        this.action = event.getAction();
        this.action_str = isKeyboardOriginated ? "Keyboard" : MotionEvent.actionToString(event.getAction());
        this.tag = tag;
        this.activity = activity;
        this.x = event.getRawX();
        this.y = event.getRawY();
        this.pressure = event.getPressure();
        this.size = event.getSize();
        this.downTime = event.getDownTime();
        this.timestamp = event.getEventTime();
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

    public long getTimestamp() {
        return timestamp;
    }


    public String getTag() {
        return tag;
    }

    public String getActivity() {
        return activity;
    }

    public int getType() {
        return type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}