package cc.minieye.kalman.core;

import cc.minieye.kalman.maths.CVector;

public final class LowPassFilter {
    private double alpha;
    private CVector oldValueVec;
    private double oldvalue;

    public LowPassFilter(double d, int i) {
        this.alpha = d;
        this.oldValueVec = new CVector(i);
    }

    public double getValue(double d) {
        this.oldvalue = ((1.0d - this.alpha) * this.oldvalue) + (this.alpha * d);
        return this.oldvalue;
    }

    public CVector getValue(CVector cVector) {
        this.oldValueVec.copy(this.oldValueVec.times(1.0d - this.alpha).plus(cVector.times(this.alpha)));
        return this.oldValueVec;
    }
}
