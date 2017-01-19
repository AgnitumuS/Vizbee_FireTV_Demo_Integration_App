package tv.vizbee.firetvdemointegrationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Video one
        Video videoOne = VideoCatalog.all.get(VideoCatalog.BIG_BUCK_BUNNY);

        ImageView videoOneImage = (ImageView) findViewById(R.id.main_video_one_image);
        videoOneImage.setImageDrawable(ContextCompat.getDrawable(this, videoOne.getImageRes()));
        videoOneImage.setOnClickListener(this);

        TextView videoOneText = (TextView) findViewById(R.id.main_video_one_title);
        videoOneText.setText(videoOne.getTitle());

        // Video two
        Video videoTwo = VideoCatalog.all.get(VideoCatalog.TEARS_OF_STEEL);

        ImageView videoTwoImage = (ImageView) findViewById(R.id.main_video_two_image);
        videoTwoImage.setImageDrawable(ContextCompat.getDrawable(this, videoTwo.getImageRes()));
        videoTwoImage.setOnClickListener(this);

        TextView videoTwoText = (TextView) findViewById(R.id.main_video_two_title);
        videoTwoText.setText(videoTwo.getTitle());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_video_one_image:
                launchVideoViewPlayerActivity(VideoCatalog.BIG_BUCK_BUNNY);
                break;

            case R.id.main_video_two_image:
                launchExoplayerActivity(VideoCatalog.TEARS_OF_STEEL);
                break;
        }
    }

    private void launchVideoViewPlayerActivity(String guid) {
        Video video = VideoCatalog.all.get(guid);

        startActivity(new Intent(this, VideoViewPlayerActivity.class)
                .putExtra("video", video));
    }

    private void launchExoplayerActivity(String guid) {
        Video video = VideoCatalog.all.get(guid);

        startActivity(new Intent(this, ExoPlayerActivity.class)
                .putExtra("video", video));
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }
}
