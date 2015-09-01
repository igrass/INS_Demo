package cc.minieye.instoolkit;

import android.content.Context;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.objects.GPSListener;

public final class MyGPSListener extends GPSListener implements LocationListener {
    private boolean first_location;
    public boolean isGPS;
    private boolean isGPSFix;
    Context mContext;
    private long mInitialTimeStamp;
    private Location mLastLocation;
    private float mLastLocationMillis;
    LocationManager mLocManager;
    Simulation mSimulation;
    public Listener onGpsStatusChange;
    UserGPSListenerCallback userCallbackFcn;
    public int valGPSCounter;

    public interface UserGPSListenerCallback {
        void onGPSStateChange(int i);

        void onLocationChanged(Location location);
    }

    public MyGPSListener() {
        this.first_location = true;
        this.isGPSFix = false;
        this.mLastLocation = null;
        this.valGPSCounter = 0;
        this.userCallbackFcn = null;
    }

    public void Init(Context context, Simulation simulation, LocationManager locationManager, CVector cVector, long j) {
        Init();
        this.gpspos = cVector;
        this.mContext = context;
        this.mInitialTimeStamp = j;
        this.mSimulation = simulation;
        this.mLocManager = locationManager;
        this.mLastLocation = this.mLocManager.getLastKnownLocation("gps");
        this.onGpsStatusChange = new Listener() {
            public void onGpsStatusChanged(int i) {
                MyGPSListener.this.onGpsStatusChangedHandler(i);
            }
        };
    }

    public Location getLastLocation() {
        return this.mLastLocation;
    }

    public void onGpsStatusChangedHandler(int i) {
        boolean z = true;
        switch (i) {
            case GpsStatus.GPS_EVENT_FIRST_FIX /*3*/:
                this.isGPSFix = true;
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS /*4*/:
                if (this.mLastLocation != null) {
                    if (((float) SystemClock.elapsedRealtime()) - this.mLastLocationMillis >= 2500.0f) {
                        z = false;
                    }
                    this.isGPSFix = z;
                    break;
                }
                break;
        }
        if (this.userCallbackFcn == null) {
            return;
        }
        if (this.isGPSFix) {
            this.userCallbackFcn.onGPSStateChange(GPS_READY);
        } else {
            this.userCallbackFcn.onGPSStateChange(GPS_FIXING);
        }
    }

    public void onLocationChanged(Location location) {
        if (setLocation(location)) {
            float currentTimeMillis = (float) (((double) (System.currentTimeMillis() - this.mInitialTimeStamp)) / 1000.0d);
            this.mLastLocationMillis = (float) SystemClock.elapsedRealtime();
            if (location != null) {
                this.mLastLocation = location;
            }
            if (this.first_location) {
                this.mSimulation.getKalman().setPositionOrigin(getECEFCartesianPosition(), true);
                Toast.makeText(this.mContext, "Gps Position Updated.\nYour current position will be used to set the local frame origin.", Toast.LENGTH_LONG).show();
                this.first_location = false;
                if (this.userCallbackFcn != null) {
                    this.userCallbackFcn.onGPSStateChange(GPS_READY);
                }
            }
            this.mSimulation.setMeasures(currentTimeMillis, this.gpspos, MeasuresManager.IGPS_POSITION);
            this.mSimulation.setMeasures(currentTimeMillis, this.gpsvel, MeasuresManager.IGPS_VELOCITY);
            this.mSimulation.setMeasures(currentTimeMillis, this.gpsheading, MeasuresManager.IGPS_BEARING);
            this.mSimulation.setMeasures(currentTimeMillis, this.gpsaccuracy, MeasuresManager.IGPS_ACCURACY);
            if (this.userCallbackFcn != null) {
                this.userCallbackFcn.onLocationChanged(location);
            }
            this.valGPSCounter++;
        }
    }

    public void onProviderDisabled(String str) {
        this.mSimulation.getKalman().setGPSStatus(false);
        if (this.userCallbackFcn != null) {
            this.userCallbackFcn.onGPSStateChange(GPS_DISABLE);
        }
    }

    public void onProviderEnabled(String str) {
        this.mSimulation.getKalman().setGPSStatus(true);
        if (this.userCallbackFcn != null) {
            this.userCallbackFcn.onGPSStateChange(GPS_FIXING);
        }
    }

    public void onStatusChanged(String str, int i, Bundle bundle) {
        if (this.mLocManager != null) {
            this.isGPS = this.mLocManager.isProviderEnabled("gps");
        }
        if (this.userCallbackFcn != null) {
            this.userCallbackFcn.onGPSStateChange(GPS_FIXING);
        }
    }

    public void setCallback(UserGPSListenerCallback userGPSListenerCallback) {
        this.userCallbackFcn = userGPSListenerCallback;
    }
}
