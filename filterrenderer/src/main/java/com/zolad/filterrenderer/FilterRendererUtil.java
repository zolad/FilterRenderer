package com.zolad.filterrenderer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.widget.Toast;

public class FilterRendererUtil {


    public  static  void  setFilterRenderer(Context context,GLSurfaceView glSurfaceView,FilterRenderer renderer){


        final ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();


        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(renderer);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        } else {
            Toast.makeText(context, "Not support OpenGL2.0", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
