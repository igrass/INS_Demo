package cc.minieye.kalman.core;

public class CovarianceSettings {
    static double accSTDEV;
    static double biasVAR;
    public static double covStateAcceleration;
    public static double covStateAngle;
    public static double covStateOmega;
    public static double covStateOmegaBias;
    public static double covStatePosition;
    public static double covStateVelocity;
    static double gyroVAR;
    static double magSTDEV;
    private static double[] valAcceleration;
    private static double[] valAltitude;
    private static double[] valAngle;
    private static double[] valCompass;
    private static double[] valOmega;
    private static double[] valPosition;
    private static double[] valVelocity;
    private static double[] valVerticalVelocity;

    static {
        covStateAngle = 5.0d;
        covStateOmega = 0.01d;
        covStateOmegaBias = 1.0E-6d;
        covStatePosition = 10.0d;
        covStateVelocity = 10.0d;
        covStateAcceleration = 5.0d;
        gyroVAR = 1.0E-7d;
        biasVAR = 1.0E-8d;
        accSTDEV = 0.05000000074505806d;
        magSTDEV = 0.5d;
        valAngle = new double[]{50.0d, 100.0d, 100000.0d, 1.0d};
        valCompass = new double[]{100.0d, 100.0d, 100000.0d, 10.0d};
        valOmega = new double[]{0.01d, 0.1d, 0.001d, 1000000.0d};
        valPosition = new double[]{0.001d, 0.1d, 0.1d, 0.1d};
        valAltitude = new double[]{0.01d, 5.0d, 5.0d, 5.0d};
        valVelocity = new double[]{0.001d, 0.1d, 0.1d, 0.1d};
        valVerticalVelocity = new double[]{0.01d, 10.0d, 10.0d, 10.0d};
        valAcceleration = new double[]{0.01d, 1.0d, 1.0d, 1.0d};
    }

    public static double getMeasurGPSGroundVelocity(int i) {
        return valVelocity[i];
    }

    public static double getMeasurGPSVerticalVelocity(int i) {
        return valVerticalVelocity[i];
    }

    public static double getMeasureAcceleration(int i) {
        return valAcceleration[i];
    }

    public static double getMeasureAngle(int i) {
        return valAngle[i];
    }

    public static double getMeasureAngularVelocity(int i) {
        return valOmega[i];
    }

    public static double getMeasureGPSAltitude(int i) {
        return valAltitude[i];
    }

    public static double getMeasureGPSGroundPosition(int i) {
        return valPosition[i];
    }

    public static double getMeasureMagneticCompass(int i) {
        return valCompass[i];
    }
}
