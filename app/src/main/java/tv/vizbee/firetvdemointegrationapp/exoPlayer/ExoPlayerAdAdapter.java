package tv.vizbee.firetvdemointegrationapp.exoPlayer;

/**
 * Created by jesse on 3/16/17.
 */

import android.util.Log;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsManager;

import tv.vizbee.screen.api.adapter.VZBAdapter;
import tv.vizbee.screen.api.messages.AdStatus;
import tv.vizbee.screen.api.messages.PlaybackStatus;

/**
 * Created by jesse on 3/14/17.
 */

public class ExoPlayerAdAdapter extends VZBAdapter implements AdEvent.AdEventListener {

    private static final String LOG_TAG = "ExoPlayerAdAdapter";

    private AdsManager mAdsManager;
    private AdStatus mAdStatus;
    private String mGuid;

    public ExoPlayerAdAdapter(AdsManager mAdsManager, String guid) {
        this.mAdsManager = mAdsManager;
        this.mAdsManager.addAdEventListener(this);
        this.mAdStatus = new AdStatus();
        this.mGuid = guid;
    }

    @Override
    public AdStatus getAdStatus() {

        if (mAdsManager != null) {
            if (mAdsManager.getCurrentAd() != null) {
                mAdStatus.mDuration = (int) (mAdsManager.getAdProgress().getDuration() * 1000);
                mAdStatus.mPosition = (int) (mAdsManager.getAdProgress().getCurrentTime() * 1000);

                Log.v(LOG_TAG, "Get ad status: " + mAdStatus);
                return mAdStatus;
            }
        }

        Log.w(LOG_TAG, "Get ad status: Ad not playing");

        return super.getAdStatus();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        switch (adEvent.getType()) {

            case AD_BREAK_STARTED:
                mAdStatus.mQuartile = -1;
                mAdStatus.mPlaybackStatus = PlaybackStatus.LOADING;
                getAdStatusListener().onAdPodStart();
                break;

            case STARTED:
                mAdStatus.mQuartile = -1;
                mAdStatus.mPlaybackStatus = PlaybackStatus.PLAYING;
                getAdStatusListener().onAdStart(mGuid);
                break;

            case FIRST_QUARTILE:
                mAdStatus.mQuartile = 1;
                mAdStatus.mPlaybackStatus = PlaybackStatus.PLAYING;
                getAdStatusListener().onAdFirstQuartile();
                break;

            case MIDPOINT:
                mAdStatus.mQuartile = 2;
                mAdStatus.mPlaybackStatus = PlaybackStatus.PLAYING;
                getAdStatusListener().onAdMidPoint();
                break;

            case THIRD_QUARTILE:
                mAdStatus.mQuartile = 3;
                mAdStatus.mPlaybackStatus = PlaybackStatus.PLAYING;
                getAdStatusListener().onAdThirdQuartile();
                break;

            case COMPLETED:
                mAdStatus.mQuartile = 4;
                mAdStatus.mPlaybackStatus = PlaybackStatus.FINISHED;
                getAdStatusListener().onAdCompleted();
                break;

            case AD_BREAK_ENDED:
                mAdStatus.mQuartile = 4;
                getAdStatusListener().onAdPodEnd();
                mAdStatus.mPlaybackStatus = PlaybackStatus.FINISHED;
                break;
        }
    }
}

