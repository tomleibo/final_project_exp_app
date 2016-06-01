package behavioralCapture.env;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;

import behavioralCapture.db.DbHandler;
import behavioralCapture.softKeyboard.KeyboardEventHandler;
import behavioralCapture.softKeyboard.SoftKeyboard;

public class Env {

    private static Env instance = null;
    public static boolean isFinished=false;

    private SensorHandler sensorHandler;
    private TouchEventHandler touchHandler;
    private FocusHandler focusHandler;
    private DbHandler db;
    private Context context;
    private Gson gson;
    private SensorManager sm;
    public String url;


    public Env() {

    }

    public static Env build(Context context) {
        Env env = getInstance();
        env.sensorHandler = new SensorHandler(env);
        env.touchHandler = new TouchEventHandler();
        env.focusHandler = FocusHandler.getInstance();
        SoftKeyboard.initialize();
        env.db = new DbHandler(context);
        env.context = context;
        env.sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        env.gson = new Gson();
        return env;
    }

    public static Env getInstance() {
        if (instance == null) {
            synchronized (Env.class) {
                if (instance == null) {
                    instance = new Env();
                    Log.wtf("Env", "pid: " + android.os.Process.myPid());
                }
            }
        }
        return instance;
    }

    public void updateTagAndPushKeyboardEvents(String tag) {
        KeyboardEventHandler.getInstance().updateTagAndPushAllEventsToToatalQueue(tag);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SensorHandler getSensorHandler() {
        return sensorHandler;
    }

    public TouchEventHandler getTouchHandler() {
        return touchHandler;
    }

    public DbHandler getDb() {
        return db;
    }

    public Context getContext() {
        return context;
    }

    public SensorManager getSensorManager() {
        return sm;
    }

    public Gson getGson() {
        return gson;
    }

    public void addAllViews(View content) {
        registerTouchEvents(content);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) content).getChildAt(0);

        registerChildViews(viewGroup);
    }

    public void registerChildViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            registerTouchEvents(v);
            if (v instanceof EditText) {
                registerFocusChangeEvent((EditText) v);
            }

            if (v instanceof ViewGroup) {
                registerChildViews((ViewGroup) v);
            }

        }
    }

    private void registerFocusChangeEvent(EditText v) {
        focusHandler.registerFocusChangeEvent(v);
    }

    public void registerTouchEvents(View v) {
        touchHandler.registerTouchEvents(v);
    }
}
