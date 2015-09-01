package opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public final class HalfSphere {
    public static final int PLANET_BLEND_MODE_ATMO = 2;
    public static final int PLANET_BLEND_MODE_FADE = 4;
    public static final int PLANET_BLEND_MODE_NONE = 1;
    public static final int PLANET_BLEND_MODE_SOLID = 3;
    private int[] Textures;
    private float direction;
    private boolean doBumpMap;
    private int mSlices;
    private int mStacks;
    private FloatBuffer m_ColorData;
    private FloatBuffer m_NormalData;
    private FloatBuffer m_TextureData;
    private FloatBuffer m_VertexData;
    private int nTextures;
    private boolean withTexture;

    public HalfSphere(Context context, float f, float f2, boolean z, int i, int i2) {
        this.Textures = new int[PLANET_BLEND_MODE_SOLID];
        this.doBumpMap = true;
        this.direction = 1.0f;
        this.mSlices = i;
        this.mStacks = i2;
        this.withTexture = z;
        this.direction = f2;
        computeAndBuildData(f, i, i2);
    }

    private void computeAndBuildData(float f, int i, int i2) {
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        float f2 = 0.0f;
        float f3 = 1.0f;
        float[] fArr = new float[((((i * PLANET_BLEND_MODE_ATMO) + PLANET_BLEND_MODE_ATMO) * i2) * PLANET_BLEND_MODE_SOLID)];
        float[] fArr2 = new float[((((i * PLANET_BLEND_MODE_ATMO) + PLANET_BLEND_MODE_ATMO) * PLANET_BLEND_MODE_FADE) * i2)];
        float[] fArr3 = new float[((((i * PLANET_BLEND_MODE_ATMO) + PLANET_BLEND_MODE_ATMO) * i2) * PLANET_BLEND_MODE_SOLID)];
        float[] fArr4 = null;
        if (this.withTexture) {
            fArr4 = new float[((((i * PLANET_BLEND_MODE_ATMO) + PLANET_BLEND_MODE_ATMO) * i2) * PLANET_BLEND_MODE_ATMO)];
        }
        for (int i7 = 0; i7 < i2; i7 += PLANET_BLEND_MODE_NONE) {
            double d = (double) this.direction;
            double d2 = (3.141592653589793d * (0.5d - (((double) (i7 + 0)) / ((double) (i2 * PLANET_BLEND_MODE_ATMO))))) * d;
            double d3 = (double) this.direction;
            d = (3.141592653589793d * (0.5d - (((double) (i7 + PLANET_BLEND_MODE_NONE)) / ((double) (i2 * PLANET_BLEND_MODE_ATMO))))) * d;
            d3 = Math.cos(d2);
            d2 = Math.sin(d2);
            double cos = Math.cos(d);
            d = Math.sin(d);
            for (int i8 = 0; i8 < i; i8 += PLANET_BLEND_MODE_NONE) {
                double d4 = -3.141592653589793d + ((6.283185307179586d * ((double) i8)) * (1.0d / ((double) (i - 1))));
                double cos2 = Math.cos(d4);
                d4 = Math.sin(d4);
                fArr[i3 + 0] = ((float) (cos * cos2)) * f;
                fArr[i3 + PLANET_BLEND_MODE_NONE] = ((float) (cos * d4)) * f;
                fArr[i3 + PLANET_BLEND_MODE_ATMO] = ((float) d) * f;
                fArr[i3 + PLANET_BLEND_MODE_SOLID] = ((float) (d3 * cos2)) * f;
                fArr[i3 + PLANET_BLEND_MODE_FADE] = ((float) (d3 * d4)) * f;
                fArr[i3 + 5] = ((float) d2) * f;
                fArr3[i4 + 0] = (float) (cos * cos2);
                fArr3[i4 + PLANET_BLEND_MODE_NONE] = (float) (cos * d4);
                fArr3[i4 + PLANET_BLEND_MODE_ATMO] = (float) d;
                fArr3[i4 + PLANET_BLEND_MODE_SOLID] = (float) (cos2 * d3);
                fArr3[i4 + PLANET_BLEND_MODE_FADE] = (float) (d4 * d3);
                fArr3[i4 + 5] = (float) d2;
                if (this.withTexture) {
                    float f4 = (float) (((double) i8) * (1.0d / ((double) (i - 1))));
                    fArr4[i5 + 0] = f4;
                    fArr4[i5 + PLANET_BLEND_MODE_NONE] = ((float) (i7 + PLANET_BLEND_MODE_NONE)) * (1.0f / ((float) i2));
                    fArr4[i5 + PLANET_BLEND_MODE_ATMO] = f4;
                    fArr4[i5 + PLANET_BLEND_MODE_SOLID] = ((float) (i7 + 0)) * (1.0f / ((float) i2));
                }
                fArr2[i6 + 0] = f3;
                fArr2[i6 + PLANET_BLEND_MODE_NONE] = 0.0f;
                fArr2[i6 + PLANET_BLEND_MODE_ATMO] = f2;
                fArr2[i6 + PLANET_BLEND_MODE_FADE] = f3;
                fArr2[i6 + 5] = 0.0f;
                fArr2[i6 + 6] = f2;
                fArr2[i6 + PLANET_BLEND_MODE_SOLID] = 1.0f;
                fArr2[i6 + 7] = 1.0f;
                f2 += 0.0f;
                f3 -= 0.0f;
                i6 += 8;
                i3 += 6;
                i4 += 6;
                i5 += PLANET_BLEND_MODE_FADE;
                int i9 = i3 + 0;
                int i10 = i3 + PLANET_BLEND_MODE_SOLID;
                float f5 = fArr[i3 - 3];
                fArr[i10] = f5;
                fArr[i9] = f5;
                i9 = i3 + PLANET_BLEND_MODE_ATMO;
                i10 = i3 + 5;
                f5 = fArr[i3 - 1];
                fArr[i10] = f5;
                fArr[i9] = f5;
                i9 = i3 + PLANET_BLEND_MODE_NONE;
                i10 = i3 + PLANET_BLEND_MODE_FADE;
                f5 = fArr[i3 - 2];
                fArr[i10] = f5;
                fArr[i9] = f5;
                i9 = i4 + 0;
                i10 = i4 + PLANET_BLEND_MODE_SOLID;
                f5 = fArr3[i4 - 3];
                fArr3[i10] = f5;
                fArr3[i9] = f5;
                i9 = i4 + PLANET_BLEND_MODE_ATMO;
                i10 = i4 + 5;
                f5 = fArr3[i4 - 1];
                fArr3[i10] = f5;
                fArr3[i9] = f5;
                i9 = i4 + PLANET_BLEND_MODE_NONE;
                i10 = i4 + PLANET_BLEND_MODE_FADE;
                f5 = fArr3[i4 - 2];
                fArr3[i10] = f5;
                fArr3[i9] = f5;
                if (this.withTexture) {
                    i9 = i5 + 0;
                    i10 = i5 + PLANET_BLEND_MODE_ATMO;
                    f5 = fArr4[i5 - 2];
                    fArr4[i10] = f5;
                    fArr4[i9] = f5;
                    i9 = i5 + PLANET_BLEND_MODE_NONE;
                    i10 = i5 + PLANET_BLEND_MODE_SOLID;
                    f5 = fArr4[i5 - 1];
                    fArr4[i10] = f5;
                    fArr4[i9] = f5;
                }
            }
        }
        this.m_VertexData = newDirectFloatBuffer.makeFloatBuffer(fArr);
        this.m_NormalData = newDirectFloatBuffer.makeFloatBuffer(fArr3);
        if (this.withTexture) {
            this.m_TextureData = newDirectFloatBuffer.makeFloatBuffer(fArr4);
        }
        this.m_ColorData = newDirectFloatBuffer.makeFloatBuffer(fArr2);
    }

    private void multiTextureBumpMap(GL11 gl11, int i, int i2) {
        gl11.glActiveTexture(33984);
        gl11.glBindTexture(3553, i);
        gl11.glTexEnvf(8960, 8704, 34160.0f);
        gl11.glTexEnvf(8960, 34161, 34478.0f);
        gl11.glTexEnvf(8960, 34176, 5890.0f);
        gl11.glTexEnvf(8960, 34177, 34168.0f);
        gl11.glActiveTexture(33985);
        gl11.glBindTexture(3553, i2);
        gl11.glTexEnvf(8960, 8704, 8448.0f);
    }

    public static void setBlendMode(int i) {
        GLES20.glEnable(3042);
        if (i == PLANET_BLEND_MODE_NONE) {
            GLES20.glDisable(3042);
        } else if (i == PLANET_BLEND_MODE_FADE) {
            GLES20.glBlendFunc(770, 771);
        } else if (i == PLANET_BLEND_MODE_ATMO) {
            GLES20.glBlendFunc(PLANET_BLEND_MODE_NONE, PLANET_BLEND_MODE_NONE);
        } else if (i == PLANET_BLEND_MODE_SOLID) {
            GLES20.glDisable(3042);
        }
    }

    public int createTexture(GL10 gl10, Context context, int i) {
        int[] iArr = new int[PLANET_BLEND_MODE_NONE];
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        gl10.glGenTextures(PLANET_BLEND_MODE_NONE, iArr, 0);
        gl10.glBindTexture(3553, iArr[0]);
        GLUtils.texImage2D(3553, 0, decodeResource, 0);
        gl10.glTexParameterf(3553, 10241, 9729.0f);
        gl10.glTexParameterf(3553, 10240, 9729.0f);
        decodeResource.recycle();
        gl10.glBindTexture(3553, 0);
        return iArr[0];
    }

    public void drawES2(int i, int i2, int i3, int i4) {
        GLES20.glEnable(2884);
        GLES20.glCullFace(1029);
        GLES20.glFrontFace(2304);
        this.m_VertexData.rewind();
        GLES20.glVertexAttribPointer(i, PLANET_BLEND_MODE_SOLID, 5126, false, 0, this.m_VertexData);
        GLES20.glEnableVertexAttribArray(i);
        if (i2 >= 0) {
            GLES20.glVertexAttribPointer(i2, PLANET_BLEND_MODE_SOLID, 5126, false, 0, this.m_NormalData);
            GLES20.glEnableVertexAttribArray(i2);
        }
        if (i4 > 0) {
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.Textures[0]);
            this.m_TextureData.rewind();
            GLES20.glVertexAttribPointer(i4, PLANET_BLEND_MODE_ATMO, 5126, false, 0, this.m_TextureData);
            GLES20.glEnableVertexAttribArray(i4);
        }
        if (i3 > 0) {
            this.m_ColorData.rewind();
            GLES20.glVertexAttribPointer(i3, PLANET_BLEND_MODE_FADE, 5126, false, 0, this.m_ColorData);
            GLES20.glEnableVertexAttribArray(i3);
        }
        GLES20.glDrawArrays(5, 0, (((this.mSlices + PLANET_BLEND_MODE_NONE) * PLANET_BLEND_MODE_ATMO) * (this.mStacks - 1)) + PLANET_BLEND_MODE_ATMO);
        ShaderManager.checkGlError("Sphere::glDrawArrays");
        GLES20.glDisable(2884);
        GLES20.glFrontFace(2305);
    }

    public void drawES2(int i, int i2, int i3, int i4, int i5) {
        GLES20.glEnable(2884);
        GLES20.glCullFace(1029);
        GLES20.glFrontFace(2304);
        this.m_VertexData.rewind();
        GLES20.glVertexAttribPointer(i, PLANET_BLEND_MODE_SOLID, 5126, false, 0, this.m_VertexData);
        GLES20.glEnableVertexAttribArray(i);
        if (i2 >= 0) {
            this.m_NormalData.rewind();
            GLES20.glVertexAttribPointer(i2, PLANET_BLEND_MODE_SOLID, 5126, false, 0, this.m_NormalData);
            GLES20.glEnableVertexAttribArray(i2);
        }
        if (i3 >= 0) {
            this.m_ColorData.rewind();
            GLES20.glVertexAttribPointer(i3, PLANET_BLEND_MODE_FADE, 5126, false, 0, this.m_ColorData);
            GLES20.glEnableVertexAttribArray(i3);
        }
        if (i4 > 0 && this.withTexture) {
            if (i5 >= 0) {
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, i5);
            }
            this.m_TextureData.rewind();
            GLES20.glVertexAttribPointer(i4, PLANET_BLEND_MODE_ATMO, 5126, false, 0, this.m_TextureData);
            GLES20.glEnableVertexAttribArray(i4);
        }
        GLES20.glDrawArrays(5, 0, (((this.mSlices + PLANET_BLEND_MODE_NONE) * PLANET_BLEND_MODE_ATMO) * (this.mStacks - 1)) + PLANET_BLEND_MODE_ATMO);
        ShaderManager.checkGlError("Sphere::glDrawArrays");
        GLES20.glDisable(2884);
        GLES20.glFrontFace(2305);
    }

    public void drawES2BumpMap(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        if (i5 > 0 && i6 > 0 && i7 > 0) {
            GLES20.glActiveTexture(33984);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(i7, "sTexture"), 0);
            GLES20.glBindTexture(3553, i5);
            GLES20.glActiveTexture(33985);
            GLES20.glUniform1i(GLES20.glGetUniformLocation(i7, "sNormalTexture"), PLANET_BLEND_MODE_NONE);
            GLES20.glBindTexture(3553, i6);
            this.m_TextureData.rewind();
            GLES20.glVertexAttribPointer(i4, PLANET_BLEND_MODE_ATMO, 5126, false, 0, this.m_TextureData);
            GLES20.glEnableVertexAttribArray(i4);
        }
        drawES2(i, i2, i3, -1);
        if (i5 > 0 && i6 > 0 && i7 > 0) {
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, 0);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, 0);
        }
    }

    public void loadTexture(GL11 gl11, Context context, int i) {
        this.withTexture = false;
        if (gl11 != null) {
            gl11.glGenTextures(PLANET_BLEND_MODE_NONE, this.Textures, 0);
            this.nTextures = 0;
            this.Textures[0] = ShaderManager.createTexture(gl11, context, i);
            this.nTextures = PLANET_BLEND_MODE_NONE;
            this.withTexture = true;
        }
    }

    public void loadTwoTextures(GL11 gl11, Context context, int i, int i2) {
        this.withTexture = false;
        if (gl11 != null) {
            gl11.glGenTextures(PLANET_BLEND_MODE_ATMO, this.Textures, 0);
            this.nTextures = 0;
            if (GLUtilFcns.getTextures(gl11, context, i, this.Textures[0]) && GLUtilFcns.getTextures(gl11, context, i2, this.Textures[PLANET_BLEND_MODE_NONE])) {
                this.nTextures = PLANET_BLEND_MODE_ATMO;
                this.withTexture = true;
            }
        }
    }
}
