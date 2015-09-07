package cc.minieye.kalman.core.attitude;

import android.util.Log;
import cc.minieye.kalman.core.physics;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CQuaternion;
import cc.minieye.kalman.maths.CVector;

public final class phyAttitude implements physics {
    public static int iMRP1;
    public static int iMRP2;
    public static int iMRP3;
    private static int iOmgBx;
    private static int iOmgBy;
    private static int iOmgBz;
    private static int iOmgx;
    private static int iOmgy;
    private static int iOmgz;
    public static int nstate;
    private boolean DEBUG;
    private String TAG;
    private CQuaternion quaternion;

    static {
        nstate = 9;
        iMRP1 = 1;
        iMRP2 = 2;
        iMRP3 = 3;
        iOmgx = 4;
        iOmgy = 5;
        iOmgz = 6;
        iOmgBx = 7;
        iOmgBy = 8;
        iOmgBz = 9;
    }

    public phyAttitude() {
        this.DEBUG = false;
        this.TAG = "DynCheck";
        this.quaternion = new CQuaternion();
        if (this.DEBUG) {
            testDynamics();
            testObservation();
        }
    }

    private void testDynamics() {
        int i;
        CVector cVector = new CVector(18);
        CVector cVector2 = new CVector(18);
        for (int i2 = 1; i2 <= 18; i2++) {
            cVector.setElement(i2, 0.1d * ((double) i2));
        }
        DynamicsNonLinear(cVector, false, cVector2);
        CVector cVector3 = new CVector(18);
        CMatrix cMatrix = new CMatrix(18, 18);
        for (i = 1; i <= 18; i++) {
            CVector cVector4 = new CVector(18);
            cVector4.setElement(i, 1.0d);
            DynamicsNonLinear(cVector.plus(cVector4.times(1.0E-13d)), false, cVector3);
            cMatrix.setMatrix(1, 18, i, i, cVector3.minus(cVector2).times(1.0E13d));
        }
        CMatrix cMatrix2 = new CMatrix(18, 18);
        DynamicsTangent(cVector, cMatrix2, new CMatrix(18, 18));
        CMatrix minus = cMatrix.minus(cMatrix2);
        if (this.DEBUG) {
            for (int i3 = 1; i3 <= 18; i3++) {
                for (i = 1; i <= 18; i++) {
                    if (Math.abs(minus.Get(i3, i)) > 0.001d) {
                        Log.d(this.TAG, "row" + i3 + " col" + i + " error =" + minus.Get(i3, i) + " vnom = " + cMatrix2.Get(i3, i) + " vfdif = " + cMatrix.Get(i3, i));
                    }
                }
            }
        }
    }

    private void testObservation() {
        int i;
        CVector cVector = new CVector(18);
        CVector cVector2 = new CVector(18);
        for (int i2 = 1; i2 <= 18; i2++) {
            cVector.setElement(i2, 0.1d * ((double) i2));
        }
        ObservationNonLinear(cVector, true, cVector2);
        CVector cVector3 = new CVector(15);
        CMatrix cMatrix = new CMatrix(15, 18);
        for (i = 1; i <= 18; i++) {
            CVector cVector4 = new CVector(18);
            cVector4.setElement(i, 1.0d);
            ObservationNonLinear(cVector.plus(cVector4.times(1.0E-13d)), true, cVector3);
            cMatrix.setMatrix(1, 15, i, i, cVector3.minus(cVector2).times(1.0E13d));
        }
        CMatrix cMatrix2 = new CMatrix(15, 18);
        ObservationTangent(cVector, true, cMatrix2);
        CMatrix minus = cMatrix.minus(cMatrix2);
        if (this.DEBUG) {
            for (int i3 = 1; i3 <= 15; i3++) {
                for (i = 1; i <= 18; i++) {
                    if (Math.abs(minus.Get(i3, i)) > 0.001d) {
                        Log.d("ObsCheck", "row" + i3 + " col" + i + " error =" + minus.Get(i3, i) + " vnom = " + cMatrix2.Get(i3, i) + " vfdif = " + cMatrix.Get(i3, i));
                    }
                }
            }
        }
    }

    public void DynamicsNonLinear(CVector cVector, boolean z, CVector cVector2) {
        CVector cVector3 = new CVector(cVector.getSubVector(iMRP1, 3));
        CVector cVector4 = new CVector(cVector.getSubVector(iOmgx, 3));
        CVector cVector5 = new CVector(cVector.getSubVector(iOmgBx, 3));
        cVector2.setSubVector(iMRP1, 3, cVector4.times(-1.0d).cross(cVector3).plus(cVector5).minus(cVector5.cross(cVector3).times(0.5d)).plus(cVector3.times(cVector5.dot(cVector3) / 4.0d)));
        cVector2.setElement(iOmgx, 0.0d);
        cVector2.setElement(iOmgy, 0.0d);
        cVector2.setElement(iOmgz, 0.0d);
        cVector2.setElement(iOmgBx, 0.0d);
        cVector2.setElement(iOmgBy, 0.0d);
        cVector2.setElement(iOmgBz, 0.0d);
    }

    public void DynamicsTangent(CVector cVector, CMatrix cMatrix, CMatrix cMatrix2) {
        cMatrix.Zeros();
        CVector cVector2 = new CVector(cVector.getSubVector(iMRP1, 3));
        CVector cVector3 = new CVector(cVector.getSubVector(iOmgx, 3));
        CVector cVector4 = new CVector(cVector.getSubVector(iOmgBx, 3));
        CMatrix times = cVector3.crossProductDiff(cVector2, 2).times(-1.0d);
        CMatrix times2 = cVector4.crossProductDiff(cVector2, 2).times(-0.5d);
        CMatrix times3 = cVector2.timesMatrix(cVector4.dotProductDiff(cVector2, 2)).plus(CMatrix.Identity(3).times(cVector4.dot(cVector2))).times(0.25d);
        cMatrix.setMatrix(iMRP1, iMRP3, iMRP1, iMRP3, times.plus(times2).plus(times3));
        cMatrix.setMatrix(iMRP1, iMRP3, iOmgx, iOmgz, cVector3.crossProductDiff(cVector2, 1).times(-1.0d));
        times = cVector4.crossProductDiff(cVector2, 1).times(-0.5d);
        times2 = cVector2.timesMatrix(cVector4.dotProductDiff(cVector2, 1)).times(0.25d);
        cMatrix.setMatrix(iMRP1, iMRP3, iOmgBx, iOmgBz, CMatrix.Identity(3).plus(times).plus(times2));
        cMatrix2.setIdentity();
    }

    public void ObservationNonLinear(CVector cVector, boolean z, CVector cVector2) {
        cVector2.setSubVectorIdx(1, 3, cVector, iMRP1);
        cVector2.setSubVectorIdx(4, 3, cVector, iOmgx);
    }

    public void ObservationTangent(CVector cVector, boolean z, CMatrix cMatrix) {
        cMatrix.Set(1, iMRP1, 1.0d);
        cMatrix.Set(2, iMRP2, 1.0d);
        cMatrix.Set(3, iMRP3, 1.0d);
        cMatrix.Set(4, iOmgx, 1.0d);
        cMatrix.Set(5, iOmgy, 1.0d);
        cMatrix.Set(6, iOmgz, 1.0d);
    }

    public CMatrix diffQuaternionMRPVector(CVector cVector) {
        CMatrix cMatrix = new CMatrix(4, 3);
        double element = cVector.getElement(1);
        double element2 = cVector.getElement(2);
        double element3 = cVector.getElement(3);
        double d = 16.0d + (((element * element) + (element2 * element2)) + (element3 * element3));
        double d2 = d * d;
        cMatrix.Set(1, 1, (-(64.0d * element)) / d2);
        cMatrix.Set(1, 2, (-(64.0d * element2)) / d2);
        cMatrix.Set(1, 3, (-(64.0d * element3)) / d2);
        cMatrix.Set(2, 1, (8.0d / d) - (((16.0d * element) * element) / d2));
        cMatrix.Set(2, 2, ((16.0d * element) * element2) / d2);
        cMatrix.Set(2, 3, ((16.0d * element) * element3) / d2);
        cMatrix.Set(3, 1, ((16.0d * element2) * element) / d2);
        cMatrix.Set(3, 2, (8.0d / d) - (((16.0d * element2) * element2) / d2));
        cMatrix.Set(3, 3, ((16.0d * element2) * element3) / d2);
        cMatrix.Set(4, 1, (element * (16.0d * element3)) / d2);
        cMatrix.Set(4, 2, (element2 * (16.0d * element3)) / d2);
        cMatrix.Set(4, 3, (8.0d / d) - ((element3 * (16.0d * element3)) / d2));
        return cMatrix;
    }

    public CVector getAngularVelocity(CVector cVector) {
        return new CVector(cVector.getSubVector(iOmgx, 3));
    }

    public CQuaternion getAttitudeQuaternion() {
        return this.quaternion.copy();
    }

    public void getGyroBias(CVector cVector, CVector cVector2) {
        cVector2.setSubVectorIdx(1, 3, cVector, iOmgBx);
    }

    public int getMeasureVectorSize() {
        return 6;
    }

    public double getPitchAngle() {
        return this.quaternion.getEuler2();
    }

    public double getRollAngle() {
        return this.quaternion.getEuler1();
    }

    public double getYawAngle() {
        double euler3 = this.quaternion.getEuler3();
        return euler3;
//        return euler3 < 0.0d ? euler3 + 6.283185307179586d : euler3;
    }

    public final void quaternion2VectorParameters(CQuaternion cQuaternion, CVector cVector) {
        cVector.copy(cQuaternion.getVectorPart().times(1.0d / cQuaternion.getScalarPart()));
    }

    public void setAttitude(CQuaternion cQuaternion) {
        this.quaternion.copy(cQuaternion.times(this.quaternion));
        this.quaternion.normalize();
    }

    public void vectorParameters2Quaternion(CVector cVector, CQuaternion cQuaternion) {
        double norm = cVector.norm();
        cQuaternion.setValues(2.0d, cVector.getElement(1), cVector.getElement(2), cVector.getElement(3));
        cQuaternion.copy(cQuaternion.times(1.0d / Math.sqrt(4.0d + (norm * norm))));
        cQuaternion.normalize();
    }
}
