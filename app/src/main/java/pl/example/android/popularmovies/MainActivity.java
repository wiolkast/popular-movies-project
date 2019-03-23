package pl.example.android.popularmovies;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.example.android.popularmovies.model.Movie;
import pl.example.android.popularmovies.utilities.MovieAdapter;
import pl.example.android.popularmovies.utilities.MovieViewModel;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private final static int PICTURE_WIDTH_PIXELS = 342;
    private RecyclerView recyclerView;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;
    private MovieAdapter adapter;
    private List<Movie> moviesList;
    private MovieViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorMessageTextView = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.progress_bar);

        int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        int spanCount = (int) Math.floor((double) screenWidthPixels / (double) PICTURE_WIDTH_PIXELS);
        recyclerView = findViewById(R.id.rv_movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(this, new ArrayList<Movie>(), this);
        recyclerView.setAdapter(adapter);

        model = ViewModelProviders.of(this).get(MovieViewModel.class);
        model.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                moviesList = movies;
                adapter.updateData(movies);
            }
        });
        model.getStatus().observe(this, new Observer<MovieViewModel.Status>() {
            @Override
            public void onChanged(@Nullable MovieViewModel.Status status) {
                if (status == null) {
                    return;
                }
                switch (status) {
                    case LOADING:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        errorMessageTextView.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        model.getOrderBy().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                model.loadMovies(s);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_about:
                showCredits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(int itemId) {
        Intent detailActivity = new Intent(this, DetailActivity.class);
        detailActivity.putExtra("MovieDetails", moviesList.get(itemId));
        startActivity(detailActivity);
    }

    private void showCredits() {
        int themeId = 2;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, themeId);
        builder.setView(this.getLayoutInflater().inflate(R.layout.credits_dialog, null));
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog creditsDialog = builder.create();
        creditsDialog.setCancelable(true);
        creditsDialog.show();
    }
}
