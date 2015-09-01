package cc.minieye.instoolkit.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Window;

import cc.minieye.instoolkit.SimulationView;
import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.ui.FormHorizon;
import cc.minieye.kalman.util.Constants;

import java.util.Locale;

public final class FormGeneralPanel {
    private int displayId;
    private boolean hud_mode;
    private Context mContext;
    private float mScreenHeight;
    private float mScreenWidth;
    private Paint paint_dbgdata;
    private Paint paint_info_filled;
    private Paint paint_info_frame;
    private Paint paint_rawdata;
    private Paint paint_ticks;
    public FormCompass panelCompass;
    public FormGroundTrack panelGroundTrack;
    public FormHorizon panelHorizon;
    private boolean plot_forces;
    private boolean plot_googlemap;
    private boolean plot_rawdata;
    private boolean plot_referential;
    private String str_pos_unit;
    private String str_vel_unit;

    public FormGeneralPanel(Context context, DisplayMetrics displayMetrics, int i) {
        this.panelHorizon = null;
        this.panelGroundTrack = null;
        this.panelCompass = null;
        this.displayId = 1;
        this.paint_rawdata = new Paint();
        this.paint_dbgdata = new Paint();
        this.paint_ticks = new Paint();
        this.paint_info_filled = new Paint();
        this.paint_info_frame = new Paint();
        this.hud_mode = false;
        this.plot_forces = false;
        this.plot_referential = false;
        this.plot_rawdata = false;
        this.plot_googlemap = false;
        this.str_pos_unit = "m";
        this.str_vel_unit = "m/s";
        this.mContext = context;
        this.mScreenHeight = (float) displayMetrics.heightPixels;
        this.mScreenWidth = (float) displayMetrics.widthPixels;
        this.paint_rawdata.setColor(-7829368);
        this.paint_rawdata.setTextSize(12.0f);
        this.paint_rawdata.setAntiAlias(true);
        this.paint_rawdata.setStyle(Style.STROKE);
        this.paint_rawdata.setTypeface(Typeface.MONOSPACE);
        this.paint_dbgdata.setColor(-7829368);
        this.paint_dbgdata.setTextSize(12.0f);
        this.paint_dbgdata.setAntiAlias(true);
        this.paint_dbgdata.setStyle(Style.STROKE);
        this.paint_ticks.setColor(-1);
        this.paint_ticks.setAntiAlias(true);
        this.paint_ticks.setStyle(Style.STROKE);
        this.paint_ticks.setStrokeWidth(3.0f);
        this.paint_info_filled.setColor(-1);
        this.paint_info_filled.setTextSize(24.0f);
        this.paint_info_filled.setAntiAlias(true);
        this.paint_info_filled.setStyle(Style.FILL);
        this.paint_info_frame.setColor(-12303292);
        this.paint_info_frame.setAntiAlias(true);
        this.paint_info_frame.setStyle(Style.STROKE);
        this.paint_info_frame.setStrokeWidth(1.5f);
        setDisplayView(i);
        if (this.displayId == SimulationView.PANEL_HORIZON) {
            this.panelHorizon = new FormHorizon(context, displayMetrics, this.paint_rawdata, this.paint_info_filled, this.paint_info_frame, this.paint_ticks);
        } else if (this.displayId == SimulationView.PANEL_GROUNDTRACK) {
            this.panelGroundTrack = new FormGroundTrack(context, displayMetrics);
        } else if (this.displayId == SimulationView.PANEL_COMPAS) {
            this.panelCompass = new FormCompass(context, displayMetrics);
        }
    }

    private void DisplayVector(Canvas canvas, float f, float f2, CVector cVector, int i) {
        float[] fArr = new float[]{f, f2, ((float) cVector.getElement(2)) + f, f2 - ((float) cVector.getElement(3))};
        Paint paint = new Paint(this.paint_ticks);
        paint.setStrokeWidth(3.0f);
        paint.setColor(i);
        canvas.drawLines(fArr, paint);
    }

    public static int calculateInSampleSize(Options options, int i, int i2) {
        int i3 = options.outHeight;
        int i4 = options.outWidth;
        return (i3 > i2 || i4 > i) ? i4 > i3 ? Math.round(((float) i3) / ((float) i2)) : Math.round(((float) i4) / ((float) i)) : 1;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources resources, int i, int i2, int i3) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, i, options);
        options.inSampleSize = calculateInSampleSize(options, i2, i3);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, i, options);
    }

    private void drawGoogleMapFrame(Canvas canvas) {
        int i = ((int) (0.5f * this.mScreenWidth)) + 5;
        int i2 = (int) (0.3f * (1.1f * this.mScreenHeight));
        int i3 = (int) (this.mScreenWidth / 2.0f);
        int i4 = (int) (this.mScreenHeight - (((float) i2) / 2.0f));
        RectF rectF = new RectF();
        rectF.left = (float) (i3 - (i / 2));
        rectF.bottom = (float) ((i2 / 2) + i4);
        rectF.right = (float) ((i / 2) + i3);
        rectF.top = (float) (i4 - (i2 / 2));
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
    }

    private void setDisplayView(int i) {
        this.displayId = i;
    }

    public void DisplayForces(Canvas canvas, CVector cVector, CVector cVector2) {
        if (this.plot_forces) {
            float f = this.mScreenWidth / 2.0f;
            float f2 = this.mScreenHeight / 2.0f;
            float norm = 50.0f / ((float) cVector2.norm());
            DisplayVector(canvas, f, f2, cVector.times((double) (50.0f / ((float) cVector.norm()))), -65281);
            DisplayVector(canvas, f, f2, cVector2.times((double) norm), -16711681);
        }
    }

    public void DisplayRawData(Canvas canvas, CVector cVector, double d, CVector cVector2, double d2, CVector cVector3, double d3, CVector cVector4, double d4) {
        if (this.plot_rawdata && !this.hud_mode) {
            String format = String.format(Locale.ENGLISH, "Acc = %8.3f  %8.3f  %8.3f m/s2  (%3d Hz)", new Object[]{Double.valueOf(cVector.getElement(1)), Double.valueOf(cVector.getElement(2)), Double.valueOf(cVector.getElement(3)), Integer.valueOf((int) d)});
            String format2 = String.format(Locale.ENGLISH, "Gyr = %8.3f  %8.3f  %8.3f rad/s (%3d Hz)", new Object[]{Double.valueOf(cVector2.getElement(1)), Double.valueOf(cVector2.getElement(2)), Double.valueOf(cVector2.getElement(3)), Integer.valueOf((int) d2)});
            String format3 = String.format(Locale.ENGLISH, "Mag = %8.3f  %8.3f  %8.3f uT    (%3d Hz)", new Object[]{Double.valueOf(cVector3.getElement(1)), Double.valueOf(cVector3.getElement(2)), Double.valueOf(cVector3.getElement(3)), Integer.valueOf((int) d3)});
            String format4 = String.format(Locale.ENGLISH, "GPS = [h=%7.1f m, l=%5.1f deg, L=%5.1f deg] (%2d Hz)", new Object[]{Double.valueOf(cVector4.getElement(1)), Double.valueOf(cVector4.getElement(2)), Double.valueOf(cVector4.getElement(3)), Integer.valueOf((int) d4)});
            Rect rect = new Rect();
            this.paint_rawdata.getTextBounds(format4, 0, format4.length(), rect);
            float height = this.mScreenHeight - (1.2f * ((float) rect.height()));
            float width = (this.mScreenWidth - ((float) rect.width())) / 2.0f;
            RectF rectF = new RectF();
            rectF.left = (width - ((float) rect.left)) - 2.0f;
            rectF.bottom = 5.0f + height;
            rectF.right = (((float) rect.width()) + width) - ((float) rect.left);
            rectF.top = height - ((float) (rect.height() * 4));
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
            canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
            canvas.drawText(format, width, height - 36.0f, this.paint_rawdata);
            canvas.drawText(format2, width, height - 24.0f, this.paint_rawdata);
            canvas.drawText(format3, width, height - 12.0f, this.paint_rawdata);
            canvas.drawText(format4, width, height, this.paint_rawdata);
        }
    }

    public void DisplayReferential(Canvas canvas, CMatrix cMatrix) {
        if (this.plot_referential) {
            float f = this.mScreenWidth / 2.0f;
            float f2 = this.mScreenHeight / 2.0f;
            CVector times = cMatrix.times(new CVector(new double[]{1.0d, 0.0d, 0.0d}));
            CVector times2 = cMatrix.times(new CVector(new double[]{0.0d, 1.0d, 0.0d}));
            CVector times3 = cMatrix.times(new CVector(new double[]{0.0d, 0.0d, 1.0d}));
            DisplayVector(canvas, f, f2, times.times(100.0d), -65536);
            DisplayVector(canvas, f, f2, times2.times(100.0d), -16711936);
            DisplayVector(canvas, f, f2, times3.times(100.0d), -16776961);
        }
    }

    public void DrawBoard(Canvas canvas, double d, float f, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9, double d10, CVector cVector, CVector cVector2, CVector cVector3) {
        if (this.displayId == SimulationView.PANEL_HORIZON) {
            this.panelHorizon.DrawBoard(canvas, d, f, d4, d5, d6, d7, d8, d9, d10, cVector);
        } else if (this.displayId == SimulationView.PANEL_GROUNDTRACK) {
            this.panelGroundTrack.DrawBoard(canvas, d, d2, d3, d4, d5, d6, d7, d8, d9, d10, cVector, cVector2, cVector3);
            if (this.plot_googlemap) {
                drawGoogleMapFrame(canvas);
            }
        } else {
            this.panelCompass.DrawBoard(canvas, d, d7);
        }
    }

    public void addGroundMarker(CVector cVector) {
        this.panelGroundTrack.addGroundMarker(cVector);
    }

    public void dbgPrintMatrix(Canvas canvas, int i, int i2, CMatrix cMatrix) {
        int i3;
        int Rows = cMatrix.Rows();
        int Cols = cMatrix.Cols();
        int i4 = 1;
        String str = "";
        while (i4 <= Rows) {
            Object obj = " [";
            for (i3 = 1; i3 <= Cols; i3++) {
                obj = String.format(Locale.ENGLISH, "%s %8.3f", new Object[]{obj, Double.valueOf(cMatrix.Get(i4, i3))});
            }
            i4++;
            str = new StringBuilder(String.valueOf(obj)).append("]").toString();
        }
        Rect rect = new Rect();
        this.paint_dbgdata.getTextBounds(str, 0, str.length(), rect);
        float f = (float) (i2 * 12);
        float height = (this.mScreenHeight / 2.0f) - ((float) ((rect.height() + 6) * i));
        RectF rectF = new RectF();
        rectF.left = f - ((float) 2);
        rectF.right = (((float) rect.width()) + f) + ((float) 2);
        rectF.bottom = ((float) ((rect.height() + 1) * Rows)) + height;
        rectF.top = height - 5.0f;
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        for (i4 = 1; i4 <= Rows; i4++) {
            String obj = " [";
            for (i3 = 1; i3 <= Cols; i3++) {
                obj = String.format(Locale.ENGLISH, "%s %8.3f", new Object[]{obj, Double.valueOf(cMatrix.Get(i4, i3))});
            }
            canvas.drawText(new StringBuilder(String.valueOf(obj)).append("]").toString(), f, ((float) (i4 * 12)) + height, this.paint_dbgdata);
        }
    }

    public void dbgPrintScalar(Canvas canvas, int i, int i2, String str, double d) {
        String format = String.format(Locale.ENGLISH, "%s[ %8.3f]", new Object[]{str, Double.valueOf(d)});
        Rect rect = new Rect();
        this.paint_dbgdata.getTextBounds(format, 0, format.length(), rect);
        float f = (float) (i2 * 12);
        float textSize = this.mScreenHeight - (((float) (i + 1)) * (1.0f + (this.paint_dbgdata.getTextSize() + ((float) 10))));
        RectF rectF = new RectF();
        rectF.left = f - ((float) 5);
        rectF.right = (((float) rect.width()) + f) + ((float) 5);
        rectF.bottom = 5.0f + textSize;
        rectF.top = textSize - ((float) (rect.height() + 1));
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        canvas.drawText(format, f, textSize, this.paint_dbgdata);
    }

    public void dbgPrintVector(Canvas canvas, int i, int i2, String str, CVector cVector) {
        int Rows = cVector.Rows();
        Object stringBuilder = new StringBuilder(String.valueOf(str)).append("[").toString();
        for (int i3 = 1; i3 <= Rows; i3++) {
            stringBuilder = String.format(Locale.ENGLISH, "%s %8.3f", new Object[]{stringBuilder, Double.valueOf(cVector.getElement(i3))});
        }
        String stringBuilder2 = new StringBuilder(String.valueOf(stringBuilder)).append("]").toString();
        Rect rect = new Rect();
        this.paint_dbgdata.getTextBounds(stringBuilder2, 0, stringBuilder2.length(), rect);
        float f = (float) (i2 * 12);
        float textSize = this.mScreenHeight - (((float) (i + 1)) * (1.0f + (this.paint_dbgdata.getTextSize() + ((float) 10))));
        RectF rectF = new RectF();
        rectF.left = f - ((float) 5);
        rectF.right = (((float) rect.width()) + f) + ((float) 5);
        rectF.bottom = 5.0f + textSize;
        rectF.top = textSize - ((float) (rect.height() + 1));
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_filled);
        canvas.drawRoundRect(rectF, 5.0f, 5.0f, this.paint_info_frame);
        canvas.drawText(stringBuilder2, f, textSize, this.paint_dbgdata);
    }

    public int getDisplay() {
        return this.displayId;
    }

    public void resetGroundOrigin(CVector cVector) {
        this.panelGroundTrack.clear();
        this.panelGroundTrack.setOrigin(cVector);
    }

    public void setFilterIcon(int i) {
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setFilterIcon(i);
        }
        if (this.panelHorizon != null) {
            this.panelHorizon.setFilterIcon(i);
        }
    }

    public void setGpsIcon(int i) {
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setGpsIcon(i);
        }
        if (this.panelHorizon != null) {
            this.panelHorizon.setGpsIcon(i);
        }
    }

    public void setHUDMode(boolean z) {
        this.hud_mode = z;
        if (this.panelHorizon != null) {
            this.panelHorizon.setHUDMode(z);
        }
    }

    public void setMagDisturbanceStatus(boolean z) {
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setMagDisturbanceStatus(z);
        }
        if (this.panelHorizon != null) {
            this.panelHorizon.setMagDisturbanceStatus(z);
        }
    }

    public void setProperty(int i, boolean z) {
        if (this.panelHorizon != null) {
            this.panelHorizon.setProperty(i, z);
        }
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setProperty(i, z);
        }
    }

    public void setUnitsMode(int i) {
        double d;
        double d2 = 1.0d;
        if (i == 1) {
            this.str_pos_unit = "ft";
            this.str_vel_unit = "ft/s";
            d = Constants.METER2FEET;
            d2 = Constants.METER2FEET;
        } else if (i == 2) {
            this.str_pos_unit = "km";
            this.str_vel_unit = "km/h";
            d = Constants.METER2KM;
            d2 = 3600.0d * Constants.METER2KM;
        } else if (i == 3) {
            this.str_pos_unit = "mi";
            this.str_vel_unit = "mph";
            d = Constants.METER2MILES;
            d2 = 3600.0d * Constants.METER2MILES;
        } else {
            this.str_pos_unit = "m";
            this.str_vel_unit = "m/s";
            d = 1.0d;
        }
        if (this.panelHorizon != null) {
            this.panelHorizon.setUnitsMode(i, this.str_pos_unit, d, this.str_vel_unit, d2);
        }
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setUnitsMode(i, this.str_pos_unit, d, this.str_vel_unit, d2);
        }
        if (this.panelCompass != null) {
            this.panelCompass.setUnitsMode(i, this.str_pos_unit, d, this.str_vel_unit, d2);
        }
    }

    public void setWindow(Window window, int i, int i2, boolean z) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.widthPixels = i;
        displayMetrics.heightPixels = i2;
        displayMetrics.density = 1.0f;
        setWindow(window, displayMetrics, z);
    }

    public void setWindow(Window window, DisplayMetrics displayMetrics, boolean z) {
        this.mScreenHeight = (float) displayMetrics.heightPixels;
        this.mScreenWidth = (float) displayMetrics.widthPixels;
        if (this.panelHorizon != null) {
            this.panelHorizon.setSize(displayMetrics);
        }
        if (this.panelGroundTrack != null) {
            this.panelGroundTrack.setSize(displayMetrics);
        }
        if (this.panelCompass != null) {
            this.panelCompass.setSize(displayMetrics);
        }
    }
}
