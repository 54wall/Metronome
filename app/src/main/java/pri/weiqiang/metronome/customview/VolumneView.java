package pri.weiqiang.metronome.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import pri.weiqiang.metronome.R;


public class VolumneView extends View {

    public static final int OFFSET = 0;//左边起始偏移量
    private String TAG = VolumneView.class.getSimpleName();
    private Paint mPaint;
    private int mCount;
    private int mWidth;
    private int mRectHeight;
    private int mRectWidth;
    private LinearGradient mLinearGradient;
    private double mRandom;
    private float mcurrentHeight;
    private int delay = 600;

    public VolumneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);

    }

    public VolumneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumneView(Context context) {
        this(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setColor(Color.BLUE);
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.volumneView);
        if (ta != null) {
            mCount = ta.getInt(R.styleable.volumneView_count, 6);
            ta.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mRectHeight = getMeasuredHeight();
        mRectWidth = mWidth / mCount;
        Log.e(TAG, "mWidth:" + mWidth + "mRectWidth:" + mRectWidth);
        mLinearGradient = new LinearGradient(0, 0, mRectWidth, mRectHeight,
                Color.TRANSPARENT, getResources().getColor(R.color.colorPrimaryDark)
                , Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //整个onDraw将会一直循环运行
//        Log.e(TAG, "onDraw");
        super.onDraw(canvas);
        for (int i = 0; i < mCount; i++) {
            mRandom = Math.random();
            mcurrentHeight = (float) (mRectHeight * mRandom);
            canvas.drawRect(OFFSET + i * mRectWidth, mcurrentHeight, OFFSET
                    + (i + 1) * mRectWidth, mRectHeight, mPaint);
        }
        postInvalidateDelayed(delay);
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}