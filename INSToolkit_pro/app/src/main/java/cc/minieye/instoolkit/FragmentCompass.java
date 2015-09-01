package cc.minieye.instoolkit;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import cc.minieye.compass.GLView;

public class FragmentCompass extends Fragment {
    private CameraPreview mPreview;
    private SimulationView mSimulationView;

    /* renamed from: cc.minieye.instoolkit.FragmentCompass.1 */
    class AnonymousClass1 implements OnClickListener {
        private final /* synthetic */ AttitudeIndicatorActivity val$activity;

        AnonymousClass1(AttitudeIndicatorActivity attitudeIndicatorActivity) {
            this.val$activity = attitudeIndicatorActivity;
        }

        public void onClick(View view) {
            this.val$activity.showPopupMenu(view, FragmentCompass.this.getView());
        }
    }

    public FragmentCompass() {
        this.mSimulationView = null;
    }

    private void togglebackground() {
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mSimulationView != null) {
            this.mSimulationView.setPanelSize(getView().getWidth(), getView().getHeight());
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setHasOptionsMenu(true);
        AttitudeIndicatorActivity attitudeIndicatorActivity = (AttitudeIndicatorActivity) getActivity();
        View inflate = layoutInflater.inflate(R.layout.fraglayout, viewGroup, false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        attitudeIndicatorActivity.mDisplay.getMetrics(displayMetrics);
        ((ViewGroup) inflate).addView(new GLView(attitudeIndicatorActivity.getApplicationContext(), displayMetrics, attitudeIndicatorActivity.mSimulation.getKalman().getAttitude()), new LayoutParams(-1, -1));
        this.mPreview = new CameraPreview(attitudeIndicatorActivity);
        ((ViewGroup) inflate).addView(this.mPreview);
        if (attitudeIndicatorActivity.useTabletLayout) {
            View inflate2 = layoutInflater.inflate(R.layout.fraglayouticon, viewGroup, false);
            ((ViewGroup) inflate).addView(inflate2);
            ((Button) inflate2.findViewById(R.id.win_menu_btn)).setOnClickListener(new AnonymousClass1(attitudeIndicatorActivity));
        }
        return inflate;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.lookup_groundtrack /*2131034160*/:
                togglebackground();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mPreview != null) {
            this.mPreview.setCamera(null);
        }
    }

    public void onPreferencesChange(AttitudeIndicatorActivity.OptionsInav optionsInav) {
        if (this.mSimulationView != null) {
            int i;
            this.mSimulationView.getPanel().setUnitsMode(optionsInav.enMeterUnit);
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

    public void onResume() {
        super.onResume();
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
}
