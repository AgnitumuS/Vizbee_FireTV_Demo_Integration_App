package tv.vizbee.firetvdemointegrationapp.exoPlayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
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

public class ExoPlayerActivity extends AppCompatActivity implements AdaptiveMediaSourceEventListener,
        ExtractorMediaSource.EventListener, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener, ExoPlayer.EventListener {

    private static final String LOG_TAG = "ExoPlayerActivity";

    private SimpleExoPlayer mPlayer;
    private SimpleExoPlayerView mPlayerView;
    private Handler mHandler;
    private int mStartPosition = -1;

    private ImaSdkFactory mSdkFactory;
    private AdsLoader mAdsLoader;
    private AdsManager mAdsManager;
    private boolean mIsAdDisplayed;
    private boolean mIsAdsInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 1. Create a default TrackSelector
        mHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        mPlayer.addListener(this);

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
        if (mPlayerView.dispatchMediaKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
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

        // Save start position
        mStartPosition = position;

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
        mPlayer.setPlayWhenReady(false);

        // Initialize & request Ads
        initializeAds();
        requestAds(getString(R.string.ad_tag_vmap_single_pre_mid_post));

    }

    private void resumeContent() {
        // Seek when start position is available
        if ((mAdsLoader == null) || !mIsAdDisplayed) {

            if ((mStartPosition != -1)) {
                mPlayer.seekTo(mStartPosition);
                mStartPosition = -1;
            }

            if (mPlayer != null) {
                mPlayer.setPlayWhenReady(true);
            }
            mPlayerView.setVisibility(View.VISIBLE);
        }
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

    //-------------------------------------------------------------------------
    // ExoPlayer events
    //-------------------------------------------------------------------------

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY) {
            Log.d(LOG_TAG, "Player state changed: READY, PlayWhenReady = " + (playWhenReady));

        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            Log.d(LOG_TAG, "Player state changed: ENDED");

            // Handle completed event for playing post-rolls.
            if (mAdsLoader != null) {
                mAdsLoader.contentComplete();
            }

        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            Log.d(LOG_TAG, "Player state changed: BUFFERING");

        } else if (playbackState == ExoPlayer.STATE_IDLE) {
            Log.d(LOG_TAG, "Player state changed: IDLE");
        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(LOG_TAG, "Playback error: " + error.getLocalizedMessage());
    }

    @Override
    public void onPositionDiscontinuity() {}

    //-------------------------------------------------------------------------
    // Ads
    //-------------------------------------------------------------------------

    private void initializeAds() {

        if (mIsAdsInitialized) { return; }

        Log.i(LOG_TAG, "Initializing ads SDK");

        // Create IMA settings
        ImaSdkSettings imaSdkSettings = new ImaSdkSettings();

        // Create an AdsLoader.
        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(this, imaSdkSettings);

        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                Log.i(LOG_TAG, "Ads manager loaded, initializing ads manager ");

                mIsAdsInitialized = true;

                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();

                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(ExoPlayerActivity.this);
                mAdsManager.addAdEventListener(ExoPlayerActivity.this);
                mAdsManager.init();

                // Set up Vizbee ad adapter
                ExoPlayerAdAdapter vizbeeAdAdapter = new ExoPlayerAdAdapter(mAdsManager, "");
                Vizbee.getInstance().setAdAdapter(vizbeeAdAdapter);
            }
        });
    }

    /**
     * Request video ads from the given VAST ad tag.
     *
     * @param adTagUrl URL of the ad's VAST XML
     */
    private void requestAds(String adTagUrl) {
        Log.i(LOG_TAG, "Requesting ads with tag = " + adTagUrl);

        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer((ViewGroup) findViewById(R.id.exoplayer_root));

        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mIsAdDisplayed || mPlayer == null || mPlayer.getDuration() <= 1) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(mPlayer.getCurrentPosition(),
                        mPlayer.getDuration());
            }
        });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e(LOG_TAG, "Ad error: " + adErrorEvent.getError().getMessage());
        resumeContent();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.i(LOG_TAG, "Ad event: " + adEvent.getType());

        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {

            case LOADED:
                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.
                mAdsManager.start();
                break;

            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                mIsAdDisplayed = true;
                mPlayer.setPlayWhenReady(false);
                mPlayerView.setVisibility(View.GONE);
                break;

            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                mIsAdDisplayed = false;
                resumeContent();
                break;

            case ALL_ADS_COMPLETED:
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                finish();
                break;

            default:
                break;
        }
    }
}
