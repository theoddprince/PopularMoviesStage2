package udacity.com.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.databinding.ActivityMovieDetailBinding;
import udacity.com.popularmovies.sync.MovieSyncTask;
import udacity.com.popularmovies.sync.ReviewsAsyncTask;
import udacity.com.popularmovies.sync.TrailersAsyncTask;
import udacity.com.popularmovies.utilities.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private TrailerAdapter tAdapter;
    private ReviewsAdapter rAdapter;
    private CheckBox favorite;

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH ,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    private static final int ID_MOVIE_LOADER = 999;
    private Uri mUri;
    private ActivityMovieDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movie_detail);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        RecyclerView tRecyclerView = mDetailBinding.recyclerviewTrailers;
        RecyclerView rRecyclerView =mDetailBinding.recyclerviewReviews;

        LinearLayoutManager layoutManagerTrailer =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        LinearLayoutManager layoutManagerReviews =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        tRecyclerView.setLayoutManager(layoutManagerTrailer);
        tRecyclerView.setHasFixedSize(true);
        tAdapter = new TrailerAdapter(this);
        tRecyclerView.setNestedScrollingEnabled(false);
        tRecyclerView.setAdapter(tAdapter);

        rRecyclerView.setLayoutManager(layoutManagerReviews);
        rRecyclerView.setHasFixedSize(true);
        rAdapter = new ReviewsAdapter(this);
        rRecyclerView.setNestedScrollingEnabled(false);
        rRecyclerView.setAdapter(rAdapter);

        favorite = mDetailBinding.checkBox;

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for MovieDetailActivity cannot be null");

        /* This connects our Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {

            case ID_MOVIE_LOADER:

                return new CursorLoader(this,
                        mUri,
                        MOVIE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        boolean cursorHasValidData = false;
        final ContentResolver favoriteContentResolver = getContentResolver();
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        //Set the Page Title as the Movie Title
        setTitle(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
        //Setting the poster with the help of picasso library
        String poster_str = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
        Picasso.with(this).load(this.getString(R.string.POSTER_MOVIES_URL) + poster_str).into(mDetailBinding.movieImageDetail);
        //Dividing with 2 because I set the number of stars to be 5 , step size 0.1
        Float ratebar = data.getFloat(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)) / 2 ;
        mDetailBinding.ratingTxt.setText(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)) + "/10");
        mDetailBinding.ratingBar2.setRating(ratebar);
        //Setting the Title of the Movie
        mDetailBinding.movieTitle.setText(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
        //Setting the Movie Overview
        mDetailBinding.movieDetail.setText(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
        //Setting release date
        mDetailBinding.releaseDateTxt.setText(data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));

        //Getting Trailers
        //Stage 2
        final String movieID = data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID));
        URL movieTrailersRequestUrl = NetworkUtils.getTrailersUrl(this, movieID) ;
        TrailersAsyncTask myTask = new TrailersAsyncTask(this,movieTrailersRequestUrl,tAdapter);
        myTask.execute();

        URL movieReviewsRequestUrl = NetworkUtils.getReviewsUrl(this , data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID)));
        ReviewsAsyncTask revTast = new ReviewsAsyncTask(this,movieReviewsRequestUrl , rAdapter);
        revTast.execute();

        if(isFavoriteMovieFound(favoriteContentResolver,movieID))
        {
            mDetailBinding.checkBox.setChecked(true);
        }

        final Context context = this;

        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked)
                {
                    //Add the movie to favorites when checked
                    ContentValues[] moviesContentValues = new ContentValues[1];
                    ArrayList<ContentValues> retVal = new ArrayList<>();
                    ContentValues map = new ContentValues();
                    if(data.moveToFirst()) {
                        do {
                            DatabaseUtils.cursorRowToContentValues(data, map);
                            retVal.add(map);
                        } while (data.moveToNext());
                    }

                    //I don't need the _id field in the favorite since I change between popular and top rated so the position of the movie poster will be same for both cases
                    //So when I try to insert into the DB only one unique ID will be inserted.
                    map.remove("_id");
                    moviesContentValues[0] = map;

                    favoriteContentResolver.bulkInsert(
                            MoviesContract.MovieFavoriteEntry.CONTENT_URI,
                            moviesContentValues);
                }
                else
                {
                    //Remove the Movie from Favorites if unchecked
                    String[] selectionArguments = new String[]{movieID};
                    favoriteContentResolver.delete(
                        MoviesContract.MovieFavoriteEntry.CONTENT_URI,
                            MoviesContract.MovieFavoriteEntry.COLUMN_ID + " = ? ",
                            selectionArguments);

                    try {
                        MovieSyncTask.syncMovies(context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private boolean isFavoriteMovieFound(ContentResolver favoriteContentResolver , String movieID)
    {
        Uri uriForMovieClicked = MoviesContract.MovieFavoriteEntry.buildMovieUriWithId(movieID);
        String[] projectionColumns = {MoviesContract.MovieFavoriteEntry.COLUMN_ID};
        final Cursor cursorReviews = favoriteContentResolver.query(
                uriForMovieClicked,
                projectionColumns,
                null,
                null,
                null);
        int found = cursorReviews.getCount();

        cursorReviews.close();

        if(found == 1)
            return true;
        else
            return false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
