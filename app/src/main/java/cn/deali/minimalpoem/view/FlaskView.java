package cn.deali.minimalpoem.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import cn.deali.minimalpoem.R;

/**
 * @author SamLeung
 * e-mail samlssplus@gmail.com
 * github https://github.com/samlss
 * description A flask loading view.
 */

public class FlaskView extends View {
    private final static int DEFAULT_SIZE = 300; //the default size if set "wrap_content"
    private final static int DEFAULT_STROKE_WIDTH = 5; // the default stoke width
    private final static float DEFAULT_HEIGHT_PERCENT = 0.5f; //the default height of water
    private final static int DEFAULT_STROKE_COLOR = Color.WHITE; //the default stoke color
    private final static int DEFAULT_WATER_COLOR = Color.parseColor("#3e6cbb"); //the default water color
    private final static int DEFAULT_BUBBLE_COLOR = Color.parseColor("#ced0d4"); //the default bubble color
    private final static int DEFAULT_BUBBLE_MAX_RADIUS = 15; //the default max radius of bubble in pixel
    private final static int DEFAULT_BUBBLE_MIN_RADIUS = 5; //the default max radius of bubble in pixel
    private final static float DEFAULT_BUBBLE_MAX_SPEECH = 10; //the default max speech value
    private final static float DEFAULT_BUBBLE_MIN_SPEECH = 5; //the default max speech value
    private final static int DEFAULT_BUBBLE_MAX_NUMBER = 20; //Number of bubbles that can coexist at the same time
    private final static int DEFAULT_BUBBLE_CREATION_INTERVAL = 50; //the default bubble creation interval in millis

    private int mStrokeColor = DEFAULT_STROKE_COLOR;
    private int mWaterColor = DEFAULT_WATER_COLOR;
    private int mBubbleColor = DEFAULT_BUBBLE_COLOR;

    private int mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private float mWaterHeightPercent = DEFAULT_HEIGHT_PERCENT;

    private Paint mStrokePaint;
    private Paint mWaterPaint;
    private Paint mBubblePaint;

    private Path mFlaskPath;
    private RectF mFlaskBoundRect, mWaterRect; //the whole flask bound rect & the water rect

    private int mBubbleMaxRadius = DEFAULT_BUBBLE_MAX_RADIUS;
    private int mBubbleMinRadius = DEFAULT_BUBBLE_MIN_RADIUS;

    private List<Bubble> mBubbles; //all the bubbles
    private List<Bubble> mRemovedBubbles; //help to record the removed bubbles, avoid "java.util.ConcurrentModificationException"
    private Stack<Bubble> mRecycler;

    private Random mOnlyRandom = new Random(); //the only random object

    private int mBubbleCreationInterval = DEFAULT_BUBBLE_CREATION_INTERVAL;
    private int mBubbleMaxNumber = DEFAULT_BUBBLE_MAX_NUMBER;

    private float mBubbleMaxSpeed = DEFAULT_BUBBLE_MAX_SPEECH;
    private float mBubbleMinSpeed = DEFAULT_BUBBLE_MIN_SPEECH;
    private long mBubbleCreationTime;

    private Handler mHandler = new Handler(); //help to update the bubbles state task

    public FlaskView(Context context) {
        this(context, null);
    }

    public FlaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        parseAttrs(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        parseAttrs(attrs);
        init();
    }

    private void parseAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FlaskView);
        mStrokeColor = typedArray.getColor(R.styleable.FlaskView_stroke_color, DEFAULT_STROKE_COLOR);
        mWaterColor = typedArray.getColor(R.styleable.FlaskView_water_color, DEFAULT_WATER_COLOR);
        mBubbleColor = typedArray.getColor(R.styleable.FlaskView_bubble_color, DEFAULT_BUBBLE_COLOR);
        mBubbleMaxRadius = typedArray.getInteger(R.styleable.FlaskView_bubble_max_radius, DEFAULT_BUBBLE_MAX_RADIUS);
        mBubbleMinRadius = typedArray.getInteger(R.styleable.FlaskView_bubble_min_radius, DEFAULT_BUBBLE_MIN_RADIUS);
        mBubbleMaxSpeed = typedArray.getFloat(R.styleable.FlaskView_bubble_max_speed, DEFAULT_BUBBLE_MAX_SPEECH);
        mBubbleMinSpeed = typedArray.getFloat(R.styleable.FlaskView_bubble_min_speed, DEFAULT_BUBBLE_MIN_SPEECH);
        mBubbleMaxNumber = typedArray.getInteger(R.styleable.FlaskView_bubble_max_number, DEFAULT_BUBBLE_MAX_NUMBER);
        mBubbleCreationInterval = typedArray.getInteger(R.styleable.FlaskView_bubble_creation_interval, DEFAULT_BUBBLE_CREATION_INTERVAL);
        mStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.FlaskView_stroke_width, DEFAULT_STROKE_WIDTH);
        mWaterHeightPercent = typedArray.getFloat(R.styleable.FlaskView_water_height_percent, DEFAULT_HEIGHT_PERCENT);

        typedArray.recycle();

        checkPercent();
    }

    private void checkPercent() {
        if (mWaterHeightPercent > 1) {
            mWaterHeightPercent = 1;
        } else if (mWaterHeightPercent < 0) {
            mWaterHeightPercent = 0;
        }
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mWaterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWaterPaint.setStyle(Paint.Style.FILL);
        mWaterPaint.setColor(mWaterColor);

        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setStyle(Paint.Style.FILL);

        mFlaskPath = new Path();
        mFlaskBoundRect = new RectF();
        mWaterRect = new RectF();

        mBubbles = new ArrayList<>(mBubbleMaxNumber);
        mRemovedBubbles = new ArrayList<>(mBubbleMaxNumber);
        mRecycler = new Stack<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int w = widthSpecSize;
        int h = heightSpecSize;

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE;
            h = DEFAULT_SIZE;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE;
            h = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize;
            h = DEFAULT_SIZE;
        }

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float centerX = w / 2;
        float centerY = h / 2;

        float flaskBottomCircleRadius = w / 5f;

        float neckHeight = flaskBottomCircleRadius * 2f / 3;
        float headHeight = 0.3f * neckHeight;

        mFlaskPath.reset();

        float flaskCenterY = centerY + (neckHeight + headHeight) / 2;

        float[] leftEndPos = new float[2];
        float[] rightEndPos = new float[2];
        float[] bottomPos = new float[2];

        leftEndPos[0] = (float) (flaskBottomCircleRadius * Math.cos(250 * Math.PI / 180f) + centerX);
        leftEndPos[1] = (float) (flaskBottomCircleRadius * Math.sin(250 * Math.PI / 180f) + flaskCenterY);

        rightEndPos[0] = (float) (flaskBottomCircleRadius * Math.cos(-70 * Math.PI / 180f) + centerX);
        rightEndPos[1] = (float) (flaskBottomCircleRadius * Math.sin(-70 * Math.PI / 180f) + flaskCenterY);

        bottomPos[0] = (float) (flaskBottomCircleRadius * Math.cos(90 * Math.PI / 180f) + centerX);
        bottomPos[1] = (float) (flaskBottomCircleRadius * Math.sin(90 * Math.PI / 180f) + flaskCenterY);

        RectF flaskArcRect = new RectF(centerX - flaskBottomCircleRadius, flaskCenterY - flaskBottomCircleRadius,
                centerX + flaskBottomCircleRadius, flaskCenterY + flaskBottomCircleRadius);

        mFlaskPath.addArc(flaskArcRect, -70, 320);

        mFlaskPath.moveTo(leftEndPos[0], leftEndPos[1]);
        mFlaskPath.lineTo(leftEndPos[0], leftEndPos[1] - neckHeight);

        mFlaskPath.quadTo(leftEndPos[0] - flaskBottomCircleRadius / 8, leftEndPos[1] - neckHeight - headHeight / 2,
                leftEndPos[0], leftEndPos[1] - neckHeight - headHeight);

        mFlaskPath.lineTo(rightEndPos[0], rightEndPos[1] - neckHeight - headHeight);

        mFlaskPath.quadTo(rightEndPos[0] + flaskBottomCircleRadius / 8, rightEndPos[1] - neckHeight - headHeight / 2,
                rightEndPos[0], rightEndPos[1] - neckHeight);

        mFlaskPath.lineTo(rightEndPos[0], rightEndPos[1]);

        mFlaskPath.computeBounds(mFlaskBoundRect, false);
        mFlaskBoundRect.bottom -= (mFlaskBoundRect.bottom - bottomPos[1]);

        updateWaterRect();
        start();
    }

    private void updateWaterRect() {
        mWaterRect.set(mFlaskBoundRect.left, mFlaskBoundRect.bottom - mFlaskBoundRect.height() * mWaterHeightPercent,
                mFlaskBoundRect.right, mFlaskBoundRect.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        //clip flask
        canvas.clipPath(mFlaskPath);

        //draw water
        canvas.drawRect(mWaterRect, mWaterPaint);

        //clip water rect
        canvas.clipRect(mWaterRect);

        //draw bubbles
        drawBubbles(canvas);

        canvas.restore();

        //draw the whole flask
        canvas.drawPath(mFlaskPath, mStrokePaint);
    }

    /**
     * Draw bubbles.
     */
    private void drawBubbles(Canvas canvas) {
        if (mBubbles.isEmpty()) {
            return;
        }

        for (Bubble bubble : mBubbles) {
            canvas.drawCircle(bubble.x, bubble.y, bubble.radius, mBubblePaint);
        }
    }

    /**
     * To optimize performance, use recycler {@link #mRecycler}
     */
    private Bubble obtainBubble() {
        if (mRecycler.isEmpty()) {
            return new Bubble();
        }

        return mRecycler.pop();
    }

    /**
     * Recycling the bubble that are no longer in use
     */
    private void recycle(Bubble bubble) {
        if (bubble == null) {
            return;
        }

        if (mRecycler.size() >= mBubbleMaxNumber) {
            mRecycler.pop();
        }

        mRecycler.push(bubble);
    }

    /**
     * Now create a random bubble.
     */
    private void createBubble() {
        if (mBubbles.size() >= mBubbleMaxNumber
                || mWaterRect.isEmpty()) {
            return;
        }

        long current = System.currentTimeMillis();
        if ((current - mBubbleCreationTime) < mBubbleCreationInterval) {
            return;
        }

        mBubbleCreationTime = current;

        Bubble bubble = obtainBubble();
        int radius = mBubbleMinRadius + mOnlyRandom.nextInt(mBubbleMaxRadius - mBubbleMinRadius);

        bubble.radius = radius;
        bubble.speed = mBubbleMinSpeed + mOnlyRandom.nextFloat() * mBubbleMaxSpeed;

        bubble.x = mWaterRect.left + mOnlyRandom.nextInt((int) mWaterRect.width()); //random x coordinate
        bubble.y = mWaterRect.bottom - radius - mStrokeWidth / 2; //the fixed y coordinate

        mBubbles.add(bubble);
    }


    /**
     * Update all the bubbles state
     */
    private void updateBubblesState() {
        mRemovedBubbles.clear();

        for (Bubble bubble : mBubbles) {
            if (bubble.y - bubble.speed <= mWaterRect.top - bubble.radius) {
                mRemovedBubbles.add(bubble);
                recycle(bubble);
            } else {
                bubble.y -= bubble.speed;
            }
        }

        if (!mRemovedBubbles.isEmpty()) {
            mBubbles.removeAll(mRemovedBubbles);
        }
    }


    /**
     * Internal class, include the bubble's parameter
     */
    private class Bubble {
        int radius;     //the bubble radius
        float speed;   //the bubble speed
        float x;        //the bubble start x
        float y;        //the bubble start y
    }

    /**
     * Set the water level to the percentage of the height of the flask, value is [0-1].
     */
    public void setWaterHeightPercent(float waterHeightPercent) {
        this.mWaterHeightPercent = waterHeightPercent;
        checkPercent();
        updateWaterRect();
        postInvalidate();
    }

    /**
     * Get the water level to the percentage of the height of the flask, value is [0-1].
     */
    public float getWaterHeightPercent() {
        return mWaterHeightPercent;
    }

    /**
     * Get the stroke color of flask.
     */
    public int getStrokeColor() {
        return mStrokeColor;
    }

    /**
     * Set the stroke color of flask.
     */
    public void setStrokeColor(int strokeColor) {
        this.mStrokeColor = strokeColor;
        mStrokePaint.setColor(mStrokeColor);
        postInvalidate();
    }

    /**
     * Get the water color.
     */
    public int getWaterColor() {
        return mWaterColor;
    }

    /**
     * Set the water color.
     */
    public void setWaterColor(int waterColor) {
        this.mWaterColor = waterColor;
        mWaterPaint.setColor(mWaterColor);
        postInvalidate();
    }

    /**
     * Get the color of all the bubbles .
     */
    public int getBubbleColor() {
        return mBubbleColor;
    }

    /**
     * Set the color of all the bubbles.
     */
    public void setBubbleColor(int bubbleColor) {
        this.mBubbleColor = bubbleColor;
        mBubblePaint.setColor(mBubbleColor);
        postInvalidate();
    }

    /**
     * Set the flask stroke width in pixel.
     */
    public void setStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        mStrokePaint.setStrokeWidth(strokeWidth);
        postInvalidate();
    }

    /**
     * Get the flask stroke width in pixel.
     */
    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * Set the creation of the bubble interval in millis,  i.e. the time difference between the two bubbles.
     */
    public void setBubbleCreationInterval(int bubbleCreationInterval) {
        this.mBubbleCreationInterval = bubbleCreationInterval;
    }

    /**
     * Get the creation of the bubble interval in millis.
     */
    public int getBubbleCreationInterval() {
        return mBubbleCreationInterval;
    }

    /**
     * The bubbles handled task.
     * Call handler to schedule the task.
     */
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            createBubble();
            updateBubblesState();
            invalidate();

            mHandler.postDelayed(this, 20);
        }
    };


    /**
     * Start animation
     */
    public void start() {
        stop();
        mHandler.post(mTask);
    }

    /**
     * Stop animation
     */
    public void stop() {
        mHandler.removeCallbacks(mTask);
    }

    /**
     * Release this view.
     */
    public void release() {
        stop();

        mRemovedBubbles.clear();
        mBubbles.clear();
        mRecycler.clear();
    }
}
