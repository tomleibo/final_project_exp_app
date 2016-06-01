package behavioralCapture.events;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import com.google.gson.Gson;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.tables.Table;

/**
 * Created by thinkPAD on 10/21/2015.
 */
public class DoubleTouchEvent implements Jsonable {

    private final int action;
    private final String action_str;
    private final float x0;
    private final String tag;
    private final String activity;
    private float x1;
    private final float y0;
    private float y1;
    private final float pressure0;
    private final float pressure1;
    private final float size0;
    private final float size1;
    private final long downTime;
    private final long timestamp;

    public DoubleTouchEvent(int action, String action_str, float x0, float x1, float y0, float y1,
                            float pressure0, float pressure1, float size0, float size1, long downTime, long timestamp, String tag, String activity) {
        this.action = action+ Table.TOUCH;
        this.action_str = action_str;
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.pressure0 = pressure0;
        this.pressure1 = pressure1;
        this.size0 = size0;
        this.size1 = size1;
        this.downTime = downTime;
        this.timestamp = timestamp;
        this.tag = tag;
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public DoubleTouchEvent(MotionEvent event, long downTime, String tag, String activity) {
        this.action = event.getAction() + Table.TOUCH;
        this.action_str = MotionEvent.actionToString(event.getAction());
        this.x0 = event.getX(0);
        this.y0 = event.getY(0);
        try {
            this.x1 = event.getX(1);
            this.y1 = event.getY(1);
        }
        catch (IllegalArgumentException e){
            this.x1 = -1;
            this.y1 = -1;
            Log.w("Double touch",e);
        }
        this.pressure0 = event.getPressure(0);
        this.pressure1 = event.getPressure(1);
        this.size0 = event.getSize(0);
        this.size1 = event.getSize(1);
        this.downTime = downTime;
        this.timestamp = event.getEventTime();
        this.tag = tag;
        this.activity = activity;


    }

    public int getAction() {
        return action;
    }

    public String getAction_str() {
        return action_str;
    }

    public float getX0() {
        return x0;
    }

    public float getX1() {
        return x1;
    }

    public float getY0() {
        return y0;
    }

    public float getY1() {
        return y1;
    }

    public float getPressure0() {
        return pressure0;
    }

    public float getPressure1() {
        return pressure1;
    }

    public float getSize0() {
        return size0;
    }

    public float getSize1() {
        return size1;
    }

    public long getDownTime() {
        return downTime;
    }
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    @Override
    public long getTime() {
        return timestamp;
    }


    public String getTag() {
        return tag;
    }

    public String getActivity() {
        return activity;
    }
}
