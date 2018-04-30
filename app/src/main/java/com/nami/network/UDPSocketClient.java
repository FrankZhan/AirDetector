package com.nami.network;

import android.util.Log;

import com.espressif.iot.esptouch.task.__IEsptouchTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * this class is used to help send UDP data according to length
 * smartConfig 时辅助
 * @author afunx
 */
public class UDPSocketClient {

    private static final String TAG = "APP_UDPSocketClient";
    private DatagramSocket mSocket;
    private volatile boolean mIsStop;
    private volatile boolean mIsClosed;

    public UDPSocketClient() {
        try {
            this.mSocket = new DatagramSocket();
            this.mIsStop = false;
            this.mIsClosed = false;
        } catch (SocketException e) {
            if (__IEsptouchTask.DEBUG) {
                Log.e(TAG, "SocketException");
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void interrupt() {
        if (__IEsptouchTask.DEBUG) {
            Log.i(TAG, "USPSocketClient is interrupt");
        }
        this.mIsStop = true;
    }

    /**
     * close the UDP socket
     */
    public synchronized void close() {
        if (!this.mIsClosed) {
            this.mSocket.close();
            this.mIsClosed = true;
        }
    }

    /**
     * send the data by UDP
     * 自定义函数：发送字符串到特定位置
     *
     * @param data       the data to be sent
     * @param targetHostName      the count of the data
     * @param targetPort the port of target           7001
     * @param interval   the milliseconds to between each UDP sent
     */
    public void sendData(String data, String targetHostName, int targetPort, long interval){
        if ((data == null) || (data.length() <= 0)) {
            if (__IEsptouchTask.DEBUG) {
                Log.e(TAG, "sendStrData(): data == null or length <= 0");
            }
            return;
        }
        for (int i=0; !mIsStop && i < 2; i++) {
            try {
                Log.i(TAG, "data.length = " + data.length());
                DatagramPacket localDatagramPacket = new DatagramPacket(
                        data.getBytes(), data.length(),
                        InetAddress.getByName(targetHostName), targetPort);
                this.mSocket.send(localDatagramPacket);
            } catch (UnknownHostException e) {
                if (__IEsptouchTask.DEBUG) {
                    Log.e(TAG, "sendData(): UnknownHostException");
                }
                e.printStackTrace();
                mIsStop = true;
                break;
            } catch (IOException e) {
                if (__IEsptouchTask.DEBUG) {
                    Log.e(TAG, "sendData(): IOException, but just ignore it");
                }
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (__IEsptouchTask.DEBUG) {
                    Log.e(TAG, "sendData is Interrupted");
                }
                mIsStop = true;
                break;
            }
        }
        if (mIsStop) {
            close();
        }
    }
}
