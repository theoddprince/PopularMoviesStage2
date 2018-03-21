package udacity.com.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import udacity.com.popularmovies.data.MoviesContract;
import udacity.com.popularmovies.data.PopularMoviesPrefrences;
import udacity.com.popularmovies.utilities.MoviesSyncUtils;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler{

    /*
   * The columns of data that we are interested in displaying within our MainActivity's list of
   * Movie data.
   */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_VOTE_COUNT,
            MoviesContract.MovieEntry.COLUMN_ID ,
            MoviesContract.MovieEntry.COLUMN_VIDEO,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int INDEX_MOVIE_ID = 2;
    private static final int ID_MOVIE_LOADER = 99;
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;
    private GridLayoutManager gridManager;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String KEY_SORT_CHANGED = "sort_state";
    private static Bundle mBundleRecyclerViewState;
    private String sort;
    private static Bundle mBundleSortChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView =  findViewById(R.id.recyclerview_forecast);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

         gridManager =
              new GridLayoutManager(this ,2);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(gridManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mAdapter);
        showLoading();

        ViewTreeObserver viewTreeObserver = mRecyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                changePortrait();
            }
        });

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ID_MOVIE_LOADER, null, this);

        MoviesSyncUtils.initialize(this);

        setSortTitle();
    }

    //In case we rotate the phone the number of columns shown will be 4 in land mode or 2 in the normal mode .
    private void changePortrait() {
        int columns =2 ;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                columns = 2;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                columns = 4;
                break;
        }
        ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(columns);
    }

    private void showMoviesDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        /* Then, hide the movies data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.my_option_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_MOVIE_LOADER:

                showLoading();

                Uri movieQueryUri = MoviesContract.MovieEntry.CONTENT_URI;
                String selection = MoviesContract.MovieEntry.getSqlSelectMoviePosters();

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //I don't want to reset the position to be 0 , just keep the current state .
        //onStop will work when we click on the Home Button or when we move between activities.
        //In case one of the mentioned above is done we restore the recycler state and reload the data without loosing the position :) .
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);

            //In case the sorting is changed then set the position to 0 and start scrolling from start.
            if(mBundleSortChanged != null)
            {
                String lastSort = mBundleSortChanged.getString(KEY_SORT_CHANGED);
                if (lastSort != sort)
                {
                    if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                    mRecyclerView.smoothScrollToPosition(mPosition);
                }
            }

            mAdapter.swapCursor(data);
            if (data.getCount() != 0) showMoviesDataView();
        }
        else
        {
            mAdapter.swapCursor(data);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);
            if (data.getCount() != 0) showMoviesDataView();
        }

        setSortTitle();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(String movieId) {
        Intent movieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        Uri uriForMovieClicked = MoviesContract.MovieEntry.buildMovieUriWithId(movieId);
        movieDetailIntent.setData(uriForMovieClicked);
        startActivity(movieDetailIntent);
    }

    //Change the mainactivity title to be as the selected Sort .
    private void setSortTitle() {
        sort = PopularMoviesPrefrences.getSorting(this);
        setTitle(sort);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

        mBundleSortChanged = new Bundle();
        String sortState = sort;
        mBundleSortChanged.putString(KEY_SORT_CHANGED , sortState);
    }

}
