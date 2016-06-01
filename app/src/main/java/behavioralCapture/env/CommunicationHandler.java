package behavioralCapture.env;

import android.hardware.SensorEvent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class CommunicationHandler implements Runnable{
    private static final int CAPACITY = 1000;
    private static final String TAG = "CommunicationHandler";

    private Env env;
    private BlockingQueue<SensorEvent> eventQueue;
    private Thread runningThread;
    private boolean shouldStop = false;
    private String url;


    public CommunicationHandler(String url, Env env) {
        this.eventQueue= new LinkedBlockingDeque<>(CAPACITY);
        this.url=url;
        this.env=env;
        runningThread = new Thread(this);
        runningThread.start();
    }

    public void sendEvent(SensorEvent event) throws InterruptedException {
        eventQueue.put(event);
    }

    public void run() {
        while(true) {
            String json = null;
            try {
                SensorEvent event = eventQueue.take();
                json = env.getSensorHandler().translateEventToJson(event);
            } catch (InterruptedException e) {
                json="interrupted exception occured.";
                e.printStackTrace();
            }
            Log.d(TAG, "sending json: " + json.substring(0, Math.min(10, json.length())));
            String response = executePost(url,"json="+json);
            Log.wtf(TAG,"RESPONSE: "+response);
        }
    }

    public void stop() {
        shouldStop = true;
        runningThread.interrupt();
    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();

            int erCode = connection.getResponseCode();
            Log.wtf(TAG,"error code: "+erCode);
            //Get Response
            InputStream is=null;
            if (erCode == 200) {
                is = connection.getInputStream();
            }
            else {
                is = connection.getErrorStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
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
            return sb.toString();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
