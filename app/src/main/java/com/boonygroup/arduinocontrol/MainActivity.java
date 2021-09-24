package com.boonygroup.arduinocontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    EditText textOut;
    TextView textIn;

    String txtData;
    String outPut;
    Socket socket = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.btnSend);
        textOut = (EditText)findViewById(R.id.txtData);
        textIn = (TextView)findViewById(R.id.txtTextIn);
        buttonSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtData = textOut.getText().toString();
               //new GetVideoLink().execute();
                SendCommandToArduino myAsyncTask=new SendCommandToArduino();
                // TODO Auto-generated method stub
                myAsyncTask.execute();
                //sendData();

            }
        });


    }

    public class SendCommandToArduino extends AsyncTask<Void, Void, Void> {
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                if (socket!= null && !socket.isClosed())
                    socket.close();
                socket = new Socket("192.168.1.168", 9876);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String cl = txtData + String.valueOf('\n');

                dataOutputStream.writeBytes(cl);
                dataOutputStream.flush();
                dataInputStream = new DataInputStream(socket.getInputStream());

                outPut = dataInputStream.readLine();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch blockch0
                e.printStackTrace();
            }
            /*finally{
                if (socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null){
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }*/
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            textIn.setText(outPut);
        }
    }
}
