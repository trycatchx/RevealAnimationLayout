package com.revealanimation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

public class RevealAnimationLayout extends FrameLayout {

    public Path mClipPath;                 // 剪裁区域路径
    public Path mOpClipPath;
    public Paint mPaint;                   // 画笔
    public Region mAreaRegion;             // 内容区域
    public RectF mLayer;                   // 画布图层大小

    public ValueAnimator.AnimatorUpdateListener mUpdateListener;
    public Animator.AnimatorListener mAnimatorListener;
    public ValueAnimator mStartingAnimator;
    public float mAnimatorValue;
    private int defaultDuration = 400;


    public enum AnimaType {
        Circle, LeftRight, UpDown,BackCircle,BackLeftRight,BackUpDown
    }

    public AnimaType mAnimaType = AnimaType.UpDown;


    public RevealAnimationLayout(@NonNull Context context) {
        this(context, null);
    }

    public RevealAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealAnimationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr();
    }

    private void initAttr() {
        mLayer = new RectF();
        mClipPath = new Path();
        mOpClipPath = new Path();
        mAreaRegion = new Region();
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        setWillNotDraw(false);

        initAnimator();

        setLayerType(LAYER_TYPE_HARDWARE, mPaint);
    }

    /**
     * 初始化动画类
     */

    private void initAnimator() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                //拿到动画的执行的百分比mAnimatorValue
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mStartingAnimator = new ValueAnimator().setDuration(defaultDuration);
        mStartingAnimator.setInterpolator(new AccelerateInterpolator());
        mStartingAnimator.addUpdateListener(mUpdateListener);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        mRCHelper.onSizeChanged(this, w, h);
        mLayer.set(0, 0, w, h);
        refreshRegion(this);
    }

    public void refreshRegion(View view) {
        int w = (int) mLayer.width();
        int h = (int) mLayer.height();
        RectF areas = new RectF();
        areas.left = view.getPaddingLeft();
        areas.top = view.getPaddingTop();
        areas.right = w - view.getPaddingRight();
        areas.bottom = h - view.getPaddingBottom();
        mClipPath.reset();

        PointF center = new PointF(w / 2, h / 2);
        if (mAnimaType == AnimaType.Circle || mAnimaType == AnimaType.BackCircle) {
            float d = (float) Math.hypot(areas.width(), areas.height());
            float r = d / 2 * mAnimatorValue;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                mClipPath.addCircle(center.x, center.y, r, Path.Direction.CW);
                mClipPath.moveTo(0, 0);  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w, h);
            } else {
                float y = h / 2 - r;
                mClipPath.moveTo(areas.left, y);
                mClipPath.addCircle(center.x, center.y, r, Path.Direction.CW);
            }
        } else if (mAnimaType == AnimaType.UpDown || mAnimaType == AnimaType.BackUpDown) {
            float top = areas.height() / 2 * (1 - mAnimatorValue);
            float bottom = areas.height() / 2 * (1 + mAnimatorValue);

            RectF mRectF = new RectF(areas.left, top, areas.right, bottom);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                mClipPath.addRect(mRectF, Path.Direction.CW);
                mClipPath.moveTo(0, 0);  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w, h);
            } else {
                mClipPath.moveTo(areas.left, top);
                mClipPath.addRect(mRectF, Path.Direction.CW);
            }
        } else if (mAnimaType == AnimaType.LeftRight || mAnimaType == AnimaType.BackLeftRight) {
            float left = areas.width() / 2 * (1 - mAnimatorValue);
            float right = areas.width() / 2 * (1 + mAnimatorValue);

            RectF mRectF = new RectF(left, areas.top, right, areas.bottom);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                mClipPath.addRect(mRectF, Path.Direction.CW);
                mClipPath.moveTo(0, 0);  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w, h);
            } else {
                mClipPath.moveTo(left, areas.top);
                mClipPath.addRect(mRectF, Path.Direction.CW);
            }
        }
    }


    /**
     * 如果没有设置背景是不会调用这个的
     *
     * @param canvas
     */

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
//        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        onClipDraw(canvas);
        canvas.restore();
    }


    public void onClipDraw(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mOpClipPath.reset();
        mOpClipPath.addRect(0, 0, mLayer.width(), mLayer.height(), Path.Direction.CW);

        mOpClipPath.op(mClipPath, Path.Op.DIFFERENCE);
        canvas.drawPath(mOpClipPath, mPaint);

    }


    @Override
    public void invalidate() {
        refreshRegion(this);
        super.invalidate();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mStartingAnimator != null) {
            mStartingAnimator.cancel();
            mStartingAnimator.removeAllUpdateListeners();
            mStartingAnimator.removeAllListeners();
        }
    }

    /**
     * 开启动画
     * @param animaType 动画类型
     */

    public void startAnimal(AnimaType animaType) {
        this.mAnimaType = animaType;
        setVisibility(View.VISIBLE);
        mStartingAnimator.cancel();
        if(mAnimaType == AnimaType.BackCircle ||
                mAnimaType == AnimaType.BackLeftRight ||
                mAnimaType == AnimaType.BackUpDown ) {
            mStartingAnimator.setFloatValues(1,0);
        } else {
            mStartingAnimator.setFloatValues(0,1);
        }
        mStartingAnimator.start();
    }


}
