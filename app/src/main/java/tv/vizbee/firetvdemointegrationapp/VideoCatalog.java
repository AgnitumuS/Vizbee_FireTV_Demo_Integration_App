package tv.vizbee.firetvdemointegrationapp;

import java.util.HashMap;

/**
 * Created by jesse on 1/18/17.
 */

public class VideoCatalog {

    public static final String BIG_BUCK_BUNNY = "bunny";
    public static final String TEARS_OF_STEEL = "tears";

    public static final HashMap<String, Video> all = new HashMap() {{
        put(BIG_BUCK_BUNNY, new Video("Big Buck Bunny", BIG_BUCK_BUNNY, "http://commondatastorage.googleapis.com/gtv-videos-bucket/big_buck_bunny_1080p.mp4", R.drawable.bigbuckbunny_720x1024));
        put(TEARS_OF_STEEL, new Video("Tears of Steel", TEARS_OF_STEEL, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/TearsOfSteel.m3u8", R.drawable.tearsofsteel_720x1024));
    }};
}
