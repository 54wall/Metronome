package com.cainwong.metronome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cainwong.metronome.App;
import com.cainwong.metronome.R;
import com.cainwong.metronome.RotateControlView;
import com.cainwong.metronome.core.BeatModel;
import com.cainwong.metronome.core.Metronome;
import com.cainwong.metronome.services.AudioService;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    public static final long MIN_DELAY = 215;
    public static final long MAX_DELAY = 3000;
    public static final int ROUNDTO_VALUE = 10;
    @Inject
    Metronome metronome;
    @Inject
    @Named("mainThread")
    Scheduler mainThreadScheduler;
    @Inject
    @Named("immediate")
    Scheduler intervalScheduler;
    @Inject
    @Named("newThread")
    Scheduler newThreadScheduler;
    Subscription delaySubscription;
    Subscription playStateSubscription;
    @BindView(R.id.rotate)
    RotateControlView rotate;
    @BindView(R.id.beat_left_ib)
    ImageButton beatLeftIb;
    @BindView(R.id.beat_tempo_tv)
    TextView beatTempoTv;
    @BindView(R.id.beat_right_ib)
    ImageButton beatRightIb;
    @BindView(R.id.beat_less_ib)
    ImageButton beatLessIb;
    @BindView(R.id.beat_plus_ib)
    ImageButton beatPlusIb;
    @BindView(R.id.beat_start_ib)
    ImageButton beatStartIb;
    @BindView(R.id.beat_switch)
    TextView beatSwitch;
    @BindView(R.id.beat_bpm_et)
    EditText beatBpmEt;
    private String TAG = MainActivity.class.getSimpleName();
    private int mBpm = 120;
    private int mX = 4;
    private int mY = 4;
    private int mMaxValue = 400;       //最大值
    private int mMinValue = 60;       //最小值
    private BeatModel[] mBeatArray = new BeatModel[]{new BeatModel(2, 2), new BeatModel(3, 4), new BeatModel(4, 4), new BeatModel(4, 8), new BeatModel(6, 8)};
    private int curentBeatDex = 2;

    //Activity最外层的Layout视图
    private View activityRootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component(this).inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rotate.setMaxAndMinValue(mMinValue, mMaxValue, mBpm);
        beatTempoTv.setText(mBeatArray[curentBeatDex].getName());
        beatBpmEt.setText(mBpm + "");
        activityRootView = findViewById(R.id.root_layout);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, final int left, final int top, final int right,
                                       final int bottom, final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                            metronome.stopPlay();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    beatBpmEt.setCursorVisible(true);
                                }
                            });

                        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                            String dpmStr = beatBpmEt.getText().toString();
                            if (dpmStr == null || dpmStr.equals("")) {
                                mBpm = mMinValue;
                            } else {
                                mBpm = Integer.parseInt(dpmStr);
                                if (mBpm > mMaxValue) {
                                    mBpm = mMaxValue;
                                } else if (mBpm < mMinValue) {
                                    mBpm = mMinValue;
                                }


                            }
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    beatBpmEt.setText(mBpm + "");

                                    beatBpmEt.setCursorVisible(false);
                                }
                            });
                            metronome.setConfig(mBpm);
                            rotate.setValue(mBpm);
                        }
                    }
                }).start();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        RxView.clicks(beatLeftIb)
//                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        curentBeatDex--;
                        if (curentBeatDex < 0) {
                            curentBeatDex = (mBeatArray.length - 1);
                        }
                        metronome.setConfig(mBeatArray[curentBeatDex].getmX(), mBeatArray[curentBeatDex].getmY());
                        beatTempoTv.setText(mBeatArray[curentBeatDex].getName());
                    }
                });


        RxView.clicks(beatRightIb)
//                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        curentBeatDex++;
                        if (curentBeatDex > (mBeatArray.length - 1)) {
                            curentBeatDex = 0;
                        }
                        metronome.setConfig(mBeatArray[curentBeatDex].getmX(), mBeatArray[curentBeatDex].getmY());
                        beatTempoTv.setText(mBeatArray[curentBeatDex].getName());
                    }
                });


        RxView.clicks(beatLessIb)
//                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        mBpm--;
                        if (mBpm < mMinValue) {
                            mBpm = mMinValue;
                        }
                        metronome.setConfig(mBpm);
                        rotate.setValue(mBpm);
                        beatBpmEt.setText(mBpm + "");
                    }
                });


        RxView.clicks(beatPlusIb)
//                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        mBpm++;
                        if (mBpm > mMaxValue) {
                            mBpm = mMaxValue;
                        }
                        metronome.setConfig(mBpm);
                        rotate.setValue(mBpm);
                        beatBpmEt.setText(mBpm + "");
                    }
                });


        rotate.setOnTempChangeListener(new RotateControlView.OnTempChangeListener() {
            @Override
            public void change(final int temp) {
                metronome.setConfig(temp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBpm = temp;
                        beatBpmEt.setText(mBpm + "");
                    }
                });

            }
        });

        // Handle click events

        RxView.clicks(beatStartIb)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        metronome.togglePlay();
                    }
                });

        // play state display
        playStateSubscription = metronome.getPlayStateObservable()
                .observeOn(mainThreadScheduler)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        beatStartIb.setImageResource(aBoolean ? R.mipmap.pause_icon
                                : R.mipmap.play_icon);
                        if (aBoolean) {
                            startService(new Intent(getApplicationContext(), AudioService.class));
                        } else {
                            stopService(new Intent(getApplicationContext(), AudioService.class));
                        }
                    }
                });

    }

    @Override
    protected void onStop() {
        if (delaySubscription != null && !delaySubscription.isUnsubscribed()) {
            delaySubscription.unsubscribe();
        }
        if (playStateSubscription != null && !playStateSubscription.isUnsubscribed()) {
            playStateSubscription.unsubscribe();
        }
        super.onStop();
    }
}