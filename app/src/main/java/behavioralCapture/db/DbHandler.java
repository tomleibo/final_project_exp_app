package behavioralCapture.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import behavioralCapture.env.Env;
import behavioralCapture.events.DoubleTouchEvent;
import behavioralCapture.Utils.Jsonable;
import behavioralCapture.softKeyboard.KeyboardEvent;
import behavioralCapture.events.MotionSensorEventWrapper;
import behavioralCapture.events.ScaleTouchEvent;
import behavioralCapture.events.SingleTouchEvent;
import behavioralCapture.events.TouchEventChunk;
import behavioralCapture.db.tables.DoubleTouchTable;
import behavioralCapture.db.tables.KeyEventTable;
import behavioralCapture.db.tables.MotionEventTable;
import behavioralCapture.db.tables.ScaleEventTable;
import behavioralCapture.db.tables.SqliteColumnTypes;
import behavioralCapture.db.tables.SwipeEventTable;
import behavioralCapture.db.tables.Table;
import behavioralCapture.db.tables.TouchEventTable;

public class DbHandler extends SQLiteOpenHelper implements Runnable {
    private static final String DATABASE_NAME = "capturedEvents";
    private static final int DATABASE_VERSION = 33;
    private static final String TAG = "DbHandler";

    public static Table[] tables = {new MotionEventTable(),
            new TouchEventTable(),
            new DoubleTouchTable(),
            new SwipeEventTable(),
            new ScaleEventTable(),
            new KeyEventTable()};

    private BlockingQueue<Pair<Class, Jsonable>> queue;
    private Thread thread;


    public DbHandler(Context context) {
        this(context, null, null, 0);
    }

    private DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        queue = new LinkedBlockingDeque<>();
        thread = new Thread(this);
        thread.start();
    }

    public static String getCreateTableQuery(Table t) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        //sb.append(DATABASE_NAME + ".");
        sb.append(t.getName() + "(\n");
        String[] columnNames = t.getColumnNames();
        SqliteColumnTypes[] columnTypes = t.getColumnTypes();
        String[] modifiers = t.getColumnModifiers();
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i] + " ");
            sb.append(columnTypes[i] + " ");
            try {
                sb.append(modifiers[i]);
            } catch (IndexOutOfBoundsException e) {
                //keep calm and ignore the exception.
            }
            if (i < columnNames.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append(");");
        return sb.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table t : tables) {
            String query = getCreateTableQuery(t);
            db.execSQL(query);
            t.onCreate();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table t : tables) {
            t.onUpgrade();
            db.execSQL("DROP TABLE IF EXISTS " + t.getName());
        }
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private long insert(String tableName, ContentValues values) {
        SQLiteDatabase db=null;
        try {
            db = getWritableDatabase();
            return db.insert(tableName, null, values);
        }
        catch (SQLiteException e) {
            Log.e(TAG,"insertion exception",e);
            return -1;
        }
    }

    private long insertMotionSensorEvent(MotionSensorEventWrapper e) {
        ContentValues values = new ContentValues();
        values.put(MotionEventTable.ACCURACY, e.getAccuracy());
        values.put(MotionEventTable.TIMESTAMP, (e.getTimestamp()));
        values.put(MotionEventTable.TYPE, e.getType());
        values.put(MotionEventTable.X, e.getValues()[0]);
        values.put(MotionEventTable.Y, e.getValues()[1]);
        values.put(MotionEventTable.Z, e.getValues()[2]);
        values.put(MotionEventTable.IS_REMOVED,0);
        return insert(MotionEventTable.TABLE_NAME, values);
    }

    private long insertSwipeEvent(TouchEventChunk e) {
        ContentValues values = new ContentValues();
        values.put(SwipeEventTable.TIMESTAMP, e.getTimestamp());
        values.put(SwipeEventTable.IS_REMOVED,0);
        values.put(SwipeEventTable.TAG,e.getTag());
        values.put(SwipeEventTable.ACTIVITY,e.getActivity());
        long id = insert(SwipeEventTable.TABLE_NAME, values);
        for (SingleTouchEvent te : e.getEventsChunk()) {
            long id2 = insertTouchEvent(te,id);
        }
        return id;
    }

    private long insertKeyboardEvent(KeyboardEvent e) {
        ContentValues values = new ContentValues();
        values.put(KeyEventTable.ACTION, e.getType());
        values.put(KeyEventTable.DOWN_TIME, e.getTimestamp());
        values.put(KeyEventTable.KEY_CODE, e.getKeyCode());
        values.put(KeyEventTable.KEY_STR, e.getKeyStr());
        values.put(KeyEventTable.IS_REMOVED, 0);
        return insert(KeyEventTable.TABLE_NAME, values);
    }

    private long insertTouchEvent(SingleTouchEvent e, long chunkId) {
        ContentValues values = new ContentValues();
        values.put(TouchEventTable.ACTION, e.getAction());
        values.put(TouchEventTable.ACTION_STR, e.getAction_str());
        values.put(TouchEventTable.X, e.getX());
        values.put(TouchEventTable.Y, e.getY());
        values.put(TouchEventTable.DOWN_TIME, e.getDownTime());
        values.put(TouchEventTable.TIMESTAMP, e.getTimestamp());
        values.put(TouchEventTable.PRESSURE, e.getPressure());
        values.put(TouchEventTable.SIZE, e.getSize());
        values.put(TouchEventTable.IS_REMOVED,0);
        values.put(TouchEventTable.CHUNK_ID,chunkId);
        values.put(TouchEventTable.TAG,e.getTag());
        values.put(TouchEventTable.ACTIVITY,e.getActivity());

        return insert(TouchEventTable.TABLE_NAME, values);
    }

    private long insertDoubleTouchEvent(DoubleTouchEvent e,long chunkId) {
        ContentValues values = new ContentValues();
        values.put(DoubleTouchTable.ACTION, e.getAction());
        values.put(DoubleTouchTable.ACTION_STR, e.getAction_str());
        values.put(DoubleTouchTable.X0, e.getX0());
        values.put(DoubleTouchTable.X1, e.getX1());
        values.put(DoubleTouchTable.Y0, e.getY0());
        values.put(DoubleTouchTable.Y1, e.getY1());
        values.put(DoubleTouchTable.DOWN_TIME, e.getDownTime());
        values.put(DoubleTouchTable.TIMESTAMP, e.getTimestamp());
        values.put(DoubleTouchTable.PRESSURE0, e.getPressure0());
        values.put(DoubleTouchTable.PRESSURE1, e.getPressure1());
        values.put(DoubleTouchTable.SIZE0, e.getSize0());
        values.put(DoubleTouchTable.SIZE1, e.getSize1());
        values.put(DoubleTouchTable.IS_REMOVED, 0);
        values.put(DoubleTouchTable.SCALE_EVENT_ID,chunkId);
        values.put(DoubleTouchTable.TAG,e.getTag());
        values.put(DoubleTouchTable.ACTIVITY,e.getActivity());

        return insert(DoubleTouchTable.TABLE_NAME, values);
    }

    private long insertScaleEvent(ScaleTouchEvent e) {
        ContentValues values = new ContentValues();
        values.put(ScaleEventTable.DOWN_TIME, e.getTimestamp());
        values.put(ScaleEventTable.IS_REMOVED,0);
        values.put(ScaleEventTable.TAG,e.getTag());
        values.put(ScaleEventTable.ACTIVITY, e.getActivity());
        long id = insert(ScaleEventTable.TABLE_NAME, values);
        for (DoubleTouchEvent dte : e.getEvents()) {
            insertDoubleTouchEvent(dte,id);
        }
        return id;
    }

    public List<MotionSensorEventWrapper> getAllMotionEvents(String selection, String[] selectionArgs, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {MotionEventTable._ID, MotionEventTable.TYPE, MotionEventTable.TIMESTAMP, MotionEventTable.X, MotionEventTable.Y, MotionEventTable.Z, MotionEventTable.ACCURACY};
        String sortOrder = MotionEventTable.TIMESTAMP + " ASC";
        Cursor cursor = db.query(
                MotionEventTable.TABLE_NAME,
                projection,
                selection,                                // where clause without the "where" word. use ?. these ?s will get value from the next arg.
                selectionArgs,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,
                limit
        );
        List<MotionSensorEventWrapper> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new MotionEventTable().cursorToEventObject(cursor,0));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public static List<SingleTouchEvent> selectAllTouchEvents() {
        return Env.getInstance().getDb().selectTouchEvents(null, null,null);
    }

    public List<SingleTouchEvent> selectTouchEvents(String selection, String[] selectionArgs, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = null;
        String sortOrder = TouchEventTable.TIMESTAMP + " ASC";
        Cursor cursor = db.query(
                TouchEventTable.TABLE_NAME,
                projection,
                selection,                                // where clause without the "where" word. use ?. these ?s will get value from the next arg.
                selectionArgs,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,
                limit
        );
        List<SingleTouchEvent> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new TouchEventTable().cursorToEventObject(cursor, 0));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public List<Jsonable> selectKeyboardEvents(String selection, String[] selectionArgs, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = null;
        String sortOrder = KeyEventTable.DOWN_TIME + " ASC";
        Cursor cursor = db.query(
                KeyEventTable.TABLE_NAME,
                projection,
                selection,                                // where clause without the "where" word. use ?. these ?s will get value from the next arg.
                selectionArgs,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,
                limit
        );
        List<Jsonable> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new KeyEventTable().cursorToEventObject(cursor,0));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }



    public List<Jsonable> selectSwipeEvents(int limit, int offset, String where) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder query = new StringBuilder("select * from ");
        query.append(SwipeEventTable.TABLE_NAME);
        query.append(" t1 join ");
        query.append(TouchEventTable.TABLE_NAME);
        query.append(" t2 on t1.");
        query.append(SwipeEventTable.ID);
        query.append("=t2.");
        query.append(TouchEventTable.CHUNK_ID);
        query.append(where==null?"":" where "+where);
        query.append(" order by t1.");
        query.append(SwipeEventTable.ID);
        query.append(",t2.");
        query.append(TouchEventTable.DOWN_TIME);
        query.append(" limit ");
        query.append(limit);
        query.append(" offset ");
        query.append(offset);
        Cursor cursor = db.rawQuery(query.toString(), null);
        cursor.moveToFirst();
        List<Jsonable> result = new LinkedList<>();
        while (!cursor.isAfterLast()) {
            TouchEventChunk event = new SwipeEventTable().cursorToEventObject(cursor,0);
            result.add(event);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }


    public List<Jsonable> selectScaleEvents(int limit, int offset,String where) {
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder query = new StringBuilder("select * from ");
        query.append(ScaleEventTable.TABLE_NAME);
        query.append(" t1 join ");
        query.append(DoubleTouchTable.TABLE_NAME);
        query.append(" t2 on t1.");
        query.append(ScaleEventTable.ID);
        query.append("=t2.");
        query.append(DoubleTouchTable.SCALE_EVENT_ID);
        query.append(where==null?"":" where "+where);
        query.append(" order by t1.");
        query.append(ScaleEventTable.ID);
        query.append(",t2.");
        query.append(DoubleTouchTable.DOWN_TIME);
        query.append(" limit ");
        query.append(limit);
        query.append(" offset ");
        query.append(offset);
        Cursor cursor = db.rawQuery(query.toString(), null);
        cursor.moveToFirst();
        List<Jsonable> result = new LinkedList<>();
        while(!cursor.isAfterLast()) {
            result.add(new ScaleEventTable().cursorToEventObject(cursor, 0));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public long getEventCountForTableName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select count(*) from ? where IS_REMOVED=0", new String[]{name});
        c.moveToFirst();
        long l = c.getLong(0);
        return l;
    }



    public List<MotionSensorEventWrapper> getAllMotionEventsBetweenTimestamps(long start, long end) {
        String selection = MotionEventTable.TIMESTAMP + " = ? AND " + MotionEventTable.TIMESTAMP + " = ?";
        String[] selectionArgs = {"" + start, "" + end};
        return getAllMotionEvents(selection, selectionArgs, null);
    }

    /*public int deleteMotionEventsByTimeStamps(long start, long end) {
        return delete(MotionEventTable.TABLE_NAME, MotionEventTable.TIMESTAMP + " = ? AND " + MotionEventTable.TIMESTAMP + " = ?"
                , new String[]{"" + start, "" + end});
    }

    private int delete(String tableName, String selection, String[] selectArgs) {
        return getReadableDatabase().delete(tableName, selection, selectArgs);
    }*/

    public void insert(Class clz, Jsonable event) {
        try {
            queue.put(new Pair<>(clz, event));
        } catch (InterruptedException e) {
            Log.e(TAG, "exception in inserting an event to the db queue ", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Pair<Class, Jsonable> p = queue.take();
                if (p.first.equals(MotionSensorEventWrapper.class)) {
                    insertMotionSensorEvent((MotionSensorEventWrapper) p.second);
                } else if ((p.first.equals(SingleTouchEvent.class))) {
                    insertTouchEvent((SingleTouchEvent) p.second, -1);
                } else if ((p.first.equals(TouchEventChunk.class))) {
                    insertSwipeEvent((TouchEventChunk) p.second);
                } else if ((p.first.equals(KeyboardEvent.class))) {
                    insertKeyboardEvent((KeyboardEvent) p.second);
                } else if ((p.first.equals(ScaleTouchEvent.class))) {
                    insertScaleEvent((ScaleTouchEvent) p.second);
                }

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
