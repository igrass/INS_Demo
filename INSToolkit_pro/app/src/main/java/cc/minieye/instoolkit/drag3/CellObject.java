package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import cc.minieye.instoolkit.R;

public class CellObject extends ImageView implements DragSource, DropTarget {
    public int dataType;
    private String descText;
    public int mCellNumber;
    public boolean mEmpty;
    public GridView mGrid;
    private String unitText;

    public CellObject(Context context) {
        super(context);
        this.mEmpty = true;
        this.mCellNumber = -1;
        this.dataType = 0;
    }

    public CellObject(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEmpty = true;
        this.mCellNumber = -1;
        this.dataType = 0;
    }

    public CellObject(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mEmpty = true;
        this.mCellNumber = -1;
        this.dataType = 0;
    }

    public CellObject(Context context, View view) {
        super(context);
        this.mEmpty = true;
        this.mCellNumber = -1;
        this.dataType = 0;
    }

    public CellObject(Context context, CellObject cellObject) {
        super(context);
        this.mEmpty = true;
        this.mCellNumber = -1;
        this.dataType = 0;
        this.mEmpty = cellObject.mEmpty;
        this.mCellNumber = cellObject.mCellNumber;
        this.mGrid = cellObject.mGrid;
        this.dataType = cellObject.dataType;
        this.unitText = cellObject.unitText;
        this.descText = cellObject.descText;
        cellObject.setOnClickListener((OnClickListener) context);
        cellObject.setOnLongClickListener((OnLongClickListener) context);
    }

    public boolean acceptDrop(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        return this.mEmpty && this.mCellNumber >= 0;
    }

    public boolean allowDrag() {
        return !this.mEmpty;
    }

    public Rect estimateDropLocation(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj, Rect rect) {
        return null;
    }

    public boolean isEmpty() {
        return this.mEmpty;
    }

    public void onDragEnter(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        setBackgroundResource(this.mEmpty ? R.color.cell_empty_hover : R.color.cell_filled_hover);
    }

    public void onDragExit(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        setBackgroundResource(this.mEmpty ? R.color.cell_empty : R.color.cell_filled);
    }

    public void onDragOver(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
    }

    public void onDrop(DragSource dragSource, int i, int i2, int i3, int i4, DragView dragView, Object obj) {
        this.mEmpty = false;
        setBackgroundResource(this.mEmpty ? R.color.cell_empty : R.color.cell_filled);
        this.dataType = ((CellObject) obj).dataType;
        Drawable drawable = ((ImageView) dragSource).getDrawable();
        if (drawable != null) {
            setImageDrawable(drawable);
        }
    }

    public void onDropCompleted(View view, boolean z) {
        if (z) {
            this.mEmpty = true;
            setBackgroundResource(this.mEmpty ? R.color.cell_empty : R.color.cell_filled);
            setScaleType(ScaleType.CENTER);
            setImageResource(android.R.drawable.ic_menu_add);
        }
    }

    public boolean performClick() {
        return this.mEmpty ? super.performClick() : false;
    }

    public boolean performLongClick() {
        return !this.mEmpty ? super.performLongClick() : false;
    }

    public void setDescriptionText(String str) {
        this.descText = str;
    }

    public void setDragController(DragController dragController) {
    }

    public void setUnitText(String str) {
        this.unitText = str;
    }

    public void setValue(CellDataFormater cellDataFormater, int i, float f) {
        setImageBitmap(cellDataFormater.getDataBitmap(i, f));
    }
}
