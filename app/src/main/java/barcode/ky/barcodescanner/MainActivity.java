package barcode.ky.barcodescanner;

import android.hardware.Camera;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import barcode.ky.barcodescanner.CameraSurfacePreview;

public class MainActivity extends AppCompatActivity {
    String TAG = "Barcode";

    TextView txtBarcode;
    String oldbarcode="";
    Camera camera;
    CameraSurfacePreview cameraSurfacePreview;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "msg " + msg.obj + " " + oldbarcode);
            txtBarcode.setText(msg.obj.toString());
             {
                Log.e(TAG, "send msg " );
                sentBarcode(msg.obj.toString());
                oldbarcode = msg.obj.toString();
                cameraSurfacePreview.stopPreview();
            }

        }
    };
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Socket socket;
    String response = "";
    String dstAddress = "192.168.1.107";
    int dstPort = 9999;
    void sentBarcode(final String barcode){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("client","socket client " + dstAddress + "  " + dstPort);
                try {
                    socket = new Socket(dstAddress, dstPort);
                    ByteArrayOutputStream byteArrayOutputStream =
                            new ByteArrayOutputStream(1024);

                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();
                    Log.e("client","read ");
                    /*while ((bytesRead = inputStream.read(buffer)) != -1){
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response += byteArrayOutputStream.toString("UTF-8");
                        Log.e("clicent","response " + response);
                    }*/
                    Log.e("client","send barcode " + barcode);
                    OutputStream of = socket.getOutputStream();
                    of.write(barcode.getBytes());

                    //Log.e("clicent","response ->" + response);
                }catch (Exception ex){
                    Log.e("client","read " + ex.toString());
                    ex.printStackTrace();
                }finally {
                    if(socket !=null){
                        try {
                            Log.e("client","socket close ");
                            socket.close();
                        }catch (Exception ex){
                            Log.e("client","socket close " + ex.toString());
                            ex.printStackTrace();
                        }

                    }
                }
            }
        });
        thread.start();

    }
}
