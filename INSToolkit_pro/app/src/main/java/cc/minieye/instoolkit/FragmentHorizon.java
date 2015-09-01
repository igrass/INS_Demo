package cc.minieye.instoolkit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class FragmentHorizon extends Fragment {
    private boolean enCameraMode;
    private CameraPreview mPreview;
    private SimulationView mSimulationView;
    private View mainView;

    /* renamed from: cc.minieye.instoolkit.FragmentHorizon.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ AttitudeIndicatorActivity val$activity;

        AnonymousClass1(AttitudeIndicatorActivity attitudeIndicatorActivity) {
            this.val$activity = attitudeIndicatorActivity;
        }

        public void onClick(View view) {
            this.val$activity.showPopupMenu(view, FragmentHorizon.this.getView());
        }
    }

    public FragmentHorizon() {
        this.enCameraMode = false;
    }

    public void changeBackgroundMode() {
        if (this.enCameraMode) {
            ((ViewGroup) this.mainView).removeAllViews();
            ((ViewGroup) this.mainView).addView(this.mSimulationView);
        } else {
            ((ViewGroup) this.mainView).removeAllViews();
            ((ViewGroup) this.mainView).addView(this.mPreview);
            ((ViewGroup) this.mainView).addView(this.mSimulationView);
        }
        this.enCameraMode = !this.enCameraMode;
        this.mSimulationView.getPanel().setHUDMode(this.enCameraMode);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mSimulationView.setPanelSize(getView().getWidth(), getView().getHeight());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setHasOptionsMenu(true);
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        this.mSimulationView = new SimulationView(attitudeIndicatorActivity, attitudeIndicatorActivity.getWindow(), viewGroup.getWidth(), viewGroup.getHeight(), attitudeIndicatorActivity.mSimulation, SimulationView.PANEL_HORIZON, false);
        int rotation = attitudeIndicatorActivity.mWindowManager.getDefaultDisplay().getRotation();
        boolean z = rotation == 0 || rotation == 2;
        this.mSimulationView.setLandscape(z);
        this.mSimulationView.getPanel().setProperty(20, false);
        this.mPreview = new CameraPreview(attitudeIndicatorActivity);
        this.mainView = layoutInflater.inflate(R.layout.fraglayout, viewGroup, false);
        ((ViewGroup) this.mainView).addView(this.mSimulationView, new LayoutParams(-1, -1));
        if (attitudeIndicatorActivity.useTabletLayout) {
            View inflate = layoutInflater.inflate(R.layout.fraglayouticon, viewGroup, false);
            ((ViewGroup) this.mainView).addView(inflate);
            ((Button) inflate.findViewById(R.id.win_menu_btn)).setOnClickListener(new AnonymousClass1(attitudeIndicatorActivity));
        }
        return this.mainView;
    }

    public void onGPSStatusChange(int i) {
        if (this.mSimulationView != null) {
            this.mSimulationView.setGPSIcon(i);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.lookup_background /*2131034159*/:
                changeBackgroundMode();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPause() {
        super.onPause();
        this.mSimulationView.stopSimulation();
        if (this.mPreview != null) {
            this.mPreview.setCamera(null);
        }
    }

    public void onPreferencesChange(AttitudeIndicatorActivity.OptionsInav optionsInav) {
        if (this.mSimulationView != null) {
            int i;
            this.mSimulationView.getPanel().setUnitsMode(optionsInav.enMeterUnit);
            this.mSimulationView.setPitchTrimValue(optionsInav.fZeroPitchValue);
            for (i = 0; i <= 10; i++) {
                this.mSimulationView.getPanel().setProperty(i, false);
            }
            for (i = 0; i < optionsInav.InstrList.length; i++) {
                if (((optionsInav.InstrList[i].equals("null") ? 0 : 1) & (optionsInav.InstrList[i].isEmpty() ? 0 : 1)) != 0) {
                    this.mSimulationView.getPanel().setProperty((int) Double.parseDouble(optionsInav.InstrList[i]), true);
                }
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        if (this.enCameraMode) {
            menu.findItem(R.id.lookup_background).setTitle((String) getResources().getText(R.string.lookup_horizon));
        } else {
            menu.findItem(R.id.lookup_background).setTitle((String) getResources().getText(R.string.lookup_hud));
        }
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume() {
        super.onResume();
        this.mSimulationView.setFilterIcon();
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        if (attitudeIndicatorActivity.getCamera() != null) {
            this.mPreview.setCamera(attitudeIndicatorActivity.getCamera());
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mPreview != null) {
            this.mPreview.setCamera(null);
        }
    }

    public void setCameraMode(boolean z) {
        this.enCameraMode = z;
    }

    public void setRestPosition(double d) {
        if (this.mSimulationView != null) {
            this.mSimulationView.setPitchTrimValue(d);
        }
    }
}
