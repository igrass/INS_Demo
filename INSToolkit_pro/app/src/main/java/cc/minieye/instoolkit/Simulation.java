package cc.minieye.instoolkit;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Locale;

import cc.minieye.instoolkit.MyGPSListener.UserGPSListenerCallback;
import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.core.position.EKFPosition;
import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.core.position.UKFPosition;
import cc.minieye.kalman.core.position.phyPosition;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;
import cc.minieye.kalman.util.WGS84;
import cc.minieye.objects.UDPServer;

public class Simulation implements SensorEventListener {
    public int DataTransferPeriod;
    public boolean allowDataTransferTask;
    private float[] calibrationData;
    private int calibrationDataSize;
    private int calibrationDuration;
    public boolean canSendDataOverWifi;
    private EKFPosition ekf;
    private boolean firstRun;
    float[] floatUDPData;
    private boolean hasFixed;
    private boolean isCalibrating;
    int kalmanId;
    private Sensor mAccelerometer;
    private Context mContext;
    private Runnable mDataTransferTask;
    private MyGPSListener mGPS;
    private int mGPSUpdatePeriod;
    private Sensor mGyroscope;
    private Handler mHandlerTimer;
    private long mInitialTimeStamp;
    private long mLastTimeStamp;
    private Sensor mLinearAccelerometer;
    private LocationManager mLocManager;
    private Sensor mLuminance;
    public double mLuminanceValue;
    private Sensor mMagneto;
    private Sensor mPressure;
    public double mPressureValue;
    private SensorManager mSensorManager;
    private Sensor mTemperature;
    public double mTemperatureValue;
    private MeasuresManager measuresManager;
    private UDPServer udp;
    private UKFPosition ukf;
    UserCallbackFcn userCallbackFcn;
    private int vSensorDelayRate;
    public double valAccelerometerFrequency;
    public double valGPSFrequency;
    public double valGyroscopeFrequency;
    public double valMagnetometerFrequency;

    public interface UserCallbackFcn {
        void onGPSStateChange(int i);

        void onLocationChanged(Location location);

        void ready(PositionFilter positionFilter);
    }

    public interface OnDataReadyListener {
        void onDataReady();
    }

    public Simulation(Context context, SensorManager sensorManager, LocationManager locationManager, int i) {
        this.floatUDPData = new float[15];
        this.canSendDataOverWifi = false;
        this.udp = null;
        this.userCallbackFcn = null;
        this.kalmanId = 1;
        this.mHandlerTimer = new Handler();
        this.mGPS = new MyGPSListener();
        this.mGPSUpdatePeriod = 100;
        this.valAccelerometerFrequency = 0.0d;
        this.valGyroscopeFrequency = 0.0d;
        this.valMagnetometerFrequency = 0.0d;
        this.valGPSFrequency = 0.0d;
        this.DataTransferPeriod = 200;
        this.allowDataTransferTask = true;
        this.mLastTimeStamp = 0;
        this.vSensorDelayRate = SensorManager.SENSOR_DELAY_UI;
        this.firstRun = true;
        this.calibrationData = new float[3];
        this.calibrationDataSize = 0;
        this.isCalibrating = false;
        this.calibrationDuration = 3000;
        this.mDataTransferTask = new Runnable() {
            public void run() {
                if (Simulation.this.userCallbackFcn != null) {
                    Simulation.this.userCallbackFcn.ready(Simulation.this.getKalman());
                }
                if (Simulation.this.udp != null && Simulation.this.canSendDataOverWifi) {
                    Simulation.this.floatUDPData[0] = (float) Simulation.this.getKalman().getData(Constants.DATA_TIME);
                    Simulation.this.floatUDPData[1] = (float) Simulation.this.getKalman().getData(Constants.DATA_ACC_X_MES);
                    Simulation.this.floatUDPData[2] = (float) Simulation.this.getKalman().getData(Constants.DATA_ACC_Y_MES);
                    Simulation.this.floatUDPData[3] = (float) Simulation.this.getKalman().getData(Constants.DATA_ACC_Z_MES);
                    Simulation.this.floatUDPData[4] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLER_PITCH_RAW);
                    Simulation.this.floatUDPData[5] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLER_ROLL_RAW);
                    Simulation.this.floatUDPData[6] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLER_YAW_RAW);
                    Simulation.this.floatUDPData[7] = (float) Simulation.this.getKalman().getData(Constants.DATA_MAG_X_MES);
                    Simulation.this.floatUDPData[8] = (float) Simulation.this.getKalman().getData(Constants.DATA_MAG_Y_MES);
                    Simulation.this.floatUDPData[9] = (float) Simulation.this.getKalman().getData(Constants.DATA_MAG_Z_MES);
                    Simulation.this.floatUDPData[10] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLE_PITCH_EST);
                    Simulation.this.floatUDPData[11] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLE_ROLL_EST);
                    Simulation.this.floatUDPData[12] = (float) Simulation.this.getKalman().getData(Constants.DATA_ANGLE_YAW_EST);
                    Simulation.this.udp.send(Simulation.this.floatUDPData);
                }
                if (Simulation.this.DataTransferPeriod > 0 && Simulation.this.allowDataTransferTask) {
                    Simulation.this.mHandlerTimer.postDelayed(Simulation.this.mDataTransferTask, (long) Simulation.this.DataTransferPeriod);
                }
            }
        };
        this.mContext = context;
        this.mSensorManager = sensorManager;
        this.mLocManager = locationManager;
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
        this.mLinearAccelerometer = this.mSensorManager.getDefaultSensor(10);
        this.mGyroscope = this.mSensorManager.getDefaultSensor(4);
        this.mMagneto = this.mSensorManager.getDefaultSensor(2);
        this.mTemperature = this.mSensorManager.getDefaultSensor(13);
        this.mLuminance = this.mSensorManager.getDefaultSensor(5);
        this.mPressure = this.mSensorManager.getDefaultSensor(6);
        this.measuresManager = new MeasuresManager(i);
        this.ekf = new EKFPosition(0.0d, 0.0d, this.measuresManager, i);
        this.ukf = new UKFPosition(0.0d, 0.0d, this.measuresManager, i);
        this.mInitialTimeStamp = System.currentTimeMillis();
    }

    public void calibrate() {
        this.isCalibrating = true;
        this.calibrationDataSize = 0;
        float[] fArr = this.calibrationData;
        float[] fArr2 = this.calibrationData;
        this.calibrationData[2] = 0.0f;
        fArr2[1] = 0.0f;
        fArr[0] = 0.0f;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Simulation.this.isCalibrating = false;
                if (Simulation.this.calibrationDataSize > 0) {
                    float[] fArr = new float[]{Simulation.this.calibrationData[0] / ((float) Simulation.this.calibrationDataSize), Simulation.this.calibrationData[1] / ((float) Simulation.this.calibrationDataSize), Simulation.this.calibrationData[2] / ((float) Simulation.this.calibrationDataSize)};
                    fArr[2] = (float) (((double) fArr[2]) - phyPosition.getGravityAmplitude());
                    Toast.makeText(Simulation.this.mContext, String.format(Locale.US, "biais = [%f %f %f] (n=%d)", new Object[]{Float.valueOf(fArr[0]), Float.valueOf(fArr[1]), Float.valueOf(fArr[2]), Integer.valueOf(Simulation.this.calibrationDataSize)}), Toast.LENGTH_SHORT).show();
                    Simulation.this.ekf.setAccBias((double) fArr[0], (double) fArr[1], (double) fArr[2]);
                    Simulation.this.ukf.setAccBias((double) fArr[0], (double) fArr[1], (double) fArr[2]);
                    Editor edit = PreferenceManager.getDefaultSharedPreferences(Simulation.this.mContext).edit();
                    edit.putFloat("accbiasX", fArr[0]);
                    edit.putFloat("accbiasY", fArr[1]);
                    edit.putFloat("accbiasZ", fArr[2]);
                    edit.commit();
                }
            }
        }, (long) this.calibrationDuration);
    }

    public void closeUDP() {
        if (this.udp != null) {
            this.udp.close();
            this.udp = null;
        }
    }

    public MyGPSListener getGPS() {
        return this.mGPS;
    }

    public PositionFilter getKalman() {
        return this.kalmanId == 2 ? this.ukf : this.ekf;
    }

    public int getKalmanId() {
        return this.kalmanId;
    }

    public MeasuresManager getMeasureManager() {
        return this.measuresManager;
    }

    public boolean hasMinimumRequirements() {
        return (this.mAccelerometer == null || this.mLinearAccelerometer == null || this.mGyroscope == null || this.mMagneto == null) ? false : true;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        float currentTimeMillis = (float) (((double) (System.currentTimeMillis() - this.mInitialTimeStamp)) / 1000.0d);
        int type = sensorEvent.sensor.getType();
        float f = (float) (((double) sensorEvent.timestamp) / 1.0E9d);
        int i = sensorEvent.accuracy;
        this.measuresManager.onSensorChanged(currentTimeMillis, sensorEvent);
        if (type == 1) {
            if (this.isCalibrating && i == 3) {
                this.calibrationData[0] = this.calibrationData[0] + sensorEvent.values[0];
                this.calibrationData[1] = this.calibrationData[1] + sensorEvent.values[1];
                this.calibrationData[2] = this.calibrationData[2] + sensorEvent.values[2];
                this.calibrationDataSize++;
            }
        } else if (type == 13) {
            this.mTemperatureValue = (double) sensorEvent.values[0];
        } else if (type == 5) {
            this.mLuminanceValue = (double) sensorEvent.values[0];
        } else if (type == 6) {
            this.mPressureValue = (double) sensorEvent.values[0];
        }
    }

    public boolean openUDP(int i, String str) {
        if (this.udp == null) {
            this.udp = new UDPServer(i, str);
        }
        if (this.udp.isOpen()) {
            this.udp.close();
            this.udp.set(i, str);
        }
        return this.udp.open();
    }

    public void setKalmanId(int i) {
        this.kalmanId = i;
    }

    public void setMeasures(float f, float f2, int i) {
        this.measuresManager.setMeasures(f, f2, i);
    }

    public void setMeasures(float f, CVector cVector, int i) {
        this.measuresManager.setMeasures(f, cVector, i);
    }

    public void setPower(int i) {
        int[] iArr = new int[3];
        iArr[0] = SensorManager.SENSOR_DELAY_NORMAL;
        iArr[1] = SensorManager.SENSOR_DELAY_UI;
        iArr[2] = SensorManager.SENSOR_DELAY_FASTEST;
        if (i <= 2) {
            this.vSensorDelayRate = iArr[i];
        }
    }

    public void setUserCallback(UserCallbackFcn userCallbackFcn) {
        this.userCallbackFcn = userCallbackFcn;
        if (userCallbackFcn != null) {
            this.allowDataTransferTask = true;
        }
        this.mHandlerTimer.removeCallbacks(this.mDataTransferTask);
        this.mHandlerTimer.postDelayed(this.mDataTransferTask, 1);
    }

    public void startSimulation() {
        if (this.mAccelerometer != null) {
            this.mSensorManager.registerListener(this, this.mAccelerometer, this.vSensorDelayRate);
        }
        if (this.mLinearAccelerometer != null) {
            this.mSensorManager.registerListener(this, this.mLinearAccelerometer, this.vSensorDelayRate);
        }
        if (this.mGyroscope != null) {
            this.mSensorManager.registerListener(this, this.mGyroscope, this.vSensorDelayRate);
        }
        if (this.mMagneto != null) {
            this.mSensorManager.registerListener(this, this.mMagneto, this.vSensorDelayRate);
        }
        if (this.mTemperature != null) {
            this.mSensorManager.registerListener(this, this.mTemperature, 3);
        }
        if (this.mLuminance != null) {
            this.mSensorManager.registerListener(this, this.mLuminance, 3);
        }
        if (this.mPressure != null) {
            this.mSensorManager.registerListener(this, this.mPressure, 3);
        }
        LocationManager locationManager = this.mLocManager;
        this.mGPS.Init(this.mContext, this, locationManager, WGS84.wgs84Coordinate(0.0d, 0.0d, 0.0d), this.mInitialTimeStamp);
        this.mGPS.setCallback(new UserGPSListenerCallback() {
            public void onGPSStateChange(int i) {
                Simulation.this.userCallbackFcn.onGPSStateChange(i);
            }

            public void onLocationChanged(Location location) {
                Simulation.this.userCallbackFcn.onLocationChanged(location);
            }
        });
        this.mLocManager.requestLocationUpdates("gps", (long) this.mGPSUpdatePeriod, 0.0f, this.mGPS);
        this.mLocManager.addGpsStatusListener(this.mGPS.onGpsStatusChange);
        boolean isProviderEnabled = this.mLocManager.isProviderEnabled("gps");
        this.ekf.setGPSStatus(isProviderEnabled);
        this.ukf.setGPSStatus(isProviderEnabled);
        Location location = null;
        if (this.firstRun) {
            location = null;
            if (isProviderEnabled) {
                location = this.mLocManager.getLastKnownLocation("gps");
            }
        }
        if (location == null) {
            location = new Location("gps");
            location.setAltitude(0.0d);
            location.setLongitude(0.0d);
            location.setLatitude(0.0d);
        }
        this.mGPS.setLocation(location);
        this.ekf.setPositionOrigin(this.mGPS.getPosition(), false);
        this.ukf.setPositionOrigin(this.mGPS.getPosition(), false);
        this.measuresManager.setMeasures(0.0f, this.mGPS.getPosition(), MeasuresManager.IGPS_POSITION);
        this.measuresManager.setMeasures(0.0f, new CVector(3), MeasuresManager.IGPS_VELOCITY);
        this.firstRun = false;
        this.mHandlerTimer.removeCallbacks(this.mDataTransferTask);
        this.allowDataTransferTask = true;
        this.DataTransferPeriod = 50;
        this.mHandlerTimer.postDelayed(this.mDataTransferTask, 1);
        getKalman().start();
    }

    public void stopSimulation() {
        this.mSensorManager.unregisterListener(this);
        this.mLocManager.removeUpdates(this.mGPS);
        this.mHandlerTimer.removeCallbacks(this.mDataTransferTask);
        this.allowDataTransferTask = false;
        getKalman().stop();
    }
}
