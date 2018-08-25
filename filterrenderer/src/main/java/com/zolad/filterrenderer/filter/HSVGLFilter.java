package com.zolad.filterrenderer.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.zolad.filterrenderer.R;
import com.zolad.filterrenderer.filter.base.GLFilter;
import com.zolad.filterrenderer.util.TextResourceReader;

import java.lang.ref.WeakReference;

public class HSVGLFilter extends GLFilter {


    private int mHueLocation;
    private int mValueLocation;
    private int mSaturationLocation;
    private float mHue = 1.0f;
    private float mValue = 1.0f;
    private float mSaturation = 1.0f;

    public HSVGLFilter(Context context, float hue, float saturation, float value) {
        super(VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.hsv));


        mHue = hue;
        mValue = value;
        mSaturation = saturation;
    }


    public void setHSV(float hue, float saturation, float value) {
        mHue = hue;
        mValue = value;
        mSaturation = saturation;
    }

    @Override
    protected void onCreate() {
        super.onCreate();


        mHueLocation = GLES20.glGetUniformLocation(mProgramId, "hue");
        mValueLocation = GLES20.glGetUniformLocation(mProgramId, "value");
        mSaturationLocation = GLES20.glGetUniformLocation(mProgramId, "saturation");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    protected void onDrawArraysAfter() {

    }

    protected void onDrawArraysPre() {

        GLES20.glUniform1f(mHueLocation, mHue);
        GLES20.glUniform1f(mValueLocation, mValue);
        GLES20.glUniform1f(mSaturationLocation, mSaturation);

    }
}
