package opengl;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MatrixStack {
    private static final int DEFAULT_MAX_DEPTH = 32;
    private static final int MATRIX_SIZE = 16;
    private float[] mMatrix;
    private float[] mTemp;
    private int mTop;

    public MatrixStack() {
        commonInit(DEFAULT_MAX_DEPTH);
    }

    public MatrixStack(int i) {
        commonInit(i);
    }

    private void adjust(int i) {
        this.mTop += i * MATRIX_SIZE;
    }

    private void commonInit(int i) {
        this.mMatrix = new float[(i * MATRIX_SIZE)];
        this.mTemp = new float[DEFAULT_MAX_DEPTH];
        glLoadIdentity();
    }

    private float fixedToFloat(int i) {
        return 1.5258789E-5f * ((float) i);
    }

    private void preflight_adjust(int i) {
        int i2 = this.mTop + (i * MATRIX_SIZE);
        if (i2 < 0) {
            throw new IllegalArgumentException("stack underflow");
        } else if (i2 + MATRIX_SIZE > this.mMatrix.length) {
            throw new IllegalArgumentException("stack overflow");
        }
    }

    public void getMatrix(float[] fArr, int i) {
        System.arraycopy(this.mMatrix, this.mTop, fArr, i, MATRIX_SIZE);
    }

    public void glFrustumf(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.frustumM(this.mMatrix, this.mTop, f, f2, f3, f4, f5, f6);
    }

    public void glFrustumx(int i, int i2, int i3, int i4, int i5, int i6) {
        glFrustumf(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4), fixedToFloat(i5), fixedToFloat(i6));
    }

    public void glLoadIdentity() {
        Matrix.setIdentityM(this.mMatrix, this.mTop);
    }

    public void glLoadMatrixf(FloatBuffer floatBuffer) {
        floatBuffer.get(this.mMatrix, this.mTop, MATRIX_SIZE);
    }

    public void glLoadMatrixf(float[] fArr, int i) {
        System.arraycopy(fArr, i, this.mMatrix, this.mTop, MATRIX_SIZE);
    }

    public void glLoadMatrixx(IntBuffer intBuffer) {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            this.mMatrix[this.mTop + i] = fixedToFloat(intBuffer.get());
        }
    }

    public void glLoadMatrixx(int[] iArr, int i) {
        for (int i2 = 0; i2 < MATRIX_SIZE; i2++) {
            this.mMatrix[this.mTop + i2] = fixedToFloat(iArr[i + i2]);
        }
    }

    public void glMultMatrixf(FloatBuffer floatBuffer) {
        floatBuffer.get(this.mTemp, MATRIX_SIZE, MATRIX_SIZE);
        glMultMatrixf(this.mTemp, MATRIX_SIZE);
    }

    public void glMultMatrixf(float[] fArr, int i) {
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, 0, MATRIX_SIZE);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, 0, fArr, i);
    }

    public void glMultMatrixx(IntBuffer intBuffer) {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            this.mTemp[i + MATRIX_SIZE] = fixedToFloat(intBuffer.get());
        }
        glMultMatrixf(this.mTemp, MATRIX_SIZE);
    }

    public void glMultMatrixx(int[] iArr, int i) {
        for (int i2 = 0; i2 < MATRIX_SIZE; i2++) {
            this.mTemp[i2 + MATRIX_SIZE] = fixedToFloat(iArr[i + i2]);
        }
        glMultMatrixf(this.mTemp, MATRIX_SIZE);
    }

    public void glOrthof(float f, float f2, float f3, float f4, float f5, float f6) {
        Matrix.orthoM(this.mMatrix, this.mTop, f, f2, f3, f4, f5, f6);
    }

    public void glOrthox(int i, int i2, int i3, int i4, int i5, int i6) {
        glOrthof(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4), fixedToFloat(i5), fixedToFloat(i6));
    }

    public void glPopMatrix() {
        preflight_adjust(-1);
        adjust(-1);
    }

    public void glPushMatrix() {
        preflight_adjust(1);
        System.arraycopy(this.mMatrix, this.mTop, this.mMatrix, this.mTop + MATRIX_SIZE, MATRIX_SIZE);
        adjust(1);
    }

    public void glRotatef(float f, float f2, float f3, float f4) {
        Matrix.setRotateM(this.mTemp, 0, f, f2, f3, f4);
        System.arraycopy(this.mMatrix, this.mTop, this.mTemp, MATRIX_SIZE, MATRIX_SIZE);
        Matrix.multiplyMM(this.mMatrix, this.mTop, this.mTemp, MATRIX_SIZE, this.mTemp, 0);
    }

    public void glRotatex(int i, int i2, int i3, int i4) {
        glRotatef((float) i, fixedToFloat(i2), fixedToFloat(i3), fixedToFloat(i4));
    }

    public void glScalef(float f, float f2, float f3) {
        Matrix.scaleM(this.mMatrix, this.mTop, f, f2, f3);
    }

    public void glScalex(int i, int i2, int i3) {
        glScalef(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3));
    }

    public void glTranslatef(float f, float f2, float f3) {
        Matrix.translateM(this.mMatrix, this.mTop, f, f2, f3);
    }

    public void glTranslatex(int i, int i2, int i3) {
        glTranslatef(fixedToFloat(i), fixedToFloat(i2), fixedToFloat(i3));
    }
}
