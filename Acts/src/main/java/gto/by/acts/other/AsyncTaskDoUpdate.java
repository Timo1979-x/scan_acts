package gto.by.acts.other;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by ltv on 14.10.2015.
 */
//public class AsyncTaskDoUpdate extends AsyncTask<Object, Integer, AsyncTaskDoUpdate.Results> {
//    public class Results {
//        public String errorMessage = null;
//        public boolean error = false;
//    };
//
//    //AsyncTask<Object, Integer, SelectActionActivity.Results>
//    private String connString = null, userName = null, userPassword = null;
//    Connection c = null;
//    //ResultSet rs = null;
//    PreparedStatement ps1 = null;
//
//    @Override
//    protected void onPostExecute (AsyncTaskDoUpdate.Results result) {
//        Toast.makeText(getApplicationContext(), result.errorMessage, Toast.LENGTH_LONG).show();
//        Log.d("db", result.errorMessage);
//    }
//    @Override
//    protected AsyncTaskDoUpdate.Results doInBackground(Object... params) {
//        //SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//        SharedPreferences prefs = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
//        userName = prefs.getString(Constants.USERNAME, null);
//        userPassword = prefs.getString(Constants.PASSWORD, null);
//        connString = prefs.getString(Constants.CONNECTIONSTRING, null);
//        AsyncTaskDoUpdate.Results results = new AsyncTaskDoUpdate.Results();
//        if(userName == null || userPassword == null || connString == null) {
//            results.errorMessage = "Settings is empty!";
//            return results;
//        }
//
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            c = DriverManager.getConnection(connString, userName, userPassword);
//
//            ps1 = c.prepareStatement("Update [AWP_BTO].[Application].[Act] set [ActStatusID]=? where [number] = ? and [Period] = ?");
//            ps1.setByte(1, (Byte)params[0]);
//            ps1.setString(2, (String) params[1]);
//            ps1.setDate(3, (java.sql.Date) params[2]);
//            ps1.execute();
//            results.errorMessage = "update count: " + ps1.getUpdateCount();
//        } catch(Exception e) {
//            results.errorMessage = e.getMessage();
//            Log.e("db", results.errorMessage, e);
//        } finally {
//            if(ps1!=null) {
//                try {
//                    ps1.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(c != null) {
//                try {
//                    c.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return results;
//    }
//}


