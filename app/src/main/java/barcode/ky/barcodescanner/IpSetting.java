package barcode.ky.barcodescanner;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IpSetting extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String IP = "IP";
    EditText editIp[] = new EditText[4];
    void showIp(){
        String ip = sharedPreferences.getString(IP, "");
        if(ip!="" && ip.length() > 0) {
            String[] ips = ip.split(",");
            if(ips.length > 0) {
                for (int i = 0; i < ips.length; i++) {
                    editIp[i].setText(ips[i]);
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_setting);
        sharedPreferences = getSharedPreferences("IP",0);



        editIp[0] = (EditText)findViewById(R.id.ip0);
        editIp[1] = (EditText)findViewById(R.id.ip1);
        editIp[2] = (EditText)findViewById(R.id.ip2);
        editIp[3] = (EditText)findViewById(R.id.ip3);

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edtip = editIp[0].getText().toString();
                for(int i = 1; i < 4; i++){
                    edtip += "." + editIp[i].getText() ;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IP,edtip);
                editor.commit();
            }
        });

        showIp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ip_setting, menu);
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
