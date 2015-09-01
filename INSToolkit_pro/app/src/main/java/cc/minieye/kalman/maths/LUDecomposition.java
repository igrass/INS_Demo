package cc.minieye.kalman.maths;

import java.lang.reflect.Array;

public class LUDecomposition {
    private double[][] L;
    private double[][] LU;
    private double[][] U;
    private int mrow;
    private int ncol;
    private int[] piv;
    private int pivsign;

    public LUDecomposition(CMatrix cMatrix, boolean z) {
        int i;
        this.LU = cMatrix.getArrayCopy();
        this.mrow = cMatrix.Rows();
        this.ncol = cMatrix.Cols();
        this.L = (double[][]) Array.newInstance(Double.TYPE, new int[]{this.mrow, this.mrow});
        this.U = (double[][]) Array.newInstance(Double.TYPE, new int[]{this.mrow, this.mrow});
        this.piv = new int[this.mrow];
        for (i = 0; i < this.mrow; i++) {
            this.piv[i] = i;
        }
        this.pivsign = 1;
        for (i = 0; i < this.ncol; i++) {
            int i2;
            this.L[i][i] = 1.0d;
            for (i2 = i; i2 < this.mrow; i2++) {
                int i3;
                double d = 0.0d;
                double[] dArr = this.U[i2];
                for (i3 = 0; i3 < i; i3++) {
                    d += this.L[i3][i] * dArr[i3];
                }
                this.U[i2][i] = cMatrix.Get(i + 1, i2 + 1) - d;
            }
            for (i2 = i + 1; i2 < this.mrow; i2++) {
                double d = 0.0d;
                double[] dArr = this.U[i];
                for (int i3 = 0; i3 < i; i3++) {
                    d += this.L[i3][i2] * dArr[i3];
                }
                this.L[i][i2] = (cMatrix.Get(i2 + 1, i + 1) - d) / this.U[i][i];
            }
        }
    }

    public double det() {
        if (this.mrow != this.ncol) {
            throw new IllegalArgumentException("CMatrix must be square.");
        }
        double d = (double) this.pivsign;
        for (int i = 0; i < this.ncol; i++) {
            d *= this.LU[i][i];
        }
        return d;
    }

    public double[] getDoublePivot() {
        double[] obj = new double[this.mrow];
        System.arraycopy(this.piv, 0, obj, 0, this.mrow);
        return obj;
    }

    public CMatrix getL() {
        CMatrix cMatrix = new CMatrix(this.mrow, this.ncol);
        double[][] array = cMatrix.getArray();
        for (int i = 0; i < this.mrow; i++) {
            System.arraycopy(this.L[i], 0, array[i], 0, this.ncol);
        }
        return cMatrix;
    }

    public int[] getPivot() {
        int[] obj = new int[this.mrow];
        System.arraycopy(this.piv, 0, obj, 0, this.mrow);
        return obj;
    }

    public CMatrix getU() {
        CMatrix cMatrix = new CMatrix(this.ncol, this.ncol);
        double[][] array = cMatrix.getArray();
        for (int i = 0; i < this.mrow; i++) {
            System.arraycopy(this.U[i], 0, array[i], 0, this.ncol);
        }
        return cMatrix;
    }

    public boolean isNonsingular() {
        for (int i = 0; i < this.ncol; i++) {
            if (this.LU[i][i] == 0.0d) {
                return false;
            }
        }
        return true;
    }

    public void solve(CMatrix cMatrix, CMatrix cMatrix2) {
        if (cMatrix.Rows() != this.mrow) {
            throw new IllegalArgumentException("CMatrix row dimensions must agree.");
        } else if (isNonsingular()) {
            int i;
            double[] dArr;
            int i2;
            int i3;
            double[] dArr2;
            int Cols = cMatrix.Cols();
            cMatrix2.copyFrom(cMatrix.getMatrix(this.piv, 0, Cols - 1));
            double[][] array = cMatrix2.getArray();
            for (i = 0; i < this.ncol; i++) {
                dArr = this.L[i];
                for (i2 = i + 1; i2 < this.ncol; i2++) {
                    for (i3 = 0; i3 < Cols; i3++) {
                        dArr2 = array[i3];
                        dArr2[i2] = dArr2[i2] - (array[i3][i] * dArr[i2]);
                    }
                }
            }
            for (i3 = this.ncol - 1; i3 >= 0; i3--) {
                dArr = this.U[i3];
                for (i = 0; i < Cols; i++) {
                    double[] dArr3 = array[i];
                    dArr3[i3] = dArr3[i3] / dArr[i3];
                }
                for (i2 = 0; i2 < i3; i2++) {
                    for (i = 0; i < Cols; i++) {
                        dArr2 = array[i];
                        dArr2[i2] = dArr2[i2] - (array[i][i3] * dArr[i2]);
                    }
                }
            }
        } else {
            throw new RuntimeException("CMatrix is singular.");
        }
    }
}
