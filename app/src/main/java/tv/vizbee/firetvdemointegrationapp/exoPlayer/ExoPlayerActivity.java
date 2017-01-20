package tv.vizbee.firetvdemointegrationapp.exoPlayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import tv.vizbee.firetvdemointegrationapp.BuildConfig;
import tv.vizbee.firetvdemointegrationapp.R;
import tv.vizbee.firetvdemointegrationapp.model.Video;
import tv.vizbee.screen.api.Vizbee;
import tv.vizbee.screen.api.messages.VideoInfo;

public class ExoPlayerActivity extends AppCompatActivity implements AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener {

    private SimpleExoPlayer mPlayer;
    private SimpleExoPlayerView mPlayerView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        // 1. Create a default TrackSelector
        mHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        // 4. Set up the player view
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer_player);
        mPlayerView.setPlayer(mPlayer);
        mPlayerView.setUseController(true);
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

        if (null != mPlayer) {
            mPlayer.release();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mPlayerView.dispatchMediaKeyEvent(event);
    }

    private void prepareVideo(Video video, int position) {

        // ---------------------------
        // [BEGIN] Vizbee Integration
        // ---------------------------

        VideoInfo videoInfo = new VideoInfo(video.getGuid());
        ExoPlayerAdapter vizbeeVideoAdapter = new ExoPlayerAdapter(mPlayer);
        Vizbee.getInstance().setVideoAdapter(this, videoInfo, vizbeeVideoAdapter);

        // ---------------------------
        // [END] Vizbee Integration
        // ---------------------------

        loadVideo(video);
    }

    private void loadVideo(Video video) {

        Uri videoUri = Uri.parse(video.getVideoURL());

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, BuildConfig.APPLICATION_ID), bandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = ExoplayerUtils.buildMediaSource(this, videoUri, dataSourceFactory, mHandler, "", this, this);

        // Prepare the player with the source.
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {

    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {

    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {

    }

    @Override
    public void onLoadError(IOException error) {

    }

}
