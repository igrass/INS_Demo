package opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public final class newDirectFloatBuffer {
    ByteBuffer vbb;

    public newDirectFloatBuffer(int i) {
        this.vbb = ByteBuffer.allocateDirect(i * 4);
        this.vbb.order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer makeFloatBuffer(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        asFloatBuffer.put(fArr);
        asFloatBuffer.position(0);
        return asFloatBuffer;
    }

    public FloatBuffer getBuffer() {
        return this.vbb.asFloatBuffer();
    }
}
