package cc.minieye.instoolkit;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import cc.minieye.instoolkit.Simulation.UserCallbackFcn;
import cc.minieye.instoolkit.ui.DialogWithCheckBox;
import cc.minieye.instoolkit.ui.FormOptionsList;
import cc.minieye.instoolkit.util.SystemUiHider;
import cc.minieye.instoolkit.util.SystemUiHider.OnVisibilityChangeListener;
import cc.minieye.kalman.core.CovarianceSettings;
import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public final class AttitudeIndicatorActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    static int CONTENT_COMPAS = 0;
    static int CONTENT_DATA = 0;
    static int CONTENT_GROUND = 0;
    static int CONTENT_HUD = 0;
    static int CONTENT_PLOTS = 0;
    static int CONTENT_VOID = 0;
    private static boolean DEBUG_TRACE = false;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private static final boolean TOGGLE_ON_CLICK = true;
    static String[] fragmentTags;
    private AlertDialog alertGPSdialog;
    private AlertDialog alertWifiDialog;
    private ProgressDialog calibrateProgressDialog;
    private int defaultCameraId;
    private FragmentCompass fragmentCompass;
    private FragmentDataGrid fragmentDataGrid;
    private FragmentHorizon fragmentHorizon;
    private FragmentFigure fragmentPlots;
    private DataRecorder logging;
    private BroadcastReceiver mBatInfoReceiver;
    private Camera mCamera;
    OnTouchListener mDelayHideTouchListener;
    public Display mDisplay;
    Handler mHideHandler;
    Runnable mHideRunnable;
    public LocationManager mLocManager;
    public PowerManager mPowerManager;
    public SensorManager mSensorManager;
    public Simulation mSimulation;
    private SystemUiHider mSystemUiHider;
    private WakeLock mWakeLock;
    public WifiManager mWifiManager;
    private BroadcastReceiver mWifiStateReceiver;
    public WindowManager mWindowManager;
    private boolean menuShown;
    private int nextfragment;
    public OptionsInav opts;
    private int selectedFragment;
    public boolean useTabletLayout;
    public int vBatteryLevel;
    private int[] viewIds;

    /* renamed from: cc.minieye.instoolkit.AttitudeIndicatorActivity.13 */
    class AnonymousClass13 implements OnMenuItemClickListener {
        private final /* synthetic */ int val$idContainer;

        AnonymousClass13(int i) {
            this.val$idContainer = i;
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.menu_add_view_left) {
                AttitudeIndicatorActivity.this.showLayout(0);
                AttitudeIndicatorActivity.this.showFragmentContent(0, AttitudeIndicatorActivity.this.getAvailableFragment(1));
            } else if (itemId == R.id.menu_add_view_right) {
                AttitudeIndicatorActivity.this.showLayout(1);
                AttitudeIndicatorActivity.this.showFragmentContent(1, AttitudeIndicatorActivity.this.getAvailableFragment(1));
            } else if (itemId == R.id.menu_add_view_down) {
                AttitudeIndicatorActivity.this.showLayout(2);
                AttitudeIndicatorActivity.this.showFragmentContent(2, AttitudeIndicatorActivity.this.getAvailableFragment(1));
            } else if (itemId == R.id.menu_change_view) {
                itemId = AttitudeIndicatorActivity.this.getAvailableFragment(AttitudeIndicatorActivity.this.nextfragment);
                AttitudeIndicatorActivity.this.nextfragment = itemId + 1;
                AttitudeIndicatorActivity.this.showFragmentContent(this.val$idContainer, itemId);
            } else if (itemId == R.id.menu_close_view) {
                AttitudeIndicatorActivity.this.hideLayout(this.val$idContainer);
            }
            return AttitudeIndicatorActivity.TOGGLE_ON_CLICK;
        }
    }

    /* renamed from: cc.minieye.instoolkit.AttitudeIndicatorActivity.5 */
    static class AnonymousClass5 implements OnClickListener {
        private final /* synthetic */ SharedPreferences val$settings;

        AnonymousClass5(SharedPreferences sharedPreferences) {
            this.val$settings = sharedPreferences;
        }

        public void onClick(View view) {
            view.setVisibility(View.GONE);
            Editor edit = this.val$settings.edit();
            edit.putString("skipMessage", "checked");
            edit.commit();
        }
    }

    class OptionsInav {
        String[] InstrList;
        int KalmanFilterType;
        String UDPadress;
        int UDPport;
        float abx;
        float aby;
        float abz;
        int covSetting;
        boolean enGoogleMapMain;
        boolean enKapmanPosition;
        int enMeterUnit;
        boolean enUDPOutput;
        double fZeroPitchValue;
        int fileformat;
        String[] gridDataType;
        int layoutStyle;
        double valAccelerationCovariance;
        double valCompassCovariance;
        double valKalmanFrequency;
        int valKalmanPower;
        double valMeasureAngleCovariance;
        double valOmegaCovariance;
        double valPositionCovariance;
        double valVelocityCovariance;

        OptionsInav() {
            this.InstrList = null;
            this.gridDataType = null;
        }
    }

    static {
        DEBUG_TRACE = false;
        CONTENT_VOID = 0;
        CONTENT_HUD = 1;
        CONTENT_GROUND = 2;
        CONTENT_DATA = 3;
        CONTENT_PLOTS = 4;
        CONTENT_COMPAS = 5;
        String[] strArr = new String[6];
        strArr[0] = "";
        strArr[1] = "fragHorizon";
        strArr[2] = "fragGround";
        strArr[3] = "fragDataGrid";
        strArr[4] = "fragDataPlots";
        strArr[5] = "fragCompass";
        fragmentTags = strArr;
    }

    public AttitudeIndicatorActivity() {
        this.mHideHandler = null;
        this.mHideRunnable = null;
        this.mWifiManager = null;
        this.nextfragment = 1;
        this.viewIds = new int[]{R.id.fragment_container1, R.id.fragment_container2, R.id.fragment_container3};
        this.useTabletLayout = TOGGLE_ON_CLICK;
        this.menuShown = false;
        this.opts = new OptionsInav();
        this.alertGPSdialog = null;
        this.alertWifiDialog = null;
        this.mDelayHideTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AttitudeIndicatorActivity.this.delayedHide(AttitudeIndicatorActivity.AUTO_HIDE_DELAY_MILLIS);
                return false;
            }
        };
        this.mWifiStateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                    return;
                }
                if (intent.getBooleanExtra("connected", false)) {
                    AttitudeIndicatorActivity.this.mSimulation.canSendDataOverWifi = AttitudeIndicatorActivity.TOGGLE_ON_CLICK;
                } else {
                    AttitudeIndicatorActivity.this.mSimulation.canSendDataOverWifi = AttitudeIndicatorActivity.TOGGLE_ON_CLICK;
                }
            }
        };
        this.mBatInfoReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("level", 0);
                AttitudeIndicatorActivity.this.vBatteryLevel = (intExtra * 100) / intent.getIntExtra("scale", 100);
            }
        };
    }

    private void buildAlertMessageNoGps() {
        Builder builder = new Builder(this);
        builder.setMessage(R.string.msg_gps).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AttitudeIndicatorActivity.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        this.alertGPSdialog = builder.create();
        this.alertGPSdialog.show();
    }

    private void buildAlertMessageNoWifi() {
        Builder builder = new Builder(this);
        builder.setMessage(R.string.msg_wifi).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AttitudeIndicatorActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
                AttitudeIndicatorActivity.this.mSimulation.canSendDataOverWifi = AttitudeIndicatorActivity.TOGGLE_ON_CLICK;
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        this.alertWifiDialog = builder.create();
        this.alertWifiDialog.show();
    }

    private void delayedHide(int i) {
        if (this.mHideHandler != null) {
            this.mHideHandler.removeCallbacks(this.mHideRunnable);
            this.mHideHandler.postDelayed(this.mHideRunnable, (long) i);
        }
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration viewConfiguration = ViewConfiguration.get(this);
            Field declaredField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (declaredField != null) {
                declaredField.setAccessible(TOGGLE_ON_CLICK);
                declaredField.setBoolean(viewConfiguration, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPreferences() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.opts.enMeterUnit = (int) Double.parseDouble(defaultSharedPreferences.getString("meterschecked", "0"));
        this.opts.fZeroPitchValue = (double) defaultSharedPreferences.getFloat("rest_pitch_value", 0.0f);
        this.opts.enGoogleMapMain = defaultSharedPreferences.getBoolean("googlemapchecked", false);
        if (this.useTabletLayout) {
            this.opts.layoutStyle = (int) Double.parseDouble(defaultSharedPreferences.getString("selectlayout", "4"));
        } else {
            this.opts.layoutStyle = (int) Double.parseDouble(defaultSharedPreferences.getString("selectlayout", "2"));
        }
        this.opts.InstrList = defaultSharedPreferences.getString("disp_multi_select", "1,2,3,4,5,6,7,11").split(",");
        this.opts.KalmanFilterType = (int) Double.parseDouble(defaultSharedPreferences.getString("filterchoice", "1"));
        if (this.mSimulation.getKalmanId() != this.opts.KalmanFilterType) {
            this.mSimulation.setKalmanId(this.opts.KalmanFilterType);
        }
        this.opts.valKalmanFrequency = Double.parseDouble(defaultSharedPreferences.getString("editfrequency", "50"));
        this.mSimulation.getKalman().setFrequency(this.opts.valKalmanFrequency);
        this.opts.valKalmanPower = (int) Double.parseDouble(defaultSharedPreferences.getString("editpower", "1"));
        this.mSimulation.setPower(this.opts.valKalmanPower);
        this.opts.enKapmanPosition = defaultSharedPreferences.getBoolean("enKFposition", false);
        this.mSimulation.getKalman().enablePositionFilter(this.opts.enKapmanPosition);
        this.opts.covSetting = Integer.parseInt(defaultSharedPreferences.getString("paramcov", "1"));
        if (this.opts.covSetting == 999) {
            this.opts.valMeasureAngleCovariance = Double.parseDouble(defaultSharedPreferences.getString("editanglecov", "50"));
            this.opts.valCompassCovariance = Double.parseDouble(defaultSharedPreferences.getString("editcompasscov", "100"));
            this.opts.valOmegaCovariance = Double.parseDouble(defaultSharedPreferences.getString("editomegacov", "0.01"));
            this.opts.valPositionCovariance = Double.parseDouble(defaultSharedPreferences.getString("editpositioncov", "0.001"));
            this.opts.valVelocityCovariance = Double.parseDouble(defaultSharedPreferences.getString("editvelocitycov", "0.001"));
            this.opts.valAccelerationCovariance = Double.parseDouble(defaultSharedPreferences.getString("editaccelcov", "0.01"));
            this.mSimulation.getKalman().setMeasurementCovariances(this.opts.valMeasureAngleCovariance, this.opts.valCompassCovariance, this.opts.valOmegaCovariance, this.opts.valPositionCovariance, this.opts.valVelocityCovariance, this.opts.valAccelerationCovariance);
            this.opts.abx = defaultSharedPreferences.getFloat("accbiasX", 0.0f);
            this.opts.aby = defaultSharedPreferences.getFloat("accbiasY", 0.0f);
            this.opts.abz = defaultSharedPreferences.getFloat("accbiasZ", 0.0f);
            this.mSimulation.getKalman().setAccBias((double) this.opts.abx, (double) this.opts.aby, (double) this.opts.abz);
        } else {
            if (this.opts.covSetting < 1 || this.opts.covSetting > 4) {
                this.opts.covSetting = 1;
            }
            int i = this.opts.covSetting - 1;
            this.opts.valMeasureAngleCovariance = CovarianceSettings.getMeasureAngle(i);
            this.opts.valCompassCovariance = CovarianceSettings.getMeasureMagneticCompass(i);
            this.opts.valOmegaCovariance = CovarianceSettings.getMeasureAngularVelocity(i);
            this.opts.valPositionCovariance = CovarianceSettings.getMeasureGPSGroundPosition(i);
            this.opts.valVelocityCovariance = CovarianceSettings.getMeasurGPSGroundVelocity(i);
            this.opts.valAccelerationCovariance = CovarianceSettings.getMeasureAcceleration(i);
            this.mSimulation.getKalman().setMeasurementCovariances(this.opts.valMeasureAngleCovariance, this.opts.valCompassCovariance, this.opts.valOmegaCovariance, this.opts.valPositionCovariance, this.opts.valVelocityCovariance, this.opts.valAccelerationCovariance);
            this.opts.abx = defaultSharedPreferences.getFloat("accbiasX", 0.0f);
            this.opts.aby = defaultSharedPreferences.getFloat("accbiasY", 0.0f);
            this.opts.abz = defaultSharedPreferences.getFloat("accbiasZ", 0.0f);
            this.mSimulation.getKalman().setAccBias((double) this.opts.abx, (double) this.opts.aby, (double) this.opts.abz);
        }
        this.opts.fileformat = (int) Double.parseDouble(defaultSharedPreferences.getString("fileformat_select", "0"));
        this.opts.gridDataType = defaultSharedPreferences.getString("datagrid", "").split(";");
        if (this.fragmentHorizon != null) {
            this.fragmentHorizon.onPreferencesChange(this.opts);
        }
        if (this.fragmentDataGrid != null) {
            this.fragmentDataGrid.onPreferencesChange(this.opts);
        }
        this.opts.enUDPOutput = defaultSharedPreferences.getBoolean("enUDP", false);
        if (this.opts.enUDPOutput) {
            this.opts.UDPport = (int) Double.parseDouble(defaultSharedPreferences.getString("editudpport", "12345"));
            this.opts.UDPadress = defaultSharedPreferences.getString("editudpaddress", "192.168.1.2");
        }
    }

    public static boolean isTablet(Activity activity) {
        int i = activity.getResources().getConfiguration().screenLayout & 15;
        boolean z = i == 4 || i == 3;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        boolean z2 = displayMetrics.densityDpi == 213 || displayMetrics.densityDpi == 320;
        return (z || z2) ? TOGGLE_ON_CLICK : false;
    }

    private boolean onMenuSelected(int i) {
        if (!this.useTabletLayout) {
            switch (i) {
                case R.id.lookup_background /*2131034159*/:
                    if (this.selectedFragment == CONTENT_HUD) {
                        this.fragmentHorizon.changeBackgroundMode();
                        return TOGGLE_ON_CLICK;
                    }
                    this.fragmentHorizon.setCameraMode(false);
                    showFragmentContent(0, CONTENT_HUD);
                    return TOGGLE_ON_CLICK;
                case R.id.lookup_groundtrack /*2131034160*/:
                    showFragmentContent(0, CONTENT_GROUND);
                    return TOGGLE_ON_CLICK;
            }
        }
        switch (i) {
            case R.id.lookup_compass /*2131034161*/:
                showFragmentContent(0, CONTENT_COMPAS);
                return TOGGLE_ON_CLICK;
            case R.id.lookup_plot /*2131034162*/:
                showFragmentContent(0, CONTENT_PLOTS);
                return TOGGLE_ON_CLICK;
            case R.id.lookup_infoboard /*2131034163*/:
                if (this.useTabletLayout) {
                    showFragmentContent(2, CONTENT_DATA);
                    return TOGGLE_ON_CLICK;
                }
                showFragmentContent(0, CONTENT_DATA);
                return TOGGLE_ON_CLICK;
            case R.id.lookup_restposition /*2131034164*/:
                CVector estimatedAngles = this.mSimulation.getKalman().getEstimatedAngles();
                this.opts.fZeroPitchValue = Constants.RAD2DEG * estimatedAngles.getElement(2);
                this.fragmentHorizon.setRestPosition(this.opts.fZeroPitchValue);
                Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                edit.putFloat("rest_pitch_value", (float) this.opts.fZeroPitchValue);
                edit.apply();
                return TOGGLE_ON_CLICK;
            case R.id.lookup_calibrate /*2131034165*/:
                showCalibration();
                return TOGGLE_ON_CLICK;
            case R.id.lookup_logging /*2131034166*/:
                startRecording();
                return TOGGLE_ON_CLICK;
            case R.id.lookup_settings /*2131034167*/:
                showOptionsForm();
                return TOGGLE_ON_CLICK;
            case R.id.lookup_about /*2131034169*/:
                showDisclaimer();
                return TOGGLE_ON_CLICK;
            default:
                return false;
        }
    }

    private void setActionBarHider() {
        this.mHideHandler = new Handler();
        this.mHideRunnable = new Runnable() {
            public void run() {
                AttitudeIndicatorActivity.this.mSystemUiHider.hide();
            }
        };
        View findViewById = findViewById(R.id.fragment_container1);
        this.mSystemUiHider = SystemUiHider.getInstance(this, findViewById, HIDER_FLAGS);
        this.mSystemUiHider.setup();
        this.mSystemUiHider.setOnVisibilityChangeListener(new OnVisibilityChangeListener() {
            int mShortAnimTime;

            @TargetApi(13)
            public void onVisibilityChange(boolean z) {
                if (VERSION.SDK_INT >= 13 && this.mShortAnimTime == 0) {
                    this.mShortAnimTime = AttitudeIndicatorActivity.this.getResources().getInteger(android.R.integer.config_shortAnimTime);
                }
                if (z) {
                    AttitudeIndicatorActivity.this.delayedHide(AttitudeIndicatorActivity.AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });
        findViewById.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AttitudeIndicatorActivity.this.mSystemUiHider.toggle();
            }
        });
    }

    public static void showHelpOverlay(Activity activity, Context context, View view, int i, String str, boolean z) {
        if (view != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(str, 0);
            if (!sharedPreferences.getString("skipMessage", "NOT checked").equalsIgnoreCase("checked") || z) {
                activity.addContentView(activity.getLayoutInflater().inflate(R.layout.help1_layout, null), new LayoutParams(-1, -1));
                ((ImageView) view.findViewById(R.id.image_help)).setBackgroundResource(i);
                ((RelativeLayout) view.findViewById(R.id.help1)).setOnClickListener(new AnonymousClass5(sharedPreferences));
            }
        }
    }

    public void applyPreferences() {
        if (this.logging != null) {
            this.logging.setFileFormat(this.opts.fileformat);
        }
        if (this.opts.enUDPOutput) {
            this.mSimulation.canSendDataOverWifi = TOGGLE_ON_CLICK;
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                buildAlertMessageNoWifi();
            } else if (wifiManager.getWifiState() == 1) {
                buildAlertMessageNoWifi();
            }
            this.mSimulation.openUDP(this.opts.UDPport, this.opts.UDPadress);
            return;
        }
        this.mSimulation.closeUDP();
    }

    public int getAvailableFragment(int i) {
        FragmentManager fragmentManager = getFragmentManager();
        if (i < 1) {
            i = 1;
        }
        int i2 = i;
        while (i2 < fragmentTags.length) {
            Fragment findFragmentByTag = fragmentManager.findFragmentByTag(fragmentTags[i2]);
            if (findFragmentByTag == null || !findFragmentByTag.isVisible()) {
                return i2;
            }
            i2++;
        }
        i2 = 1;
        while (i2 < i) {
            Fragment findFragmentByTag2 = fragmentManager.findFragmentByTag(fragmentTags[i2]);
            if (findFragmentByTag2 == null || !findFragmentByTag2.isVisible()) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    public Camera getCamera() {
        return this.mCamera;
    }

    public void hideLayout(int i) {
        int i2 = this.viewIds[i];
        View findViewById = findViewById(i2);
        if (findViewById != null) {
            findViewById.setVisibility(View.GONE);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            Fragment findFragmentById = fragmentManager.findFragmentById(i2);
            if (findFragmentById != null) {
                beginTransaction.remove(findFragmentById);
            }
            beginTransaction.commit();
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (DEBUG_TRACE) {
            Debug.startMethodTracing("inavtrace");
        }
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        this.mDisplay = this.mWindowManager.getDefaultDisplay();
        this.mDisplay.getMetrics(new DisplayMetrics());
        this.mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = this.mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getName());
        this.mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(this.mBatInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        registerReceiver(this.mWifiStateReceiver, new IntentFilter("android.net.wifi.supplicant.CONNECTION_CHANGE"));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.mSimulation = new Simulation(this, this.mSensorManager, this.mLocManager, this.mWindowManager.getDefaultDisplay().getRotation());
        if (bundle == null) {
            this.fragmentHorizon = new FragmentHorizon();
            this.fragmentCompass = new FragmentCompass();
            this.fragmentDataGrid = new FragmentDataGrid();
            this.fragmentPlots = new FragmentFigure();
        } else {
            this.fragmentHorizon = (FragmentHorizon) getFragmentManager().findFragmentByTag(fragmentTags[1]);
            if (this.fragmentHorizon == null) {
                this.fragmentHorizon = new FragmentHorizon();
            }
            this.fragmentCompass = (FragmentCompass) getFragmentManager().findFragmentByTag(fragmentTags[5]);
            if (this.fragmentCompass == null) {
                this.fragmentCompass = new FragmentCompass();
            }
            this.fragmentDataGrid = (FragmentDataGrid) getFragmentManager().findFragmentByTag(fragmentTags[3]);
            if (this.fragmentDataGrid == null) {
                this.fragmentDataGrid = new FragmentDataGrid();
            }
            this.fragmentPlots = (FragmentFigure) getFragmentManager().findFragmentByTag(fragmentTags[4]);
            if (this.fragmentPlots == null) {
                this.fragmentPlots = new FragmentFigure();
            }
        }
        getPreferences();
        applyPreferences();
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        if (cameraInfo != null) {
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == 0) {
                    this.defaultCameraId = i;
                }
            }
        }
        if (!isTablet(this)) {
            if (this.opts.layoutStyle == 1) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            getWindow().requestFeature(9);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        getOverflowMenu();
        setContentView(R.layout.mainlayout);
        if (!isTablet(this)) {
            setActionBarHider();
        }
        this.mSimulation.setUserCallback(new UserCallbackFcn() {
            public void onGPSStateChange(int i) {
                if (AttitudeIndicatorActivity.this.fragmentHorizon != null) {
                    AttitudeIndicatorActivity.this.fragmentHorizon.onGPSStatusChange(i);
                }
            }

            public void onLocationChanged(Location location) {
            }

            public void ready(PositionFilter positionFilter) {
                if (AttitudeIndicatorActivity.this.fragmentDataGrid != null && AttitudeIndicatorActivity.this.fragmentDataGrid.isVisible()) {
                    AttitudeIndicatorActivity.this.fragmentDataGrid.updateData(positionFilter);
                }
                if (AttitudeIndicatorActivity.this.fragmentPlots != null && AttitudeIndicatorActivity.this.fragmentPlots.isVisible()) {
                    AttitudeIndicatorActivity.this.fragmentPlots.pushData(positionFilter);
                }
                AttitudeIndicatorActivity.this.logging.pushData(positionFilter);
            }
        });
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        if (findViewById(R.id.fragment_container2) != null) {
            this.selectedFragment = CONTENT_HUD;
            this.useTabletLayout = TOGGLE_ON_CLICK;
//            beginTransaction.add(R.id.fragment_container1, this.fragmentGround, fragmentTags[CONTENT_GROUND]);
            beginTransaction.add(R.id.fragment_container1, this.fragmentHorizon, fragmentTags[CONTENT_GROUND]);
            if (this.opts.layoutStyle == 4 || this.opts.layoutStyle == 3) {
                beginTransaction.add(R.id.fragment_container2, this.fragmentHorizon, fragmentTags[CONTENT_HUD]);
                if (this.opts.layoutStyle == 4) {
                    beginTransaction.add(R.id.fragment_container3, this.fragmentDataGrid, fragmentTags[CONTENT_DATA]);
                }
            }
        } else {
            this.useTabletLayout = false;
            if (bundle == null) {
                this.selectedFragment = CONTENT_HUD;
                beginTransaction.add(R.id.fragment_container1, this.fragmentHorizon, fragmentTags[CONTENT_HUD]);
            } else {
                this.selectedFragment = bundle.getInt("selectedFragment");
                showFragmentContent(0, this.selectedFragment);
            }
        }
        beginTransaction.commit();
        if (bundle == null) {
            if (!this.mLocManager.isProviderEnabled("gps")) {
                buildAlertMessageNoGps();
            }
            if (!this.mSimulation.hasMinimumRequirements()) {
                DialogWithCheckBox.show(this, "Your system does not have all the required sensors for the app to work accurately!", "MINIMUM_REQUIREMENTS_CHECK");
            }
        }
        this.logging = new DataRecorder(this, this.opts.fileformat);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return TOGGLE_ON_CLICK;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return onMenuSelected(menuItem.getItemId()) ? TOGGLE_ON_CLICK : super.onOptionsItemSelected(menuItem);
    }

    protected void onPause() {
        super.onPause();
        this.mSimulation.stopSimulation();
        this.mWakeLock.release();
    }

    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        delayedHide(100);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.mLocManager.isProviderEnabled("gps")) {
            menu.findItem(R.id.lookup_groundtrack).setEnabled(TOGGLE_ON_CLICK);
        } else {
            menu.findItem(R.id.lookup_groundtrack).setEnabled(false);
        }
        if (this.useTabletLayout) {
            menu.findItem(R.id.lookup_groundtrack).setVisible(false);
            menu.findItem(R.id.lookup_infoboard).setVisible(false);
            menu.findItem(R.id.lookup_plot).setVisible(false);
        }
        if (this.logging.isRecording()) {
            menu.findItem(R.id.lookup_logging).setTitle(R.string.lookup_notlogging);
        } else {
            menu.findItem(R.id.lookup_logging).setTitle(R.string.lookup_logging);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected void onResume() {
        super.onResume();
        this.mWakeLock.acquire();
        registerReceiver(this.mBatInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        registerReceiver(this.mWifiStateReceiver, new IntentFilter("android.net.wifi.supplicant.CONNECTION_CHANGE"));
        getPreferences();
        applyPreferences();
        this.mSimulation.startSimulation();
        if (this.mCamera == null) {
            this.mCamera = Camera.open(this.defaultCameraId);
            if (this.mCamera != null) {
                this.mCamera.setDisplayOrientation(0);
            }
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("selectedFragment", this.selectedFragment);
    }

    protected void onStop() {
        super.onStop();
        unregisterReceiver(this.mBatInfoReceiver);
        unregisterReceiver(this.mWifiStateReceiver);
        if (this.alertGPSdialog != null) {
            this.alertGPSdialog.dismiss();
        }
        if (this.alertWifiDialog != null) {
            this.alertWifiDialog.dismiss();
        }
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
        if (DEBUG_TRACE) {
            Debug.stopMethodTracing();
        }
    }

    public void showCalibration() {
        Builder builder = new Builder(this);
        builder.setCancelable(TOGGLE_ON_CLICK);
        builder.setTitle("Title");
        builder.setMessage("Put the device on a horizontal surface and press START.");
        builder.setInverseBackgroundForced(TOGGLE_ON_CLICK);
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AttitudeIndicatorActivity.this.calibrateProgressDialog = new ProgressDialog(AttitudeIndicatorActivity.this);
                AttitudeIndicatorActivity.this.calibrateProgressDialog.setMessage("Wait!");
                AttitudeIndicatorActivity.this.calibrateProgressDialog.show();
                AttitudeIndicatorActivity.this.mSimulation.calibrate();
                AttitudeIndicatorActivity.this.calibrateProgressDialog.dismiss();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDisclaimer() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.about_layout);
        dialog.setTitle(R.string.app_name);
        ((TextView) dialog.findViewById(R.id.text)).setText(R.string.disclaimer_text);
        ((TextView) dialog.findViewById(R.id.textwarning)).setText(R.string.warning);
        ((ImageView) dialog.findViewById(R.id.image)).setImageResource(R.drawable.icon);
        dialog.show();
    }

    public void showFragmentContent(int i, int i2) {
        if (i2 >= 0) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            this.selectedFragment = i2;
            Fragment findFragmentByTag = fragmentManager.findFragmentByTag(fragmentTags[i2]);
            if (findFragmentByTag == null || !findFragmentByTag.isVisible()) {
                if (i2 == CONTENT_HUD) {
                    if (this.fragmentHorizon != null) {
                        beginTransaction.replace(this.viewIds[i], this.fragmentHorizon, fragmentTags[CONTENT_HUD]);
                    } else {
                        beginTransaction.show(null);
                    }
                } else if (i2 == CONTENT_GROUND) {
                } else if (i2 == CONTENT_COMPAS) {
                    if (this.fragmentCompass != null) {
                        beginTransaction.replace(this.viewIds[i], this.fragmentCompass, fragmentTags[CONTENT_COMPAS]);
                    } else {
                        beginTransaction.show(null);
                    }
                } else if (i2 == CONTENT_PLOTS) {
                    beginTransaction.replace(this.viewIds[i], this.fragmentPlots, fragmentTags[CONTENT_PLOTS]);
                } else if (i2 == CONTENT_DATA) {
                    if (this.fragmentDataGrid != null) {
                        beginTransaction.replace(this.viewIds[i], this.fragmentDataGrid, fragmentTags[CONTENT_DATA]);
                    } else {
                        beginTransaction.show(null);
                    }
                }
                beginTransaction.commit();
                return;
            }
            Toast.makeText(this, "This view is already displayed!", Toast.LENGTH_LONG).show();
        }
    }

    public void showLayout(int i) {
        View findViewById = findViewById(this.viewIds[i]);
        if (findViewById != null) {
            findViewById.setVisibility(View.VISIBLE);
        }
    }

    public void showOptionsForm() {
        startActivity(new Intent(getBaseContext(), FormOptionsList.class));
    }

    public void showPopupMenu(View view, View view2) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
        int id = ((ViewGroup) view2.getParent()).getId();
        int i = this.viewIds[0];
        int i2 = 0;
        while (i2 < this.viewIds.length) {
            if (this.viewIds[i2] == id) {
                break;
            }
            i2++;
        }
        i2 = i;
        popupMenu.getMenu().findItem(R.id.menu_add_view_left).setVisible(false);
        popupMenu.getMenu().findItem(R.id.menu_add_view_right).setVisible(false);
        if (this.useTabletLayout) {
            if (id == R.id.fragment_container1) {
                popupMenu.getMenu().findItem(R.id.menu_add_view_right).setVisible(TOGGLE_ON_CLICK);
                popupMenu.getMenu().findItem(R.id.menu_add_view_down).setVisible(TOGGLE_ON_CLICK);
            } else if (id == R.id.fragment_container2) {
                popupMenu.getMenu().findItem(R.id.menu_add_view_left).setVisible(TOGGLE_ON_CLICK);
                popupMenu.getMenu().findItem(R.id.menu_add_view_down).setVisible(TOGGLE_ON_CLICK);
            } else if (id == R.id.fragment_container3) {
                popupMenu.getMenu().findItem(R.id.menu_add_view_left).setVisible(TOGGLE_ON_CLICK);
                popupMenu.getMenu().findItem(R.id.menu_add_view_right).setVisible(TOGGLE_ON_CLICK);
            }
        }
        popupMenu.setOnMenuItemClickListener(new AnonymousClass13(i2));
        popupMenu.show();
    }

    public void startRecording() {
        this.logging.enableRecording(this.logging.isRecording() ? false : TOGGLE_ON_CLICK);
    }
}
