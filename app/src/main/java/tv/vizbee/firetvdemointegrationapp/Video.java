package tv.vizbee.firetvdemointegrationapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Created by jesse on 1/18/17.
 */

public class Video implements Parcelable {

    private String title;
    private String guid;
    private String videoURL;
    private @DrawableRes int imageRes;

    public Video(String title, String guid, String videoURL, int imageRes) {
        this.title = title;
        this.guid = guid;
        this.videoURL = videoURL;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getGuid() {
        return guid;
    }

    public String getVideoURL() {
        return videoURL;
    }

    @DrawableRes
    public int getImageRes() {
        return imageRes;
    }

    protected Video(Parcel in) {
        this.title = in.readString();
        this.guid = in.readString();
        this.videoURL = in.readString();
        this.imageRes = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(guid);
        dest.writeString(videoURL);
        dest.writeInt(imageRes);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
