package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

public class DragController {
    public static int DRAG_ACTION_COPY = 0;
    public static int DRAG_ACTION_MOVE = 0;
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private static final String TAG = "DragController";
    private static final int VIBRATE_DURATION = 35;
    private Context mContext;
    private final int[] mCoordinatesTemp;
    private DisplayMetrics mDisplayMetrics;
    private Object mDragInfo;
    private DragSource mDragSource;
    private DragView mDragView;
    private boolean mDragging;
    private ArrayList<DropTarget> mDropTargets;
    private InputMethodManager mInputMethodManager;
    private DropTarget mLastDropTarget;
    private DragListener mListener;
    private float mMotionDownX;
    private float mMotionDownY;
    private View mMoveTarget;
    private View mOriginator;
    private Rect mRectTemp;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private Vibrator mVibrator;
    private IBinder mWindowToken;

    interface DragListener {
        void onDragEnd();

        void onDragStart(DragSource dragSource, Object obj, int i);
    }

    static {
        DRAG_ACTION_MOVE = 0;
        DRAG_ACTION_COPY = 1;
    }

    public DragController(Context context) {
        this.mRectTemp = new Rect();
        this.mCoordinatesTemp = new int[2];
        this.mDisplayMetrics = new DisplayMetrics();
        this.mDropTargets = new ArrayList();
        this.mContext = context;
        this.mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private static int clamp(int i, int i2, int i3) {
        return i < i2 ? i2 : i >= i3 ? i3 - 1 : i;
    }

    private boolean drop(float f, float f2) {
        int[] iArr = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget((int) f, (int) f2, iArr);
        if (findDropTarget == null) {
            return PROFILE_DRAWING_DURING_DRAG;
        }
        findDropTarget.onDragExit(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
        if (findDropTarget.acceptDrop(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo)) {
            findDropTarget.onDrop(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
            this.mDragSource.onDropCompleted((View) findDropTarget, true);
            return true;
        }
        this.mDragSource.onDropCompleted((View) findDropTarget, PROFILE_DRAWING_DURING_DRAG);
        return true;
    }

    private void endDrag() {
        if (this.mDragging) {
            this.mDragging = PROFILE_DRAWING_DURING_DRAG;
            if (this.mOriginator != null) {
                this.mOriginator.setVisibility(View.VISIBLE);
            }
            if (this.mListener != null) {
                this.mListener.onDragEnd();
            }
            if (this.mDragView != null) {
                this.mDragView.remove();
                this.mDragView = null;
            }
        }
    }

    private DropTarget findDropTarget(int i, int i2, int[] iArr) {
        Rect rect = this.mRectTemp;
        ArrayList arrayList = this.mDropTargets;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            DropTarget dropTarget = (DropTarget) arrayList.get(size);
            dropTarget.getHitRect(rect);
            dropTarget.getLocationOnScreen(iArr);
            rect.offset(iArr[0] - dropTarget.getLeft(), iArr[1] - dropTarget.getTop());
            if (rect.contains(i, i2)) {
                iArr[0] = i - iArr[0];
                iArr[1] = i2 - iArr[1];
                return dropTarget;
            }
        }
        return null;
    }

    private Bitmap getViewBitmap(View view) {
        view.clearFocus();
        view.setPressed(PROFILE_DRAWING_DURING_DRAG);
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(PROFILE_DRAWING_DURING_DRAG);
        int drawingCacheBackgroundColor = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        if (drawingCacheBackgroundColor != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            Log.e(TAG, "failed getViewBitmap(" + view + ")", new RuntimeException());
            return null;
        }
        drawingCache = Bitmap.createBitmap(drawingCache);
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheBackgroundColor(drawingCacheBackgroundColor);
        return drawingCache;
    }

    private void recordScreenSize() {
        ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(this.mDisplayMetrics);
    }

    public void addDropTarget(DropTarget dropTarget) {
        this.mDropTargets.add(dropTarget);
    }

    public void cancelDrag() {
        endDrag();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragging;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return (this.mMoveTarget == null || !this.mMoveTarget.dispatchUnhandledMove(view, i)) ? PROFILE_DRAWING_DURING_DRAG : true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            recordScreenSize();
        }
        int clamp = clamp((int) motionEvent.getRawX(), 0, this.mDisplayMetrics.widthPixels);
        int clamp2 = clamp((int) motionEvent.getRawY(), 0, this.mDisplayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_DOWN /*0*/:
                this.mMotionDownX = (float) clamp;
                this.mMotionDownY = (float) clamp2;
                this.mLastDropTarget = null;
                break;
            case MotionEvent.ACTION_UP /*1*/:
            case MotionEvent.ACTION_CANCEL /*3*/:
                if (this.mDragging) {
                    drop((float) clamp, (float) clamp2);
                }
                endDrag();
                break;
        }
        return this.mDragging;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mDragging) {
            return PROFILE_DRAWING_DURING_DRAG;
        }
        int action = motionEvent.getAction();
        int clamp = clamp((int) motionEvent.getRawX(), 0, this.mDisplayMetrics.widthPixels);
        int clamp2 = clamp((int) motionEvent.getRawY(), 0, this.mDisplayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_DOWN /*0*/:
                this.mMotionDownX = (float) clamp;
                this.mMotionDownY = (float) clamp2;
                break;
            case MotionEvent.ACTION_UP /*1*/:
                if (this.mDragging) {
                    drop((float) clamp, (float) clamp2);
                }
                endDrag();
                break;
            case MotionEvent.ACTION_MOVE /*2*/:
                this.mDragView.move((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                int[] iArr = this.mCoordinatesTemp;
                DropTarget findDropTarget = findDropTarget(clamp, clamp2, iArr);
                if (findDropTarget != null) {
                    if (this.mLastDropTarget == findDropTarget) {
                        findDropTarget.onDragOver(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
                    } else {
                        if (this.mLastDropTarget != null) {
                            this.mLastDropTarget.onDragExit(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
                        }
                        findDropTarget.onDragEnter(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
                    }
                } else if (this.mLastDropTarget != null) {
                    this.mLastDropTarget.onDragExit(this.mDragSource, iArr[0], iArr[1], (int) this.mTouchOffsetX, (int) this.mTouchOffsetY, this.mDragView, this.mDragInfo);
                }
                this.mLastDropTarget = findDropTarget;
                break;
            case MotionEvent.ACTION_CANCEL /*3*/:
                cancelDrag();
                break;
        }
        return true;
    }

    public void removeAllDropTargets() {
        this.mDropTargets = new ArrayList();
    }

    public void removeDragListener(DragListener dragListener) {
        this.mListener = null;
    }

    public void removeDropTarget(DropTarget dropTarget) {
        this.mDropTargets.remove(dropTarget);
    }

    public void setDragListener(DragListener dragListener) {
        this.mListener = dragListener;
    }

    void setMoveTarget(View view) {
        this.mMoveTarget = view;
    }

    public void setWindowToken(IBinder iBinder) {
        this.mWindowToken = iBinder;
    }

    public void startDrag(Bitmap bitmap, int i, int i2, int i3, int i4, int i5, int i6, DragSource dragSource, Object obj, int i7) {
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        this.mInputMethodManager.hideSoftInputFromWindow(this.mWindowToken, 0);
        if (this.mListener != null) {
            this.mListener.onDragStart(dragSource, obj, i7);
        }
        int i8 = ((int) this.mMotionDownX) - i;
        int i9 = ((int) this.mMotionDownY) - i2;
        this.mTouchOffsetX = this.mMotionDownX - ((float) i);
        this.mTouchOffsetY = this.mMotionDownY - ((float) i2);
        this.mDragging = true;
        this.mDragSource = dragSource;
        this.mDragInfo = obj;
        this.mVibrator.vibrate(35);
        DragView dragView = new DragView(this.mContext, bitmap, i8, i9, i3, i4, i5, i6);
        this.mDragView = dragView;
        dragView.show(this.mWindowToken, (int) this.mMotionDownX, (int) this.mMotionDownY);
    }

    public void startDrag(View view, DragSource dragSource, Object obj, int i) {
        if (dragSource.allowDrag()) {
            this.mOriginator = view;
            Bitmap viewBitmap = getViewBitmap(view);
            if (viewBitmap != null) {
                int[] iArr = this.mCoordinatesTemp;
                view.getLocationOnScreen(iArr);
                startDrag(viewBitmap, iArr[0], iArr[1], 0, 0, viewBitmap.getWidth(), viewBitmap.getHeight(), dragSource, obj, i);
                viewBitmap.recycle();
                if (i == DRAG_ACTION_MOVE) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
}
