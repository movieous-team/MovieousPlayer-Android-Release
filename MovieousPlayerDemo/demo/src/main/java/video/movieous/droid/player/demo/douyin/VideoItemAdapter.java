package video.movieous.droid.player.demo.douyin;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;

import java.util.ArrayList;
import java.util.List;

import video.movieous.droid.player.demo.R;
import video.movieous.droid.player.listener.OnCompletionListener;
import video.movieous.droid.player.listener.OnPreparedListener;
import video.movieous.droid.player.ui.widget.VideoView;

public class VideoItemAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<VideoListItem> mVideoItemList;
    private List<VideoViewHolder> mHolderList = new ArrayList<>();

    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private AnalyticsListener mAnalyticsListener;

    public VideoItemAdapter(Context context, ArrayList<VideoListItem> videoItemList) {
        this.mContext = context;
        this.mVideoItemList = videoItemList;
    }

    public void setListener(OnPreparedListener onPreparedListener, OnCompletionListener onCompletionListener, AnalyticsListener analyticsListener) {
        mOnPreparedListener = onPreparedListener;
        mOnCompletionListener = onCompletionListener;
        mAnalyticsListener = analyticsListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(View.inflate(mContext, R.layout.item_video_details, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VideoViewHolder holder = (VideoViewHolder) viewHolder;
        int index = mVideoItemList.size() - 1 - position;
        VideoListItem videoItem = mVideoItemList.get(index);
        mHolderList.add(holder);

        if (videoItem.avatarRes > 0) {
            Glide.with(mContext).load(videoItem.avatarRes).into(holder.iv_avatar);
        } else {
            Glide.with(mContext).load(videoItem.avatarUrl).into(holder.iv_avatar);
        }

        // 视频封面
        if (!TextUtils.isEmpty(videoItem.coverUrl)) {
            Glide.with(mContext.getApplicationContext())
                    .load(videoItem.coverUrl.endsWith(".kpg") ? "" : videoItem.coverUrl)
                    .into(holder.videoView.getPreviewImageView());
        }

        holder.tv_content.setText(videoItem.content);
        holder.tv_name.setText(videoItem.userName);
        holder.videoView.setVideoPath(videoItem.videoUrl);
        holder.videoView.setRepeatMode(Player.REPEAT_MODE_ALL);
        holder.videoView.setReleaseOnDetachFromWindow(false);
    }

    @Override
    public int getItemCount() {
        return mVideoItemList != null ? mVideoItemList.size() : 0;
    }

    public void release() {
        for (VideoViewHolder holder : mHolderList) {
            holder.videoView.release();
        }
        mHolderList.clear();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_content;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.video_view);
            videoView.setOnPreparedListener(mOnPreparedListener);
            videoView.setOnCompletionListener(mOnCompletionListener);
            videoView.setAnalyticsListener(mAnalyticsListener);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);

            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
