package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

public class DeleteZone extends ImageView implements DropTarget {
    private DragController mDragController;
    private boolean mEnabled;

    public DeleteZone(Context context) {
        super(context);
        this.mEnabled = true;
    }

    public DeleteZone(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnabled = true;
    }

    public DeleteZone(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mEnabled = true;
    }

    public boolean acceptDrop(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        return isEnabled();
    }

    public Rect estimateDropLocation(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj, Rect rect) {
        return null;
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    public boolean isEnabled() {
        return this.mEnabled && getVisibility() == 0;
    }

    public void onDragEnter(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        if (isEnabled()) {
            setImageLevel(2);
        }
    }

    public void onDragExit(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        if (isEnabled()) {
            setImageLevel(1);
        }
    }

    public void onDragOver(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
    }

    public void onDrop(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        if (isEnabled()) {
            toast("Moved to trash.");
        }
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setup(DragController dragController) {
        this.mDragController = dragController;
        if (dragController != null) {
            dragController.addDropTarget(this);
        }
    }

    public void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
