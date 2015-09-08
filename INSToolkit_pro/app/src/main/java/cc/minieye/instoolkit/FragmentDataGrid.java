package cc.minieye.instoolkit;

import android.app.Fragment;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import cc.minieye.instoolkit.drag3.CellDataFormater;
import cc.minieye.instoolkit.drag3.CellObject;
import cc.minieye.instoolkit.drag3.CellObjectAdapter;
import cc.minieye.instoolkit.drag3.DragController;
import cc.minieye.instoolkit.drag3.DragLayer;
import cc.minieye.instoolkit.drag3.DragSource;
import cc.minieye.instoolkit.ui.DialogCellItem;
import cc.minieye.instoolkit.ui.DialogCellItem.ReadyListener;
import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.util.Constants;

public class FragmentDataGrid extends Fragment implements OnLongClickListener, OnClickListener {
    public static final boolean Debugging = false;
    private DialogCellItem adbCellItems;
    private CellDataFormater cellDataFormater;
    private String gridConfiguration;
    private GridView gridView;
    private DragController mDragController;
    private DragLayer mDragLayer;
    int mScreenHeight;
    int mScreenWidth;
    private AttitudeIndicatorActivity.OptionsInav options;

    /* renamed from: cc.minieye.instoolkit.FragmentDataGrid.2 */
    class AnonymousClass2 implements OnClickListener {
        private final /* synthetic */ AttitudeIndicatorActivity val$activity;

        AnonymousClass2(AttitudeIndicatorActivity attitudeIndicatorActivity) {
            this.val$activity = attitudeIndicatorActivity;
        }

        public void onClick(View view) {
            this.val$activity.showPopupMenu(view, FragmentDataGrid.this.getView());
        }
    }

    /* renamed from: cc.minieye.instoolkit.FragmentDataGrid.3 */
    class AnonymousClass3 implements ReadyListener {
        private final /* synthetic */ View val$v;

        AnonymousClass3(View view) {
            this.val$v = view;
        }

        public void ready(boolean z, int i) {
            if (z) {
                FragmentDataGrid.this.addNewImageToScreen(i, this.val$v, -1);
            }
        }
    }

    private int findFreeSpotInGrid(GridView gridView) {
        int count = gridView.getCount();
        for (int i = 0; i < count; i++) {
            CellObject cellObject = (CellObject) gridView.getChildAt(i);
            if (cellObject != null && cellObject.mEmpty) {
                return i;
            }
        }
        return count - 1;
    }

    private void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public void addNewImageToScreen(int i, View view, int i2) {
        if (this.mDragLayer != null) {
            CellObject cellObject;
            CellObject cellObject2 = (CellObject) view;
            if (cellObject2 == null) {
                GridView gridView = this.mDragLayer.getGridView();
                if (i2 < 0) {
                    i2 = findFreeSpotInGrid(gridView);
                }
                cellObject = (CellObject) gridView.getChildAt(i2);
                gridView.getChildCount();
                if (cellObject != null) {
                    cellObject.mCellNumber = i2;
                } else {
                    return;
                }
            }
            cellObject = cellObject2;
            cellObject.setOnClickListener(this);
            cellObject.setOnLongClickListener(this);
            this.mDragLayer.addGridViewElement(cellObject, i2, this.cellDataFormater.getDataBitmap(i, 0.0f), i);
            this.mDragLayer.getGridView().invalidateViews();
        }
    }

    public boolean getSpecialData(int i, float[] fArr) {
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        if (i == Constants.DATA_WIFI_STRENGTH) {
            if (attitudeIndicatorActivity.mWifiManager != null) {
                List scanResults = attitudeIndicatorActivity.mWifiManager.getScanResults();
                if (scanResults != null && scanResults.size() > 0) {
                    fArr[0] = (float) ((ScanResult) scanResults.get(0)).level;
                }
            }
            return true;
        } else if (i == Constants.DATA_BATTERY_LEVEL) {
            fArr[0] = (float) attitudeIndicatorActivity.vBatteryLevel;
            return true;
        } else if (i == Constants.DATA_TEMPERATURE) {
            fArr[0] = (float) attitudeIndicatorActivity.mSimulation.mTemperatureValue;
            return true;
        } else if (i == Constants.DATA_LUMINOSITY) {
            fArr[0] = (float) attitudeIndicatorActivity.mSimulation.mLuminanceValue;
            return true;
        } else if (i == Constants.DATA_PRESSURE) {
            fArr[0] = (float) attitudeIndicatorActivity.mSimulation.mPressureValue;
            return true;
        } else if (i == Constants.DATA_MOVE) {
            fArr[0] = (float) attitudeIndicatorActivity.moveManager.currMove.norm();
            return true;
        } else {
            return false;
        }
    }

    public void loadGridConfiguration() {
        onPreferencesChange(this.options);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onClick(View view) {
        showSelectionDialog(view);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        View inflate = layoutInflater.inflate(R.layout.dragboard_layout, viewGroup, false);
        this.options = attitudeIndicatorActivity.opts;
        if (bundle == null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            displayMetrics.heightPixels = viewGroup.getHeight();
            displayMetrics.widthPixels = viewGroup.getWidth();
            if (displayMetrics.heightPixels < 100) {
                displayMetrics.heightPixels = 100;
            }
            if (displayMetrics.widthPixels < 100) {
                displayMetrics.widthPixels = 1000;
            }
            this.mScreenHeight = displayMetrics.heightPixels;
            this.mScreenWidth = displayMetrics.widthPixels;
        } else {
            this.mScreenHeight = bundle.getInt("mScreenHeight");
            this.mScreenWidth = bundle.getInt("mScreenWidth");
            this.gridConfiguration = bundle.getString("gridConfiguration");
            this.options.gridDataType = this.gridConfiguration.split(";");
        }
        this.gridView = (GridView) inflate.findViewById(R.id.image_grid_view);
        if (this.gridView == null) {
            toast("Unable to find GridView");
        } else {
            CellObjectAdapter cellObjectAdapter = new CellObjectAdapter(getActivity().getApplicationContext(), this, this.mScreenWidth, this.mScreenHeight, this.options.gridDataType);
            this.gridView.setAdapter(cellObjectAdapter);
            this.gridView.setNumColumns(cellObjectAdapter.getColumnCount());
            this.gridView.setColumnWidth(cellObjectAdapter.getColumnWidth());
            this.cellDataFormater = new CellDataFormater(cellObjectAdapter.cellWidth, cellObjectAdapter.cellHeight);
        }
        ((ImageView) inflate.findViewById(R.id.add_view)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FragmentDataGrid.this.showSelectionDialog(null);
            }
        });
        this.mDragController = new DragController(getActivity());
        this.mDragLayer = (DragLayer) inflate.findViewById(R.id.drag_layer);
        this.mDragLayer.setDragController(this.mDragController);
        this.mDragLayer.setGridView(this.gridView);
        this.mDragController.setDragListener(this.mDragLayer);
        if (attitudeIndicatorActivity.useTabletLayout) {
            View inflate2 = layoutInflater.inflate(R.layout.fraglayouticon, viewGroup, false);
            ((ViewGroup) inflate).addView(inflate2, new LayoutParams(-1, -1));
            ((Button) inflate2.findViewById(R.id.win_menu_btn)).setOnClickListener(new AnonymousClass2(attitudeIndicatorActivity));
        }
        return inflate;
    }

    public void onDestroyView() {
        super.onDestroyView();
        saveGridConfiguration();
    }

    public boolean onLongClick(View view) {
        if (view.isInTouchMode()) {
            return startDrag(view);
        }
        toast("isInTouchMode returned false. Try touching the view again.");
        return false;
    }

    public void onPause() {
        super.onPause();
        saveGridConfiguration();
    }

    public void onPreferencesChange(AttitudeIndicatorActivity.OptionsInav optionsInav) {
        this.options = optionsInav;
        for (String str : optionsInav.gridDataType) {
            if (!str.isEmpty()) {
                String[] split = str.replace("{", "").replace("}", "").replace(" ", "").split(",");
                addNewImageToScreen(Integer.valueOf(split[1]).intValue(), null, Integer.valueOf(split[0]).intValue());
            }
        }
    }

    public void onResume() {
        super.onResume();
        onPreferencesChange(this.options);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("mScreenHeight", this.mScreenHeight);
        bundle.putInt("mScreenWidth", this.mScreenWidth);
        bundle.putString("gridConfiguration", this.gridConfiguration);
    }

    public void onStart() {
        super.onStart();
        loadGridConfiguration();
    }

    public void onStop() {
        super.onStop();
        saveGridConfiguration();
    }

    public void saveGridConfiguration() {
        int count = this.mDragLayer.getGridView().getCount();
        String str = "";
        for (int i = 0; i < count; i++) {
            CellObject gridElement = this.mDragLayer.getGridElement(i);
            if (!(gridElement == null || gridElement.mEmpty)) {
                int i2 = gridElement.mCellNumber;
                int i3 = gridElement.dataType;
                str = new StringBuilder(String.valueOf(str)).append(String.format("{%d,%d};", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)})).toString();
            }
        }
        this.options.gridDataType = str.split(";");
        this.gridConfiguration = str;
        Editor edit = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        edit.putString("datagrid", str);
        edit.commit();
    }

    public void showSelectionDialog(View view) {
        this.adbCellItems = new DialogCellItem(getActivity(), new AnonymousClass3(view));
        this.adbCellItems.setDialog();
        this.adbCellItems.show();
    }

    public boolean startDrag(View view) {
        DragSource dragSource = (DragSource) view;
        this.mDragController.startDrag(view, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);
        return true;
    }

    public void updateData(PositionFilter positionFilter) {
        if (this.mDragLayer != null) {
            GridView gridView = this.mDragLayer.getGridView();
            AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
            int count = gridView.getCount();
            for (int i = 0; i < count; i++) {
                CellObject cellObject = (CellObject) gridView.getChildAt(i);
                if (cellObject != null) {
                    int i2 = cellObject.dataType;
                    if (i2 > 0 && !cellObject.mEmpty) {
                        float[] fArr = new float[]{0.0f};
                        if (getSpecialData(i2, fArr)) {
                            cellObject.setValue(this.cellDataFormater, i2, fArr[0]);
                        } else {
                            cellObject.setValue(this.cellDataFormater, i2, (float) positionFilter.getData(i2));
                        }
                    }
                }
            }
            gridView.invalidate();
        }
    }
}
