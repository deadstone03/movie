package com.xingdai.movie;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xingdai.movie.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    public MoviesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayAdapter<Movie> movieListViewAdapter = new ArrayAdapter<Movie>(
                getActivity(), R.layout.movie_list_item_view,
                R.id.movie_list_item_view,
                new ArrayList<Movie>()
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Movie movie = getItem(position);
                View nextView = null;
                if (convertView != null) {
                    nextView = convertView;
                } else {
                    nextView = inflater.inflate(R.layout.movie_list_item_view, parent, false);
                }
                ((TextView)nextView.findViewById(R.id.movie_title)).setText(movie.title);
                ((TextView)nextView.findViewById(R.id.movie_review)).setText(movie.overview);
                String imageUrlTemplate = "http://image.tmdb.org/t/p/w185" + movie.posterPath;
                Uri url = Uri.parse(imageUrlTemplate).buildUpon()
                        .appendQueryParameter("api_key", "15222f160ada48efe4535bb9cc7317dd")
                        .build();
                Log.i("adapter", url.toString());
                ImageView movieImage  = ((ImageView)nextView.findViewById(R.id.movie_image));
                Picasso.with(getActivity()).load(url.toString()).into(movieImage);

                return nextView;
            }
        };
        ListView movieListView = (ListView) rootView.findViewById(R.id.movie_list_view);
        movieListView.setAdapter(movieListViewAdapter);
        new FetchMovieListTask(movieListViewAdapter).execute();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}

class FetchMovieListTask extends AsyncTask<Void, Void, List<Movie>> {
    private static final String LOG_TAG = FetchMovieListTask.class.getName();
    private ArrayAdapter<Movie> mArrayAdapter;

    public FetchMovieListTask(ArrayAdapter<Movie> arrayAdapter) {
        mArrayAdapter = arrayAdapter;
    }
    @Override
    protected List<Movie> doInBackground(Void... params) {
        String baseUrl = "http://api.themoviedb.org/3/movie/popular";
        Uri uri = null;
        BufferedReader reader = null;
        String movieListJson = null;

        try {
             uri = Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter("page", "1")
                    .appendQueryParameter("api_key", "15222f160ada48efe4535bb9cc7317dd")
                    .build();
            Log.i(LOG_TAG, uri.toString());
            HttpURLConnection urlConnection = (HttpURLConnection)new URL(uri.toString()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieListJson = buffer.toString();
            Log.i(LOG_TAG, movieListJson);
            List<Movie> movies = parseMovieListJson(movieListJson);
            return movies;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, uri.toString(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "fail to fetch", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "fail to parse the json", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mArrayAdapter.clear();
            mArrayAdapter.addAll(movies);
        }
    }

    List<Movie> parseMovieListJson(String movieListJson) throws JSONException {
        JSONObject json = new JSONObject(movieListJson);
        JSONArray moviesJson = json.getJSONArray("results");
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < moviesJson.length(); ++i) {
            Movie movie = new Movie(moviesJson.getJSONObject(i));
            movies.add(movie);
        }
        return movies;
    }
}
