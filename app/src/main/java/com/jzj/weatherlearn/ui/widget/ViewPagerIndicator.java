package com.jzj.weatherlearn.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jzj.weatherlearn.R;

/**
 * 圆点指示器
 * 提示viewpager当前页
 */
public class ViewPagerIndicator extends View {

    //圆点个数
    private static final int DEFAULT_TOTAL_INDEX = 5;
    //当前下标
    private static final int DEFAULT_CURRENT_INDEX = 0;
    //邻点距离
    private static final int DEFAULT_CIRCLE_DISTANCE = 40;
    //半径
    private static final int DEFAULT_CIRCLE_RADIUS = 8;
    //选中圆的半径
    private static final int DEFAULT_CIRCLE_SELECTED_RADIUS = 11;

    private int selectedColor;
    private int unselectedColor;
    private Paint paint;
    private int startX;
    private int startY;
    private int startSelectedY;
    private int endY;
    private int radius;
    private int selectRadius;
    private int distance;
    private int totalIndex;
    private int currentIndex;
    private int centreX;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Indicator, defStyleAttr, 0);
        selectedColor = typedArray.getColor(R.styleable.Indicator_selectedColor, Color.GRAY);
        unselectedColor = typedArray.getColor(R.styleable.Indicator_unselectedColor, Color.LTGRAY);
        paint = new Paint();
        radius = DEFAULT_CIRCLE_RADIUS;
        selectRadius = DEFAULT_CIRCLE_SELECTED_RADIUS;
        distance = DEFAULT_CIRCLE_DISTANCE;
        totalIndex = DEFAULT_TOTAL_INDEX;
        currentIndex = DEFAULT_CURRENT_INDEX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centreX = getWidth() / 2;
        startY = (getHeight() / 2) - radius;
        //startSelectedY = getHeight() / 2 - DEFAULT_CIRCLE_SELECTED_RADIUS;
        startSelectedY = getHeight() / 2 - selectRadius;
        endY = startY + (2 * radius);
        if (totalIndex % 2 == 0) {
            //startX = centreX - (int) (((totalIndex * distance) / 2) - distance + radius + 0.5);
            //startX = centreX - (int) (1.0 * (totalIndex - 1) / 2 * DEFAULT_CIRCLE_DISTANCE);
            startX = centreX - (int) (1.0 * (totalIndex - 1) / 2 * distance);
        } else {
            //startX = centreX - (int) (totalIndex / 2) * distance + radius;
            //startX = centreX - totalIndex / 2 * DEFAULT_CIRCLE_DISTANCE;
            startX = centreX - totalIndex / 2 * distance;
        }
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(unselectedColor);
        int tempX = startX;
        for (int i = 0; i < totalIndex; i++) {
//            RectF rectF = new RectF(tempX, tempX + (2 * radius), startY, endY);
//            RectF rectF = new RectF(tempX - DEFAULT_CIRCLE_RADIUS, startY,
//                    tempX + DEFAULT_CIRCLE_RADIUS, startY + 2 * DEFAULT_CIRCLE_RADIUS);
            RectF rectF = new RectF(tempX - radius, startY,
                    tempX + radius, startY + 2 * radius);
            //选中圆点
            if (i == currentIndex) {
                paint.setColor(selectedColor);
//                rectF = new RectF(tempX, tempX + (2 * selectRadius), startY, startY + (2 * selectRadius));
//                rectF = new RectF(tempX - DEFAULT_CIRCLE_SELECTED_RADIUS, startSelectedY,
//                        tempX + DEFAULT_CIRCLE_SELECTED_RADIUS, startSelectedY + 2 * DEFAULT_CIRCLE_SELECTED_RADIUS);
                rectF = new RectF(tempX - selectRadius, startSelectedY,
                        tempX + selectRadius, startSelectedY + 2 * selectRadius);
            }
            canvas.drawOval(rectF, paint);
            //样式还原
            if (i == currentIndex) {
                paint.setColor(unselectedColor);
            }
            tempX += distance;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //int desired = selectRadius * 2 + getPaddingBottom() + getPaddingTop();
        //int desired = DEFAULT_CIRCLE_SELECTED_RADIUS * 2 + getPaddingBottom() + getPaddingTop();
        int desired = selectRadius * 2 + getPaddingBottom() + getPaddingTop();
        if (specMode == MeasureSpec.EXACTLY) {
            result = Math.max(desired, specSize);
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(desired, specSize);
            } else result = desired;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //int desired = (totalIndex - 1) * distance + selectRadius * 2 + getPaddingLeft() + getPaddingRight();
        //int desired = (totalIndex - 1) * DEFAULT_CIRCLE_DISTANCE + DEFAULT_CIRCLE_SELECTED_RADIUS * 2 + getPaddingLeft() + getPaddingRight();
        int desired = (totalIndex - 1) * distance + selectRadius * 2 + getPaddingLeft() + getPaddingRight();
        if (specMode == MeasureSpec.EXACTLY) {
            result = Math.max(desired, specSize);
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(desired, specSize);
            } else result = desired;
        }
        return result;
    }


    public void setCurrentIndex(int index) {
        if (index < 0)
            return;
        currentIndex = index;
        invalidate();
    }

    public void setTotalIndex(int total) {
        int oldTotalIndex = totalIndex;
        if (total < 1)
            return;
//        if (total < oldTotalIndex) {
//            if (currentIndex > total)
//                currentIndex = total;
//        }
        if (totalIndex < oldTotalIndex) {
            if (currentIndex == totalIndex)
                currentIndex = totalIndex - 1;
        }
        totalIndex = total;
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public void setSelectRadius(int selectRadius) {
        this.selectRadius = selectRadius;
        invalidate();
    }

    public void setDistance(int distance) {
        this.distance = distance;
        invalidate();
    }

}
