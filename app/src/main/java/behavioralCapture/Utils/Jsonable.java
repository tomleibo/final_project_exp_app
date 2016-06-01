package behavioralCapture.Utils;

import com.google.gson.Gson;

/**
 * Created by thinkPAD on 8/18/2015.
 */
public interface Jsonable {
    String toJson(Gson gson);
    long getTime();
}
