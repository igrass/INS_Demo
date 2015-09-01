package cc.minieye.kalman.maths;

public final class CQuaternion extends CVector {
    public CQuaternion() {
        super(4);
        setValues(1.0d, 0.0d, 0.0d, 0.0d);
    }

    public CQuaternion(double d, double d2, double d3) {
        super(4);
        double cos = Math.cos(d / 2.0d);
        double sin = Math.sin((-d) / 2.0d);
        double cos2 = Math.cos(d2 / 2.0d);
        double sin2 = Math.sin((-d2) / 2.0d);
        double cos3 = Math.cos(d3 / 2.0d);
        double sin3 = Math.sin((-d3) / 2.0d);
        this.array[0][0] = ((cos * cos2) * cos3) + ((sin * sin2) * sin3);
        this.array[0][1] = ((sin * cos2) * cos3) - ((cos * sin2) * sin3);
        this.array[0][2] = ((cos * sin2) * cos3) + ((sin * cos2) * sin3);
        this.array[0][3] = ((cos * cos2) * sin3) - ((sin * sin2) * cos3);
    }

    public CQuaternion(double d, double d2, double d3, double d4) {
        super(4);
        setValues(d, d2, d3, d4);
    }

    public CQuaternion(CVector cVector) {
        super(4);
        setValues(cVector.getElement(1), cVector.getElement(2), cVector.getElement(3), cVector.getElement(4));
    }

    public CQuaternion(CVector cVector, double d) {
        super(4);
        this.array[0][0] = Math.cos(d / 2.0d);
        this.array[0][1] = cVector.getElement(1) * Math.sin(d / 2.0d);
        this.array[0][2] = cVector.getElement(2) * Math.sin(d / 2.0d);
        this.array[0][3] = cVector.getElement(3) * Math.sin(d / 2.0d);
    }

    public CMatrix QuaternionMatrix(CVector cVector) {
        double element = cVector.getElement(1);
        double element2 = cVector.getElement(2);
        double element3 = cVector.getElement(3);
        CMatrix cMatrix = new CMatrix(4, 4);
        cMatrix.Set(1, 2, -element);
        cMatrix.Set(1, 3, -element2);
        cMatrix.Set(1, 4, -element3);
        cMatrix.Set(2, 1, element);
        cMatrix.Set(2, 3, element3);
        cMatrix.Set(2, 4, -element2);
        cMatrix.Set(3, 1, element2);
        cMatrix.Set(3, 2, -element3);
        cMatrix.Set(3, 4, element);
        cMatrix.Set(4, 1, element3);
        cMatrix.Set(4, 2, element2);
        cMatrix.Set(4, 3, -element);
        return cMatrix.times(0.5d);
    }

    public CMatrix RotationMatrix() {
        CMatrix cMatrix = new CMatrix(3, 3);
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        cMatrix.Set(1, 1, (((2.0d * d) * d) + ((2.0d * d2) * d2)) - 1.0d);
        cMatrix.Set(1, 2, 2.0d * ((d2 * d3) + (d * d4)));
        cMatrix.Set(1, 3, 2.0d * ((d2 * d4) - (d * d3)));
        cMatrix.Set(2, 1, 2.0d * ((d2 * d3) - (d * d4)));
        cMatrix.Set(2, 2, (((2.0d * d) * d) + ((2.0d * d3) * d3)) - 1.0d);
        cMatrix.Set(2, 3, 2.0d * ((d3 * d4) + (d * d2)));
        cMatrix.Set(3, 1, 2.0d * ((d2 * d4) + (d * d3)));
        cMatrix.Set(3, 2, ((d3 * d4) - (d2 * d)) * 2.0d);
        cMatrix.Set(3, 3, ((d * (2.0d * d)) + ((2.0d * d4) * d4)) - 1.0d);
        return cMatrix;
    }

    public CMatrix RotationMatrixH() {
        CMatrix cMatrix = new CMatrix(3, 3);
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        cMatrix.Set(1, 1, (((d * d) + (d2 * d2)) - (d3 * d3)) - (d4 * d4));
        cMatrix.Set(1, 2, 2.0d * ((d2 * d3) + (d * d4)));
        cMatrix.Set(1, 3, 2.0d * ((d2 * d4) - (d * d3)));
        cMatrix.Set(2, 1, 2.0d * ((d2 * d3) - (d * d4)));
        cMatrix.Set(2, 2, (((d * d) - (d2 * d2)) + (d3 * d3)) - (d4 * d4));
        cMatrix.Set(2, 3, 2.0d * ((d3 * d4) + (d * d2)));
        cMatrix.Set(3, 1, 2.0d * ((d2 * d4) + (d * d3)));
        cMatrix.Set(3, 2, 2.0d * ((d3 * d4) - (d * d2)));
        cMatrix.Set(3, 3, (((d * d) - (d2 * d2)) - (d3 * d3)) + (d4 * d4));
        return cMatrix;
    }

    public CVector Vector() {
        CVector cVector = new CVector(4);
        System.arraycopy(this.array[0], 0, cVector.array[0], 0, 4);
        return cVector;
    }

    public CQuaternion copy() {
        return new CQuaternion(this.array[0][0], this.array[0][1], this.array[0][2], this.array[0][3]);
    }

    public CMatrix diffRotationMatrix(int i) {
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        double[][] dArr;
        if (i == 0) {
            dArr = new double[3][];
            dArr[0] = new double[]{2.0d * d, 2.0d * d4, -2.0d * d3};
            dArr[1] = new double[]{d4 * -2.0d, 2.0d * d, 2.0d * d2};
            dArr[2] = new double[]{d3 * 2.0d, d2 * -2.0d, d * 2.0d};
            return new CMatrix(dArr);
        } else if (i == 1) {
            dArr = new double[3][];
            dArr[0] = new double[]{2.0d * d2, 2.0d * d3, 2.0d * d4};
            dArr[1] = new double[]{d3 * 2.0d, -2.0d * d2, 2.0d * d};
            dArr[2] = new double[]{d4 * 2.0d, d * -2.0d, d2 * -2.0d};
            return new CMatrix(dArr);
        } else if (i == 2) {
            dArr = new double[3][];
            dArr[0] = new double[]{-2.0d * d3, 2.0d * d2, -2.0d * d};
            dArr[1] = new double[]{d2 * 2.0d, 2.0d * d3, 2.0d * d4};
            dArr[2] = new double[]{d * 2.0d, d4 * 2.0d, d3 * -2.0d};
            return new CMatrix(dArr);
        } else {
            dArr = new double[3][];
            dArr[0] = new double[]{-2.0d * d4, 2.0d * d, 2.0d * d2};
            dArr[1] = new double[]{d * -2.0d, -2.0d * d4, 2.0d * d3};
            dArr[2] = new double[]{d2 * 2.0d, 2.0d * d3, 2.0d * d4};
            return new CMatrix(dArr);
        }
    }

    public double getEuler1() {
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        return -Math.atan2(((d * d2) + (this.array[0][3] * d3)) * 2.0d, 1.0d - (((d2 * d2) + (d3 * d3)) * 2.0d));
    }

    public double getEuler2() {
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        return -Math.asin(((d * this.array[0][2]) - (d2 * this.array[0][3])) * 2.0d);
    }

    public double getEuler3() {
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        return -Math.atan2(((d * d4) + (d2 * d3)) * 2.0d, 1.0d - (((d3 * d3) + (d4 * d4)) * 2.0d));
    }

    public double getScalarPart() {
        return this.array[0][0];
    }

    public CVector getVectorPart() {
        return new CVector(new double[]{this.array[0][1], this.array[0][2], this.array[0][3]});
    }

    public CQuaternion inv() {
        CQuaternion cQuaternion = new CQuaternion();
        double norm = norm();
        cQuaternion.array[0][0] = this.array[0][0] / norm;
        cQuaternion.array[0][1] = (-this.array[0][1]) / norm;
        cQuaternion.array[0][2] = (-this.array[0][2]) / norm;
        cQuaternion.array[0][3] = (-this.array[0][3]) / norm;
        return cQuaternion;
    }

    public double norm() {
        return Math.sqrt(norm2());
    }

    public double norm2() {
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        return (((d * d) + (d2 * d2)) + (d3 * d3)) + (d4 * d4);
    }

    public void normalize() {
        timesSelf(1.0d / norm());
    }

    public void setValues(double d, double d2, double d3, double d4) {
        this.array[0][0] = d;
        this.array[0][1] = d2;
        this.array[0][2] = d3;
        this.array[0][3] = d4;
    }

    public CQuaternion times(double d) {
        CQuaternion cQuaternion = new CQuaternion();
        double[][] array = cQuaternion.getArray();
        for (int i = 0; i < 4; i++) {
            array[0][i] = this.array[0][i] * d;
        }
        return cQuaternion;
    }

    public CQuaternion times(CQuaternion cQuaternion) {
        CQuaternion cQuaternion2 = new CQuaternion();
        double d = this.array[0][0];
        double d2 = this.array[0][1];
        double d3 = this.array[0][2];
        double d4 = this.array[0][3];
        double[][] array = cQuaternion.getArray();
        double d5 = array[0][0];
        double d6 = array[0][1];
        double d7 = array[0][2];
        double d8 = array[0][3];
        array = cQuaternion2.getArray();
        array[0][0] = (d * d5) - (((d2 * d6) + (d3 * d7)) + (d4 * d8));
        array[0][1] = ((d * d6) + (d5 * d2)) + ((d3 * d8) - (d4 * d7));
        array[0][2] = ((d * d7) + (d5 * d3)) + ((d4 * d6) - (d2 * d8));
        array[0][3] = ((d * d8) + (d4 * d5)) + ((d2 * d7) - (d3 * d6));
        return cQuaternion2;
    }

    public void timesSelf(double d) {
        for (int i = 0; i < 4; i++) {
            double[] dArr = this.array[0];
            dArr[i] = dArr[i] * d;
        }
    }
}
