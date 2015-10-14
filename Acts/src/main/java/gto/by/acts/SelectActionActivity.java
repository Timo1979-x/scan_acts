package gto.by.acts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.regex.Pattern;

import gto.by.acts.other.Constants;

public class SelectActionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static Pattern qrActPattern = java.util.regex.Pattern.compile("^А ([^\\s]+) (\\d{4})(\\d{2})(\\d{2})$");
    private Button bTest = null;
    //private Button bScan = null;
    private TextView tvFormat = null, tvContent = null;
    private Spinner spActs = null;
    private EditText etTestQrCode = null;
    private String [] actStatusNames = null;
    private byte[] actStatusIDs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);

//        bScan = (Button)findViewById(R.id.bScan);
//        bScan.setOnClickListener(this);

        bTest = (Button)findViewById(R.id.bTest);
        bTest.setOnClickListener(this);

        tvFormat = (TextView)findViewById(R.id.tvFormat);
        tvContent = (TextView)findViewById(R.id.tvContent);
        spActs = (Spinner)findViewById(R.id.spActs);
        spActs.setOnItemSelectedListener(this);

        etTestQrCode = (EditText)findViewById(R.id.etTestQRCode);

        actStatusNames = getIntent().getStringArrayExtra("actStatusNames");
        actStatusIDs = getIntent().getByteArrayExtra("actStatusIDs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, actStatusNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActs.setAdapter(dataAdapter);
        spActs.setSelection(1);
        int pos = spActs.getSelectedItemPosition();
        long id = spActs.getSelectedItemId();

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.planets_array, android.R.layout.simple_spinner_item);


//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(), android.R.layout.simple_spinner_item, new String[] {"1", "2", "4"});
//
//        HashMap<Integer, String> map = new HashMap<Integer, String>();
//        map.put(1, "1_");
//        map.put(2, "2_");
//        DictionaryAdapter da = new DictionaryAdapter(map);
//
//
//        sp.setAdapter(da);
    }

    public void launchScannerActivity(View v) {
        Intent intent = new Intent(this, ScannerActivity.class);
        int pos = spActs.getSelectedItemPosition();
        byte newState = actStatusIDs[pos];
        intent.putExtra("newState", newState);
        startActivityForResult(intent, 1);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null) {return;}
//        String name = data.getStringExtra("name");
//        tvName.setText("Your name is " + name);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bScan:
//                Intent intent = new Intent(this, ScanActsActivity.class);
//                startActivity(intent);
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                //scanIntegrator.initiateScan(Arrays.asList("QR_CODE"));
                scanIntegrator.initiateScan();

                break;
            case R.id.bTest:
                //processScanCode("QR_CODE", etTestQrCode.getText().toString());
                break;
            default:
                break;
        }
    }

//    public void processScanCode(String scanFormat, String scanContent) {
//        Matcher m = qrActPattern.matcher(scanContent);
//        if(!(scanFormat.equals("QR_CODE") && m.matches())) {
//            Toast.makeText(getApplicationContext(),
//                    "Wrong scan data format!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        AsyncTask<Object, Integer, SelectActionActivity.Results> task = new AsyncTask<Object, Integer, SelectActionActivity.Results>() {
//            private String connString = null, userName = null, userPassword = null;
//            Connection c = null;
//            //ResultSet rs = null;
//            PreparedStatement ps1 = null;
//
//            @Override
//            protected void onPostExecute (SelectActionActivity.Results result) {
//                Toast.makeText(getApplicationContext(), result.errorMessage, Toast.LENGTH_LONG).show();
//                Log.d("db", result.errorMessage);
//            }
//            @Override
//            protected SelectActionActivity.Results doInBackground(Object... params) {
//                //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                SharedPreferences prefs = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
//                userName = prefs.getString(Constants.USERNAME, null);
//                userPassword = prefs.getString(Constants.PASSWORD, null);
//                connString = prefs.getString(Constants.CONNECTIONSTRING, null);
//                SelectActionActivity.Results results = new SelectActionActivity.Results();
//                if(userName == null || userPassword == null || connString == null) {
//                    results.errorMessage = "Settings is empty!";
//                    return results;
//                }
//
//                try {
//                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
//                    c = DriverManager.getConnection(connString, userName, userPassword);
//
//                    ps1 = c.prepareStatement("Update [AWP_BTO].[Application].[Act] set [ActStatusID]=? where [number] = ? and [Period] = ?");
//                    ps1.setByte(1, (Byte)params[0]);
//                    ps1.setString(2, (String) params[1]);
//                    ps1.setDate(3, (java.sql.Date) params[2]);
//                    ps1.execute();
//                    results.errorMessage = "update count: " + ps1.getUpdateCount();
//                } catch(Exception e) {
//                    results.errorMessage = e.getMessage();
//                    Log.e("db", results.errorMessage, e);
//                } finally {
//                    if(ps1!=null) {
//                        try {
//                            ps1.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if(c != null) {
//                        try {
//                            c.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                return results;
//            }
//        };
//
//        String dateLiteral = m.group(2) + "-" + m.group(3) + "-" + m.group(4);
//        java.sql.Date dat = java.sql.Date.valueOf(dateLiteral);
//        int pos = spActs.getSelectedItemPosition();
//        Byte state = actStatusIDs[pos];
//        task.execute(state, m.group(1), dat);
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        String msg;
        switch (resultCode) {
            case Constants.RESULT_NEW_STATE_UNDEFINED:
                msg = "Не задан Act state ID";
                break;
            default:
                msg = String.format("Scanner activity closed requestCode = %d resultCode = %d", requestCode, resultCode);
                break;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//        if (scanningResult == null) {
//            Toast.makeText(getApplicationContext(),
//                    "No scan data received!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String scanContent = scanningResult.getContents();
//        String scanFormat = scanningResult.getFormatName();
//        tvFormat.setText("FORMAT: " + scanFormat);
//        tvContent.setText("CONTENT: " + scanContent);
//
//        processScanCode(scanFormat, scanContent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(getApplicationContext(),
//                "onItemSelected " + i + " " + l, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
//        Toast.makeText(getApplicationContext(),
//                "onNothingSelected", Toast.LENGTH_SHORT).show();
    }
}
