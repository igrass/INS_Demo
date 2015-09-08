package cc.minieye.kalman.core;

import android.hardware.SensorEvent;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;

public final class MeasuresManager {
    public static int IACCELEROMETER;
    public static int IGPS_ACCURACY;
    public static int IGPS_BEARING;
    public static int IGPS_POSITION;
    public static int IGPS_VELOCITY;
    public static int IGYROSCOPE;
    public static int ILINACCELEROMETER;
    public static int IMAGNETOMETER;
    public static int IPRESSURE;
    public CVector OriginPosition;
    private LowPassFilter filterAcc;
    private LowPassFilter filterLinearAcc;
    private CMatrix matSensor2Device;
    private CVector mesAcc;
    private double mesBearing;
    private float mesGpsAccuracy;
    private CVector mesGpsPos;
    private CVector mesGpsVel;
    private CVector mesGyros;
    private CVector mesLinAcc;
    private CVector mesMag;
    private boolean newAccMeasure;
    private boolean newBearingMeasure;
    private boolean newGpsMeasure;
    private boolean newGyroMeasure;
    private boolean newMagMeasure;

    static {
        IACCELEROMETER = 1;
        ILINACCELEROMETER = 2;
        IGYROSCOPE = 3;
        IMAGNETOMETER = 4;
        IGPS_POSITION = 5;
        IGPS_VELOCITY = 6;
        IGPS_BEARING = 7;
        IGPS_ACCURACY = 8;
        IPRESSURE = 9;
    }

    public MeasuresManager(int i) {
        this.mesAcc = new CVector(3);
        this.filterAcc = new LowPassFilter(0.8d, 3);
        this.filterLinearAcc = new LowPassFilter(0.8d, 3);
        this.newAccMeasure = false;
        this.mesLinAcc = new CVector(3);
        this.mesGyros = new CVector(3);
        this.newGyroMeasure = false;
        this.mesMag = new CVector(3);
        this.newMagMeasure = false;
        this.mesGpsPos = new CVector(new double[]{0.0d, 0.0d, 0.0d});
        this.mesGpsVel = new CVector(new double[]{0.0d, 0.0d, 0.0d});
        this.newGpsMeasure = false;
        this.mesGpsAccuracy = 0.0f;
        this.mesBearing = 0.0d;
        this.newBearingMeasure = false;
        this.OriginPosition = new CVector(3);
        this.matSensor2Device = new CMatrix(3, 3);
        setDeviceBasisMatrix(this.matSensor2Device, i);
    }

    public static void setDeviceBasisMatrix(CMatrix cMatrix, int i) {
        Frame.getDeviceBasisMatrix(cMatrix, i);
    }

    public CVector getAcceleration() {
        return this.mesAcc;
    }

    public float getAccuracy() {
        return this.mesGpsAccuracy;
    }

    public double getBearingAngle() {
        return this.mesBearing;
    }

    public CVector getGravity() {
        return this.mesAcc.minus(this.mesLinAcc);
    }

    public CVector getLinearAcceleration() {
        return this.mesLinAcc;
    }

    public CVector getMagneticFieldVector() {
        return this.mesMag;
    }

    public CVector getPosition() {
        return this.mesGpsPos;
    }

    public CVector getRotationVector() {
        return this.mesGyros;
    }

    public CVector getVelocity() {
        return this.mesGpsVel;
    }

    public boolean isMagDisturbance() {
        double norm = this.mesMag.norm();
        return norm < 25.0d || norm > 65.0d;
    }

    public boolean isNewAccMeasures() {
        return this.newAccMeasure;
    }

    public boolean isNewGpsMeasures() {
        return this.newGpsMeasure;
    }

    public boolean isNewGyrMeasures() {
        return this.newGyroMeasure;
    }

    public boolean isNewMagMeasures() {
        return this.newMagMeasure;
    }

    public void onSensorChanged(float f, SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if (type == 1) {
            setMeasures(f, sensorEvent.values, IACCELEROMETER);
        } else if (type == 10) {
            setMeasures(f, sensorEvent.values, ILINACCELEROMETER);
        } else if (type == 4) {
            setMeasures(f, sensorEvent.values, IGYROSCOPE);
        } else if (type == 2) {
            setMeasures(f, sensorEvent.values, IMAGNETOMETER);
        } else if (type == 6) {
            setMeasures(f, sensorEvent.values[0], IPRESSURE);
        }
    }

    public void setGPSOrigin(CVector cVector) {
        this.OriginPosition.copy(cVector);
    }

    public void setMeasures(float f, float f2, int i) {
        if (i == IGPS_BEARING) {
            this.mesBearing = (double) f2;
            this.newBearingMeasure = true;
        } else if (i == IGPS_ACCURACY) {
            this.mesGpsAccuracy = f2;
        }
    }

    public void setMeasures(float f, CVector cVector, int i) {
        if (i == IACCELEROMETER) {
            this.mesAcc.copy(this.matSensor2Device.times(this.filterAcc.getValue(cVector)));
            this.newAccMeasure = true;
        } else if (i == ILINACCELEROMETER) {
            this.mesLinAcc.copy(this.matSensor2Device.times(this.filterLinearAcc.getValue(cVector)));
            this.newAccMeasure = true;
        } else if (i == IGYROSCOPE) {
            this.mesGyros.copy(this.matSensor2Device.times(cVector));
            this.newGyroMeasure = true;
        } else if (i == IMAGNETOMETER) {
            this.mesMag.copy(this.matSensor2Device.times(cVector));
            this.newMagMeasure = true;
        } else if (i == IGPS_POSITION) {
            this.mesGpsPos.copy(cVector.minus(this.OriginPosition));
        } else if (i == IGPS_VELOCITY) {
            this.mesGpsVel.copy(cVector);
        }
    }

    public void setMeasures(float f, float[] fArr, int i) {
        setMeasures(f, new CVector(new double[]{(double) fArr[0], (double) fArr[1], (double) fArr[2]}), i);
    }
}
