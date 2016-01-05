package com.lge.camera;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.lge.camera.adapter.HelpItemAdapter;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.lang.ref.WeakReference;

public class CameraHelp extends Activity {
    private static final int MSG_SHOW_ITEM = 0;
    private int delayTimeToMoveSelectedItem;
    private boolean isScrolled;
    private int mCameraId;
    private Handler mHandler;
    private HelpItemAdapter mHelpAdapter;
    private ListView mHelpListView;
    private int mHelpMode;
    private String mKeyString;
    private HelpMenuMediaBroadCastReceiver mMediaReceiver;
    private CameraScreenOffReceiver mScreenOffReceiver;
    private boolean mSecureCamera;

    private class CameraScreenOffReceiver extends BroadcastReceiver {
        private CameraHelp mHelpMenu;

        public CameraScreenOffReceiver(Activity activity) {
            this.mHelpMenu = (CameraHelp) activity;
        }

        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    this.mHelpMenu.finish();
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "HelpMenuMediaBroadCastReceiver Exception : ", e);
            }
        }
    }

    private static class HelpHandler extends Handler {
        private final WeakReference<HelpItemAdapter> mHelpItemAdapter;
        private String mHelpKeyString;
        private final WeakReference<ListView> mListView;

        HelpHandler(ListView listView, HelpItemAdapter adapter, String keyString) {
            this.mHelpKeyString = "";
            this.mListView = new WeakReference(listView);
            this.mHelpItemAdapter = new WeakReference(adapter);
            this.mHelpKeyString = keyString;
        }

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ((ListView) this.mListView.get()).setSelection(((HelpItemAdapter) this.mHelpItemAdapter.get()).getItemPosition(this.mHelpKeyString));
            }
        }
    }

    private class HelpMenuMediaBroadCastReceiver extends BroadcastReceiver {
        private CameraHelp mHelpMenu;

        public HelpMenuMediaBroadCastReceiver(Activity activity) {
            this.mHelpMenu = (CameraHelp) activity;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                if (action.equals("android.intent.action.MEDIA_BAD_REMOVAL") || action.equals("android.intent.action.MEDIA_REMOVED") || action.equals("android.intent.action.MEDIA_MOUNTED") || action.equals("android.intent.action.MEDIA_EJECT")) {
                    this.mHelpMenu.finish();
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "HelpMenuMediaBroadCastReceiver Exception : ", e);
            }
        }
    }

    public CameraHelp() {
        this.mHandler = null;
        this.mSecureCamera = false;
        this.isScrolled = false;
        this.delayTimeToMoveSelectedItem = CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL;
    }

    protected void onCreate(Bundle savedInstanceState) {
        String title_version;
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(Ola_ImageFormat.RGB_LABEL);
        AppControlUtil.disableNavigationButton(this);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService("window");
        wm.getDefaultDisplay().getMetrics(outMetrics);
        CameraConstants.setLcdSize(outMetrics.widthPixels, outMetrics.heightPixels);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.mMediaReceiver = new HelpMenuMediaBroadCastReceiver(this);
        registerMediaReceiver(this.mMediaReceiver);
        Intent intent = getIntent();
        this.mHelpMode = intent.getIntExtra(CameraConstants.KEY_STRING_HELP_MODE, 0);
        this.mKeyString = intent.getStringExtra(CameraConstants.KEY_STRING_HELP_MENU);
        this.mCameraId = intent.getIntExtra(CameraConstants.KEY_STRING_CAMERA_ID, 0);
        this.mSecureCamera = intent.getBooleanExtra(CameraConstants.SECURE_CAMERA, false);
        if (this.mSecureCamera) {
            this.mScreenOffReceiver = new CameraScreenOffReceiver(this);
            registerCameraScreenOffReceiver(this.mScreenOffReceiver);
        }
        Common.configureWindowFlag(getWindow(), false, this.mSecureCamera);
        setContentView(R.layout.help_view);
        int orientation = 2;
        switch (wm.getDefaultDisplay().getRotation()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                orientation = 1;
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
                orientation = 2;
                break;
        }
        this.mHelpAdapter = new HelpItemAdapter(getApplicationContext(), this, this.mHelpMode, this.mCameraId, orientation);
        this.mHelpListView = (ListView) findViewById(R.id.help_list_view);
        this.mHelpListView.setAdapter(this.mHelpAdapter);
        this.mHandler = new HelpHandler(this.mHelpListView, this.mHelpAdapter, this.mKeyString);
        this.isScrolled = false;
        this.mHelpListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                CameraHelp.this.isScrolled = true;
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        try {
            CamLog.d(FaceDetector.TAG, "VersionName is " + getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName);
        } catch (NameNotFoundException e) {
            CamLog.e(FaceDetector.TAG, "VersionName is not found, ", e);
        }
        if (this.mHelpMode == 0) {
            title_version = getString(R.string.help_title);
        } else {
            title_version = getString(R.string.sp_help_video_title_NORMAL);
        }
        ab.setTitle(title_version);
        setTitle(title_version);
        this.mHelpListView.setSelection(this.mHelpAdapter.getItemPosition(this.mKeyString));
        setResult(4, intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onRestart() {
        CamLog.d(FaceDetector.TAG, "onRestart()-start ");
        CheckStatusManager.setEnterCheckComplete(false);
        super.onRestart();
        CamLog.d(FaceDetector.TAG, "onRestart()-end");
    }

    protected void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume()-start ");
        if (CheckStatusManager.checkEnterApplication(this, true)) {
            this.mSecureCamera = getIntent().getBooleanExtra(CameraConstants.SECURE_CAMERA, false);
            Common.configureWindowFlag(getWindow(), false, this.mSecureCamera);
            super.onResume();
            CamLog.d(FaceDetector.TAG, "onResume()-end ");
            return;
        }
        super.onResume();
        CamLog.d(FaceDetector.TAG, "onResume()-end,  checkEnterApplication");
        CheckStatusManager.checkCameraOut(this, null);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(Ola_ImageFormat.RGB_LABEL);
    }

    public void onPause() {
        this.mSecureCamera = false;
        Common.configureWindowFlag(getWindow(), false, this.mSecureCamera);
        if (this.mHelpAdapter != null) {
            this.mHelpAdapter.onPause();
        }
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mMediaReceiver != null) {
            unregisterReceiver(this.mMediaReceiver);
            this.mMediaReceiver = null;
        }
        if (this.mScreenOffReceiver != null) {
            unregisterReceiver(this.mScreenOffReceiver);
            this.mScreenOffReceiver = null;
        }
        if (this.mHelpAdapter != null) {
            this.mHelpAdapter.unbind();
            this.mHelpAdapter = null;
        }
        this.mHandler = null;
        this.mHelpListView = null;
    }

    private void registerMediaReceiver(HelpMenuMediaBroadCastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addDataScheme("file");
        registerReceiver(receiver, intentFilter);
    }

    private void registerCameraScreenOffReceiver(CameraScreenOffReceiver receiver) {
        registerReceiver(receiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "Help onConfigurationChanged [" + this.isScrolled + "]");
        if (!(this.isScrolled || this.mHandler == null)) {
            this.mHandler.sendEmptyMessageDelayed(0, (long) this.delayTimeToMoveSelectedItem);
        }
        int actionBarHeight = getActionBar().getHeight();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(16843499, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        ((RelativeLayout) findViewById(R.id.help_layout_view)).setPaddingRelative(0, actionBarHeight, 0, 0);
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
        CameraConstants.setLcdSize(outMetrics.widthPixels, outMetrics.heightPixels);
        if (this.mHelpAdapter != null) {
            this.mHelpAdapter.setOrientation(newConfig.orientation);
            this.mHelpAdapter.refreshDialog();
        }
        super.onConfigurationChanged(newConfig);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case 82:
                CamLog.d(FaceDetector.TAG, "KEYCODE_MENU or KEYCODE_BACK keyup");
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
