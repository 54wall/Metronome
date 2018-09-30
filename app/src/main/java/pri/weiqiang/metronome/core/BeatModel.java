package pri.weiqiang.metronome.core;

import android.util.Log;

public class BeatModel {
    private int mX;
    private int mY;
    private String TAG = BeatModel.class.getSimpleName();

    public BeatModel(int x, int y) {
        Log.e(TAG, "BeatModel");
        mX = x;
        mY = y;
    }

    public int getmX() {
        Log.e(TAG, "getmX:"+mX);
        return mX;
    }

    public void setmX(int mX) {
        Log.e(TAG, "setmX:"+mX);
        this.mX = mX;
    }

    public int getmY() {
        Log.e(TAG, "getmY:"+mY);
        return mY;
    }

    public void setmY(int mY) {
        Log.e(TAG, "setmY:"+mY);
        this.mY = mY;
    }

    public String getName() {
        Log.e(TAG, "getName:"+mX + "/" + mY);
        return mX + "/" + mY;
    }

}
