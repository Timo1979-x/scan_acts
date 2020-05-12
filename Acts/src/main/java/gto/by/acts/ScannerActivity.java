package gto.by.acts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import gto.by.acts.helpers.SoundHelper;
import gto.by.acts.other.Constants;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import gto.by.acts.fragments.*;

public class ScannerActivity extends ActionBarActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {
    class Results {
        String message = null;
        boolean error = false;
        Throwable cause = null;
    };

    public class AsyncTaskDoUpdate extends AsyncTask<Object, Integer, ScannerActivity.Results> {
        private Context context;

        private String connString = null, userName = null, userPassword = null;
        Connection c = null;
        PreparedStatement ps1 = null;

        public AsyncTaskDoUpdate(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute (ScannerActivity.Results result) {
            Toast.makeText(this.context, result.message, Toast.LENGTH_LONG).show();
            if (result.error) {
                Log.e(LOG_TAG, result.message);
            }else{
                Log.d(LOG_TAG, result.message);
            }
        }

        @Override
        protected ScannerActivity.Results doInBackground(Object... params) {
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
            userName = prefs.getString(Constants.USERNAME, null);
            userPassword = prefs.getString(Constants.PASSWORD, null);
            connString = prefs.getString(Constants.CONNECTIONSTRING, null);
            ScannerActivity.Results results = new ScannerActivity.Results();
            if(userName == null || userPassword == null || connString == null) {
                results.message = "Settings is empty!";
                results.error = true;
                return results;
            }

            try {
                long t1 = System.currentTimeMillis();
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                c = DriverManager.getConnection(connString, userName, userPassword);
                long t2 = System.currentTimeMillis();
                ps1 = c.prepareStatement("Update [AWP_BTO].[Application].[Act] set [ActStatusID]=? where [number] = ?"); //  and [Period] = ?
                long t3 = System.currentTimeMillis();
                ps1.setByte(1, (Byte)params[0]);
                ps1.setString(2, (String) params[1]);
//                ps1.setDate(3, (java.sql.Date) params[2]);
                ps1.execute();
                long t4 = System.currentTimeMillis();
                results.message = "update count: " + ps1.getUpdateCount() + " timings (ms): " + (t2 - t1) + " " + (t3 - t2) + " " + (t4 - t3);
                SoundHelper.playSound(context, R.raw.success);
            } catch(Exception e) {
                SoundHelper.playSound(context, R.raw.error);
                results.message = e.getMessage();
                results.error = true;
                results.cause = e;
                Log.e(LOG_TAG, results.message, e);
            } finally {
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
    }

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    public static final String LOG_TAG = "ScannerActivity";
    private static Pattern qrActPattern = java.util.regex.Pattern.compile("^А ([^\\s]+) (\\d{4})(\\d{2})(\\d{2})$");
    private byte newState;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }
        newState = getIntent().getByteExtra("newState", (byte)-1);
        if(newState == -1){
            Intent intent = new Intent();
            setResult(Constants.RESULT_NEW_STATE_UNDEFINED, intent);
            finish();
            return;
        }
        mScannerView = new ZXingScannerView(this);
        setupFormats();
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;

        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);


        if(mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult == null) {
            Toast.makeText(this,
                    "No scan data received!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String scanContent = rawResult.getText();
            String scanFormat = rawResult.getBarcodeFormat().toString();
            Toast.makeText(this,
                    "Contents = " + scanContent + ", Format = " + scanFormat,
                    Toast.LENGTH_SHORT).show();
            processScanCode(scanFormat, scanContent);

            //showMessageDialog("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString());
            // mScannerView.setResultHandler(this);
        }
        mScannerView.startCamera(mCameraId);
    }

    public void processScanCode(String scanFormat, String scanContent) {
        Matcher m = qrActPattern.matcher(scanContent);
        if(!(scanFormat.equals("QR_CODE") && m.matches())) {
            Toast.makeText(getApplicationContext(),
                    "Wrong scan data format!", Toast.LENGTH_SHORT).show();
            SoundHelper.playSound(this, R.raw.bad_code);
            return;
        }

        AsyncTaskDoUpdate task1 = new AsyncTaskDoUpdate(this);

        String dateLiteral = m.group(2) + "-" + m.group(3) + "-" + m.group(4);
        java.sql.Date dat = java.sql.Date.valueOf(dateLiteral);

        task1.execute(Byte.valueOf(newState), m.group(1), dat);
    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                String name = ZXingScannerView.ALL_FORMATS.get(i).name();
                if(name == "QR_CODE" || name == "EAN_13") {
                    mSelectedIndices.add(i);
                }
            }
        }

        for(int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }
}