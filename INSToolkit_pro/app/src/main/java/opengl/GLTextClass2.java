package opengl;

import android.opengl.GLU;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public final class GLTextClass2 {
    private static int maxNbChar;
    private int[] UVarray;
    private float curX;
    private float curY;
    private float curZ;
    private float[] defaultColor;
    private String fontName;
    private int fontSize;
    GLBuildFont mFontBuilder;
    MatrixGrabber mg;
    private int screenHeight;
    private int screenWidth;
    private FloatBuffer texBuffer;
    private float[] uvs;
    private float[] ver;
    private FloatBuffer vertexBuffer;
    private float[] vertices;
    private float xScale;
    private float yScale;

    static {
        maxNbChar = 20;
    }

    public GLTextClass2(GL11 gl11, String str, int i) {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.screenWidth = 600;
        this.screenHeight = 600;
        this.vertices = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
        this.defaultColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.curZ = 0.0f;
        this.curY = 0.0f;
        this.curX = 0.0f;
        this.yScale = 1.0f;
        this.xScale = 1.0f;
        this.UVarray = new int[4];
        this.fontSize = i;
        this.fontName = str;
        this.vertexBuffer = new newDirectFloatBuffer(maxNbChar * 4).getBuffer();
        this.vertexBuffer.put(this.vertices);
        this.vertexBuffer.rewind();
        this.texBuffer = new newDirectFloatBuffer((maxNbChar * 2) * 4).getBuffer();
        this.mFontBuilder = new GLBuildFont(gl11, str, i);
        this.mg = new MatrixGrabber();
    }

    public GLTextClass2(GL11 gl11, GLBuildFont gLBuildFont) {
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.screenWidth = 600;
        this.screenHeight = 600;
        this.vertices = new float[]{0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
        this.defaultColor = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        this.curZ = 0.0f;
        this.curY = 0.0f;
        this.curX = 0.0f;
        this.yScale = 1.0f;
        this.xScale = 1.0f;
        this.UVarray = new int[4];
        this.mFontBuilder = gLBuildFont;
        this.vertexBuffer = new newDirectFloatBuffer(maxNbChar * 4).getBuffer();
        this.vertexBuffer.put(this.vertices);
        this.vertexBuffer.rewind();
        this.texBuffer = new newDirectFloatBuffer((maxNbChar * 2) * 4).getBuffer();
        this.mg = new MatrixGrabber();
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
        this.yScale = f;
        this.xScale = f;
    }

    public void SetScale(float f, float f2) {
        this.xScale = f;
        this.yScale = f2;
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public int lastColumn() {
        return (int) (((float) this.screenWidth) / (((float) this.mFontBuilder.defaultCharWidth) * this.xScale));
    }

    public int lastLine() {
        return (int) (((float) this.screenHeight) / (((float) this.mFontBuilder.charHeight) * this.yScale));
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
