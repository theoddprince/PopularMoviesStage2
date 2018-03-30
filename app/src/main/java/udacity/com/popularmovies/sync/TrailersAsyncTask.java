package udacity.com.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import udacity.com.popularmovies.TrailerAdapter;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.utilities.NetworkUtils;
import udacity.com.popularmovies.utilities.OpenMoviesJsonUtils;

/**
 * Created by AMIRMAT on 3/16/2018.
 */

public class TrailersAsyncTask extends AsyncTask {

    private Context context;
    private URL movieTrailersRequestUrl;
    private TrailerAdapter tAdapter;

    public TrailersAsyncTask(Context context,URL movieTrailersRequestUrl , TrailerAdapter tAdapter) {
        this.context = context;
        this.movieTrailersRequestUrl = movieTrailersRequestUrl;
        this.tAdapter = tAdapter;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieTrailersRequestUrl,context);

            ContentValues[] MovieTrailers = OpenMoviesJsonUtils
                    .getMoviesTrailersFromJson(context, jsonMoviesResponse);

            if (MovieTrailers != null && MovieTrailers.length != 0) {
                ContentResolver trailerContentResolver = context.getContentResolver();

                             /* Delete old movie data because we don't need to keep multiple movies' data */
                trailerContentResolver.delete(
                        MoviesContract.MovieTrailerEntry.CONTENT_URI,
                        null,
                        null);

                              /* Insert our new movie data into Movies's ContentProvider */
                trailerContentResolver.bulkInsert(
                        MoviesContract.MovieTrailerEntry.CONTENT_URI,
                        MovieTrailers);


                Uri QueryUri = MoviesContract.MovieTrailerEntry.CONTENT_URI;
                String[] projectionColumns = {MoviesContract.MovieTrailerEntry._ID};

                final Cursor cursorTrailer = context.getContentResolver().query(
                        QueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                return cursorTrailer;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Cursor temp = (Cursor) o ;
        tAdapter.swapCursor(temp);
    }
}
