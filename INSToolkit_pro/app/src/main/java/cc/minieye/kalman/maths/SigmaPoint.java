package cc.minieye.kalman.maths;

public class SigmaPoint extends CVector {
    public SigmaPoint(int i) {
        super(i);
    }

    public SigmaPoint(CVector cVector) {
        super(cVector.row);
        copy(cVector);
    }
}
