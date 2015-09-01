package cc.minieye.kalman.core;

import cc.minieye.kalman.maths.CMatrix;
import cc.minieye.kalman.maths.CVector;

public interface physics {
    void DynamicsNonLinear(CVector cVector, boolean z, CVector cVector2);

    void DynamicsTangent(CVector cVector, CMatrix cMatrix, CMatrix cMatrix2);

    void ObservationNonLinear(CVector cVector, boolean z, CVector cVector2);

    void ObservationTangent(CVector cVector, boolean z, CMatrix cMatrix);
}
