package com.example.richardmu.movie.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class PosterFragment extends Fragment {

    private CustomImageArrayAdapter gridAdapter;

    public PosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String [] urls = {
                "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                "http://www.flickeringmyth.com/wp-content/uploads/2016/03/Civil-War-International-Poster.jpg",
                "http://www.batman-3d.de/wp-content/myfotos/tdk/The-Dark-Knight.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMjE0Njc1NDYzN15BMl5BanBnXkFtZTcwNjAxMzYyMQ@@._V1_SX640_SY720_.jpg",
                "http://i.imgur.com/DvpvklR.png"
        };
        GridView grid = (GridView)rootView.findViewById(R.id.gridview_poster);
        gridAdapter = new CustomImageArrayAdapter(getActivity(), R.layout.grid_item_poster, Arrays.asList(urls));
        grid.setAdapter(gridAdapter);
        return rootView;
    }
}
