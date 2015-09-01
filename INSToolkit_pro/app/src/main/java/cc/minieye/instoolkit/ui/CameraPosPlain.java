package cc.minieye.instoolkit.ui;

import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CQuaternion;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public class CameraPosPlain {
    public static int MODE_NORMAL;
    public static int MODE_TARGET;
    public static int MODE_TRANSITION;
    private static float maxDistanceCamera;
    private static float maxZoom;
    private static float minDistanceCamera;
    private static float minZoom;
    private float camRotationSensitivity;
    private float cameraStandOff;
    private CQuaternion dquat;
    private int inCameraMode;
    private int inCameraTransitionCounter;
    private boolean isTransitionSet;
    private float maxDistanceToTarget;
    private float maxZoomToTarget;
    private float minDistanceToTarget;
    private float minZoomToTarget;
    final int nstep;
    private CQuaternion quaternion;
    private float[] vCameraPosition;
    private float[] vCameraPositionFinal;
    private float[] vCameraPositionInitial;
    private float[] vCameraTarget;
    private float vDistanceCameraCur;
    private float vDistanceCameraNom;
    private float vInitialDistance;
    private CVector viewVector;

    static {
        MODE_NORMAL = 0;
        MODE_TRANSITION = 1;
        MODE_TARGET = 2;
        minZoom = 0.01f;
        maxZoom = 5.0f;
        minDistanceCamera = 5.0f;
        maxDistanceCamera = 300.0f;
    }

    CameraPosPlain() {
        this.nstep = 100;
        this.vInitialDistance = 5.0f;
        this.vDistanceCameraCur = 5.0f;
        this.vDistanceCameraNom = this.vDistanceCameraCur;
        this.maxZoomToTarget = 10.0f;
        this.minZoomToTarget = 0.1f;
        this.vCameraPosition = new float[]{this.vDistanceCameraCur, this.vDistanceCameraCur / 1.414f, this.vDistanceCameraCur / 1.414f};
        this.vCameraPositionInitial = new float[3];
        this.vCameraPositionFinal = new float[3];
        this.cameraStandOff = 1.0f;
        this.inCameraMode = 0;
        this.vCameraTarget = new float[]{0.0f, 0.0f, 0.0f};
        this.inCameraTransitionCounter = 1;
        this.isTransitionSet = false;
        this.camRotationSensitivity = 0.5f;
        this.dquat = new CQuaternion();
        this.viewVector = new CVector(new double[]{1.0d, 0.0d, 0.0d});
    }

    private void setTransition(float[] fArr) {
        if (this.inCameraMode == MODE_TRANSITION && !this.isTransitionSet) {
            this.vCameraTarget[0] = fArr[0];
            this.vCameraTarget[1] = fArr[1];
            this.vCameraTarget[2] = fArr[2];
            float[] fArr2 = new float[]{this.vCameraTarget[0] - this.vCameraPosition[0], this.vCameraTarget[1] - this.vCameraPosition[1], this.vCameraTarget[2] - this.vCameraPosition[2]};
            float sqrt = (float) Math.sqrt((double) (((fArr2[0] * fArr2[0]) + (fArr2[1] * fArr2[1])) + (fArr2[2] * fArr2[2])));
            this.vCameraPositionFinal[0] = this.vCameraTarget[0] - ((this.cameraStandOff * fArr2[0]) / sqrt);
            this.vCameraPositionFinal[1] = this.vCameraTarget[1] - ((this.cameraStandOff * fArr2[1]) / sqrt);
            this.vCameraPositionFinal[2] = this.vCameraTarget[2] - ((fArr2[2] * this.cameraStandOff) / sqrt);
            this.maxDistanceToTarget = this.maxZoomToTarget * this.cameraStandOff;
            this.minDistanceToTarget = this.minZoomToTarget * this.cameraStandOff;
            this.isTransitionSet = true;
        }
    }

    public double getAngle1() {
        return this.dquat.getEuler1();
    }

    public double getAngle2() {
        return this.dquat.getEuler2();
    }

    public double getAngle3() {
        return this.dquat.getEuler3();
    }

    public float getCurrentDistance() {
        return this.vDistanceCameraCur;
    }

    public int getMode() {
        return this.inCameraMode;
    }

    public float[] getPosition() {
        return this.vCameraPosition;
    }

    public CMatrix getRotation() {
        return this.dquat.RotationMatrixH();
    }

    public void setCamera(float[] fArr) {
        setTransition(fArr);
        float log;
        if (this.inCameraMode == MODE_TARGET || this.inCameraMode == MODE_TRANSITION) {
            float f;
            float f2;
            if (this.inCameraMode == MODE_TRANSITION) {
                this.inCameraTransitionCounter++;
                log = ((float) Math.log((double) (1.0f + (((float) this.inCameraTransitionCounter) / 100.0f)))) / ((float) Math.log(2.0d));
                f = ((this.vCameraPositionFinal[0] - this.vCameraPositionInitial[0]) * log) + this.vCameraPositionInitial[0];
                f2 = this.vCameraPositionInitial[1] + ((this.vCameraPositionFinal[1] - this.vCameraPositionInitial[1]) * log);
                log = (log * (this.vCameraPositionFinal[2] - this.vCameraPositionInitial[2])) + this.vCameraPositionInitial[2];
            } else {
                f = this.vCameraPosition[0];
                f2 = this.vCameraPosition[1];
                log = this.vCameraPosition[2];
            }
            if (this.inCameraTransitionCounter > 100) {
                this.inCameraTransitionCounter = 1;
                this.vCameraPosition[0] = f;
                this.vCameraPosition[1] = f2;
                this.vCameraPosition[2] = log;
                this.vDistanceCameraCur = this.cameraStandOff;
                this.inCameraMode = MODE_TARGET;
            }
        } else if (this.inCameraMode == MODE_NORMAL) {
            log = this.vCameraPosition[0];
            log = this.vCameraPosition[1];
            log = this.vCameraPosition[2];
        }
    }

    public void setCameraPosition(float f, float f2, boolean z) {
        if (this.inCameraMode == MODE_NORMAL || this.inCameraMode == MODE_TARGET) {
            float f3 = (float) (Constants.DEG2RAD * ((double) (180.0f * (this.camRotationSensitivity * f))));
            float f4 = (float) (Constants.DEG2RAD * ((double) (180.0f * (this.camRotationSensitivity * f2))));
            this.dquat = new CQuaternion(Math.sin((double) f3), 0.0d, 0.0d, Math.cos((double) f3)).times(new CQuaternion(0.0d, Math.sin((double) f4), 0.0d, Math.cos((double) f4)));
            CQuaternion cQuaternion = this.dquat;
            CVector times = cQuaternion.RotationMatrixH().times(this.viewVector);
            this.vCameraPosition[0] = this.vCameraTarget[0] + ((float) (((double) this.vDistanceCameraCur) * times.getElement(1)));
            this.vCameraPosition[1] = this.vCameraTarget[1] + ((float) (((double) this.vDistanceCameraCur) * times.getElement(2)));
            this.vCameraPosition[2] = this.vCameraTarget[2] + ((float) (((double) this.vDistanceCameraCur) * times.getElement(3)));
            if (z) {
                this.quaternion = cQuaternion;
                this.viewVector = times;
            }
        }
    }

    public void setCurrentZoom(float f) {
        if (f > minZoom && f < maxZoom) {
            if (this.inCameraMode == MODE_TRANSITION || this.inCameraMode == MODE_TARGET) {
                if (this.cameraStandOff * f > this.minDistanceToTarget && this.cameraStandOff * f < this.maxDistanceToTarget) {
                    this.vDistanceCameraCur = this.cameraStandOff * f;
                    setCameraPosition(0.0f, 0.0f, false);
                }
            } else if (this.vDistanceCameraNom * f > minDistanceCamera && this.vDistanceCameraNom * f < maxDistanceCamera) {
                this.vDistanceCameraCur = this.vDistanceCameraNom * f;
                setCameraPosition(0.0f, 0.0f, false);
            }
        }
    }

    public void setFinalZoom(float f) {
        if (this.inCameraMode == MODE_TRANSITION || this.inCameraMode == MODE_TARGET) {
            if (this.cameraStandOff * f > this.maxDistanceToTarget) {
                this.cameraStandOff = this.maxDistanceToTarget;
            } else if (this.cameraStandOff * f < this.minDistanceToTarget) {
                this.cameraStandOff = this.minDistanceToTarget;
            } else {
                this.cameraStandOff *= f;
            }
        } else if (this.vDistanceCameraNom * f < minDistanceCamera) {
            this.vDistanceCameraNom = minDistanceCamera;
        } else if (this.vDistanceCameraNom * f > maxDistanceCamera) {
            this.vDistanceCameraNom = maxDistanceCamera;
        } else {
            this.vDistanceCameraNom *= f;
        }
    }

    public void setMode(int i) {
        this.inCameraMode = i;
        if (i == MODE_NORMAL) {
            this.vDistanceCameraCur = this.vInitialDistance;
            this.vCameraPosition[0] = this.vCameraPositionInitial[0];
            this.vCameraPosition[1] = this.vCameraPositionInitial[1];
            this.vCameraPosition[2] = this.vCameraPositionInitial[2];
        } else if (i == MODE_TRANSITION) {
            this.vInitialDistance = this.vDistanceCameraCur;
            this.vCameraPositionInitial[0] = this.vCameraPosition[0];
            this.vCameraPositionInitial[1] = this.vCameraPosition[1];
            this.vCameraPositionInitial[2] = this.vCameraPosition[2];
            this.isTransitionSet = false;
        }
    }

    public void setStandOff(float f) {
        this.cameraStandOff = f;
    }

    public void setTarget(float[] fArr) {
        this.vCameraTarget[0] = fArr[0];
        this.vCameraTarget[1] = fArr[1];
        this.vCameraTarget[2] = fArr[2];
    }
}
