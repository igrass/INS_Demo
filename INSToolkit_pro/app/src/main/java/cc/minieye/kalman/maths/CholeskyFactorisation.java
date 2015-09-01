package cc.minieye.kalman.maths;

public class CholeskyFactorisation {
    static boolean spdmatrixcholesky(CMatrix cMatrix, int i, boolean z, CMatrix cMatrix2) throws MatrixException {
        int i2;
        double d;
        int i3;
        double Get;
        double sqrt;
        int i4;
        if (z) {
            for (i2 = 1; i2 <= i; i2++) {
                d = 0.0d;
                for (i3 = 1; i3 <= i2; i3++) {
                    d += cMatrix.Get(i3, i2) * cMatrix.Get(i3, i2);
                }
                Get = cMatrix.Get(i2, i2) - d;
                if (Get <= 0.0d) {
                    throw new MatrixException("Matrix is not positive definite");
                }
                sqrt = Math.sqrt(Get);
                cMatrix2.Set(i2, i2, sqrt);
                if (i2 < i) {
                    for (i4 = i2 + 1; i4 <= i - 1; i4++) {
                        d = 0.0d;
                        for (i3 = 1; i3 <= i2; i3++) {
                            d += cMatrix.Get(i3, i4) * cMatrix.Get(i3, i2);
                        }
                        cMatrix2.Set(i2, i4, cMatrix.Get(i2, i4) - d);
                    }
                    d = 1.0d / sqrt;
                    for (i3 = i2 + 1; i3 <= i - 1; i3++) {
                        cMatrix2.Set(i2, i3, cMatrix.Get(i2, i3) * d);
                    }
                }
            }
        } else {
            for (i2 = 1; i2 <= i - 1; i2++) {
                d = 0.0d;
                for (i3 = 1; i3 <= i2; i3++) {
                    d += cMatrix.Get(i2, i3) * cMatrix.Get(i2, i3);
                }
                Get = cMatrix.Get(i2, i2) - d;
                if (Get <= 0.0d) {
                    throw new MatrixException("Matrix is not positive definite");
                }
                sqrt = Math.sqrt(Get);
                cMatrix2.Set(i2, i2, sqrt);
                if (i2 < i - 1) {
                    for (i4 = i2 + 1; i4 <= i - 1; i4++) {
                        d = 0.0d;
                        for (i3 = 0; i3 <= i2 - 1; i3++) {
                            d += cMatrix.Get(i4, i3) * cMatrix.Get(i2, i3);
                        }
                        cMatrix2.Set(i4, i2, cMatrix.Get(i4, i2) - d);
                    }
                    d = 1.0d / sqrt;
                    for (i3 = i2 + 1; i3 <= i - 1; i3++) {
                        cMatrix2.Set(i3, i2, cMatrix.Get(i3, i2) * d);
                    }
                }
            }
        }
        return true;
    }
}
