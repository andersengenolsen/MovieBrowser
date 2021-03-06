package anders.olsen.moviebrowser.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

import anders.olsen.moviebrowser.R;
import anders.olsen.moviebrowser.model.Genre;
import anders.olsen.moviebrowser.model.MediaObject;

import static anders.olsen.moviebrowser.activities.BaseActivity.SHARED_PREF_GENRES;
import static anders.olsen.moviebrowser.constants.JsonConstants.FIRST_AIR_DATE;
import static anders.olsen.moviebrowser.constants.JsonConstants.GENRES;
import static anders.olsen.moviebrowser.constants.JsonConstants.GENRE_IDS;
import static anders.olsen.moviebrowser.constants.JsonConstants.ID;
import static anders.olsen.moviebrowser.constants.JsonConstants.JSON_RESULT;
import static anders.olsen.moviebrowser.constants.JsonConstants.MEDIA_TYPE;
import static anders.olsen.moviebrowser.constants.JsonConstants.NAME;
import static anders.olsen.moviebrowser.constants.JsonConstants.ORIGINAL_LANGUAGE;
import static anders.olsen.moviebrowser.constants.JsonConstants.OVERVIEW;
import static anders.olsen.moviebrowser.constants.JsonConstants.POSTER_PATH;
import static anders.olsen.moviebrowser.constants.JsonConstants.RELEASE_DATE;
import static anders.olsen.moviebrowser.constants.JsonConstants.STATUS_CODE;
import static anders.olsen.moviebrowser.constants.JsonConstants.STATUS_CODE_CREATED;
import static anders.olsen.moviebrowser.constants.JsonConstants.STATUS_CODE_DELETED;
import static anders.olsen.moviebrowser.constants.JsonConstants.STATUS_CODE_UPDATED;
import static anders.olsen.moviebrowser.constants.JsonConstants.TITLE;
import static anders.olsen.moviebrowser.constants.JsonConstants.VOTE_AVERAGE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.MEDIA_TYPE_MOVIE;
import static anders.olsen.moviebrowser.constants.TmdbConstants.MEDIA_TYPE_TV;

/**
 * Class which parse JSON-data from the TMDB API.
 * Package-protected, only loader-classes need access.
 *
 * @author Anders Engen Olsen
 * @see TmdbManager
 */
class JsonParser {

    private final static String LOG_TAG = JsonParser.class.getSimpleName();

    /**
     * Activity context
     */
    private Context context;

    /**
     * Constructor.
     *
     * @param context Activity context
     */
    JsonParser(Context context) {
        this.context = context;
    }

    /**
     * Obtaining an ArrayList with MediaObjects from JSON.
     * <p>
     * The fields can vary between TV and MOVIES.
     *
     * @param json json input
     * @param type Type mediaobject, movie / tv. Null if not known
     * @return ArrayList<MediaObject>
     */
    ArrayList<MediaObject> parseMediaObjects(JSONObject json, String type) {

        // Fields to fetch from JSON
        int id;
        String poster, rating, language, plot, title, releaseDate, mediaType;

        ArrayList<MediaObject> mediaObjects = new ArrayList<>();

        // Map with genres from Shared Prefs
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_GENRES,
                Context.MODE_PRIVATE);
        Map<String, ?> genreMap = prefs.getAll();

        try {
            JSONArray jsonArr = json.getJSONArray(JSON_RESULT);

            for (int i = 0; i < jsonArr.length(); i++) {

                JSONObject object = jsonArr.getJSONObject(i);

                // If type is null, trying to fetch type from JSON
                if (type == null) {
                    mediaType = object.getString(MEDIA_TYPE);
                } else {
                    mediaType = type;
                }

                // Getting correct fields, whether movie / tv.
                // Can also be company, person etc. Skipping if so.
                if (mediaType.equals(MEDIA_TYPE_MOVIE)) {
                    releaseDate = object.getString(RELEASE_DATE);
                    title = object.getString(TITLE);
                } else if (mediaType.equals(MEDIA_TYPE_TV)) {
                    releaseDate = object.getString(FIRST_AIR_DATE);
                    title = object.getString(NAME);
                } else
                    continue;

                id = object.getInt(ID);
                poster = object.getString(POSTER_PATH);
                rating = object.getString(VOTE_AVERAGE);
                language = object.getString(ORIGINAL_LANGUAGE);
                plot = object.getString(OVERVIEW);

                JSONArray genreArr = object.getJSONArray(GENRE_IDS);
                String[] genres = new String[genreArr.length()];

                // Genres
                if (!genreMap.isEmpty()) {
                    for (int n = 0; n < genreArr.length(); n++) {
                        int genreId = genreArr.getInt(n);

                        for (Map.Entry<String, ?> entry : genreMap.entrySet()) {
                            if (genreId == Integer.parseInt(entry.getKey()))
                                genres[n] = (String) entry.getValue();
                        }
                    }
                }

                MediaObject movie = new MediaObject.MediaObjectBuilder(id)
                        .imagePath(poster)
                        .rating(rating)
                        .language(language)
                        .handling(plot)
                        .releaseDate(releaseDate)
                        .title(title)
                        .genre(genres)
                        .type(mediaType)
                        .build();
                mediaObjects.add(movie);
            }
        } catch (JSONException err) {
            Toast.makeText(context, context.getString(R.string.error_json), Toast.LENGTH_SHORT).show();
        }
        return mediaObjects;
    }

    /**
     * Processing JSONobject which contains a list of genres.
     * Genres are saved in SharedPrefs, and also returned as an ArrayList.
     *
     * @param json jsonobject with genres
     */
    ArrayList<Genre> parseGenres(JSONObject json) {
        // SharedPrefs
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREF_GENRES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // ArrayList
        ArrayList<Genre> genreList = new ArrayList<>();

        try {
            JSONArray jsonArr = json.getJSONArray(GENRES);

            for (int i = 0; i < jsonArr.length(); i++) {

                JSONObject object = jsonArr.getJSONObject(i);

                Genre genre = new Genre(object.getInt(ID), object.getString(NAME));
                editor.putString(String.valueOf(genre.getId()), genre.getGenre());
                genreList.add(genre);
            }
        } catch (JSONException err) {
            Toast.makeText(context, context.getString(R.string.error_json), Toast.LENGTH_SHORT).show();
        } finally {
            editor.apply();
        }

        return genreList;
    }

    /**
     * Fetching token from json
     *
     * @param json json with token
     * @return token, null if none found
     */
    String parseToken(JSONObject json) {
        final String SUCCESS = "success";
        final String REQUEST_TOKEN = "request_token";

        try {
            boolean success = json.getBoolean(SUCCESS);

            if (success) {
                return json.getString(REQUEST_TOKEN);
            }

        } catch (JSONException err) {
            Toast.makeText(context, context.getString(R.string.error_json),
                    Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    /**
     * Fetching a Session ID from JSON
     *
     * @param json json with session id
     * @return session id, null if none found
     */
    String parseSession(JSONObject json) {
        final String SUCCESS = "success";
        final String SESSION_ID = "session_id";

        try {
            boolean success = json.getBoolean(SUCCESS);

            if (success) {
                return json.getString(SESSION_ID);
            }

        } catch (JSONException err) {
            Toast.makeText(context, context.getString(R.string.error_json),
                    Toast.LENGTH_SHORT).show();
        }

        return null;

    }

    /**
     * Validating response when inserting / updating user lists.
     *
     * @param json json
     * @return true if valid response
     */
    boolean parseJsonResponse(JSONObject json) {
        try {
            return json.getInt(STATUS_CODE) == STATUS_CODE_UPDATED ||
                    json.getInt(STATUS_CODE) == STATUS_CODE_CREATED ||
                    json.getInt(STATUS_CODE) == STATUS_CODE_DELETED;
        } catch (JSONException err) {
            Toast.makeText(context, context.getString(R.string.error_json),
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    /**
     * Parsing JSON, extracting youtube-url to trailer.
     *
     * @param json json
     * @return Url to trailer
     * @throws NoSuchElementException trailer not found
     */
    String parseTrailerID(JSONObject json) {

        try {
            JSONArray jsonArr = json.getJSONArray(JSON_RESULT);

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);

                String site = jsonObject.getString("site");
                String type = jsonObject.getString("type");

                if (site.equalsIgnoreCase("youtube")
                        && (type.equalsIgnoreCase("trailer")
                        || type.equalsIgnoreCase("teaser"))) {
                    return jsonObject.getString("key");
                }
            }
        } catch (JSONException err) {
            Toast.makeText(context, R.string.trailer_not_found,
                    Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
