package cc.minieye.instoolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ZoomControls;

import cc.minieye.instoolkit.MyGPSListener;
import cc.minieye.kalman.maths.CDataArray;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

import java.util.Locale;

import opengl.ShaderManager;
import opengl.Sphere;

public class FormGroundTrack implements OnTouchListener {
    private static int DOT_ARROW;
    private static int DOT_CROSS;
    private static int DOT_SQUARE;
    private static int DOT_TRACE;
    private float CanvasHeight;
    private float CanvasWidth;
    double RAD2DEG;
    public boolean buttonCLICK;
    public boolean buttonROTATE;
    public boolean buttonZOOM;
    private CameraPosPlain cam;
    private double cvLength2Unit;
    private double cvVelocity2Unit;
    private int iFilterStatus;
    private int iGPSStatus;
    private boolean iMagDisturbStatus;
    private boolean isGoogleMapsShown;
    private CVector lastpos;
    private int lenDot;
    private float mScreenHeight;
    private float mScreenWidth;
    ZoomControls mZoomControl;
    float marginTop;
    public Point mousePointRightBtnDown;
    private int nGridTicks;
    private int nTrackPts;
    private CVector origine;
    private Paint paint_grid;
    private Paint paint_info_filled;
    private Paint paint_info_frame;
    private Paint paint_line;
    private Paint paint_point;
    private Paint paint_text;
    public boolean plot_altimeter;
    public boolean plot_compass;
    public boolean plot_g;
    public boolean plot_speed;
    public boolean plot_timer;
    public boolean plot_verticalspeed;
    final RectF rectField;
    private float scalex;
    private float scaley;
    private float screenDensity;
    private String str_pos_unit;
    private String str_vel_unit;
    private CDataArray vMarkerPts;
    private CDataArray vTrackPts;
    private float vZoomNew;
    private float vZoomOld;
    private float xmax;
    private float xmin;
    private float ymax;
    private float ymin;

    static {
        DOT_CROSS = 1;
        DOT_SQUARE = 2;
        DOT_TRACE = 3;
        DOT_ARROW = 4;
    }

    public FormGroundTrack(Context context, DisplayMetrics displayMetrics) {
        this.RAD2DEG = Constants.RAD2DEG;
        this.marginTop = 0.95f * this.mScreenHeight;
        this.rectField = new RectF();
        this.iGPSStatus = MyGPSListener.GPS_FIXING;
        this.iFilterStatus = 1;
        this.iMagDisturbStatus = false;
        this.scalex = 1.0f;
        this.scaley = 1.0f;
        this.xmin = -5.0f;
        this.xmax = 5.0f;
        this.ymin = -5.0f;
        this.ymax = 5.0f;
        this.nGridTicks = 10;
        this.lenDot = 20;
        this.paint_text = new Paint();
        this.paint_info_filled = new Paint();
        this.paint_info_frame = new Paint();
        this.paint_point = new Paint();
        this.paint_grid = null;
        this.paint_line = new Paint();
        this.plot_altimeter = true;
        this.plot_speed = true;
        this.plot_verticalspeed = true;
        this.plot_g = true;
        this.plot_compass = true;
        this.plot_timer = true;
        this.isGoogleMapsShown = false;
        this.nTrackPts = 1000;
        this.vTrackPts = new CDataArray(this.nTrackPts, 3);
        this.vMarkerPts = new CDataArray(100, 3);
        this.origine = new CVector(3);
        this.lastpos = new CVector(3);
        this.str_pos_unit = "m";
        this.cvLength2Unit = 1.0d;
        this.str_vel_unit = "m/s";
        this.cvVelocity2Unit = 1.0d;
        this.cam = new CameraPosPlain();
        this.mousePointRightBtnDown = new Point();
        this.buttonROTATE = false;
        this.buttonZOOM = false;
        this.buttonCLICK = false;
        setSize(displayMetrics);
        this.paint_text.setColor(-7829368);
        this.paint_text.setTextSize(12.0f);
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
        this.paint_point.setColor(-16776961);
        this.paint_point.setAntiAlias(true);
        this.paint_point.setStyle(Style.STROKE);
        this.paint_point.setStrokeWidth(1.5f);
        this.paint_line.setColor(-1);
        this.paint_line.setAntiAlias(true);
        this.paint_line.setStyle(Style.STROKE);
        this.paint_line.setStrokeWidth(1.5f);
        this.paint_grid = new Paint(this.paint_line);
        this.paint_grid.setStyle(Style.STROKE);
        this.paint_grid.setStrokeWidth(0.5f);
        this.paint_grid.setPathEffect(new DashPathEffect(new float[]{10.0f, 20.0f}, 0.0f));
        this.mZoomControl = new ZoomControls(context);
        this.mZoomControl.setOnZoomInClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.mZoomControl.setOnZoomOutClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        setScale(this.xmin, this.xmax, this.ymin, this.ymax);
        addGroundMarker(new CVector(new double[]{0.0d, 0.0d, 0.0d}));
    }

    private void WorldToScreenCoordinates(CVector cVector, CVector cVector2) {
        cVector2.copy(cVector);
    }

    private void displayFilterStatus(Canvas canvas) {
        String format;
        Paint paint = new Paint(this.paint_info_filled);
        Paint paint2 = new Paint(this.paint_text);
        paint2.setTextSize(14.0f);
        if (this.iFilterStatus == 2) {
            format = String.format(Locale.US, "Filter:        UKF", new Object[0]);
            paint2.setColor(-16777216);
            paint.setColor(-16711936);
        } else {
            format = String.format(Locale.US, "Filter:     EKF", new Object[0]);
            paint2.setColor(-16777216);
            paint.setColor(-16711936);
        }
        float f = 0.01f * this.mScreenWidth;
        plotText(canvas, f, 0.16f * this.mScreenHeight, format, paint2, paint);
    }

    private void displayGpsStatus(Canvas canvas) {
        String format;
        Paint paint = new Paint(this.paint_info_filled);
        Paint paint2 = new Paint(this.paint_text);
        paint2.setTextSize(14.0f);
        if (this.iGPSStatus == MyGPSListener.GPS_READY) {
            format = String.format(Locale.US, "GPS:        Connected ", new Object[0]);
            paint2.setColor(-16777216);
            paint.setColor(-16711936);
        } else if (this.iGPSStatus == MyGPSListener.GPS_FIXING) {
            format = String.format(Locale.US, "GPS:     Acquiring... ", new Object[0]);
            paint2.setColor(-65536);
            paint.setColor(-256);
        } else {
            format = String.format(Locale.US, "GPS:     Disconnected ", new Object[0]);
            paint2.setColor(-1);
            paint.setColor(-65536);
        }
        float f = 0.01f * this.mScreenWidth;
        plotText(canvas, f, 0.1f * this.mScreenHeight, format, paint2, paint);
    }

    private void displayGrid(Canvas canvas, float f, float f2, float f3, float f4, int i) {
        int i2 = i <= 1 ? 2 : i;
        if (i2 >= 20) {
            i2 = 20;
        }
        float f5 = f3 / ((float) i2);
        float f6 = f4 / ((float) i2);
        float[] fArr = new float[4];
        for (int i3 = 0; i3 <= i2; i3++) {
            fArr[0] = f;
            fArr[1] = (((float) i3) * f6) + f2;
            fArr[2] = f + f3;
            fArr[3] = (((float) i3) * f6) + f2;
            canvas.drawLines(fArr, this.paint_grid);
            fArr[0] = (((float) i3) * f5) + f;
            fArr[1] = f2;
            fArr[2] = (((float) i3) * f5) + f;
            fArr[3] = f2 + f4;
            canvas.drawLines(fArr, this.paint_grid);
        }
    }

    private void displayGround(Canvas canvas, float[] fArr, float f, float f2, float f3) {
        float f4 = fArr[0];
        float f5 = fArr[1];
        this.rectField.left = f4 - (0.99f * (f2 / 2.0f));
        this.rectField.right = (0.99f * (f2 / 2.0f)) + f4;
        this.rectField.bottom = (0.99f * (f / 2.0f)) + f5;
        this.rectField.top = f5 - (0.99f * (f / 2.0f));
        displayGrid(canvas, this.rectField.left, this.rectField.top, this.rectField.width(), this.rectField.height(), this.nGridTicks);
        canvas.drawRect(this.rectField, this.paint_line);
        plotDot(canvas, f4, f5, 1.0f, DOT_ARROW, -65536);
        float f6 = this.CanvasWidth / this.scalex;
        float f7 = this.CanvasHeight / this.scaley;
        canvas.drawText(String.format(Locale.ENGLISH, "[%.1f %.1f]x[%.1f %.1f]  [%8.1f x %8.1f] %s/div", new Object[]{Float.valueOf(this.xmin), Float.valueOf(this.xmax), Float.valueOf(this.ymin), Float.valueOf(this.ymax), Float.valueOf(f6 / ((float) this.nGridTicks)), Float.valueOf(f7 / ((float) this.nGridTicks)), this.str_pos_unit}), 0.5f * this.mScreenWidth, this.mScreenHeight - (1.0f + this.paint_text.getTextSize()), this.paint_text);
    }

    private void displayMagneticDisturbanceStatus(Canvas canvas) {
        Paint paint = new Paint(this.paint_info_filled);
        Paint paint2 = new Paint(this.paint_text);
        if (this.iMagDisturbStatus) {
            String format = String.format(Locale.US, "MAG disturbances", new Object[0]);
            paint2.setColor(-65536);
            paint.setColor(-256);
            float f = 0.01f * this.mScreenWidth;
            plotText(canvas, f, 0.22f * this.mScreenHeight, format, paint2, paint);
        }
    }

    private void displayVelocity(Canvas canvas, CVector cVector) {
        float norm = (float) cVector.norm();
        String format = String.format(Locale.US, "Ground Speed: %5.1f %s", new Object[]{Float.valueOf(norm), this.str_vel_unit});
        Canvas canvas2 = canvas;
        plotText(canvas2, 0.01f * this.mScreenWidth, 0.3f * this.mScreenHeight, format, this.paint_text, null);
    }

    private void getInstrumentCenter(float f, float f2, float f3, float f4, float[] fArr) {
        fArr[0] = (f4 / 2.0f) + f;
        fArr[1] = f2 - (f3 / 2.0f);
    }

    private int getMargin() {
        String format = String.format(Locale.ENGLISH, "Vel  [%8.1f %8.1f] %s", new Object[]{Double.valueOf(0.0d), Double.valueOf(0.0d), this.str_vel_unit});
        Rect rect = new Rect();
        this.paint_text.getTextBounds(format, 0, format.length(), rect);
        float f = 0.01f * this.mScreenWidth;
        float textSize = this.mScreenHeight - (((10.0f + this.paint_text.getTextSize()) + 1.0f) * 1.0f);
        RectF rectF = new RectF();
        return (int) ((f + ((float) rect.width())) + 8.0f);
    }

    private void plotDot(Canvas canvas, float f, float f2, float f3, int i, int i2) {
        float f4 = ((-f3) * ((float) this.lenDot)) / 2.0f;
        float f5 = ((-f3) * ((float) this.lenDot)) / 2.0f;
        float f6 = (((float) this.lenDot) * f3) / 2.0f;
        float f7 = (((float) this.lenDot) * f3) / 2.0f;
        this.paint_point.setColor(i2);
        if (i == DOT_CROSS) {
            canvas.drawLine(f, f5, f, f7, this.paint_point);
            canvas.drawLine(f + f4, f2, f + f6, f2, this.paint_point);
        } else if (i == DOT_SQUARE) {
            Rect rect = new Rect();
            rect.top = (int) (f2 + f5);
            rect.left = (int) (f + f4);
            rect.bottom = (int) (f2 + f7);
            rect.right = (int) (f + f6);
            canvas.drawRect(rect, this.paint_point);
        } else if (i == DOT_ARROW) {
            canvas.drawLine(f, f2, f + f6, f2 + f7, this.paint_point);
            canvas.drawLine(f, f2, f + f4, f7 + f2, this.paint_point);
        } else {
            canvas.drawLine(f, f5 + f2, f, f7 + f2, this.paint_point);
            canvas.drawLine(f + f4, f2, f + f6, f2, this.paint_point);
        }
    }

    private void plotMarkers(Canvas canvas, float[] fArr) {
        CVector cVector = new CVector(2);
        int size = this.vMarkerPts.size();
        for (int i = 0; i < size; i++) {
            float rollingData = (float) (((double) this.vMarkerPts.getRollingData(i, 1)) - this.lastpos.getElement(1));
            float rollingData2 = (float) (((double) this.vMarkerPts.getRollingData(i, 2)) - this.lastpos.getElement(2));
            WorldToScreenCoordinates(new CVector(new double[]{(double) rollingData, (double) rollingData2}), cVector);
            Canvas canvas2 = canvas;
            plotDot(canvas2, fArr[0] + (((float) cVector.getElement(1)) * this.scalex), fArr[1] - (((float) cVector.getElement(2)) * this.scaley), 1.0f, DOT_SQUARE, -16711936);
        }
    }

    private void plotText(Canvas canvas, float f, float f2, String str, Paint paint, Paint paint2) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        RectF rectF = new RectF();
        rectF.left = f - 5.0f;
        rectF.right = (((float) rect.width()) + f) + (4.0f * 5.0f);
        rectF.bottom = f2 + 5.0f;
        rectF.top = (f2 - ((float) rect.height())) - (2.0f * 5.0f);
        float height = f2 - ((float) (rect.height() / 2));
        if (paint2 == null) {
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        } else {
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, paint2);
        }
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        canvas.drawText(str, f, height, paint);
    }

    private void plotToolDistance(Canvas canvas, CVector cVector) {
        float f = 0.0f;
        if (this.vTrackPts.size() > 2) {
            f = (float) new CVector(this.vTrackPts.get(0)).minus(new CVector(this.vTrackPts.getLastPoint())).norm();
        }
        String format = String.format(Locale.US, "Distance from 0/: %5.1f %s", new Object[]{Float.valueOf(f), this.str_pos_unit});
        Canvas canvas2 = canvas;
        plotText(canvas2, 0.01f * this.mScreenWidth, 0.5f * this.mScreenHeight, format, this.paint_text, null);
    }

    private void plotTrack(Canvas canvas, float[] fArr, CVector cVector) {
        if (cVector.minus(this.lastpos).norm() > 0.1d * this.cvLength2Unit) {
            this.vTrackPts.addValue(0.0f, cVector);
            this.lastpos.copy(cVector);
        }
        int size = this.vTrackPts.size();
        if (size >= 1) {
            float MinY = (float) (((double) this.vTrackPts.MinY(1)) - this.lastpos.getElement(1));
            float MaxY = (float) (((double) this.vTrackPts.MaxY(1)) - this.lastpos.getElement(1));
            float MinY2 = (float) (((double) this.vTrackPts.MinY(2)) - this.lastpos.getElement(2));
            float MaxY2 = (float) (((double) this.vTrackPts.MaxY(2)) - this.lastpos.getElement(2));
            this.xmin = 10.0f * (Math.signum(MinY) * ((float) Math.ceil((double) (Math.abs(MinY) / 10.0f))));
            this.xmax = 10.0f * (Math.signum(MaxY) * ((float) Math.floor((double) (Math.abs(MaxY) / 10.0f))));
            this.ymin = 10.0f * (Math.signum(MinY2) * ((float) Math.ceil((double) (Math.abs(MinY2) / 10.0f))));
            this.ymax = 10.0f * (Math.signum(MaxY2) * ((float) Math.floor((double) (Math.abs(MaxY2) / 10.0f))));
            setScale(this.xmin, this.xmax, this.ymin, this.ymax);
            CVector cVector2 = new CVector(2);
            int i = 0;
            while (i < size) {
                float rollingData = (float) (((double) this.vTrackPts.getRollingData(i, 1)) - this.lastpos.getElement(1));
                float rollingData2 = (float) (((double) this.vTrackPts.getRollingData(i, 2)) - this.lastpos.getElement(2));
                WorldToScreenCoordinates(new CVector(new double[]{(double) rollingData, (double) rollingData2}), cVector2);
                rollingData2 = (float) cVector2.getElement(1);
                rollingData = (float) cVector2.getElement(2);
                float f = rollingData2 > MaxY ? rollingData2 : MaxY;
                float f2 = rollingData2 < MinY ? rollingData2 : MinY;
                float f3 = rollingData > MaxY2 ? rollingData : MaxY2;
                float f4 = rollingData < MinY2 ? rollingData : MinY2;
                plotDot(canvas, (this.scalex * rollingData2) + fArr[0], fArr[1] - (rollingData * this.scaley), 1.0f, DOT_TRACE, Color.argb((int) (100.0f + (155.0f * (((float) i) / ((float) size)))), 0, 0, MotionEventCompat.ACTION_MASK));
                i++;
                MaxY2 = f3;
                MinY2 = f4;
                MaxY = f;
                MinY = f2;
            }
        }
    }

    private int printData(Canvas canvas, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10, CVector cVector, CVector cVector2, CVector cVector3) {
        String[] strArr = new String[11];
        strArr[0] = String.format(Locale.ENGLISH, "LOCAL FRAME", new Object[0]);
        int i = 1;
        if (this.plot_timer) {
            strArr[1] = String.format(Locale.ENGLISH, "Time %8.1f s", new Object[]{Double.valueOf(d)});
            i = 2;
        }
        if (this.plot_altimeter) {
            strArr[i] = String.format(Locale.ENGLISH, "ALT  %8.1f %s", new Object[]{Double.valueOf(d4), this.str_pos_unit});
            i++;
        }
        strArr[i] = String.format(Locale.ENGLISH, "Pos  [%8.1f %8.1f] %s", new Object[]{Double.valueOf(cVector2.getElement(1)), Double.valueOf(cVector2.getElement(2)), this.str_pos_unit});
        i++;
        if (this.plot_speed) {
            strArr[i] = String.format(Locale.ENGLISH, "GS   %8.1f %s", new Object[]{Double.valueOf(d6), this.str_vel_unit});
            i++;
        }
        if (this.plot_verticalspeed) {
            strArr[i] = String.format(Locale.ENGLISH, "VS   %8.1f %s", new Object[]{Double.valueOf(d5), this.str_vel_unit});
            i++;
        }
        strArr[i] = String.format(Locale.ENGLISH, "Vel  [%8.1f %8.1f] %s", new Object[]{Double.valueOf(cVector3.getElement(1)), Double.valueOf(cVector3.getElement(2)), this.str_vel_unit});
        Rect rect = new Rect();
        this.paint_text.getTextBounds(strArr[i], 0, strArr[i].length(), rect);
        i++;
        if (this.plot_g) {
            float r5 = (float) cVector.norm();
            if (!Float.isNaN(r5)) {
                strArr[i] = String.format(Locale.ENGLISH, "G    %8.3f", new Object[]{Float.valueOf(r5)});
                i++;
            }
        }
        float f = 0.01f * this.mScreenWidth;
        float textSize = this.mScreenHeight - (1.0f * (1.0f + (10.0f + this.paint_text.getTextSize())));
        RectF rectF = new RectF();
        rectF.left = f - 2.0f;
        rectF.right = 8.0f + (((float) rect.width()) + f);
        rectF.bottom = 5.0f + textSize;
        rectF.top = (textSize - ((float) (rect.height() * i))) - 10.0f;
        if (canvas != null) {
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
            for (int i2 = 0; i2 < i; i2++) {
                canvas.drawText(strArr[i2], f, textSize - ((float) (((i - i2) - 1) * 12)), this.paint_text);
            }
        }
        return (int) rectF.right;
    }

    private void setScale(float f, float f2, float f3, float f4) {
        this.xmax = Math.max(Math.abs(f2), Math.abs(f));
        this.xmin = -this.xmax;
        this.ymax = Math.max(Math.abs(f4), Math.abs(f3));
        this.ymin = -this.ymax;
        this.scalex = this.CanvasWidth / (this.xmax - this.xmin);
        this.scaley = this.CanvasHeight / (this.ymax - this.ymin);
    }

    private float spacing(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return FloatMath.sqrt((x * x) + (y * y));
    }

    public void DrawBoard(Canvas canvas, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10, CVector cVector, CVector cVector2, CVector cVector3) {
        float f = 0.05f * this.mScreenWidth;
        double d11 = d6 * this.cvVelocity2Unit;
        double d12 = d5 * this.cvVelocity2Unit;
        double d13 = d4 * this.cvLength2Unit;
        cVector2.timesSelf(this.cvLength2Unit);
        cVector3.timesSelf(this.cvVelocity2Unit);
        int printData = printData(canvas, d, d2, d3, d13, d12, d11, d7, d8, d9, d10, cVector, cVector2, cVector3);
        displayGpsStatus(canvas);
        displayFilterStatus(canvas);
        displayMagneticDisturbanceStatus(canvas);
        displayVelocity(canvas, cVector3);
        this.marginTop = 0.95f * this.mScreenHeight;
        float[] fArr = new float[2];
        getInstrumentCenter((float) printData, this.marginTop, this.CanvasHeight, this.CanvasWidth - ((float) printData), fArr);
        if (!this.isGoogleMapsShown) {
            displayGround(canvas, fArr, this.CanvasHeight, this.CanvasWidth - ((float) printData), (float) d7);
        }
        plotToolDistance(canvas, cVector2);
        if (!this.isGoogleMapsShown) {
            plotTrack(canvas, fArr, cVector2);
            plotMarkers(canvas, fArr);
        }
    }

    public void addGroundMarker(CVector cVector) {
        this.vMarkerPts.addValue(0.0f, cVector.times(this.cvLength2Unit));
    }

    public void clear() {
        this.vTrackPts.clear();
    }

    public int getBottom() {
        return (int) (this.mScreenHeight - this.rectField.bottom);
    }

    public int getCanvasHeight() {
        return (int) this.CanvasHeight;
    }

    public int getCanvasWidth() {
        return (int) this.CanvasWidth;
    }

    public int getHeight() {
        return (int) this.mScreenHeight;
    }

    public int getLeft() {
        return (int) (this.rectField.left * this.screenDensity);
    }

    public int getRight() {
        return ((int) (this.mScreenWidth - (this.rectField.right * this.screenDensity))) * 0;
    }

    public int getTop() {
        return (int) (this.rectField.top * this.screenDensity);
    }

    public int getWidth() {
        return (int) this.mScreenWidth;
    }

    public void mouseDragged(Point point, MotionEvent motionEvent) {
        if (this.buttonCLICK || this.buttonROTATE) {
            int i = this.mousePointRightBtnDown.x - point.x;
            int i2 = this.mousePointRightBtnDown.y - point.y;
            if ((i * i) + (i2 * i2) > 100) {
                this.buttonCLICK = false;
                this.cam.setCameraPosition(((float) i) / this.mScreenWidth, ((float) i2) / this.mScreenHeight, false);
                return;
            }
            this.buttonCLICK = true;
            this.buttonROTATE = false;
        } else if (this.buttonZOOM) {
            this.vZoomNew = spacing(motionEvent);
            if (this.vZoomOld > 2.0f && this.vZoomNew > 2.0f) {
                this.cam.setCurrentZoom(this.vZoomOld / this.vZoomNew);
            }
        }
    }

    public void mousePressed(int i, Point point, MotionEvent motionEvent) {
        if (i == 2) {
            if (!this.buttonZOOM) {
                this.vZoomOld = spacing(motionEvent);
                this.buttonZOOM = true;
                this.buttonROTATE = false;
                this.buttonCLICK = false;
            }
        } else if (i == 3 && !this.buttonROTATE) {
            this.mousePointRightBtnDown = new Point(point);
            this.buttonZOOM = false;
            this.buttonROTATE = true;
            this.buttonCLICK = true;
        }
    }

    public void mouseReleased(Point point) {
        if (this.buttonROTATE && !this.buttonCLICK) {
            this.buttonROTATE = false;
            this.cam.setCameraPosition(((float) (this.mousePointRightBtnDown.x - point.x)) / this.mScreenWidth, ((float) (this.mousePointRightBtnDown.y - point.y)) / this.mScreenHeight, true);
        } else if (this.buttonCLICK) {
            this.buttonCLICK = false;
        } else if (this.buttonZOOM) {
            this.buttonZOOM = false;
            if (this.vZoomOld > 2.0f && this.vZoomNew > 2.0f) {
                this.cam.setFinalZoom(this.vZoomOld / this.vZoomNew);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        Point point = new Point();
        point.x = (int) motionEvent.getX();
        point.y = (int) motionEvent.getY();
        switch (motionEvent.getAction() & MotionEventCompat.ACTION_MASK) {
            case ShaderManager.UNIFORM_MVP_MATRIX /*0*/:
                mousePressed(3, point, motionEvent);
                break;
            case Sphere.PLANET_BLEND_MODE_NONE /*1*/:
            case Sphere.PLANET_BLEND_MODE_SOLID /*3*/:
            case ShaderManager.UNIFORM_ALPHA /*6*/:
                mouseReleased(point);
                break;
            case Sphere.PLANET_BLEND_MODE_ATMO /*2*/:
                mouseDragged(point, motionEvent);
                break;
            case ShaderManager.UNIFORM_COLOR_VECTOR /*5*/:
                mousePressed(2, point, motionEvent);
                break;
        }
        return true;
    }

    public void setFilterIcon(int i) {
        this.iFilterStatus = i;
    }

    public void setGpsIcon(int i) {
        this.iGPSStatus = i;
    }

    public void setMagDisturbanceStatus(boolean z) {
        this.iMagDisturbStatus = z;
    }

    public void setOrigin(CVector cVector) {
        this.origine.copy(cVector.times(this.cvLength2Unit));
    }

    public void setProperty(int i, boolean z) {
        switch (i) {
            case Sphere.PLANET_BLEND_MODE_FADE /*4*/:
                this.plot_compass = z;
            case ShaderManager.UNIFORM_COLOR_VECTOR /*5*/:
                this.plot_speed = z;
            case Constants.PROPERTY_PLOTG /*7*/:
                this.plot_g = z;
            case Constants.PROPERTY_TIMER /*11*/:
                this.plot_timer = z;
            case Constants.PROPERTY_GOOGLEMAP /*20*/:
                this.isGoogleMapsShown = z;
            default:
        }
    }

    public void setSize(DisplayMetrics displayMetrics) {
        this.mScreenHeight = (float) displayMetrics.heightPixels;
        this.mScreenWidth = (float) displayMetrics.widthPixels;
        this.screenDensity = displayMetrics.density;
        if (this.screenDensity == 0.0f) {
            this.screenDensity = 1.0f;
        }
        this.CanvasWidth = this.mScreenWidth * 1.0f;
        this.CanvasHeight = this.mScreenHeight * 0.95f;
        float f = 0.95f * this.mScreenHeight;
        int margin = getMargin();
        float[] fArr = new float[2];
        getInstrumentCenter((float) margin, f, this.CanvasHeight, this.CanvasWidth - ((float) margin), fArr);
        int i = (int) this.CanvasHeight;
        int i2 = ((int) this.CanvasWidth) - margin;
        this.rectField.left = fArr[0] - ((((float) i2) / 2.0f) * 0.99f);
        this.rectField.right = ((((float) i2) / 2.0f) * 0.99f) + fArr[0];
        this.rectField.bottom = fArr[1] + ((((float) i) / 2.0f) * 0.99f);
        this.rectField.top = fArr[1] - ((((float) i) / 2.0f) * 0.99f);
        setScale(this.xmin, this.xmax, this.ymin, this.ymax);
    }

    public void setUnitsMode(int i, String str, double d, String str2, double d2) {
        this.xmin = (float) (((double) this.xmin) / this.cvLength2Unit);
        this.xmax = (float) (((double) this.xmax) / this.cvLength2Unit);
        this.ymin = (float) (((double) this.ymin) / this.cvLength2Unit);
        this.ymax = (float) (((double) this.ymax) / this.cvLength2Unit);
        this.str_pos_unit = str;
        this.str_vel_unit = str2;
        this.cvLength2Unit = d;
        this.cvVelocity2Unit = d2;
        this.xmin = (float) (((double) this.xmin) * this.cvLength2Unit);
        this.xmax = (float) (((double) this.xmax) * this.cvLength2Unit);
        this.ymin = (float) (((double) this.ymin) * this.cvLength2Unit);
        this.ymax = (float) (((double) this.ymax) * this.cvLength2Unit);
    }
}
