package cc.minieye.instoolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import java.util.Locale;

import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public class FormCompass {
    double RAD2DEG;
    private CameraPosPlain cam;
    private float cardinalCircleDepth;
    private float cardinalCircleRadius;
    private int colorBackground;
    private int colorPanel;
    private int colorPointsCardinaux;
    private int colorTicks;
    private double cvLength2Unit;
    private double cvVelocity2Unit;
    boolean enDistanceMode;
    private float mScreenHeight;
    private float mScreenWidth;
    private Paint paint_arrow;
    private Paint paint_black;
    private Paint paint_info_filled;
    private Paint paint_info_frame;
    private Paint paint_point;
    private Paint paint_text;
    private String str_pos_unit;
    private String str_vel_unit;

    public FormCompass(Context context, DisplayMetrics displayMetrics) {
        this.RAD2DEG = Constants.RAD2DEG;
        this.paint_text = new Paint();
        this.paint_info_filled = new Paint();
        this.paint_info_frame = new Paint();
        this.paint_point = new Paint();
        this.paint_arrow = new Paint();
        this.colorPanel = Color.rgb(60, 60, 60);
        this.colorBackground = Color.rgb(124, 124, 124);
        this.colorPointsCardinaux = Color.rgb(154, 124, 224);
        this.colorTicks = Color.rgb(254, 254, 254);
        this.cardinalCircleRadius = 0.0f;
        this.cardinalCircleDepth = 0.0f;
        this.str_pos_unit = "m";
        this.cvLength2Unit = 1.0d;
        this.str_vel_unit = "m/s";
        this.cvVelocity2Unit = 1.0d;
        this.cam = new CameraPosPlain();
        this.enDistanceMode = false;
        setSize(displayMetrics);
        this.paint_text.setColor(-7829368);
        this.paint_text.setTextSize(40.0f);
        this.paint_text.setAntiAlias(true);
        this.paint_text.setStyle(Style.STROKE);
        this.paint_text.setTypeface(Typeface.MONOSPACE);
        this.paint_info_filled.setColor(-1);
        this.paint_info_filled.setTextSize(24.0f);
        this.paint_info_filled.setAntiAlias(true);
        this.paint_info_filled.setStyle(Style.FILL);
        this.paint_info_frame.setColor(-12303292);
        this.paint_info_frame.setAntiAlias(true);
        this.paint_info_frame.setStyle(Style.STROKE);
        this.paint_info_frame.setStrokeWidth(1.5f);
        this.paint_arrow.setColor(-65536);
        this.paint_arrow.setAntiAlias(true);
        this.paint_arrow.setStrokeWidth(1.5f);
        this.paint_arrow.setStyle(Style.FILL);
        this.paint_point.setColor(-16776961);
        this.paint_point.setAntiAlias(true);
        this.paint_point.setStyle(Style.STROKE);
        this.paint_point.setStrokeWidth(1.5f);
        this.paint_black = new Paint(this.paint_info_filled);
        this.paint_black.setColor(-16777216);
    }

    private void plotCompass(Canvas canvas, float f, float f2, float f3, double d) {
        float cos = (float) (((double) f3) * Math.cos(30.0d / this.RAD2DEG));
        float sin = (float) (((double) (2.0f * f3)) * Math.sin(30.0d / this.RAD2DEG));
        double[][] r6 = new double[2][];
        r6[0] = new double[]{Math.cos(d), -Math.sin(d)};
        r6[1] = new double[]{Math.sin(d), Math.cos(d)};
        CMatrix cMatrix = new CMatrix(r6);
        CVector times = cMatrix.times(new CVector(new double[]{0.0d, (double) (cos / 2.0f)}));
        CVector times2 = cMatrix.times(new CVector(new double[]{(double) (sin / 4.0f), (double) ((-cos) / 3.0f)}));
        CVector times3 = cMatrix.times(new CVector(new double[]{(double) ((-sin) / 4.0f), (double) ((-cos) / 3.0f)}));
        CVector times4 = cMatrix.times(new CVector(new double[]{0.0d, -0.15d * ((double) cos)}));
        float[] fArr = new float[]{((float) times.getElement(1)) + f, f2 - ((float) times.getElement(2)), ((float) times2.getElement(1)) + f, f2 - ((float) times2.getElement(2)), ((float) times4.getElement(1)) + f, f2 - ((float) times4.getElement(2)), ((float) times3.getElement(1)) + f, f2 - ((float) times3.getElement(2))};
        Path path = new Path();
        this.paint_arrow.setColor(-65536);
        path.moveTo(fArr[0], fArr[1]);
        path.lineTo(fArr[2], fArr[3]);
        path.lineTo(fArr[4], fArr[5]);
        canvas.drawPath(path, this.paint_arrow);
        this.paint_arrow.setColor(-16776961);
        path.rewind();
        path.moveTo(fArr[0], fArr[1]);
        path.lineTo(fArr[6], fArr[7]);
        path.lineTo(fArr[4], fArr[5]);
        canvas.drawPath(path, this.paint_arrow);
        this.paint_point.setColor(this.colorTicks);
        for (int i = 0; i < 360; i += 5) {
            path.rewind();
            float cos2 = (float) Math.cos((double) ((3.1415927f * ((float) i)) / 180.0f));
            float sin2 = (float) Math.sin((double) ((3.1415927f * ((float) i)) / 180.0f));
            path.moveTo(((this.cardinalCircleRadius - this.cardinalCircleDepth) * cos2) + f, ((this.cardinalCircleRadius - this.cardinalCircleDepth) * sin2) + f2);
            path.lineTo((cos2 * this.cardinalCircleRadius) + f, (sin2 * this.cardinalCircleRadius) + f2);
            canvas.drawPath(path, this.paint_point);
        }
        this.paint_text.setColor(this.colorPointsCardinaux);
        canvas.save();
        canvas.rotate(0.0f, f, f2);
        Canvas canvas2 = canvas;
        canvas2.drawText("S", f, (1.2f * this.cardinalCircleRadius) + f2, this.paint_text);
        canvas.rotate(90.0f, f, f2);
        canvas2 = canvas;
        canvas2.drawText("W", f, (1.2f * this.cardinalCircleRadius) + f2, this.paint_text);
        canvas.rotate(90.0f, f, f2);
        canvas2 = canvas;
        canvas2.drawText("N", f, (1.2f * this.cardinalCircleRadius) + f2, this.paint_text);
        canvas.rotate(90.0f, f, f2);
        canvas2 = canvas;
        canvas2.drawText("E", f, (1.2f * this.cardinalCircleRadius) + f2, this.paint_text);
        canvas.restore();
    }

    private void printAngle(Canvas canvas, double d) {
        String str = "";
        if (d < 22.0d) {
            str = "N";
        } else if (d >= 22.0d && d < 67.0d) {
            str = "NE";
        } else if (d >= 67.0d && d < 112.0d) {
            str = "E";
        } else if (d >= 112.0d && d < 157.0d) {
            str = "SE";
        } else if (d >= 157.0d && d < 202.0d) {
            str = "S";
        } else if (d >= 202.0d && d < 247.0d) {
            str = "SW";
        } else if (d >= 247.0d && d < 292.0d) {
            str = "W";
        } else if (d >= 292.0d && d < 337.0d) {
            str = "NW";
        } else if (d >= 337.0d) {
            str = "N";
        }
        str = String.format(Locale.US, "%s %05.1f", new Object[]{str, Double.valueOf(d)});
        Rect rect = new Rect();
        this.paint_text.getTextBounds(str, 0, str.length(), rect);
        float width = (this.mScreenWidth / 2.0f) - ((float) (rect.width() / 2));
        float height = (this.mScreenHeight / 2.0f) + ((float) (rect.height() / 2));
        RectF rectF = new RectF();
        rectF.left = width - 5.0f;
        rectF.right = (((float) rect.width()) + width) + (4.0f * 5.0f);
        rectF.bottom = height + 5.0f;
        rectF.top = (height - ((float) rect.height())) - (2.0f * 5.0f);
        this.paint_text.setColor(-7829368);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        canvas.drawText(str, width, height, this.paint_text);
    }

    public void DrawBoard(Canvas canvas, double d, double d2) {
        float f = this.mScreenWidth / 2.0f;
        float f2 = this.mScreenHeight / 2.0f;
        float min = Math.min(this.mScreenHeight, this.mScreenWidth);
        this.cardinalCircleRadius = (0.8f * min) / 2.0f;
        this.cardinalCircleDepth = (0.1f * min) / 2.0f;
        canvas.drawColor(this.colorPanel);
        this.paint_black.setColor(-16777216);
        canvas.drawCircle(f, f2, (1.0f * min) / 2.0f, this.paint_black);
        this.paint_black.setColor(this.colorBackground);
        canvas.drawCircle(f, f2, this.cardinalCircleRadius, this.paint_black);
        this.paint_black.setColor(-16777216);
        canvas.drawCircle(f, f2, this.cardinalCircleRadius - this.cardinalCircleDepth, this.paint_black);
        Canvas canvas2 = canvas;
        plotCompass(canvas2, f, f2, 0.8f * min, (double) ((float) (d2 / this.RAD2DEG)));
        printAngle(canvas, d2);
    }

    public void setSize(DisplayMetrics displayMetrics) {
        this.mScreenHeight = (float) displayMetrics.heightPixels;
        this.mScreenWidth = (float) displayMetrics.widthPixels;
    }

    public void setUnitsMode(int i, String str, double d, String str2, double d2) {
        this.str_pos_unit = str;
        this.str_vel_unit = str2;
        this.cvLength2Unit = d;
        this.cvVelocity2Unit = d2;
    }
}
