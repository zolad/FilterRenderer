package com.zolad.filterrenderer.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.zolad.filterrenderer.R;
import com.zolad.filterrenderer.filter.base.GLFilter;
import com.zolad.filterrenderer.util.TextResourceReader;

import java.lang.ref.WeakReference;

public class BCSGLFilter extends GLFilter {


    private WeakReference<Context> mContext;

    private int mBrightnessLocation;
    private int mContrastLocation;
    private int mSaturationLocation;
    private float mBrightness;
    private float mContrast;
    private float mSaturation;

    public BCSGLFilter(Context context, float brightness, float contrast, float saturation) {
        super(VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.bcs));

        this.mContext = new WeakReference<>(context);
        mBrightness = brightness;
        mContrast = contrast;
        mSaturation = saturation;
    }


    public void setBCS(float brightness, float contrast, float saturation) {
        mBrightness = brightness;
        mContrast = contrast;
        mSaturation = saturation;
    }

    @Override
    protected void onCreate() {
        super.onCreate();


        mBrightnessLocation = GLES20.glGetUniformLocation(mProgramId, "brightness");
        mContrastLocation = GLES20.glGetUniformLocation(mProgramId, "contrast");
        mSaturationLocation = GLES20.glGetUniformLocation(mProgramId, "saturation");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    protected void onDrawArraysAfter() {

    }

    protected void onDrawArraysPre() {

        GLES20.glUniform1f(mBrightnessLocation, mBrightness);
        GLES20.glUniform1f(mContrastLocation, mContrast);
        GLES20.glUniform1f(mSaturationLocation, mSaturation);

    }
}