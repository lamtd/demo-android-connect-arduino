package com.boonygroup.arduinocontrol;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boonygroup.bnhelper.DeviceHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    EditText textOut;
    TextView textIn;

    String txtData_Wifi_Configuration;
    String txtData_Content;
    String txtData_Server_Configuration;

    String outPut;
    String DeviceIP;
    Socket socket = null;

    EditText txtDeviceIP;
    EditText txtSSID;
    EditText txtPassword;

    EditText txtLine1;
    EditText txtLine2;
    EditText txtLine3;
    EditText txtLine4;

    EditText txtServer;

    boolean Is_Send_Wifi_Config = false;
    boolean Is_Send_Default_Data  = false;
    boolean Is_Send_Server_Data = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.btnSend);
        textOut = (EditText)findViewById(R.id.txtData);
        textIn = (TextView)findViewById(R.id.txtTextIn);
        txtDeviceIP = (EditText)findViewById(R.id.txtDeviceIP);
        txtSSID =  (EditText)findViewById(R.id.txtSSID);
        txtPassword =  (EditText)findViewById(R.id.txtPassword);

        txtLine1 =  (EditText)findViewById(R.id.txtLine1);
        txtLine2 =  (EditText)findViewById(R.id.txtLine2);
        txtLine3 =  (EditText)findViewById(R.id.txtLine3);
        txtLine4 =  (EditText)findViewById(R.id.txtLine4);

        txtServer =  (EditText)findViewById(R.id.txtServer);

        String IPaddress = DeviceHelper.getIPAddress(true);
        if (!IPaddress.isEmpty())
        {
            String[] separated = IPaddress.split("\\.");
            String host= separated[0]+"."+ separated[1]+ "." + separated[2] + ".";
            txtDeviceIP.setText(host);
        }

        buttonSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Get_Data_From_Input();
                DeviceIP = txtDeviceIP.getText().toString();
                if (Is_Send_Wifi_Config)
                {
                    Send_Config_Wifi_Command_To_Arduino myAsyncTask=new Send_Config_Wifi_Command_To_Arduino();
                    // TODO Auto-generated method stub
                    myAsyncTask.execute();
                }
                if (Is_Send_Default_Data)
                {
                    Send_Data_Command_To_Arduino myAsyncTask= new Send_Data_Command_To_Arduino();
                    // TODO Auto-generated method stub
                    myAsyncTask.execute();
                }
                if (Is_Send_Server_Data)
                {
                    Send_Server_Config_Command_To_Arduino myAsyncTask = new Send_Server_Config_Command_To_Arduino();
                    myAsyncTask.execute();
                }
                //sendData();

            }
        });

        Button buttonAllUpdate = (Button)findViewById(R.id.btnAllUpdate);
        buttonAllUpdate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Get_Data_From_Input();
               // DeviceIP = txtDeviceIP.getText().toString();
                //new GetVideoLink().execute();
                CheckAllNetwork myAsyncTask=new CheckAllNetwork();
                // TODO Auto-generated method stub
                myAsyncTask.execute();
                //sendData();

            }
        });
    }
    private void Get_Data_From_Input()
    {

        if (txtSSID.getText().toString().length() > 0
                || txtPassword.getText().toString().length() > 0)
        {
            Is_Send_Wifi_Config = true;
            txtData_Wifi_Configuration = "swf"+txtSSID.getText().toString() + "<br>" + txtPassword.getText().toString();
        }
        else
        {
            Is_Send_Wifi_Config = false;
        }
        if (txtLine1.getText().toString().length() > 0
                || txtLine2.getText().toString().length() > 0
                || txtLine3.getText().toString().length() > 0
                || txtLine4.getText().toString().length() > 0   )
        {
            Is_Send_Default_Data = true;
            txtData_Content = "sdt";
            txtData_Content += txtLine1.getText().toString();
            txtData_Content += "<br>" + txtLine2.getText().toString();
            txtData_Content += "<br>" + txtLine3.getText().toString();
            txtData_Content += "<br>" + txtLine4.getText().toString();
        }
        else
        {
            Is_Send_Default_Data = false;
        }
        if (txtServer.getText().toString().length() > 0)
        {
            Is_Send_Server_Data  = true;
            txtData_Server_Configuration = "ssv" + txtServer.getText().toString();
        }
        else
        {
            Is_Send_Server_Data = false;
        }

        //new GetVideoLink().execute();
    }
    public class Send_Config_Wifi_Command_To_Arduino extends AsyncTask<Void, Void, Void> {
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                if (socket!= null && !socket.isClosed())
                    socket.close();

                socket = new Socket(DeviceIP, 9876);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String cl = txtData_Wifi_Configuration + String.valueOf('\n');

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
    public class Send_Data_Command_To_Arduino extends AsyncTask<Void, Void, Void> {
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                if (socket!= null && !socket.isClosed())
                    socket.close();

                socket = new Socket(DeviceIP, 9876);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String cl = txtData_Content + String.valueOf('\n');

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
    public class Send_Server_Config_Command_To_Arduino extends AsyncTask<Void, Void, Void> {
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                if (socket!= null && !socket.isClosed())
                    socket.close();

                socket = new Socket(DeviceIP, 9876);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String cl =  txtData_Server_Configuration + String.valueOf('\n');

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
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            textIn.setText(outPut);
        }
    }
    public class CheckAllNetwork extends AsyncTask<Void, String, Void> {
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        int count= 0;
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            int timeout=1000;
            for (int i=1;i<255;i++){
                String IPaddress = DeviceHelper.getIPAddress(true);
                if (IPaddress.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Lỗi ! Không có kết nối mạng!",
                     Toast.LENGTH_LONG).show();
                    return null;
                }
                String[] separated = IPaddress.split("\\.");
                String host= separated[0]+"."+ separated[1]+ "." + separated[2] + "." + i;
                try {
                    if (InetAddress.getByName(host).isReachable(100)){
                        publishProgress(host);
                        if (Is_Send_Wifi_Config)
                        {
                            try {
                                if (socket!= null && !socket.isClosed())
                                    socket.close();

                                socket = new Socket(host, 9876);
                                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                String cl = txtData_Wifi_Configuration + String.valueOf('\n');
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
                        }
                        if (Is_Send_Default_Data)
                        {
                            try {
                                if (socket!= null && !socket.isClosed())
                                    socket.close();

                                socket = new Socket(host, 9876);
                                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                String cl = txtData_Content + String.valueOf('\n');

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
                        }
                        if (Is_Send_Server_Data)
                        {
                            try {
                                if (socket!= null && !socket.isClosed())
                                    socket.close();
                                socket = new Socket(host, 9876);
                                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                String cl =  txtData_Server_Configuration + String.valueOf('\n');

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

                        }
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            textIn.setText("Đã gửi tới : " + values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            textIn.setText("Cấu hình thành công " + count + " thiết bị");
        }
    }
}
