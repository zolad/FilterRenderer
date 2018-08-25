package com.zolad.filterrenderer.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.zolad.filterrenderer.R;
import com.zolad.filterrenderer.filter.base.GLFilter;
import com.zolad.filterrenderer.util.GLConstant;
import com.zolad.filterrenderer.util.ShaderHelper;
import com.zolad.filterrenderer.util.TextResourceReader;

import java.lang.ref.WeakReference;

public class BlendGLFilter extends GLFilter {


    protected int blendResId;
    private WeakReference<Context> mContext;


    public int mBlendTextureUniform;
    public int mBlendSourceTexture = GLConstant.NO_TEXTURE;
    private int mStrengthLocation;
    private float mStrength;


    public BlendGLFilter(Context context, int resId) {
        this(context, resId, 0.4f);
    }

    public BlendGLFilter(Context context, int resId, final float strength) {
        super(VERTEX_SHADER, TextResourceReader.readTextFileFromResource(context, R.raw.blend));
        this.blendResId = resId;
        this.mContext = new WeakReference<>(context);
        mStrength = strength;
    }

    public void setStrength(float mStrength) {
        this.mStrength = mStrength;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBlendTextureUniform = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture2");
        mStrengthLocation = GLES20.glGetUniformLocation(mProgramId, "strength");

        if (mContext.get() != null) {
            mBlendSourceTexture = ShaderHelper.loadTexture(mContext.get(), blendResId);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        int[] texture = new int[]{mBlendSourceTexture};
        GLES20.glDeleteTextures(1, texture, 0);
        mBlendSourceTexture = -1;
    }

    protected void onDrawArraysAfter() {
        if (mBlendSourceTexture != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    protected void onDrawArraysPre() {

        GLES20.glUniform1f(mStrengthLocation, mStrength);

        if (mBlendSourceTexture != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlendSourceTexture);
            GLES20.glUniform1i(mBlendTextureUniform, 3);
        }
    }


}
