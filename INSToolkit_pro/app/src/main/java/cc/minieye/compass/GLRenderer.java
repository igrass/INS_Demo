package cc.minieye.compass;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.DisplayMetrics;

import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import cc.minieye.instoolkit.R;
import cc.minieye.kalman.core.attitude.AttitudeFilter;
import cc.minieye.kalman.util.Constants;
import opengl.GLTextClass;
import opengl.ShaderManager;
import opengl.ShaderManager.Attribute;
import opengl.Shaders;
import opengl.Sphere;

public final class GLRenderer implements Renderer {
    public static final float NO_DIMENSION = -1.0f;
    public static int OPT_FPS;
    private static int SHADER_ATTRIB_COLOR;
    private static int SHADER_ATTRIB_NORMAL;
    private static int SHADER_ATTRIB_TEXTURE0_COORDS;
    private static int SHADER_ATTRIB_VERTEX;
    private float WidthHeightRatio;
    private AttitudeFilter attitude;
    private int cfps;
    private float farClipPlane;
    private int fps;
    private Context mContext;
    private Runnable mFPSTimerTask;
    private Handler mHandlerTimer;
    private int mScreenHeight;
    private int mScreenWidth;
    private float[] m_LightPosition;
    private float[] m_MMatrix;
    private float[] m_MVMatrix;
    private float[] m_MVPMatrix;
    private float[] m_ProjMatrix;
    private float[] m_ProjMatrixText;
    private float[] m_WorldMatrix;
    private float nearClipPlane;
    private boolean optPrintFps;
    private float pitch;
    private float roll;
    public ShaderManager shaderCompass;
    public ShaderManager shaderGlass;
    private Sphere sphere;
    private Sphere sphereGlass;
    private int texGlId;
    private int texResourceId;
    private GLTextClass textCaption;
    protected float[] textColor;
    private float yaw;

    static {
        OPT_FPS = 10;
        SHADER_ATTRIB_VERTEX = 1;
        SHADER_ATTRIB_COLOR = 2;
        SHADER_ATTRIB_NORMAL = 3;
        SHADER_ATTRIB_TEXTURE0_COORDS = 4;
    }

    public GLRenderer(Context context, DisplayMetrics displayMetrics, AttitudeFilter attitudeFilter) {
        this.m_MVMatrix = new float[16];
        this.m_MVPMatrix = new float[16];
        this.m_ProjMatrix = new float[16];
        this.m_ProjMatrixText = new float[16];
        this.m_MMatrix = new float[16];
        this.m_WorldMatrix = new float[16];
        this.m_LightPosition = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
        this.texGlId = 0;
        this.texResourceId = R.drawable.compass;
        this.sphere = null;
        this.sphereGlass = null;
        this.textColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.optPrintFps = true;
        this.fps = 0;
        this.cfps = 0;
        this.mHandlerTimer = new Handler();
        this.nearClipPlane = 0.85f;
        this.farClipPlane = 10.0f;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
        this.roll = 0.0f;
        this.shaderCompass = null;
        this.shaderGlass = null;
        this.attitude = null;
        this.mFPSTimerTask = new Runnable() {
            public void run() {
                GLRenderer.this.fps = GLRenderer.this.cfps / 2;
                GLRenderer.this.cfps = 0;
                GLRenderer.this.mHandlerTimer.postDelayed(GLRenderer.this.mFPSTimerTask, 2000);
            }
        };
        this.mContext = context;
        this.mScreenWidth = 800;
        this.mScreenHeight = 600;
        setDisplay(displayMetrics);
        this.mHandlerTimer.removeCallbacks(this.mFPSTimerTask);
        this.mHandlerTimer.postDelayed(this.mFPSTimerTask, 1000);
        this.attitude = attitudeFilter;
    }

    private void draw(GL11 gl11, float f, float f2, float f3) {
        GLES20.glUseProgram(this.shaderCompass.program);
        ShaderManager.checkGlError("glUseProgram::shaderCompass");
        float[] fArr = (float[]) this.m_MVMatrix.clone();
        float[] fArr2 = (float[]) this.m_MVPMatrix.clone();
        float[] fArr3 = new float[16];
        Matrix.setIdentityM(fArr3, 0);
        Matrix.rotateM(fArr3, 0, 90.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(fArr3, 0, f, NO_DIMENSION, 0.0f, 0.0f);
        Matrix.rotateM(fArr3, 0, f2, 0.0f, NO_DIMENSION, 0.0f);
        Matrix.rotateM(fArr3, 0, f3, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(fArr2, 0, this.m_WorldMatrix, 0, fArr3, 0);
        Matrix.multiplyMM(fArr2, 0, this.m_ProjMatrix, 0, fArr2, 0);
        GLES20.glUniformMatrix4fv(this.shaderCompass.m_UniformIDs[4], 1, false, fArr, 0);
        GLES20.glUniformMatrix4fv(this.shaderCompass.m_UniformIDs[0], 1, false, fArr2, 0);
        GLES20.glUniform3fv(this.shaderCompass.m_UniformIDs[2], 1, this.m_LightPosition, 0);
        this.sphere.drawES2(SHADER_ATTRIB_VERTEX, SHADER_ATTRIB_NORMAL, -1, SHADER_ATTRIB_TEXTURE0_COORDS, this.texGlId);
        GLES20.glUseProgram(this.shaderGlass.program);
        ShaderManager.checkGlError("glUseProgram::shaderGlass");
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        GLES20.glUniform1f(this.shaderGlass.m_UniformIDs[6], 0.7f);
        GLES20.glUniformMatrix4fv(this.shaderGlass.m_UniformIDs[4], 1, false, this.m_MVMatrix, 0);
        GLES20.glUniformMatrix4fv(this.shaderGlass.m_UniformIDs[0], 1, false, this.m_MVPMatrix, 0);
        GLES20.glUniform3fv(this.shaderGlass.m_UniformIDs[2], 1, this.m_LightPosition, 0);
        this.sphereGlass.drawES2(SHADER_ATTRIB_VERTEX, SHADER_ATTRIB_NORMAL, -1, SHADER_ATTRIB_TEXTURE0_COORDS, 0);
        GLES20.glDisable(3042);
    }

    private void drawTextInformation(GL11 gl11) {
        if (this.optPrintFps) {
            this.textCaption.printES2(this.fps + " FPS", this.textCaption.getColumnCount() - 8, this.textCaption.getLineCount() - 3, this.textColor, this.m_ProjMatrixText);
            this.textCaption.printES2("P:" + String.format(Locale.US, "%5.1f", new Object[]{Float.valueOf(this.pitch)}) + " R:" + String.format(Locale.US, "%5.1f", new Object[]{Float.valueOf(this.roll)}) + " Y:" + String.format(Locale.US, "%5.1f", new Object[]{Float.valueOf(this.yaw)}), this.textCaption.getColumnCount() - 25, this.textCaption.getLineCount() - 1, this.textColor, this.m_ProjMatrixText);
            this.cfps++;
        }
    }

    public void createShaders() {
        if (this.shaderCompass == null) {
            this.shaderCompass = new ShaderManager();
            this.shaderCompass.createProgram(Shaders.m_DefaultSphereVertexShader, Shaders.m_DefaultSphereFragmentShader, new Attribute[]{new Attribute(SHADER_ATTRIB_VERTEX, "aPosition"), new Attribute(SHADER_ATTRIB_NORMAL, "aNormal"), new Attribute(SHADER_ATTRIB_TEXTURE0_COORDS, "aTextureCoord")});
        }
        if (this.shaderGlass == null) {
            this.shaderGlass = new ShaderManager();
            this.shaderGlass.createProgram(Shaders.m_DefaultSphereVertexShader, Shaders.m_BlendingFragmentShader, new Attribute[]{new Attribute(SHADER_ATTRIB_VERTEX, "aPosition"), new Attribute(SHADER_ATTRIB_NORMAL, "aNormal"), new Attribute(SHADER_ATTRIB_TEXTURE0_COORDS, "aTextureCoord")});
        }
    }

    public int getHeight() {
        return this.mScreenHeight;
    }

    public int getWidth() {
        return this.mScreenWidth;
    }

    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16640);
        Matrix.setIdentityM(this.m_MMatrix, 0);
        Matrix.multiplyMM(this.m_MVMatrix, 0, this.m_WorldMatrix, 0, this.m_MMatrix, 0);
        Matrix.multiplyMM(this.m_MVPMatrix, 0, this.m_ProjMatrix, 0, this.m_MVMatrix, 0);
        GLES20.glEnable(2929);
        GLES20.glEnable(2884);
        this.pitch = (float) (this.attitude.getEstimatedPitchAngle() * Constants.RAD2DEG);
        this.yaw = (float) (this.attitude.getEstimatedYawAngle() * Constants.RAD2DEG);
        this.roll = (float) (this.attitude.getEstimatedRollAngle() * Constants.RAD2DEG);
        draw((GL11) gl10, this.pitch, this.roll, this.yaw);
        drawTextInformation((GL11) gl10);
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        setDisplay(i2, i);
        GLES20.glViewport(0, 0, i, i2);
        Matrix.orthoM(this.m_ProjMatrixText, 0, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        GLES20.glDisable(3024);
        GLES20.glEnable(2929);
        GLES20.glDepthFunc(515);
        GLES20.glClearDepthf(1.0f);
        GLES20.glDepthRangef(0.0f, 5.0f);
        Matrix.setLookAtM(this.m_WorldMatrix, 0, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        this.WidthHeightRatio = ((float) getWidth()) / ((float) getHeight());
        Matrix.frustumM(this.m_ProjMatrix, 0, -this.WidthHeightRatio, this.WidthHeightRatio, NO_DIMENSION, 1.0f, this.nearClipPlane, this.farClipPlane);
        GLES20.glBlendFunc(770, 771);
        this.textCaption = new GLTextClass((GL11) gl10, "Arial", 18);
        this.textCaption.setScreenDimension(this.mScreenWidth, this.mScreenHeight);
        this.textCaption.createShader(gl10);
        createShaders();
        this.texGlId = ShaderManager.createTexture((GL11) gl10, this.mContext, this.texResourceId);
        this.sphere = new Sphere(this.mContext, 1.0f, true, 64, 64);
        this.sphereGlass = new Sphere(this.mContext, 1.1f, true, 64, 64);
    }

    public void setDisplay(int i, int i2) {
        this.mScreenHeight = i;
        this.mScreenWidth = i2;
    }

    public void setDisplay(DisplayMetrics displayMetrics) {
        this.mScreenHeight = displayMetrics.heightPixels;
        this.mScreenWidth = displayMetrics.widthPixels;
    }

    public void setOptions(int i, boolean z) {
        if (i == OPT_FPS) {
            this.optPrintFps = z;
        }
    }
}
