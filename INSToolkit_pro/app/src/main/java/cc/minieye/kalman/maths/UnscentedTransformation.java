package cc.minieye.kalman.maths;

public class UnscentedTransformation {
    int N;
    double alpha;
    double beta;
    double kappa;

    public UnscentedTransformation() {
        this.kappa = 0.0d;
        this.alpha = 0.001d;
        this.beta = 2.0d;
    }

    public void CalculateSigmaPoints(CVector cVector, CMatrix cMatrix, int i, SigmaPoint[] sigmaPointArr) {
        this.N = i;
        sigmaPointArr[0] = new SigmaPoint(cVector);
        CMatrix times = cMatrix.times((((this.alpha * this.alpha) * (((double) i) + this.kappa)) - ((double) i)) + ((double) i));
        CMatrix cMatrix2 = new CMatrix(times.Rows(), times.Cols());
        times.Cholesky(cMatrix2);
        for (int i2 = 1; i2 <= i; i2++) {
            sigmaPointArr[i2].copy(cVector.plus(cMatrix2.GetColumn(i2)));
            sigmaPointArr[i2 + i].copy(cVector.minus(cMatrix2.GetColumn(i2)));
        }
    }

    public void CalculateSigmaWeight(int i, double[] dArr, double[] dArr2) {
        double d = ((this.alpha * this.alpha) * (((double) i) + this.kappa)) - ((double) i);
        dArr2[0] = d / (((double) i) + d);
        dArr[0] = (d / (((double) i) + d)) + ((1.0d - (this.alpha * this.alpha)) + this.beta);
        for (int i2 = 1; i2 <= i; i2++) {
            dArr[i2] = 1.0d / (2.0d * (((double) i) + d));
            dArr2[i2] = dArr[i2];
            dArr[i2 + i] = dArr[i2];
            dArr2[i2 + i] = dArr[i2];
        }
    }

    public CMatrix getCovariance(double[] dArr, SigmaPoint[] sigmaPointArr, CVector cVector, SigmaPoint[] sigmaPointArr2, CVector cVector2) {
        int i = 0;
        if (sigmaPointArr2.length != sigmaPointArr.length) {
            throw new IllegalArgumentException("Not enough Sigma Point.");
        } else if (sigmaPointArr2.length != dArr.length) {
            throw new IllegalArgumentException("Number of weights should be equal to the number of Sigma Point.");
        } else {
            CMatrix cMatrix = new CMatrix(sigmaPointArr[0].Rows(), sigmaPointArr2[0].Rows());
            int length = dArr.length;
            while (i < length) {
                cMatrix.plusSelf(sigmaPointArr[i].minus(cVector).times(sigmaPointArr2[i].minus(cVector2).Transpose()).times(dArr[i]));
                i++;
            }
            return cMatrix;
        }
    }

    public CMatrix getCovariance(SigmaPoint[] sigmaPointArr, CVector cVector, double[] dArr) {
        return getCovariance(dArr, sigmaPointArr, cVector, sigmaPointArr, cVector);
    }

    public SigmaPoint getMean(SigmaPoint[] sigmaPointArr, double[] dArr) {
        int i = 0;
        if (sigmaPointArr.length != dArr.length) {
            throw new IllegalArgumentException("Number of weights should be equal to the number of Sigma Point.");
        }
        SigmaPoint sigmaPoint = new SigmaPoint(sigmaPointArr[0].Rows());
        int length = dArr.length;
        while (i < length) {
            sigmaPoint.plusSelf(sigmaPointArr[i].times(dArr[i]));
            i++;
        }
        return sigmaPoint;
    }

    public CVector getState(double[] dArr, SigmaPoint[] sigmaPointArr) {
        int i = 0;
        if (sigmaPointArr.length != dArr.length) {
            throw new IllegalArgumentException("Number of weights should be equal to the number of Sigma Point.");
        }
        int length = dArr.length;
        CVector sigmaPoint = new SigmaPoint(sigmaPointArr[0].Rows());
        while (i < length) {
            sigmaPoint.plusSelf(sigmaPointArr[i].times(dArr[i]));
            i++;
        }
        return sigmaPoint;
    }
}
