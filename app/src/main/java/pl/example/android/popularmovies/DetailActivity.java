package pl.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.example.android.popularmovies.model.Movie;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("MovieDetails");

        TextView title = findViewById(R.id.title);
        TextView releaseDate = findViewById(R.id.release_date);
        final ImageView poster = findViewById(R.id.poster);
        TextView voteAverage = findViewById(R.id.vote_average);
        TextView plotSynopsis = findViewById(R.id.plot_synopsis);

        title.setText(movie.getMovieTitle());
        releaseDate.setText(movie.getMovieReleaseDate());
        Picasso.with(this).load(movie.getMoviePosterSmall())
                .placeholder(R.drawable.placeholder)
                .into(poster);
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        plotSynopsis.setText(movie.getPlotSynopsis());
    }
}
