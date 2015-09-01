package cc.minieye.kalman.maths;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CDataArray implements Parcelable {
    public static final Creator CREATOR;
    private int dim;
    private int index;
    public ArrayList<String> legends;
    public String legendx;
    private int max_pts;
    private float[] maxval;
    private float[] minval;
    private int size;
    public String title;
    private List<float[]> values;
    private float xmax;
    private float xmin;

    static {
        CREATOR = new Creator() {
            public CDataArray createFromParcel(Parcel parcel) {
                return new CDataArray(parcel);
            }

            public CDataArray[] newArray(int i) {
                return new CDataArray[i];
            }
        };
    }

    public CDataArray(int i, int i2) {
        int i3 = 0;
        this.max_pts = 200;
        this.index = 0;
        this.size = 0;
        this.dim = 0;
        this.minval = new float[10];
        this.maxval = new float[10];
        this.legends = new ArrayList();
        this.dim = i2;
        this.xmin = 1.0E20f;
        this.xmax = -1.0E20f;
        this.title = new String("");
        this.legendx = new String("");
        while (i3 < this.dim) {
            this.minval[i3] = 1.0E20f;
            this.maxval[i3] = -1.0E20f;
            this.legends.add("");
            i3++;
        }
        if (i > this.max_pts) {
            this.max_pts = i;
        }
        this.values = new ArrayList(this.max_pts);
    }

    public CDataArray(Parcel parcel) {
        this.max_pts = 200;
        this.index = 0;
        this.size = 0;
        this.dim = 0;
        this.minval = new float[10];
        this.maxval = new float[10];
        this.legends = new ArrayList();
        readFromParcel(parcel);
    }

    public CDataArray(String str, String str2, String[] strArr, int i) {
        int i2 = 0;
        this.max_pts = 200;
        this.index = 0;
        this.size = 0;
        this.dim = 0;
        this.minval = new float[10];
        this.maxval = new float[10];
        this.legends = new ArrayList();
        this.dim = 0;
        if (strArr != null) {
            this.dim = strArr.length;
        }
        this.xmin = 1.0E20f;
        this.xmax = -1.0E20f;
        this.title = new String(str);
        this.legendx = new String(str2);
        while (i2 < this.dim) {
            this.minval[i2] = 1.0E20f;
            this.maxval[i2] = -1.0E20f;
            this.legends.add(strArr[i2]);
            i2++;
        }
        if (i > this.max_pts) {
            this.max_pts = i;
        }
        this.values = new ArrayList(this.max_pts);
    }

    private void readFromParcel(Parcel parcel) {
        this.dim = parcel.readInt();
        this.max_pts = parcel.readInt();
        this.size = parcel.readInt();
        this.index = parcel.readInt();
        this.title = parcel.readString();
        this.legends = parcel.createStringArrayList();
        this.legendx = parcel.readString();
        this.xmin = parcel.readFloat();
        this.xmax = parcel.readFloat();
        parcel.readFloatArray(this.minval);
        parcel.readFloatArray(this.maxval);
        this.values = new ArrayList(this.max_pts);
        parcel.readList(this.values, null);
    }

    private void updateBounds() {
        int length = ((float[]) this.values.get(0)).length - 1;
        for (int i = 0; i < length; i++) {
            this.minval[i] = 1.0E20f;
            this.maxval[i] = -1.0E20f;
        }
        for (int i2 = 0; i2 < size(); i2++) {
            for (int i3 = 0; i3 < length; i3++) {
                float f = ((float[]) this.values.get(i2))[i3 + 1];
                if (f < this.minval[i3]) {
                    this.minval[i3] = f;
                }
                if (f > this.maxval[i3]) {
                    this.maxval[i3] = f;
                }
            }
        }
    }

    public float MaxX() {
        this.xmax = -1.0E20f;
        for (int i = 0; i < size(); i++) {
            float f = ((float[]) this.values.get(i))[0];
            if (f > this.xmax) {
                this.xmax = f;
            }
        }
        return this.xmax;
    }

    public float MaxY(int i) {
        return i > this.dim ? this.maxval[0] : this.maxval[i];
    }

    public float MinX() {
        this.xmin = 1.0E20f;
        for (int i = 0; i < size(); i++) {
            float f = ((float[]) this.values.get(i))[0];
            if (f < this.xmin) {
                this.xmin = f;
            }
        }
        return this.xmin;
    }

    public float MinY(int i) {
        return i > this.dim ? this.minval[0] : this.minval[i];
    }

    public void addValue(float f, float f2) {
        if (dimension() != 1) {
            throw new ArrayIndexOutOfBoundsException("Size not consistent with definition of DataArray.");
        }
        if (size() < this.max_pts) {
            this.values.add(new float[]{f, f2});
            this.size++;
        } else {
            this.values.set(this.index, new float[]{f, f2});
        }
        this.index++;
        if (this.index >= this.max_pts) {
            this.index = 0;
            updateBounds();
        }
        if (f < this.xmin) {
            this.xmin = f;
        } else if (f > this.xmax) {
            this.xmax = f;
        }
        if (f2 < this.minval[0]) {
            this.minval[0] = f2;
        } else if (f2 > this.maxval[0]) {
            this.maxval[0] = f2;
        }
    }

    public void addValue(float f, float f2, float f3, float f4) {
        if (dimension() != 3) {
            throw new ArrayIndexOutOfBoundsException("Size not consistent with definition of DataArray.");
        }
        if (size() < this.max_pts) {
            this.values.add(new float[]{f, f2, f3, f4});
            this.size++;
        } else {
            this.values.set(this.index, new float[]{f, f2, f3, f4});
        }
        this.index++;
        if (this.index >= this.max_pts) {
            this.index = 0;
            updateBounds();
        }
        if (f < this.xmin) {
            this.xmin = f;
        } else if (f > this.xmax) {
            this.xmax = f;
        }
        if (f2 < this.minval[0]) {
            this.minval[0] = f2;
        } else if (f2 > this.maxval[0]) {
            this.maxval[0] = f2;
        }
        if (f3 < this.minval[1]) {
            this.minval[1] = f3;
        } else if (f3 > this.maxval[1]) {
            this.maxval[1] = f3;
        }
        if (f4 < this.minval[2]) {
            this.minval[2] = f4;
        } else if (f4 > this.maxval[2]) {
            this.maxval[2] = f4;
        }
    }

    public void addValue(float f, CVector cVector) {
        int Rows = cVector.Rows();
        if (Rows != dimension()) {
            throw new ArrayIndexOutOfBoundsException("Size not consistent with definition of DataArray.");
        }
        float[] obj = new float[(Rows + 1)];
        obj[0] = f;
        for (int i = 0; i < Rows; i++) {
            float element = (float) cVector.getElement(i + 1);
            obj[i + 1] = element;
            if (element < this.minval[i]) {
                this.minval[i] = element;
            }
            if (element > this.maxval[i]) {
                this.maxval[i] = element;
            }
        }
        if (size() < this.max_pts) {
            this.values.add(obj);
            this.size++;
        } else {
            this.values.set(this.index, obj);
        }
        this.index++;
        if (this.index >= this.max_pts) {
            this.index = 0;
            updateBounds();
        }
        if (f < this.xmin) {
            this.xmin = f;
        }
        if (f > this.xmax) {
            this.xmax = f;
        }
    }

    public void addValue(float f, CVector cVector, CVector cVector2) {
        addValue(f, new CVector(cVector).cat(cVector2));
    }

    public void clear() {
        this.values.clear();
        this.index = 0;
        this.size = 0;
    }

    public int describeContents() {
        return 0;
    }

    public int dimension() {
        return this.dim;
    }

    public float get(int i, int i2) {
        if (i > size()) {
            throw new ArrayIndexOutOfBoundsException("Index I too big.");
        } else if (i2 <= dimension()) {
            return ((float[]) this.values.get(i))[i2];
        } else {
            throw new ArrayIndexOutOfBoundsException("Index J too big.");
        }
    }

    public float[] get(int i) {
        if (i <= size()) {
            return (float[]) this.values.get(i);
        }
        throw new ArrayIndexOutOfBoundsException("Index I too big.");
    }

    public float[] getLastPoint() {
        return (this.values.size() == 0 || this.index < 1) ? new float[this.dim] : (float[]) this.values.get(this.index - 1);
    }

    public float getRollingData(int i, int i2) {
        if (i > size()) {
            throw new ArrayIndexOutOfBoundsException("Index I too big.");
        } else if (i2 > dimension()) {
            throw new ArrayIndexOutOfBoundsException("Index J too big.");
        } else {
            int i3 = this.index + i;
            if (i3 >= size()) {
                i3 -= size();
            }
            return ((float[]) this.values.get(i3))[i2];
        }
    }

    public List<float[]> getValues() {
        return this.values;
    }

    public int size() {
        return this.size;
    }

    public boolean writeToFile(String str) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory == null) {
            return false;
        }
        File file = new File(externalStorageDirectory, str);
        if (!file.exists()) {
            return false;
        }
        try {
            Writer fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("# \n");
            bufferedWriter.write("# \n");
            int size = size();
            for (int i = 0; i < size - 1; i++) {
                Object format = String.format(Locale.ENGLISH, "%5f ", new Object[]{Float.valueOf(((float[]) this.values.get(i))[0])});
                int i2 = 0;
                while (i2 < this.dim) {
                    Object[] objArr = new Object[]{format, Float.valueOf(((float[]) this.values.get(i))[i2 + 1])};
                    i2++;
                    String format2 = String.format(Locale.ENGLISH, "%s %8.3f ", objArr);
                }
                bufferedWriter.write(new StringBuilder(String.valueOf(format)).append("\n").toString());
            }
            bufferedWriter.close();
            fileWriter.close();
            return true;
        } catch (Throwable e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
            return false;
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.dim);
        parcel.writeInt(this.max_pts);
        parcel.writeInt(this.size);
        parcel.writeInt(this.index);
        parcel.writeString(this.title);
        parcel.writeStringList(this.legends);
        parcel.writeString(this.legendx);
        parcel.writeFloat(this.xmin);
        parcel.writeFloat(this.xmax);
        parcel.writeFloatArray(this.minval);
        parcel.writeFloatArray(this.maxval);
        parcel.writeList(this.values);
    }
}
