package cc.minieye.compass;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;

import cc.minieye.kalman.core.attitude.AttitudeFilter;

public class GLView extends GLSurfaceView {
    private GLRenderer renderer;

    public GLView(Context context, DisplayMetrics displayMetrics, AttitudeFilter attitudeFilter) {
        super(context);
        this.renderer = null;
        this.renderer = new GLRenderer(context, displayMetrics, attitudeFilter);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 1);
        getHolder().setFormat(-3);
        setZOrderOnTop(true);
        setRenderer(this.renderer);
    }
}
