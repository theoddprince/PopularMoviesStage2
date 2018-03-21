package udacity.com.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import udacity.com.popularmovies.data.MoviesContract;

public class MovieDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "moviesDB.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 3;

    // Constructor
    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID                + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_ID    + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_VIDEO + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL );";

        db.execSQL(CREATE_TABLE);

        final String CREATE_TABLE_TRAILERS = "CREATE TABLE "  + MoviesContract.MovieTrailerEntry.TABLE_NAME + " (" +
                MoviesContract.MovieTrailerEntry._ID                + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_ISO    + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_ISO2 + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY  + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_SIZE + " TEXT NOT NULL," +
                MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_TYPE + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE_TRAILERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieTrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
