package tv.vizbee.firetvdemointegrationapp;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoViewPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaController.MediaPlayerControl {

    private VideoView mVideoView;
    private ProgressBar mLoadingSpinner;

    private Video mVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view_player);

        mVideoView = (VideoView) findViewById(R.id.video_view_player);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);

        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);
        controller.setMediaPlayer(this);

        mLoadingSpinner = (ProgressBar) findViewById(R.id.video_video_loading);

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            if (extras.containsKey("video")) {
                mVideo = extras.getParcelable("video");
                mVideoView.setVideoPath(mVideo.getVideoURL());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mVideoView) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {

        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                mLoadingSpinner.setVisibility(View.VISIBLE);
                break;

            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mLoadingSpinner.setVisibility(View.GONE);
                break;
        }

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // Exit
        finish();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        // Exit
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int i) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
