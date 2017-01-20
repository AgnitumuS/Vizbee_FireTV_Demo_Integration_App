package tv.vizbee.firetvdemointegrationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tv.vizbee.firetvdemointegrationapp.exoPlayer.ExoPlayerActivity;
import tv.vizbee.firetvdemointegrationapp.model.Video;
import tv.vizbee.firetvdemointegrationapp.model.VideoCatalog;
import tv.vizbee.firetvdemointegrationapp.videoView.VideoViewPlayerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Video one
        Video videoOne = VideoCatalog.all.get(VideoCatalog.SINTEL);

        ImageView videoOneImage = (ImageView) findViewById(R.id.main_video_one_image);
        videoOneImage.setImageDrawable(ContextCompat.getDrawable(this, videoOne.getImageRes()));
        videoOneImage.setOnClickListener(this);

        TextView videoOneText = (TextView) findViewById(R.id.main_video_one_title);
        videoOneText.setText(videoOne.getTitle());

        // Video two
        Video videoTwo = VideoCatalog.all.get(VideoCatalog.BIG_BUCK_BUNNY);

        ImageView videoTwoImage = (ImageView) findViewById(R.id.main_video_two_image);
        videoTwoImage.setImageDrawable(ContextCompat.getDrawable(this, videoTwo.getImageRes()));
        videoTwoImage.setOnClickListener(this);

        TextView videoTwoText = (TextView) findViewById(R.id.main_video_two_title);
        videoTwoText.setText(videoTwo.getTitle());

        // Video three
        Video videoThree = VideoCatalog.all.get(VideoCatalog.TEARS_OF_STEEL);

        ImageView videoThreeImage = (ImageView) findViewById(R.id.main_video_three_image);
        videoThreeImage.setImageDrawable(ContextCompat.getDrawable(this, videoThree.getImageRes()));
        videoThreeImage.setOnClickListener(this);

        TextView videoThreeText = (TextView) findViewById(R.id.main_video_three_title);
        videoThreeText.setText(videoThree.getTitle());

        // Video four
        Video videoFour = VideoCatalog.all.get(VideoCatalog.ELEPHANTS_DREAM);

        ImageView videoFourImage = (ImageView) findViewById(R.id.main_video_four_image);
        videoFourImage.setImageDrawable(ContextCompat.getDrawable(this, videoFour.getImageRes()));
        videoFourImage.setOnClickListener(this);

        TextView videoFourText = (TextView) findViewById(R.id.main_video_four_title);
        videoFourText.setText(videoFour.getTitle());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();
    }

    private void handleIntent() {
        Bundle extras = getIntent().getExtras();
        if ((null != extras) && !extras.containsKey("duplicate")) {

            getIntent().putExtra("duplicate", true);

            String guid = null;
            int position = -1;

            if (extras.containsKey("guid")) {
                guid = extras.getString("guid");
            }
            if (extras.containsKey("position")) {
                position = extras.getInt("position");
            }
            if (null != guid) {
//                launchVideoViewPlayerActivity(guid, position);
                launchExoplayerActivity(guid, position);
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_video_one_image:
                launchVideoViewPlayerActivity(VideoCatalog.SINTEL, 0);
                break;

            case R.id.main_video_two_image:
                launchVideoViewPlayerActivity(VideoCatalog.BIG_BUCK_BUNNY, 0);
                break;

            case R.id.main_video_three_image:
                launchExoplayerActivity(VideoCatalog.TEARS_OF_STEEL, 0);
                break;

            case R.id.main_video_four_image:
                launchExoplayerActivity(VideoCatalog.ELEPHANTS_DREAM, 0);
                break;
        }
    }

    private void launchVideoViewPlayerActivity(String guid, int position) {
        Video video = VideoCatalog.all.get(guid);

        if (null != video) {
            Log.d(TAG, String.format("Launching VideoViewPlayerActivity with video: %s @ %d", video.getTitle(), position));
            startActivity(new Intent(this, VideoViewPlayerActivity.class)
                    .putExtra("video", video)
                    .putExtra("position", position)
            );
        } else {
            displayPlayError(guid);
        }
    }

    private void launchExoplayerActivity(String guid, int position) {
        Video video = VideoCatalog.all.get(guid);

        if (null != video) {
            Log.d(TAG, String.format("Launching ExoPlayerActivity with video: %s @ %d", video.getTitle(), position));
            startActivity(new Intent(this, ExoPlayerActivity.class)
                    .putExtra("video", video)
                    .putExtra("position", position)
            );
        } else {
            displayPlayError(guid);
        }
    }

    private void displayPlayError(String guid) {
        Toast.makeText(this, String.format("Video with GUID: %s is not supported by this demo app!", guid), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }
}
