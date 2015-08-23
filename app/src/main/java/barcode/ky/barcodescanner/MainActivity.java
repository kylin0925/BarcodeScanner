package barcode.ky.barcodescanner;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import android.os.Handler;
import android.widget.TextView;

import barcode.ky.barcodescanner.CameraSurfacePreview;

public class MainActivity extends AppCompatActivity {
    String TAG = "Barcode";

    TextView txtBarcode;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"msg " + msg.arg1 + " " + msg.obj);
            txtBarcode.setText(msg.obj.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraSurfacePreview cameraSurfacePreview = (CameraSurfacePreview)findViewById(R.id.surfaceView);
        cameraSurfacePreview.setHandler(handler);

        txtBarcode = (TextView)findViewById(R.id.txtBarcode);
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
}
