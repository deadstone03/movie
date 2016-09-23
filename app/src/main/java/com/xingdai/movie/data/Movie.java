package com.xingdai.movie.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xingdai on 9/21/16.
 */
public class Movie {
    public String posterPath;
    public boolean adult;
    public String overview;
    public Date releaseDate;
    public List<Integer> genreIds;
    public Long id;
    public String originalTitle;
    public String originalLanguage;
    public String title;
    public String backdropPath;
    public Double popularity;
    public Long voteCount;
    public Boolean video;
    public Double voteAverage;

    public Movie(JSONObject jsonObject) throws JSONException {
        this.posterPath = jsonObject.getString("poster_path");
        this.adult = jsonObject.getBoolean("adult");
        this.overview = jsonObject.getString("overview");
        try {
            this.releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(
                    jsonObject.getString("release_date"));
        } catch (ParseException e) {
            Log.i("Movie", "parse date failed", e);
            this.releaseDate = null;
        }
        this.genreIds = new ArrayList<Integer>();

        JSONArray genreIdsJson = jsonObject.getJSONArray("genre_ids");
        int len = genreIdsJson.length();
        for (int i = 0; i < genreIdsJson.length(); ++i) {
            this.genreIds.add(genreIdsJson.getInt(i));
        }

        this.id = jsonObject.getLong("id");
        this.originalTitle = jsonObject.getString("original_title");
        this.originalLanguage = jsonObject.getString("original_language");
        this.title = jsonObject.getString("title");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.popularity = jsonObject.getDouble("popularity");
        this.voteCount = jsonObject.getLong("vote_count");
        this.video = jsonObject.getBoolean("video");
        this.voteAverage = jsonObject.getDouble("vote_average");
    }
}
