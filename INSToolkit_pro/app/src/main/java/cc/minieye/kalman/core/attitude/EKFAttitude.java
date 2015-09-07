package cc.minieye.kalman.core.attitude;

import android.os.Handler;

import cc.minieye.kalman.core.CovarianceSettings;
import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CQuaternion;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.WGS84;

public final class EKFAttitude extends AttitudeFilter {
    public static int IACCELEROMETER;
    public static int IGPS_BEARING;
    public static int IGPS_POSITION;
    public static int IGPS_VELOCITY;
    public static int IGYROSCOPE;
    public static int ILINACCELEROMETER;
    public static int IMAGNETOMETER;
    private static int STARTED;
    private static int STOPPED;
    private static int nmeas;
    private static int nstate;
    private double AccFreeFallMeasure;
    public CVector OriginPosition;
    private CMatrix S;
    private CMatrix Sinv;
    public CVector angles;
    private double covMeasAngle;
    private double covMeasCompass;
    private double covMeasOmega;
    private double covStateAngle;
    private double covStateOmega;
    private double covStateOmegaBias;
    private CVector gyro_bias;
    private int initialisation_stage;
    public CVector innovation;
    public WGS84 localWGSFrame;
    private Handler mHandler;
    private Runnable mUpdateTimeTask;
    private CMatrix matA;
    private CMatrix matB;
    private CMatrix matC;
    private CMatrix matKGain;
    private CMatrix matP;
    private CMatrix matPdot;
    private CMatrix matQ;
    private CMatrix matR;
    private double maxMagMeasure;
    public CVector measure;
    private double mesBearing;
    private double minMagMeasure;
    private boolean newBearingMeasure;
    private CVector observ;
    private long period;
    private CQuaternion stateDQuat;
    private CVector stateEstimate;
    private final CVector statePrediction;
    private final CVector stateUpdate;
    private CVector state_dot;
    private int status;
    private boolean useRK4;

    static {
        IACCELEROMETER = 1;
        ILINACCELEROMETER = 2;
        IGYROSCOPE = 3;
        IMAGNETOMETER = 4;
        IGPS_POSITION = 5;
        IGPS_VELOCITY = 6;
        IGPS_BEARING = 7;
        STOPPED = -1;
        STARTED = 1;
        nstate = phyAttitude.nstate;
        nmeas = 6;
    }

    public EKFAttitude(MeasuresManager measuresManager, int i) {
        this.localWGSFrame = new WGS84();
        this.mHandler = new Handler();
        this.status = STOPPED;
        this.matA = new CMatrix(nstate, nstate);
        this.matB = new CMatrix(nstate, nstate);
        this.matC = new CMatrix(nmeas, nstate);
        this.matKGain = new CMatrix(nstate, nmeas);
        this.S = new CMatrix(nmeas, nmeas);
        this.Sinv = new CMatrix(nmeas, nmeas);
        this.matQ = new CMatrix(nstate, nstate);
        this.matR = new CMatrix(nmeas, nmeas);
        this.matP = new CMatrix(nstate, nstate);
        this.matPdot = new CMatrix(nstate, nstate);
        this.stateEstimate = new CVector(nstate);
        this.state_dot = new CVector(nstate);
        this.observ = new CVector(nmeas);
        this.measure = new CVector(nmeas);
        this.angles = new CVector(3);
        this.stateUpdate = new CVector(nstate);
        this.statePrediction = new CVector(nstate);
        this.innovation = new CVector(nmeas);
        this.gyro_bias = new CVector(3);
        this.stateDQuat = new CQuaternion();
        this.covMeasAngle = CovarianceSettings.getMeasureAngle(0);
        this.covMeasCompass = CovarianceSettings.getMeasureMagneticCompass(0);
        this.covMeasOmega = CovarianceSettings.getMeasureAngularVelocity(0);
        this.covStateAngle = CovarianceSettings.covStateAngle;
        this.covStateOmega = CovarianceSettings.covStateOmega;
        this.covStateOmegaBias = CovarianceSettings.covStateOmegaBias;
        this.AccFreeFallMeasure = 9.8100004196167d;
        this.maxMagMeasure = 100.0d;
        this.minMagMeasure = 10.0d;
        this.mesBearing = 0.0d;
        this.newBearingMeasure = false;
        this.OriginPosition = new CVector(3);
        this.period = 20;
        this.useRK4 = true;
        this.initialisation_stage = 0;
        this.mUpdateTimeTask = new Runnable() {
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                EKFAttitude.this.ekfStep(((double) (currentTimeMillis - EKFAttitude.this.last_time)) / 1000.0d);
                EKFAttitude.this.last_time = currentTimeMillis;
                if (EKFAttitude.this.status == EKFAttitude.STARTED) {
                    EKFAttitude.this.mHandler.postDelayed(EKFAttitude.this.mUpdateTimeTask, EKFAttitude.this.period);
                }
            }
        };
        setFrequency(1000.0d * (1.0d / ((double) this.period)));
        this.stateEstimate.Zeros();
        reset(false);
        this.initialisation_stage = 0;
        AttitudeFilter.setDeviceBasisMatrix(this.matSensor2Device, i);
        this.measuresManager = measuresManager;
        start();
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    private boolean ekfCheckForAnomaly() {
        int i = 0;
        CVector acceleration = this.measuresManager.getAcceleration();
        CVector magneticFieldVector = this.measuresManager.getMagneticFieldVector();
        if (Math.abs((acceleration.norm() / this.AccFreeFallMeasure) - 1.0d) > 0.1d) {
            this.matR.Set(1, 1, this.covMeasAngle * 100.0d);
            this.matR.Set(2, 2, this.covMeasAngle * 100.0d);
        } else {
            setMeasurementCovariances(this.covMeasAngle, this.covMeasCompass, this.covMeasOmega);
        }
        double norm = magneticFieldVector.norm();
        int i2 = norm > this.maxMagMeasure ? 1 : 0;
        if (norm < this.minMagMeasure) {
            i = 1;
        }
        if ((i2 | i) != 0) {
            this.matR.Set(3, 3, 1000.0d * this.covMeasAngle);
        }
        return true;
    }

    private void ekfError(double d, CVector cVector, CVector cVector2) {
        ekfMeasurements(d, cVector, this.measure);
        this.physics.ObservationNonLinear(cVector, true, this.observ);
        cVector2.copy(this.measure.minus(this.observ));
    }

    private void ekfMeasurements(double d, CVector cVector, CVector cVector2) {
        getMeasuredOrientation(this.angles);
        CQuaternion times = new CQuaternion(this.angles.getElement(1), this.angles.getElement(2), this.angles.getElement(3)).times(this.physics.getAttitudeQuaternion().inv());
        CVector cVector3 = new CVector(3);
        this.physics.quaternion2VectorParameters(times, cVector3);
        cVector2.setSubVector(1, 3, cVector3);
        this.physics.getGyroBias(cVector, this.gyro_bias);
        cVector2.setSubVector(4, 3, this.measuresManager.getRotationVector().minus(this.gyro_bias));
    }

    private void ekfPredict(CVector cVector, double d, CVector cVector2) {
        this.physics.DynamicsTangent(cVector, this.matA, this.matB);
        this.matPdot = this.matA.times(this.matP).times(this.matA.Transpose()).plus(this.matB.times(this.matQ.times(this.matB.Transpose())));
        this.matP = this.matP.plus(this.matPdot.times(d));
        this.physics.DynamicsNonLinear(cVector, false, this.state_dot);
        if (this.useRK4) {
            CVector cVector3 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector, false, cVector3);
            cVector3.timesSelf(d / 6.0d);
            CVector cVector4 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector3.times(3.0d)), false, cVector4);
            cVector4.timesSelf(d / 3.0d);
            CVector cVector5 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector4.times(1.5d)), false, cVector5);
            cVector5.timesSelf(d / 3.0d);
            CVector cVector6 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector5.times(3.0d)), false, cVector6);
            cVector6.timesSelf(d / 6.0d);
            cVector2.copy(cVector.plus(cVector3.plus(cVector4.plus(cVector5.plus(cVector6)))));
            return;
        }
        cVector2.copy(cVector.plus(this.state_dot.times(d)));
    }

    private void ekfStep(double d) {
        boolean z = true;
        if (this.initialisation_stage == 1) {
            InitState();
            this.initialisation_stage = 2;
        } else if (this.initialisation_stage == 0) {
            return;
        }
        if (this.status == STARTED) {
            if (!(this.measuresManager.isNewAccMeasures() || this.measuresManager.isNewGyrMeasures() || this.measuresManager.isNewMagMeasures())) {
                z = false;
            }
            ekfCheckForAnomaly();
            ekfError(d, this.stateEstimate, this.innovation);
            ekfUpdate(this.stateEstimate, this.innovation, z, this.stateUpdate);
            this.physics.vectorParameters2Quaternion(this.stateUpdate, this.stateDQuat);
            this.physics.setAttitude(this.stateDQuat);
            this.stateUpdate.setElement(phyAttitude.iMRP1, 0.0d);
            this.stateUpdate.setElement(phyAttitude.iMRP2, 0.0d);
            this.stateUpdate.setElement(phyAttitude.iMRP3, 0.0d);
            ekfPredict(this.stateUpdate, d, this.statePrediction);
            this.physics.vectorParameters2Quaternion(this.statePrediction, this.stateDQuat);
            this.physics.setAttitude(this.stateDQuat);
            this.stateEstimate.copy(this.statePrediction);
        }
    }

    private void ekfUpdate(CVector cVector, CVector cVector2, boolean z, CVector cVector3) {
        if (z) {
            this.physics.ObservationTangent(cVector, true, this.matC);
            CMatrix Transpose = this.matC.Transpose();
            this.S = this.matR.plus(this.matC.times(this.matP).times(Transpose));
            this.S.InverseSquareMat(this.Sinv);
            this.matKGain = this.matP.times(Transpose).times(this.Sinv);
            Transpose = CMatrix.Identity(nstate).minus(this.matKGain.times(this.matC));
            this.matP = Transpose.times(this.matP.times(Transpose.Transpose())).plus(this.matKGain.times(this.matR.times(this.matKGain.Transpose())));
            cVector3.copy(cVector.plus(this.matKGain.times(cVector2)));
            return;
        }
        cVector3.copy(cVector);
    }

    public void InitState() {
        setGyroBias(0.0d, 0.0d, 0.0d);
        getMeasuredOrientation(this.angles);
        this.physics.setAttitude(new CQuaternion(this.angles.getElement(1), this.angles.getElement(2), this.angles.getElement(3)));
        this.stateEstimate.setElement(1, 0.0d);
        this.stateEstimate.setElement(2, 0.0d);
        this.stateEstimate.setElement(3, 0.0d);
        this.stateEstimate.setElement(4, 0.0d);
        this.stateEstimate.setElement(5, 0.0d);
        this.stateEstimate.setElement(6, 0.0d);
        this.stateEstimate.setSubVector(7, 3, this.gyro_bias);
    }

    public CQuaternion getAttitudeQuaternion() {
        return this.physics.getAttitudeQuaternion();
    }

    public CVector getEstimateState(int i) {
        return new CVector(this.stateEstimate.getSubVector(i, 3));
    }

    public CVector getEstimatedAngles() {
        return new CVector(new float[]{(float) getEstimatedRollAngle(), (float) getEstimatedPitchAngle(), (float) getEstimatedYawAngle()});
    }

    public CVector getEstimatedAngularVelocity() {
        return this.physics.getAngularVelocity(this.stateEstimate);
    }

    public CVector getEstimatedGyroBias() {
        CVector cVector = new CVector(3);
        this.physics.getGyroBias(this.stateEstimate, cVector);
        return cVector;
    }

    public double getEstimatedHeadingAngle() {
        CVector times = new CQuaternion(getEstimatedRollAngle(), getEstimatedPitchAngle(), 0.0d).RotationMatrix().Transpose().times(this.measuresManager.getMagneticFieldVector());
        double d = -Math.atan2(times.getElement(2), times.getElement(1));
        return d < 0.0d ? d + 6.283185307179586d : d;
    }

    public double getEstimatedPitchAngle() {
        return this.physics.getPitchAngle();
    }

    public double getEstimatedRollAngle() {
        return this.physics.getRollAngle();
    }

    public double getEstimatedYawAngle() {
        return this.physics.getYawAngle();
    }

    public CVector getMeasuredECEFPosition() {
        return this.measuresManager.getPosition().plus(this.OriginPosition);
    }

    public CVector getMeasuredLocalPosition() {
        return this.measuresManager.getPosition().minus(this.OriginPosition);
    }

    public CVector getMeasuredSpeed() {
        return this.measuresManager.getVelocity();
    }

    public CVector getMeasuredSpeedInLVLH() {
        CVector wgs84Coordinate = WGS84.wgs84Coordinate(getMeasuredECEFPosition());
        return WGS84.MatECEF2ENU(wgs84Coordinate.getElement(1), wgs84Coordinate.getElement(2)).times(this.measuresManager.getVelocity());
    }

    public CMatrix matDevFrame2LocalFrame() {
        return matLocalFrame2DevFrame().Transpose();
    }

    public CMatrix matLocalFrame2DevFrame() {
        return this.physics.getAttitudeQuaternion().RotationMatrix();
    }

    public void reset(boolean z) {
        setStateCovariances(CovarianceSettings.covStateAngle, CovarianceSettings.covStateOmega, CovarianceSettings.covStateOmegaBias);
        setMeasurementCovariances(this.covMeasAngle, this.covMeasCompass, this.covMeasOmega);
        this.matP.setIdentity();
        this.matP = this.matP.times(1000.0d);
        if (z) {
            InitState();
            this.last_time = System.currentTimeMillis();
            this.initialTime = this.last_time;
        }
    }

    public void setEstimatedAngles(double[] dArr) {
        this.physics.setAttitude(new CQuaternion(dArr[0], dArr[1], dArr[2]));
    }

    public void setFrequency(double d) {
        this.period = (long) (1000.0d * (1.0d / d));
    }

    public void setGyroBias(double d, double d2, double d3) {
        this.gyro_bias.setElement(1, d);
        this.gyro_bias.setElement(2, d2);
        this.gyro_bias.setElement(3, d3);
    }

    public void setMeasurementCovariances(double d, double d2, double d3) {
        this.covMeasAngle = d;
        this.covMeasCompass = d2;
        this.covMeasOmega = d3;
        double d4 = this.covMeasOmega;
        double d5 = this.covMeasOmega;
        double d6 = this.covMeasOmega;
        this.matR.setDiagonal(new double[]{this.covMeasAngle, this.covMeasAngle, this.covMeasCompass, d4, d5, d6});
    }

    public void setMeasurementCovariances(CMatrix cMatrix) {
        this.matR.copyFrom(cMatrix);
    }

    public void setPositionOrigin(CVector cVector, boolean z) {
        this.OriginPosition.copy(cVector);
        this.measuresManager.setGPSOrigin(this.OriginPosition);
    }

    public void setStateCovariances(double d, double d2, double d3) {
        this.covStateAngle = d;
        this.covStateOmega = d2;
        this.covStateOmegaBias = d3;
        this.matQ.setDiagonal(new double[]{this.covStateAngle, this.covStateAngle, this.covStateAngle, this.covStateOmega, this.covStateOmega, this.covStateOmega, this.covStateOmegaBias, this.covStateOmegaBias, this.covStateOmegaBias});
    }

    public void setStateCovariances(CMatrix cMatrix) {
        this.matQ.copyFrom(cMatrix);
    }

    public void start() {
        this.last_time = System.currentTimeMillis();
        this.initialTime = this.last_time;
        this.initialisation_stage = 1;
        this.status = STARTED;
        this.mHandler.postDelayed(this.mUpdateTimeTask, this.period);
    }

    public void step(double d) {
        ekfStep(d);
    }

    public void stop() {
        this.status = STOPPED;
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    public String toString() {
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("Innovation = " + this.innovation.toString() + "\n")).append("Update = ").append(this.stateUpdate.toString()).append("\n").toString())).append("Prediction = ").append(this.statePrediction.toString()).append("\n").toString())).append("Measure = ").append(this.measure.toString()).append("\n").toString())).append("Observation = ").append(this.observ.toString()).append("\n").toString())).append("K =\n").append(this.matKGain.toString()).append("\n").toString())).append("P =\n").append(this.matP.toString()).append("\n").toString())).append("S =\n").append(this.S.toString()).append("\n").toString())).append("\n").toString())).append("A =\n").append(this.matA.toString()).append("\n").toString())).append("B =\n").append(this.matB.toString()).append("\n").toString())).append("C =\n").append(this.matC.toString()).append("\n").toString())).append("\n").toString())).append("phi = ").append(this.physics.getAttitudeQuaternion().getEuler1()).append("\n").toString())).append("theta = ").append(this.physics.getAttitudeQuaternion().getEuler2()).append("\n").toString())).append("psi = ").append(this.physics.getAttitudeQuaternion().getEuler3()).append("\n").toString())).append("rotmat(f->0)=").append(matLocalFrame2DevFrame().toString()).append("\n").toString();
    }
}
