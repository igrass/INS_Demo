package opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public final class newDirectIntBuffer {
    ByteBuffer vbb;

    public newDirectIntBuffer(int i) {
        this.vbb = ByteBuffer.allocateDirect(i * 4);
        this.vbb.order(ByteOrder.nativeOrder());
    }

    public IntBuffer getBuffer() {
        return this.vbb.asIntBuffer();
    }
}
