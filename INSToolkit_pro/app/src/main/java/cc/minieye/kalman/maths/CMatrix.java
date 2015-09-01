package cc.minieye.kalman.maths;

import android.util.Log;
import java.lang.reflect.Array;
import java.util.Locale;

public class CMatrix {
    protected double[][] array;
    protected int col;
    protected int row;

    public CMatrix(int i, int i2) {
        this.array = (double[][]) Array.newInstance(Double.TYPE, new int[]{i2, i});
        this.row = i;
        this.col = i2;
    }

    public CMatrix(CMatrix cMatrix) {
        this.row = cMatrix.row;
        this.col = cMatrix.col;
        this.array = (double[][]) Array.newInstance(Double.TYPE, new int[]{this.col, this.row});
        copyFrom(cMatrix);
    }

    public CMatrix(double[][] dArr) {
        int length = dArr.length;
        int length2 = dArr[0].length;
        this.array = (double[][]) Array.newInstance(Double.TYPE, new int[]{length, length2});
        this.row = length2;
        this.col = length;
        for (int i = 0; i < length2; i++) {
            for (int i2 = 0; i2 < length; i2++) {
                this.array[i2][i] = dArr[i][i2];
            }
        }
    }

    public CMatrix(double[][] dArr, int i, int i2) {
        this.array = dArr;
        this.row = i;
        this.col = i2;
    }

    public static final CMatrix Identity(int i) {
        CMatrix cMatrix = new CMatrix(i, i);
        cMatrix.setIdentity();
        return cMatrix;
    }

    public static CMatrix rotationMatrix(double d, double d2, double d3) {
        CMatrix cMatrix = new CMatrix(3, 3);
        double cos = Math.cos(d3);
        double sin = Math.sin(d3);
        double cos2 = Math.cos(d2);
        double sin2 = Math.sin(d2);
        double cos3 = Math.cos(d);
        double sin3 = Math.sin(d);
        cMatrix.Set(0, 0, cos2 * cos);
        cMatrix.Set(0, 1, ((-cos3) * sin) + ((sin3 * sin2) * cos));
        cMatrix.Set(0, 2, (sin3 * sin) + ((cos3 * sin2) * cos));
        cMatrix.Set(1, 0, cos2 * sin);
        cMatrix.Set(1, 1, (cos3 * cos) + ((sin3 * sin2) * sin));
        int i = 2;
        cMatrix.Set(1, i, (cos * (-sin3)) + (sin * (cos3 * sin2)));
        cMatrix.Set(2, 0, -sin2);
        cMatrix.Set(2, 1, cos2 * sin3);
        cMatrix.Set(2, 2, cos3 * cos2);
        return cMatrix;
    }

    public void Cholesky(CMatrix cMatrix) {
        try {
            CholeskyFactorisation.spdmatrixcholesky(this, this.col, true, cMatrix);
        } catch (MatrixException e) {
            Log.d("CMATRIX", "CMatrix is not positive definite for Cholesky().");
            cMatrix.setIdentity();
        }
    }

    public int Cols() {
        return this.col;
    }

    public double Determinant() {
        return new LUDecomposition(this, true).det();
    }

    public final double Get(int i, int i2) {
        if (this.row >= i && this.col >= i2 && i2 >= 1 && i >= 1) {
            return this.array[i2 - 1][i - 1];
        }
        throw new ArrayIndexOutOfBoundsException("Bad indices.");
    }

    public final CVector GetColumn(int i) {
        CVector cVector = new CVector(Rows());
        for (int i2 = 1; i2 <= Rows(); i2++) {
            cVector.setElement(i2, this.array[i - 1][i2 - 1]);
        }
        return cVector;
    }

    public void InverseSquareMat(CMatrix cMatrix) {
        if (this.row != this.col) {
            throw new IllegalArgumentException("CMatrix is not square for InverseSquareMat(). Use Inverse() instead.");
        }
        CMatrix cMatrix2 = new CMatrix(this.row, this.col);
        cMatrix2.setIdentity();
        new LUDecomposition(this, true).solve(cMatrix2, cMatrix);
    }

    public int Rows() {
        return this.row;
    }

    public final void Set(int i, int i2, double d) {
        if (this.row < i || this.col < i2 || i2 < 1 || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        }
        this.array[i2 - 1][i - 1] = d;
    }

    public final CMatrix Transpose() {
        CMatrix cMatrix = new CMatrix(this.col, this.row);
        double[][] array = cMatrix.getArray();
        for (int i = 0; i < this.row; i++) {
            for (int i2 = 0; i2 < this.col; i2++) {
                array[i][i2] = this.array[i2][i];
            }
        }
        return cMatrix;
    }

    public void Zeros() {
        for (int i = 0; i < this.row; i++) {
            for (int i2 = 0; i2 < this.col; i2++) {
                this.array[i2][i] = 0.0d;
            }
        }
    }

    protected void checkMatrixDimensions(CMatrix cMatrix) {
        if (cMatrix.row != this.row || cMatrix.col != this.col) {
            throw new IllegalArgumentException("CMatrix dimensions must agree.");
        }
    }

    public Object clone() {
        return copy();
    }

    public CMatrix copy() {
        CMatrix cMatrix = new CMatrix(this.row, this.col);
        for (int i = 0; i < this.col; i++) {
            System.arraycopy(this.array[i], 0, cMatrix.array[i], 0, this.row);
        }
        return cMatrix;
    }

    public void copyFrom(CMatrix cMatrix) {
        for (int i = 0; i < this.col; i++) {
            System.arraycopy(cMatrix.array[i], 0, this.array[i], 0, this.row);
        }
    }

    public void copyTo(CMatrix cMatrix) {
        for (int i = 0; i < this.col; i++) {
            System.arraycopy(this.array[i], 0, cMatrix.array[i], 0, this.row);
        }
    }

    public double[][] getArray() {
        return this.array;
    }

    public double[][] getArrayCopy() {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{this.col, this.row});
        for (int i = 0; i < this.col; i++) {
            System.arraycopy(this.array[i], 0, dArr[i], 0, this.row);
        }
        return dArr;
    }

    public void getLU(CMatrix cMatrix, CMatrix cMatrix2) {
        LUDecomposition lUDecomposition = new LUDecomposition(this, true);
        lUDecomposition.getL().copyTo(cMatrix);
        lUDecomposition.getU().copyTo(cMatrix2);
    }

    public final CMatrix getMatrix(int i, int i2, int i3, int i4) {
        CMatrix cMatrix = new CMatrix((i2 - i) + 1, (i4 - i3) + 1);
        double[][] array = cMatrix.getArray();
        int i5 = i3;
        while (i5 <= i4) {
            try {
                System.arraycopy(this.array[i5 - 1], i - 1, array[i5 - i3], 0, (i2 - i) + 1);
                i5++;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Submatrix indices");
            }
        }
        return cMatrix;
    }

    public final CMatrix getMatrix(int[] iArr, int i, int i2) {
        CMatrix cMatrix = new CMatrix(iArr.length, (i2 - i) + 1);
        double[][] array = cMatrix.getArray();
        int i3 = 0;
        while (i3 < iArr.length) {
            try {
                for (int i4 = i; i4 <= i2; i4++) {
                    array[i4 - i][i3] = this.array[i4][iArr[i3]];
                }
                i3++;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Submatrix indices");
            }
        }
        return cMatrix;
    }

    public CMatrix minus(CMatrix cMatrix) {
        CMatrix cMatrix2 = new CMatrix(this.row, this.col);
        double[][] array = cMatrix2.getArray();
        checkMatrixDimensions(cMatrix);
        for (int i = 0; i < this.row; i++) {
            for (int i2 = 0; i2 < this.col; i2++) {
                array[i2][i] = this.array[i2][i] - cMatrix.array[i2][i];
            }
        }
        return cMatrix2;
    }

    public CMatrix plus(CMatrix cMatrix) {
        checkMatrixDimensions(cMatrix);
        CMatrix cMatrix2 = new CMatrix(this);
        cMatrix2.plusSelf(cMatrix);
        return cMatrix2;
    }

    public void plusSelf(CMatrix cMatrix) {
        checkMatrixDimensions(cMatrix);
        for (int i = 0; i < this.row; i++) {
            for (int i2 = 0; i2 < this.col; i2++) {
                double[] dArr = this.array[i2];
                dArr[i] = dArr[i] + cMatrix.array[i2][i];
            }
        }
    }

    public void setDiagonal(double[] dArr) {
        Zeros();
        int length = dArr.length;
        if (this.row < length || this.col < length) {
            throw new ArrayIndexOutOfBoundsException("Bad length for diagonal.");
        }
        for (int i = 1; i <= length; i++) {
            Set(i, i, dArr[i - 1]);
        }
    }

    public final void setIdentity() {
        Zeros();
        for (int i = 0; i < this.row; i++) {
            this.array[i][i] = 1.0d;
        }
    }

    public final void setMatrix(int i, int i2, int i3, int i4, CMatrix cMatrix) {
        int i5 = i3;
        while (i5 <= i4) {
            try {
                System.arraycopy(cMatrix.array[i5 - i3], 0, this.array[i5 - 1], i - 1, (i2 - i) + 1);
                i5++;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException("Submatrix indices");
            }
        }
    }

    public CMatrix times(double d) {
        CMatrix cMatrix = new CMatrix(this);
        cMatrix.timesSelf(d);
        return cMatrix;
    }

    public CMatrix times(CMatrix cMatrix) {
        if (cMatrix.row != this.col) {
            throw new IllegalArgumentException("CMatrix inner dimensions must agree.");
        }
        CMatrix cMatrix2 = new CMatrix(this.row, cMatrix.col);
        for (int i = 0; i < cMatrix.col; i++) {
            double[] dArr = cMatrix.array[i];
            for (int i2 = 0; i2 < this.row; i2++) {
                double d = 0.0d;
                for (int i3 = 0; i3 < this.col; i3++) {
                    d += this.array[i3][i2] * dArr[i3];
                }
                cMatrix2.array[i][i2] = d;
            }
        }
        return cMatrix2;
    }

    public CVector times(CVector cVector) {
        if (cVector.Rows() != this.col) {
            throw new IllegalArgumentException("CMatrix-Vector inner dimensions must agree.");
        }
        CVector cVector2 = new CVector(this.row);
        double[][] array = cVector2.getArray();
        for (int i = 0; i < this.row; i++) {
            double d = 0.0d;
            for (int i2 = 0; i2 < this.col; i2++) {
                d += this.array[i2][i] * cVector.array[0][i2];
            }
            array[0][i] = d;
        }
        return cVector2;
    }

    public void timesSelf(double d) {
        for (int i = 0; i < this.row; i++) {
            for (int i2 = 0; i2 < this.col; i2++) {
                double[] dArr = this.array[i2];
                dArr[i] = dArr[i] * d;
            }
        }
    }

    public String toString() {
        String str = new String();
        for (int i = 0; i < this.row; i++) {
            Object obj = str;
            for (int i2 = 0; i2 < this.col; i2++) {
                obj = new StringBuilder(String.valueOf(obj)).append(String.format(Locale.US, "%.3f ", new Object[]{Double.valueOf(this.array[i2][i])})).toString();
            }
            str = new StringBuilder(String.valueOf(obj)).append("\n").toString();
        }
        return str;
    }
}
