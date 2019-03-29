### Android UI 篇- 实现一个揭露动画

[一、应用场景](#1)

[二、流程分析](#2)

[三、代码实现](#3)

###一、应用场景
##### 1、 先上效果图：
![效果图](https://upload-images.jianshu.io/upload_images/2788235-6f59dfb29fc8aeb3.gif?imageMogr2/auto-orient/strip)

##### 2、 应用场景分析：
- 适用于 ```Activity``` 界面跳转
- 适用于 ```View``` 的切换
- 支持所有 ```View``` 布局的动画效果


##### 3、代码使用（非常简洁好用）：
- 在你需要做动画的布局上，套上```RevealAnimationLayout``` 就可以了，**支持套任何布局！！！**


```
    <com.clipanimation.RevealAnimationLayout
        android:id="@+id/animat_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        >
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="400dp"
            android:layout_marginLeft="50dp"
            android:layout_marginRight="50dp"
            android:scaleType="centerCrop"
            android:src="@mipmap/test"/>
    </com.clipanimation.RevealAnimationLayout>
```


###二、流程分析

##### 1、```android 5.0``` 其实已经拥有对应的接口 ```ViewAnimationUtils.createCircularReveal``` ,然而还是有一定的局限性：
- 只能再 ```android 5.0``` 上使用
- 只提供圆形的揭露效果
- 也没有提供拓展其他图形接口

##### 2、对于这个效果，我们只能自定义 View，来实现。思路步骤如下：
- 做一个空父布局，提供出去，可以套任何布局
- 在空的布局对套进来的子布局操作。
- 要在 ```draw``` 这个函数下手，并且要在 ```super.draw()``` 之后去做剪裁，目的是确保子布局先 ```draw``` 完。
- 使用画笔 ```setXfermode``` 去实现这个动画效果。先用 ```Path``` 画出圆形/矩形，重叠在画布上面，取出重叠的 ```View``` 。

##### 3、我们复写```draw```函数，对```draw```函数进行重写。回顾一下```draw``` 函数的流程：


![绘制流程](https://upload-images.jianshu.io/upload_images/2788235-bf94e6c126b12392.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
###### 可以重写的只有：
- ```draw```
- ```onDraw``` （只能绘制自己，绘制不了内部子布局）
- ```dispatchDraw```

###### 综上：满足条件的只有 ```draw``` 和 ```dispatchDraw```重写这两个都可以实现，譬如下面模拟代码：

```
@Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas); //自身绘制在canvas上，子布局绘制在canvas
        onClipDraw(canvas); //上面代码绘制完毕，再对整个canvas进行操作
        canvas.restore();
    }
```


```
@Override
    protected void dispatchDraw(Canvas canvas) {
     canvas.saveLayer(mLayer, null,Canvas.ALL_SAVE_FLAG);
     /**
     *子布局绘制在canvas，自身还没有绘制完毕，还要跑绘制
     *drawAutofilledHighlight-onDrawForeground-drawDefau*ltFocusHighlight。不过没关系我们只要对子布局操作
     */
        super.dispatchDraw(canvas);
        onClipDraw(canvas); //子布局绘制完毕，再对canvas进行操作
        canvas.restore()
    }
```

###三、代码实现

##### 1、首先开启一个动画器，拿到动画执行的 百分比值 ：```mAnimatorValue ```


```
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


```

##### 2、我们再来生成个剪裁路径 ```mClipPath```（```Path```类），需要通过百分比 ```mAnimatorValue``` 计算出 ```mClipPath``` 需要添加半径为多大的圆 ：


```
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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
            //通过动画的百分比mAnimatorValue，计算出圆的半径
            float r = d / 2 * mAnimatorValue;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            //这里添加了一个圆
                mClipPath.addCircle(center.x, center.y, r, Path.Direction.CW);
                mClipPath.moveTo(0, 0);  // 通过空操作让Path区域占满画布
                mClipPath.moveTo(w, h);
            } else {
                float y = h / 2 - r;
                mClipPath.moveTo(areas.left, y);
                mClipPath.addCircle(center.x, center.y, r, Path.Direction.CW);
            }
        }
    }
```

##### 3、最后这里我们对 ```Draw``` 下手，把生成好的 ```mClipPath```，画到 ```canvas``` （整个已经绘制到的布局），使用 ```PorterDuff.Mode.DST_OUT``` 拿出 和圆重叠的部分。（其中使用了一个小技巧兼容 ```android 9.0``` 详细看下面代码）

```
@Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
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
        //取反，因为 android 9.0 不对 paint 以外（paint 自身有指定区域，这里在画笔上添加的区域是一个圆）的布局绘制
        mOpClipPath.op(mClipPath, Path.Op.DIFFERENCE);
        canvas.drawPath(mOpClipPath, mPaint);

    }
```

>代码很简单，就一个布局。可以用来实现 ```Activity``` 的跳转，界面部分 ```View``` 的切换。也极易拓展，建议大家下载下来看看。




最后鸣谢：http://www.gcssloop.com/gebug/rclayout 给了我灵感。



