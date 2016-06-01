package behavioralCapture.env;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.tables.KeyEventTable;
import behavioralCapture.db.tables.MotionEventTable;
import behavioralCapture.db.tables.ScaleEventTable;
import behavioralCapture.db.tables.SwipeEventTable;
import behavioralCapture.db.tables.Table;
import behavioralCapture.db.tables.TouchEventTable;
import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.events.TouchEventChunk;


//TODO avoid using env as singleton. check what is destroyed from env when only service is running in the background.
public class CommunicationService extends Service implements Runnable{
    private static final int BUFFER_SIZE = 100;
    private static final String TAG = "communication service";

    public static final String STATE_EXTRA_NAME = "STATE";
    public static final int STATE_SEND_NEW = 1;
    public static final int STATE_SEND_ALL = 2;

    public static final String URL_EXTRA_NAME = "URL";
    private static final int DEFAULT_STATE = STATE_SEND_ALL;

    public static final String USER_AT_LAST_SCREEN_EXTRA_NAME = "LAST_SCREEN";

    private String url;
    private int lastStartId = -1;
    private Thread thread;
    private int state;
    private long startTime;
    private MessageDigest md;
    private Env env=null;
    private Table[] tablesToSend = {
            new KeyEventTable(),
            new MotionEventTable(),
            new TouchEventTable(),
            new SwipeEventTable(),
            new ScaleEventTable()
    };
    private int finishedTables=0;
    private boolean userAtFinalScreen=false;
    public CommunicationService() {

    }

    @Override
    public void onCreate() {
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            //never happens.
        }
        this.thread = new Thread(this);
        this.startTime = SystemClock.uptimeMillis();
        this.env=Env.getInstance();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.url = intent.getStringExtra(URL_EXTRA_NAME);
        this.state = intent.getIntExtra(STATE_EXTRA_NAME, DEFAULT_STATE);
        this.userAtFinalScreen = intent.getBooleanExtra(USER_AT_LAST_SCREEN_EXTRA_NAME, false);
        if (lastStartId == -1) {
            this.thread.start();
        }
        this.lastStartId = startId;
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        List<? extends Jsonable> result;
        int resultCount;
        switch(state) {
            case STATE_SEND_ALL:
                startTime=0;
                break;
            case STATE_SEND_NEW:
                //nothing
        }
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(finishedTables < tablesToSend.length || !userAtFinalScreen){
            for (Table t : tablesToSend) {
                int cursor=0;
                do {
                    result = t.selectFromDate(BUFFER_SIZE, cursor, startTime);
                    resultCount =result.size();
                    if (resultCount!=0){
                        String jsonArray = createJsonArrayStringFromListOfJsonables(result);
                        executePost(url,jsonArray);
                        long start = result.get(0).getTime();
                        long end;
                        if (t instanceof SwipeEventTable) {
                            List<SingleTouchEvent> eventsChunk = ((TouchEventChunk) (result.get(result.size()-1))).getEventsChunk();
                            end = eventsChunk.get(eventsChunk.size() - 1).getDownTime();
                        }
                        else {
                            end = result.get(result.size()-1).getTime();
                        }
                        long affectedRows = t.setIsRemoved(start, end, BUFFER_SIZE);
                        if (affectedRows < resultCount) {
                            Log.wtf(TAG, "ERROR: not all sent rows were updated as sent in LDB ");
                        }
                    }
                    cursor+=BUFFER_SIZE;
                }
                while (resultCount == BUFFER_SIZE);
                if (resultCount==0) {
                    finishedTables++;
                }
                else {
                    finishedTables=0;
                }
            }
        }

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "You can now close the application. Thank You!", Toast.LENGTH_LONG).show();
            }
        });
        stopSelf(lastStartId);
    }

    private String createJsonArrayStringFromListOfJsonables(List<? extends Jsonable> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String prefix ="";
        for (Jsonable jsonable : events) {
            sb.append(prefix);
            sb.append(jsonable.toJson(env.getGson()));
            prefix=",";
        }
        sb.append("]");
        return sb.toString();
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Content-Length",Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            int erCode = connection.getResponseCode();
            Log.d(TAG, "error code: " + erCode);
            //Get Response
            InputStream is;
            is = (erCode==200)?connection.getInputStream():connection.getErrorStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
                Log.wtf(TAG, line);
            }
            rd.close();
            wr.close();

            return response.toString();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage()+"\n");
            sb.append(e.getLocalizedMessage()+"\n");
            sb.append(e.getClass().getCanonicalName()+"\n");
            for (StackTraceElement el : e.getStackTrace()) {
                sb.append(el.toString()+"\n");
            }
            Log.wtf(TAG,sb.toString());
            return sb.toString();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

}
