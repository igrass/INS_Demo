package opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.opengles.GL11;

public class GLUtilFcns {
    private GLUtilFcns() {
    }

    public static float[] GetWorldCoords(GL11 gl11, float[] fArr, float f, float f2, float[] fArr2, float[] fArr3) {
        float[] fArr4 = new float[3];
        float[] fArr5 = new float[16];
        float[] fArr6 = new float[16];
        float[] r8 = new float[4];
        float[] r9 = new float[4];
        int i = (int) (f2 - fArr[1]);
        r8[0] = (float) (((double) ((2.0f * fArr[0]) / f)) - 1.0d);
        r8[1] = (float) (((double) ((((float) i) * 2.0f) / f2)) - 1.0d);
        r8[2] = -1.0f;
        r8[3] = 1.0f;
        Matrix.multiplyMM(fArr6, 0, fArr2, 0, fArr3, 0);
        Matrix.invertM(fArr5, 0, fArr6, 0);
        Matrix.multiplyMV(r9, 0, fArr5, 0, r8, 0);
        if (((double) r9[3]) == 0.0d) {
            Log.e("World coords", "ERROR!");
            return fArr4;
        }
        fArr4[0] = r9[0] / r9[3];
        fArr4[1] = r9[1] / r9[3];
        return fArr4;
    }

    public static final boolean getTextures(GL11 gl11, Context context, int i, int i2) {
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        if (decodeResource == null) {
            return false;
        }
        gl11.glBindTexture(3553, i2);
        GLUtils.texImage2D(3553, 0, decodeResource, 0);
        gl11.glTexParameterx(3553, 10241, 9729);
        gl11.glTexParameterx(3553, 10240, 9729);
        decodeResource.recycle();
        return true;
    }

    public static final void setWhiteColorLight(GL11 gl11) {
        float[] fArr = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        gl11.glLightfv(16385, 4611, fArr, 0);
        gl11.glLightfv(16385, 4608, fArr, 0);
        gl11.glLightfv(16385, 4609, fArr, 0);
        gl11.glLightfv(16385, 4610, fArr, 0);
    }

    public static final void setWhiteColorMaterial(GL11 gl11) {
        float[] fArr = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
        float[] fArr2 = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
        float[] fArr3 = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        gl11.glDisable(3553);
        gl11.glMaterialfv(1032, 4608, fArr, 0);
        gl11.glMaterialfv(1032, 4610, fArr3, 0);
        gl11.glMaterialfv(1032, 4609, fArr2, 0);
        gl11.glMaterialf(1032, 5633, 0.0f);
    }
}
