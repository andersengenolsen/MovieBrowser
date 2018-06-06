package olsen.anders.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO representing a "MediaObject", meaning a Movie or TV shows downloaded from TMDB API.
 * Implementing Parcelable, so that it can be passed with intents.
 * <p>
 * Using BuilderPattern
 *
 * @author Anders Engen Olsen.
 * @see MediaObjectBuilder
 * @see Parcelable
 */

public class MediaObject implements Parcelable {

    private final int id;

    private final String title;
    private final String releaseDate;
    private final String[] genre;
    private final String rating;
    private final String language;
    private final String handling;
    private final String imagePath;
    private final String type;

    /**
     * Constructor.
     *
     * @param mediaObjectBuilder builder
     * @see MediaObjectBuilder
     */
    private MediaObject(MediaObjectBuilder mediaObjectBuilder) {
        this.id = mediaObjectBuilder.id;
        this.title = mediaObjectBuilder.title;
        this.releaseDate = mediaObjectBuilder.releaseDate;
        this.genre = mediaObjectBuilder.genre;
        this.rating = mediaObjectBuilder.rating;
        this.language = mediaObjectBuilder.language;
        this.handling = mediaObjectBuilder.handling;
        this.imagePath = mediaObjectBuilder.imagePath;
        this.type = mediaObjectBuilder.type;
    }

    /**
     * @return true if movie, false if tv
     */
    public boolean isMovie() {
        return type.equalsIgnoreCase("movie");
    }

    /**
     * @return true if tv, false if movie
     */
    public boolean isTv() {
        return type.equalsIgnoreCase("tv");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @return string-representation of the mediaobjects genres.
     */
    public String getGenre() {
        StringBuilder builder = new StringBuilder();

        for (String s : genre) {
            builder.append(s);
            builder.append(" ");
        }

        return builder.toString();
    }

    public String getRating() {
        return rating;
    }

    public String getLanguage() {
        return language;
    }

    public String getHandling() {
        return handling;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getType() {
        return type;
    }

    /**
     * Creator-constant which receives the object to parcel.
     */
    public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {

        public MediaObject createFromParcel(Parcel in) {
            return new MediaObject(in);
        }

        public MediaObject[] newArray(int size) {
            return new MediaObject[size];
        }
    };

    /**
     * Values must be read in same order as written to parcel!
     *
     * @param in Parcel to demarshal.
     * @see #writeToParcel(Parcel, int)
     */
    private MediaObject(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.genre = in.createStringArray();
        this.rating = in.readString();
        this.language = in.readString();
        this.handling = in.readString();
        this.imagePath = in.readString();
        this.type = in.readString();
    }

    /**
     * Writing to parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.releaseDate);
        dest.writeStringArray(this.genre);
        dest.writeString(this.rating);
        dest.writeString(this.language);
        dest.writeString(this.handling);
        dest.writeString(this.imagePath);
        dest.writeString(this.type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Builder for MediaObjects.
     */
    public static class MediaObjectBuilder {

        /**
         * Final, must be provided
         */
        private final int id;

        /**
         * Base URL for pics
         */
        private final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

        private String title;
        private String releaseDate;
        private String[] genre;
        private String rating;
        private String language;
        private String handling;
        private String imagePath;
        private String type;

        /**
         * Default constructor.
         *
         * @param id id that must be set
         */
        public MediaObjectBuilder(int id) {
            this.id = id;
        }

        public MediaObjectBuilder title(String title) {
            this.title = title;
            return this;
        }

        public MediaObjectBuilder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public MediaObjectBuilder genre(String[] genre) {
            this.genre = genre;
            return this;
        }

        public MediaObjectBuilder rating(String rating) {
            this.rating = rating;
            return this;
        }

        public MediaObjectBuilder language(String language) {
            this.language = language;
            return this;
        }

        public MediaObjectBuilder handling(String handling) {
            this.handling = handling;
            return this;
        }

        public MediaObjectBuilder imagePath(String imagePath) {
            this.imagePath = POSTER_BASE_URL + imagePath;
            return this;
        }

        public MediaObjectBuilder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * @return MediaObject Built mediaobject
         */
        public MediaObject build() {
            return new MediaObject(this);
        }
    }
}
