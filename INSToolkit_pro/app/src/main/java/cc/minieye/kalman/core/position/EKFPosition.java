package cc.minieye.kalman.core.position;

import android.os.Handler;
import android.util.Log;

import cc.minieye.kalman.core.CovarianceSettings;
import cc.minieye.kalman.core.MeasuresManager;
import cc.minieye.kalman.core.attitude.AttitudeFilter;
import cc.minieye.kalman.core.attitude.EKFAttitude;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;
import cc.minieye.kalman.util.WGS84;

public final class EKFPosition extends PositionFilter {
    private static boolean DEBUG_MEASURES;
    private static int STARTED;
    private static int STOPPED;
    private static int nmeas;
    private static int nstate;
    private CMatrix S;
    private CMatrix Sinv;
    private CVector acc_bias;
    private CVector biaisPosition;
    private double covAcceleration;
    private double covAngle;
    private double covCompass;
    private double covOmega;
    private double covPosition;
    private double covStateAcceleration;
    private double covStateAngle;
    private double covStateOmega;
    private double covStateOmegaBias;
    private double covStatePosition;
    private double covStateVelocity;
    private double covVelocity;
    private int initialisation_stage;
    public CVector innovation;
    private EKFAttitude kattitude;
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
    private CMatrix matSensor2Device;
    public CVector measure;
    private CVector observ;
    private long period;
    private CVector stateEstimate;
    private final CVector statePrediction;
    private final CVector stateUpdate;
    private CVector state_dot;
    private int status;
    private boolean useRK4;

    static {
        DEBUG_MEASURES = false;
        STOPPED = -1;
        STARTED = 1;
        nstate = 9;
        nmeas = 9;
    }

    public EKFPosition(double d, double d2, MeasuresManager measuresManager, int i) {
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
        this.stateUpdate = new CVector(nstate);
        this.statePrediction = new CVector(nstate);
        this.innovation = new CVector(nmeas);
        this.acc_bias = new CVector(3);
        this.covAngle = 0.001d;
        this.covCompass = 0.1d;
        this.covOmega = 0.001d;
        this.covPosition = 1.0E-4d;
        this.covVelocity = 1.0E-4d;
        this.covAcceleration = 0.07d;
        this.covStateAngle = 10000.0d;
        this.covStateOmega = 100.0d;
        this.covStateOmegaBias = 1.0E-6d;
        this.covStatePosition = 100.0d;
        this.covStateVelocity = 100.0d;
        this.covStateAcceleration = 5.0d;
        this.biaisPosition = new CVector(3);
        this.matSensor2Device = new CMatrix(3, 3);
        this.period = 20;
        this.useRK4 = true;
        this.initialisation_stage = 0;
        this.mUpdateTimeTask = new Runnable() {
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                EKFPosition.this.ekfStep(((double) (currentTimeMillis - EKFPosition.this.last_time)) / 1000.0d);
                EKFPosition.this.last_time = currentTimeMillis;
                if (EKFPosition.this.status == EKFPosition.STARTED) {
                    EKFPosition.this.mHandler.postDelayed(EKFPosition.this.mUpdateTimeTask, EKFPosition.this.period);
                }
            }
        };
        this.measuresManager = measuresManager;
        this.measuresManager.setGPSOrigin(this.OriginPosition);
        this.kattitude = new EKFAttitude(measuresManager, i);
        this.matENU2ECEF = WGS84.MatENU2ECEF(d, d2);
        setFrequency(1.0d / ((double) this.period));
        this.stateEstimate.Zeros();
        reset(false);
        this.initialisation_stage = 0;
        this.matSensor2Device = this.kattitude.matSensor2DeviceFrame();
        start();
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    private void InitState() {
        this.kattitude.InitState();
        this.matP.setIdentity();
        this.stateEstimate.setElement(1, 0.0d);
        this.stateEstimate.setElement(2, 0.0d);
        this.stateEstimate.setElement(3, 0.0d);
        this.stateEstimate.setElement(4, 0.0d);
        this.stateEstimate.setElement(5, 0.0d);
        this.stateEstimate.setElement(6, 0.0d);
        this.stateEstimate.setElement(7, 0.0d);
        this.stateEstimate.setElement(8, 0.0d);
        this.stateEstimate.setElement(9, 0.0d);
    }

    private boolean ekfCheckForAnomaly() {
        return true;
    }

    private void ekfError(double d, CVector cVector, boolean z, CVector cVector2) {
        ekfMeasurements(d, cVector, z, this.measure);
        this.physics.ObservationNonLinear(cVector, true, this.observ);
        cVector2.copy(this.measure.minus(this.observ));
    }

    private void ekfMeasurements(double d, CVector cVector, boolean z, CVector cVector2) {
        if (this.isGPSEnable) {
            CVector position = this.measuresManager.getPosition();
            CVector velocity = this.measuresManager.getVelocity();
            if (z) {
                this.matR.setDiagonal(new double[]{this.covAcceleration, this.covAcceleration, this.covAcceleration, this.covVelocity, this.covVelocity, this.covVelocity, this.covPosition, this.covPosition, this.covPosition});
            } else {
                DeadReckon.DeadReckoning(phyPosition.getAcceleration(cVector), phyPosition.getSpeed(cVector), d, position, velocity, position, velocity);
                this.matR.setDiagonal(new double[]{this.covAcceleration, this.covAcceleration, this.covAcceleration, this.covVelocity, this.covVelocity, this.covVelocity, 1000.0d * this.covPosition, 1000.0d * this.covPosition, 1000.0d * this.covPosition});
            }
            cVector2.setSubVector(phyPosition.mRx, 3, position);
            cVector2.setSubVector(phyPosition.mVx, 3, velocity);
        }
        CVector linearAccInInertFrame = this.physics.getLinearAccInInertFrame(this.measuresManager.getLinearAcceleration().minus(this.acc_bias));
        cVector2.setSubVector(phyPosition.mAx, 3, linearAccInInertFrame);
        if (DEBUG_MEASURES) {
            Log.d("DEBUG", "meas.Lin.Acc = " + linearAccInInertFrame.Transpose().toString());
        }
    }

    private void ekfPredict(CVector cVector, double d, CVector cVector2) {
        this.physics.DynamicsTangent(cVector, this.matA, this.matB);
        this.matPdot = this.matA.times(this.matP).times(this.matA.Transpose()).plus(this.matB.times(this.matQ.times(this.matB.Transpose())));
        this.matP = this.matP.plus(this.matPdot.times(d));
        this.physics.DynamicsNonLinear(cVector, this.isGPSEnable, this.state_dot);
        if (this.useRK4) {
            CVector cVector3 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector, this.isGPSEnable, cVector3);
            cVector3.timesSelf(d / 6.0d);
            CVector cVector4 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector3.times(3.0d)), this.isGPSEnable, cVector4);
            cVector4.timesSelf(d / 3.0d);
            CVector cVector5 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector4.times(1.5d)), this.isGPSEnable, cVector5);
            cVector5.timesSelf(d / 3.0d);
            CVector cVector6 = new CVector(nstate);
            this.physics.DynamicsNonLinear(cVector.plus(cVector5.times(3.0d)), this.isGPSEnable, cVector6);
            cVector6.timesSelf(d / 6.0d);
            cVector2.copy(cVector.plus(cVector3.plus(cVector4.plus(cVector5.plus(cVector6)))));
            return;
        }
        cVector2.copy(cVector.plus(this.state_dot.times(d)));
    }

    private void ekfStep(double d) {
        this.kattitude.step(d);
        if (this.isKFPositionEnable) {
            if (this.initialisation_stage == 1) {
                InitState();
                this.initialisation_stage = 2;
            } else if (this.initialisation_stage == 0) {
                return;
            }
            if (this.status == STARTED) {
                this.physics.setAttitude(this.kattitude.getAttitudeQuaternion(), this.kattitude.matLocalFrame2DevFrame());
                boolean isNewAccMeasures = this.measuresManager.isNewAccMeasures();
                boolean isNewGpsMeasures = this.measuresManager.isNewGpsMeasures();
                ekfCheckForAnomaly();
                ekfError(d, this.stateEstimate, isNewGpsMeasures, this.innovation);
                ekfUpdate(this.stateEstimate, this.innovation, isNewAccMeasures, isNewGpsMeasures, this.stateUpdate);
                ekfPredict(this.stateUpdate, d, this.statePrediction);
                this.stateEstimate.copy(this.statePrediction);
                return;
            }
            return;
        }
        this.stateEstimate.setSubVector(phyPosition.mRx, 3, this.measuresManager.getPosition());
        this.stateEstimate.setSubVector(phyPosition.mVx, 3, this.measuresManager.getVelocity());
    }

    private void ekfUpdate(CVector cVector, CVector cVector2, boolean z, boolean z2, CVector cVector3) {
        int i = 1;
        if (z) {
            this.physics.ObservationTangent(cVector, true, this.matC);
            CMatrix Transpose = this.matC.Transpose();
            this.S = this.matR.plus(this.matC.times(this.matP).times(Transpose));
            this.S.InverseSquareMat(this.Sinv);
            this.matKGain = this.matP.times(Transpose).times(this.Sinv);
            Transpose = CMatrix.Identity(nstate).minus(this.matKGain.times(this.matC));
            this.matP = Transpose.times(this.matP.times(Transpose.Transpose())).plus(this.matKGain.times(this.matR.times(this.matKGain.Transpose())));
            CVector times = this.matKGain.times(cVector2);
            int i2 = z2 ? 0 : 1;
            if (this.isGPSEnable) {
                i = 0;
            }
            if ((i2 | i) != 0) {
                phyPosition.resetStatePosition(times);
                phyPosition.resetStateVelocity(times);
            }
            cVector3.copy(cVector.plus(times));
            return;
        }
        cVector3.copy(cVector);
    }

    public AttitudeFilter getAttitude() {
        return this.kattitude;
    }

    public double getData(int i) {
        switch (i) {
            case Constants.DATA_ALT_MES:
                return getMeasureAltitude();
            case Constants.DATA_ALT_EST:
                return getEstimatedAltitude();
            case Constants.DATA_G_EST:
                return getEstimatedAcceleration().norm();
            case Constants.DATA_GPS_ACCURACY:
                return (double) getGPSAccuracy();

            case Constants.DATA_POS_X_MES:
                return this.measuresManager.getPosition().getElement(1);
            case Constants.DATA_POS_Y_MES:
                return this.measuresManager.getPosition().getElement(2);
            case Constants.DATA_POS_Z_MES:
                return this.measuresManager.getPosition().getElement(3);

            case Constants.DATA_POS_X_EST:
                return getEstimatedECEFPosition().getElement(1);
            case Constants.DATA_POS_Y_EST:
                return getEstimatedECEFPosition().getElement(2);
            case Constants.DATA_POS_Z_EST:
                return getEstimatedECEFPosition().getElement(3);

            case Constants.DATA_VEL_X_MES:
                return this.measuresManager.getVelocity().getElement(1);
            case Constants.DATA_VEL_Y_MES:
                return this.measuresManager.getVelocity().getElement(2);
            case Constants.DATA_VEL_Z_MES:
                return this.measuresManager.getVelocity().getElement(3);

            case Constants.DATA_VEL_X_EST:
                return getEstimatedSpeed().getElement(1);
            case Constants.DATA_VEL_Y_EST:
                return getEstimatedSpeed().getElement(2);
            case Constants.DATA_VEL_Z_EST:
                return getEstimatedSpeed().getElement(3);

            case Constants.DATA_ACC_X_MES:
                return this.measuresManager.getAcceleration().getElement(1);
            case Constants.DATA_ACC_Y_MES:
                return this.measuresManager.getAcceleration().getElement(2);
            case Constants.DATA_ACC_Z_MES:
                return this.measuresManager.getAcceleration().getElement(3);

            case Constants.DATA_ACC_X_EST:
                return getEstimatedAcceleration().getElement(1);
            case Constants.DATA_ACC_Y_EST:
                return getEstimatedAcceleration().getElement(2);
            case Constants.DATA_ACC_Z_EST:
                return getEstimatedAcceleration().getElement(3);

            case Constants.DATA_ACC_X_LIN:
                return this.measuresManager.getLinearAcceleration().getElement(1);
            case Constants.DATA_ACC_Y_LIN:
                return this.measuresManager.getLinearAcceleration().getElement(2);
            case Constants.DATA_ACC_Z_LIN:
                return this.measuresManager.getLinearAcceleration().getElement(3);

            case Constants.DATA_TIME:
                return (double) this.last_time;

            default:
                return this.kattitude.getData(i);
        }
    }

    public CVector getEstimateState(int i) {
        return new CVector(this.stateEstimate.getSubVector(i, 3));
    }

    public CVector getEstimatedAcceleration() {
        return phyPosition.getAcceleration(this.stateEstimate);
    }

    public CVector getEstimatedAngles() {
        return this.kattitude.getEstimatedAngles();
    }

    public CVector getEstimatedAngularVelocity() {
        return this.kattitude.getEstimatedAngularVelocity();
    }

    public CVector getEstimatedECEFPosition() {
        return this.matENU2ECEF.times(phyPosition.getLocalPosition(this.stateEstimate)).plus(this.OriginPosition);
    }

    public CVector getEstimatedRelativePosition() {
        return phyPosition.getLocalPosition(this.stateEstimate).minus(this.biaisPosition);
    }

    public CVector getEstimatedSpeed() {
        return phyPosition.getSpeed(this.stateEstimate);
    }

    public CVector getGravity() {
        return this.physics.getGravityInLVLH();
    }

    public void getMeasuredOrientation(CVector cVector) {
        this.kattitude.getMeasuredOrientation(cVector);
    }

    public CMatrix matDevFrame2LocalFrame() {
        return this.kattitude.matDevFrame2LocalFrame();
    }

    public CMatrix matLocalFrame2DevFrame() {
        return this.kattitude.matLocalFrame2DevFrame();
    }

    public void reset(boolean z) {
        this.kattitude.reset(z);
        setStateCovariances(CovarianceSettings.covStateAngle, CovarianceSettings.covStateOmega, CovarianceSettings.covStatePosition, CovarianceSettings.covStateVelocity, CovarianceSettings.covStateAcceleration, CovarianceSettings.covStateOmegaBias);
        setMeasurementCovariances(this.covAngle, this.covCompass, this.covOmega, this.covPosition, this.covVelocity, this.covAcceleration);
        if (z) {
            InitState();
            this.last_time = System.currentTimeMillis();
            this.initialTime = this.last_time;
        }
    }

    public void setAccBias(double d, double d2, double d3) {
        this.acc_bias = this.matSensor2Device.times(new CVector(new double[]{d, d2, d3}));
    }

    public void setFrequency(double d) {
        this.kattitude.setFrequency(d);
        this.period = (long) (1000.0d * (1.0d / d));
    }

    public void setGyroBias(double d, double d2, double d3) {
        this.kattitude.setGyroBias(d, d2, d3);
    }

    public void setMeasurementCovariances(double d, double d2, double d3, double d4, double d5, double d6) {
        this.kattitude.setMeasurementCovariances(d, d2, d3);
        this.covPosition = d4;
        this.covVelocity = d5;
        this.covAcceleration = d6;
        this.matR.setDiagonal(new double[]{this.covAcceleration, this.covAcceleration, this.covAcceleration, this.covVelocity, this.covVelocity, this.covVelocity, this.covPosition, this.covPosition, this.covPosition});
    }

    public void setPositionOrigin(CVector cVector, boolean z) {
        if (z) {
            phyPosition.resetStatePosition(this.stateEstimate);
        }
        this.biaisPosition.copy(phyPosition.getLocalPosition(this.stateEstimate));
        this.OriginPosition.copy(cVector);
        this.measuresManager.setGPSOrigin(cVector);
        CVector wgs84Coordinate = WGS84.wgs84Coordinate(cVector);
        this.matENU2ECEF = WGS84.MatENU2ECEF(wgs84Coordinate.getElement(1), wgs84Coordinate.getElement(2));
    }

    public void setStateCovariances(double d, double d2, double d3, double d4, double d5, double d6) {
        this.kattitude.setStateCovariances(d, d2, d6);
        this.covStatePosition = d3;
        this.covStateVelocity = d4;
        this.covStateAcceleration = d5;
        this.matQ.setDiagonal(new double[]{this.covStateAcceleration, this.covStateAcceleration, this.covStateAcceleration, this.covStateVelocity, this.covStateVelocity, this.covStateVelocity, this.covStatePosition, this.covStatePosition, this.covStatePosition});
    }

    public void start() {
        this.last_time = System.currentTimeMillis();
        this.initialTime = this.last_time;
        this.kattitude.start();
        this.initialisation_stage = 1;
        this.status = STARTED;
        this.mHandler.postDelayed(this.mUpdateTimeTask, this.period);
    }

    public void stop() {
        this.kattitude.stop();
        this.status = STOPPED;
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    public String toString() {
        CVector linearAcceleration = this.measuresManager.getLinearAcceleration();
        StringBuilder append = new StringBuilder(String.valueOf("*** KPOSITION ***\n" + "Acc measure/Dev = " + linearAcceleration.toString() + "\n")).append("Acc measure/Inert = ");
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(append.append(this.physics.getLinearAccInInertFrame(linearAcceleration).toString()).append("\n\n").toString())).append("Innovation = ").append(this.innovation.toString()).append("\n").toString())).append("Update = ").append(this.stateUpdate.toString()).append("\n").toString())).append("Prediction = ").append(this.statePrediction.toString()).append("\n").toString())).append("Measure = ").append(this.measure.toString()).append("\n").toString())).append("Observation = ").append(this.observ.toString()).append("\n").toString())).append("K =\n").append(this.matKGain.toString()).append("\n").toString())).append("P =\n").append(this.matP.toString()).append("\n").toString())).append("S =\n").append(this.S.toString()).append("\n").toString())).append("\n").toString())).append("A =\n").append(this.matA.toString()).append("\n").toString())).append("B =\n").append(this.matB.toString()).append("\n").toString())).append("C =\n").append(this.matC.toString()).append("\n").toString())).append("*** KATTITUDE ***\n").toString())).append(this.kattitude.toString()).toString();
    }
}
