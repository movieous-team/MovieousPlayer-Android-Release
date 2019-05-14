package video.movieous.droid.player.demo;

import android.app.Application;
import android.content.Context;
import cn.ezandroid.ezpermission.EZPermission;
import cn.ezandroid.ezpermission.Permission;
import video.movieous.droid.player.MovieousPlayer;
import video.movieous.droid.player.MovieousPlayerEnv;
import video.movieous.droid.player.strategy.ULoadControl;
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
        /**
         * 不需要自定义缓冲设置，可以不调用以下代码
         * minBufferMs： 最小缓冲时间 默认 15000ms
         * maxBufferMs： 最大缓冲时间 默认 50000ms
         * bufferForPlaybackMs：首次缓冲开始播放时间，需要小于最小缓冲时间 默认 2500ms
         * bufferForPlaybackAfterRebufferMs：再次缓冲开始播放时间，需要小于最小缓冲时间 5000ms
         */
        ULoadControl myLoadControl = new ULoadControl(3000, 10000, 1000, 2000);
        MovieousPlayer.setLoadControl(myLoadControl);

        // 申请权限
        EZPermission.permissions(Permission.STORAGE)
                .apply(this, null);
    }

}
