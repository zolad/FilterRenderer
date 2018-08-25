package com.zolad.filterrenderer.filter.base;

import android.opengl.GLES20;

import com.zolad.filterrenderer.util.GLConstant;
import com.zolad.filterrenderer.util.LimitHashMap;
import com.zolad.filterrenderer.util.ShaderHelper;

import java.nio.FloatBuffer;

public class GLFilter {

    private String TAG = "GLFilter";


    public static final String VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    protected final String mVertexShader;
    protected final String mFragmentShader;
    protected boolean mIsInitialized = false;
    protected int mProgramId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mIntputWidth;
    protected int mIntputHeight;
    protected int mOutputWidth, mOutputHeight;

    public GLFilter() {
        this(VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GLFilter(final String vertexShader, final String fragmentShader) {

        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;

    }

    public void create() {

        mProgramId = ShaderHelper.buildProgram(mVertexShader, mFragmentShader);
        onCreate();

    }


    protected void onCreate() {
        mGLAttribPosition = GLES20.glGetAttribLocation(mProgramId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");

        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgramId,
                "inputTextureCoordinate");
        mIsInitialized = true;
    }


    public void create(LimitHashMap<String, Integer> programCacheMap) {


        Integer checkCache = programCacheMap.get(this.getClass().getName());

        if (checkCache != null && checkCache != 0) {

            mProgramId = checkCache;

        } else {
            mProgramId = ShaderHelper.buildProgram(mVertexShader, mFragmentShader);
            programCacheMap.put(this.getClass().getName(), mProgramId);
        }

        onCreate();


    }

    public void onInputSizeChanged(final int width, final int height) {
        mIntputWidth = width;
        mIntputHeight = height;
    }

    public void onDisplaySizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public int onDrawFrame(final int textureId, final FloatBuffer vertexBuffer) {

        GLES20.glUseProgram(mProgramId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (!mIsInitialized) {
            return GLConstant.NOT_INIT;
        }
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 3, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mGLAttribPosition);


        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);


        if (textureId != GLConstant.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        onDrawArraysAfter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return GLConstant.ON_DRAWN;
    }


    public boolean isInitialized() {
        return mIsInitialized;
    }


    public void destroy() {

        onDestroy();

    }

    protected void onDestroy() {

    }


    protected void onDrawArraysPre() {
    }

    protected void onDrawArraysAfter() {
    }

    protected void setInteger(final int location, final int intValue) {

        GLES20.glUniform1i(location, intValue);

    }

    protected void setFloat(final int location, final float floatValue) {

        GLES20.glUniform1f(location, floatValue);

    }

    public void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
