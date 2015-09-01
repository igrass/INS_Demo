package cc.minieye.kalman.core;

import cc.minieye.kalman.maths.CMatrix;

public class Frame {
    public static CMatrix getDeviceBasisMatrix(CMatrix cMatrix, int i) {
        CMatrix Identity = CMatrix.Identity(3);
        double d = i == 0 ? -1.5707963267948966d : i == 2 ? 1.5707963267948966d : i == 3 ? 3.141592653589793d : 0.0d;
        double cos = Math.cos(d);
        d = Math.sin(d);
        Identity.Set(1, 1, cos);
        Identity.Set(1, 2, -d);
        Identity.Set(1, 3, 0.0d);
        Identity.Set(2, 1, d);
        Identity.Set(2, 2, cos);
        Identity.Set(2, 3, 0.0d);
        Identity.Set(3, 1, 0.0d);
        Identity.Set(3, 2, 0.0d);
        Identity.Set(3, 3, 1.0d);
        cMatrix.Set(1, 1, 0.0d);
        cMatrix.Set(1, 2, 0.0d);
        cMatrix.Set(1, 3, -1.0d);
        cMatrix.Set(2, 1, 0.0d);
        cMatrix.Set(2, 2, -1.0d);
        cMatrix.Set(2, 3, 0.0d);
        cMatrix.Set(3, 1, 1.0d);
        cMatrix.Set(3, 2, 0.0d);
        cMatrix.Set(3, 3, 0.0d);
        cMatrix.copyFrom(cMatrix.times(Identity));
        return cMatrix;
    }
}
