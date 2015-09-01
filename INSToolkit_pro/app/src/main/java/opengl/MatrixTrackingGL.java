package opengl;

import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public class MatrixTrackingGL implements GL, GL10, GL10Ext, GL11, GL11Ext {
    private static final boolean _check = false;
    ByteBuffer mByteBuffer;
    float[] mCheckA;
    float[] mCheckB;
    private MatrixStack mCurrent;
    FloatBuffer mFloatBuffer;
    private int mMatrixMode;
    private MatrixStack mModelView;
    private MatrixStack mProjection;
    private MatrixStack mTexture;
    private GL10 mgl;
    private GL10Ext mgl10Ext;
    private GL11 mgl11;
    private GL11Ext mgl11Ext;

    public MatrixTrackingGL(GL gl) {
        this.mgl = (GL10) gl;
        if (gl instanceof GL10Ext) {
            this.mgl10Ext = (GL10Ext) gl;
        }
        if (gl instanceof GL11) {
            this.mgl11 = (GL11) gl;
        }
        if (gl instanceof GL11Ext) {
            this.mgl11Ext = (GL11Ext) gl;
        }
        this.mModelView = new MatrixStack();
        this.mProjection = new MatrixStack();
        this.mTexture = new MatrixStack();
        this.mCurrent = this.mModelView;
        this.mMatrixMode = 5888;
    }

    private void check() {
        int i;
        int i2 = 0;
        switch (this.mMatrixMode) {
            case 5888:
                i = 35213;
                break;
            case 5889:
                i = 35214;
                break;
            case 5890:
                i = 35215;
                break;
            default:
                throw new IllegalArgumentException("Unknown matrix mode");
        }
        if (this.mByteBuffer == null) {
            this.mCheckA = new float[16];
            this.mCheckB = new float[16];
            this.mByteBuffer = ByteBuffer.allocateDirect(64);
            this.mByteBuffer.order(ByteOrder.nativeOrder());
            this.mFloatBuffer = this.mByteBuffer.asFloatBuffer();
        }
        this.mgl.glGetIntegerv(i, this.mByteBuffer.asIntBuffer());
        for (i = 0; i < 16; i++) {
            this.mCheckB[i] = this.mFloatBuffer.get(i);
        }
        this.mCurrent.getMatrix(this.mCheckA, 0);
        i = 0;
        while (i2 < 16) {
            if (this.mCheckA[i2] != this.mCheckB[i2]) {
                Log.d("GLMatWrap", "i:" + i2 + " a:" + this.mCheckA[i2] + " a:" + this.mCheckB[i2]);
                i = 1;
            }
            i2++;
        }
        if (i != 0) {
            throw new IllegalArgumentException("Matrix math difference.");
        }
    }

    public void getMatrix(float[] fArr, int i) {
        this.mCurrent.getMatrix(fArr, i);
    }

    public int getMatrixMode() {
        return this.mMatrixMode;
    }

    public void glActiveTexture(int i) {
        this.mgl.glActiveTexture(i);
    }

    public void glAlphaFunc(int i, float f) {
        this.mgl.glAlphaFunc(i, f);
    }

    public void glAlphaFuncx(int i, int i2) {
        this.mgl.glAlphaFuncx(i, i2);
    }

    public void glBindBuffer(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glBindTexture(int i, int i2) {
        this.mgl.glBindTexture(i, i2);
    }

    public void glBlendFunc(int i, int i2) {
        this.mgl.glBlendFunc(i, i2);
    }

    public void glBufferData(int i, int i2, Buffer buffer, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glBufferSubData(int i, int i2, int i3, Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    public void glClear(int i) {
        this.mgl.glClear(i);
    }

    public void glClearColor(float f, float f2, float f3, float f4) {
        this.mgl.glClearColor(f, f2, f3, f4);
    }

    public void glClearColorx(int i, int i2, int i3, int i4) {
        this.mgl.glClearColorx(i, i2, i3, i4);
    }

    public void glClearDepthf(float f) {
        this.mgl.glClearDepthf(f);
    }

    public void glClearDepthx(int i) {
        this.mgl.glClearDepthx(i);
    }

    public void glClearStencil(int i) {
        this.mgl.glClearStencil(i);
    }

    public void glClientActiveTexture(int i) {
        this.mgl.glClientActiveTexture(i);
    }

    public void glClipPlanef(int i, FloatBuffer floatBuffer) {
        this.mgl11.glClipPlanef(i, floatBuffer);
    }

    public void glClipPlanef(int i, float[] fArr, int i2) {
        this.mgl11.glClipPlanef(i, fArr, i2);
    }

    public void glClipPlanex(int i, IntBuffer intBuffer) {
        this.mgl11.glClipPlanex(i, intBuffer);
    }

    public void glClipPlanex(int i, int[] iArr, int i2) {
        this.mgl11.glClipPlanex(i, iArr, i2);
    }

    public void glColor4f(float f, float f2, float f3, float f4) {
        this.mgl.glColor4f(f, f2, f3, f4);
    }

    public void glColor4ub(byte b, byte b2, byte b3, byte b4) {
        throw new UnsupportedOperationException();
    }

    public void glColor4x(int i, int i2, int i3, int i4) {
        this.mgl.glColor4x(i, i2, i3, i4);
    }

    public void glColorMask(boolean z, boolean z2, boolean z3, boolean z4) {
        this.mgl.glColorMask(z, z2, z3, z4);
    }

    public void glColorPointer(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glColorPointer(int i, int i2, int i3, Buffer buffer) {
        this.mgl.glColorPointer(i, i2, i3, buffer);
    }

    public void glCompressedTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer) {
        this.mgl.glCompressedTexImage2D(i, i2, i3, i4, i5, i6, i7, buffer);
    }

    public void glCompressedTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer) {
        this.mgl.glCompressedTexSubImage2D(i, i2, i3, i4, i5, i6, i7, i8, buffer);
    }

    public void glCopyTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mgl.glCopyTexImage2D(i, i2, i3, i4, i5, i6, i7, i8);
    }

    public void glCopyTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mgl.glCopyTexSubImage2D(i, i2, i3, i4, i5, i6, i7, i8);
    }

    public void glCullFace(int i) {
        this.mgl.glCullFace(i);
    }

    public void glCurrentPaletteMatrixOES(int i) {
        throw new UnsupportedOperationException();
    }

    public void glDeleteBuffers(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glDeleteBuffers(int i, int[] iArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glDeleteTextures(int i, IntBuffer intBuffer) {
        this.mgl.glDeleteTextures(i, intBuffer);
    }

    public void glDeleteTextures(int i, int[] iArr, int i2) {
        this.mgl.glDeleteTextures(i, iArr, i2);
    }

    public void glDepthFunc(int i) {
        this.mgl.glDepthFunc(i);
    }

    public void glDepthMask(boolean z) {
        this.mgl.glDepthMask(z);
    }

    public void glDepthRangef(float f, float f2) {
        this.mgl.glDepthRangef(f, f2);
    }

    public void glDepthRangex(int i, int i2) {
        this.mgl.glDepthRangex(i, i2);
    }

    public void glDisable(int i) {
        this.mgl.glDisable(i);
    }

    public void glDisableClientState(int i) {
        this.mgl.glDisableClientState(i);
    }

    public void glDrawArrays(int i, int i2, int i3) {
        this.mgl.glDrawArrays(i, i2, i3);
    }

    public void glDrawElements(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glDrawElements(int i, int i2, int i3, Buffer buffer) {
        this.mgl.glDrawElements(i, i2, i3, buffer);
    }

    public void glDrawTexfOES(float f, float f2, float f3, float f4, float f5) {
        this.mgl11Ext.glDrawTexfOES(f, f2, f3, f4, f5);
    }

    public void glDrawTexfvOES(FloatBuffer floatBuffer) {
        this.mgl11Ext.glDrawTexfvOES(floatBuffer);
    }

    public void glDrawTexfvOES(float[] fArr, int i) {
        this.mgl11Ext.glDrawTexfvOES(fArr, i);
    }

    public void glDrawTexiOES(int i, int i2, int i3, int i4, int i5) {
        this.mgl11Ext.glDrawTexiOES(i, i2, i3, i4, i5);
    }

    public void glDrawTexivOES(IntBuffer intBuffer) {
        this.mgl11Ext.glDrawTexivOES(intBuffer);
    }

    public void glDrawTexivOES(int[] iArr, int i) {
        this.mgl11Ext.glDrawTexivOES(iArr, i);
    }

    public void glDrawTexsOES(short s, short s2, short s3, short s4, short s5) {
        this.mgl11Ext.glDrawTexsOES(s, s2, s3, s4, s5);
    }

    public void glDrawTexsvOES(ShortBuffer shortBuffer) {
        this.mgl11Ext.glDrawTexsvOES(shortBuffer);
    }

    public void glDrawTexsvOES(short[] sArr, int i) {
        this.mgl11Ext.glDrawTexsvOES(sArr, i);
    }

    public void glDrawTexxOES(int i, int i2, int i3, int i4, int i5) {
        this.mgl11Ext.glDrawTexxOES(i, i2, i3, i4, i5);
    }

    public void glDrawTexxvOES(IntBuffer intBuffer) {
        this.mgl11Ext.glDrawTexxvOES(intBuffer);
    }

    public void glDrawTexxvOES(int[] iArr, int i) {
        this.mgl11Ext.glDrawTexxvOES(iArr, i);
    }

    public void glEnable(int i) {
        this.mgl.glEnable(i);
    }

    public void glEnableClientState(int i) {
        this.mgl.glEnableClientState(i);
    }

    public void glFinish() {
        this.mgl.glFinish();
    }

    public void glFlush() {
        this.mgl.glFlush();
    }

    public void glFogf(int i, float f) {
        this.mgl.glFogf(i, f);
    }

    public void glFogfv(int i, FloatBuffer floatBuffer) {
        this.mgl.glFogfv(i, floatBuffer);
    }

    public void glFogfv(int i, float[] fArr, int i2) {
        this.mgl.glFogfv(i, fArr, i2);
    }

    public void glFogx(int i, int i2) {
        this.mgl.glFogx(i, i2);
    }

    public void glFogxv(int i, IntBuffer intBuffer) {
        this.mgl.glFogxv(i, intBuffer);
    }

    public void glFogxv(int i, int[] iArr, int i2) {
        this.mgl.glFogxv(i, iArr, i2);
    }

    public void glFrontFace(int i) {
        this.mgl.glFrontFace(i);
    }

    public void glFrustumf(float f, float f2, float f3, float f4, float f5, float f6) {
        this.mCurrent.glFrustumf(f, f2, f3, f4, f5, f6);
        this.mgl.glFrustumf(f, f2, f3, f4, f5, f6);
    }

    public void glFrustumx(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mCurrent.glFrustumx(i, i2, i3, i4, i5, i6);
        this.mgl.glFrustumx(i, i2, i3, i4, i5, i6);
    }

    public void glGenBuffers(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGenBuffers(int i, int[] iArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glGenTextures(int i, IntBuffer intBuffer) {
        this.mgl.glGenTextures(i, intBuffer);
    }

    public void glGenTextures(int i, int[] iArr, int i2) {
        this.mgl.glGenTextures(i, iArr, i2);
    }

    public void glGetBooleanv(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetBooleanv(int i, boolean[] zArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glGetBufferParameteriv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetBufferParameteriv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetClipPlanef(int i, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetClipPlanef(int i, float[] fArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glGetClipPlanex(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetClipPlanex(int i, int[] iArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public int glGetError() {
        return this.mgl.glGetError();
    }

    public void glGetFixedv(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetFixedv(int i, int[] iArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glGetFloatv(int i, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetFloatv(int i, float[] fArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glGetIntegerv(int i, IntBuffer intBuffer) {
        this.mgl.glGetIntegerv(i, intBuffer);
    }

    public void glGetIntegerv(int i, int[] iArr, int i2) {
        this.mgl.glGetIntegerv(i, iArr, i2);
    }

    public void glGetLightfv(int i, int i2, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetLightfv(int i, int i2, float[] fArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetLightxv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetLightxv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetMaterialfv(int i, int i2, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetMaterialfv(int i, int i2, float[] fArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetMaterialxv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetMaterialxv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetPointerv(int i, Buffer[] bufferArr) {
        throw new UnsupportedOperationException();
    }

    public String glGetString(int i) {
        return this.mgl.glGetString(i);
    }

    public void glGetTexEnviv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexEnviv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexEnvxv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexEnvxv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameterfv(int i, int i2, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameterfv(int i, int i2, float[] fArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameteriv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameteriv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameterxv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glGetTexParameterxv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glHint(int i, int i2) {
        this.mgl.glHint(i, i2);
    }

    public boolean glIsBuffer(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean glIsEnabled(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean glIsTexture(int i) {
        throw new UnsupportedOperationException();
    }

    public void glLightModelf(int i, float f) {
        this.mgl.glLightModelf(i, f);
    }

    public void glLightModelfv(int i, FloatBuffer floatBuffer) {
        this.mgl.glLightModelfv(i, floatBuffer);
    }

    public void glLightModelfv(int i, float[] fArr, int i2) {
        this.mgl.glLightModelfv(i, fArr, i2);
    }

    public void glLightModelx(int i, int i2) {
        this.mgl.glLightModelx(i, i2);
    }

    public void glLightModelxv(int i, IntBuffer intBuffer) {
        this.mgl.glLightModelxv(i, intBuffer);
    }

    public void glLightModelxv(int i, int[] iArr, int i2) {
        this.mgl.glLightModelxv(i, iArr, i2);
    }

    public void glLightf(int i, int i2, float f) {
        this.mgl.glLightf(i, i2, f);
    }

    public void glLightfv(int i, int i2, FloatBuffer floatBuffer) {
        this.mgl.glLightfv(i, i2, floatBuffer);
    }

    public void glLightfv(int i, int i2, float[] fArr, int i3) {
        this.mgl.glLightfv(i, i2, fArr, i3);
    }

    public void glLightx(int i, int i2, int i3) {
        this.mgl.glLightx(i, i2, i3);
    }

    public void glLightxv(int i, int i2, IntBuffer intBuffer) {
        this.mgl.glLightxv(i, i2, intBuffer);
    }

    public void glLightxv(int i, int i2, int[] iArr, int i3) {
        this.mgl.glLightxv(i, i2, iArr, i3);
    }

    public void glLineWidth(float f) {
        this.mgl.glLineWidth(f);
    }

    public void glLineWidthx(int i) {
        this.mgl.glLineWidthx(i);
    }

    public void glLoadIdentity() {
        this.mCurrent.glLoadIdentity();
        this.mgl.glLoadIdentity();
    }

    public void glLoadMatrixf(FloatBuffer floatBuffer) {
        int position = floatBuffer.position();
        this.mCurrent.glLoadMatrixf(floatBuffer);
        floatBuffer.position(position);
        this.mgl.glLoadMatrixf(floatBuffer);
    }

    public void glLoadMatrixf(float[] fArr, int i) {
        this.mCurrent.glLoadMatrixf(fArr, i);
        this.mgl.glLoadMatrixf(fArr, i);
    }

    public void glLoadMatrixx(IntBuffer intBuffer) {
        int position = intBuffer.position();
        this.mCurrent.glLoadMatrixx(intBuffer);
        intBuffer.position(position);
        this.mgl.glLoadMatrixx(intBuffer);
    }

    public void glLoadMatrixx(int[] iArr, int i) {
        this.mCurrent.glLoadMatrixx(iArr, i);
        this.mgl.glLoadMatrixx(iArr, i);
    }

    public void glLoadPaletteFromModelViewMatrixOES() {
        throw new UnsupportedOperationException();
    }

    public void glLogicOp(int i) {
        this.mgl.glLogicOp(i);
    }

    public void glMaterialf(int i, int i2, float f) {
        this.mgl.glMaterialf(i, i2, f);
    }

    public void glMaterialfv(int i, int i2, FloatBuffer floatBuffer) {
        this.mgl.glMaterialfv(i, i2, floatBuffer);
    }

    public void glMaterialfv(int i, int i2, float[] fArr, int i3) {
        this.mgl.glMaterialfv(i, i2, fArr, i3);
    }

    public void glMaterialx(int i, int i2, int i3) {
        this.mgl.glMaterialx(i, i2, i3);
    }

    public void glMaterialxv(int i, int i2, IntBuffer intBuffer) {
        this.mgl.glMaterialxv(i, i2, intBuffer);
    }

    public void glMaterialxv(int i, int i2, int[] iArr, int i3) {
        this.mgl.glMaterialxv(i, i2, iArr, i3);
    }

    public void glMatrixIndexPointerOES(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glMatrixIndexPointerOES(int i, int i2, int i3, Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    public void glMatrixMode(int i) {
        switch (i) {
            case 5888:
                this.mCurrent = this.mModelView;
                break;
            case 5889:
                this.mCurrent = this.mProjection;
                break;
            case 5890:
                this.mCurrent = this.mTexture;
                break;
            default:
                throw new IllegalArgumentException("Unknown matrix mode: " + i);
        }
        this.mgl.glMatrixMode(i);
        this.mMatrixMode = i;
    }

    public void glMultMatrixf(FloatBuffer floatBuffer) {
        int position = floatBuffer.position();
        this.mCurrent.glMultMatrixf(floatBuffer);
        floatBuffer.position(position);
        this.mgl.glMultMatrixf(floatBuffer);
    }

    public void glMultMatrixf(float[] fArr, int i) {
        this.mCurrent.glMultMatrixf(fArr, i);
        this.mgl.glMultMatrixf(fArr, i);
    }

    public void glMultMatrixx(IntBuffer intBuffer) {
        int position = intBuffer.position();
        this.mCurrent.glMultMatrixx(intBuffer);
        intBuffer.position(position);
        this.mgl.glMultMatrixx(intBuffer);
    }

    public void glMultMatrixx(int[] iArr, int i) {
        this.mCurrent.glMultMatrixx(iArr, i);
        this.mgl.glMultMatrixx(iArr, i);
    }

    public void glMultiTexCoord4f(int i, float f, float f2, float f3, float f4) {
        this.mgl.glMultiTexCoord4f(i, f, f2, f3, f4);
    }

    public void glMultiTexCoord4x(int i, int i2, int i3, int i4, int i5) {
        this.mgl.glMultiTexCoord4x(i, i2, i3, i4, i5);
    }

    public void glNormal3f(float f, float f2, float f3) {
        this.mgl.glNormal3f(f, f2, f3);
    }

    public void glNormal3x(int i, int i2, int i3) {
        this.mgl.glNormal3x(i, i2, i3);
    }

    public void glNormalPointer(int i, int i2, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glNormalPointer(int i, int i2, Buffer buffer) {
        this.mgl.glNormalPointer(i, i2, buffer);
    }

    public void glOrthof(float f, float f2, float f3, float f4, float f5, float f6) {
        this.mCurrent.glOrthof(f, f2, f3, f4, f5, f6);
        this.mgl.glOrthof(f, f2, f3, f4, f5, f6);
    }

    public void glOrthox(int i, int i2, int i3, int i4, int i5, int i6) {
        this.mCurrent.glOrthox(i, i2, i3, i4, i5, i6);
        this.mgl.glOrthox(i, i2, i3, i4, i5, i6);
    }

    public void glPixelStorei(int i, int i2) {
        this.mgl.glPixelStorei(i, i2);
    }

    public void glPointParameterf(int i, float f) {
        throw new UnsupportedOperationException();
    }

    public void glPointParameterfv(int i, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glPointParameterfv(int i, float[] fArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glPointParameterx(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glPointParameterxv(int i, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glPointParameterxv(int i, int[] iArr, int i2) {
        throw new UnsupportedOperationException();
    }

    public void glPointSize(float f) {
        this.mgl.glPointSize(f);
    }

    public void glPointSizePointerOES(int i, int i2, Buffer buffer) {
        throw new UnsupportedOperationException();
    }

    public void glPointSizex(int i) {
        this.mgl.glPointSizex(i);
    }

    public void glPolygonOffset(float f, float f2) {
        this.mgl.glPolygonOffset(f, f2);
    }

    public void glPolygonOffsetx(int i, int i2) {
        this.mgl.glPolygonOffsetx(i, i2);
    }

    public void glPopMatrix() {
        this.mCurrent.glPopMatrix();
        this.mgl.glPopMatrix();
    }

    public void glPushMatrix() {
        this.mCurrent.glPushMatrix();
        this.mgl.glPushMatrix();
    }

    public int glQueryMatrixxOES(IntBuffer intBuffer, IntBuffer intBuffer2) {
        return this.mgl10Ext.glQueryMatrixxOES(intBuffer, intBuffer2);
    }

    public int glQueryMatrixxOES(int[] iArr, int i, int[] iArr2, int i2) {
        return this.mgl10Ext.glQueryMatrixxOES(iArr, i, iArr2, i2);
    }

    public void glReadPixels(int i, int i2, int i3, int i4, int i5, int i6, Buffer buffer) {
        this.mgl.glReadPixels(i, i2, i3, i4, i5, i6, buffer);
    }

    public void glRotatef(float f, float f2, float f3, float f4) {
        this.mCurrent.glRotatef(f, f2, f3, f4);
        this.mgl.glRotatef(f, f2, f3, f4);
    }

    public void glRotatex(int i, int i2, int i3, int i4) {
        this.mCurrent.glRotatex(i, i2, i3, i4);
        this.mgl.glRotatex(i, i2, i3, i4);
    }

    public void glSampleCoverage(float f, boolean z) {
        this.mgl.glSampleCoverage(f, z);
    }

    public void glSampleCoveragex(int i, boolean z) {
        this.mgl.glSampleCoveragex(i, z);
    }

    public void glScalef(float f, float f2, float f3) {
        this.mCurrent.glScalef(f, f2, f3);
        this.mgl.glScalef(f, f2, f3);
    }

    public void glScalex(int i, int i2, int i3) {
        this.mCurrent.glScalex(i, i2, i3);
        this.mgl.glScalex(i, i2, i3);
    }

    public void glScissor(int i, int i2, int i3, int i4) {
        this.mgl.glScissor(i, i2, i3, i4);
    }

    public void glShadeModel(int i) {
        this.mgl.glShadeModel(i);
    }

    public void glStencilFunc(int i, int i2, int i3) {
        this.mgl.glStencilFunc(i, i2, i3);
    }

    public void glStencilMask(int i) {
        this.mgl.glStencilMask(i);
    }

    public void glStencilOp(int i, int i2, int i3) {
        this.mgl.glStencilOp(i, i2, i3);
    }

    public void glTexCoordPointer(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glTexCoordPointer(int i, int i2, int i3, Buffer buffer) {
        this.mgl.glTexCoordPointer(i, i2, i3, buffer);
    }

    public void glTexEnvf(int i, int i2, float f) {
        this.mgl.glTexEnvf(i, i2, f);
    }

    public void glTexEnvfv(int i, int i2, FloatBuffer floatBuffer) {
        this.mgl.glTexEnvfv(i, i2, floatBuffer);
    }

    public void glTexEnvfv(int i, int i2, float[] fArr, int i3) {
        this.mgl.glTexEnvfv(i, i2, fArr, i3);
    }

    public void glTexEnvi(int i, int i2, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glTexEnviv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glTexEnviv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glTexEnvx(int i, int i2, int i3) {
        this.mgl.glTexEnvx(i, i2, i3);
    }

    public void glTexEnvxv(int i, int i2, IntBuffer intBuffer) {
        this.mgl.glTexEnvxv(i, i2, intBuffer);
    }

    public void glTexEnvxv(int i, int i2, int[] iArr, int i3) {
        this.mgl.glTexEnvxv(i, i2, iArr, i3);
    }

    public void glTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer) {
        this.mgl.glTexImage2D(i, i2, i3, i4, i5, i6, i7, i8, buffer);
    }

    public void glTexParameterf(int i, int i2, float f) {
        this.mgl.glTexParameterf(i, i2, f);
    }

    public void glTexParameterfv(int i, int i2, FloatBuffer floatBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glTexParameterfv(int i, int i2, float[] fArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glTexParameteri(int i, int i2, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glTexParameteriv(int i, int i2, IntBuffer intBuffer) {
        this.mgl11.glTexParameteriv(i, i2, intBuffer);
    }

    public void glTexParameteriv(int i, int i2, int[] iArr, int i3) {
        this.mgl11.glTexParameteriv(i, i2, iArr, i3);
    }

    public void glTexParameterx(int i, int i2, int i3) {
        this.mgl.glTexParameterx(i, i2, i3);
    }

    public void glTexParameterxv(int i, int i2, IntBuffer intBuffer) {
        throw new UnsupportedOperationException();
    }

    public void glTexParameterxv(int i, int i2, int[] iArr, int i3) {
        throw new UnsupportedOperationException();
    }

    public void glTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer) {
        this.mgl.glTexSubImage2D(i, i2, i3, i4, i5, i6, i7, i8, buffer);
    }

    public void glTranslatef(float f, float f2, float f3) {
        this.mCurrent.glTranslatef(f, f2, f3);
        this.mgl.glTranslatef(f, f2, f3);
    }

    public void glTranslatex(int i, int i2, int i3) {
        this.mCurrent.glTranslatex(i, i2, i3);
        this.mgl.glTranslatex(i, i2, i3);
    }

    public void glVertexPointer(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glVertexPointer(int i, int i2, int i3, Buffer buffer) {
        this.mgl.glVertexPointer(i, i2, i3, buffer);
    }

    public void glViewport(int i, int i2, int i3, int i4) {
        this.mgl.glViewport(i, i2, i3, i4);
    }

    public void glWeightPointerOES(int i, int i2, int i3, int i4) {
        throw new UnsupportedOperationException();
    }

    public void glWeightPointerOES(int i, int i2, int i3, Buffer buffer) {
        throw new UnsupportedOperationException();
    }
}
