package cc.minieye.instoolkit.drag3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;

import cc.minieye.instoolkit.FragmentDataGrid;
import cc.minieye.instoolkit.R;

public class CellObjectAdapter extends BaseAdapter {
    public static final int DEFAULT_NUM_IMAGES = 8;
    public float aspectRatio;
    public int cellHeight;
    public int cellWidth;
    private int gridHeight;
    private int gridWidth;
    private int[] items;
    private Context mContext;
    private FragmentDataGrid mFragment;
    public ViewGroup mParentView;

    public CellObjectAdapter(Context context, FragmentDataGrid fragmentDataGrid, int i, int i2, String[] strArr) {
        this.cellWidth = 120;
        this.cellHeight = 60;
        this.aspectRatio = 0.5f;
        this.mParentView = null;
        this.gridHeight = 0;
        this.gridWidth = 0;
        this.mContext = context;
        this.mFragment = fragmentDataGrid;
        this.gridWidth = i;
        this.gridHeight = i2;
        this.cellWidth = ((int) (((float) i) / ((float) getColumnCount()))) - 8;
        this.cellHeight = (int) (((float) this.cellWidth) * this.aspectRatio);
        this.items = new int[getCount()];
        for (String str : strArr) {
            if (!str.isEmpty()) {
                String[] split = str.replace("{", "").replace("}", "").replace(" ", "").split(",");
                this.items[Integer.valueOf(split[0]).intValue()] = Integer.valueOf(split[1]).intValue();
            }
        }
    }

    public int getCellCount() {
        return getRowCount() * getColumnCount();
    }

    public int getColumnCount() {
        return Math.min(this.mContext.getResources().getInteger(R.integer.max_num_grid_columns), this.gridWidth / this.cellWidth);
    }

    public int getColumnWidth() {
        return this.cellWidth;
    }

    public int getCount() {
        return getCellCount();
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getRowCount() {
        return (int) Math.ceil(((double) this.mContext.getResources().getInteger(R.integer.num_images)) / ((double) getColumnCount()));
    }

    public View getView(int i, View _view, ViewGroup viewGroup) {
        this.mParentView = viewGroup;
        CellObject view;
        if (_view != null) {
            view = (CellObject) _view;
        } else {
            view = new CellObject(this.mContext);
            view.setLayoutParams(new LayoutParams(this.cellWidth, this.cellHeight));
            view.setScaleType(ScaleType.CENTER);
            view.setPadding(DEFAULT_NUM_IMAGES, DEFAULT_NUM_IMAGES, DEFAULT_NUM_IMAGES, DEFAULT_NUM_IMAGES);
        }
        if (this.items[i] > 0) {
            view.mCellNumber = i;
            view.mGrid = (GridView) this.mParentView;
            view.mEmpty = false;
            view.dataType = this.items[i];
            view.setBackgroundResource(R.color.cell_empty);
            this.items[i] = -1;
        }
        if (view.mEmpty) {
            view.mCellNumber = i;
            view.mGrid = (GridView) this.mParentView;
            view.mEmpty = true;
            view.dataType = -1;
            view.setImageResource(android.R.drawable.ic_menu_add);
        }
        view.setBackgroundResource(R.color.cell_empty);
        view.setOnClickListener(this.mFragment);
        view.setOnLongClickListener(this.mFragment);
        return view;
    }

    public void setWidth(int i) {
        this.gridWidth = i;
        this.cellWidth = ((int) (((float) i) / ((float) getColumnCount()))) - 8;
        this.cellHeight = (int) (((float) this.cellWidth) * this.aspectRatio);
    }
}
