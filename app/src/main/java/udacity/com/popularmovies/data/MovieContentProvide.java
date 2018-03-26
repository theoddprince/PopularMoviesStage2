package udacity.com.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvide extends ContentProvider {

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_ID = 200;

    //Stage 2
    public static final int CODE_MOVIE_TRAILER = 222;
    public static final int CODE_MOVIE_TRAILER_ID = 300;
    public static final int CODE_MOVIE_REVIEWS = 400;

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private MovieDBHelper movieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        /* This URI is content://udacity.com.popularmovies/movies/ */
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, CODE_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", CODE_MOVIES_ID);

        //Stage 2
        matcher.addURI(authority, MoviesContract.PATH_MOVIE_TRAILERS, CODE_MOVIE_TRAILER);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE_TRAILERS + "/#", CODE_MOVIE_TRAILER_ID);

        matcher.addURI(authority, MoviesContract.PATH_MOVIE_REVIEWS, CODE_MOVIE_REVIEWS);

        return matcher;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        //String vote_count = value.getAsString(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            //Stage 2
            case CODE_MOVIE_TRAILER:
                db.beginTransaction();
                int rowsInserted2 = 0;
                try {
                    for (ContentValues value : values) {
                        //String vote_count = value.getAsString(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
                        long _id = db.insert(MoviesContract.MovieTrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted2 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted2;
            case CODE_MOVIE_REVIEWS :

                db.beginTransaction();
                int rowsInserted3 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MovieReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted3++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted3 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted3;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDbHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {
            case  CODE_MOVIES: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_MOVIES_ID:{

                String movieID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieID};

                cursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        MoviesContract.MovieEntry.COLUMN_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            //Stage 2
            case CODE_MOVIE_TRAILER_ID:{

                String trailerID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{trailerID};

                cursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieTrailerEntry.TABLE_NAME,
                        null,
                        MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case  CODE_MOVIE_TRAILER: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieTrailerEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_MOVIE_REVIEWS:{
                cursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieReviewEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
         /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIES:
                numRowsDeleted = movieDbHelper.getWritableDatabase().delete(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case CODE_MOVIE_TRAILER:
                numRowsDeleted = movieDbHelper.getWritableDatabase().delete(
                        MoviesContract.MovieTrailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_MOVIE_REVIEWS:
                numRowsDeleted = movieDbHelper.getWritableDatabase().delete(
                        MoviesContract.MovieReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;

    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
