package cc.minieye.instoolkit.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.view.View;

public abstract class SystemUiHider {
    public static final int FLAG_FULLSCREEN = 2;
    public static final int FLAG_HIDE_NAVIGATION = 6;
    public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 1;
    private static OnVisibilityChangeListener sDummyListener;
    protected Activity mActivity;
    protected View mAnchorView;
    protected int mFlags;
    protected OnVisibilityChangeListener mOnVisibilityChangeListener;

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(boolean z);
    }

    static {
        sDummyListener = new OnVisibilityChangeListener() {
            public void onVisibilityChange(boolean z) {
            }
        };
    }

    protected SystemUiHider(Activity activity, View view, int i) {
        this.mOnVisibilityChangeListener = sDummyListener;
        this.mActivity = activity;
        this.mAnchorView = view;
        this.mFlags = i;
    }

    public static SystemUiHider getInstance(Activity activity, View view, int i) {
        return VERSION.SDK_INT >= 11 ? new SystemUiHiderHoneycomb(activity, view, i) : new SystemUiHiderBase(activity, view, i);
    }

    public abstract void hide();

    public abstract boolean isVisible();

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener onVisibilityChangeListener) {
        if (onVisibilityChangeListener == null) {
            onVisibilityChangeListener = sDummyListener;
        }
        this.mOnVisibilityChangeListener = onVisibilityChangeListener;
    }

    public abstract void setup();

    public abstract void show();

    public void toggle() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }
}
