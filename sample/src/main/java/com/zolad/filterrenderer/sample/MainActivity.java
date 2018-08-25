package com.zolad.filterrenderer.sample;

import android.graphics.SurfaceTexture;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.widget.RadioGroup;

import com.zolad.filterrenderer.FilterRenderer;
import com.zolad.filterrenderer.FilterRendererUtil;
import com.zolad.filterrenderer.filter.BCSGLFilter;
import com.zolad.filterrenderer.filter.BlendGLFilter;
import com.zolad.filterrenderer.filter.BrightnessGLFilter;
import com.zolad.filterrenderer.filter.GrayGLFilter;
import com.zolad.filterrenderer.filter.HSVGLFilter;
import com.zolad.filterrenderer.filter.LookupGLFilter;
import com.zolad.filterrenderer.filter.SharpenGLFilter;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView mBtv;
    private MediaPlayer mediaPlayer;
    private boolean loaded = false;
    FilterRenderer renderer;

    MediaMuxer mm;
    boolean isSurfaceAvailable = false;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtv = findViewById(R.id.btv);
        mRadioGroup = findViewById(R.id.radiogroup);
        this.mediaPlayer = new MediaPlayer();


        renderer = new FilterRenderer(mBtv);

        //
        //

        //renderer2.setFilter(new GLFilter());
        //renderer.setFilter(new LomoGLFilter(this));
        FilterRendererUtil.setFilterRenderer(this, mBtv, renderer);


        renderer.setSurfaceListener(new FilterRenderer.SurfaceListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface) {

                isSurfaceAvailable = true;

                if (mediaPlayer != null) {


                    mediaPlayer.setSurface(new Surface(surface));

                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video);

                    setVideoURI(uri);

                    start();

                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }
        });


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                if (!isSurfaceAvailable)
                    return;
                if (checkedId == R.id.rb0) {
                    renderer.setFilter(null);
                } else if (checkedId == R.id.rb1) {
                    renderer.setFilter(new GrayGLFilter());
                } else if (checkedId == R.id.rb2) {
                    renderer.setFilter(new BrightnessGLFilter(-0.5f));
                } else if (checkedId == R.id.rb3) {
                    renderer.setFilter(new LookupGLFilter(MainActivity.this, R.raw.fairy_tale));
                } else if (checkedId == R.id.rb4) {
                    renderer.setFilter(new HSVGLFilter(MainActivity.this,1.2f,1.2f,1.0f));
                } else if (checkedId == R.id.rb5) {
                    renderer.setFilter(new SharpenGLFilter(4f));
                } else if (checkedId == R.id.rb6) {
                    renderer.setFilter(new BlendGLFilter(MainActivity.this, R.raw.blue));
                } else if (checkedId == R.id.rb7) {
                    renderer.setFilter(new BCSGLFilter(MainActivity.this,1.1f,1.1f,1.3f));
                }


            }
        });


    }


    public void setVideoURI(Uri uri) {


        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            loaded = true;
        } catch (Exception ex) {

        }
    }

    public void start() {


        if (loaded) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

        }


    }


}
