package behavioralCapture.Utils;

import android.content.Context;
import android.os.SystemClock;

public class SensorTimeCalibrator {
    private final Context context;
    private long prevSysTime = -1;
    private long mPrevTimestamp;
    private long mOffset = -1;
    private long mDivisor;
    private boolean isCalibrated=false;

    public SensorTimeCalibrator(Context context) {
        this.context=context;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public void setOffsetFromSensorToUptime(long timestamp) {
        long curSysTime = SystemClock.uptimeMillis();
        if (prevSysTime == -1){
            prevSysTime = SystemClock.uptimeMillis();
            mPrevTimestamp = timestamp;
            return;
        }
        if (mOffset == -1){
            long timestampDelta = timestamp - mPrevTimestamp;
            long sysTimeDelta = curSysTime - prevSysTime;
            prevSysTime = curSysTime;
            mPrevTimestamp = timestamp;
            if (sysTimeDelta == 0) {
                return;
            }
            if (timestampDelta / sysTimeDelta > 1000) {
                mDivisor = 1000000;
            } else {
                mDivisor = 1;
            }

            mOffset = (timestamp / mDivisor) - curSysTime;
            isCalibrated=true;
        }
    }

    public long getUptimeMillis(long timestamp) {
        return (timestamp/mDivisor) - mOffset;
    }


}
