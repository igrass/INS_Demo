package opengl;

import javax.microedition.khronos.opengles.GL10;

public class MatrixGrabber {
    public float[] mModelView;
    public float[] mProjection;

    public MatrixGrabber() {
        this.mModelView = new float[16];
        this.mProjection = new float[16];
    }

    private void getMatrix(GL10 gl10, int i, float[] fArr) {
        MatrixTrackingGL matrixTrackingGL = (MatrixTrackingGL) gl10;
        matrixTrackingGL.glMatrixMode(i);
        matrixTrackingGL.getMatrix(fArr, 0);
    }

    public void getCurrentModelView(GL10 gl10) {
        getMatrix(gl10, 5888, this.mModelView);
    }

    public void getCurrentProjection(GL10 gl10) {
        getMatrix(gl10, 5889, this.mProjection);
    }

    public void getCurrentState(GL10 gl10) {
        getCurrentProjection(gl10);
        getCurrentModelView(gl10);
    }
}
