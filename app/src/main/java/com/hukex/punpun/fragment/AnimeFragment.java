package com.hukex.punpun.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hukex.punpun.AnimeActivity;
import com.hukex.punpun.R;
import com.hukex.punpun.model.anime.AnimeInfoCall;
import com.hukex.punpun.model.anime.AnimeTop;
import com.hukex.punpun.model.anime.Genre;
import com.hukex.punpun.model.anime.Producer;
import com.hukex.punpun.model.anime.Studio;
import com.hukex.punpun.utils.GlideApp;


public class AnimeFragment extends Fragment {

    private AnimeInfoCall animeInfoCall;
    private AnimeTop anime;


    public AnimeFragment() {
    }

    public AnimeFragment(AnimeTop anime, AnimeInfoCall animeInfoCall) {
        this.anime = anime;
        this.animeInfoCall = animeInfoCall;

    }

    public static AnimeFragment newInstance(AnimeTop anime, AnimeInfoCall animeInfoCall) {
        AnimeFragment fragment = new AnimeFragment(anime, animeInfoCall);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.anime_manga_item, container, false);
    }

    @Override
    public void onDetach() {
        AnimeActivity.call1 = null;
        BottomNavigationView bottomNavigationViewWallpaper = ((Activity) getContext()).findViewById(R.id.bottomWallpaperBar);
        bottomNavigationViewWallpaper.setVisibility(View.VISIBLE);
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View parentItem = (View) view.getParent();
        BottomNavigationView bottomNavigationViewWallpaper = parentItem.findViewById(R.id.bottomWallpaperBar);
        bottomNavigationViewWallpaper.setVisibility(View.GONE);

        ProgressBar progressBar;
        progressBar = view.findViewById(R.id.progress_bar_circle_image_item);
        ImageView imageView = view.findViewById(R.id.image_inside);
        GlideApp.with(view.getContext()).asBitmap()
                .load(anime.getImageUrl()).centerCrop().listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
        TextView title = view.findViewById(R.id.title_inside);
        title.setText(anime.getTitle());
        TextView score = view.findViewById(R.id.score_inside);
        score.setText(String.valueOf(anime.getScore()));
        ImageView start = view.findViewById(R.id.star_inside);
        if (anime.getScore() >= 4 && anime.getScore() <= 6) {
            start.setImageResource(R.drawable.ic_start_half);
        } else if (anime.getScore() < 3) {
            start.setImageResource(R.drawable.ic_start_empty);
        }
        TextView members = view.findViewById(R.id.members_inside);
        members.setText("(" + animeInfoCall.getScoredBy() + " " + getString(R.string.users) + ")");
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        TextView episodes = view.findViewById(R.id.episodes_inside);
        episodes.setText(anime.getEpisodes() + " " + getString(R.string.episodes));
        TextView rank = view.findViewById(R.id.rank_inside);
        rank.setText(String.valueOf(anime.getRank()));
        TextView type = view.findViewById(R.id.type_inside);
        type.setText(anime.getType());
        TextView status = view.findViewById(R.id.status_inside);
        status.setText(animeInfoCall.getStatus());

        TextView rating = view.findViewById(R.id.rating_inside);
        rating.setText(animeInfoCall.getRating());

        TextView synopsis = view.findViewById(R.id.synopsis_inside);
        synopsis.setText(animeInfoCall.getSynopsis());

        TextView duration = view.findViewById(R.id.duration_inside);
        duration.setText(animeInfoCall.getDuration());


        TextView aired_date = view.findViewById(R.id.aired_inside);
        aired_date.setText(animeInfoCall.getAired().getString());
        Button trailerButton = view.findViewById(R.id.trailer_button);
        trailerButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(animeInfoCall.getTrailerUrl()))));

        TextView source = view.findViewById(R.id.source_inside);
        source.setText(animeInfoCall.getSource());

        TextView genre = view.findViewById(R.id.genre_inside);
        int n = animeInfoCall.getGenres().size(), count = 1;
        for (Genre animeInfoCallGenre : animeInfoCall.getGenres()) {
            genre.append(animeInfoCallGenre.name);
            if (n > count++) genre.append(",");

        }


        TextView studios = view.findViewById(R.id.studios_inside);
        int ns = animeInfoCall.getStudios().size(), counts = 1;
        for (Studio animeInfoCallStudio : animeInfoCall.getStudios()) {
            studios.append(animeInfoCallStudio.name);
            if (ns > counts++) studios.append(",");

        }

        TextView producers = view.findViewById(R.id.producers_inside);
        int np = animeInfoCall.getProducers().size(), countp = 1;
        for (Producer animeInfoCallProducer : animeInfoCall.getProducers()) {
            producers.append(animeInfoCallProducer.name);
            if (np > countp++) producers.append(",");

        }

    }

}
