package video.movieous.droid.player.demo.douyin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import video.movieous.droid.player.demo.R;
import video.movieous.droid.player.demo.VideoPlayerBaseActivity;
import video.movieous.droid.player.demo.utils.SSLSocketFactoryCompat;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;

/**
 * 仿抖音上下滑动播放
 */
public class DouyinVideoPlayActivity extends VideoPlayerBaseActivity {
    private static final String TAG = "DouyinVideoPlayActivity";

    private RecyclerView mVideoListRecyclerView;
    private VideoItemAdapter mVideoItemAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private PagerSnapHelper mPagerSnapHelper;
    private static ArrayList<VideoListItem> mVideoList;
    private boolean mIsPause;
    private int mPlayPosition;
    private View mPlayView;

    private static String[] VIDEO_SOURCE = new String[]{"dou-yin", "kuai-shou", "huo-shan", "mei-pai"};
    private static String[] TIME_RANGE = new String[]{"week", "month"};
    private static int MAX_PAGE = 5;
    private static int SOURCE_INDEX;

    private void playVideo() {
        View snapView = mPagerSnapHelper.findSnapView(mLinearLayoutManager);
        if (snapView == null) {
            return;
        }
        final int position = mLinearLayoutManager.getPosition(snapView);
        if (position < 0) {
            return;
        }

        if (mPlayView != null) {
            final VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
            vh.videoView.setTag(vh.videoView.getVideoUri().toString());
            vh.videoView.stopPlayback();
        }

        mPlayView = snapView;
        mPlayPosition = position;
        final VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
        Log.i(TAG, "start play, url: " + vh.videoView.getVideoUri().toString());

        if (vh.videoView.getTag() == null || !vh.videoView.getTag().equals(vh.videoView.getVideoUri().toString())) {
            vh.videoView.start();
        } else {
            vh.videoView.restart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsPause) {
            mIsPause = false;
            playVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
        VideoItemAdapter.VideoViewHolder vh = (VideoItemAdapter.VideoViewHolder) mVideoListRecyclerView.getChildViewHolder(mPlayView);
        vh.videoView.pause();
    }

    @Override
    protected void onDestroy() {
        mVideoItemAdapter.release();
        mVideoListRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    private static X509TrustManager getTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 可能在外部提前获取数据源
    public static boolean initData() {
        if (mVideoList == null) {
            mVideoList = new ArrayList<>();
        }
        int rangeIndex = 1;//Utils.getRandomNum(0, 1);
        if (SOURCE_INDEX >= VIDEO_SOURCE.length) {
            SOURCE_INDEX = 0;
        }
        String url = String.format(
                "http://kuaiyinshi.com/api/hot/videos/?source=%s&page=%d&st=%s&_=%d",
                VIDEO_SOURCE[SOURCE_INDEX], MAX_PAGE, TIME_RANGE[rangeIndex], System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        );
        SOURCE_INDEX++;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().sslSocketFactory(new SSLSocketFactoryCompat(), getTrustManager()).build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            parseResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "total video list: " + (mVideoList == null ? 0 : mVideoList.size()));
        return mVideoList != null && !mVideoList.isEmpty();
    }

    private static void parseResponse(Response response) {
        if (!response.isSuccessful()) {
            return;
        }
        try {
            JSONObject json = new JSONObject(response.body().string());
            if (json.optInt("code") != 200) {
                return;
            }
            JSONArray data = json.optJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject videoItem = data.getJSONObject(i);
                String head = "http:";
                String videoUrl = head + videoItem.optString("video_url");
                String userName = videoItem.optString("nickname");
                String content = videoItem.optString("desc");
                String coverUrl = head + videoItem.optString("video_img");
                String avatarUrl = head + videoItem.optString("avatar", null);
                VideoListItem videoBean = avatarUrl == null ?
                        new VideoListItem(R.drawable.ic_empty_zhihu, videoUrl, userName, content, coverUrl, 720, 1280) :
                        new VideoListItem(videoUrl, userName, content, coverUrl, avatarUrl, 720, 1280);
                mVideoList.add(videoBean);
            }
            Log.i("movieous", "video list length = " + data.length());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        initView();
        if (mVideoList == null || mVideoList.isEmpty()) {
            new Thread(() -> initData()).start();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_douyin_video_play);
        mVideoListRecyclerView = findViewById(R.id.rv_video_detail);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mVideoListRecyclerView.setLayoutManager(mLinearLayoutManager);
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(mVideoListRecyclerView);
        mVideoItemAdapter = new VideoItemAdapter(this, mVideoList);
        mVideoListRecyclerView.setAdapter(mVideoItemAdapter);
        mVideoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPagerSnapHelper.findSnapView(mLinearLayoutManager) != mPlayView) {
                    playVideo();
                }
            }
        });

        if (mVideoList != null && !mVideoList.isEmpty()) {
            mVideoListRecyclerView.post(() -> {
                mVideoListRecyclerView.scrollToPosition(mPlayPosition);
                mVideoListRecyclerView.post(() -> playVideo());
            });
        }
    }

}
