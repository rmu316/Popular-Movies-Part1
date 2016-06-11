package com.example.richardmu.movie.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class PosterFragment extends Fragment {

    private CustomImageArrayAdapter gridAdapter;
    private String MovieJsonStr;
    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public PosterFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView grid = (GridView)rootView.findViewById(R.id.gridview_poster);
        gridAdapter = new CustomImageArrayAdapter(getActivity(),
                R.layout.grid_item_poster,
                new ArrayList<String>());
        grid.setAdapter(gridAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String urlOfThatImg = gridAdapter.getItem(position);
                ArrayList<String> toBeAdded = new ArrayList<>();
                toBeAdded.add(decompressUrl(urlOfThatImg));
                toBeAdded.add(MovieJsonStr);
                Intent DetailactivityPage = new Intent(getContext(), DetailActivity.class).putStringArrayListExtra(
                        Intent.EXTRA_TEXT, toBeAdded);
                startActivity(DetailactivityPage);
            }
        });
        return rootView;
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String type = prefs.getString(getString(R.string.pref_sorted_order_key), getString(R.string.pref_sorted_order_default));
        new FetchMovieInfoTask().execute(type);
    }

    private String decompressUrl(String url) {
        return url.substring(IMAGE_BASE_URL.length());
    }

    public class FetchMovieInfoTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
        private String BASE_URL = "http://api.themoviedb.org/3/movie/";
        // Please see README.md for what to add here!
        private final String API_KEY = "api_key";

        protected String[] doInBackground(String ...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                BASE_URL = BASE_URL.concat(params[0]);
                Uri builder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY,APP_ID)
                        .build();
                URL url = new URL(builder.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                MovieJsonStr = buffer.toString();
                try {
                    return getMovieDataFromJson(MovieJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String []fetchedData) {
            if (fetchedData != null) {
                gridAdapter.clear();
                gridAdapter.setMovieData(new ArrayList<>(Arrays.asList(fetchedData)));
            }
        }

        // Get list of all image urls supplied by the movie database API
        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            final String TMDB_RESULT = "results";
            final String TMDB_POSTER = "poster_path";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);
            Integer MOVIE_SIZE = movieArray.length();

            String []resultStrs = new String[MOVIE_SIZE];

            for (int i = 0; i < MOVIE_SIZE; i++) {
                JSONObject oneResult = movieArray.getJSONObject(i);
                String poster = oneResult.getString(TMDB_POSTER);
                resultStrs[i] = IMAGE_BASE_URL + poster;
            }
            return resultStrs;
        }
    }
}
