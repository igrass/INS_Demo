package cc.minieye.instoolkit;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;


import cc.minieye.instoolkit.ui.FormGeneralPanel;
import cc.minieye.kalman.core.attitude.AttitudeFilter;
import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.core.position.phyPosition;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;
import cc.minieye.kalman.util.LVLH;
import cc.minieye.kalman.util.WGS84;

public class SimulationView extends View implements OnLongClickListener, OnClickListener, OnTouchListener {
    public static int PANEL_COMPAS;
    public static int PANEL_GROUNDTRACK;
    public static int PANEL_HORIZON;
    private boolean DEBUG;
    public int DataRecordingPeriod;
    public int DataTransferPeriod;
    private boolean enFullScreenMode;
    private boolean isTabletLandscape;
    private long lastcurt;
    private Context mContext;
    private long mInitialTimeStamp;
    private long mLastTimeStamp;
    private int mScreenHeight;
    private int mScreenWidth;
    private Window mWindow;
    private FormGeneralPanel panel;
    private double pitchTrimZero;
    private Simulation sim;
    private int vBatteryLevel;

    static {
        PANEL_HORIZON = 1;
        PANEL_GROUNDTRACK = 2;
        PANEL_COMPAS = 3;
    }

    public SimulationView(Context context, Window window, int i, int i2, Simulation simulation, int i3, boolean z) {
        super(context);
        this.DEBUG = false;
        this.pitchTrimZero = 0.0d;
        this.DataRecordingPeriod = 50;
        this.DataTransferPeriod = 50;
        this.mLastTimeStamp = 0;
        this.lastcurt = 0;
        this.isTabletLandscape = false;
        this.enFullScreenMode = false;
        this.mContext = context;
        this.mWindow = window;
        this.isTabletLandscape = z;
        this.sim = simulation;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.widthPixels = i;
        displayMetrics.heightPixels = i2;
        if (displayMetrics.widthPixels < 100) {
            displayMetrics.widthPixels = 100;
        }
        if (displayMetrics.heightPixels < 100) {
            displayMetrics.heightPixels = 100;
        }
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
        this.panel = new FormGeneralPanel(context, displayMetrics, i3);
        this.mInitialTimeStamp = System.currentTimeMillis();
    }

    private void addGoogleMapPoint(double d, double d2) {
        this.mContext.getResources().getDrawable(R.drawable.icon);
    }

    public void addGroundMarker() {
        this.panel.addGroundMarker(this.sim.getKalman().getEstimatedRelativePosition());
    }

    public void disableMyLocation() {
    }

    public void enableMyLocation() {
    }

    public FormGeneralPanel getPanel() {
        return this.panel;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onClick(View view) {
    }

    protected void onDraw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastcurt > 5) {
            PositionFilter kalman = this.sim.getKalman();
            AttitudeFilter attitude = kalman.getAttitude();
            this.sim.getGPS();
            setMagDisturbanceStatus(this.sim.getMeasureManager().isMagDisturbance());
            this.lastcurt = currentTimeMillis;
            float currentTimeMillis2 = (float) (((double) (System.currentTimeMillis() - this.mInitialTimeStamp)) / 1000.0d);
            double estimatedRollAngle = Constants.RAD2DEG * attitude.getEstimatedRollAngle();
            double estimatedPitchAngle = Constants.RAD2DEG * attitude.getEstimatedPitchAngle();
            double estimatedYawAngle = Constants.RAD2DEG * attitude.getEstimatedYawAngle();
            double estimatedHeadingAngle = Constants.RAD2DEG * attitude.getEstimatedHeadingAngle();
            CMatrix matLocalFrame2DevFrame = kalman.matLocalFrame2DevFrame();
            CVector wgs84Coordinate = WGS84.wgs84Coordinate(kalman.getEstimatedECEFPosition());
            double element = wgs84Coordinate.getElement(1);
            double element2 = wgs84Coordinate.getElement(2);
            double element3 = wgs84Coordinate.getElement(3);
            CVector estimatedRelativePosition = kalman.getEstimatedRelativePosition();
            CVector cVector = new CVector(3);
            LVLH.getVectorInENU(estimatedRelativePosition, element, element2, cVector);
            CVector estimatedSpeed = this.sim.getKalman().getEstimatedSpeed();
            CVector cVector2 = new CVector(3);
            LVLH.getVelocityInENU(estimatedSpeed, element, element2, cVector2);
            double element4 = cVector2.getElement(1);
            double element5 = cVector2.getElement(2);
            LVLH.getVectorInENU(estimatedSpeed, element, element2, cVector2);
            CVector estimatedAcceleration = this.sim.getKalman().getEstimatedAcceleration();
            if (!this.sim.getKalman().isEnable()) {
                estimatedAcceleration.timesSelf(Double.NaN);
            }
            Canvas canvas2 = canvas;
            this.panel.DrawBoard(canvas2, kalman.getTimeStamp(), (float) this.vBatteryLevel, element * Constants.RAD2DEG, element2 * Constants.RAD2DEG, element3, element4, element5, estimatedHeadingAngle, estimatedPitchAngle - this.pitchTrimZero, estimatedRollAngle, estimatedYawAngle, estimatedAcceleration, cVector, cVector2);
            this.panel.DisplayForces(canvas, kalman.getMeasureTotalAcc(), attitude.getMeasureMag());
            this.panel.DisplayReferential(canvas, matLocalFrame2DevFrame);
            if (this.DEBUG) {
                Canvas canvas3 = canvas;
                this.panel.dbgPrintVector(canvas3, 12, 4, "est=", matLocalFrame2DevFrame.times(kalman.getEstimatedAcceleration()));
                this.panel.dbgPrintVector(canvas, 14, 4, "mes=", kalman.getMeasureTotalAcc());
                canvas3 = canvas;
                this.panel.dbgPrintVector(canvas3, 13, 4, "g  =", matLocalFrame2DevFrame.times(kalman.getGravity()));
                estimatedRelativePosition = new CVector(3);
                attitude.getMeasuredOrientation(estimatedRelativePosition);
                CVector times = estimatedRelativePosition.times((double) Constants.RAD2DEG);
                this.panel.dbgPrintVector(canvas, 16, 4, "angm=", times);
                FormGeneralPanel formGeneralPanel = this.panel;
                times = new CVector(new double[]{estimatedRollAngle, estimatedPitchAngle, estimatedYawAngle});
                formGeneralPanel.dbgPrintVector(canvas, 17, 4, "ange=", times);
                this.panel.dbgPrintScalar(canvas, 15, 6, "g0  =", phyPosition.getGravityAmplitude());
            }
        }
        invalidate();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.widthPixels = getWidth();
        displayMetrics.heightPixels = getHeight();
        this.mScreenWidth = displayMetrics.widthPixels;
        this.mScreenHeight = displayMetrics.heightPixels;
        this.panel.setWindow(this.mWindow, displayMetrics, this.enFullScreenMode);
    }

    public boolean onLongClick(View view) {
        return false;
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void resetGroundOrigin() {
        CVector estimatedECEFPosition = this.sim.getKalman().getEstimatedECEFPosition();
        this.sim.getKalman().setPositionOrigin(estimatedECEFPosition, false);
        this.panel.resetGroundOrigin(estimatedECEFPosition);
    }

    public void setFilterIcon() {
        this.panel.setFilterIcon(this.sim.getKalmanId());
    }

    public void setGPSIcon(int i) {
        this.panel.setGpsIcon(i);
    }

    public void setLandscape(boolean z) {
        this.isTabletLandscape = z;
    }


    public void setMagDisturbanceStatus(boolean z) {
        this.panel.setMagDisturbanceStatus(z);
    }

    public void setPanelSize(int i, int i2) {
        this.mScreenWidth = i;
        this.mScreenHeight = i2;
        this.panel.setWindow(this.mWindow, i, i2, this.enFullScreenMode);
    }

    public void setPitchTrimValue(double d) {
        this.pitchTrimZero = d;
    }

    public void setScreen(boolean z) {
        this.enFullScreenMode = z;
    }

    public void stopSimulation() {
    }
}
