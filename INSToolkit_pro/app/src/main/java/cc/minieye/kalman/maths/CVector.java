package cc.minieye.kalman.maths;

public class CVector extends CMatrix {
    public CVector(int i) {
        super(i, 1);
    }

    public CVector(CVector cVector) {
        super(cVector.row, 1);
        copy(cVector);
    }

    public CVector(double[] dArr) {
        super(dArr.length, 1);
        System.arraycopy(dArr, 0, this.array[0], 0, dArr.length);
    }

    public CVector(float[] fArr) {
        super(fArr.length, 1);
        int length = fArr.length;
        for (int i = 0; i < length; i++) {
            this.array[0][i] = (double) fArr[i];
        }
    }

    public CMatrix Skew3() {
        CMatrix cMatrix = new CMatrix(3, 3);
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        cMatrix.Set(1, 2, -d3);
        cMatrix.Set(1, 3, d2);
        cMatrix.Set(2, 1, d3);
        cMatrix.Set(2, 3, -d);
        cMatrix.Set(3, 1, d2);
        cMatrix.Set(3, 2, d);
        return cMatrix;
    }

    public CVector cat(CVector cVector) {
        int i;
        CVector cVector2 = new CVector(this.row + cVector.row);
        double[][] array = cVector2.getArray();
        for (i = 0; i < this.row; i++) {
            array[0][i] = this.array[0][i];
        }
        for (i = 0; i < cVector.row; i++) {
            array[0][this.row + i] = cVector.array[0][i];
        }
        return cVector2;
    }

    public void copy(int i, CVector cVector) {
        System.arraycopy(cVector.array[0], 0, this.array[0], i - 1, cVector.row);
    }

    public void copy(CVector cVector) {
        System.arraycopy(cVector.array[0], 0, this.array[0], 0, cVector.row);
    }

    public void copy(CVector cVector, int[] iArr) {
        int i = cVector.row;
        for (int i2 = 0; i2 < i; i2++) {
            this.array[0][i2] = cVector.array[0][iArr[i2]];
        }
    }

    public void copy(double[] dArr) {
        System.arraycopy(dArr, 0, this.array[0], 0, dArr.length);
    }

    public CVector cross(CVector cVector) {
        if (Rows() != 3) {
            throw new ArrayIndexOutOfBoundsException("Bad size for vector in cross product.");
        }
        return new CVector(new double[]{(this.array[0][1] * cVector.array[0][2]) - (this.array[0][2] * cVector.array[0][1]), (this.array[0][2] * cVector.array[0][0]) - (this.array[0][0] * cVector.array[0][2]), (this.array[0][0] * cVector.array[0][1]) - (this.array[0][1] * cVector.array[0][0])});
    }

    public CMatrix crossProductDiff(CVector cVector, int i) {
        if (Rows() != 3) {
            throw new ArrayIndexOutOfBoundsException("Bad size for vector in cross product.");
        } else if (i > 2) {
            throw new ArrayIndexOutOfBoundsException("Bad argument for crossProductDiff.");
        } else {
            CMatrix cMatrix = new CMatrix(3, 3);
            if (i == 1) {
                cMatrix.Set(1, 2, cVector.array[0][1]);
                cMatrix.Set(1, 3, -cVector.array[0][1]);
                cMatrix.Set(2, 1, -cVector.array[0][2]);
                cMatrix.Set(2, 3, cVector.array[0][0]);
                cMatrix.Set(3, 1, cVector.array[0][1]);
                cMatrix.Set(3, 2, -cVector.array[0][0]);
            } else {
                cMatrix.Set(1, 2, -this.array[0][2]);
                cMatrix.Set(1, 3, this.array[0][1]);
                cMatrix.Set(2, 1, this.array[0][2]);
                cMatrix.Set(2, 3, -this.array[0][0]);
                cMatrix.Set(3, 1, -this.array[0][1]);
                cMatrix.Set(3, 2, this.array[0][0]);
            }
            return cMatrix;
        }
    }

    public CMatrix diag() {
        CMatrix cMatrix = new CMatrix(this.row, this.row);
        cMatrix.setDiagonal(this.array[0]);
        return cMatrix;
    }

    public double dot(CVector cVector) {
        double d = 0.0d;
        for (int i = 0; i < this.row; i++) {
            d += this.array[0][i] * cVector.array[0][i];
        }
        return d;
    }

    public CVector dotProductDiff(CVector cVector, int i) {
        if (i > 2) {
            throw new ArrayIndexOutOfBoundsException("Bad argument for dotProductDiff.");
        }
        CVector cVector2 = new CVector(3);
        if (i == 1) {
            cVector2.setElement(1, cVector.array[0][0]);
            cVector2.setElement(2, cVector.array[0][1]);
            cVector2.setElement(3, cVector.array[0][2]);
        } else {
            cVector2.setElement(1, this.array[0][0]);
            cVector2.setElement(2, this.array[0][1]);
            cVector2.setElement(3, this.array[0][2]);
        }
        return cVector2;
    }

    public double getElement(int i) {
        if (Rows() >= i && i >= 1) {
            return this.array[0][i - 1];
        }
        throw new ArrayIndexOutOfBoundsException("Bad indices.");
    }

    public CVector getSubVector(int i, int i2) {
        if (Rows() < i || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        } else if ((i + i2) - 1 > Rows()) {
            throw new ArrayIndexOutOfBoundsException("Bad length.");
        } else {
            CVector cVector = new CVector(i2);
            System.arraycopy(this.array[0], i - 1, cVector.array[0], 0, i2);
            return cVector;
        }
    }

    public CVector minus(CVector cVector) {
        CVector cVector2 = new CVector(this.row);
        double[][] array = cVector2.getArray();
        for (int i = 0; i < this.row; i++) {
            array[0][i] = this.array[0][i] - cVector.array[0][i];
        }
        return cVector2;
    }

    public double norm() {
        double d = 0.0d;
        for (int i = 0; i < Rows(); i++) {
            d += this.array[0][i] * this.array[0][i];
        }
        return Math.sqrt(d);
    }

    public CVector plus(CVector cVector) {
        CVector cVector2 = new CVector(this.row);
        double[][] array = cVector2.getArray();
        checkMatrixDimensions(cVector);
        for (int i = 0; i < this.row; i++) {
            array[0][i] = this.array[0][i] + cVector.array[0][i];
        }
        return cVector2;
    }

    public void setElement(int i, double d) {
        if (Rows() < i || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        }
        this.array[0][i - 1] = d;
    }

    public void setSubVector(int i, int i2, CVector cVector) {
        if (Rows() < i || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        }
        System.arraycopy(cVector.array[0], 0, this.array[0], i - 1, i2);
    }

    public void setSubVector(int i, int i2, double[] dArr) {
        if (Rows() < i || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        }
        System.arraycopy(dArr, 0, this.array[0], i - 1, i2);
    }

    public void setSubVectorIdx(int i, int i2, CVector cVector, int i3) {
        if (Rows() < i || i < 1) {
            throw new ArrayIndexOutOfBoundsException("Bad indices.");
        } else if (Rows() < (i + i2) - 1 || cVector.Rows() < (i3 + i2) - 1) {
            throw new ArrayIndexOutOfBoundsException("Bad length.");
        } else {
            System.arraycopy(cVector.array[0], i3 - 1, this.array[0], i - 1, i2);
        }
    }

    public CVector times(double d) {
        CVector cVector = new CVector(this);
        cVector.timesSelf(d);
        return cVector;
    }

    public CVector times(CVector cVector) {
        CVector cVector2 = new CVector(this.row);
        double[][] array = cVector2.getArray();
        for (int i = 0; i < this.row; i++) {
            array[0][i] = this.array[0][i] * cVector.array[0][i];
        }
        return cVector2;
    }

    public CMatrix timesMatrix(CVector cVector) {
        int i = this.row;
        CMatrix cMatrix = new CMatrix(this.row, i);
        double[][] array = cMatrix.getArray();
        for (int i2 = 0; i2 < i; i2++) {
            double d = cVector.array[0][i2];
            for (int i3 = 0; i3 < this.row; i3++) {
                array[i2][i3] = this.array[0][i3] * d;
            }
        }
        return cMatrix;
    }

    public void timesSelf(double d) {
        for (int i = 0; i < this.row; i++) {
            double[] dArr = this.array[0];
            dArr[i] = dArr[i] * d;
        }
    }
}
