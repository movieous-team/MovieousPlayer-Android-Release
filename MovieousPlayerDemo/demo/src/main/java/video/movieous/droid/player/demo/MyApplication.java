package video.movieous.droid.player.demo;

import android.app.Application;
import android.content.Context;
import cn.ezandroid.ezpermission.EZPermission;
import cn.ezandroid.ezpermission.Permission;
import video.movieous.droid.player.MovieousPlayerEnv;
import video.movieous.droid.player.util.ULog;

public class MyApplication extends Application {
    public static MyApplication gContext = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        gContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 SDK，必须第一个调用，否则会出现异常
        MovieousPlayerEnv.init(gContext);
        // 开启本地缓存，可以离线播放, 需要 okhttp 支持
        MovieousPlayerEnv.setCacheInfo(getCacheDir(), null, 100 * 1024 * 1024, "MovieousPlayer20", true);
        // 设置 log 输出，以下为默认设置，如果不需要修改，可以不用调用
        ULog.getInstance()
                .setLogPath("/sdcard/movieous/log/player/")
                .isOpen(true)
                .isSave(true)
                .setLevel(ULog.I)
                .initialize();

        // 申请权限
        EZPermission.permissions(Permission.STORAGE)
                .apply(this, null);
    }

}
