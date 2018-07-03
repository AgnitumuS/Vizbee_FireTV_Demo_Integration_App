package tv.vizbee.firetvdemointegrationapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import tv.vizbee.screen.api.Vizbee;
import tv.vizbee.screen.api.VizbeeOptions;
import tv.vizbee.screen.api.adapter.IAppAdapter;
import tv.vizbee.screen.api.messages.VideoInfo;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ---------------------------
        // [BEGIN] Vizbee Integration
        // ---------------------------

        VizbeeOptions options = new VizbeeOptions.Builder().build();

        Vizbee.getInstance().init(this, "vzb2000002", new IAppAdapter() {
            @Override
            public void start(Activity currentActivity, VideoInfo video, int position) {

                // This functionality is similar to invoking a deep link from Amazon FireTV.
                // There are many ways of customizing this adapter method to your app.

                // One common approach is -
                // 1. Send intent to the main activity (so UI is cleared for new video to be presented)
                // 2. Let main activity route the intent per existing app deeplink rules.

                Intent i = new Intent(currentActivity, MainActivity.class)
                        .putExtra("guid", video.getGUID())
                        .putExtra("position", position);
                currentActivity.startActivity(i);
            }
        }, options);

        // ---------------------------
        // [END] Vizbee Integration
        // ---------------------------

    }
}
