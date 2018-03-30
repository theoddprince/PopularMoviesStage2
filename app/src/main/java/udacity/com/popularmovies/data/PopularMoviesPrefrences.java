package udacity.com.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import udacity.com.popularmovies.R;

public class PopularMoviesPrefrences {

    public static final String PREF_SORT_POPULAR = "Popular";
    public static final String PREF_SORT_TOPRATED = "Top Rated";

    public static String getSorting(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keySorting = context.getString(R.string.pref_sort_key);
        String defaultSort = context.getString(R.string.pref_sort_default);
        return sp.getString(keySorting, defaultSort);
    }

    public static String checkSort(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultSort= context.getString(R.string.pref_sort_default);

        String sort = sp.getString("Sort", defaultSort);

        if (sort.equals(PREF_SORT_POPULAR))
           return "popular";
        else if (sort.equals(PREF_SORT_TOPRATED))
           return "top_rated";
        else
            return "favorite";
    }

}
