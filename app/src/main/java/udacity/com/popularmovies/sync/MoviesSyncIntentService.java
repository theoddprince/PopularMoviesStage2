package udacity.com.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MoviesSyncIntentService extends IntentService {

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            MovieSyncTask.syncMovies(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
