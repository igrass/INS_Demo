package cc.minieye.kalman.util;

import cc.minieye.kalman.maths.CVector;

public class LVLH {
    public static void getVectorInENU(CVector cVector, double d, double d2, CVector cVector2) {
        cVector2.copy(WGS84.MatECEF2ENU(d, d2).times(cVector));
    }

    public static CVector getVelocityInECEF(CVector cVector, double d, double d2) {
        return WGS84.MatENU2ECEF(d, d2).times(cVector);
    }

    public static void getVelocityInENU(CVector cVector, double d, double d2, CVector cVector2) {
        CVector times = WGS84.MatECEF2ENU(d, d2).times(cVector);
        double element = times.getElement(3);
        double sqrt = Math.sqrt((times.getElement(2) * times.getElement(2)) + (times.getElement(1) * times.getElement(1)));
        cVector2.setElement(1, element);
        cVector2.setElement(2, sqrt);
    }
}
