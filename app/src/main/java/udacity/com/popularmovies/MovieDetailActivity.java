package udacity.com.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.databinding.ActivityMovieDetailBinding;
import udacity.com.popularmovies.sync.TrailersAsyncTask;
import udacity.com.popularmovies.utilities.NetworkUtils;
import udacity.com.popularmovies.utilities.OpenMoviesJsonUtils;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    String posterMovieUrl = "http://image.tmdb.org/t/p/w500";

    private TrailerAdapter tAdapter;
    private RecyclerView tRecyclerView;
    private URL movieTrailersRequestUrl;

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

        tRecyclerView = mDetailBinding.recyclerviewTrailers;
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        tRecyclerView.setLayoutManager(layoutManager);
        tRecyclerView.setHasFixedSize(true);
        tAdapter = new TrailerAdapter(this);
        tRecyclerView.setNestedScrollingEnabled(false);
        tRecyclerView.setAdapter(tAdapter);

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
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
        Picasso.with(this).load(posterMovieUrl + poster_str).into(mDetailBinding.movieImageDetail);
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
        movieTrailersRequestUrl = NetworkUtils.getTrailersUrl(this, data.getString(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID))) ;
        TrailersAsyncTask myTask = new TrailersAsyncTask(this,movieTrailersRequestUrl,tAdapter);
        myTask.execute();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
