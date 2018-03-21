package udacity.com.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.utilities.NetworkUtils;
import udacity.com.popularmovies.utilities.OpenMoviesJsonUtils;

public class MovieSyncTask {

    synchronized public static void syncMovies(Context context) throws IOException {

        URL moviesRequestUrl = NetworkUtils.getUrl(context);
        String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl,context);


        /* Parse the JSON into a list of movie values */
        try {
            ContentValues[] MoviesValues = OpenMoviesJsonUtils
                    .getMoviesContentValuesFromJson(context, jsonMoviesResponse);

            if (MoviesValues != null && MoviesValues.length != 0) {
                ContentResolver moviesContentResolver = context.getContentResolver();

                 /* Delete old movie data because we don't need to keep multiple movies' data */
               moviesContentResolver.delete(
                                 MoviesContract.MovieEntry.CONTENT_URI,
                                null,
                                null);


                /* Insert our new movie data into Movies's ContentProvider */
                moviesContentResolver.bulkInsert(
                        MoviesContract.MovieEntry.CONTENT_URI,
                        MoviesValues);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
