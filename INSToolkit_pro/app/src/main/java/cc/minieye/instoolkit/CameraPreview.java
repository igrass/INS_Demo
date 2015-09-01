package cc.minieye.instoolkit;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

final class CameraPreview extends ViewGroup implements Callback {
    private final String TAG;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;
    private SurfaceView mSurfaceView;

    CameraPreview(Context context) {
        super(context);
        this.TAG = "Preview";
        this.mSurfaceView = new SurfaceView(context);
        this.mHolder = this.mSurfaceView.getHolder();
        this.mHolder.addCallback(this);
        addView(this.mSurfaceView);
    }

    private Size getOptimalPreviewSize(List<Size> list, int i, int i2) {
        double d = ((double) i) / ((double) i2);
        if (list == null) {
            return null;
        }
        Size size = null;
        double d2 = Double.MAX_VALUE;
        for (Size size2 : list) {
            if (Math.abs((((double) size2.width) / ((double) size2.height)) - d) <= 0.1d && ((double) Math.abs(size2.height - i2)) < d2) {
                d2 = (double) Math.abs(size2.height - i2);
                size = size2;
            }
        }
        if (size != null) {
            return size;
        }
        Size size3 = size;
        double d3 = Double.MAX_VALUE;
        for (Size size22 : list) {
            if (((double) Math.abs(size22.height - i2)) < d3) {
                d3 = (double) Math.abs(size22.height - i2);
                size3 = size22;
            }
        }
        return size3;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z && getChildCount() > 0) {
            int i5;
            int i6;
            View childAt = getChildAt(0);
            int i7 = i3 - i;
            int i8 = i4 - i2;
            if (this.mPreviewSize != null) {
                i5 = this.mPreviewSize.width;
                i6 = this.mPreviewSize.height;
            } else {
                i6 = i8;
                i5 = i7;
            }
            if (i7 * i6 > i8 * i5) {
                i6 = (i5 * i8) / i6;
                childAt.layout((i7 - i6) / 2, 0, (i6 + i7) / 2, i8);
                return;
            }
            i6 = (i6 * i7) / i5;
            childAt.layout(0, (i8 - i6) / 2, i7, (i6 + i8) / 2);
        }
    }

    protected void onMeasure(int i, int i2) {
        int resolveSize = resolveSize(getSuggestedMinimumWidth(), i);
        int resolveSize2 = resolveSize(getSuggestedMinimumHeight(), i2);
        setMeasuredDimension(resolveSize, resolveSize2);
        if (this.mSupportedPreviewSizes != null) {
            this.mPreviewSize = getOptimalPreviewSize(this.mSupportedPreviewSizes, resolveSize, resolveSize2);
        }
    }

    public void setCamera(Camera camera) {
        if (camera != null) {
            this.mCamera = camera;
            this.mSupportedPreviewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.mCamera.getParameters().setPreviewSize(this.mPreviewSize.width, this.mPreviewSize.height);
        requestLayout();
        try {
            if (this.mCamera != null) {
                this.mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (Throwable e) {
            Log.e("Preview", "IOException caused by setPreviewDisplay()", e);
        }
        this.mCamera.startPreview();
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (this.mCamera != null) {
                this.mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (Throwable e) {
            Log.e("Preview", "IOException caused by setPreviewDisplay()", e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
        }
    }
}
