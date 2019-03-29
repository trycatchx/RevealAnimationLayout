package com.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.revealanimation.RevealAnimationLayout;

import app.tcl.com.animation.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button btn_circle, btn_updown, btn_leftright;
    Button btn_back_circle, btn_back_updown, btn_back_leftright;
    RevealAnimationLayout mClipAnimationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_circle = findViewById(R.id.btn_circle);
        btn_updown = findViewById(R.id.btn_updown);
        btn_leftright = findViewById(R.id.btn_leftright);

        btn_back_circle = findViewById(R.id.btn_back_circle);
        btn_back_updown = findViewById(R.id.btn_back_updown);
        btn_back_leftright = findViewById(R.id.btn_back_leftright);
        mClipAnimationLayout = findViewById(R.id.animat_layout);
        btn_updown.setOnClickListener(this);
        btn_circle.setOnClickListener(this);
        btn_leftright.setOnClickListener(this);
        btn_back_circle.setOnClickListener(this);
        btn_back_updown.setOnClickListener(this);
        btn_back_leftright.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_circle:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.Circle);
                break;
            case R.id.btn_updown:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.UpDown);
                break;
            case R.id.btn_leftright:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.LeftRight);
                break;
            case R.id.btn_back_circle:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.BackCircle);
                break;
            case R.id.btn_back_updown:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.BackUpDown);
                break;
            case R.id.btn_back_leftright:
                mClipAnimationLayout.startAnimal(RevealAnimationLayout.AnimaType.BackLeftRight);
                break;

        }
    }
}
