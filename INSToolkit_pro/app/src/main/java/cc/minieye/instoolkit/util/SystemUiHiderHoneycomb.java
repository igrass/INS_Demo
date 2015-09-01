package cc.minieye.instoolkit.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;

@TargetApi(11)
public class SystemUiHiderHoneycomb extends SystemUiHiderBase {
    private int mHideFlags;
    private int mShowFlags;
    private OnSystemUiVisibilityChangeListener mSystemUiVisibilityChangeListener;
    private int mTestFlags;
    private boolean mVisible;

    protected SystemUiHiderHoneycomb(Activity activity, View view, int i) {
        super(activity, view, i);
        this.mVisible = true;
        this.mSystemUiVisibilityChangeListener = new OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int i) {
                if ((SystemUiHiderHoneycomb.this.mTestFlags & i) != 0) {
                    if (VERSION.SDK_INT < 16) {
                        if (SystemUiHiderHoneycomb.this.mActivity.getActionBar() != null) {
                            SystemUiHiderHoneycomb.this.mActivity.getActionBar().hide();
                        }
                        SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                    SystemUiHiderHoneycomb.this.mOnVisibilityChangeListener.onVisibilityChange(false);
                    SystemUiHiderHoneycomb.this.mVisible = false;
                    return;
                }
                SystemUiHiderHoneycomb.this.mAnchorView.setSystemUiVisibility(SystemUiHiderHoneycomb.this.mShowFlags);
                if (VERSION.SDK_INT < 16) {
                    if (SystemUiHiderHoneycomb.this.mActivity.getActionBar() != null) {
                        SystemUiHiderHoneycomb.this.mActivity.getActionBar().show();
                    }
                    SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                SystemUiHiderHoneycomb.this.mOnVisibilityChangeListener.onVisibilityChange(true);
                SystemUiHiderHoneycomb.this.mVisible = true;
            }
        };
        this.mShowFlags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.mHideFlags = 1;
        this.mTestFlags = 1;
        if ((this.mFlags & 2) != 0) {
            this.mShowFlags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            this.mHideFlags |= 1028;
        }
        if ((this.mFlags & 6) != 0) {
            this.mShowFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            this.mHideFlags |= 514;
            this.mTestFlags = 2;
        }
    }

    public void hide() {
        this.mAnchorView.setSystemUiVisibility(this.mHideFlags);
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void setup() {
        this.mAnchorView.setOnSystemUiVisibilityChangeListener(this.mSystemUiVisibilityChangeListener);
    }

    public void show() {
        this.mAnchorView.setSystemUiVisibility(this.mShowFlags);
    }
}
