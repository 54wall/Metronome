package pri.cainwong.metronome.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pri.cainwong.metronome.App;
import pri.cainwong.metronome.R;
import pri.cainwong.metronome.core.Metronome;

public class AudioService extends Service {

    public static final int CLICK_SOUND = AudioManager.FX_KEYPRESS_STANDARD;
    public static final int ACCENT_CLICK_SOUND = AudioManager.FX_FOCUS_NAVIGATION_UP;
    @Inject
    AudioManager audioManager;
    @Inject
    Metronome metronome;
    Disposable beatSubscription;
    //最大音量
    float audioMaxVolumn;
    //当前音量
    float audioCurrentVolumn;
    float volumnRatio;
    private String TAG = AudioService.class.getSimpleName();
    //实例化AudioManager对象，控制声音
    private AudioManager am = null;
    //然后就是需要初始化SoundPool，并且把音频放入HashMap中
    //音效播放池
    private SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    //存放音效的HashMap
    private Map<Integer, Integer> map = new HashMap<Integer, Integer>();


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        App.component(this).inject(this);
        initSoundPool();
    }


    private void initSoundPool() {
        Log.e(TAG, "initSoundPool");
        //实例化AudioManager对象，控制声音
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        //最大音量
        audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;

        map.put(0, soundPool.load(this, R.raw.tock0, 1));
        //metronome3.mp3 在android28上没有声音，因为文件太小，只有467字节
        map.put(1, soundPool.load(this, R.raw.tick0, 1));
    }

    private void playSoundPool(int key) {
        final long time = SystemClock.currentThreadTimeMillis();
        Log.e(TAG, "key=" + key + ",volumnRatio=" + volumnRatio);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                Log.e(TAG, "setOnLoadCompleteListener" + (SystemClock.currentThreadTimeMillis() - time));
            }
        });
//        key = 0;
        soundPool.play(
                map.get(key),//声音资源
                volumnRatio,//左声道
                volumnRatio,//右声道
                1,//优先级
                0,//循环次数，0是不循环，-1是一直循环
                1);//回放速度，0.5~2.0之间，1为正常速度

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if (beatSubscription == null /*|| beatSubscription.isUnsubscribed()*/) {
            beatSubscription = metronome.getBeatObservable().subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer beat) throws Exception {
                    int key = (beat == 1) ? 0 : 1;
                    Log.e(TAG, "onStartCommand beat=" + beat);
                    playSoundPool(key);
                }

//                @Override
//                public void call(Integer beat) {

//                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (beatSubscription != null && !beatSubscription.isDisposed()) {
            beatSubscription.dispose();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return null;
    }

}
