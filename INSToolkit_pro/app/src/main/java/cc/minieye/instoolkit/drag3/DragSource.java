package cc.minieye.instoolkit.drag3;

import android.view.View;

public interface DragSource {
    boolean allowDrag();

    void onDropCompleted(View view, boolean z);

    void setDragController(DragController dragController);
}
