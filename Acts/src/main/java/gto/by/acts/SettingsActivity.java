package gto.by.acts;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bOk = null, bCancel = null;
    private EditText etUser = null, etPass = null, etConnString = null;
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String CONNECTIONSTRING = "connString";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        bOk = (Button)findViewById(R.id.bOk);
        bOk.setOnClickListener(this);
        bCancel = (Button)findViewById(R.id.bCancel);
        bCancel.setOnClickListener(this);

        etPass = (EditText)findViewById(R.id.etPassword);
        etUser = (EditText)findViewById(R.id.etUser);
        etConnString = (EditText)findViewById(R.id.etConnString);

        loadValues();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //
    }

    private void loadValues() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);

        etUser.setText(sPref.getString(USERNAME, "sa"));
        etPass.setText(sPref.getString(PASSWORD, "123456"));
        etConnString.setText(sPref.getString(CONNECTIONSTRING, "jdbc:jtds:sqlserver://192.168.200.7/AWP_BTO;instance=SQLEXPRESS;socketTimeout=5"));
        Toast.makeText(this, "TextS loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bOk) {
            saveValues();
        }
        finish();
    }

    private void saveValues() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putString(USERNAME, etUser.getText().toString());
        ed.putString(PASSWORD, etPass.getText().toString());
        ed.putString(CONNECTIONSTRING, etConnString.getText().toString());
    }
}
