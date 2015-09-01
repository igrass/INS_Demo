package cc.minieye.kalman.core.position;

import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.core.attitude.AttitudeFilter;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.WGS84;

public abstract class PositionFilter {
    public CVector OriginPosition;
    protected long initialTime;
    public boolean isGPSEnable;
    public boolean isKFPositionEnable;
    protected long last_time;
    public CMatrix matENU2ECEF;
    public MeasuresManager measuresManager;
    public phyPosition physics;

    public PositionFilter() {
        this.isGPSEnable = false;
        this.isKFPositionEnable = false;
        this.measuresManager = null;
        this.physics = new phyPosition();
        this.matENU2ECEF = null;
        this.OriginPosition = new CVector(3);
        this.initialTime = 0;
    }

    public void enablePositionFilter(boolean z) {
        this.isKFPositionEnable = z;
    }

    public long getAbsoluteTime() {
        return this.last_time;
    }

    public abstract AttitudeFilter getAttitude();

    public double getBearing() {
        return this.measuresManager.getBearingAngle();
    }

    public abstract double getData(int i);

    public abstract CVector getEstimateState(int i);

    public abstract CVector getEstimatedAcceleration();

    public double getEstimatedAltitude() {
        return WGS84.wgs84Coordinate(getEstimatedECEFPosition()).getElement(3);
    }

    public abstract CVector getEstimatedAngles();

    public abstract CVector getEstimatedAngularVelocity();

    public abstract CVector getEstimatedECEFPosition();

    public CVector getEstimatedLatLonAlt() {
        return WGS84.wgs84Coordinate(getEstimatedECEFPosition());
    }

    public abstract CVector getEstimatedRelativePosition();

    public abstract CVector getEstimatedSpeed();

    public float getGPSAccuracy() {
        return this.measuresManager.getAccuracy();
    }

    public abstract CVector getGravity();

    public double getMeasureAltitude() {
        return WGS84.wgs84Coordinate(this.measuresManager.getPosition().plus(this.OriginPosition)).getElement(3);
    }

    public CVector getMeasureLatLonAlt() {
        return WGS84.wgs84Coordinate(this.measuresManager.getPosition().plus(this.OriginPosition));
    }

    public CVector getMeasureLinearAcc() {
        return this.measuresManager.getLinearAcceleration();
    }

    public CVector getMeasurePosition() {
        return this.measuresManager.getPosition();
    }

    public CVector getMeasureTotalAcc() {
        return this.measuresManager.getAcceleration();
    }

    public CVector getMeasureTotalAccInertial() {
        return matDevFrame2LocalFrame().times(this.measuresManager.getAcceleration());
    }

    public CVector getMeasureVelocity() {
        return this.measuresManager.getVelocity();
    }

    public double getTimeStamp() {
        return ((double) (this.last_time - this.initialTime)) / 1000.0d;
    }

    public boolean isEnable() {
        return this.isKFPositionEnable;
    }

    public abstract CMatrix matDevFrame2LocalFrame();

    public abstract CMatrix matLocalFrame2DevFrame();

    public abstract void reset(boolean z);

    public abstract void setAccBias(double d, double d2, double d3);

    public abstract void setFrequency(double d);

    public void setGPSStatus(boolean z) {
        this.isGPSEnable = z;
    }

    public abstract void setMeasurementCovariances(double d, double d2, double d3, double d4, double d5, double d6);

    public abstract void setPositionOrigin(CVector cVector, boolean z);

    public abstract void start();

    public abstract void stop();
}
