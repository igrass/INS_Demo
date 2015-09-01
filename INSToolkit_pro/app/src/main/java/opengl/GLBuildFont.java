package opengl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL11;

public class GLBuildFont {
    public int charHeight;
    public int[] charWidth;
    public int colCount;
    public int defaultCharWidth;
    public int firstCharOffset;
    public int fntCellHeight;
    public int fntCellWidth;
    public int fntTexHeight;
    public int fntTexWidth;
    private String fontName;
    private int fontSize;
    public int margin;
    public int rowCount;
    public int texID;

    public GLBuildFont(GL11 gl11, String str, int i) {
        this.fntTexWidth = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        this.fntTexHeight = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        this.fntCellWidth = 32;
        this.fntCellHeight = 32;
        this.firstCharOffset = 0;
        this.margin = 10;
        this.fontSize = i;
        this.fontName = str;
        if (gl11 != null) {
            this.charWidth = new int[WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN];
            int[] iArr = new int[1];
            gl11.glGenTextures(1, iArr, 0);
            this.texID = iArr[0];
            gl11.glBindTexture(3553, this.texID);
            createFont(gl11);
        }
    }

    public void createFont(GL11 gl11) {
        this.fntTexWidth = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.fntTexHeight = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.fntCellWidth = 10;
        this.fntCellHeight = 16;
        this.fontSize = 15;
        Bitmap createBitmap = Bitmap.createBitmap(this.fntTexWidth, this.fntTexHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        createBitmap.eraseColor(0);
        Paint paint = new Paint();
        paint.setTextSize((float) this.fontSize);
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setARGB(255, 255, 255, 255);
        Rect rect = new Rect();
        paint.getTextBounds("l", 0, "l".length(), rect);
        this.charHeight = rect.height();
        this.colCount = this.fntTexWidth / this.fntCellWidth;
        this.rowCount = (this.fntTexHeight / 2) / this.colCount;
        this.firstCharOffset = 0;
        int i = 0;
        for (int i2 = 0; i2 < this.rowCount; i2++) {
            int i3 = 0;
            while (i3 < this.colCount) {
                String ch = Character.toString((char) i);
                this.charWidth[i] = (int) paint.measureText(ch);
                canvas.drawText(ch, 0.0f + ((float) (this.fntCellWidth * i3)), (float) (this.fntCellHeight * (i2 + 1)), paint);
                i3++;
                i++;
            }
        }
        int[] iArr = new int[1];
        gl11.glGenTextures(1, iArr, 0);
        this.texID = iArr[0];
        gl11.glBindTexture(3553, this.texID);
        gl11.glTexParameterf(3553, 10241, 9728.0f);
        gl11.glTexParameterf(3553, 10240, 9729.0f);
        gl11.glTexParameterf(3553, 10242, 33071.0f);
        gl11.glTexParameterf(3553, 10243, 33071.0f);
        GLUtils.texImage2D(3553, 0, createBitmap, 0);
        this.defaultCharWidth = this.charWidth[32];
        this.margin = this.charHeight / 5;
        createBitmap.recycle();
    }

    public final String getFontName() {
        return this.fontName;
    }
}
