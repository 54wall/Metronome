package pri.cainwong.metronome.core;

import android.util.Log;

public class BeatModel {
    int mX;
    int mY;
    private String TAG = BeatModel.class.getSimpleName();

    public BeatModel(int x, int y) {
        Log.e(TAG, "BeatModel");
        mX = x;
        mY = y;
    }

    public int getmX() {
        Log.e(TAG, "getmX");
        return mX;
    }

    public void setmX(int mX) {
        Log.e(TAG, "setmX");
        this.mX = mX;
    }

    public int getmY() {
        Log.e(TAG, "getmY");
        return mY;
    }

    public void setmY(int mY) {
        Log.e(TAG, "setmY");
        this.mY = mY;
    }

    public String getName() {
        Log.e(TAG, "getName");
        return mX + "/" + mY;
    }

}
