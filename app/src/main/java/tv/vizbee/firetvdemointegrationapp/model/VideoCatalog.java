package tv.vizbee.firetvdemointegrationapp.model;

import java.util.HashMap;

import tv.vizbee.firetvdemointegrationapp.R;

public class VideoCatalog {

    public static final String BIG_BUCK_BUNNY = "bigbuck";
    public static final String TEARS_OF_STEEL = "tears";
    public static final String SINTEL = "sintel";
    public static final String ELEPHANTS_DREAM = "elephants";

    public static final HashMap<String, Video> all = new HashMap() {{
        put(SINTEL, new Video("Sintel", SINTEL, "http://peach.themazzone.com/durian/movies/sintel-2048-surround.mp4", R.drawable.sintel_720x1024));
        put(BIG_BUCK_BUNNY, new Video("Big Buck Bunny", BIG_BUCK_BUNNY, "http://commondatastorage.googleapis.com/gtv-videos-bucket/big_buck_bunny_1080p.mp4", R.drawable.bigbuckbunny_720x1024));
        put(TEARS_OF_STEEL, new Video("Tears of Steel", TEARS_OF_STEEL, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/TearsOfSteel.m3u8", R.drawable.tearsofsteel_720x1024));
        put(ELEPHANTS_DREAM, new Video("Elephant's Dream", ELEPHANTS_DREAM, "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/ElephantsDream.m3u8", R.drawable.elephantdream_720x1024));
    }};
}
