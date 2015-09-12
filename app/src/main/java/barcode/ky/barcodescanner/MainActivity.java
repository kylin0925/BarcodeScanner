package barcode.ky.barcodescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import barcode.ky.barcodescanner.CameraSurfacePreview;

public class MainActivity extends AppCompatActivity {
    String TAG = "Barcode";

    TextView txtBarcode;
    String oldbarcode="";
    Button btnConnect;
    Button btnDisconnect;
    Camera camera;
    CameraSurfacePreview cameraSurfacePreview;
    SharedPreferences sharedPreferences;
    Socket socket;
    String response = "";
    String dstAddress = "192.168.1.107";
    int dstPort = 9999;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "msg " + msg.obj + " " + oldbarcode);
            if(msg.arg1 == 123) {
                txtBarcode.setText(msg.obj.toString());
                {
                    Log.e(TAG, "send msg ");
                    sentBarcode(msg.obj.toString());
                    oldbarcode = msg.obj.toString();
                    cameraSurfacePreview.stopPreview();
                }
            }else if(msg.arg1 == 1 || msg.arg1 == 2){
                btnConnect.setText(msg.obj.toString());
            }

        }
    };

    void sendHandlerMsg(int id,String msg){
        Message message = new Message();
        message.arg1 = id;
        message.obj = msg;
        handler.sendMessage(message);
    }
    void sockConnect(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(dstAddress, dstPort);
                    sendHandlerMsg(1,"connect");
                }catch (Exception ex){
                    Log.e(TAG, "read " + ex.toString());
                    sendHandlerMsg(1,"connect failed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(MainActivity.this, "socket error ", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });

                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }
    void sockDisconnect(){
        if(socket !=null){
            try {
                Log.e(TAG,"socket close ");
                socket.close();
                sendHandlerMsg(2,"disconnect");
            }catch (Exception ex){
                Log.e(TAG,"socket close " + ex.toString());
                ex.printStackTrace();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraSurfacePreview = (CameraSurfacePreview)findViewById(R.id.surfaceView);
        cameraSurfacePreview.setHandler(handler);

        txtBarcode = (TextView)findViewById(R.id.txtBarcode);
        //sentBarcode("1212121aa");
        camera = cameraSurfacePreview.getCamera();
        cameraSurfacePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldbarcode = "";
                cameraSurfacePreview.startPreview();
            }
        });
        sharedPreferences = getSharedPreferences("IP",0);
        String ip = sharedPreferences.getString("IP","");
        TextView txtIP = (TextView)findViewById(R.id.txtIp);
        txtIP.setText(ip);

        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHandlerMsg(1,"connecting");
                sockConnect();
            }
        });

        btnDisconnect = (Button)findViewById(R.id.btnDisConnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket.isConnected())
                    sockDisconnect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,IpSetting.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void sentBarcode(final String barcode){
        if(socket == null || socket.isConnected() == false) {
            Log.e(TAG,"RETURN ");
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"socket client " + dstAddress + "  " + dstPort);
                try {
                    //socket = new Socket(dstAddress, dstPort);
                    ByteArrayOutputStream byteArrayOutputStream =
                            new ByteArrayOutputStream(1024);

                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();

                    Log.e(TAG,"read ");
                    /*while ((bytesRead = inputStream.read(buffer)) != -1){
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response += byteArrayOutputStream.toString("UTF-8");
                        Log.e("clicent","response " + response);
                    }*/
                    Log.e(TAG,"send barcode " + barcode);
                    OutputStream of = socket.getOutputStream();
                    of.write(barcode.getBytes());

                    //Log.e("clicent","response ->" + response);
                }catch (Exception ex){
                    Log.e(TAG, "read " + ex.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(MainActivity.this, "socket error ", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });

                    ex.printStackTrace();
                }
            }
        });
        thread.start();

    }
}
