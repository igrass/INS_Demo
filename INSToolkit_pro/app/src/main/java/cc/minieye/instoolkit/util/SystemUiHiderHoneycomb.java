package cc.minieye.instoolkit.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

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
                        SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
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
                    SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(0, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
                }
                SystemUiHiderHoneycomb.this.mOnVisibilityChangeListener.onVisibilityChange(true);
                SystemUiHiderHoneycomb.this.mVisible = true;
            }
        };
        this.mShowFlags = AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
        this.mHideFlags = 1;
        this.mTestFlags = 1;
        if ((this.mFlags & 2) != 0) {
            this.mShowFlags |= AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
            this.mHideFlags |= 1028;
        }
        if ((this.mFlags & 6) != 0) {
            this.mShowFlags |= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
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
