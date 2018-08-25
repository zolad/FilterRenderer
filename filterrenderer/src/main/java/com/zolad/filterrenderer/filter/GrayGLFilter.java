package com.zolad.filterrenderer.filter;

import android.opengl.GLES20;

import com.zolad.filterrenderer.filter.base.GLFilter;

public class GrayGLFilter  extends GLFilter {

    public static final String GRAY_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     float grayColor = (0.3 * textureColor.r + 0.59 * textureColor.g + 0.11 * textureColor.b);\n" +
            "     \n" +
            "     gl_FragColor = vec4(grayColor, grayColor, grayColor, 1.0);\n" +
            " }";



    public GrayGLFilter() {
        super(VERTEX_SHADER, GRAY_FRAGMENT_SHADER);

    }

    @Override
    protected void onCreate() {
        super.onCreate();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



}
