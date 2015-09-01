package cc.minieye.kalman.core.attitude;

import android.os.Handler;
import cc.minieye.kalman.core.CovarianceSettings;
import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CQuaternion;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.maths.SigmaPoint;
import cc.minieye.kalman.maths.UnscentedTransformation;

public class UKFAttitude extends AttitudeFilter {
    static int STARTED;
    static int STOPPED;
    private static int nState;
    private static int nmeas;
    private static int nprocess;
    public CVector OriginPosition;
    private double[] WeightC;
    private double[] WeightM;
    public final CVector angles;
    private double covAngle;
    private double covCompass;
    private double covOmega;
    private double covStateAngle;
    private double covStateOmega;
    private double covStateOmegaBias;
    private final CVector gyro_bias;
    private int initialisation_stage;
    public final CVector innovation;
    private double kfreq;
    private Handler mHandler;
    private final CMatrix mMeasureNoiseCovariance;
    private final CMatrix mProcessNoiseCovariance;
    private CMatrix mStateCovarianceEstimate;
    private CMatrix mStateCovarianceUpdate;
    private Runnable mUpdateTimeTask;
    public final CVector measure;
    private final CVector observ;
    private long period;
    private SigmaPoint[] sigmaPoints;
    private CQuaternion stateDQuat;
    private final CVector state_dot;
    private int status;
    private final UnscentedTransformation ut;
    private final CVector vStateEstimate;
    private final CVector vStateUpdate;

    static {
        STOPPED = -1;
        STARTED = 1;
        nState = phyAttitude.nstate;
        nmeas = 6;
        nprocess = nState;
    }

    public UKFAttitude(MeasuresManager measuresManager, int i) {
        int i2 = 0;
        this.mHandler = new Handler();
        this.kfreq = 50.0d;
        this.period = 20;
        this.ut = new UnscentedTransformation();
        this.sigmaPoints = new SigmaPoint[((nState * 2) + 1)];
        this.WeightC = new double[((nState * 2) + 1)];
        this.WeightM = new double[((nState * 2) + 1)];
        this.vStateUpdate = new CVector(nState);
        this.vStateEstimate = new CVector(nState);
        this.mStateCovarianceUpdate = new CMatrix(nState, nState);
        this.mStateCovarianceEstimate = new CMatrix(nState, nState);
        this.mProcessNoiseCovariance = new CMatrix(nState, nState);
        this.mMeasureNoiseCovariance = new CMatrix(nmeas, nmeas);
        this.state_dot = new CVector(nState);
        this.observ = new CVector(nmeas);
        this.measure = new CVector(nmeas);
        this.angles = new CVector(3);
        this.innovation = new CVector(nmeas);
        this.gyro_bias = new CVector(3);
        this.stateDQuat = new CQuaternion();
        this.covAngle = 0.0010000000474974513d;
        this.covCompass = 0.009999999776482582d;
        this.covOmega = 0.0010000000474974513d;
        this.covStateAngle = CovarianceSettings.covStateAngle;
        this.covStateOmega = CovarianceSettings.covStateOmega;
        this.covStateOmegaBias = CovarianceSettings.covStateOmegaBias;
        this.OriginPosition = new CVector(3);
        this.initialisation_stage = 0;
        this.mUpdateTimeTask = new Runnable() {
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                UKFAttitude.this.ukfStep(((double) (currentTimeMillis - UKFAttitude.this.last_time)) / 1000.0d);
                UKFAttitude.this.last_time = currentTimeMillis;
                if (UKFAttitude.this.status == UKFAttitude.STARTED) {
                    UKFAttitude.this.mHandler.postDelayed(UKFAttitude.this.mUpdateTimeTask, UKFAttitude.this.period);
                }
            }
        };
        this.vStateEstimate.Zeros();
        reset(false);
        this.initialisation_stage = 0;
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        this.measuresManager = measuresManager;
        while (i2 < (nState * 2) + 1) {
            this.sigmaPoints[i2] = new SigmaPoint(nState);
            i2++;
        }
        this.ut.CalculateSigmaWeight(nState, this.WeightC, this.WeightM);
        start();
    }

    private void DynamicsNonLinear(double d, CVector cVector, boolean z, CVector cVector2) {
        this.physics.DynamicsNonLinear(cVector, false, this.state_dot);
        if (z) {
            CVector cVector3 = new CVector(nState);
            this.physics.DynamicsNonLinear(cVector, false, cVector3);
            cVector3.timesSelf(d / 6.0d);
            CVector cVector4 = new CVector(nState);
            this.physics.DynamicsNonLinear(cVector.plus(cVector3.times(3.0d)), false, cVector4);
            cVector4.timesSelf(d / 3.0d);
            CVector cVector5 = new CVector(nState);
            this.physics.DynamicsNonLinear(cVector.plus(cVector4.times(1.5d)), false, cVector5);
            cVector5.timesSelf(d / 3.0d);
            CVector cVector6 = new CVector(nState);
            this.physics.DynamicsNonLinear(cVector.plus(cVector5.times(3.0d)), false, cVector6);
            cVector6.timesSelf(d / 6.0d);
            cVector2.copy(cVector.plus(cVector3.plus(cVector4.plus(cVector5.plus(cVector6)))));
            return;
        }
        cVector2.setSubVector(1, nState, cVector.getSubVector(1, nState).plus(this.state_dot.times(d)));
    }

    private boolean ukfCheckForAnomaly() {
        if (this.measuresManager.getAcceleration().norm() / 9.806d <= 1.1d) {
            setMeasurementCovariances(this.covAngle, this.covCompass, this.covOmega);
        }
        return true;
    }

    private void ukfMeasurementUpdate(double d, SigmaPoint[] sigmaPointArr, CVector cVector, CMatrix cMatrix, double[] dArr, double[] dArr2, CVector cVector2, CMatrix cMatrix2) {
        SigmaPoint[] sigmaPointArr2 = new SigmaPoint[((nState * 2) + 1)];
        for (int i = 0; i < (nState * 2) + 1; i++) {
            sigmaPointArr2[i] = new SigmaPoint(nmeas);
            this.physics.ObservationNonLinear(sigmaPointArr[i], false, sigmaPointArr2[i]);
        }
        CVector state = this.ut.getState(dArr2, sigmaPointArr2);
        CMatrix covariance = this.ut.getCovariance(dArr, sigmaPointArr2, state, sigmaPointArr2, state);
        covariance.plusSelf(this.mMeasureNoiseCovariance);
        CMatrix covariance2 = this.ut.getCovariance(dArr, sigmaPointArr, cVector, sigmaPointArr2, state);
        CMatrix cMatrix3 = new CMatrix(nmeas, nmeas);
        covariance.InverseSquareMat(cMatrix3);
        covariance2 = covariance2.times(cMatrix3);
        cMatrix2.copyFrom(cMatrix.minus(covariance2.times(covariance.times(covariance2.Transpose()))));
        ukfMeasurements(d, cVector, this.measure);
        this.physics.ObservationNonLinear(cVector, true, this.observ);
        this.innovation.copy(this.measure.minus(this.observ));
        cVector2.copy(cVector.plus(covariance2.times(this.innovation)));
    }

    private void ukfMeasurements(double d, CVector cVector, CVector cVector2) {
        getMeasuredOrientation(this.angles);
        CQuaternion times = new CQuaternion(this.angles.getElement(1), this.angles.getElement(2), this.angles.getElement(3)).times(this.physics.getAttitudeQuaternion().inv());
        CVector cVector3 = new CVector(3);
        this.physics.quaternion2VectorParameters(times, cVector3);
        cVector2.setSubVector(1, 3, cVector3);
        this.physics.getGyroBias(cVector, this.gyro_bias);
        cVector2.setSubVector(4, 3, this.measuresManager.getRotationVector().minus(this.gyro_bias));
    }

    private void ukfStep(double d) {
        if (this.status == STARTED) {
            if (this.measuresManager.isNewAccMeasures() || this.measuresManager.isNewGyrMeasures() || !this.measuresManager.isNewMagMeasures()) {
                ukfCheckForAnomaly();
                this.ut.CalculateSigmaPoints(this.vStateEstimate, this.mStateCovarianceEstimate, nState, this.sigmaPoints);
                ukfTimeUpdate(d, this.sigmaPoints, this.WeightC, this.WeightM, this.vStateUpdate, this.mStateCovarianceUpdate);
                this.physics.vectorParameters2Quaternion(this.vStateUpdate, this.stateDQuat);
                this.physics.setAttitude(this.stateDQuat);
                this.vStateUpdate.setElement(phyAttitude.iMRP1, 0.0d);
                this.vStateUpdate.setElement(phyAttitude.iMRP2, 0.0d);
                this.vStateUpdate.setElement(phyAttitude.iMRP3, 0.0d);
                ukfMeasurementUpdate(d, this.sigmaPoints, this.vStateUpdate, this.mStateCovarianceUpdate, this.WeightC, this.WeightM, this.vStateEstimate, this.mStateCovarianceEstimate);
                this.physics.vectorParameters2Quaternion(this.vStateEstimate, this.stateDQuat);
                this.physics.setAttitude(this.stateDQuat);
            } else {
                ukfCheckForAnomaly();
                this.ut.CalculateSigmaPoints(this.vStateEstimate, this.mStateCovarianceEstimate, nState, this.sigmaPoints);
                ukfTimeUpdate(d, this.sigmaPoints, this.WeightC, this.WeightM, this.vStateUpdate, this.mStateCovarianceUpdate);
                this.physics.vectorParameters2Quaternion(this.vStateUpdate, this.stateDQuat);
                this.physics.setAttitude(this.stateDQuat);
                this.vStateUpdate.setElement(phyAttitude.iMRP1, 0.0d);
                this.vStateUpdate.setElement(phyAttitude.iMRP2, 0.0d);
                this.vStateUpdate.setElement(phyAttitude.iMRP3, 0.0d);
                ukfMeasurementUpdate(d, this.sigmaPoints, this.vStateUpdate, this.mStateCovarianceUpdate, this.WeightC, this.WeightM, this.vStateEstimate, this.mStateCovarianceEstimate);
                this.physics.vectorParameters2Quaternion(this.vStateEstimate, this.stateDQuat);
                this.physics.setAttitude(this.stateDQuat);
            }
        }
    }

    private void ukfTimeUpdate(double d, SigmaPoint[] sigmaPointArr, double[] dArr, double[] dArr2, CVector cVector, CMatrix cMatrix) {
        SigmaPoint[] sigmaPointArr2 = new SigmaPoint[((nState * 2) + 1)];
        for (int i = 0; i < (nState * 2) + 1; i++) {
            sigmaPointArr2[i] = new SigmaPoint(nState);
            DynamicsNonLinear(d, sigmaPointArr[i], false, sigmaPointArr2[i]);
        }
        cVector.copy(this.ut.getState(dArr2, sigmaPointArr2));
        cMatrix.copyFrom(this.ut.getCovariance(dArr, sigmaPointArr2, cVector, sigmaPointArr2, cVector).plus(this.mProcessNoiseCovariance));
    }

    public void InitState() {
        setGyroBias(0.0d, 0.0d, 0.0d);
        getMeasuredOrientation(this.angles);
        this.physics.setAttitude(new CQuaternion(this.angles.getElement(1), this.angles.getElement(2), this.angles.getElement(3)));
        this.vStateEstimate.setElement(1, 0.0d);
        this.vStateEstimate.setElement(2, 0.0d);
        this.vStateEstimate.setElement(3, 0.0d);
        this.vStateEstimate.setElement(4, 0.0d);
        this.vStateEstimate.setElement(5, 0.0d);
        this.vStateEstimate.setElement(6, 0.0d);
        this.vStateEstimate.setSubVector(7, 3, this.gyro_bias);
    }

    public CVector getEstimatedAngles() {
        return new CVector(new double[]{getQuaternion().getEuler1(), getQuaternion().getEuler2(), getQuaternion().getEuler3()});
    }

    public CVector getEstimatedAngularVelocity() {
        return this.physics.getAngularVelocity(this.vStateEstimate);
    }

    public CVector getEstimatedGyroBias() {
        CVector cVector = new CVector(3);
        this.physics.getGyroBias(this.vStateEstimate, cVector);
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

    public CMatrix getMeasurementCovariances() {
        return this.mMeasureNoiseCovariance;
    }

    public CMatrix getProcessCovariances() {
        return this.mProcessNoiseCovariance;
    }

    public CQuaternion getQuaternion() {
        return this.physics.getAttitudeQuaternion();
    }

    public CMatrix matDevFrame2LocalFrame() {
        return matLocalFrame2DevFrame().Transpose();
    }

    public CMatrix matLocalFrame2DevFrame() {
        return this.physics.getAttitudeQuaternion().RotationMatrix();
    }

    public void reset(boolean z) {
        this.mStateCovarianceEstimate.setIdentity();
        setProcessCovariances(CovarianceSettings.covStateAngle, CovarianceSettings.covStateOmega, CovarianceSettings.covStateOmegaBias);
        setMeasurementCovariances(this.covAngle, this.covCompass, this.covOmega);
        if (z) {
            this.last_time = System.currentTimeMillis();
            InitState();
        }
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
        this.covAngle = d;
        this.covCompass = d2;
        this.covOmega = d3;
        CMatrix cMatrix = new CMatrix(nmeas, nmeas);
        cMatrix.setDiagonal(new double[]{this.covAngle, this.covAngle, this.covCompass, this.covOmega, this.covOmega, this.covOmega});
        setMeasurementCovariances(cMatrix);
    }

    public void setMeasurementCovariances(CMatrix cMatrix) {
        this.mMeasureNoiseCovariance.copyFrom(cMatrix);
    }

    public void setProcessCovariances(double d, double d2, double d3) {
        this.covStateAngle = d * d;
        this.covStateOmega = d2 * d2;
        this.covStateOmegaBias = d3 * d3;
        CMatrix cMatrix = new CMatrix(nprocess, nprocess);
        cMatrix.setDiagonal(new double[]{this.covStateAngle, this.covStateAngle, this.covStateAngle, this.covStateOmega, this.covStateOmega, this.covStateOmega, this.covStateOmegaBias, this.covStateOmegaBias, this.covStateOmegaBias});
        setProcessCovariances(cMatrix);
    }

    public void setProcessCovariances(CMatrix cMatrix) {
        this.mProcessNoiseCovariance.copyFrom(cMatrix);
    }

    public void start() {
        this.last_time = System.currentTimeMillis();
        this.initialTime = this.last_time;
        this.initialisation_stage = 1;
        InitState();
        this.status = STARTED;
        this.mHandler.postDelayed(this.mUpdateTimeTask, this.period);
    }

    public void step(double d) {
        ukfStep(d);
    }

    public void stop() {
        this.status = STOPPED;
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }
}
