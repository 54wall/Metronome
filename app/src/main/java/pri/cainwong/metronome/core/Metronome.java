package pri.cainwong.metronome.core;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by cwong on 10/14/15.
 */
@Singleton
public class Metronome {

    public static final int DEFAULT_DELAY = 500;
    @Inject
    @Named("newThread")
    Scheduler scheduler;
    private String TAG = Metronome.class.getSimpleName();
    private int mBpm = 100;
    private int mX = 4;
    private int mY = 4;
    private int delay;
    private int numBeats = mX;
    private int beat = 0;
    private int firstDelay = 0;
    private BehaviorSubject<Integer> delayObservable = BehaviorSubject.create();
    private BehaviorSubject<Integer> beatObservable = BehaviorSubject.create();
    private BehaviorSubject<Boolean> playStateObservable = BehaviorSubject.create();
    private PublishSubject<Object> stopTrigger = PublishSubject.create();
    private boolean delayIsChange = false;

    @Inject
    public Metronome() {
        Log.e(TAG, "Metronome");
        setConfig(mBpm, mX, mY);
    }

    private void setDelay(int delay) {
        Log.e(TAG, "^^^^^setDelay");
        this.delay = delay;
        delayObservable.onNext(delay);
        restartIfPlaying();
    }

    public void setConfig(int pdm, int x, int y) {
        Log.e(TAG, "setConfig mBpm:" + mBpm + ",mX:" + mX + ",mY:" + mY);
        if (mBpm == 0 || x == 0 || y == 0) {
            return;
        }
        mBpm = pdm;
        mX = x;
        mY = y;
        delay = (int) (((1000 * 60.0) / mBpm) * (1.0 * mX / mY));
        delayIsChange = true;
        Log.e(TAG, "频率:" + mBpm);
    }


    public void setConfig(int x, int y) {
        Log.e(TAG, "setConfig x y");
        setConfig(mBpm, x, y);
    }

    public void setConfig(int pdm) {
        Log.e(TAG, "setConfig pdm");
        setConfig(pdm, mX, mY);
    }


    public void setNumBeats(int numBeats) {
        Log.e(TAG, "setNumBeats");
        this.numBeats = numBeats;
        restartIfPlaying();
    }

    public Observable<Integer> getDelayObservable() {
        Log.e(TAG, "getDelayObservable");
        return delayObservable;
    }

    public Observable<Integer> getBeatObservable() {
        Log.e(TAG, "getBeatObservable");
        return beatObservable;
    }

    public Observable<Boolean> getPlayStateObservable() {
        Log.e(TAG, "getPlayStateObservable");
        return playStateObservable;
    }


    public void togglePlay() {
        Log.e(TAG, "togglePlay");
        if (isPlaying()) {
            stop();
        } else {
            play();
        }
    }

    private boolean isPlaying() {
        Log.e(TAG, "^^^^^isPlaying()");
        return Boolean.TRUE.equals(playStateObservable.getValue());
    }

    private void play() {
        Log.e(TAG, "^^^^^play() interval");
        playStateObservable.onNext(true);
        Observable.interval(delay, TimeUnit.MILLISECONDS, scheduler)
                .takeUntil(stopTrigger)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "play() beat=" + beat + "频率:" + mBpm);
                        beat++;
                        beatObservable.onNext(beat);
                        if (beat == numBeats) {
                            beat = 0;
                        }
                        if (delayIsChange) {
                            delayIsChange = false;
                            setDelay(delay);
                        }
                    }
                });


    }

    private void stop() {
        Log.e(TAG, "^^^^^stop");
        stopTrigger.onNext("stop");
//        stopTrigger.onNext(null);
//        stopTrigger.onComplete();

        beat = 0;
        Log.e(TAG, "stop 重置 beat = 0");
        playStateObservable.onNext(false);
    }

    public void stopPlay() {
        Log.e(TAG, "^^^^^stopPlay");
        if (isPlaying()) {
            stop();
        }
    }

    private void restartIfPlaying() {
        Log.e(TAG, "^^^^^restartIfPlaying");

        if (isPlaying()) {
            stopTrigger.onNext("restartIfPlaying");
//            stopTrigger.onNext(null);
//            stopTrigger.onComplete();
            play();
        }
    }

}
