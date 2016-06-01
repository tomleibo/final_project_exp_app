package behavioralCapture.env;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by user1 on 10/03/2016.
 */

public class FocusHandler implements View.OnFocusChangeListener {
    private static FocusHandler instance;

    public static synchronized FocusHandler getInstance(){
        if (instance == null) {
            synchronized (FocusHandler.class) {
                if (instance == null) {
                    instance = new FocusHandler();
                }
            }
        }
        return instance;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            Log.wtf("Walak","Ata");
        }
        else {
            String tag = v.getTag() == null ? "" : (String) v.getTag();
            Env.getInstance().updateTagAndPushKeyboardEvents(tag);
        }
    }

    public void registerFocusChangeEvent(EditText v) {
        v.setOnFocusChangeListener(this);
    }
}
