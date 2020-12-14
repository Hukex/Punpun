package com.hukex.punpun;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.hukex.punpun.adapter.RecyclerViewAdapterAnime;
import com.hukex.punpun.adapter.RecyclerViewAdapterWallpaper;
import com.hukex.punpun.api.AnimeMangaApi;
import com.hukex.punpun.fragment.AnimeFragment;
import com.hukex.punpun.model.anime.AnimeInfoCall;
import com.hukex.punpun.model.anime.AnimeTop;
import com.hukex.punpun.model.anime.AnimeTopCall;
import com.hukex.punpun.model.anime.SearchAnime;
import com.hukex.punpun.model.manga.MangaTop;
import com.hukex.punpun.model.wallpapers.Wallpaper;
import com.hukex.punpun.utils.ItemClickListener;
import com.hukex.punpun.widget.AutofitRecyclerViewAnimeManga;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnimeActivity extends AppCompatActivity implements ItemClickListener {


    private AutofitRecyclerViewAnimeManga autofitRecyclerViewAnimeManga;
    private RecyclerViewAdapterAnime recyclerViewAdapterAnime;
    private boolean isScrolling, reachEnd = false;
    private List<AnimeTop> list;
    private int page = 1;
    private ProgressBar progressBar, progressBarCenter;
    private Sprite doubleBounce = new WanderingCubes();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.OnScrollListener scrollListener;
    private GridLayoutManager layoutManager;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar materialToolbar;
    private Snackbar snackbar;
    private AnimeMangaApi animeMangaApi;
    private String urlBase;
    private SearchView searchView;
    private SharedPreferences sharedPref;
    private AnimeInfoCall animeInfoCall;

    @Override
    protected void onResume() {
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchDarkMode = sharedPref.getBoolean("DARKMODE", false);
        if (switchDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        animeMangaApi = retrofit.create(AnimeMangaApi.class);


        progressBarCenter = findViewById(R.id.progress_bar_center);
        progressBarCenter.setIndeterminateDrawable(doubleBounce);
        autofitRecyclerViewAnimeManga = findViewById(R.id.autofitRecyclerViewAnime);
        swipeRefreshLayout = findViewById(R.id.swipeResfreshWallpaper);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            reachEnd = false;
            searchAndLoadAnimes();
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.purple_main));
        urlBase = "top/anime/";
        searchAndLoadAnimes();
        materialToolbar = findViewById(R.id.topWallpaperBar);
        materialToolbar.setOnClickListener(v -> {
            autofitRecyclerViewAnimeManga.smoothScrollToPosition(0);
        });
        searchViewMethod();
        materialToolbar.setNavigationOnClickListener(v -> {
            if (!urlBase.equals("top/anime/")) {
                goBackAction();
            }
        });

        materialToolbar.setOnMenuItemClickListener(v -> {
            if (v.getItemId() == R.id.settingsW) { // go to settings activity
                Intent intent = new Intent(this, Preference.class);
                intent.putExtra("caller", "com.hukex.punpun.AnimeActivity");
                startActivity(intent);
                return true;
            }
            return false;
        });


        bottomNavigationView = findViewById(R.id.bottomWallpaperBar);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(v -> {
            if (v.getItemId() == R.id.bar_wallpaper) { // go to wallpaper activity
                Intent intent = new Intent(this, WallpaperActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
                overridePendingTransition(0, 0);
                return true;
            } else if (v.getItemId() == R.id.bar_manga) {   // go to manga activity
                Intent intent = new Intent(this, MangaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void searchAndLoadAnimes() {


        swipeRefreshLayout.setRefreshing(true);
        Call<AnimeTopCall> call1 = animeMangaApi.getTopAnimes(urlBase + page);
        progressBar = findViewById(R.id.progress_bar_circle);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        call1.enqueue(new Callback<AnimeTopCall>() {
            @Override
            public void onResponse(Call<AnimeTopCall> call, Response<AnimeTopCall> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    snackbar = Snackbar.make(swipeRefreshLayout, R.string.updated, Snackbar.LENGTH_SHORT).setAnchorView(bottomNavigationView);
                    snackbar.show();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    return;
                }
                progressBarCenter.setVisibility(View.GONE);
                list = response.body().getTop();
                recyclerViewAdapterAnime = new RecyclerViewAdapterAnime(AnimeActivity.this, list, AnimeActivity.this);
                autofitRecyclerViewAnimeManga.setHasFixedSize(true);
                autofitRecyclerViewAnimeManga.setAdapter(recyclerViewAdapterAnime);

                if (scrollListener == null) {
                    scrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                isScrolling = true;
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            // Log.d("here", " dx " + dx + " dy " + dy);

                            if (dy < -15) {
                                bottomNavigationView.animate()
                                        .translationY(0)
                                        .setDuration(250);

                            } else if (dy > 0) {
                                if (snackbar.isShown())
                                    snackbar.dismiss();
                                if (dy > 15) {
                                    bottomNavigationView.animate()
                                            .translationY(bottomNavigationView.getHeight())
                                            .setDuration(250);
                                }
                                final int visibleThreshold = 1;
                                layoutManager = (GridLayoutManager) autofitRecyclerViewAnimeManga.getLayoutManager();
                                int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                                int currentTotalCount = layoutManager.getItemCount();
                                Log.d("here", isScrolling + " currentotalCount " + currentTotalCount + " <= lastItem " + lastItem + " vt " + visibleThreshold);
                                if (isScrolling && !reachEnd && currentTotalCount <= lastItem + visibleThreshold) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    secondApiCallScroll(animeMangaApi, urlBase);
                                }
                            }
                        }
                    };
                    autofitRecyclerViewAnimeManga.addOnScrollListener(scrollListener);
                }
            }


            @Override
            public void onFailure(Call<AnimeTopCall> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressBarCenter.setVisibility(View.GONE);
                errorConnectionToast();
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void searchViewMethod() {
        FrameLayout frameLayout = findViewById(R.id.blackBlur);
        searchView = findViewById(R.id.searchW);
        searchView.setMaxWidth(dpToPx(1650));
        searchView.setOnSearchClickListener(v -> {
            TypedValue typedValue = new TypedValue();
            this.getTheme().resolveAttribute(R.attr.backgroundOwn, typedValue, true);
            @ColorInt int color = typedValue.data;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color
                    , ContextCompat.getColor(this, R.color.purple_200));
            colorAnimation.setDuration(600); // milliseconds
            colorAnimation.addUpdateListener(animator -> {
                materialToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                searchView.findViewById(R.id.search_plate).setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnimation.start();
            materialToolbar.setNavigationIcon(null);
            frameLayout.bringToFront();
            frameLayout.setVisibility(View.VISIBLE);
            frameLayout.setOnClickListener(v1 -> {
                searchView.setQuery("", false);
                searchView.setIconified(true);
            });
        });
        searchView.setOnCloseListener(() -> {
            TypedValue typedValue = new TypedValue();
            this.getTheme().resolveAttribute(R.attr.backgroundOwn, typedValue, true);
            @ColorInt int color = typedValue.data;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(AnimeActivity.this, R.color.purple_200)
                    , color);
            colorAnimation.setDuration(600); // milliseconds
            colorAnimation.addUpdateListener(animator -> {
                materialToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                searchView.findViewById(R.id.search_plate).setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnimation.start();
            if (!urlBase.equals("top/anime/"))
                materialToolbar.setNavigationIcon(R.drawable.ic_left);
            else
                materialToolbar.setNavigationIcon(R.drawable.ic_wallpapers);
            frameLayout.setVisibility(View.GONE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBySubmitAndSelected(query, frameLayout);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }

    private void searchBySubmitAndSelected(String name, FrameLayout frameLayout) {
        page = 1;
        list = null;
        urlBase = "search/anime?q=" + name;

        swipeRefreshLayout.setEnabled(false);
        loadAnimesBySearch();


        materialToolbar.setTitle(name);
        searchView.setQuery("", false);
        searchView.setIconified(true);
        materialToolbar.setNavigationIcon(R.drawable.ic_left);
        frameLayout.setVisibility(View.GONE);
    }

    private void loadAnimesBySearch() {
        Call<SearchAnime> call1 = animeMangaApi.getAnimes(urlBase + page);
        progressBar = findViewById(R.id.progress_bar_circle);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        call1.enqueue(new Callback<SearchAnime>() {
            @Override
            public void onResponse(Call<SearchAnime> call, Response<SearchAnime> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    snackbar = Snackbar.make(swipeRefreshLayout, R.string.updated, Snackbar.LENGTH_SHORT).setAnchorView(bottomNavigationView);
                    snackbar.show();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    return;
                }
                progressBarCenter.setVisibility(View.GONE);
                list = response.body().getResults();
                recyclerViewAdapterAnime = new RecyclerViewAdapterAnime(AnimeActivity.this, list, AnimeActivity.this);
                autofitRecyclerViewAnimeManga.setHasFixedSize(true);
                autofitRecyclerViewAnimeManga.setAdapter(recyclerViewAdapterAnime);

                if (scrollListener == null) {
                    scrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                isScrolling = true;
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            // Log.d("here", " dx " + dx + " dy " + dy);

                            if (dy < -15) {
                                bottomNavigationView.animate()
                                        .translationY(0)
                                        .setDuration(250);

                            } else if (dy > 0) {
                                if (snackbar.isShown())
                                    snackbar.dismiss();
                                if (dy > 15) {
                                    bottomNavigationView.animate()
                                            .translationY(bottomNavigationView.getHeight())
                                            .setDuration(250);
                                }
                                final int visibleThreshold = 1;
                                layoutManager = (GridLayoutManager) autofitRecyclerViewAnimeManga.getLayoutManager();
                                int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                                int currentTotalCount = layoutManager.getItemCount();
                                Log.d("here", isScrolling + " currentotalCount " + currentTotalCount + " <= lastItem " + lastItem + " vt " + visibleThreshold);
                                if (isScrolling && !reachEnd && currentTotalCount <= lastItem + visibleThreshold) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    secondApiCallScroll(animeMangaApi, urlBase);
                                }
                            }
                        }
                    };
                    autofitRecyclerViewAnimeManga.addOnScrollListener(scrollListener);
                }
            }


            @Override
            public void onFailure(Call<SearchAnime> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressBarCenter.setVisibility(View.GONE);
                errorConnectionToast();
            }
        });
    }

    public void secondApiCallScroll(AnimeMangaApi animeMangaApi, String urlBase) {
        Call<AnimeTopCall> call2 = animeMangaApi.getTopAnimes(urlBase + ++page);
        call2.enqueue(new Callback<AnimeTopCall>() {
            @Override
            public void onResponse(Call<AnimeTopCall> call, Response<AnimeTopCall> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                isScrolling = false;
                if (response.body().top != null)
                    list.addAll(response.body().getTop());
                else
                    reachEnd = true;
                recyclerViewAdapterAnime.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AnimeTopCall> call, Throwable t) {
                errorConnectionToast();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void errorConnectionToast() {
        snackbar = Snackbar.make(swipeRefreshLayout, R.string.fail_connection, Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(bottomNavigationView).setAction(R.string.retry, v -> {
                    Thread thread = new Thread(() -> {
                        try {
                            if (hasActiveInternetConnection()) {
                                AnimeActivity.this.runOnUiThread(() -> {
                                    if (list == null && urlBase.equals("top/anime/"))
                                        searchAndLoadAnimes();
                                    else if (!urlBase.equals("top/anime/"))
                                        loadAnimesBySearch();
                                    else
                                        secondApiCallScroll(animeMangaApi, urlBase);
                                });
                            } else {
                                AnimeActivity.this.runOnUiThread(this::errorConnectionToast);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                });
        snackbar.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public boolean hasActiveInternetConnection() {
        if (this.isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("https://clients3.google.com/generate_204")
                                .openConnection());
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (
                    IOException e) {
                Log.e("s", "Error checking internet connection", e);
            }
        }
        return false;
    }

    @Override
    public void onPhotoClicked(RecyclerViewAdapterWallpaper.MyViewHolder holder, int position, List<Wallpaper> wallpapers) {
    }

    public static Call<AnimeInfoCall> call1;

    @Override
    public void onAnimeClicked(AnimeTop anime) {
        if (call1 == null) {
            call1 = animeMangaApi.getInfoAnime("anime/" + anime.getMalId());
            call1.enqueue(new Callback<AnimeInfoCall>() {
                @Override
                public void onResponse(Call<AnimeInfoCall> call, Response<AnimeInfoCall> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    animeInfoCall = response.body();
                    AnimeFragment animeFragment = AnimeFragment.newInstance(anime, animeInfoCall);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.main, animeFragment)
                            .addToBackStack(null)
                            .commit();
                }

                @Override
                public void onFailure(Call<AnimeInfoCall> call, Throwable t) {
                    Snackbar.make(swipeRefreshLayout, R.string.fail_connection, Snackbar.LENGTH_LONG).show();
                    call1 = null;
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else if (bottomNavigationView.isShown() && !urlBase.equals("top/anime/")) {
            goBackAction();
        } else {
            super.onBackPressed();
        }
    }

    private void goBackAction() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
        swipeRefreshLayout.setEnabled(true);

        urlBase = "top/anime/";
        page = 1;
        searchAndLoadAnimes();
        materialToolbar.setNavigationIcon(R.drawable.ic_anime);
        materialToolbar.setTitle(R.string.anime_title);
    }

    @Override
    public void onMangaClicked(MangaTop anime) {

    }
}