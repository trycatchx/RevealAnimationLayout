

# RevealAnimationLayout
[![Build Status](https://api.travis-ci.org/tbruyelle/RxPermissions.svg?branch=master)](https://travis-ci.org/tbruyelle/RxPermissions)

本动画揭露库，已经兼容 android9.0 版本，提供 6 种类的揭露动画，如需更多效果，可自行拓展。


# Design sketch
![效果图](https://upload-images.jianshu.io/upload_images/2788235-6f59dfb29fc8aeb3.gif?imageMogr2/auto-orient/strip)



# Usage：
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

 // java 代码中，调用
mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.Circle);
```
# 博客地址
博客：https://www.jianshu.com/p/ab8a145e9b4a

最后鸣谢：gcssloop 作者给了我灵感。



