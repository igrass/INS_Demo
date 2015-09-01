package cc.minieye.kalman.util;

import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;

public final class WGS84 {
    private static double e;
    private static double f;
    private static double wgs84_a;
    private static double wgs84_b;

    static {
        wgs84_a = 6378137.0d;
        wgs84_b = 6356752.3142d;
        f = (wgs84_a - wgs84_b) / wgs84_a;
        e = Math.sqrt(f * (2.0d - f));
    }

    public static CMatrix MatECEF2ENU(double d, double d2) {
        CMatrix cMatrix = new CMatrix(3, 3);
        double cos = Math.cos(d);
        double sin = Math.sin(d);
        double cos2 = Math.cos(d2);
        double sin2 = Math.sin(d2);
        cMatrix.Set(1, 1, -sin2);
        cMatrix.Set(1, 2, cos2);
        cMatrix.Set(1, 3, 0.0d);
        cMatrix.Set(2, 1, (-cos2) * sin);
        cMatrix.Set(2, 2, (-sin) * sin2);
        cMatrix.Set(2, 3, cos);
        cMatrix.Set(3, 1, cos2 * cos);
        cMatrix.Set(3, 2, cos * sin2);
        cMatrix.Set(3, 3, sin);
        return cMatrix;
    }

    public static CMatrix MatENU2ECEF(double d, double d2) {
        return MatECEF2ENU(d, d2).Transpose();
    }

    public static CVector wgs84Coordinate(double d, double d2, double d3) {
        double cos = Math.cos(d);
        double cos2 = Math.cos(d2);
        double sin = Math.sin(d);
        double sin2 = Math.sin(d2);
        double wgs84N = wgs84N(d);
        return new CVector(new double[]{cos2 * ((d3 + wgs84N) * cos), (cos * (d3 + wgs84N)) * sin2, (((1.0d - (e * e)) * wgs84N) + d3) * sin});
    }

    public static CVector wgs84Coordinate(CVector cVector) {
        double element = cVector.getElement(1);
        double element2 = cVector.getElement(2);
        double element3 = cVector.getElement(3);
        double atan2 = Math.atan2(element2, element);
        double sqrt = Math.sqrt((element * element) + (element2 * element2));
        double atan22 = Math.atan2(Math.sqrt(((element * element) + (element2 * element2)) + (element3 * element3)), element3);
        element = 0.0d;
        double d = 1.0d;
        if (Math.abs(element3) < 0.001d) {
            element2 = 0.0d;
        } else if (sqrt < 0.001d) {
            element2 = 3.141592653589793d * Math.signum(element3);
        } else {
            double d2 = atan22;
            element2 = atan22;
            int i = 0;
            while (true) {
                if (((i < 10 ? 1 : 0) & (d > 1.0E-9d ? 1 : 0)) == 0) {
                    break;
                }
                element = wgs84N(d2);
                double cos = (sqrt / Math.cos(element2)) - element;
                d = Math.atan(element3 / ((1.0d - (((e * e) * element) / (element + cos))) * sqrt));
                element = d2 - d;
                element2 = Math.sqrt(element * element);
                i++;
                d2 = d;
                element = cos;
                double d3 = element2;
                element2 = d;
                d = d3;
            }
        }
        return new CVector(new double[]{element2, atan2, element});
    }

    private static double wgs84N(double d) {
        double d2 = e * e;
        double sin = Math.sin(d);
        return wgs84_a / Math.sqrt(1.0d - ((d2 * sin) * sin));
    }

    public CVector vecLocalVertical(double d, double d2) {
        return new CVector(new double[]{0.0d, 0.0d, 1.0d});
    }
}
