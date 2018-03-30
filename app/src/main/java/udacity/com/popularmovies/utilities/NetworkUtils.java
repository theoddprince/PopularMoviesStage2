package udacity.com.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import udacity.com.popularmovies.R;
import udacity.com.popularmovies.data.PopularMoviesPrefrences;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static URL getUrl(Context context) {

        final String MOVIE_API_KEY = "?api_key=" + context.getString(R.string.API_KEY);
        return buildUrl( context.getString(R.string.MOVIES_URL)+ PopularMoviesPrefrences.checkSort(context) + MOVIE_API_KEY);
    }

    //Stage 2
    public  static URL getTrailersUrl(Context context , String movieID)
    {
        final String MOVIE_API_KEY = "?api_key=" + context.getString(R.string.API_KEY);
        return buildUrl(context.getString(R.string.MOVIES_URL)+ movieID +"/videos" + MOVIE_API_KEY);
    }
    //Stage 2
    public  static URL getReviewsUrl(Context context , String movieID)
    {
        final String MOVIE_API_KEY = "?api_key=" + context.getString(R.string.API_KEY);
        return buildUrl(context.getString(R.string.MOVIES_URL)+ movieID +"/reviews" + MOVIE_API_KEY);
    }

    private static URL buildUrl(String moviesURL) {
        Uri moviesQueryUri = Uri.parse(moviesURL).buildUpon()
                .build();
        try {
            URL movieQueryUrl = new URL(moviesQueryUri.toString());
            Log.v(TAG, "URL: " + movieQueryUrl);
            return movieQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url,Context context) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        }
         finally {
            urlConnection.disconnect();
        }
    }

}
