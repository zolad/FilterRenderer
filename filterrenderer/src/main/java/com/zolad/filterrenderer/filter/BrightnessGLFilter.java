package com.zolad.filterrenderer.filter;

import android.opengl.GLES20;

import com.zolad.filterrenderer.filter.base.GLFilter;
/**
 * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
 */
public class BrightnessGLFilter extends GLFilter{

    public static final String BRIGHTNESS_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
            " }";

    private int mBrightnessLocation;
    private float mBrightness;



    public BrightnessGLFilter() {
        this(0f);
    }

    public BrightnessGLFilter(final float brightness) {
        super(VERTEX_SHADER, BRIGHTNESS_FRAGMENT_SHADER);
        mBrightness = brightness;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBrightnessLocation = GLES20.glGetUniformLocation(mProgramId, "brightness");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        GLES20.glUniform1f(mBrightnessLocation, mBrightness);
    }

    public void setBrightness(final float brightness) {
        mBrightness = brightness;


    }

}
