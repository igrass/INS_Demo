package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import cc.minieye.instoolkit.R;

public class DragLayer extends FrameLayout implements DragController.DragListener {
    public DragController mDragController;
    private GridView mGridView;
    private int nitem;

    public DragLayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.nitem = 0;
    }

    public boolean addGridViewElement(CellObject cellObject, int i, Bitmap bitmap, int i2) {
        cellObject.setImageBitmap(bitmap);
        cellObject.setBackgroundResource(R.color.cell_filled);
        cellObject.dataType = i2;
        cellObject.mGrid = getGridView();
        cellObject.mEmpty = false;
        this.nitem++;
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragController.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return this.mDragController.dispatchUnhandledMove(view, i);
    }

    public CellObject getGridElement(int i) {
        return (CellObject) this.mGridView.getChildAt(i);
    }

    public int getGridElementCount() {
        return this.nitem;
    }

    public GridView getGridView() {
        return this.mGridView;
    }

    public void onDragEnd() {
        this.mDragController.removeAllDropTargets();
    }

    public void onDragStart(DragSource dragSource, Object obj, int i) {
        if (this.mGridView != null) {
            int childCount = this.mGridView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                this.mDragController.addDropTarget((DropTarget) this.mGridView.getChildAt(i2));
            }
        }
        View findViewById = findViewById(R.id.delete_zone_view);
        if (findViewById != null) {
            this.mDragController.addDropTarget((DeleteZone) findViewById);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mDragController.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mDragController.onTouchEvent(motionEvent);
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setGridView(GridView gridView) {
        this.mGridView = gridView;
    }
}
