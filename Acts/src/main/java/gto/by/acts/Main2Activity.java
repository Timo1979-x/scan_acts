package gto.by.acts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import gto.by.acts.helpers.SoundHelper;
import gto.by.acts.other.Constants;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private final static String LOG_TAG = "MainActs";
    class Results {
        ArrayList<String> actStatusNames = new ArrayList<String>();
        ArrayList<Byte> actStatusIDs = new ArrayList<Byte>();
        //HashMap<Integer, Integer> actsMapIndex2Id = new HashMap<Integer, Integer>();
        ArrayList<String> contractStatusNames = new ArrayList<String>();
        ArrayList<Byte> contractStatusIDs = new ArrayList<Byte>();
        String errorMessage;
        //HashMap<Integer, Integer> contractsMapIndex2Id = new HashMap<Integer, Integer>();
    };

    private Button bSettings = null;
    private Button bStart = null;


    public void onPlaySound(View v) {
//        if (mediaPlayer != null) {
//            try {
//                mediaPlayer.release();
//                mediaPlayer = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d(LOG_TAG, "start Raw");
//        mediaPlayer = MediaPlayer.create(this, R.raw.error);
//        mediaPlayer.start();
        SoundHelper.playSound(this, R.raw.error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bStart = (Button)findViewById(R.id.bStart);
        bStart.setOnClickListener(this);
        bSettings = (Button)findViewById(R.id.bSettings);
        bSettings.setOnClickListener(this);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.bStart:
//                intent = new Intent(this, SelectActionActivity.class);
//                startActivity(intent);
                AsyncTask<Void, Integer, Main2Activity.Results> task = new AsyncTask<Void, Integer, Main2Activity.Results>() {
                    private String connString = null, userName = null, userPassword = null;
                    Connection c = null;
                    ResultSet rs = null;
                    PreparedStatement ps1 = null;

                    @Override
                    protected void onPostExecute (Main2Activity.Results result) {

                        if(result.errorMessage == null || result.errorMessage == "") {
                            Intent intent = new Intent(Main2Activity.this, SelectActionActivity.class);
                            intent.putExtra("actStatusNames", result.actStatusNames.toArray(new String[]{}));
                            byte[] arr = new byte[result.actStatusIDs.size()];
                            for(int i = 0; i < result.actStatusIDs.size(); i++) {
                                arr[i] = result.actStatusIDs.get(i);
                            }
                            intent.putExtra("actStatusIDs", arr);
                            intent.putExtra("contractStatusNames", result.contractStatusNames.toArray(new String[]{}));

                            arr = new byte[result.contractStatusIDs.size()];
                            for(int i = 0; i < result.contractStatusIDs.size(); i++) {
                                arr[i] = result.contractStatusIDs.get(i);
                            }
                            intent.putExtra("contractStatusIDs", arr);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), result.errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(LOG_TAG, result.errorMessage);
                        }
                    }
                    @Override
                    protected Main2Activity.Results doInBackground(Void... voids) {
                        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
                        userName = prefs.getString(Constants.USERNAME, null);
                        userPassword = prefs.getString(Constants.PASSWORD, null);
                        connString = prefs.getString(Constants.CONNECTIONSTRING, null);
                        Main2Activity.Results results = new Main2Activity.Results();
                        if(userName == null || userPassword == null || connString == null) {
                            results.errorMessage = "Settings is empty!";
                            return results;
                        }

                        try {
                            Class.forName("net.sourceforge.jtds.jdbc.Driver");
                            c = DriverManager.getConnection(connString, userName, userPassword);

                            ps1 = c.prepareStatement("select [ID], [Name] from [AWP_BTO].[Dictionary].[ActStatus] order by [ID]");
                            rs = ps1.executeQuery();
                            while(rs.next()) {
                                results.actStatusNames.add(rs.getString(2));
                                results.actStatusIDs.add(rs.getByte(1));
                            }
                            rs.close();
                            ps1.close();
                            ps1 = c.prepareStatement("select [ID], [Name] from [AWP_BTO].[Dictionary].[ContractState] order by [ID]");
                            rs = ps1.executeQuery();
                            while(rs.next()) {
                                results.contractStatusNames.add(rs.getString(2));
                                results.contractStatusIDs.add(rs.getByte(1));
                            }

                        } catch(Exception e) {
                            results.errorMessage = e.getMessage();
                            Log.e(LOG_TAG, results.errorMessage, e);
                        } finally {
                            if(rs != null) {
                                try {
                                    rs.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(ps1!=null) {
                                try {
                                    ps1.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(c != null) {
                                try {
                                    c.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return results;
                    }
                };
                task.execute();
                break;
            default:
                break;
        }
    }
}
