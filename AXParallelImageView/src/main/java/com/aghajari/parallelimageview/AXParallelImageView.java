/*
 * Copyright (C) 2021 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.aghajari.parallelimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * AXParallelLineCollageImageView
 *
 * @author Amir Hossein Aghajari
 * @version 1.0.0
 */
public class AXParallelImageView extends View {

    @Nullable
    private Bitmap parallelLineBitmap = null;
    private int strokePeriod = 6;
    private int maxLines = 6;
    private float minStroke = 18, maxStroke = 36;
    private long duration = 500;
    private Direction direction = Direction.TOP_LEFT;
    private boolean autoStart = true;

    @Nullable
    private ValueAnimator valueAnimator = null;
    private float startX = 0;
    private float animationStart;
    private Paint paint;
    private final Path path = new Path();
    private boolean shouldRun = false;

    public enum Direction {
        TOP_LEFT(false),
        BOTTOM_RIGHT(true),
        TOP_RIGHT(true),
        BOTTOM_LEFT(false),
        LEFT(false),
        RIGHT(true),
        TOP(false),
        BOTTOM(true);

        private final boolean right;

        Direction(boolean rightMovementDirection) {
            right = rightMovementDirection;
        }

        public Direction getMovementDirection() {
            return right ? RIGHT : LEFT;
        }
    }

    public AXParallelImageView(@NonNull Context context) {
        this(context, null);
    }

    public AXParallelImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AXParallelImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        minStroke *= getContext().getResources().getDisplayMetrics().density;
        maxStroke *= getContext().getResources().getDisplayMetrics().density;

        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.AXParallelImageView, defStyleAttr, 0);
            autoStart = a.getBoolean(R.styleable.AXParallelImageView_autoStart, autoStart);
            startX = a.getDimension(R.styleable.AXParallelImageView_startPosition, startX);
            minStroke = a.getDimension(R.styleable.AXParallelImageView_minStroke, minStroke);
            maxStroke = a.getDimension(R.styleable.AXParallelImageView_maxStroke, maxStroke);
            strokePeriod = a.getInteger(R.styleable.AXParallelImageView_strokePeriod, strokePeriod);
            maxLines = a.getInteger(R.styleable.AXParallelImageView_maxLinesCount, maxLines);
            duration = a.getInteger(R.styleable.AXParallelImageView_duration, (int) duration);
            direction = Direction.values()[a.getInt(R.styleable.AXParallelImageView_direction, 0)];
            if (a.hasValue(R.styleable.AXParallelImageView_image)) {
                setParallelLineImage(a.getResourceId(R.styleable.AXParallelImageView_image, 0));
            } else if (a.hasValue(R.styleable.AXParallelImageView_grayImage)) {
                setGrayscaleParallelLineImage(a.getResourceId(R.styleable.AXParallelImageView_grayImage, 0));
            }
            a.recycle();
        }

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    private void initBitmaps() {
        if (parallelLineBitmap != null && getWidth() > 0 && getHeight() > 0) {
            paint.setShader(new BitmapShader(crop(parallelLineBitmap, getWidth(), getHeight())
                    , Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int xPadding = getPaddingLeft() + getPaddingRight();
        int yPadding = getPaddingTop() + getPaddingBottom();
        int width = getMeasuredWidth() - xPadding;
        int height = getMeasuredHeight() - yPadding;
        if (parallelLineBitmap == null || parallelLineBitmap.getHeight() == 0 || parallelLineBitmap.getWidth() == 0) {
            if (getBackground() != null && getBackground().getMinimumWidth() > 0 && getBackground().getMinimumHeight() > 0) {
                fixScale(xPadding, yPadding, width, height, getBackground().getMinimumWidth(), getBackground().getMinimumHeight());
            } else {
                int size = Math.min(width, height);
                setMeasuredDimension(size + xPadding, size + yPadding);
            }
        } else {
            fixScale(xPadding, yPadding, width, height, parallelLineBitmap.getWidth(), parallelLineBitmap.getHeight());
        }
    }

    private void fixScale(int xPadding, int yPadding, int width, int height, int bw, int bh) {
        boolean fixedWidth = bw >= bh;
        int size = Math.min(width, height);
        int mW, mH;
        if (fixedWidth) {
            mW = size;
            mH = bh * size / bw;
        } else {
            mH = size;
            mW = bw * size / bh;
        }

        while (mW + xPadding > width || mH + yPadding > height) {
            mW /= 1.5;
            mH /= 1.5;
        }
        setMeasuredDimension(mW + xPadding, mH + yPadding);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBitmaps();
        if (isRunning()) {
            stop();
            start();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attach();
    }

    private void attach() {
        if (autoStart || shouldRun)
            start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detach();
    }

    private void detach() {
        if (isRunning())
            pause();
        else
            stop();
    }

    @Override
    public void setVisibility(int visibility) {
        int old = getVisibility();
        super.setVisibility(visibility);
        if (old != visibility) {
            if (visibility == VISIBLE) {
                attach();
            } else {
                detach();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isEnabled())
            return;

        final float xSpace = findDistanceX();
        final float ySpace = findDistanceY();
        final float startY = findStartY();
        final int size = Math.max(getWidth(), getHeight()) * 2;

        path.reset();
        // draw origin line
        drawLine(path, 0, startX, startY);

        // after origin line
        int drawCount = Math.abs((int) ((size - startX) / xSpace)) + 1;
        for (int i = 1; i <= drawCount; i++) {
            float x = startX + i * xSpace;
            float y = startY + i * ySpace;
            drawLine(path, i, x, y);
        }

        // before origin line
        drawCount = Math.abs((int) (startX / xSpace)) + 1;
        for (int i = 1; i <= drawCount; i++) {
            float x = startX - i * xSpace;
            float y = startY - i * ySpace;
            drawLine(path, i, x, y);
        }

        path.close();
        canvas.drawPath(path, getPaint());
    }

    /**
     * calculate line's stroke
     *
     * @param i : distance from the origin line
     * @return line's stroke;
     */
    protected float findStroke(int i) {
        int res = i;
        boolean reverse = false;
        while (res > strokePeriod) {
            res -= strokePeriod;
            reverse = !reverse;
        }
        return (Math.abs(maxStroke - minStroke) / strokePeriod) * (reverse ? strokePeriod - res : res);
    }

    /**
     * @return lines path's Paint
     */
    protected Paint getPaint() {
        return paint;
    }

    /**
     * draw new line in path
     */
    protected void drawLine(Path path, int i, float x, float y) {
        if (!isOnDisplay(x, y))
            return;

        final float stroke = minStroke + (i > 0 ? findStroke(i) : 0);
        float y1 = y - stroke / 2;
        float y2 = y + stroke / 2;
        float x1 = x - stroke / 2;
        float x2 = x + stroke / 2;

        switch (direction) {
            case TOP_LEFT:
            case BOTTOM_RIGHT:
                path.moveTo(0, y1);
                path.lineTo(x1, 0);
                path.lineTo(x2, 0);
                path.lineTo(0, y2);
                path.lineTo(0, y1);
                break;
            case TOP_RIGHT:
            case BOTTOM_LEFT:
                path.moveTo(getWidth(), y1);
                path.lineTo(getWidth() - x1, 0);
                path.lineTo(getWidth() - x2, 0);
                path.lineTo(getWidth(), y2);
                path.lineTo(getWidth(), y1);
                break;
            case LEFT:
            case RIGHT:
                path.moveTo(x1, 0);
                path.lineTo(x2, 0);
                path.lineTo(x2, getHeight());
                path.lineTo(x1, getHeight());
                path.lineTo(x1, 0);
                break;
            case TOP:
            case BOTTOM:
                path.moveTo(0, y1);
                path.lineTo(0, y2);
                path.lineTo(getWidth(), y2);
                path.lineTo(getWidth(), y1);
                path.lineTo(0, y1);
                break;
        }

    }

    /**
     * @return true if the line is on display
     */
    protected boolean isOnDisplay(float x, float y) {
        float y1 = y - maxStroke / 2;
        float y2 = y + maxStroke / 2;
        float x1 = x - maxStroke / 2;
        float x2 = x + maxStroke / 2;
        int size = Math.max(getWidth(), getHeight()) * 2;
        return (!(x2 <= 0) && !(x1 >= size))
                || (!(y2 <= 0) && !(y1 >= size));
    }

    /**
     * calculate the origin line's startY base on startX
     */
    protected float findStartY() {
        //return startX * getHeight() / getWidth();
        return startX;
    }

    /**
     * @return the distance between two lines on the x-axis
     */
    protected float findDistanceX() {
        //return (float) (getWidth() / displayCount);
        return findDistance();
    }

    /**
     * @return the distance between two lines on the y-axis
     */
    protected float findDistanceY() {
        //return (float) (getHeight() / displayCount);
        return findDistance();
    }

    /**
     * @return the distance between two lines on the axis
     */
    protected float findDistance() {
        return (float) (Math.min(getWidth(), getHeight()) / maxLines);
    }

    /**
     * Specifies the start of movement from the {@link Direction#LEFT} or {@link Direction#RIGHT}
     *
     * @return {@link Direction#getMovementDirection()}
     */
    protected Direction findAnimatorDirection() {
        /*switch (direction) {
            case TOP_RIGHT:
            case RIGHT:
            case BOTTOM_RIGHT:
            case BOTTOM:
                return Direction.RIGHT;
            default:
                return Direction.LEFT;
        }*/
        return direction.getMovementDirection();
    }

    public void start() {
        if (valueAnimator != null && valueAnimator.isRunning())
            return;
        shouldRun = true;
        animationStart = startX;

        final float value = (findAnimatorDirection() == Direction.RIGHT ? -1 : 1) * findDistanceX();
        valueAnimator = ValueAnimator.ofFloat(0, value);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float cv = (float) valueAnimator.getAnimatedValue();
                float newStart = animationStart + cv;

                // Avoiding possible animator delays
                if (Math.abs(startX) > Math.abs(newStart))
                    newStart += value;
                if (Math.abs(startX) < Math.abs(newStart))
                    startX = newStart;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                animationStart += value;
            }
        });
        valueAnimator.start();
    }

    public void stop() {
        shouldRun = false;
        if (valueAnimator != null)
            valueAnimator.cancel();
        valueAnimator = null;
    }

    /**
     * pause when view detached from window
     */
    void pause() {
        shouldRun = true;
        if (valueAnimator != null)
            valueAnimator.cancel();
    }

    public boolean isRunning() {
        return valueAnimator != null && valueAnimator.isRunning();
    }

    /**
     * sets the photo behind the lines
     */
    public void setParallelLineImage(@NonNull Bitmap bitmap) {
        this.parallelLineBitmap = bitmap;
        initBitmaps();
        invalidate();
    }

    /**
     * sets the photo behind the lines
     *
     * @see #setParallelLineImage(Bitmap)
     */
    public void setParallelLineImage(@DrawableRes int res) {
        setParallelLineImage(BitmapFactory.decodeResource(getResources(), res));
    }

    /**
     * sets the photo behind the lines with grayscale effect
     *
     * @see #setParallelLineImage(Bitmap)
     */
    public void setGrayscaleParallelLineImage(@NonNull Bitmap bitmap) {
        setParallelLineImage(toGrayscale(bitmap));
    }

    /**
     * sets the photo behind the lines with grayscale effect
     *
     * @see #setGrayscaleParallelLineImage(Bitmap)
     */
    public void setGrayscaleParallelLineImage(@DrawableRes int res) {
        setGrayscaleParallelLineImage(BitmapFactory.decodeResource(getResources(), res));
    }

    @Nullable
    public Bitmap getParallelLineImage() {
        return parallelLineBitmap;
    }

    /**
     * sets direction of movement
     */
    public void setDirection(@NonNull Direction direction) {
        this.direction = direction;
        invalidate();
    }

    /**
     * @return direction of movement
     */
    public @NonNull Direction getDirection() {
        return direction;
    }

    /**
     * sets max stroke
     */
    public void setMaxStroke(float maxStroke) {
        if (maxStroke < 0)
            throw new RuntimeException("Stroke must be a positive value");
        this.maxStroke = maxStroke;
        invalidate();
    }

    /**
     * @return max stroke
     */
    public float getMaxStroke() {
        return maxStroke;
    }

    /**
     * sets min stroke
     */
    public void setMinStroke(float minStroke) {
        if (minStroke < 0)
            throw new RuntimeException("Stroke must be a positive value");
        this.minStroke = minStroke;
        invalidate();
    }

    /**
     * @return min stroke
     */
    public float getMinStroke() {
        return minStroke;
    }

    /**
     * sets maximum number of lines displayed on the view
     */
    public void setMaxLinesCount(int maxLinesCount) {
        if (maxLinesCount < 1)
            throw new RuntimeException("MaxLinesCount must be >= 1");
        this.maxLines = maxLinesCount;
        invalidate();
    }

    public int getMaxLinesCount() {
        return maxLines;
    }

    /**
     * sets period of division stroke between lines
     */
    public void setStrokePeriod(int strokePeriod) {
        if (strokePeriod <= 0)
            throw new RuntimeException("StrokePeriod must be > 0");
        this.strokePeriod = strokePeriod;
        invalidate();
    }

    public int getStrokePeriod() {
        return strokePeriod;
    }

    /**
     * sets the duration of moving a line
     */
    public void setDuration(long duration) {
        if (duration <= 0)
            throw new RuntimeException("Duration must be > 0");
        this.duration = duration;
        if (valueAnimator != null)
            valueAnimator.setDuration(duration);
    }

    public void setStartPosition(float start) {
        if (isRunning())
            throw new RuntimeException("Can't change StartPosition while animator is running!");
        startX = start;
    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private static Bitmap crop(Bitmap original, int w, int h) {
        return Bitmap.createScaledBitmap(original, w, h, false);
    }
}
