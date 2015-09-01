package cc.minieye.kalman.core.position;

import cc.minieye.kalman.maths.CVector;

public class DeadReckon {
    public static void DeadReckoning(CVector cVector, CVector cVector2, double d, CVector cVector3, CVector cVector4, CVector cVector5, CVector cVector6) {
        cVector5.copy(cVector3.plus(cVector4.times(d)));
        cVector6.copy(cVector4.plus(cVector.times(d)));
    }
}
