package com.hukex.punpun.fragment;

import android.app.Activity;
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
import com.hukex.punpun.MangaActivity;
import com.hukex.punpun.R;
import com.hukex.punpun.model.anime.Genre;
import com.hukex.punpun.model.manga.Author;
import com.hukex.punpun.model.manga.MangaInfoCall;
import com.hukex.punpun.model.manga.MangaTop;
import com.hukex.punpun.utils.GlideApp;


public class MangaFragment extends Fragment {

    private MangaInfoCall MangaInfoCall;
    private MangaTop manga;


    public MangaFragment() {
    }

    public MangaFragment(MangaTop manga, MangaInfoCall MangaInfoCall) {
        this.manga = manga;
        this.MangaInfoCall = MangaInfoCall;

    }

    public static MangaFragment newInstance(MangaTop manga, MangaInfoCall MangaInfoCall) {
        MangaFragment fragment = new MangaFragment(manga, MangaInfoCall);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.anime_manga_item, container, false);
    }

    @Override
    public void onDetach() {
        MangaActivity.call1 = null;
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
                .load(manga.getImageUrl()).centerCrop().listener(new RequestListener() {
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
        title.setText(manga.getTitle());
        TextView score = view.findViewById(R.id.score_inside);
        score.setText(String.valueOf(manga.getScore()));
        ImageView start = view.findViewById(R.id.star_inside);
        if (manga.getScore() >= 4 && manga.getScore() <= 6) {
            start.setImageResource(R.drawable.ic_start_half);
        } else if (manga.getScore() < 3) {
            start.setImageResource(R.drawable.ic_start_empty);
        }
        TextView members = view.findViewById(R.id.members_inside);
        members.setText("(" + MangaInfoCall.getScoredBy() + " " + getString(R.string.users) + ")");
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        TextView volums = view.findViewById(R.id.episodes_inside);
        volums.setText(manga.getVolumes() + " " + getString(R.string.volums));
        TextView rank = view.findViewById(R.id.rank_inside);
        if (manga.getRank() != null)
            rank.setText(String.valueOf(manga.getRank()));
        TextView type = view.findViewById(R.id.type_inside);
        type.setText(manga.getType());
        TextView status = view.findViewById(R.id.status_inside);
        status.setText(MangaInfoCall.getStatus());


        TextView synopsis = view.findViewById(R.id.synopsis_inside);
        synopsis.setText(MangaInfoCall.getSynopsis());


        TextView genre = view.findViewById(R.id.genre_inside);
        int n = MangaInfoCall.getGenres().size(), count = 1;
        for (Genre mangaInfoCallGenre : MangaInfoCall.getGenres()) {
            genre.append(mangaInfoCallGenre.name);
            if (n > count++) genre.append(",");

        }
        view.findViewById(R.id.aired_label).setVisibility(View.GONE);
        view.findViewById(R.id.studios_label).setVisibility(View.GONE);
        view.findViewById(R.id.source_label).setVisibility(View.GONE);
        view.findViewById(R.id.trailer_button).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.duration_label).setVisibility(View.GONE);
        view.findViewById(R.id.duration_inside).setVisibility(View.GONE);
        view.findViewById(R.id.source_inside).setVisibility(View.GONE);

        view.findViewById(R.id.aired_inside).setVisibility(View.GONE);
        view.findViewById(R.id.studios_inside).setVisibility(View.GONE);
        TextView authorsLabel = view.findViewById(R.id.producers_label);
        authorsLabel.setText(R.string.authors);

        TextView authors = view.findViewById(R.id.producers_inside);
        int np = MangaInfoCall.getAuthors().size(), countp = 1;
        for (Author animeInfoCallAuthor : MangaInfoCall.getAuthors()) {
            authors.append(animeInfoCallAuthor.name);
            if (np > countp++) authors.append(",");

        }

    }

}
