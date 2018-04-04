package barcode.ky.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.Image;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    String TAG = "Barcode";

    TextView txtBarcode;
    String oldbarcode="";
    Button btnConnect;
    Button btnDisconnect;
    ImageView imageView;
    Camera camera;
    CameraSurfacePreview cameraSurfacePreview;
    SharedPreferences sharedPreferences;
    Socket socket;
    String response = "";
    String dstAddress = "192.168.1.107";
    int dstPort = 9999;
    RangeView rangeView;
    Bitmap bitmap;
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
                    sentBarcodeBrocast(msg.obj.toString());
                }
            }
            else if(msg.arg1 == 888){
                Bundle bundle = msg.getData();
                byte[] bytes = bundle.getByteArray("barcode_bitmap");
                Log.e(TAG,"888 " + bytes);
                if(bytes!=null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,null);
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.GONE);
                }

            }
            else if(msg.arg1 == 1 || msg.arg1 == 2){
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cameraSurfacePreview = (CameraSurfacePreview)findViewById(R.id.surfaceView);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(CameraSurfacePreview.width, CameraSurfacePreview.height);
        cameraSurfacePreview.setLayoutParams(params);
        cameraSurfacePreview.setHandler(handler);

        txtBarcode = (TextView)findViewById(R.id.txtBarcode);
        //sentBarcode("1212121aa");
        camera = cameraSurfacePreview.getCamera();
        cameraSurfacePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldbarcode = "";
                cameraSurfacePreview.startPreview();
                rangeView.setVisibility(View.GONE);
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

        Point point = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(point);
        Log.e(TAG, "size " + point);
        rangeView = (RangeView)findViewById(R.id.rangeView);
        cameraSurfacePreview.setRangeView(rangeView);
        imageView = (ImageView)findViewById(R.id.imgbarcode);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Log.e(TAG,"facing " + info.facing + " " + info.orientation);

        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();

        Log.e(TAG,"rotation " + rotation);
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
            Intent intent = new Intent(this,Setting.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void sentBarcodeBrocast(final String barcode){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try

                {
                    DatagramSocket socket = new DatagramSocket(5555, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);
                    byte[] bytes = barcode.getBytes();
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), 5555);
                    Log.e(TAG,"send to pc");
                    socket.send(datagramPacket);
                    socket.close();
                } catch (Exception ex){
                    Log.e(TAG, "error " + ex.toString());
                }
            }
        });
        thread.start();
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
