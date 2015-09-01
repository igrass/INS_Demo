package opengl;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import opengl.ShaderManager.Attribute;

public final class GLTextClass {
    private static int ATTRIB_TEXTURE0_COORDS;
    private static int ATTRIB_VERTEX;
    private static int maxNbChar;
    private int[] UVarray;
    private float curX;
    private float curY;
    private float curZ;
    private float[] defaultColor;
    private String fontName;
    private int fontSize;
    private int handleColor;
    GLBuildFont mFontBuilder;
    private float[] m_newMVPMatrix;
    MatrixGrabber mg;
    private int screenHeight;
    private int screenWidth;
    private ShaderManager shaderText;
    private FloatBuffer texBuffer;
    private float[] uvs;
    private float[] ver;
    private FloatBuffer vertexBuffer;
    private float[] vertices;
    private float xScale;
    private float yScale;

    static {
        maxNbChar = 30;
        ATTRIB_VERTEX = 1;
        ATTRIB_TEXTURE0_COORDS = 2;
    }

    public GLTextClass(GL11 gl11, String str, int i) {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.screenWidth = 600;
        this.screenHeight = 600;
        this.vertices = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
        this.m_newMVPMatrix = new float[16];
        this.defaultColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.curZ = 0.0f;
        this.curY = 0.0f;
        this.curX = 0.0f;
        SetScale(1.0f);
        this.UVarray = new int[4];
        this.fontSize = i;
        this.fontName = str;
        this.vertexBuffer = new newDirectFloatBuffer((maxNbChar * 3) * 6).getBuffer();
        this.vertexBuffer.put(this.vertices);
        this.vertexBuffer.rewind();
        this.texBuffer = new newDirectFloatBuffer((maxNbChar * 2) * 6).getBuffer();
        this.mFontBuilder = new GLBuildFont(gl11, str, i);
        this.mg = new MatrixGrabber();
    }

    public GLTextClass(GL11 gl11, GLBuildFont gLBuildFont) {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.screenWidth = 600;
        this.screenHeight = 600;
        this.vertices = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
        this.m_newMVPMatrix = new float[16];
        this.defaultColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.curZ = 0.0f;
        this.curY = 0.0f;
        this.curX = 0.0f;
        SetScale(1.0f);
        this.UVarray = new int[4];
        this.mFontBuilder = gLBuildFont;
        this.vertexBuffer = new newDirectFloatBuffer((maxNbChar * 3) * 4).getBuffer();
        this.vertexBuffer.put(this.vertices);
        this.vertexBuffer.rewind();
        this.texBuffer = new newDirectFloatBuffer((maxNbChar * 2) * 4).getBuffer();
        this.mg = new MatrixGrabber();
    }

    private float getCharHeight() {
        return ((float) (this.mFontBuilder.fntCellHeight + this.mFontBuilder.margin)) * this.yScale;
    }

    private float getCharWidth() {
        return ((float) this.mFontBuilder.fntCellWidth) * this.xScale;
    }

    private float getLineX(int i) {
        return ((float) i) / ((float) getColumnCount());
    }

    private float getLineY(int i) {
        return ((float) i) / ((float) getLineCount());
    }

    private void print(GL11 gl11, String str) {
        float f = this.curX;
        float f2 = this.curY;
        float f3 = this.curZ;
        float f4 = this.xScale * ((float) this.mFontBuilder.fntCellWidth);
        float f5 = this.yScale * ((float) this.mFontBuilder.fntCellHeight);
        this.UVarray[2] = this.mFontBuilder.fntCellWidth;
        this.UVarray[3] = (-this.mFontBuilder.fntCellHeight) + 1;
        gl11.glEnable(3553);
        gl11.glBindTexture(3553, this.mFontBuilder.texID);
        gl11.glDisable(2896);
        gl11.glEnable(3042);
        for (int i = 0; i != str.length(); i++) {
            int charAt = str.charAt(i) - this.mFontBuilder.firstCharOffset;
            int i2 = charAt / this.mFontBuilder.colCount;
            this.UVarray[0] = (charAt % this.mFontBuilder.colCount) * this.mFontBuilder.fntCellWidth;
            this.UVarray[1] = ((i2 + 1) * this.mFontBuilder.fntCellHeight) + 1;
            gl11.glTexParameteriv(3553, 35741, this.UVarray, 0);
            ((GL11Ext) gl11).glDrawTexfOES(f, f2, f3, f4, f5);
            int i3 = this.mFontBuilder.firstCharOffset + charAt;
            if (i3 >= this.mFontBuilder.charWidth.length) {
                i3 = 0;
            }
            f += (((float) this.mFontBuilder.charWidth[i3]) * this.xScale) + 2.0f;
        }
        this.curX = (float) ((int) f);
        gl11.glDisable(3042);
    }

    private void printES2(String str, float[] fArr, float[] fArr2) {
        float f = this.curX;
        float f2 = this.curY;
        float f3 = ((float) this.mFontBuilder.fntCellWidth) / ((float) this.mFontBuilder.fntTexWidth);
        float f4 = ((float) this.mFontBuilder.fntCellHeight) / ((float) this.mFontBuilder.fntTexHeight);
        float f5 = ((float) this.mFontBuilder.margin) / ((float) this.mFontBuilder.fntTexHeight);
        float columnCount = 1.0f / ((float) getColumnCount());
        float lineCount = 1.0f / ((float) getLineCount());
        int length = str.length();
        float[] fArr3 = new float[12];
        float[] fArr4 = new float[18];
        this.texBuffer.clear();
        this.vertexBuffer.clear();
        int i = 0;
        float f6 = f;
        while (i != str.length()) {
            int charAt = str.charAt(i) - (this.mFontBuilder.firstCharOffset * 0);
            int i2 = charAt / this.mFontBuilder.colCount;
            fArr3[0] = (((float) ((charAt % this.mFontBuilder.colCount) + 0)) * f3) + 0.0f;
            fArr3[1] = ((((float) (i2 + 1)) * f4) + 0.0f) + f5;
            fArr3[2] = fArr3[0] + f3;
            fArr3[3] = fArr3[1];
            fArr3[4] = fArr3[0] + f3;
            fArr3[5] = fArr3[1] - f4;
            fArr3[6] = fArr3[0];
            fArr3[7] = fArr3[1];
            fArr3[8] = fArr3[4];
            fArr3[9] = fArr3[5];
            fArr3[10] = fArr3[0];
            fArr3[11] = fArr3[5];
            this.texBuffer.put(fArr3);
            float f7 = ((float) i) * columnCount;
            fArr4[0] = (this.vertices[0] * columnCount) + f7;
            fArr4[1] = this.vertices[1] * lineCount;
            fArr4[3] = (this.vertices[3] * columnCount) + f7;
            fArr4[4] = this.vertices[4] * lineCount;
            fArr4[6] = (this.vertices[9] * columnCount) + f7;
            fArr4[7] = this.vertices[10] * lineCount;
            fArr4[9] = (this.vertices[0] * columnCount) + f7;
            fArr4[10] = this.vertices[1] * lineCount;
            fArr4[12] = (this.vertices[9] * columnCount) + f7;
            fArr4[13] = this.vertices[10] * lineCount;
            fArr4[15] = f7 + (this.vertices[6] * columnCount);
            fArr4[16] = this.vertices[7] * lineCount;
            this.vertexBuffer.put(fArr4);
            charAt += this.mFontBuilder.firstCharOffset;
            if (charAt >= this.mFontBuilder.charWidth.length) {
                charAt = 0;
            }
            i++;
            f6 = ((((float) this.mFontBuilder.charWidth[charAt]) * this.xScale) + 2.0f) + f6;
        }
        GLES20.glUseProgram(this.shaderText.program);
        GLES20.glEnable(3042);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mFontBuilder.texID);
        this.vertexBuffer.rewind();
        GLES20.glVertexAttribPointer(ATTRIB_VERTEX, 3, 5126, false, 0, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(ATTRIB_VERTEX);
        GLES20.glUniform4fv(this.handleColor, 1, fArr, 0);
        GLES20.glUniformMatrix4fv(this.shaderText.m_UniformIDs[0], 1, false, fArr2, 0);
        this.texBuffer.rewind();
        GLES20.glVertexAttribPointer(ATTRIB_TEXTURE0_COORDS, 2, 5126, false, 0, this.texBuffer);
        GLES20.glEnableVertexAttribArray(ATTRIB_TEXTURE0_COORDS);
        GLES20.glDrawArrays(4, 0, length * 6);
        GLES20.glDisable(3042);
        this.curX = (float) ((int) f6);
    }

    public int GetTextHeight() {
        return (int) (((float) this.mFontBuilder.fntCellHeight) * this.yScale);
    }

    public int GetTextLength(String str) {
        float f = 0.0f;
        for (int i = 0; i != str.length(); i++) {
            f += this.xScale * ((float) this.mFontBuilder.charWidth[str.charAt(i)]);
        }
        return (int) f;
    }

    public void SetScale(float f) {
        this.xScale = f;
        this.yScale = (this.xScale * ((float) this.screenHeight)) / ((float) this.screenWidth);
    }

    public void SetScale(float f, float f2) {
        this.xScale = f;
        this.yScale = f2;
    }

    public void createShader(GL10 gl10) {
        if (this.shaderText == null) {
            Attribute[] attributeArr = new Attribute[]{new Attribute(ATTRIB_VERTEX, "aPosition"), new Attribute(ATTRIB_TEXTURE0_COORDS, "aTextureCoord")};
            this.shaderText = new ShaderManager();
            this.shaderText.createProgram(Shaders.m_PanelTexVertexShader, Shaders.m_PanelTexFragmentShader, attributeArr);
            this.handleColor = this.shaderText.m_UniformIDs[5];
        }
    }

    public int getColumnCount() {
        return (int) (((float) this.screenWidth) / getCharWidth());
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public int getLineCount() {
        return (int) ((((float) this.screenHeight) / getCharHeight()) - 1.0f);
    }

    public void print(GL11 gl11, String str, float f, float f2, float f3) {
        gl11.glMatrixMode(5889);
        gl11.glPushMatrix();
        gl11.glMatrixMode(5888);
        gl11.glPushMatrix();
        int[] iArr = new int[4];
        iArr[2] = this.screenWidth;
        iArr[3] = this.screenHeight;
        float[] fArr = new float[3];
        gl11.glGetIntegerv(2978, iArr, 0);
        this.mg.getCurrentProjection(gl11);
        float[] fArr2 = new float[16];
        float[] fArr3 = this.mg.mProjection;
        this.mg.getCurrentModelView(gl11);
        fArr2 = new float[16];
        int gluProject = GLU.gluProject(f, f2, f3, this.mg.mModelView, 0, fArr3, 0, iArr, 0, fArr, 0);
        gl11.glMatrixMode(5889);
        gl11.glPopMatrix();
        gl11.glMatrixMode(5888);
        gl11.glPopMatrix();
        if (gluProject == 1) {
            setCursor(fArr[0], fArr[1], fArr[2]);
            print(gl11, str);
        }
    }

    public void print(GL11 gl11, String str, int i, int i2) {
        print(gl11, str, i, i2, this.defaultColor);
    }

    public void print(GL11 gl11, String str, int i, int i2, float[] fArr) {
        gl11.glColor4f(fArr[0], fArr[1], fArr[2], 1.0f);
        setCursor(this.mFontBuilder.defaultCharWidth * i, (this.mFontBuilder.charHeight + this.mFontBuilder.margin) * i2);
        print(gl11, str);
        gl11.glColor4f(this.defaultColor[0], this.defaultColor[1], this.defaultColor[2], 1.0f);
    }

    public void print3d(GL11 gl11, String str, float f, float f2, float f3) {
        float f4 = 0.0f;
        if (str.length() != 0) {
            gl11.glMatrixMode(5888);
            gl11.glPushMatrix();
            gl11.glTranslatef(f, f2, 0.0f * f3);
            float f5 = this.xScale * ((float) this.mFontBuilder.fntCellWidth);
            float f6 = this.yScale * ((float) this.mFontBuilder.fntCellHeight);
            float f7 = (float) (this.mFontBuilder.fntCellWidth / this.mFontBuilder.fntTexWidth);
            float f8 = (float) (this.mFontBuilder.fntCellHeight / this.mFontBuilder.fntTexHeight);
            gl11.glEnable(3553);
            gl11.glBindTexture(3553, this.mFontBuilder.texID);
            gl11.glDisable(2896);
            gl11.glEnableClientState(32884);
            gl11.glEnableClientState(32888);
            this.vertexBuffer.clear();
            this.texBuffer.clear();
            int length = str.length();
            this.uvs = new float[8];
            this.ver = new float[12];
            for (int i = 0; i < length; i++) {
                float[] fArr = this.ver;
                float f9 = f4 / 30.0f;
                this.ver[6] = f9;
                fArr[0] = f9;
                fArr = this.ver;
                f9 = this.ver[1] + (f5 / 30.0f);
                this.ver[9] = f9;
                fArr[3] = f9;
                fArr = this.ver;
                f9 = f6 / 30.0f;
                this.ver[10] = f9;
                fArr[7] = f9;
                this.vertexBuffer.put(this.ver);
                int charAt = str.charAt(i) - this.mFontBuilder.firstCharOffset;
                int i2 = charAt % this.mFontBuilder.colCount;
                int i3 = charAt / this.mFontBuilder.colCount;
                this.uvs[0] = ((float) i2) * f7;
                this.uvs[1] = ((float) (i3 + 1)) * f8;
                this.uvs[2] = ((float) (i2 + 1)) * f7;
                this.uvs[3] = this.uvs[1];
                this.uvs[4] = this.uvs[0];
                this.uvs[5] = ((float) (i3 + 0)) * f8;
                this.uvs[6] = this.uvs[2];
                this.uvs[7] = this.uvs[5];
                this.texBuffer.put(this.uvs);
                f4 += (((float) this.mFontBuilder.charWidth[charAt + this.mFontBuilder.firstCharOffset]) * this.xScale) + 2.0f;
                this.vertexBuffer.rewind();
                this.texBuffer.rewind();
                gl11.glVertexPointer(3, 5126, 0, this.vertexBuffer);
                gl11.glTexCoordPointer(2, 5126, 0, this.texBuffer);
                gl11.glDrawArrays(5, 0, 4);
                this.vertexBuffer.clear();
                this.texBuffer.clear();
            }
            gl11.glDisableClientState(32884);
            gl11.glDisableClientState(32888);
            gl11.glDisable(3553);
            gl11.glMatrixMode(5888);
            gl11.glPopMatrix();
        }
    }

    public void print3dES2(GL11 gl11, String str, float f, float f2, float f3, float[] fArr) {
        if (str.length() != 0) {
            gl11.glMatrixMode(5888);
            gl11.glPushMatrix();
            float[] fArr2 = new float[16];
            Matrix.setIdentityM(fArr2, 0);
            Matrix.translateM(fArr2, 0, (((float) this.mFontBuilder.defaultCharWidth) * f) / ((float) this.mFontBuilder.fntTexWidth), (((float) (this.mFontBuilder.charHeight + this.mFontBuilder.margin)) * f2) / ((float) this.mFontBuilder.fntTexHeight), 0.0f);
            Matrix.multiplyMM(fArr2, 0, fArr, 0, fArr2, 0);
            float f4 = ((float) this.mFontBuilder.fntCellWidth) * this.xScale;
            float f5 = ((float) this.mFontBuilder.fntCellHeight) * this.yScale;
            float f6 = (float) (this.mFontBuilder.fntCellWidth / this.mFontBuilder.fntTexWidth);
            float f7 = (float) (this.mFontBuilder.fntCellHeight / this.mFontBuilder.fntTexHeight);
            gl11.glEnable(3553);
            gl11.glBindTexture(3553, this.mFontBuilder.texID);
            gl11.glDisable(2896);
            gl11.glEnableClientState(32884);
            gl11.glEnableClientState(32888);
            this.vertexBuffer.clear();
            this.texBuffer.clear();
            int length = str.length();
            this.uvs = new float[8];
            this.ver = new float[12];
            float f8 = 0.0f;
            for (int i = 0; i < length; i++) {
                float[] fArr3 = this.ver;
                float f9 = f8 / 30.0f;
                this.ver[6] = f9;
                fArr3[0] = f9;
                fArr3 = this.ver;
                f9 = this.ver[1] + (f4 / 30.0f);
                this.ver[9] = f9;
                fArr3[3] = f9;
                fArr3 = this.ver;
                f9 = f5 / 30.0f;
                this.ver[10] = f9;
                fArr3[7] = f9;
                this.vertexBuffer.put(this.ver);
                int charAt = str.charAt(i) - this.mFontBuilder.firstCharOffset;
                int i2 = charAt % this.mFontBuilder.colCount;
                int i3 = charAt / this.mFontBuilder.colCount;
                this.uvs[0] = ((float) i2) * f6;
                this.uvs[1] = ((float) (i3 + 1)) * f7;
                this.uvs[2] = ((float) (i2 + 1)) * f6;
                this.uvs[3] = this.uvs[1];
                this.uvs[4] = this.uvs[0];
                this.uvs[5] = ((float) (i3 + 0)) * f7;
                this.uvs[6] = this.uvs[2];
                this.uvs[7] = this.uvs[5];
                this.texBuffer.put(this.uvs);
                f8 += (((float) this.mFontBuilder.charWidth[charAt + this.mFontBuilder.firstCharOffset]) * this.xScale) + 2.0f;
                this.vertexBuffer.rewind();
                this.texBuffer.rewind();
                gl11.glVertexPointer(3, 5126, 0, this.vertexBuffer);
                gl11.glTexCoordPointer(2, 5126, 0, this.texBuffer);
                gl11.glDrawArrays(5, 0, 4);
                this.vertexBuffer.clear();
                this.texBuffer.clear();
            }
            gl11.glDisableClientState(32884);
            gl11.glDisableClientState(32888);
            gl11.glDisable(3553);
            gl11.glMatrixMode(5888);
            gl11.glPopMatrix();
        }
    }

    public void printES2(String str, int i, int i2, float[] fArr, float[] fArr2) {
        setCursor(this.mFontBuilder.defaultCharWidth * i, (this.mFontBuilder.charHeight + this.mFontBuilder.margin) * i2);
        Matrix.setIdentityM(this.m_newMVPMatrix, 0);
        Matrix.translateM(this.m_newMVPMatrix, 0, getLineX(i), getLineY(i2), 0.0f);
        Matrix.multiplyMM(this.m_newMVPMatrix, 0, fArr2, 0, this.m_newMVPMatrix, 0);
        printES2(str, fArr, this.m_newMVPMatrix);
    }

    public void setCursor(float f, float f2, float f3) {
        this.curX = f;
        this.curY = f2;
        this.curZ = f3;
    }

    public void setCursor(int i, int i2) {
        this.curX = (float) i;
        this.curY = (float) i2;
        this.curZ = 0.0f;
    }

    public void setScreenDimension(int i, int i2) {
        this.screenWidth = i;
        this.screenHeight = i2;
    }
}
