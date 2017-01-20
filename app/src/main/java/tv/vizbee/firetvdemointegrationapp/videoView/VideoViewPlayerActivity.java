package tv.vizbee.firetvdemointegrationapp.videoView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import tv.vizbee.firetvdemointegrationapp.R;
import tv.vizbee.firetvdemointegrationapp.model.Video;
import tv.vizbee.screen.api.Vizbee;
import tv.vizbee.screen.api.adapter.MediaPlayerControlAdapter;
import tv.vizbee.screen.api.messages.VideoInfo;

public class VideoViewPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private VideoView mVideoView;
    private ProgressBar mLoadingSpinner;

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

        mLoadingSpinner = (ProgressBar) findViewById(R.id.video_video_loading);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleVideoIntent();
    }

    private void handleVideoIntent() {

        Bundle extras = getIntent().getExtras();
        if ((null != extras) && !extras.containsKey("duplicate")) {

            getIntent().putExtra("duplicate", true);

            Video video = null;
            int position = 0;
            if (extras.containsKey("video")) {
                video = extras.getParcelable("video");
            }
            if (extras.containsKey("position")) {
                position = extras.getInt("position");
            }

            if (null != video) {
                prepareVideo(video, position);
            }
        }
    }

    private void prepareVideo(Video video, int position) {

        // ---------------------------
        // [BEGIN] Vizbee Integration
        // ---------------------------

        VideoInfo videoInfo = new VideoInfo(video.getGuid());
        MediaPlayerControlAdapter vizbeeVideoAdapter = new MediaPlayerControlAdapter(mVideoView);
        Vizbee.getInstance().setVideoAdapter(this, videoInfo, vizbeeVideoAdapter);

        // ---------------------------
        // [END] Vizbee Integration
        // ---------------------------

        mVideoView.setVideoPath(video.getVideoURL());
    }

    @Override
    protected void onStop() {
        super.onStop();

        // ---------------------------
        // [BEGIN] Vizbee Integration
        // ---------------------------

        Vizbee.getInstance().removeVideoAdapter();

        // ---------------------------
        // [END] Vizbee Integration
        // ---------------------------

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

}
