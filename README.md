FilterRenderer
==============
Filter rendering for Android，can add filter effect to GLSurfaceView using OpenGL. 视频滤镜渲染处理，可以添加多种滤镜，使用了OpenGL

![screenshot1~](https://raw.github.com/zolad/FilterRenderer/master/screenshot/screenshot_1.gif)

Usage
==============
### 1.New a renderer and call glsurfaceview.setRenderer


```java

 FilterRenderer mRenderer = new FilterRenderer(glSurfaceView);
 
 //set renderer
 glSurfaceView.setEGLContextClientVersion(2);
 glSurfaceView.setRenderer(mRenderer);
 glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
 
 
 //add listener for surfacetexture
 mRenderer.setSurfaceListener(new FilterRenderer.SurfaceListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface) {

                // use surfacetexture
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }
        });
   
  
        
```

### 2.Set filter you want

```java

  //New a filter implement you want 
  GLFilter mFilter = new LookupGLFilter(MainActivity.this, R.raw.pic_table);
  
  mRenderer.setFilter(mFilter);
  
  //if you don't want any filter,just call mRenderer.setFilter(null);
 
  

```


License
==============

    Copyright 2018 Zolad

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
