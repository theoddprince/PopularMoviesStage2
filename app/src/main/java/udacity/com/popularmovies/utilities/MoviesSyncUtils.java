package udacity.com.popularmovies.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.sync.MoviesSyncIntentService;

public class MoviesSyncUtils {

    //private static final int SYNC_INTERVAL_HOURS = 3;
    //private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    //private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    //private static final String MOIVE_SYNC_TAG = "movie-sync";

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MoviesSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        sInitialized = true;

        /*
         * This method call triggers Sunshine to create its task to synchronize movie data
         * periodically.
         */
       // scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                /* URI for every row of movie data in our movie table*/
                Uri QueryUri = MoviesContract.MovieEntry.CONTENT_URI;
                String[] projectionColumns = {MoviesContract.MovieEntry._ID};
                //String selectionStatement = MoviesContract.MovieEntry.getSqlSelectMoviePosters();


                /* Here, we perform the query to check to see if we have any movie data */
                Cursor cursor = context.getContentResolver().query(
                        QueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Make sure to close the Cursor to avoid memory leaks! */
                cursor.close();
            }
        });

        /* Finally, once the thread is prepared, fire it off to perform our checks. */
        checkForEmpty.start();
    }

}
