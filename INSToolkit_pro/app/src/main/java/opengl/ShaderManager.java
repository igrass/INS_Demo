package opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL11;

public class ShaderManager {
    private static String TAG = null;
    public static final int UNIFORM_ALPHA = 6;
    public static final int UNIFORM_COLOR_VECTOR = 5;
    public static final int UNIFORM_LIGHT_POSITION = 2;
    public static final int UNIFORM_MVP_MATRIX = 0;
    public static final int UNIFORM_MV_MATRIX = 4;
    public static final int UNIFORM_NORMAL_MATRIX = 1;
    public int[] m_UniformIDs;
    public int program;

    public static class Attribute {
        int id;
        String name;

        public Attribute(int i, String str) {
            this.id = i;
            this.name = str;
        }
    }

    static {
        TAG = "OpenGL ES2";
    }

    public ShaderManager() {
        this.m_UniformIDs = new int[10];
    }

    public static void checkGlError(String str) {
        int glGetError = GLES20.glGetError();
        if (glGetError != 0) {
            Log.e(TAG, new StringBuilder(String.valueOf(str)).append(": glError ").append(glGetError).toString());
            throw new RuntimeException(new StringBuilder(String.valueOf(str)).append(": glError ").append(glGetError).toString());
        }
    }

    public static int createTexture(GL11 gl11, Context context, int i) {
        int[] iArr = new int[UNIFORM_NORMAL_MATRIX];
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        gl11.glGenTextures(UNIFORM_NORMAL_MATRIX, iArr, UNIFORM_MVP_MATRIX);
        gl11.glBindTexture(3553, iArr[UNIFORM_MVP_MATRIX]);
        GLUtils.texImage2D(3553, UNIFORM_MVP_MATRIX, decodeResource, UNIFORM_MVP_MATRIX);
        gl11.glTexParameterf(3553, 10241, 9729.0f);
        gl11.glTexParameterf(3553, 10240, 9729.0f);
        decodeResource.recycle();
        gl11.glBindTexture(3553, UNIFORM_MVP_MATRIX);
        return iArr[UNIFORM_MVP_MATRIX];
    }

    public int createProgram(String str, String str2, Attribute[] attributeArr) {
        int loadShader = loadShader(35633, str);
        if (loadShader == 0) {
            return UNIFORM_MVP_MATRIX;
        }
        int loadShader2 = loadShader(35632, str2);
        if (loadShader2 == 0) {
            return UNIFORM_MVP_MATRIX;
        }
        this.program = GLES20.glCreateProgram();
        if (this.program != 0) {
            GLES20.glAttachShader(this.program, loadShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(this.program, loadShader2);
            checkGlError("glAttachShader");
            for (loadShader = UNIFORM_MVP_MATRIX; loadShader < attributeArr.length; loadShader += UNIFORM_NORMAL_MATRIX) {
                GLES20.glBindAttribLocation(this.program, attributeArr[loadShader].id, attributeArr[loadShader].name);
            }
            GLES20.glLinkProgram(this.program);
            int[] iArr = new int[UNIFORM_NORMAL_MATRIX];
            GLES20.glGetProgramiv(this.program, 35714, iArr, UNIFORM_MVP_MATRIX);
            if (iArr[UNIFORM_MVP_MATRIX] != UNIFORM_NORMAL_MATRIX) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(this.program));
                GLES20.glDeleteProgram(this.program);
                this.program = UNIFORM_MVP_MATRIX;
            }
            this.m_UniformIDs[UNIFORM_MVP_MATRIX] = GLES20.glGetUniformLocation(this.program, "uMVPMatrix");
            this.m_UniformIDs[UNIFORM_NORMAL_MATRIX] = GLES20.glGetUniformLocation(this.program, "uNormalMatrix");
            this.m_UniformIDs[UNIFORM_LIGHT_POSITION] = GLES20.glGetUniformLocation(this.program, "uLightPosition");
            this.m_UniformIDs[UNIFORM_MV_MATRIX] = GLES20.glGetUniformLocation(this.program, "uMVMatrix");
            this.m_UniformIDs[UNIFORM_COLOR_VECTOR] = GLES20.glGetUniformLocation(this.program, "aColor");
            this.m_UniformIDs[UNIFORM_ALPHA] = GLES20.glGetUniformLocation(this.program, "uAlpha");
        }
        return this.program;
    }

    public int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader != 0) {
            GLES20.glShaderSource(glCreateShader, str);
            GLES20.glCompileShader(glCreateShader);
            int[] iArr = new int[UNIFORM_NORMAL_MATRIX];
            GLES20.glGetShaderiv(glCreateShader, 35713, iArr, UNIFORM_MVP_MATRIX);
            if (iArr[UNIFORM_MVP_MATRIX] == 0) {
                Log.e(TAG, "Could not compile shader " + i + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(glCreateShader));
                GLES20.glDeleteShader(glCreateShader);
                return UNIFORM_MVP_MATRIX;
            }
        }
        return glCreateShader;
    }
}
