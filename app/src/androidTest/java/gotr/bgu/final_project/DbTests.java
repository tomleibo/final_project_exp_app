package gotr.bgu.final_project;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import behavioralCapture.Utils.Jsonable;
import behavioralCapture.db.DbHandler;
import behavioralCapture.db.tables.DoubleTouchTable;
import behavioralCapture.db.tables.KeyEventTable;
import behavioralCapture.db.tables.MotionEventTable;
import behavioralCapture.db.tables.ScaleEventTable;
import behavioralCapture.db.tables.TouchEventTable;
import behavioralCapture.env.Env;
import behavioralCapture.events.DoubleTouchEvent;
import behavioralCapture.events.MotionSensorEventWrapper;
import behavioralCapture.events.ScaleTouchEvent;
import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.events.TouchEventChunk;
import behavioralCapture.softKeyboard.KeyboardEvent;

import static junit.framework.Assert.assertEquals;

public class DbTests {
    @Test
    public void createTableTouchEvent() {
        String ddl = DbHandler.getCreateTableQuery(new TouchEventTable());
        String expected = "CREATE TABLE TOUCH_EVENT(\n" +
                "ID INTEGER PRIMARY KEY,\n" +
                "ACTION INTEGER ,\n" +
                "ACTION_STR TEXT ,\n" +
                "X REAL ,\n" +
                "Y REAL ,\n" +
                "DOWN_TIME INTEGER ,\n" +
                "TIMESTAMP INTEGER ,\n" +
                "PRESSURE REAL ,\n" +
                "SIZE REAL ,\n" +
                "CHUNK_ID INTEGER ,\n" +
                "IS_REMOVED INTEGER );";
        assertEquals(expected,ddl);
    }

    @Test
    public void createTableDoubleEvent() {

        String ddl = DbHandler.getCreateTableQuery(new DoubleTouchTable());
        String expected = "CREATE TABLE DOUBLE_TOUCH_EVENT(\n" +
                "ID INTEGER PRIMARY KEY,\n" +
                "ACTION INTEGER ,\n" +
                "ACTION_STR TEXT ,\n" +
                "X0 REAL ,\n" +
                "X1 REAL ,\n" +
                "Y0 REAL ,\n" +
                "Y1 REAL ,\n" +
                "DOWN_TIME INTEGER ,\n" +
                "TIMESTAMP INTEGER ,\n" +
                "PRESSURE0 REAL ,\n" +
                "PRESSURE1 REAL ,\n" +
                "SIZE0 REAL ,\n" +
                "SIZE1 REAL  default false ,\n" +
                "SCALE_EVENT_ID INTEGER ,\n" +
                "IS_REMOVED INTEGER );";
        assertEquals(expected,ddl);
    }

    @Test
    public void testCreateKeyEventTable() {
        String ddl = DbHandler.getCreateTableQuery(new KeyEventTable());
        String exp = "CREATE TABLE KEY_EVENT(\n" +
                "ID INTEGER PRIMARY KEY,\n" +
                "ACTION INTEGER ,\n" +
                "DOWN_TIME INTEGER ,\n" +
                "KEY_CODE INTEGER ,\n" +
                "KEY_STR TEXT ,\n" +
                "IS_REMOVED INTEGER );";
    }

    @Test
    public void testCreateMotionTable(){
        String ddl = DbHandler.getCreateTableQuery(new MotionEventTable());
        String exp = "CREATE TABLE MOTION_EVENT(\n" +
                "ID INTEGER PRIMARY KEY,\n" +
                "TYPE INTEGER ,\n" +
                "TIME INTEGER ,\n" +
                "X REAL ,\n" +
                "Y REAL ,\n" +
                "Z REAL ,\n" +
                "ACCURACY INTEGER ,\n" +
                "IS_REMOVED INTEGER );";
        assertEquals(exp,ddl);
    }

    @Test
    public void testCreateScaleEventTable() {
        String ddl = DbHandler.getCreateTableQuery(new ScaleEventTable());
        String exp = "CREATE TABLE SCALE_EVENT(\n" +
                "ID INTEGER PRIMARY KEY,\n" +
                "DOWN_TIME INTEGER ,\n" +
                "IS_REMOVED INTEGER );";
        assertEquals(exp, ddl);
    }

    @Test
    public void insertAndCheckExistenceMotionEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DbHandler db = Env.getInstance().getDb();
        Method m = DbHandler.class.getDeclaredMethod("insertMotionSensorEvent", MotionSensorEventWrapper.class);
        m.setAccessible(true);
        MotionSensorEventWrapper motionSensorEventWrapper = new MotionSensorEventWrapper(1,new float[]{1.0f,1.0f,1.0f},32235252,123);
        m.invoke(db, motionSensorEventWrapper);
        List<MotionSensorEventWrapper> result = db.getAllMotionEvents(null, null, null);
        assert(result.contains(motionSensorEventWrapper));
    }

    @Test
    public void insertAndCheckExistenceSwipeEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DbHandler db = Env.getInstance().getDb();
        Method m = DbHandler.class.getDeclaredMethod("insertSwipeEvent", TouchEventChunk.class);
        m.setAccessible(true);
        List<SingleTouchEvent> singleTouches = new ArrayList<SingleTouchEvent>(Arrays.asList(new SingleTouchEvent[]{
                new SingleTouchEvent(1, "action", 0.1f, 0.2f, 987, 1021, 0.154f, 0.2135f, "tag", "activity"),
                new SingleTouchEvent(2, "action2", 0.3f, 0.4f, 636, 525, 0.666f, 0.6543f, "tag", "activity")
        }));
        TouchEventChunk tec = new TouchEventChunk(123,singleTouches);
        m.invoke(db, tec);
        List<Jsonable> result = db.selectSwipeEvents(100, 0, null);
        assert(result.contains(tec));
    }

    @Test
    public void insertAndCheckExistenceKeyboardEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DbHandler db = Env.getInstance().getDb();
        Method m = DbHandler.class.getDeclaredMethod("insertKeyboardEvent", KeyboardEvent.class);
        m.setAccessible(true);
        KeyboardEvent ke =new KeyboardEvent(1,2,3,"str1");
        m.invoke(db, ke);
        List<Jsonable> result = db.selectKeyboardEvents(null, null, null);
        assert(result.contains(ke));
    }

    @Test
    public void insertAndCheckExistenceTouchEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DbHandler db = Env.getInstance().getDb();
        Method m = DbHandler.class.getDeclaredMethod("insertTouchEvent", SingleTouchEvent.class);
        m.setAccessible(true);
        SingleTouchEvent ste = new SingleTouchEvent(777,"down",456f,123f,999999,9995555,0.5f,22f, "tag", "activity");
        m.invoke(db, ste);
        List<SingleTouchEvent> result = db.selectTouchEvents(null, null, null);
        assert(result.contains(ste));
    }


    @Test
    public void insertAndCheckExistenceScaleEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DbHandler db = Env.getInstance().getDb();
        Method m = DbHandler.class.getDeclaredMethod("insertScaleEvent", ScaleTouchEvent.class);
        m.setAccessible(true);
        List<DoubleTouchEvent> doubleTouches= new ArrayList<DoubleTouchEvent>(Arrays.asList(new DoubleTouchEvent[]{
                new DoubleTouchEvent(1,"action",0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,99999,11111, "tag", "activity"),
                new DoubleTouchEvent(2,"action2",0.111f,0.222f,0.333f,0.444f,0.555f,0.666f,0.777f,0.888f,22222,88888, "tag", "activity")
        }));
        ScaleTouchEvent ste = new ScaleTouchEvent(456,doubleTouches);
        m.invoke(db, ste);
        List<Jsonable> result = db.selectSwipeEvents(100, 0, null);
        assert(result.contains(ste));
    }



}
