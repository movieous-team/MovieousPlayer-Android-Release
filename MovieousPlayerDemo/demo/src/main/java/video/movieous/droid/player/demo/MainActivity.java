package video.movieous.droid.player.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.ezandroid.ezpermission.EZPermission;
import cn.ezandroid.ezpermission.Permission;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import video.movieous.droid.player.demo.douyin.DouyinVideoPlayActivity;
import video.movieous.droid.player.demo.utils.PermissionChecker;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE_FILE = 1;
    private static final String DEFAULT_TEST_URL = "https://vfx.mtime.cn/Video/2019/02/13/mp4/190213103941602230.mp4";

    private EditText mEditText;
    private boolean mVideoListOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String info = "版本号：" + getVersionDescription() + "，编译时间：" + getBuildTimeDescription();
        TextView txtVersion = findViewById(R.id.version_info);
        txtVersion.setText(info);

        mEditText = findViewById(R.id.VideoPathEdit);
        mEditText.setText(DEFAULT_TEST_URL);

        findViewById(R.id.douyin_play_start).setEnabled(mVideoListOK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickLocalFile(View v) {
        if (isPermissionOK()) {
            Set<MimeType> sets = MimeType.of(MimeType.MP4, MimeType.THREEGPP);
            Matisse.from(this)
                    .choose(sets, false)
                    .showSingleMediaType(true)
                    .maxSelectable(1)
                    .countable(false)
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new PicassoEngine())
                    .forResult(REQUEST_CODE_CHOOSE_FILE);
        }
    }

    public void onClickPlay(View v) {
        String videopath = mEditText.getText().toString();
        if (!"".equals(videopath)) {
            jumpToPlayerActivity(videopath);
        }
    }

    public void onClickScanQrcode(View v) {
        if (EZPermission.permissions(Permission.CAMERA).available(this)) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setOrientationLocked(true);
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        } else {
            // 申请权限
            EZPermission.permissions(Permission.CAMERA).apply(this, null);
        }
    }

    public void jumpToPlayerActivity(String videopath) {
        Class<?> cls = VideoViewActivity.class;
        Intent intent = new Intent(this, cls);
        intent.putExtra("videoPath", videopath);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        String videoPath = data.getStringExtra("videoPath");
        mEditText.setText(videoPath, TextView.BufferType.EDITABLE);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "扫码取消！", Toast.LENGTH_SHORT).show();
                } else {
                    mEditText.setText(result.getContents());
                }
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                final List<String> paths = Matisse.obtainPathResult(data);
                mEditText.setText(paths.get(0));
            }
        }
    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(this);
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            Toast.makeText(this, "Some permissions is not approved !!!", Toast.LENGTH_SHORT);
        }
        return isPermissionOK;
    }

    private String getVersionDescription() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    protected String getBuildTimeDescription() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(BuildConfig.BUILD_TIMESTAMP);
    }

    public void onClickDouyinPlay(View view) {
        startActivity(new Intent(this, DouyinVideoPlayActivity.class));
    }

    public void onClickGetDouyinVideoList(View view) {
        view.setEnabled(false);
        new Thread(() -> {
            mVideoListOK = DouyinVideoPlayActivity.initData();
            runOnUiThread(() -> {
                findViewById(R.id.douyin_play_start).setEnabled(mVideoListOK);
                view.setEnabled(true);
            });
        }).start();
    }

}
