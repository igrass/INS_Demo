package cc.minieye.instoolkit.drag3;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.util.Locale;

import cc.minieye.instoolkit.R;
import cc.minieye.kalman.util.Constants;

public class CellDataFormater {
    public static Format[] dCellTextInfo;
    int descHeight;
    int height;
    Paint mPaint;
    Paint mPaintBorder;
    Paint mPaintDescription;
    Paint mPaintFrame;
    Paint mPaintUnits;
    int width;

    public static class Format {
        public String desc;
        public String fmt;
        public int img;
        public String title;
        public int type;
        public String unit;

        public Format(String str, String str2, String str3, String str4, int i, int i2) {
            this.title = str;
            this.desc = str2;
            this.unit = str3;
            this.fmt = str4;
            this.type = i;
            if (i2 > 0) {
                this.img = i2;
            } else {
                this.img = R.drawable.ic_launcher;
            }
        }

        public int getDescImage() {
            return this.img;
        }

        public String getDescStr() {
            return this.desc;
        }

        public RowItem getRowItem() {
            return new RowItem(this.img, this.title, this.desc);
        }

        public String getTitle() {
            return this.title;
        }

        public String toString() {
            return this.desc;
        }
    }

    static {
        dCellTextInfo = new Format[]{new Format("Roll", "Roll angle", "deg", "%3.1f", Constants.DATA_ANGLE_ROLL_RAW, R.drawable.ic_launcher), new Format("Pitch", "Pitch angle", "deg", "%3.1f", Constants.DATA_ANGLE_PITCH_RAW, R.drawable.ic_launcher), new Format("Yaw", "Yaw angle", "deg", "%3.1f", Constants.DATA_ANGLE_YAW_RAW, R.drawable.ic_launcher), new Format("Heading", "Measured heading angle", "deg", "%3.1f", Constants.DATA_ANGLE_HEAD_RAW, R.drawable.ic_launcher), new Format("roll (estimate)", "Estimation of the roll angle", "deg", "%3.1f", Constants.DATA_ANGLE_ROLL_EST, R.drawable.ic_launcher), new Format("pitch (estimate)", "Estimation of the pitch angle", "deg", "%3.1f", Constants.DATA_ANGLE_PITCH_EST, R.drawable.ic_launcher), new Format("yaw (estimate)", "Estimation of the yaw angle", "deg", "%3.1f", Constants.DATA_ANGLE_YAW_EST, R.drawable.ic_launcher), new Format("Roll angular rate", "Roll angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_ROLL_RAW, R.drawable.ic_launcher), new Format("Pitch angular rate", "Pitch angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_PITCH_RAW, R.drawable.ic_launcher), new Format("Yaw angular rate", "Yaw angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_YAW_RAW, R.drawable.ic_launcher), new Format("Roll angular rate (estimate)", "Estimation of the roll angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_ROLL_EST, R.drawable.ic_launcher), new Format("Yaw angular rate (estimate)", "Estimation of the yaw angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_PITCH_EST, R.drawable.ic_launcher), new Format("Pitch angular rate (estimate)", "Estimation of the pitch angular velocity", "deg/s", "%3.1f", Constants.DATA_ANGLER_YAW_EST, R.drawable.ic_launcher), new Format("Acceleration X", "Measured acceleration in the X-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_X_MES, R.drawable.ic_launcher), new Format("Acceleration Y", "Measured acceleration in the Y-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_Y_MES, R.drawable.ic_launcher), new Format("Acceleration Z", "Measured acceleration in the Z-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_Z_MES, R.drawable.ic_launcher), new Format("Lin. acceleration X (estimate)", "Estimated acceleration in the X-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_X_EST, R.drawable.ic_launcher), new Format("Lin. acceleration Y (estimate)", "Estimated acceleration in the Y-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_Y_EST, R.drawable.ic_launcher), new Format("Lin. acceleration Z (estimate)", "Estimated acceleration in the Z-body direction", "m/s2", "%2.2f", Constants.DATA_ACC_Z_EST, R.drawable.ic_launcher), new Format("Velocity ECEF X", "Velocity in the X inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_X_MES, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Velocity ECEF Y", "Velocity in the Y inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_Y_MES, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Velocity ECEF Z", "Velocity in the Z inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_Z_MES, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Velocity ECEF X (estimate)", "Velocity in the X inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_X_EST, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Velocity ECEF Y (estimate)", "Velocity in the Y inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_Y_EST, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Velocity ECEF Z (estimate)", "Velocity in the Z inertial direction", "m/s", "%3.1f", Constants.DATA_VEL_Z_EST, cc.minieye.instoolkit.R.drawable.icon_speed), new Format("Position ECEF X", "Position in the X inertial direction", "km", "%3.1f", Constants.DATA_POS_X_MES, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Position ECEF Y", "Position in the Y inertial direction", "km", "%3.1f", Constants.DATA_POS_Y_MES, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Position ECEF Z", "Position in the Z inertial direction", "km", "%3.1f", Constants.DATA_POS_Z_MES, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Position ECEF X (estimate)", "Position in the X inertial direction", "km", "%3.1f", Constants.DATA_POS_X_EST, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Position ECEF Y (estimate)", "Position in the Y inertial direction", "km", "%3.1f", Constants.DATA_POS_Y_EST, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Position ECEF Z (estimate)", "Position in the Z inertial direction", "km", "%3.1f", Constants.DATA_POS_Z_EST, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Magnetic field X", "Measure of the magnetic field in the device X-axis", "uT", "%2.2f", Constants.DATA_MAG_X_MES, cc.minieye.instoolkit.R.drawable.icon_magnet), new Format("Magnetic field Y", "Measure of the magnetic field in the device Y-axis", "uT", "%2.2f", Constants.DATA_MAG_Y_MES, cc.minieye.instoolkit.R.drawable.icon_magnet), new Format("Magnetic field Z", "Measure of the magnetic field in the device Z-axis", "uT", "%2.2f", Constants.DATA_MAG_Z_MES, cc.minieye.instoolkit.R.drawable.icon_magnet), new Format("Altitude", "Measured altitude from GPS measurement", "m", "%.0f", Constants.DATA_ALT_MES, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("Altitude (estimate)", "Estimation of the altitude", "m", "%.0f", Constants.DATA_ALT_EST, cc.minieye.instoolkit.R.drawable.icon_geolocation), new Format("G (estimate)", "Estimation of the G-force", "g", "%.1f", Constants.DATA_G_EST, R.drawable.ic_launcher), new Format("Time Stamp", "GPS time stamp", "s", "%.2f", Constants.DATA_TIME, R.drawable.ic_launcher), new Format("GPS Accuracy", "GPS position accuracy", "m", "%.1f", Constants.DATA_GPS_ACCURACY, R.drawable.ic_launcher), new Format("Wifi strength", "Wifi signal strength", "dBm", "%.1f", Constants.DATA_WIFI_STRENGTH, R.drawable.ic_launcher), new Format("Battery Level", "The current battery capacity", "%", "%.0f", Constants.DATA_BATTERY_LEVEL, R.drawable.ic_launcher), new Format("Device Temperature", "The current temperature", "\u00b0C", "%.0f", Constants.DATA_TEMPERATURE, cc.minieye.instoolkit.R.drawable.icon_temperature), new Format("Luminosity", "The current luminosity", "Lux", "%.0f", Constants.DATA_LUMINOSITY, R.drawable.ic_launcher)};
    }

    public CellDataFormater(int i, int i2) {
        this.descHeight = 0;
        this.width = i;
        this.height = i2;
        this.mPaint = new Paint();
        this.mPaint.setColor(-16711936);
        this.mPaint.setTextSize((float) ((i * 40) / 150));
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setTypeface(Typeface.MONOSPACE);
        this.mPaintUnits = new Paint(this.mPaint);
        this.mPaintUnits.setTextSize((float) ((i * 15) / 150));
        this.mPaintDescription = new Paint(this.mPaint);
        this.mPaintDescription.setTextSize((float) ((i * 14) / 150));
        this.mPaintDescription.setColor(-1);
        this.mPaintFrame = new Paint();
        this.mPaintFrame.setColor(-16776961);
        this.mPaintFrame.setAntiAlias(true);
        this.mPaintFrame.setStyle(Style.STROKE);
        this.mPaintFrame.setStrokeWidth(1.5f);
        this.mPaintBorder = new Paint(this.mPaintFrame);
        this.mPaintBorder.setColor(-12303292);
        Rect rect = new Rect();
        this.mPaintDescription.getTextBounds("description", 0, "description".length(), rect);
        this.descHeight = rect.height();
    }

    public Bitmap getDataBitmap(int i, float f) {
        Canvas canvas = new Canvas();
        Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
        canvas.setBitmap(createBitmap);
        int i2 = 0;
        while (i2 < dCellTextInfo.length) {
            if (dCellTextInfo[i2].type == i) {
                break;
            }
            i2++;
        }
        i2 = 0;
        Rect rect = new Rect();
        canvas.drawText(dCellTextInfo[i2].title, (float) 5, (float) (this.height - 3), this.mPaintDescription);
        RectF rectF = new RectF();
        rectF.top = (float) 5;
        rectF.bottom = (float) (((this.height - 3) - 5) - this.descHeight);
        rectF.right = (float) (this.width - 5);
        rectF.left = (float) 5;
        String str = dCellTextInfo[i2].unit;
        if (str == "m" && Math.abs(f) > 1000.0f) {
            str = "km";
            f /= 1000.0f;
        } else if (str == "s" && Math.abs(f) > 60.0f) {
            if (Math.abs(f) > 86400.0f) {
                str = "day";
                f /= 86400.0f;
            } else if (Math.abs(f) > 3600.0f) {
                str = "hr";
                f /= 3600.0f;
            } else {
                str = "min";
                f /= 60.0f;
            }
        }
        this.mPaintUnits.getTextBounds(str, 0, str.length(), rect);
        canvas.drawText(str, (rectF.right - ((float) rect.width())) - ((float) 5), ((float) rect.height()) + (rectF.top + ((float) 3)), this.mPaintUnits);
        String str2 = dCellTextInfo[i2].fmt;
        canvas.drawText(String.format(Locale.US, str2, new Object[]{Float.valueOf(f)}), rectF.left + ((float) 5), rectF.bottom - ((float) 3), this.mPaint);
        canvas.drawLine(0.0f, rectF.bottom, (float) this.width, rectF.bottom, this.mPaintFrame);
        return createBitmap;
    }
}
