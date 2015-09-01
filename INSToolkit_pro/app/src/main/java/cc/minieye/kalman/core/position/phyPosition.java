package cc.minieye.kalman.core.position;

import android.util.Log;
import cc.minieye.kalman.core.physics;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CQuaternion;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.WGS84;

public final class phyPosition implements physics {
    private static double g0;
    private static int iAx;
    private static int iAy;
    private static int iAz;
    private static int iRx;
    private static int iRy;
    private static int iRz;
    private static int iVx;
    private static int iVy;
    private static int iVz;
    public static int mAx;
    public static int mAy;
    public static int mAz;
    public static int mRx;
    public static int mRy;
    public static int mRz;
    public static int mVx;
    public static int mVy;
    public static int mVz;
    private boolean DEBUG;
    final CMatrix I3;
    private CMatrix attitudeRotMat;
    public WGS84 localWGSFrame;
    private CQuaternion quaternion;

    static {
        g0 = 9.80665d;
        iAx = 1;
        iAy = 2;
        iAz = 3;
        iVx = 4;
        iVy = 5;
        iVz = 6;
        iRx = 7;
        iRy = 8;
        iRz = 9;
        mAx = 1;
        mAy = 2;
        mAz = 3;
        mVx = 4;
        mVy = 5;
        mVz = 6;
        mRx = 7;
        mRy = 8;
        mRz = 9;
    }

    public phyPosition() {
        this.DEBUG = false;
        this.I3 = new CMatrix(3, 3);
        this.quaternion = new CQuaternion();
        this.attitudeRotMat = new CMatrix(3, 3);
        this.localWGSFrame = new WGS84();
        this.I3.setIdentity();
        if (this.DEBUG) {
            testDynamics();
            testObservation();
        }
    }

    public static CVector getAcceleration(CVector cVector) {
        return new CVector(cVector.getSubVector(iAx, 3));
    }

    public static double getGravityAmplitude() {
        return g0;
    }

    public static CVector getLocalPosition(CVector cVector) {
        return new CVector(cVector.getSubVector(iRx, 3));
    }

    public static CVector getSpeed(CVector cVector) {
        return new CVector(cVector.getSubVector(iVx, 3));
    }

    public static void resetStatePosition(CVector cVector) {
        cVector.setElement(iRx, 0.0d);
        cVector.setElement(iRy, 0.0d);
        cVector.setElement(iRz, 0.0d);
    }

    public static void resetStateVelocity(CVector cVector) {
        cVector.setElement(iVx, 0.0d);
        cVector.setElement(iVy, 0.0d);
        cVector.setElement(iVz, 0.0d);
    }

    private void testDynamics() {
        int i;
        CVector cVector = new CVector(9);
        CVector cVector2 = new CVector(9);
        for (int i2 = 1; i2 <= 9; i2++) {
            cVector.setElement(i2, 0.1d * ((double) i2));
        }
        DynamicsNonLinear(cVector, true, cVector2);
        CVector cVector3 = new CVector(9);
        CMatrix cMatrix = new CMatrix(9, 9);
        for (i = 1; i <= 9; i++) {
            CVector cVector4 = new CVector(9);
            cVector4.setElement(i, 1.0d);
            DynamicsNonLinear(cVector.plus(cVector4.times(1.0E-13d)), true, cVector3);
            cMatrix.setMatrix(1, 9, i, i, cVector3.minus(cVector2).times(1.0E13d));
        }
        CMatrix cMatrix2 = new CMatrix(9, 9);
        DynamicsTangent(cVector, cMatrix2, new CMatrix(9, 9));
        CMatrix minus = cMatrix.minus(cMatrix2);
        for (int i3 = 1; i3 <= 9; i3++) {
            for (i = 1; i <= 9; i++) {
                if (Math.abs(minus.Get(i3, i)) > 0.001d) {
                    Log.d("DynCheck", "row" + i3 + " col" + i + " error =" + minus.Get(i3, i) + " vnom = " + cMatrix2.Get(i3, i) + " vfdif = " + cMatrix.Get(i3, i));
                }
            }
        }
    }

    private void testObservation() {
        int i;
        CVector cVector = new CVector(9);
        CVector cVector2 = new CVector(9);
        for (int i2 = 1; i2 <= 9; i2++) {
            cVector.setElement(i2, 0.1d * ((double) i2));
        }
        ObservationNonLinear(cVector, true, cVector2);
        CVector cVector3 = new CVector(9);
        CMatrix cMatrix = new CMatrix(9, 9);
        for (i = 1; i <= 9; i++) {
            CVector cVector4 = new CVector(9);
            cVector4.setElement(i, 1.0d);
            ObservationNonLinear(cVector.plus(cVector4.times(1.0E-13d)), true, cVector3);
            cMatrix.setMatrix(1, 9, i, i, cVector3.minus(cVector2).times(1.0E13d));
        }
        CMatrix cMatrix2 = new CMatrix(9, 9);
        ObservationTangent(cVector, true, cMatrix2);
        CMatrix minus = cMatrix.minus(cMatrix2);
        for (int i3 = 1; i3 <= 9; i3++) {
            for (i = 1; i <= 9; i++) {
                if (Math.abs(minus.Get(i3, i)) > 0.001d) {
                    Log.d("ObsCheck", "row" + i3 + " col" + i + " error =" + minus.Get(i3, i) + " vnom = " + cMatrix2.Get(i3, i) + " vfdif = " + cMatrix.Get(i3, i));
                }
            }
        }
    }

    public void DynamicsNonLinear(CVector cVector, boolean z, CVector cVector2) {
        cVector2.setElement(iAx, 0.0d);
        cVector2.setElement(iAy, 0.0d);
        cVector2.setElement(iAz, 0.0d);
        if (z) {
            cVector2.setSubVectorIdx(iRx, 3, cVector, iVx);
            CVector cVector3 = new CVector(new double[]{cVector.getElement(iAx), cVector.getElement(iAy), cVector.getElement(iAz)});
            cVector2.setSubVector(iVx, 3, cVector3);
            if (this.DEBUG) {
                Log.d("DynamicsNonLinear", "acc=" + cVector3.Transpose());
            }
        }
    }

    public void DynamicsTangent(CVector cVector, CMatrix cMatrix, CMatrix cMatrix2) {
        cMatrix.Zeros();
        cMatrix.Set(iVx, iAx, 1.0d);
        cMatrix.Set(iVy, iAy, 1.0d);
        cMatrix.Set(iVz, iAz, 1.0d);
        cMatrix.Set(iRx, iVx, 1.0d);
        cMatrix.Set(iRy, iVy, 1.0d);
        cMatrix.Set(iRz, iVz, 1.0d);
        cMatrix2.setIdentity();
    }

    public void ObservationNonLinear(CVector cVector, boolean z, CVector cVector2) {
        cVector2.setSubVector(mAx, 3, this.attitudeRotMat.times(new CVector(cVector.getSubVector(iAx, 3))));
        cVector2.setSubVectorIdx(mVx, 3, cVector, iVx);
        cVector2.setSubVectorIdx(mRx, 3, cVector, iRx);
    }

    public void ObservationTangent(CVector cVector, boolean z, CMatrix cMatrix) {
        if (z) {
            cMatrix.setMatrix(mAx, mAz, iAx, iAz, this.attitudeRotMat);
            cMatrix.Set(mVx, iVx, 1.0d);
            cMatrix.Set(mVy, iVy, 1.0d);
            cMatrix.Set(mVz, iVz, 1.0d);
            cMatrix.Set(mRx, iRx, 1.0d);
            cMatrix.Set(mRy, iRy, 1.0d);
            cMatrix.Set(mRz, iRz, 1.0d);
            return;
        }
        cMatrix.setMatrix(1, 3, iAx, iAz, this.attitudeRotMat);
    }

    public CVector getGravityInLVLH() {
        return new CVector(new double[]{0.0d, 0.0d, -g0});
    }

    public CVector getLinearAccInInertFrame(CVector cVector) {
        return this.attitudeRotMat.times(cVector).plus(getGravityInLVLH());
    }

    public int getMeasureVectorSize(boolean z) {
        return !z ? 3 : 9;
    }

    public void setAttitude(CQuaternion cQuaternion, CMatrix cMatrix) {
        this.quaternion = cQuaternion;
        this.quaternion.normalize();
        this.attitudeRotMat = cMatrix.Transpose();
    }

    public String toString() {
        return "AttitudeMat = " + this.attitudeRotMat.toString();
    }
}
