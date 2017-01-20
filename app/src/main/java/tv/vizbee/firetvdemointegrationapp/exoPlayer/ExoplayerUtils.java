package tv.vizbee.firetvdemointegrationapp.exoPlayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import tv.vizbee.firetvdemointegrationapp.BuildConfig;

public class ExoplayerUtils {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    public static MediaSource buildMediaSource(Context context, Uri uri,
                                               DataSource.Factory mediaDataSourceFactory,
                                               Handler handler,
                                               String overrideExtension,
                                               AdaptiveMediaSourceEventListener adaptiveListener,
                                               ExtractorMediaSource.EventListener extractorListener) {

        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());

        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(context, false), new DefaultSsChunkSource.Factory(mediaDataSourceFactory), handler, adaptiveListener);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(context, false), new DefaultDashChunkSource.Factory(mediaDataSourceFactory), handler, adaptiveListener);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, handler, adaptiveListener);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), handler, extractorListener);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    /* Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *     DataSource factory.
     * @return A new DataSource factory.
     */
    public static DataSource.Factory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
        return (buildDataSourceFactory(context, useBandwidthMeter ? BANDWIDTH_METER : null));
    }

    public static DataSource.Factory buildDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, buildHttpDataSourceFactory(context, bandwidthMeter));
    }

    public static HttpDataSource.Factory buildHttpDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(context), bandwidthMeter);
    }

    public static boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

    public static String getUserAgent(Context context) {
        return Util.getUserAgent(context, BuildConfig.APPLICATION_ID);
    }
}
