package cc.minieye.instoolkit;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;

public final class DataRecorder {
    private boolean DEBUG;
    private String directory;
    private String filename;
    private int format;
    private Context mContext;
    boolean mExternalStorageAvailable;
    boolean mExternalStorageWriteable;
    private String nameApp;
    private OutputBufferInfo outFileGPX;
    private OutputBufferInfo outFileMatlab1;
    private OutputBufferInfo outFileMatlab2;
    private boolean recording;

    class OutputBufferInfo {
        public FileWriter filewriter;
        public BufferedWriter outputFile;
        public String outputName;
        public int type;

        OutputBufferInfo() {
        }
    }

    public DataRecorder(Context context, int i) {
        this.DEBUG = false;
        this.mExternalStorageAvailable = false;
        this.mExternalStorageWriteable = false;
        this.outFileMatlab1 = null;
        this.outFileMatlab2 = null;
        this.outFileGPX = null;
        this.recording = false;
        this.mContext = context;
        this.nameApp = (String) context.getResources().getText(R.string.app_name);
        this.directory = this.nameApp;
        this.filename = "INav";
        this.format = i;
        String externalStorageState = Environment.getExternalStorageState();
        if ("mounted".equals(externalStorageState)) {
            this.mExternalStorageWriteable = true;
            this.mExternalStorageAvailable = true;
        } else if ("mounted_ro".equals(externalStorageState)) {
            this.mExternalStorageAvailable = true;
            this.mExternalStorageWriteable = false;
        } else {
            this.mExternalStorageWriteable = false;
            this.mExternalStorageAvailable = false;
        }
    }

    private boolean closeExternalStorageFile(OutputBufferInfo outputBufferInfo) {
        if (outputBufferInfo != null) {
            try {
                outputBufferInfo.outputFile.close();
                outputBufferInfo.filewriter.close();
                Toast.makeText(this.mContext, "Data written to " + outputBufferInfo.outputName, Toast.LENGTH_LONG).show();
            } catch (Throwable e) {
                Toast.makeText(this.mContext, "Cannot close file " + outputBufferInfo.outputName, Toast.LENGTH_LONG).show();
                if (this.DEBUG) {
                    Log.w("ExternalStorage", "Error closing " + outputBufferInfo.outputName, e);
                }
            }
        }
        return true;
    }

    private boolean closeGPXFile(OutputBufferInfo outputBufferInfo) {
        if (outputBufferInfo != null) {
            try {
                outputBufferInfo.outputFile.write("    </trkseg>\n");
                outputBufferInfo.outputFile.write("  </trk>\n");
                outputBufferInfo.outputFile.write("</gpx>\n");
                outputBufferInfo.outputFile.close();
                outputBufferInfo.filewriter.close();
                Toast.makeText(this.mContext, "Data written to " + outputBufferInfo.outputName, Toast.LENGTH_LONG).show();
            } catch (Throwable e) {
                Toast.makeText(this.mContext, "Cannot close file " + outputBufferInfo.outputName, Toast.LENGTH_LONG).show();
                if (this.DEBUG) {
                    Log.w("ExternalStorage", "Error closing " + outputBufferInfo.outputName, e);
                }
            }
        }
        return true;
    }

    private boolean openExternalStorageFile(String str, int i, OutputBufferInfo outputBufferInfo) {
        if (this.mExternalStorageWriteable) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (externalStorageDirectory == null) {
                Toast.makeText(this.mContext, "Cannot write file. Please check access right!", Toast.LENGTH_SHORT).show();
            } else {
                String str2 = this.directory + File.separator + str;
                File file = new File(externalStorageDirectory, this.directory);
                if (file.exists() || file.mkdir()) {
                    file = new File(externalStorageDirectory, str2);
                    try {
                        outputBufferInfo.outputName = str;
                        outputBufferInfo.type = i;
                        outputBufferInfo.filewriter = new FileWriter(file);
                        outputBufferInfo.outputFile = new BufferedWriter(outputBufferInfo.filewriter);
                        outputBufferInfo.outputFile.write(String.format("# Data produced with %s for Android\n", new Object[]{this.mContext.getString(R.string.app_name)}));
                        Time r0 = new Time(Time.getCurrentTimezone());
                        r0.setToNow();
                        outputBufferInfo.outputFile.write(String.format("# %s\n", new Object[]{r0.format3339(false)}));
                        if (i == 1) {
                            outputBufferInfo.outputFile.write("# RAW DATA: outputs from sensors.\n");
                            outputBufferInfo.outputFile.write("# T     PSI    THETA      PHI      ACC_X    ACC_Y    ACC_Z    GYR_X     GYR_Y    GYR_Z    MAG_X     MAG_Y    MAG_Z   LAT   LON   ALT   GPA\n");
                            outputBufferInfo.outputFile.write("# Units: deg, m/s2, rad/s, uT\n");
                            outputBufferInfo.outputFile.write("# Values\n");
                            outputBufferInfo.outputFile.write("#        T   Time with respect to start of the app\n");
                            outputBufferInfo.outputFile.write("#        ACC Accelerations, m/s2\n");
                            outputBufferInfo.outputFile.write("#        GYR Angular velocities, rad/s\n");
                            outputBufferInfo.outputFile.write("#        MAG Magnetic field measurements, uT\n");
                            outputBufferInfo.outputFile.write("#        LAT Latitude GPS measurement, deg\n");
                            outputBufferInfo.outputFile.write("#        LON Longitude GPS measurement, deg\n");
                            outputBufferInfo.outputFile.write("#        ALT Altitude GPS measurement, m\n");
                            outputBufferInfo.outputFile.write("#        GPA GPS accuracy, m\n#\n");
                        } else {
                            outputBufferInfo.outputFile.write("# ESTIMATED DATA: outputs from the Kalman filter.\n");
                            outputBufferInfo.outputFile.write("# T     PSI    THETA      PHI      ACC_X    ACC_Y    ACC_Z    GYR_X     GYR_Y    GYR_Z\n");
                            outputBufferInfo.outputFile.write("# Units: deg, m/s2, rad/s, uT\n");
                            outputBufferInfo.outputFile.write("# Values\n");
                            outputBufferInfo.outputFile.write("#        T   Time with respect to start of the app\n");
                            outputBufferInfo.outputFile.write("#        ACC Accelerations, m/s2\n");
                            outputBufferInfo.outputFile.write("#        GYR Angular velocities, rad/s\n");
                            outputBufferInfo.outputFile.write("#        MAG Magnetic field measurements, uT\n#\n");
                        }
                    } catch (Throwable e) {
                        Toast.makeText(this.mContext, "Cannot write file " + file, Toast.LENGTH_SHORT).show();
                        if (this.DEBUG) {
                            Log.w("ExternalStorage", "Error opening file " + file, e);
                        }
                    }
                } else {
                    Toast.makeText(this.mContext, "Cannot create folder " + file, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this.mContext, "Cannot write file. Please check access right!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean openGPXFile(String str, int i, OutputBufferInfo outputBufferInfo) {
        if (this.mExternalStorageWriteable) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (externalStorageDirectory == null) {
                Toast.makeText(this.mContext, "Cannot write file. Please check access right!", Toast.LENGTH_SHORT).show();
            } else {
                String str2 = this.directory + File.separator + str;
                File file = new File(externalStorageDirectory, this.directory);
                if (file.exists() || file.mkdir()) {
                    file = new File(externalStorageDirectory, str2);
                    try {
                        outputBufferInfo.outputName = str;
                        outputBufferInfo.type = i;
                        outputBufferInfo.filewriter = new FileWriter(file);
                        outputBufferInfo.outputFile = new BufferedWriter(outputBufferInfo.filewriter);
                        outputBufferInfo.outputFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n");
                        outputBufferInfo.outputFile.write("<gpx xmlns=\"http://www.topographix.com/GPX/1/1\" creator=\"INav Android\" version=\"1.1\">\n");
                        outputBufferInfo.outputFile.write("<metadata>\n");
                        outputBufferInfo.outputFile.write(String.format("<desc>Data produced with %s</desc>\n", new Object[]{this.mContext.getString(R.string.app_name)}));
                        Time r0 = new Time(Time.getCurrentTimezone());
                        r0.setToNow();
                        outputBufferInfo.outputFile.write(String.format("<time>%s</time>\n", new Object[]{r0.format3339(false)}));
                        outputBufferInfo.outputFile.write("</metadata>\n");
                        outputBufferInfo.outputFile.write("  <trk>\n");
                        outputBufferInfo.outputFile.write("    <trkseg>\n");
                    } catch (Throwable e) {
                        Toast.makeText(this.mContext, "Cannot write file " + file, Toast.LENGTH_SHORT).show();
                        if (this.DEBUG) {
                            Log.w("ExternalStorage", "Error opening file " + file, e);
                        }
                    }
                } else {
                    Toast.makeText(this.mContext, "Cannot create folder " + file, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this.mContext, "Cannot write file. Please check access right!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void recordData() {
        if (this.recording) {
            Toast.makeText(this.mContext, "Stop recording.", Toast.LENGTH_SHORT).show();
            if (this.format == 0) {
                closeGPXFile(this.outFileGPX);
                this.outFileGPX = null;
            } else if (this.format == 1) {
                closeExternalStorageFile(this.outFileMatlab1);
                this.outFileMatlab1 = null;
                closeExternalStorageFile(this.outFileMatlab2);
                this.outFileMatlab2 = null;
            }
            this.recording = false;
            return;
        }
        Date time = Calendar.getInstance().getTime();
        String format = String.format("_%02d%02d%02dT%02d%02d%02d", new Object[]{Integer.valueOf(time.getYear() - 100), Integer.valueOf(time.getMonth()), Integer.valueOf(time.getDate()), Integer.valueOf(time.getHours()), Integer.valueOf(time.getMinutes()), Integer.valueOf(time.getSeconds())});
        Toast.makeText(this.mContext, "Start recording...", Toast.LENGTH_LONG).show();
        if (this.format == 0) {
            this.outFileGPX = new OutputBufferInfo();
            openGPXFile(this.filename + format + ".gpx", 0, this.outFileGPX);
        } else if (this.format == 1) {
            this.outFileMatlab1 = new OutputBufferInfo();
            openExternalStorageFile(this.filename + format + "_raw.txt", 1, this.outFileMatlab1);
            this.outFileMatlab2 = new OutputBufferInfo();
            openExternalStorageFile(this.filename + format + "_estim.txt", 2, this.outFileMatlab2);
        }
        this.recording = true;
    }

    private void writeDataToExternalStorageFile(OutputBufferInfo outputBufferInfo, float f, CVector[] cVectorArr, String[] strArr) {
        try {
            Object format = String.format(Locale.ENGLISH, "%8.3f ", new Object[]{Float.valueOf(f)});
            int length = cVectorArr.length;
            for (int i = 0; i < length; i++) {
                int Rows = cVectorArr[i].Rows();
                for (int i2 = 1; i2 <= Rows; i2++) {
                    format = String.format(Locale.ENGLISH, "%s " + strArr[i] + " ", new Object[]{format, Double.valueOf(cVectorArr[i].getElement(i2))});
                }
            }
            outputBufferInfo.outputFile.write(new StringBuilder(String.valueOf(format)).append("\n").toString());
        } catch (Throwable e) {
            Toast.makeText(this.mContext, "Cannot write to file " + outputBufferInfo.outputName, Toast.LENGTH_SHORT).show();
            if (this.DEBUG) {
                Log.w("ExternalStorage", "Error writing " + outputBufferInfo.outputName, e);
            }
        }
    }

    private void writeDataToGPXFile(OutputBufferInfo outputBufferInfo, float f, double d, double d2, double d3, float f2) {
        try {
            outputBufferInfo.outputFile.write(String.format(Locale.ENGLISH, "      <trkpt lat=\"%.10f\" lon=\"%.10f\">\n", new Object[]{Double.valueOf(Constants.RAD2DEG * d), Double.valueOf(Constants.RAD2DEG * d2)}));
            Time r0 = new Time(Time.getCurrentTimezone());
            r0.set((long) (1000.0d * ((double) f)));
            outputBufferInfo.outputFile.write(String.format(Locale.ENGLISH, "        <time>%s</time>\n", new Object[]{r0.format3339(false)}));
            outputBufferInfo.outputFile.write(String.format(Locale.ENGLISH, "        <ele>%f</ele>\n", new Object[]{Double.valueOf(d3)}));
            outputBufferInfo.outputFile.write(String.format(Locale.ENGLISH, "        <magvar>%f</magvar>\n", new Object[]{Float.valueOf(f2)}));
            outputBufferInfo.outputFile.write("      </trkpt>\n");
        } catch (Throwable e) {
            Toast.makeText(this.mContext, "Cannot write to file " + outputBufferInfo.outputName, Toast.LENGTH_SHORT).show();
            if (this.DEBUG) {
                Log.w("ExternalStorage", "Error writing " + outputBufferInfo.outputName, e);
            }
        }
    }

    boolean enableRecording(boolean z) {
        recordData();
        this.recording = z;
        return this.recording;
    }

    boolean isRecording() {
        return this.recording;
    }

    public void pushData(PositionFilter positionFilter) {
        if (this.recording) {
            CVector cVector;
            float absoluteTime;
            if (this.outFileMatlab1 != null) {
                cVector = new CVector(3);
                positionFilter.getAttitude().getMeasuredOrientation(cVector);
                CVector measureLatLonAlt = positionFilter.getMeasureLatLonAlt();
                OutputBufferInfo outputBufferInfo = this.outFileMatlab1;
                absoluteTime = (float) positionFilter.getAbsoluteTime();
                CVector[] cVectorArr = new CVector[5];
                cVectorArr[0] = cVector.times((double) Constants.RAD2DEG);
                cVectorArr[1] = positionFilter.getMeasureTotalAcc();
                cVectorArr[2] = positionFilter.getAttitude().getMeasureGyro().times((double) Constants.RAD2DEG);
                cVectorArr[3] = positionFilter.getAttitude().getMeasureMag();
                cVectorArr[4] = new CVector(new float[]{(float) (measureLatLonAlt.getElement(1) * Constants.RAD2DEG), (float) (measureLatLonAlt.getElement(2) * Constants.RAD2DEG), (float) measureLatLonAlt.getElement(3), positionFilter.getGPSAccuracy()});
                writeDataToExternalStorageFile(outputBufferInfo, absoluteTime, cVectorArr, new String[]{"%12.6f", "%12.6f", "%12.6f", "%12.6f", "%18.13g"});
            }
            if (this.outFileMatlab2 != null) {
                float timeStamp = (float) positionFilter.getTimeStamp();
                writeDataToExternalStorageFile(this.outFileMatlab2, timeStamp, new CVector[]{positionFilter.getEstimatedAngles().times((double) Constants.RAD2DEG), positionFilter.getEstimatedAcceleration(), positionFilter.getEstimatedAngularVelocity().times((double) Constants.RAD2DEG)}, new String[]{"%12.6f", "%12.6f", "%12.6f"});
            }
            if (this.outFileGPX != null) {
                absoluteTime = (float) positionFilter.getTimeStamp();
                cVector = positionFilter.getEstimatedLatLonAlt();
                writeDataToGPXFile(this.outFileGPX, absoluteTime, cVector.getElement(1), cVector.getElement(2), cVector.getElement(3), (float) positionFilter.getBearing());
            }
        }
    }

    void setFileFormat(int i) {
        boolean z = false;
        if (this.recording && this.format != i) {
            enableRecording(false);
            z = true;
        }
        this.format = i;
        if (z) {
            enableRecording(true);
        }
    }
}
