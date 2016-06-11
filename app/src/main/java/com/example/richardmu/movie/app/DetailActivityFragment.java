package com.example.richardmu.movie.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private final String RATING_LABEL = "Rating: ";
    private final String REL_LABEL = "Release Date: ";
    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private enum infoType {
        TITLE,
        SYNOPSIS,
        RELEASE,
        RATING
    }

    public DetailActivityFragment() {
    }

    // Convert a date like 04-29-2016 => Apr 29, 2016
    public String parseDateToReadable(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            ArrayList<String> toBeExtracted = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
            String imagePath = toBeExtracted.get(0);
            Picasso.with(getContext()).load(IMAGE_BASE_URL+imagePath).into((ImageView)rootView.findViewById(R.id.movie_image));
            String movieJson = toBeExtracted.get(1);
            try {
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(getMovieInfo(movieJson, imagePath, infoType.TITLE));
                ((TextView) rootView.findViewById(R.id.movie_synopsis)).setText(getMovieInfo(movieJson, imagePath, infoType.SYNOPSIS));
                ((TextView) rootView.findViewById(R.id.movie_rating)).setText(RATING_LABEL+getMovieInfo(movieJson, imagePath, infoType.RATING));
                ((TextView) rootView.findViewById(R.id.movie_release)).setText(REL_LABEL+parseDateToReadable(getMovieInfo(movieJson, imagePath, infoType.RELEASE)));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return rootView;
    }

    private String getMovieInfo(String movieJson, String imagePath, infoType whatInfo) throws JSONException {
        final String TMDB_RESULT = "results";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_TITLE = "original_title";
        final String TMDB_SYNOP = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_REL = "release_date";
        final String DEFAULT = "N/A";

        JSONObject movieJsonObj = new JSONObject(movieJson);
        JSONArray movieArray = movieJsonObj.getJSONArray(TMDB_RESULT);
        Integer MOVIE_SIZE = movieArray.length();

        for (int i = 0; i < MOVIE_SIZE; i++) {
            JSONObject oneResult = movieArray.getJSONObject(i);
            if (oneResult.getString(TMDB_POSTER).equals(imagePath)) {
                switch (whatInfo) {
                    case TITLE:
                        return oneResult.getString(TMDB_TITLE);
                    case SYNOPSIS:
                        return oneResult.getString(TMDB_SYNOP);
                    case RATING:
                        return oneResult.getString(TMDB_RATING);
                    case RELEASE:
                        return oneResult.getString(TMDB_REL);
                    default:
                        return DEFAULT;
                }
            }
        }
        return DEFAULT;
    }
}
