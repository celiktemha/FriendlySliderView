package com.celiktemha.friendlysliderview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.celiktemha.friendlyslider.R;

import java.util.ArrayList;

/**
 * Created by ahmetcelik on 13.03.2018.
 */

public class FriendlySliderView extends View {
    private static final String TAG = FriendlySliderView.class.getSimpleName();

    private static final int CIRCLE_ANIMATION_DURATION = 120;

    private int sliderCircleColor = Color.WHITE;
    private int sliderCircleTextColor = Color.BLUE;
    private int sliderTextColor = Color.WHITE;
    private int sliderBackgroundColor = Color.BLUE;

    private static int DEFAULT_MIN_VALUE = 0;
    private static int DEFAULT_MAX_VALUE = 100;
    public int circleYAxisChangeValue;

    private Paint sliderPaint;
    private Paint sliderCurrentValuePaint;
    private TextPaint sliderTextPaint;
    private RectF sliderRect;

    private String minStringValue;
    private int minValue = DEFAULT_MIN_VALUE;
    private int maxValue = DEFAULT_MAX_VALUE;
    private String maxStringValue;

    private int sliderCircleSize;

    float currentX = 0;
    float deltaY = 0;

    private int currentValue = DEFAULT_MIN_VALUE;
    private boolean valueChangeByMethod;

    private OnSliderValueChangeListener listener;
    private int valueTextSize;


    public FriendlySliderView(Context context) {
        super(context);
        init(null);
    }

    public FriendlySliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void init(AttributeSet attributeSet) {

        Resources resources = getResources();

        if (attributeSet != null) {
            TypedArray ta = resources.obtainAttributes(attributeSet, R.styleable.FriendlySliderView);

            setMinValue(ta.getInt(R.styleable.FriendlySliderView_minValue, DEFAULT_MIN_VALUE));
            setMaxValue(ta.getInt(R.styleable.FriendlySliderView_maxValue, DEFAULT_MAX_VALUE));
            setCurrentValue(ta.getInt(R.styleable.FriendlySliderView_currentValue, DEFAULT_MIN_VALUE));
            sliderBackgroundColor = ta.getColor(R.styleable.FriendlySliderView_sliderBackgroundColor, sliderBackgroundColor);
            sliderTextColor = ta.getColor(R.styleable.FriendlySliderView_sliderTextColor, sliderTextColor);
            sliderCircleColor = ta.getColor(R.styleable.FriendlySliderView_sliderCircleColor, sliderCircleColor);
            sliderCircleTextColor = ta.getColor(R.styleable.FriendlySliderView_sliderCircleTextColor, sliderCircleTextColor);

            ta.recycle();
        }

        valueTextSize = resources.getDimensionPixelSize(R.dimen.sliderTextSize);

        createPaints();

        sliderRect = new RectF();

    }

    private void createPaints() {
        sliderPaint = new Paint();
        sliderPaint.setColor(sliderBackgroundColor);
        sliderPaint.setStyle(Paint.Style.FILL);
        sliderPaint.setAntiAlias(true);


        sliderCurrentValuePaint = new Paint();
        sliderCurrentValuePaint.setColor(sliderCircleColor);
        sliderCurrentValuePaint.setStyle(Paint.Style.FILL);
        sliderCurrentValuePaint.setAntiAlias(true);

        sliderTextPaint = new TextPaint();
        sliderTextPaint.setColor(sliderTextColor);
        sliderTextPaint.setTextSize(valueTextSize);
        sliderTextPaint.setTextAlign(Paint.Align.CENTER);
        sliderTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((ViewGroup) getParent()).setClipChildren(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();


        sliderRect.set(left, top, right, bottom);

        left = left + sliderCircleSize;
        right = right - sliderCircleSize;

        int widthWithoutPadding = right - left;

        canvas.drawRoundRect(sliderRect, 10, 10, sliderPaint);

        if (valueChangeByMethod) {
            valueChangeByMethod = false;
            currentX = widthWithoutPadding * ((currentValue - minValue) * 1.0f / (maxValue - minValue) * 1.0f);
        }

        float cx = left + currentX;
        float cy = getHeight() / 2f - deltaY;

        if (cx <= left) {
            cx = left;
        } else if (cx > right) {
            cx = right;
        }

        if (deltaY > 0) {
            sliderCurrentValuePaint.setStrokeWidth(4f);
            sliderCurrentValuePaint.setStyle(Paint.Style.STROKE);
        } else {
            sliderCurrentValuePaint.setStrokeWidth(0f);
            sliderCurrentValuePaint.setStyle(Paint.Style.FILL);
        }

        canvas.drawCircle(cx, cy, sliderCircleSize, sliderCurrentValuePaint);


        sliderTextPaint.setColor(sliderTextColor);


        currentValue = getCurrentValue(cx - left, widthWithoutPadding);

        if ((currentValue - 10 > minValue && deltaY == 0) || deltaY != 0) {
            canvas.drawText(
                    minStringValue,
                    left,
                    getHeight() / 2 - ((sliderTextPaint.descent() + sliderTextPaint.ascent()) / 2),
                    sliderTextPaint);
        }

        if ((currentValue < maxValue - 10 && deltaY == 0) || deltaY != 0) {
            canvas.drawText(
                    String.valueOf(maxStringValue),
                    right,
                    getHeight() / 2 - ((sliderTextPaint.descent() + sliderTextPaint.ascent()) / 2),
                    sliderTextPaint);
        }


        sliderTextPaint.setColor(sliderCircleTextColor);
        canvas.drawText(
                String.valueOf(currentValue),
                cx,
                cy - ((sliderTextPaint.descent() + sliderTextPaint.ascent()) / 2),
                sliderTextPaint);

    }

    private void setInitialValueBySliderHeight(int sliderHeight) {
        circleYAxisChangeValue = sliderHeight;
        sliderCircleSize = (int) Math.floor(sliderHeight / 2f) - getResources().getDimensionPixelSize(R.dimen.sliderCircleVerticalPadding);
    }

    public void setOnSliderValueChangeListener(OnSliderValueChangeListener listener) {
        this.listener = listener;
    }

    public void setCurrentValue(int value) {
        valueChangeByMethod = true;
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }

        currentValue = value;
        invalidate();
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        this.minStringValue = String.valueOf(minValue);
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        this.maxStringValue = String.valueOf(maxValue);
        invalidate();
    }

    private int getCurrentValue(float cx, float width) {
        return Math.round(cx * (maxValue - minValue) / width) + minValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();

        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom()
                + getResources().getDimensionPixelSize(R.dimen.sliderDefaultHeight);

        int widthSize = measureDimension(desiredWidth, widthMeasureSpec, true);
        int heightSize = measureDimension(desiredHeight, heightMeasureSpec, false);

        int actualBarSize = heightSize - getPaddingTop() - getPaddingBottom();
        setInitialValueBySliderHeight(actualBarSize);

        setMeasuredDimension(widthSize, heightSize);
    }

    private int measureDimension(int desiredSize, int measureSpec, boolean isWidth) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            if (isWidth) {
                result = specSize;
            } else {
                if (getLayoutParams().height == -1) {
                    result = desiredSize;
                } else {
                    result = specSize;
                }
            }
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = isWidth ? Math.max(result, specSize) : Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getActionMasked();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                startAnimation(true);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAnimation(false);
                break;
        }

        currentX = event.getX() - sliderCircleSize * 2;
        invalidate();

        return true;
    }

    private void startAnimation(final boolean up) {
        ValueAnimator valueAnimator = up ? ValueAnimator.ofFloat(0, circleYAxisChangeValue) : ValueAnimator.ofFloat(circleYAxisChangeValue, 0);
        valueAnimator.setDuration(CIRCLE_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ArrayList listenerList = valueAnimator.getListeners();
        if (listenerList == null || listenerList.size() == 0) {
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    deltaY = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!up && listener != null) {
                        listener.onSliderValueChanged(currentValue);
                    }
                }
            });
        }


        valueAnimator.start();
    }

}
