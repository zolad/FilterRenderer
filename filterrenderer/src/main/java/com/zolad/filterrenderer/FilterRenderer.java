package com.zolad.filterrenderer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.zolad.filterrenderer.filter.InputGLFilter;
import com.zolad.filterrenderer.filter.base.GLFilter;
import com.zolad.filterrenderer.util.GLConstant;
import com.zolad.filterrenderer.util.LimitHashMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

public class FilterRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = FilterRenderer.class.getSimpleName();
    private LimitHashMap<String, Integer> mProgramCacheMap;
    private SurfaceListener mSurfaceListener;
    private SurfaceTexture mSurfaceTexture;


    private FloatBuffer mTriangleVertices;
    protected int surfaceWidth, surfaceHeight;

    private float[] mSTMatrix = new float[16];


    private int mTextureID;
    private boolean updateSurface = false;
    private int mCurTextureID;
    private boolean isSurfaceCreate = false;

    private InputGLFilter mInputGLFilter;
    private GLFilter mFilter;
    private GLSurfaceView mAttachGlSurfaceView;

    public FilterRenderer(GLSurfaceView glSurfaceView) {

        mProgramCacheMap = new LimitHashMap<String, Integer>();
        mAttachGlSurfaceView = glSurfaceView;
        init();

    }

    public FilterRenderer(GLSurfaceView glSurfaceView, int programCacheSize) {

        mProgramCacheMap = new LimitHashMap<String, Integer>(programCacheSize);
        mAttachGlSurfaceView = glSurfaceView;
        init();
    }


    public void init() {
        mTriangleVertices = ByteBuffer.allocateDirect(GLConstant.mTriangleVerticesData.length * GLConstant.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(GLConstant.mTriangleVerticesData).position(0);
        Matrix.setIdentityM(mSTMatrix, 0);
        mInputGLFilter = new InputGLFilter();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        mInputGLFilter.create();


        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        if (mSurfaceListener != null) {
            mSurfaceListener.onSurfaceTextureAvailable(mSurfaceTexture);
        }
        isSurfaceCreate = true;
        onFilterChanged(true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        surfaceWidth = width;
        surfaceHeight = height;
        onFilterChanged(true);
        if (mSurfaceListener != null) {

            mSurfaceListener.onSurfaceTextureSizeChanged(mSurfaceTexture, surfaceWidth, surfaceHeight);
        }


    }

    @Override
    public void onDrawFrame(GL10 gl) {

        synchronized (this) {
            if (updateSurface) {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        mInputGLFilter.setTextureTransformMatrix(mSTMatrix);

        if (mFilter == null) {

            mInputGLFilter.onDrawFrame(mTextureID, mTriangleVertices);

        } else {


            if (!mFilter.isInitialized()) {
                if (mProgramCacheMap != null)
                    mFilter.create(mProgramCacheMap);

                mFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
                mFilter.onInputSizeChanged(surfaceWidth, surfaceHeight);

            }

            int[] cacheframebuffer = mInputGLFilter.getFrameBuffers();

            if (cacheframebuffer == null || cacheframebuffer[0] == 0) {
                if (isSurfaceCreate && surfaceWidth != 0 && surfaceHeight != 0)
                    mInputGLFilter.initFrameBuffer(surfaceWidth, surfaceHeight);
            }

            mCurTextureID = mInputGLFilter.onDrawToTexture(mTextureID, mTriangleVertices);
            mFilter.onDrawFrame(mCurTextureID, mTriangleVertices);
        }


    }


    public void setFilter(final GLFilter filter) {

        GLFilter oldFilter = mFilter != null ? mFilter : null;
        mFilter = filter;

        if (oldFilter != null)
            oldFilter.destroy();



       if(isSurfaceCreate) {
           mAttachGlSurfaceView.queueEvent(new Runnable() {
               @Override
               public void run() {

                   onFilterChanged(false);

                   if (isSurfaceCreate && mFilter!=null &&!mFilter.isInitialized()) {
                       if (mProgramCacheMap != null)
                           mFilter.create(mProgramCacheMap);
                   }

                   if (mAttachGlSurfaceView != null  && mFilter!=null && isSurfaceCreate)
                       mAttachGlSurfaceView.requestRender();
               }
           });
       }


    }


    protected void onFilterChanged(boolean isFromSurfaceChange) {


        if (mInputGLFilter != null)
            mInputGLFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);

        if (mFilter != null) {
            mFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
            mFilter.onInputSizeChanged(surfaceWidth, surfaceHeight);


            if (isSurfaceCreate && surfaceWidth != 0 && surfaceHeight != 0)
                mInputGLFilter.initFrameBuffer(surfaceWidth, surfaceHeight);

        } else {
            if (isSurfaceCreate && surfaceWidth != 0 && surfaceHeight != 0)
                mInputGLFilter.destroyFramebuffers();
        }


    }


    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
        if (mAttachGlSurfaceView != null)
            mAttachGlSurfaceView.requestRender();
    }

    public void setSurfaceListener(SurfaceListener surfaceListener) {

        mSurfaceListener = surfaceListener;

    }

    public interface SurfaceListener {

        void onSurfaceTextureAvailable(SurfaceTexture surface);

        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

    }

    public void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
