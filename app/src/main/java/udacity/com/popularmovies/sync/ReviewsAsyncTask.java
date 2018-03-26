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

import udacity.com.popularmovies.ReviewsAdapter;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.utilities.NetworkUtils;
import udacity.com.popularmovies.utilities.OpenMoviesJsonUtils;

/**
 * Created by AMIRMAT on 3/26/2018.
 */

public class ReviewsAsyncTask extends AsyncTask {

    private Context context;
    private URL movieReviewsRequestUrl;
    private ReviewsAdapter rAdapter;


    public ReviewsAsyncTask(Context context,URL movieTrailersRequestUrl , ReviewsAdapter rAdapter) {
        this.context = context;
        this.movieReviewsRequestUrl = movieTrailersRequestUrl;
        this.rAdapter = rAdapter;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewsRequestUrl,context);

            ContentValues[] MovieReviews = OpenMoviesJsonUtils
                    .getMoviesReviewsFromJson(context, jsonMoviesResponse);

            if (MovieReviews != null && MovieReviews.length != 0) {
                ContentResolver reviewContentResolver = context.getContentResolver();

                             /* Delete old movie data because we don't need to keep multiple movies' data */
                reviewContentResolver.delete(
                        MoviesContract.MovieReviewEntry.CONTENT_URI,
                        null,
                        null);

                              /* Insert our new movie data into Movies's ContentProvider */
                reviewContentResolver.bulkInsert(
                        MoviesContract.MovieReviewEntry.CONTENT_URI,
                        MovieReviews);


                Uri QueryUri = MoviesContract.MovieReviewEntry.CONTENT_URI;
                String[] projectionColumns = {MoviesContract.MovieReviewEntry._ID};

                final Cursor cursorReviews = context.getContentResolver().query(
                        QueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                return cursorReviews;
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
        rAdapter.swapCursor(temp);
    }
}
