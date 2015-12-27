package barcode.ky.barcodescanner;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Setting extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String IP = "IP";
    EditText editIp;
    void showIp(){
        String ip = sharedPreferences.getString(IP, "");
        editIp.setText(ip);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ip_setting);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("IP",0);



        editIp = (EditText)findViewById(R.id.ip0);
//        editIp[1] = (EditText)findViewById(R.id.ip1);
//        editIp[2] = (EditText)findViewById(R.id.ip2);
//        editIp[3] = (EditText)findViewById(R.id.ip3);

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edtip = editIp.getText().toString();
//                for(int i = 1; i < 4; i++){
//                    edtip += "." + editIp[i].getText() ;
//                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IP,edtip);
                editor.commit();
            }
        });

        showIp();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//       // getMenuInflater().inflate(R.menu.menu_ip_setting, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
