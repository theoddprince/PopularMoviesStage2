package udacity.com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import udacity.com.popularmovies.data.MoviesContract;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MovieAdapterViewHolder> {

    private Cursor mCursor;
    private final Context mContext;

    public TrailerAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId;
        layoutId = R.layout.trailer_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new TrailerAdapter.MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String trailer_key = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY));
        String trailer_name = mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_NAME));
        holder.movie_trailer_name.setText(trailer_name);
        Picasso.with(mContext).load(buildThumbnailUrl(trailer_key)).into(holder.movie_trailer_thumb);
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

    private String buildThumbnailUrl(String videoKey) {
        return "https://img.youtube.com/vi/" + videoKey + "/hqdefault.jpg";
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView movie_trailer_name;
        final ImageView movie_trailer_thumb;

        MovieAdapterViewHolder(View view) {
            super(view);
            movie_trailer_name = view.findViewById(R.id.trailer_name);
            movie_trailer_thumb = view.findViewById(R.id.movie_trailer_image);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mContext.getString(R.string.YOUTUBE_LINK) + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY))));

            mContext.startActivity(webIntent);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            String mimeType = "text/html";
            String title = "Share Trailer";

            ShareCompat.IntentBuilder
                    .from((Activity) mContext)
                    .setChooserTitle(title)
                    .setType(mimeType)
                    .setHtmlText(mContext.getString(R.string.YOUTUBE_LINK) + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_KEY)))
            .startChooser();

            return true;
        }
    }
}
