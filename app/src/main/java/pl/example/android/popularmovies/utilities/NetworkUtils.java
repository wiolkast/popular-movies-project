package pl.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pl.example.android.popularmovies.model.Movie;

public class NetworkUtils {

    // --------------------------------------------------------------------------------
    // Enter your API KEY. More info: https://www.themoviedb.org/faq/api
    final private static String apiKey = "";
    // --------------------------------------------------------------------------------

    final private static String LOG_TAG = NetworkUtils.class.getSimpleName();
    final private static String THEMOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    final private static String PARAM_API_KEY = "api_key";
    final private static String JSON_RESULTS = "results";
    final private static String JSON_TITLE = "title";
    final private static String JSON_RELEASE_DATE = "release_date";
    final private static String JSON_VOTE_AVERAGE = "vote_average";
    final private static String JSON_PLOT_SYNOPSIS = "overview";
    final private static String JSON_POSTER_PATH = "poster_path";
    final private static String POSTER_PATH_BASE = "http://image.tmdb.org/t/p/";
    final private static String POSTER_SIZE_SMALL = "w185";
    final private static String POSTER_SIZE_LARGE = "w342";

    private static URL buildUrl(String sortBy) {
        Uri buildUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        if (url == null) {
            return null;
        }
        InputStream inputStream = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        if (urlConnection.getResponseCode() == 200) {
            inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } else {
            Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        }
        urlConnection.disconnect();
        if (inputStream != null) {
            inputStream.close();
        }
        return null;
    }

    private static List<Movie> extractDataFromJson(String moviesJSON) {
        if (moviesJSON == null) {
            return null;
        }
        List<Movie> moviesList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray moviesArray = baseJsonResponse.getJSONArray(JSON_RESULTS);
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject currentMovie = moviesArray.getJSONObject(i);
                String movieTitle = currentMovie.getString(JSON_TITLE);
                String movieReleaseDate = currentMovie.getString(JSON_RELEASE_DATE);
                Double voteAverage = currentMovie.getDouble(JSON_VOTE_AVERAGE);
                String plotSynopsis = currentMovie.getString(JSON_PLOT_SYNOPSIS);
                String moviePosterSmall = POSTER_PATH_BASE + POSTER_SIZE_SMALL + currentMovie.getString(JSON_POSTER_PATH);
                String moviePosterLarge = POSTER_PATH_BASE + POSTER_SIZE_LARGE + currentMovie.getString(JSON_POSTER_PATH);
                Movie movie = new Movie(movieTitle, movieReleaseDate, voteAverage, plotSynopsis, moviePosterSmall, moviePosterLarge);
                moviesList.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON. ", e);
        }
        return moviesList;
    }

    public static List<Movie> getMovies(String orderBy){
        URL url = buildUrl(orderBy);
        List<Movie> moviesList = null;
        try {
            moviesList = extractDataFromJson(getResponseFromHttpUrl(url));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON. ", e);
        }
        return moviesList;
    }
}
