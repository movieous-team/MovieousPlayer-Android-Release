package video.movieous.droid.player.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import video.movieous.droid.player.listener.OnCompletionListener;
import video.movieous.droid.player.listener.OnErrorListener;
import video.movieous.droid.player.listener.OnPreparedListener;
import video.movieous.droid.player.ui.widget.VideoView;

/**
 * This is a demo activity of VideoView
 */
public class VideoViewActivity extends VideoPlayerBaseActivity implements OnPreparedListener, OnCompletionListener, AnalyticsListener, OnErrorListener {
    private static final String TAG = VideoViewActivity.class.getSimpleName();

    private VideoView mVideoView;
    String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        mVideoPath = getIntent().getStringExtra("videoPath");
        setupVideoView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        mVideoView.release();
        super.onDestroy();
    }

    private void setupVideoView() {
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setAnalyticsListener(this);
        mVideoView.setRepeatMode(Player.REPEAT_MODE_ALL);
        mVideoView.setVideoPath(mVideoPath);
    }

    @Override
    public void onPrepared() {
        Log.i(TAG, "onPrepared");
        mVideoView.start();
    }

    @Override
    public void onCompletion() {
        Log.i(TAG, "onCompletion");
    }

    @Override
    public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
        Log.i(TAG, "onRenderedFirstFrame");
    }

    @Override
    public boolean onError(Exception e) {
        Log.i(TAG, "onError: " + e.getMessage());
        return false;
    }

}
