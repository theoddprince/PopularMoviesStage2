package udacity.com.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import udacity.com.popularmovies.data.MoviesContract;

/**
 * Created by AMIRMAT on 3/26/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MovieAdapterViewHolder> {
    private Cursor mCursor;
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
        String review_author = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_REVIEW_AUTHOR));
        String review_content = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieReviewEntry.COLUMN_REVIEW_CONTENT));
        holder.movie_review_content.setText(review_content);
        holder.movie_review_author.setText(review_author);
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
        final TextView movie_review_content;
        final TextView movie_review_author;

        MovieAdapterViewHolder(View view) {
            super(view);
            movie_review_content = view.findViewById(R.id.reviews_txt);
            movie_review_author = view.findViewById(R.id.author_txt);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
        }
    }
}
