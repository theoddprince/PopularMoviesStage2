package udacity.com.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.data.PopularMoviesPrefrences;
import udacity.com.popularmovies.utilities.NetworkUtils;
import udacity.com.popularmovies.utilities.OpenMoviesJsonUtils;

public class MovieSyncTask {

    synchronized public static void syncMovies(Context context) throws IOException {

        if(PopularMoviesPrefrences.checkSort(context) != "favorite" )
        {
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
        else //Handle Favorite Part
        {
            final ContentResolver favoriteContentResolver = context.getContentResolver();
            Uri uriForMovieClicked = MoviesContract.MovieFavoriteEntry.CONTENT_URI;
            String[] projectionColumns = {MoviesContract.MovieFavoriteEntry.COLUMN_ID};
            ArrayList<ContentValues> retVal = new ArrayList<>();

            final Cursor cursorReviews = favoriteContentResolver.query(
                    uriForMovieClicked,
                    projectionColumns,
                    null,
                    null,
                    null);

            ContentValues[] moviesContentValues = new ContentValues[cursorReviews.getCount()];
            while (cursorReviews.moveToNext()) {
                ContentValues map = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursorReviews, map);
                retVal.add(map);
            }

            for(int i=0; i<retVal.size(); i++) {
                moviesContentValues[i] = retVal.get(i);
            }

               /* Delete old movie data because we don't need to keep multiple movies' data */
            favoriteContentResolver.delete(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    null);

                    /* Insert our new movie data into Movies's ContentProvider */
            favoriteContentResolver.bulkInsert(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    moviesContentValues);

            cursorReviews.close();
        }
    }
}
