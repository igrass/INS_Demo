package cc.minieye.instoolkit;

import java.util.LinkedList;

import cc.minieye.kalman.core.position.PositionFilter;
import cc.minieye.kalman.maths.CVector;

public final class MoveManager {
    public static final int TIME_INTERVAL = 10;
    public static final double MIN_SIZE = 1.0d;

    private CVector currMove;

    private static MoveManager mInstance;

    class AccInfo {
        float time;
        private CVector linearAcc;
        double size;

        AccInfo() {
        }
    }

    private LinkedList<AccInfo> accList;

    private MoveManager() {
        accList = new LinkedList();

        currMove = new CVector(3);
    }

    public static synchronized MoveManager getInstance() {
        if (mInstance == null)
            mInstance = new MoveManager();
        return mInstance;
    }

    public void pushData(PositionFilter positionFilter) {
        float timeStamp = (float) positionFilter.getTimeStamp();
        CVector linearAcceleration = new CVector(positionFilter.getMeasureLinearAcc());
        linearAcceleration.setElement(2, 0d);

        AccInfo accInfo = new AccInfo();
        accInfo.time = timeStamp;
        accInfo.linearAcc = linearAcceleration;
        accInfo.size = linearAcceleration.norm();
        if (accInfo.size < MIN_SIZE)
            accInfo.size = 0;

        addAccInfo(accInfo);
    }

    private void addAccInfo(AccInfo info) {
        while (!accList.isEmpty()) {
            if (accList.getFirst().time + TIME_INTERVAL >= info.time)
                break;

            accList.removeFirst();
        }

        accList.add(info);

        updateMove();
    }

    private void updateMove() {
        CVector move = new CVector(3);
        for (AccInfo info : accList) {
            if (info.size > 0d) {
                if (move == null) {
                    move = new CVector(info.linearAcc);
                } else {
                    double dot = move.dot(info.linearAcc);
                    if (dot >= 0d) {
                        move.copy(move.plus(info.linearAcc));
                    } else {
                        move.copy(move.minus(info.linearAcc));
                    }
                }
            }
        }

        currMove.copy(move);
    }

    public CVector getMove() {
        return currMove;
    }

    public double getMovePitch() {
        double pitch = 0.0d;
        if (currMove.norm() < MIN_SIZE)
            return pitch;
        pitch = Math.atan2(currMove.getElement(3), currMove.getElement(1));

        return pitch;
    }
}
