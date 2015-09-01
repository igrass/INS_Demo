package cc.minieye.objects;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UDPServer {
    private InetAddress local;
    private DatagramPacket mPacket;
    private DatagramSocket mSocket;
    private String server_adress;
    private int server_port;

    private class AsyncListener extends AsyncTask<ByteBuffer, Void, Integer> {
        private AsyncListener() {
        }

        public Integer doInBackground(ByteBuffer... byteBufferArr) {
            byte[] array = byteBufferArr[0].array();
            UDPServer.this.mPacket = new DatagramPacket(array, array.length, UDPServer.this.local, UDPServer.this.server_port);
            try {
                UDPServer.this.mSocket.send(UDPServer.this.mPacket);
                return Integer.valueOf(byteBufferArr[0].position());
            } catch (IOException e) {
                return Integer.valueOf(0);
            }
        }

        public void onPostExecute(Integer num) {
        }
    }

    public UDPServer(int i, String str) {
        this.mPacket = null;
        this.mSocket = null;
        this.server_port = i;
        this.server_adress = str;
    }

    public boolean close() {
        this.mSocket.close();
        return true;
    }

    public boolean isOpen() {
        return (this.mSocket == null || this.mSocket.isClosed()) ? false : true;
    }

    public boolean open() {
        try {
            this.mSocket = new DatagramSocket();
            try {
                this.local = InetAddress.getByName(this.server_adress);
                return true;
            } catch (UnknownHostException e) {
                return false;
            }
        } catch (SocketException e2) {
            return false;
        }
    }

    public boolean send(float[] fArr) {
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.LITTLE_ENDIAN);
        for (float putFloat : fArr) {
            allocateDirect.putFloat(putFloat);
        }
        new AsyncListener().execute(new ByteBuffer[]{allocateDirect});
        return true;
    }

    public void set(int i, String str) {
        this.server_port = i;
        this.server_adress = str;
    }
}
