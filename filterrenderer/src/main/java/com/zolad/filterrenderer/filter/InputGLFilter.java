package com.zolad.filterrenderer.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.zolad.filterrenderer.filter.base.GLFilter;
import com.zolad.filterrenderer.util.GLConstant;
import com.zolad.filterrenderer.util.ShaderHelper;

import java.nio.FloatBuffer;

public class InputGLFilter extends GLFilter{

    public static final String VERTEX_SHADER =
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 position;\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "void main() {\n" +
                    "  gl_Position = position;\n" +
                    "  textureCoordinate = (uSTMatrix * inputTextureCoordinate).xy;\n" +
                    "}\n";

    public static final String NO_EFFECT_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES inputImageTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "}\n";

    private int mTextureTransformMatrixLocation;
    private float[] mTextureTransformMatrix;

    public int[] getFrameBuffers() {
        return mFrameBuffers;
    }

    public int[] getFrameBufferTextures() {
        return mFrameBufferTextures;
    }

    private int[] mFrameBuffers = null;
    private int[] mFrameBufferTextures = null;
    private int mFrameWidth = -1;
    private int mFrameHeight = -1;

    public InputGLFilter(){
        super(VERTEX_SHADER,NO_EFFECT_FRAGMENT_SHADER);
    }


    public void create() {

        mProgramId = ShaderHelper.buildProgram(mVertexShader, mFragmentShader);
        onCreate();

    }


    public void onCreate() {
        mGLAttribPosition = GLES20.glGetAttribLocation(mProgramId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgramId,
                "inputTextureCoordinate");
        mIsInitialized = true;
    }



    @Override
    public int onDrawFrame(int textureId, FloatBuffer vertexBuffer) {
        GLES20.glUseProgram(mProgramId);

        if (!isInitialized()) {
            return GLConstant.NOT_INIT;
        }

        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 3, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);


        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0);

        if (textureId != GLConstant.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return GLConstant.ON_DRAWN;
    }

    public int onDrawToTexture(final int textureId,FloatBuffer vertexBuffer) {
        if (mFrameBuffers == null)
            return GLConstant.NO_TEXTURE;

        GLES20.glViewport(0, 0, mFrameWidth, mFrameHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glUseProgram(mProgramId);
        if (!isInitialized()) {
            return GLConstant.NOT_INIT;
        }
        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 3, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);


        vertexBuffer.position(GLConstant.TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false,
                GLConstant.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0);

        if (textureId != GLConstant.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        return mFrameBufferTextures[0];
    }


    public synchronized void initFrameBuffer(int width, int height) {
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height))
            destroyFramebuffers();
        if (mFrameBuffers == null) {
            mFrameWidth = width;
            mFrameHeight = height;
            mFrameBuffers = new int[1];
            mFrameBufferTextures = new int[1];

            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
            GLES20.glGenTextures(1, mFrameBufferTextures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
    }

    public synchronized void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        mFrameWidth = -1;
        mFrameHeight = -1;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void destroy() {
        super.destroy();
        mIsInitialized = false;
        GLES20.glDeleteProgram(mProgramId);
        onDestroy();
    }

    public void setTextureTransformMatrix(float[] mtx) {
        mTextureTransformMatrix = mtx;
    }

}
