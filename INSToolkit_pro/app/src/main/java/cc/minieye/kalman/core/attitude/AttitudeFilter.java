package cc.minieye.kalman.core.attitude;

import cc.minieye.kalman.core.Frame;
import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public abstract class AttitudeFilter {
    protected long initialTime;
    protected long last_time;
    protected CMatrix matSensor2Device;
    protected MeasuresManager measuresManager;
    protected final CVector mesAngles;
    protected phyAttitude physics;

    public AttitudeFilter() {
        this.physics = new phyAttitude();
        this.mesAngles = new CVector(3);
        this.matSensor2Device = new CMatrix(3, 3);
    }

    public static boolean getMeasuredRotationMatrix(CMatrix cMatrix, CMatrix cMatrix2, CVector cVector, CVector cVector2) {
        double element = cVector.getElement(1);
        double element2 = cVector.getElement(2);
        double element3 = cVector.getElement(3);
        double element4 = cVector2.getElement(1);
        double element5 = cVector2.getElement(2);
        double element6 = cVector2.getElement(3);
        double d = (element5 * element3) - (element6 * element2);
        double d2 = (element6 * element) - (element4 * element3);
        double d3 = (element4 * element2) - (element5 * element);
        double sqrt = Math.sqrt(((d * d) + (d2 * d2)) + (d3 * d3));
        if (sqrt < 0.10000000149011612d) {
            return false;
        }
        sqrt = 1.0d / sqrt;
        d *= sqrt;
        d2 *= sqrt;
        d3 *= sqrt;
        sqrt = 1.0d / Math.sqrt(((element * element) + (element2 * element2)) + (element3 * element3));
        element *= sqrt;
        element2 *= sqrt;
        element3 *= sqrt;
        sqrt = (element2 * d3) - (element3 * d2);
        double d4 = (element3 * d) - (element * d3);
        double d5 = (element * d2) - (element2 * d);
        if (cMatrix != null) {
            cMatrix.Set(1, 1, d);
            cMatrix.Set(1, 2, d2);
            cMatrix.Set(1, 3, d3);
            cMatrix.Set(2, 1, sqrt);
            cMatrix.Set(2, 2, d4);
            cMatrix.Set(2, 3, d5);
            cMatrix.Set(3, 1, element);
            cMatrix.Set(3, 2, element2);
            cMatrix.Set(3, 3, element3);
        }
        if (cMatrix2 != null) {
            float sqrt2 = 1.0f / ((float) Math.sqrt(((element4 * element4) + (element5 * element5)) + (element6 * element6)));
            d2 = (((element4 * sqrt) + (element5 * d4)) + (element6 * d5)) * ((double) sqrt2);
            element = (((element * element4) + (element2 * element5)) + (element6 * element3)) * ((double) sqrt2);
            cMatrix2.Set(1, 1, 1.0d);
            cMatrix2.Set(1, 2, 0.0d);
            cMatrix2.Set(1, 3, 0.0d);
            cMatrix2.Set(2, 1, 0.0d);
            cMatrix2.Set(2, 2, d2);
            cMatrix2.Set(2, 3, element);
            cMatrix2.Set(3, 1, 0.0d);
            cMatrix2.Set(3, 2, -element);
            cMatrix2.Set(3, 3, d2);
        }
        return true;
    }

    public static void getOrientation(CMatrix cMatrix, float[] fArr) {
        fArr[0] = (float) Math.atan2(cMatrix.Get(1, 2), cMatrix.Get(2, 2));
        fArr[1] = (float) Math.asin(-cMatrix.Get(3, 2));
        fArr[2] = (float) Math.atan2(-cMatrix.Get(3, 1), cMatrix.Get(3, 3));
    }

    public static void setDeviceBasisMatrix(CMatrix cMatrix, int i) {
        Frame.getDeviceBasisMatrix(cMatrix, i);
    }

    public double getData(int i) {
        if (i >= Constants.DATA_ANGLE_PITCH_RAW && i <= Constants.DATA_ANGLE_YAW_EST) {
            return i == Constants.DATA_ANGLE_PITCH_RAW ? this.mesAngles.getElement(2) * Constants.RAD2DEG : i == Constants.DATA_ANGLE_ROLL_RAW ? this.mesAngles.getElement(1) * Constants.RAD2DEG : i == Constants.DATA_ANGLE_YAW_RAW ? this.mesAngles.getElement(3) * Constants.RAD2DEG : i != Constants.DATA_ANGLE_HEAD_RAW ? i == Constants.DATA_ANGLE_PITCH_EST ? getEstimatedPitchAngle() * Constants.RAD2DEG : i == Constants.DATA_ANGLE_ROLL_EST ? getEstimatedRollAngle() * Constants.RAD2DEG : i == Constants.DATA_ANGLE_YAW_EST ? getEstimatedYawAngle() * Constants.RAD2DEG : i == Constants.DATA_ANGLE_HEAD_EST ? getEstimatedHeadingAngle() * Constants.RAD2DEG : 0.0d : 0.0d;
        } else {
            if (i >= Constants.DATA_ANGLER_PITCH_RAW && i <= Constants.DATA_ANGLER_YAW_EST) {
                return i == Constants.DATA_ANGLER_PITCH_RAW ? getMeasureGyro().getElement(2) * Constants.RAD2DEG : i == Constants.DATA_ANGLER_ROLL_RAW ? getMeasureGyro().getElement(1) * Constants.RAD2DEG : i == Constants.DATA_ANGLER_YAW_RAW ? getMeasureGyro().getElement(3) * Constants.RAD2DEG : i == Constants.DATA_ANGLER_PITCH_EST ? getEstimatedAngularVelocity().getElement(1) * Constants.RAD2DEG : i == Constants.DATA_ANGLER_ROLL_EST ? getEstimatedAngularVelocity().getElement(2) * Constants.RAD2DEG : i == Constants.DATA_ANGLER_YAW_EST ? getEstimatedAngularVelocity().getElement(3) * Constants.RAD2DEG : 0.0d;
            } else {
                if (i < Constants.DATA_MAG_X_MES || i > Constants.DATA_MAG_Z_MES) {
                    return i == Constants.DATA_TIME ? (double) this.last_time : 0.0d;
                } else {
                    CVector magneticFieldVector = this.measuresManager.getMagneticFieldVector();
                    return i == Constants.DATA_MAG_X_MES ? magneticFieldVector.getElement(1) : i == Constants.DATA_MAG_Y_MES ? magneticFieldVector.getElement(2) : i == Constants.DATA_MAG_Z_MES ? magneticFieldVector.getElement(3) : 0.0d;
                }
            }
        }
    }

    public abstract CVector getEstimatedAngularVelocity();

    public abstract double getEstimatedHeadingAngle();

    public abstract double getEstimatedPitchAngle();

    public abstract double getEstimatedRollAngle();

    public abstract double getEstimatedYawAngle();

    public CVector getMeasureGyro() {
        return this.measuresManager.getRotationVector();
    }

    public CVector getMeasureLinearAcc() {
        return this.measuresManager.getLinearAcceleration();
    }

    public CVector getMeasureMag() {
        return this.measuresManager.getMagneticFieldVector();
    }

    public CVector getMeasureTotalAcc() {
        return this.measuresManager.getAcceleration();
    }

    public void getMeasuredOrientation(CVector cVector) {
        CVector gravity = this.measuresManager.getGravity();
        CVector magneticFieldVector = this.measuresManager.getMagneticFieldVector();
        CMatrix cMatrix = new CMatrix(3, 3);
        getMeasuredRotationMatrix(cMatrix, new CMatrix(3, 3), gravity, magneticFieldVector);
        float[] fArr = new float[3];
        getOrientation(cMatrix, fArr);
        double d = (double) fArr[2];
        double d2 = (double) fArr[0];
        CVector cVector2 = new CVector(3);
        getMeasuredOrientation2(cVector2);
        this.mesAngles.setElement(2, -(-cVector2.getElement(2)));
        this.mesAngles.setElement(3, d2);
        cVector.copy(this.mesAngles);
    }

    public void getMeasuredOrientation2(CVector cVector) {
        double d = 0.0d;
        CVector gravity = this.measuresManager.getGravity();
        this.mesAngles.setElement(1, -Math.atan2(gravity.getElement(2), gravity.getElement(3)));
        double norm = gravity.norm();
        if (norm > 0.0d) {
            double element = gravity.getElement(1) / norm;
            if (Math.abs(element) < 1.0d) {
                d = Math.asin(element);
            }
        }
        this.mesAngles.setElement(2, d);
        CVector magneticFieldVector = this.measuresManager.getMagneticFieldVector();
        if (magneticFieldVector.norm() < 1.0E-6d) {
            this.mesAngles.setElement(3, Math.atan2(magneticFieldVector.getElement(2), magneticFieldVector.getElement(1)));
            cVector.copy(this.mesAngles);
        } else {
            this.mesAngles.setElement(3, Math.atan2(magneticFieldVector.getElement(2), magneticFieldVector.getElement(1)));
            cVector.copy(this.mesAngles);
        }
    }

    public double getTimeStamp() {
        return ((double) (this.last_time - this.initialTime)) / 1000.0d;
    }

    public CMatrix matSensor2DeviceFrame() {
        return this.matSensor2Device;
    }
}
