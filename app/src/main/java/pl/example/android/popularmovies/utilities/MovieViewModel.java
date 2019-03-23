package pl.example.android.popularmovies.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import java.util.List;

import pl.example.android.popularmovies.R;
import pl.example.android.popularmovies.model.Movie;

public class MovieViewModel extends AndroidViewModel {

    public enum Status {SUCCESS, ERROR, LOADING}
    private static MutableLiveData<List<Movie>> movies;
    private final static MutableLiveData<Status> status = new MutableLiveData<>();
    private final static MutableLiveData<String> orderBy = new MutableLiveData<>();

    public MovieViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Movie>> getMovies() {
        if(movies == null){
            movies = new MutableLiveData<>();
            loadMovies(getOrderByPreference());
        }
        return movies;
    }

    public LiveData<Status> getStatus(){
        return status;
    }

    public LiveData<String> getOrderBy(){
        orderBy.setValue(getOrderByPreference());
        return orderBy;
    }

    public void loadMovies(String orderBy){
        new TheMovieDbQueryTask().execute(orderBy);
    }

    private String getOrderByPreference(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
        return preferences.getString(getApplication().getString(R.string.order_by_key), getApplication().getString(R.string.order_by_default));
    }

    private static class TheMovieDbQueryTask extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            status.setValue(MovieViewModel.Status.LOADING);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return NetworkUtils.getMovies(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Movie> moviesList) {
            if (moviesList != null) {
                status.setValue(MovieViewModel.Status.SUCCESS);
                movies.setValue(moviesList);
            } else {
                status.setValue(MovieViewModel.Status.ERROR);
            }
        }
    }
}
