package cc.minieye.kalman.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;

import java.util.Locale;

import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;
import cc.minieye.objects.GPSListener;
import opengl.ShaderManager;
import opengl.Sphere;

public final class FormHorizon {
    private float AltTick2Degree;
    private float Hauteur;
    private float Largeur;
    private float PasAssiette;
    double RAD2DEG;
    private float SpeedTick2Degree;
    private double cvLength2Unit;
    private double cvVelocity2Unit;
    private boolean hud_mode;
    private int iFilterStatus;
    private int iGPSStatus;
    private boolean iMagDisturbStatus;
    private float mScreenHeight;
    private float mScreenWidth;
    private Paint paint_ground;
    private Paint paint_horizon;
    private Paint paint_hud;
    private Paint paint_info;
    private Paint paint_info_black;
    private Paint paint_info_filled;
    private Paint paint_info_frame;
    private Paint paint_plane;
    private Paint paint_rawdata;
    private Paint paint_ticks;
    private Paint paint_ticks_dash;
    public boolean plot_altimeter;
    public boolean plot_compass;
    public boolean plot_g;
    public boolean plot_pitch;
    public boolean plot_roll;
    public boolean plot_speed;
    public boolean plot_timer;
    public boolean plot_verticalspeed;
    private float r1;
    private float r2;
    private float r3;
    private String str_pos_unit;
    private String str_vel_unit;
    private int usemet;

    public FormHorizon(Context context, DisplayMetrics displayMetrics, Paint paint, Paint paint2, Paint paint3, Paint paint4) {
        this.RAD2DEG = Constants.RAD2DEG;
        this.iFilterStatus = 1;
        this.iGPSStatus = GPSListener.GPS_DISABLE;
        this.iMagDisturbStatus = false;
        this.paint_rawdata = new Paint();
        this.paint_info = new Paint();
        this.paint_ticks = new Paint();
        this.paint_ticks_dash = new Paint();
        this.paint_info_filled = new Paint();
        this.paint_info_frame = new Paint();
        this.paint_info_black = new Paint();
        this.paint_plane = new Paint();
        this.paint_horizon = new Paint();
        this.paint_ground = new Paint();
        this.paint_hud = new Paint();
        this.Hauteur = 30.0f;
        this.Largeur = this.Hauteur;
        this.r1 = 3.0f * this.Largeur;
        this.r2 = this.r1 + (this.Largeur / 3.0f);
        this.r3 = this.r1 + (this.Largeur / 2.0f);
        this.hud_mode = false;
        this.plot_pitch = true;
        this.plot_roll = true;
        this.plot_altimeter = true;
        this.plot_compass = true;
        this.plot_speed = true;
        this.plot_verticalspeed = true;
        this.plot_g = true;
        this.plot_timer = true;
        this.str_pos_unit = "m";
        this.cvLength2Unit = 1.0d;
        this.str_vel_unit = "m/s";
        this.cvVelocity2Unit = 1.0d;
        this.usemet = 1;
        this.AltTick2Degree = 100.0f;
        this.SpeedTick2Degree = 5.0f;
        setSize(displayMetrics);
        this.paint_rawdata = new Paint(paint);
        this.paint_info.setColor(-1);
        this.paint_info.setTextSize(20.0f);
        this.paint_info.setAntiAlias(true);
        this.paint_info.setStyle(Style.STROKE);
        this.paint_info.setTypeface(Typeface.MONOSPACE);
        this.paint_ticks = new Paint(paint4);
        this.paint_ticks_dash = new Paint(paint4);
        this.paint_ticks_dash.setStyle(Style.STROKE);
        this.paint_ticks_dash.setPathEffect(new DashPathEffect(new float[]{10.0f, 20.0f}, 0.0f));
        this.paint_info_filled = new Paint(paint2);
        this.paint_info_frame = new Paint(paint3);
        this.paint_info_black.setColor(-16777216);
        this.paint_info_black.setTextSize(26.0f);
        this.paint_info_black.setAntiAlias(true);
        this.paint_info_black.setStyle(Style.STROKE);
        this.paint_hud.setColor(-16711936);
        this.paint_hud.setTextSize(20.0f);
        this.paint_hud.setAntiAlias(true);
        this.paint_hud.setStyle(Style.STROKE);
        this.paint_horizon.setARGB(MotionEventCompat.ACTION_MASK, 0, 191, MotionEventCompat.ACTION_MASK);
        this.paint_horizon.setAntiAlias(true);
        this.paint_horizon.setStyle(Style.FILL);
        this.paint_ground.setARGB(MotionEventCompat.ACTION_MASK, 160, 82, 45);
        this.paint_ground.setAntiAlias(true);
        this.paint_ground.setStyle(Style.FILL);
        this.paint_plane.setColor(-16711936);
        this.paint_plane.setStrokeWidth(2.5f);
        this.paint_plane.setAntiAlias(true);
    }

    private void DisplayHorizonLine(Canvas canvas, float f, float f2, float f3, float f4) {
        CVector cVector;
        double d = (((double) f3) * this.RAD2DEG) * ((double) this.PasAssiette);
        Math.cos((double) f4);
        Math.sin((double) f4);
        double[][] r4 = new double[2][];
        r4[0] = new double[]{Math.cos((double) f4), Math.sin((double) f4)};
        r4[1] = new double[]{-Math.sin((double) f4), Math.cos((double) f4)};
        CMatrix cMatrix = new CMatrix(r4);
        Path path = new Path();
        Path path2 = new Path();
        double d2 = (double) (3.0f * this.mScreenHeight);
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.0f, this.mScreenHeight);
        path.lineTo(this.mScreenWidth, this.mScreenHeight);
        path.lineTo(this.mScreenWidth, 0.0f);
        path.close();
        canvas.drawPath(path, this.paint_ground);
        CVector cVector2 = new CVector(new double[]{-d2, d});
        CVector cVector3 = new CVector(new double[]{d2, d});
        cVector2 = cMatrix.times(cVector2);
        CVector times = cMatrix.times(cVector3);
        float element = f + ((float) cVector2.getElement(1));
        float element2 = f2 + ((float) cVector2.getElement(2));
        float element3 = f + ((float) times.getElement(1));
        float element4 = f2 + ((float) times.getElement(2));
        double d3 = (double) (2.0f * this.mScreenHeight);
        d = ((double) f4) * this.RAD2DEG;
        double d4 = d < 0.0d ? d + 360.0d : d;
        if (((d4 > 315.0d ? 1 : 0) | ((d4 >= 0.0d ? 1 : 0) & (d4 < 45.0d ? 1 : 0))) != 0) {
            cVector = new CVector(new double[]{0.0d, -d3});
        } else {
            if (((d4 < 135.0d ? 1 : 0) & (d4 >= 45.0d ? 1 : 0)) != 0) {
                cVector = new CVector(new double[]{-d3, 0.0d});
            } else {
                cVector = (((d4 > 225.0d ? 1 : (d4 == 225.0d ? 0 : -1)) < 0 ? 1 : 0) & ((d4 > 135.0d ? 1 : (d4 == 135.0d ? 0 : -1)) >= 0 ? 1 : 0)) != 0 ? new CVector(new double[]{0.0d, d3}) : new CVector(new double[]{d3, 0.0d});
            }
        }
        CVector plus = cVector2.plus(cVector);
        cVector = times.plus(cVector);
        float element5 = ((float) cVector.getElement(1)) + f;
        float element6 = ((float) cVector.getElement(2)) + f2;
        float element7 = ((float) plus.getElement(1)) + f;
        float element8 = ((float) plus.getElement(2)) + f2;
        path2.moveTo(element, element2);
        path2.lineTo(element3, element4);
        path2.lineTo(element5, element6);
        path2.lineTo(element7, element8);
        path2.close();
        canvas.drawPath(path2, this.paint_horizon);
    }

    private void DisplayPitchLadder(Canvas canvas, float f, float f2, float f3, float f4) {
        float f5 = 6.0f * this.Largeur;
        double d = ((double) this.PasAssiette) * (((double) f3) * this.RAD2DEG);
        float[] fArr = new float[]{2.5f, 7.5f, 15.0f, 0.0f, 5.0f, 10.0f, 20.0f, 30.0f, 40.0f, 90.0f, 90.0f};
        boolean[] zArr = new boolean[11];
        zArr[3] = true;
        zArr[4] = true;
        zArr[5] = true;
        zArr[6] = true;
        zArr[7] = true;
        zArr[8] = true;
        zArr[9] = true;
        float[] fArr2 = new float[80];
        float[] fArr3 = new float[80];
        float f6 = (float) (((double) f3) * this.RAD2DEG);
        float f7 = (float) (((double) f4) * this.RAD2DEG);
        double[][] dArr = new double[2][];
        double[] r5 = new double[2];
        r5[0] = Math.cos((double) f4);
        r5[1] = Math.sin((double) f4);
        dArr[0] = r5;
        r5 = new double[2];
        r5[0] = -Math.sin((double) f4);
        r5[1] = Math.cos((double) f4);
        dArr[1] = r5;
        CMatrix cMatrix = new CMatrix(dArr);
        Paint SelectTextPaint = SelectTextPaint(this.paint_info, this.paint_ticks);
        Paint SelectLinePaint = SelectLinePaint(this.paint_info, this.paint_ticks_dash);
        Rect rect = new Rect();
        this.paint_info_black.getTextBounds("000", 0, "000".length(), rect);
        CVector times = cMatrix.times(new CVector(new double[]{0.0d, 1.0d}));
        CVector times2 = cMatrix.times(new CVector(new double[]{1.0d, 0.0d}));
        int i = 0;
        while (i < fArr.length - 1) {
            CVector plus = times.times(((double) (fArr[i] * this.PasAssiette)) + d).plus(times2.times((double) this.r1));
            fArr3[i * 8] = ((float) plus.getElement(1)) + f;
            fArr3[(i * 8) + 1] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) (fArr[i] * this.PasAssiette)) + d).plus(times2.times((double) f5));
            fArr3[(i * 8) + 2] = ((float) plus.getElement(1)) + f;
            fArr3[(i * 8) + 3] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) (fArr[i] * this.PasAssiette)) + d).plus(times2.times((double) (-this.r1)));
            fArr3[(i * 8) + 4] = ((float) plus.getElement(1)) + f;
            fArr3[(i * 8) + 5] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) (fArr[i] * this.PasAssiette)) + d).plus(times2.times((double) (-f5)));
            fArr3[(i * 8) + 6] = ((float) plus.getElement(1)) + f;
            fArr3[(i * 8) + 7] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) ((-fArr[i]) * this.PasAssiette)) + d).plus(times2.times((double) this.r1));
            fArr2[i * 8] = ((float) plus.getElement(1)) + f;
            fArr2[(i * 8) + 1] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) ((-fArr[i]) * this.PasAssiette)) + d).plus(times2.times((double) f5));
            fArr2[(i * 8) + 2] = ((float) plus.getElement(1)) + f;
            fArr2[(i * 8) + 3] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) ((-fArr[i]) * this.PasAssiette)) + d).plus(times2.times((double) (-this.r1)));
            fArr2[(i * 8) + 4] = ((float) plus.getElement(1)) + f;
            fArr2[(i * 8) + 5] = ((float) plus.getElement(2)) + f2;
            plus = times.times(((double) ((-fArr[i]) * this.PasAssiette)) + d).plus(times2.times((double) (-f5)));
            fArr2[(i * 8) + 6] = ((float) plus.getElement(1)) + f;
            fArr2[(i * 8) + 7] = ((float) plus.getElement(2)) + f2;
            if (zArr[i] && i > 0) {
                Object[] objArr;
                String format;
                Object[] objArr2;
                String format2;
                float f8;
                float f9;
                float width;
                if ((((f6 <= fArr[i] ? 1 : 0) & (f6 > fArr[i + -1] ? 1 : 0)) | ((f6 < fArr[i + 1] ? 1 : 0) & (f6 >= fArr[i] ? 1 : 0))) != 0) {
                    objArr = new Object[1];
                    objArr[0] = Integer.valueOf((int) fArr[i]);
                    format = String.format("%3d", objArr);
                    objArr2 = new Object[1];
                    objArr2[0] = Integer.valueOf((int) fArr[i]);
                    format2 = String.format("%3d", objArr2);
                    f8 = fArr2[(i * 8) + 2];
                    f9 = fArr2[(i * 8) + 3];
                    canvas.save();
                    canvas.rotate(f7, f8, f9);
                    canvas.drawText(format, f8, f9, SelectTextPaint);
                    canvas.restore();
                    width = fArr2[(i * 8) + 6] + (((fArr2[(i * 8) + 6] - fArr2[(i * 8) + 4]) / (f5 - this.r1)) * ((float) rect.width()));
                    f8 = fArr2[(i * 8) + 7] + (((fArr2[(i * 8) + 7] - fArr2[(i * 8) + 5]) / (f5 - this.r1)) * ((float) rect.width()));
                    canvas.save();
                    canvas.rotate(f7, width, f8);
                    canvas.drawText(format2, width, f8, SelectTextPaint);
                    canvas.restore();
                }
                if ((((f6 < (-fArr[i + -1]) ? 1 : 0) & (f6 >= (-fArr[i]) ? 1 : 0)) | ((f6 < (-fArr[i]) ? 1 : 0) & (f6 >= (-fArr[i + 1]) ? 1 : 0))) != 0) {
                    objArr = new Object[1];
                    objArr[0] = Integer.valueOf(-((int) fArr[i]));
                    format = String.format("%d", objArr);
                    objArr2 = new Object[1];
                    objArr2[0] = Integer.valueOf(-((int) fArr[i]));
                    format2 = String.format("%d", objArr2);
                    f8 = fArr3[(i * 8) + 2];
                    f9 = fArr3[(i * 8) + 3];
                    canvas.save();
                    canvas.rotate(-f7, f8, f9);
                    canvas.drawText(format, f8, f9, SelectTextPaint);
                    canvas.restore();
                    width = fArr3[(i * 8) + 6] + (((fArr3[(i * 8) + 6] - fArr3[(i * 8) + 4]) / (f5 - this.r1)) * ((float) rect.width()));
                    f8 = fArr3[(i * 8) + 7] + (((fArr3[(i * 8) + 7] - fArr3[(i * 8) + 5]) / (f5 - this.r1)) * ((float) rect.width()));
                    canvas.save();
                    canvas.rotate(-f7, width, f8);
                    canvas.drawText(format2, width, f8, SelectTextPaint);
                    canvas.restore();
                }
            }
            i++;
        }
        canvas.drawLines(fArr2, SelectLinePaint);
        canvas.drawLines(fArr3, SelectLinePaint);
    }

    private void DisplayPlane(Canvas canvas, float f, float f2) {
        canvas.drawLines(new float[]{(this.Largeur * 2.0f) + f, f2, (this.Largeur * 0.5f) + f, f2, (this.Largeur * 0.5f) + f, f2, (this.Largeur * 0.25f) + f, (this.Hauteur * 0.5f) + f2, (this.Largeur * 0.25f) + f, (this.Hauteur * 0.5f) + f2, f, f2, f, f2, f - (this.Largeur * 0.25f), (this.Hauteur * 0.5f) + f2, f - (this.Largeur * 0.25f), (this.Hauteur * 0.5f) + f2, f - (this.Largeur * 0.5f), f2, f - (this.Largeur * 0.5f), f2, f - (this.Largeur * 2.0f), f2}, this.paint_plane);
    }

    private void DisplayRollLadder(Canvas canvas, float f, float f2, float f3) {
        double[] dArr = new double[]{0.0d, 0.5235987755982988d, 0.7853981633974483d, 1.0471975511965976d, 1.2217304763960306d, 1.3962634015954636d};
        double[] dArr2 = new double[]{1.2d * ((double) this.r3), (double) this.r3, (double) this.r2, (double) this.r3, (double) this.r2, (double) this.r2};
        Paint SelectLinePaint = SelectLinePaint(this.paint_info, this.paint_ticks);
        Paint SelectLinePaint2 = SelectLinePaint(this.paint_info_filled, this.paint_info_filled);
        canvas.drawLine(f, f2 - this.r1, f, f2 - this.r3, SelectLinePaint);
        for (int i = 0; i < dArr.length; i++) {
            float cos = (float) (((double) (-this.r1)) * Math.cos(dArr[i]));
            float cos2 = (float) ((-dArr2[i]) * Math.cos(dArr[i]));
            float sin = (float) (((double) (-this.r1)) * Math.sin(dArr[i]));
            float sin2 = (float) ((-dArr2[i]) * Math.sin(dArr[i]));
            canvas.drawLine(f + cos, f2 + sin, f + cos2, f2 + sin2, SelectLinePaint);
            canvas.drawLine(f - cos, f2 + sin, f - cos2, f2 + sin2, SelectLinePaint);
        }
        float sin3 = (float) (((double) (this.Largeur / 2.0f)) * Math.sin(60.0d / this.RAD2DEG));
        canvas.drawLines(new float[]{f, f2 - this.r1, (r2 / 2.0f) + f, (f2 - this.r1) - sin3, (r2 / 2.0f) + f, (f2 - this.r1) - sin3, f - (r2 / 2.0f), (f2 - this.r1) - sin3, f - ((this.Largeur / 2.0f) / 2.0f), (f2 - this.r1) - sin3, f, f2 - this.r1}, SelectLinePaint);
        double[][] r4 = new double[2][];
        r4[0] = new double[]{Math.cos((double) f3), Math.sin((double) f3)};
        r4[1] = new double[]{-Math.sin((double) f3), Math.cos((double) f3)};
        CMatrix cMatrix = new CMatrix(r4);
        CVector times = cMatrix.times(new CVector(new double[]{0.0d, (double) (-this.r1)}));
        CVector times2 = cMatrix.times(new CVector(new double[]{(double) (r2 / 2.0f), (double) ((-this.r1) + sin3)}));
        CVector times3 = cMatrix.times(new CVector(new double[]{(double) ((-r2) / 2.0f), (double) (sin3 + (-this.r1))}));
        Path path = new Path();
        float[] fArr = new float[]{((float) times.getElement(1)) + f, ((float) times.getElement(2)) + f2, ((float) times2.getElement(1)) + f, ((float) times2.getElement(2)) + f2, ((float) times2.getElement(1)) + f, ((float) times2.getElement(2)) + f2, ((float) times3.getElement(1)) + f, ((float) times3.getElement(2)) + f2, ((float) times3.getElement(1)) + f, ((float) times3.getElement(2)) + f2, ((float) times.getElement(1)) + f, ((float) times.getElement(2)) + f2};
        path.moveTo(fArr[0], fArr[1]);
        path.lineTo(fArr[2], fArr[3]);
        path.lineTo(fArr[6], fArr[7]);
        path.close();
        canvas.drawPath(path, SelectLinePaint2);
    }

    private void DisplayVerticalRuler(Canvas canvas, float f, float f2, float f3, float f4, int i, float f5, float f6, float f7, int i2, String str) {
        String format;
        String str2;
        float f8;
        Rect rect = new Rect();
        String valueOf = String.valueOf(f7);
        this.paint_info.getTextBounds(valueOf, 0, valueOf.length(), rect);
        float f9 = f3 / ((float) i);
        float[] fArr = new float[4];
        float[] fArr2 = new float[4];
        float floor = f4 * ((float) Math.floor((((double) f5) - (((double) f4) * Math.floor((double) (((float) i) / 2.0f)))) / ((double) f4)));
        float f10 = ((-((float) ((double) ((((float) ((int) (f5 / f4))) * f4) - f5)))) * f9) / f4;
        RectF rectF = new RectF();
        Rect rect2 = new Rect();
        Paint SelectTextPaint = SelectTextPaint(this.paint_info, this.paint_ticks);
        Paint SelectLinePaint = SelectLinePaint(this.paint_info, this.paint_ticks);
        float f11 = 0.0f;
        for (int i3 = 0; i3 <= i; i3++) {
            int i4 = (int) ((((float) (i - i3)) * f4) + floor);
            if (f6 <= ((float) i4)) {
                fArr[0] = f;
                fArr[1] = ((((float) i3) * f9) + f2) + f10;
                fArr[2] = f - (10.0f * ((float) i2));
                fArr[3] = fArr[1];
                canvas.drawLines(fArr, SelectLinePaint);
                format = String.format("%d", new Object[]{Integer.valueOf(i4)});
                if (i2 > 0) {
                    canvas.drawText(format, 30.0f + f, (fArr[1] - ((float) rect.top)) - (((float) rect.height()) / 2.0f), SelectTextPaint);
                } else {
                    canvas.drawText(format, f - 40.0f, (fArr[1] - ((float) rect.top)) - (((float) rect.height()) / 2.0f), SelectTextPaint);
                }
                if (i3 < i) {
                    fArr2[0] = f;
                    fArr2[1] = (((0.5f + ((float) i3)) * f9) + f2) + f10;
                    fArr2[2] = f - (10.0f * (0.5f * ((float) i2)));
                    fArr2[3] = fArr2[1];
                    canvas.drawLines(fArr2, SelectLinePaint);
                }
                int i5 = f5 > 0.0f ? 1 : -1;
                if (((0.0f <= ((float) i5) * (f5 - ((float) i4)) ? 1 : 0) & (((float) i5) * (f5 - ((float) i4)) < f4 ? 1 : 0)) != 0) {
                    valueOf = String.valueOf(f7) + str;
                    this.paint_info_black.getTextBounds(valueOf, 0, valueOf.length(), rect2);
                    fArr[1] = (((float) i3) * f9) + f2;
                    rectF.bottom = 2.0f + (fArr[1] + (((float) rect2.height()) / 2.0f));
                    rectF.top = (fArr[1] - (((float) rect2.height()) / 2.0f)) - 2.0f;
                    f11 = (fArr[1] - ((float) rect2.top)) - (((float) rect2.height()) / 2.0f);
                }
            }
        }
        if (f5 <= f7) {
            f7 = f5;
        }
        if (i2 > 0) {
            format = Math.abs(f7) > 1.0f ? String.format("%5d %s", new Object[]{Integer.valueOf((int) f7), str}) : String.format(Locale.ENGLISH, "%5.1f %s", new Object[]{Float.valueOf(f7), str});
            rectF.left = 0.0f + f;
            rectF.right = 2.0f + (rectF.left + ((float) rect2.width()));
            str2 = format;
            f8 = rectF.left;
        } else {
            str2 = Math.abs(f7) > 1.0f ? String.format("%5d %s", new Object[]{Integer.valueOf((int) f7), str}) : String.format(Locale.ENGLISH, "%5.1f %s", new Object[]{Float.valueOf(f7), str});
            rectF.right = f;
            rectF.left = rectF.right - ((float) rect2.width());
            f8 = rectF.left;
        }
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        canvas.drawText(str2, f8, f11, this.paint_info_black);
    }

    private void DisplayYawRuler(Canvas canvas, float f) {
        Rect rect = new Rect();
        float f2 = this.mScreenHeight;
        float f3 = this.mScreenWidth;
        float f4 = 0.7f * f3;
        int i = (int) ((f3 - f4) / 2.0f);
        int i2 = (int) (((double) f2) * 0.05d);
        f4 /= 10.0f;
        float[] fArr = new float[4];
        float[] fArr2 = new float[4];
        float f5 = 10.0f * ((float) ((int) ((f - 50.0f) / 10.0f)));
        float f6 = (((float) ((double) ((10.0f * ((float) ((int) (f / 10.0f)))) - f))) * f4) / 10.0f;
        Paint SelectTextPaint = SelectTextPaint(this.paint_info, this.paint_ticks);
        Paint SelectLinePaint = SelectLinePaint(this.paint_info, this.paint_ticks);
        for (int i3 = 0; i3 <= 10; i3++) {
            String str;
            fArr[0] = (((float) i) + (((float) i3) * f4)) + f6;
            fArr[1] = 10.0f + ((float) i2);
            fArr[2] = fArr[0];
            fArr[3] = 20.0f + ((float) i2);
            int i4 = ((int) f5) + ((int) (10.0f * ((float) i3)));
            if (i4 < 0) {
                i4 += 360;
            }
            if (i4 >= 360) {
                i4 -= 360;
            }
            if (i4 == 0) {
                str = "N";
                this.paint_info.setTextSize(24.0f);
            } else if (i4 == 180) {
                str = "S";
                this.paint_info.setTextSize(24.0f);
            } else if (i4 == 90) {
                str = "E";
                this.paint_info.setTextSize(24.0f);
            } else if (i4 == 270) {
                str = "W";
                this.paint_info.setTextSize(24.0f);
            } else {
                str = String.format("%d", new Object[]{Integer.valueOf(i4)});
                SelectTextPaint.setTextSize(20.0f);
            }
            SelectTextPaint.getTextBounds(str, 0, str.length(), rect);
            canvas.drawText(str, (float) ((int) ((fArr[0] - ((float) rect.left)) - (((float) rect.width()) / 2.0f))), (float) i2, SelectTextPaint);
            canvas.drawLines(fArr, SelectLinePaint);
            if (i3 < 10) {
                fArr2[0] = (((float) i) + ((0.5f + ((float) i3)) * f4)) + f6;
                fArr2[1] = 10.0f + ((float) i2);
                fArr2[2] = fArr2[0];
                fArr2[3] = 15.0f + ((float) i2);
                canvas.drawLines(fArr2, SelectLinePaint);
            }
        }
        this.paint_info.setTextSize(20.0f);
    }

    private void displayFilterStatus(Canvas canvas) {
        canvas.drawText(this.iFilterStatus == 2 ? String.format(Locale.US, "UKF", new Object[0]) : String.format(Locale.US, "EKF", new Object[0]), 0.01f * this.mScreenWidth, 0.85f * this.mScreenHeight, new Paint(this.paint_info));
    }

    private void displayGpsStatus(Canvas canvas) {
        String format;
        Paint paint = new Paint(this.paint_info_filled);
        Paint paint2 = new Paint(this.paint_info);
        if (this.iGPSStatus == GPSListener.GPS_READY) {
            format = String.format(Locale.US, "GPS", new Object[0]);
            paint2.setColor(-16711936);
            paint.setColor(-16711936);
        } else if (this.iGPSStatus == GPSListener.GPS_FIXING) {
            format = String.format(Locale.US, "GPS", new Object[0]);
            paint2.setColor(-256);
            paint.setColor(-256);
        } else {
            format = String.format(Locale.US, "no GPS", new Object[0]);
            paint2.setColor(-65536);
            paint.setColor(-65536);
        }
        canvas.drawText(format, 0.01f * this.mScreenWidth, 0.8f * this.mScreenHeight, paint2);
    }

    private void displayMagneticDisturbanceStatus(Canvas canvas) {
        Paint paint = new Paint(this.paint_info_filled);
        Paint paint2 = new Paint(this.paint_info);
        if (this.iMagDisturbStatus) {
            String format = String.format(Locale.US, "MAG", new Object[0]);
            paint2.setColor(-65281);
            paint.setColor(-1);
            canvas.drawText(format, 0.01f * this.mScreenWidth, 0.75f * this.mScreenHeight, paint2);
        }
    }

    public void DrawBoard(Canvas canvas, double d, float f, double d2, double d3, double d4, double d5, double d6, double d7, double d8, CVector cVector) {
        float f2 = this.mScreenHeight;
        float f3 = this.mScreenWidth;
        float f4 = this.mScreenWidth / 2.0f;
        float f5 = this.mScreenHeight / 2.0f;
        float f6 = f2 * 0.6f;
        double d9 = d4 * this.cvVelocity2Unit;
        double d10 = d3 * this.cvVelocity2Unit;
        double d11 = d2 * this.cvLength2Unit;
        Paint SelectTextPaint = SelectTextPaint(this.paint_info, this.paint_ticks);
        float f7 = (float) (d7 / this.RAD2DEG);
        float f8 = (float) (d6 / this.RAD2DEG);
        if (!this.hud_mode) {
            DisplayHorizonLine(canvas, f4, f5, f8, f7);
        }
        if (this.plot_pitch) {
            DisplayPitchLadder(canvas, f4, f5, f8, f7);
        }
        if (this.plot_roll) {
            DisplayRollLadder(canvas, f4, f5, f7);
        }
        DisplayPlane(canvas, f4, f5);
        if (this.plot_altimeter) {
            Canvas canvas2 = canvas;
            DisplayVerticalRuler(canvas2, (float) ((int) (((double) f4) + (0.3d * ((double) f3)))), (float) ((int) ((f2 - f6) / 2.0f)), f6, this.AltTick2Degree, 10, (float) d11, -99999.0f, 99999.0f, 1, this.str_pos_unit);
        }
        if (this.plot_speed) {
            Canvas canvas2 = canvas;
            DisplayVerticalRuler(canvas2, (float) ((int) (((double) f4) - (0.3d * ((double) f3)))), (float) ((int) ((f2 - f6) / 2.0f)), f6, this.SpeedTick2Degree, 5, (float) d9, 0.0f, 999.0f, -1, this.str_vel_unit);
        }
        if (this.plot_compass) {
            DisplayYawRuler(canvas, (float) d5);
        }
        Rect rect = new Rect();
        if (this.plot_verticalspeed) {
            String format = String.format(Locale.ENGLISH, "VS %5.1f %s", new Object[]{Double.valueOf(d10), this.str_vel_unit});
            SelectTextPaint.getTextBounds(format, 0, format.length(), rect);
            Canvas canvas3 = canvas;
            canvas3.drawText(format, (float) ((int) ((0.95f * f3) - ((float) rect.width()))), (float) ((int) (f2 - ((float) (rect.height() * 2)))), SelectTextPaint);
        }
        if (this.plot_g && cVector != null) {
            String format;
            if (Float.isNaN((((float) cVector.norm()) / 9.806f) - 1.0f)) {
                format = String.format(Locale.ENGLISH, "G N/A g", new Object[0]);
            } else {
                format = String.format(Locale.ENGLISH, "G %5.1f g", new Object[]{Float.valueOf(r2)});
            }
            SelectTextPaint.getTextBounds(format, 0, format.length(), rect);
            Canvas canvas3 = canvas;
            canvas3.drawText(format, (float) ((int) (0.05f * f3)), (float) ((int) ((0.2d * ((double) f2)) - ((double) (1.1f * ((float) rect.height()))))), SelectTextPaint);
        }
        if (this.plot_timer) {
            String format = String.format(Locale.ENGLISH, "%5.1f s", new Object[]{Double.valueOf(d)});
            SelectTextPaint.getTextBounds(format, 0, format.length(), rect);
            Canvas canvas3 = canvas;
            canvas3.drawText(format, (float) ((int) (0.05f * f3)), (float) ((int) (f2 - ((float) (rect.height() * 2)))), SelectTextPaint);
        }
        if (f > 0.0f) {
            String format = String.format(Locale.ENGLISH, "%3d %%", new Object[]{Integer.valueOf((int) f)});
            SelectTextPaint.getTextBounds(format, 0, format.length(), rect);
            Canvas canvas3 = canvas;
            canvas3.drawText(format, (float) ((int) (0.05f * f3)), (float) ((int) ((f2 - ((float) rect.height())) + 2.0f)), SelectTextPaint);
        }
        displayGpsStatus(canvas);
        displayFilterStatus(canvas);
        displayMagneticDisturbanceStatus(canvas);
    }

    public Paint SelectLinePaint(Paint paint, Paint paint2) {
        if (!this.hud_mode) {
            return paint2;
        }
        Paint paint3 = new Paint(this.paint_hud);
        paint3.setStrokeWidth(paint2.getStrokeWidth());
        paint3.setStyle(paint2.getStyle());
        paint3.setPathEffect(paint2.getPathEffect());
        return paint3;
    }

    public Paint SelectTextPaint(Paint paint, Paint paint2) {
        if (!this.hud_mode) {
            return paint;
        }
        Paint paint3 = new Paint(this.paint_hud);
        paint3.setStrokeWidth(paint.getStrokeWidth());
        paint3.setStyle(paint.getStyle());
        paint3.setPathEffect(paint.getPathEffect());
        paint3.setTypeface(paint.getTypeface());
        return paint3;
    }

    public void setFilterIcon(int i) {
        this.iFilterStatus = i;
    }

    public void setGpsIcon(int i) {
        this.iGPSStatus = i;
    }

    public void setHUDMode(boolean z) {
        this.hud_mode = z;
    }

    public void setMagDisturbanceStatus(boolean z) {
        this.iMagDisturbStatus = z;
    }

    public void setProperty(int i, boolean z) {
        switch (i) {
            case Sphere.PLANET_BLEND_MODE_NONE /*1*/:
                this.plot_pitch = z;
            case Sphere.PLANET_BLEND_MODE_ATMO /*2*/:
                this.plot_roll = z;
            case Sphere.PLANET_BLEND_MODE_SOLID /*3*/:
                this.plot_altimeter = z;
            case Sphere.PLANET_BLEND_MODE_FADE /*4*/:
                this.plot_compass = z;
            case ShaderManager.UNIFORM_COLOR_VECTOR /*5*/:
                this.plot_speed = z;
            case ShaderManager.UNIFORM_ALPHA /*6*/:
                this.plot_verticalspeed = z;
            case Constants.PROPERTY_PLOTG /*7*/:
                this.plot_g = z;
            case Constants.PROPERTY_TIMER /*11*/:
                this.plot_timer = z;
            default:
        }
    }

    public void setSize(DisplayMetrics displayMetrics) {
        this.mScreenHeight = (float) displayMetrics.heightPixels;
        this.mScreenWidth = (float) displayMetrics.widthPixels;
        this.PasAssiette = (this.mScreenWidth / 2.0f) / 45.0f;
    }

    public void setUnitsMode(int i, String str, double d, String str2, double d2) {
        this.usemet = i;
        this.str_pos_unit = str;
        this.str_vel_unit = str2;
        this.cvLength2Unit = d;
        this.cvVelocity2Unit = d2;
        this.AltTick2Degree = 100.0f;
        if (this.str_pos_unit == "ft") {
            this.AltTick2Degree = 10.0f;
        } else if (this.str_pos_unit == "km") {
            this.AltTick2Degree = 2.0f;
        } else if (this.str_pos_unit == "mi") {
            this.AltTick2Degree = 1.0f;
        }
        this.SpeedTick2Degree = 5.0f;
        if (this.str_vel_unit == "ft/s") {
            this.SpeedTick2Degree = 1.0f;
        } else if (this.str_vel_unit == "km/h") {
            this.SpeedTick2Degree = 5.0f;
        } else if (this.str_vel_unit == "mph") {
            this.SpeedTick2Degree = 10.0f;
        }
    }
}
