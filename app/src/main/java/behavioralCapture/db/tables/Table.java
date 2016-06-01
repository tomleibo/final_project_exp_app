package behavioralCapture.db.tables;

import android.database.Cursor;

import java.util.List;

import behavioralCapture.Utils.Jsonable;

/**
 * @param <T> The Type of event which correlates with the table.
 */
public interface Table<T> {
    String getName();
    String[] getColumnNames();
    SqliteColumnTypes[] getColumnTypes();
    String[] getColumnModifiers();
    void onCreate();
    void onUpgrade();
    boolean dropOnCreate();
    T cursorToEventObject(Cursor cursor,int joinOffset);
    List<? extends Jsonable> selectFromDate(int limit,int offset,long time);
    List<? extends Jsonable> selectAll(int limit,int offset);
    /**
     * @param endTime can be ignored by passing a neative value.
     * @param limit relevant only for tables that have foreign keys.
     * @return rows updated.
     */
    long setIsRemoved(long startTime,long endTime, int limit);

    static final int TOUCH = 1000;

}
