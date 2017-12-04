package tv.vizbee.firetvdemointegrationapp.exoPlayer;


import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;

import tv.vizbee.screen.api.adapter.VZBAdapter;
import tv.vizbee.screen.api.messages.PlaybackStatus;
import tv.vizbee.screen.api.messages.VideoStatus;

/**
 * A player adapter for Android's ExoPlayer
 */
public class ExoPlayerAdapter extends VZBAdapter {

    protected ExoPlayer mExoPlayer;

    public ExoPlayerAdapter(ExoPlayer exoPlayer) {
        mExoPlayer = exoPlayer;
    }

    @Override
    public void play() {
        mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void seek(int position) {
        mExoPlayer.seekTo(position);
    }

    public VideoStatus getVideoStatus() {

        VideoStatus v = new VideoStatus();

        // set state
        if (mExoPlayer.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
            v.mPlaybackStatus = PlaybackStatus.BUFFERING;

        } else if ((mExoPlayer.getPlaybackState() == ExoPlayer.STATE_READY) && mExoPlayer.getPlayWhenReady()) {
            v.mPlaybackStatus = PlaybackStatus.PLAYING;

        } else if ((mExoPlayer.getPlaybackState() == ExoPlayer.STATE_READY) && !mExoPlayer.getPlayWhenReady()){
            v.mPlaybackStatus = PlaybackStatus.PAUSED_BY_USER;

        } else if (mExoPlayer.getPlaybackState() == ExoPlayer.STATE_ENDED) {
            v.mPlaybackStatus = PlaybackStatus.FINISHED;
        }

        // set position and duration
        v.mDuration = (int) mExoPlayer.getDuration();
        v.mPosition = (int) mExoPlayer.getCurrentPosition();

        return v;
    }
}
