package com.nami.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 *  smartConfig 时辅助
 */
public class UDPSocketServer {
    private static final String TAG = "APP_UDPSocketServer";
    private final byte[] buffer;
    private DatagramPacket mReceivePacket;
    private DatagramSocket mServerSocket;
    private Context mContext;
    private WifiManager.MulticastLock mLock;
    private volatile boolean mIsClosed;

    /**
     * Constructor of UDP Socket Server
     *
     * @param port          the Socket Server port   18266
     * @param socketTimeout the socket read timeout
     * @param context       the context of the Application
     */
    public UDPSocketServer(int port, int socketTimeout, Context context) {
        this.mContext = context;
        this.buffer = new byte[64];
        this.mReceivePacket = new DatagramPacket(buffer, 64);
        try {
            this.mServerSocket = new DatagramSocket(null);
            this.mServerSocket.setReuseAddress(true);
            this.mServerSocket.bind(new InetSocketAddress(port));
            this.mServerSocket.setSoTimeout(socketTimeout);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        }
        this.mIsClosed = false;
        WifiManager manager = (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mLock = manager.createMulticastLock("test wifi");
        Log.d(TAG, "mServerSocket is created, socket read timeout: "
                + socketTimeout + ", port: " + port);
    }

    private synchronized void acquireLock() {
        if (mLock != null && !mLock.isHeld()) {
            mLock.acquire();
        }
    }

    private synchronized void releaseLock() {
        if (mLock != null && mLock.isHeld()) {
            try {
                mLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        }
    }

    /**
     * Set the socket timeout in milliseconds
     *
     * @param timeout the timeout in milliseconds or 0 for no timeout.
     * @return true whether the timeout is set suc
     */
    public boolean setSoTimeout(int timeout) {
        try {
            this.mServerSocket.setSoTimeout(timeout);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    *  自定义的函数，接受任意数据
     */
    public String receiveString() {
        Log.d(TAG, "receiveString() entrance");
        try {
            acquireLock();
            mServerSocket.receive(mReceivePacket);
            byte[] recDatas = Arrays.copyOf(mReceivePacket.getData(), mReceivePacket.getLength());
            String result = new String(recDatas);
            Log.e(TAG, "received len : " + result.length());
            Log.e(TAG, "receiveSpecLenBytes: " + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void interrupt() {
        Log.i(TAG, "USPSocketServer is interrupt");
        close();
    }

    public synchronized void close() {
        if (!this.mIsClosed) {
            Log.e(TAG, "mServerSocket is closed");
            mServerSocket.close();
            releaseLock();
            this.mIsClosed = true;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
