package udacity.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import udacity.com.popularmovies.data.MoviesContract;

/**
 * Created by AMIRMAT on 3/26/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MovieAdapterViewHolder> {
    private Cursor mCursor;
    //final private TrailerAdapter.MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;

    public ReviewsAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        layoutId = R.layout.reviews_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new ReviewsAdapter.MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
       // String trailer_key = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY));
        //String trailer_name = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_NAME));
        //holder.movie_trailer_name.setText(trailer_name);
      //  Picasso.with(mContext).load(buildThumbnailUrl(trailer_key)).into(holder.movie_trailer_thumb);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       // final TextView movie_trailer_name;
       // final ImageView movie_trailer_thumb;

        MovieAdapterViewHolder(View view) {
            super(view);
            //movie_trailer_name = view.findViewById(R.id.trailer_name);
           // movie_trailer_thumb = view.findViewById(R.id.movie_trailer_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            //Intent webIntent = new Intent(Intent.ACTION_VIEW,
            //        Uri.parse(youTubeLink + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY))));

            //mContext.startActivity(webIntent);
        }
    }
}
