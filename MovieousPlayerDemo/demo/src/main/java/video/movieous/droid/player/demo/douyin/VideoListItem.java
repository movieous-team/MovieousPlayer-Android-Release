package video.movieous.droid.player.demo.douyin;

import java.io.Serializable;

public class VideoListItem implements Serializable {

    public int avatarRes;
    public String videoUrl;
    public String userName;
    public String content;
    public String coverUrl;
    public String avatarUrl;
    public int videoWidth;
    public int videoHeight;

    public VideoListItem(int avatarRes, String videoUrl, String userName, String content, String coverUrl, int videoWidth, int videoHeight) {
        this.avatarRes = avatarRes;
        this.videoUrl = videoUrl;
        this.userName = userName;
        this.content = content;
        this.coverUrl = coverUrl;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.avatarUrl = null;
    }

    public VideoListItem(String videoUrl, String userName, String content, String coverUrl, String avatarUrl, int videoWidth, int videoHeight) {
        this.avatarRes = 0;
        this.videoUrl = videoUrl;
        this.userName = userName;
        this.content = content;
        this.coverUrl = coverUrl;
        this.avatarUrl = avatarUrl;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }
}
