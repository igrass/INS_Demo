package cc.minieye.instoolkit.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

public class SystemUiHiderBase extends SystemUiHider {
    private boolean mVisible;

    protected SystemUiHiderBase(Activity activity, View view, int i) {
        super(activity, view, i);
        this.mVisible = true;
    }

    public void hide() {
        if ((this.mFlags & 2) != 0) {
            this.mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.mOnVisibilityChangeListener.onVisibilityChange(false);
        this.mVisible = false;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setup() {
        if ((this.mFlags & 1) == 0) {
            this.mActivity.getWindow().setFlags(768, 768);
        }
    }

    public void show() {
        if ((this.mFlags & 2) != 0) {
            this.mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        this.mOnVisibilityChangeListener.onVisibilityChange(true);
        this.mVisible = true;
    }
}
