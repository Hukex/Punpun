package com.hukex.punpun;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.hukex.punpun.adapter.RecyclerViewAdapterWallpaper;
import com.hukex.punpun.api.WallpaperApi;
import com.hukex.punpun.fragment.PhotoFragmentZoom;
import com.hukex.punpun.model.anime.AnimeTop;
import com.hukex.punpun.model.manga.MangaTop;
import com.hukex.punpun.model.wallpapers.SubCategory;
import com.hukex.punpun.model.wallpapers.SubCategoryCall;
import com.hukex.punpun.model.wallpapers.Wallpaper;
import com.hukex.punpun.model.wallpapers.WallpaperCall;
import com.hukex.punpun.utils.ItemClickListener;
import com.hukex.punpun.widget.AutofitRecyclerViewWallpaper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WallpaperActivity extends AppCompatActivity implements ItemClickListener {

    private AutofitRecyclerViewWallpaper autofitRecyclerViewWallpaper;
    private boolean isScrolling, reachEnd = false;
    private List<Wallpaper> list;
    private int page = 1, numberOfPhotos = 0;
    private ProgressBar progressBar, progressBarCenter;
    private Sprite doubleBounce = new WanderingCubes();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.OnScrollListener scrollListener;
    private GridLayoutManager layoutManager;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar materialToolbar;
    private Snackbar snackbar;
    private WallpaperApi wallpaperApi;
    private String urlBase;
    private SearchView searchView;
    private List<SubCategory> subCategoryList;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView counter;

    public boolean getReachEnd() {
        return reachEnd;
    }

    public WallpaperApi getWallpaperApi() {
        return wallpaperApi;
    }

    public String getUrlBase() {
        return urlBase;
    }

    @Override
    protected void onResume() {
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
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
        sharedPref = WallpaperActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        setContentView(R.layout.activity_wallpaper);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wall.alphacoders.com/api2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        wallpaperApi = retrofit.create(WallpaperApi.class);

        progressBarCenter = findViewById(R.id.progress_bar_center);
        progressBarCenter.setIndeterminateDrawable(doubleBounce);
        autofitRecyclerViewWallpaper = findViewById(R.id.autofitRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeResfreshWallpaper);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            reachEnd = false;
            searchAndLoadWallpaperImages();
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this, R.color.purple_main));
        urlBase = getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod);
        searchAndLoadWallpaperImages();
        subCategoryCallPetition();
        materialToolbar = findViewById(R.id.topWallpaperBar);
        materialToolbar.setOnClickListener(v -> {
            autofitRecyclerViewWallpaper.smoothScrollToPosition(0);
        });
        searchViewMethod();
        materialToolbar.setNavigationOnClickListener(v -> {
            if (!urlBase.equals(getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod))) {
                goBackAction();
            }
        });

        materialToolbar.setOnMenuItemClickListener(v -> {
            if (v.getItemId() == R.id.settingsW) { // go to settings activity
                Intent intent = new Intent(this, Preference.class);
                intent.putExtra("caller", "com.hukex.punpun.WallpaperActivity");
                startActivity(intent);
                return true;
            }
            return false;
        });


        bottomNavigationView = findViewById(R.id.bottomWallpaperBar);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(v -> {
            if (v.getItemId() == R.id.bar_anime) { // go to anime activity
                Intent intent = new Intent(this, AnimeActivity.class);
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

    private void searchViewMethod() {
        FrameLayout frameLayout = findViewById(R.id.blackBlur);
        searchView = findViewById(R.id.searchW);
        searchView.setMaxWidth(dpToPx(1650));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        String[] columnNames = {
                SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2};
        int[] viewIds = {
                R.id.text1, R.id.text2};
        CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_search,
                null, columnNames, viewIds);
        searchView.setSuggestionsAdapter(adapter);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int index) {
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(index);
                int subCategoryId = cursor.getInt(0);
                String subCategoryName = cursor.getString(1);
                String numberOfPhotosS = cursor.getString(2).replaceAll("\\D+", "");
                numberOfPhotos = Integer.parseInt(numberOfPhotosS);
                searchBySubmitAndSelected(subCategoryId, subCategoryName, frameLayout);
                return true;
            }
        });


        searchView.setOnSearchClickListener(v -> {
            counter.setVisibility(View.GONE);
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
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(WallpaperActivity.this, R.color.purple_200)
                    , color);
            colorAnimation.setDuration(600); // milliseconds
            colorAnimation.addUpdateListener(animator -> {
                materialToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                searchView.findViewById(R.id.search_plate).setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnimation.start();
            if (!urlBase.equals(getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod)))
                materialToolbar.setNavigationIcon(R.drawable.ic_left);
            else
                materialToolbar.setNavigationIcon(R.drawable.ic_wallpapers);
            frameLayout.setVisibility(View.GONE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (subCategoryList == null || subCategoryList.isEmpty()) {
                    subCategoryCallPetition();
                    errorConnectionToast();
                    return true;
                }
                for (SubCategory subCategory : subCategoryList) {
                    String a = subCategory.name.toLowerCase();
                    if (a.equals(query.toLowerCase())) {
                        searchBySubmitAndSelected(subCategory.getId(), subCategory.getName(), frameLayout);
                        return false;
                    }
                }
                return searchBySubmitPersonal(query, frameLayout);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (subCategoryList == null || subCategoryList.isEmpty()) {
                    subCategoryCallPetition();
                    return true;
                }
                if (newText.length() < 3) {
                    return true;
                }
                String[] menuCols = new String[]{
                        BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2
                };
                MatrixCursor cursor = new MatrixCursor(menuCols);

                for (SubCategory subCategory : subCategoryList) {
                    String a = subCategory.name.toLowerCase();
                    if (a.startsWith(newText.toLowerCase())) {
                        String text = getString(R.string.photo_min);
                        if (subCategory.getCount() == 1)
                            text = getString(R.string.photo_min_individual);
                        cursor.addRow(new Object[]{
                                subCategory.getId(),
                                subCategory.getName(), subCategory.getCount() + " " + text});
                    }
                }
                adapter.swapCursor(cursor);
                return false;
            }
        });
    }

    private void searchBySubmitAndSelected(int id, String name, FrameLayout frameLayout) {
        page = 1;
        list = null;
        urlBase = getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.searchMethod) + id + getString(R.string.info_level2_and_page_label);
        searchAndLoadWallpaperImages();
        materialToolbar.setTitle(name);
        searchView.setQuery("", false);
        searchView.setIconified(true);
        materialToolbar.setNavigationIcon(R.drawable.ic_left);
        frameLayout.setVisibility(View.GONE);
    }

    private boolean searchBySubmitPersonal(String name, FrameLayout frameLayout) {
        page = 1;
        list = null;
        urlBase = getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.searchMethodPersonal) + name + getString(R.string.info_level2_and_page_label);
        searchAndLoadWallpaperImages();
        materialToolbar.setTitle(name);
        searchView.setQuery("", false);
        searchView.setIconified(true);
        materialToolbar.setNavigationIcon(R.drawable.ic_left);
        frameLayout.setVisibility(View.GONE);
        if (list == null) {
            Snackbar snackbar = Snackbar.make(swipeRefreshLayout, getString(R.string.not_found) + " " + name, Snackbar.LENGTH_LONG).setAnchorView(bottomNavigationView);
            snackbar.setTextColor(ContextCompat.getColor(WallpaperActivity.this, R.color.purple_200));
            snackbar.show();
            return true;
        } else
            return false;
    }


    private void subCategoryCallPetition() {
        String subCategoriesUrl = getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.subCategoryMethod);
        Call<SubCategoryCall> subCategoryCall = wallpaperApi.getSubcategories(subCategoriesUrl);
        subCategoryCall.enqueue(new Callback<SubCategoryCall>() {
            @Override
            public void onResponse(Call<SubCategoryCall> call, Response<SubCategoryCall> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                subCategoryList = response.body().getSubCategories();
                Log.d("as", String.valueOf(subCategoryList.size()));
            }

            @Override
            public void onFailure(Call<SubCategoryCall> call, Throwable t) {

            }
        });
    }

    private void searchAndLoadWallpaperImages() {

        counter = findViewById(R.id.counter);

        swipeRefreshLayout.setRefreshing(true);
        Call<WallpaperCall> call1 = wallpaperApi.getWallpapers(urlBase + page);
        progressBar = findViewById(R.id.progress_bar_circle);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        call1.enqueue(new Callback<WallpaperCall>() {
            @Override
            public void onResponse(Call<WallpaperCall> call, Response<WallpaperCall> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    snackbar = Snackbar.make(swipeRefreshLayout, R.string.updated, Snackbar.LENGTH_SHORT).setAnchorView(bottomNavigationView);
                    snackbar.show();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful()) {
                    return;
                }
                progressBarCenter.setVisibility(View.GONE);
                if (response.body() != null && response.body().getWallpapers() != null)
                    list = response.body().getWallpapers();
                else {
                    Snackbar snackbar = Snackbar.make(swipeRefreshLayout, getString(R.string.not_found), Snackbar.LENGTH_LONG).setAnchorView(bottomNavigationView);
                    snackbar.setTextColor(ContextCompat.getColor(WallpaperActivity.this, R.color.purple_200));
                    snackbar.show();
                    return;
                }
                if (response.body().total_match != null)
                    numberOfPhotos = Integer.valueOf(response.body().total_match);
                autofitRecyclerViewWallpaper.setContextAndPhotoClickListener(WallpaperActivity.this, WallpaperActivity.this);
                autofitRecyclerViewWallpaper.setListUpdate(list);
                autofitRecyclerViewWallpaper.setSharedPref(sharedPref, editor);
                autofitRecyclerViewWallpaper.createRecyclerViewAdapter();
                autofitRecyclerViewWallpaper.setHasFixedSize(true);
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
                            layoutManager = (GridLayoutManager) autofitRecyclerViewWallpaper.getLayoutManager();
                            int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                            if (numberOfPhotos != 0 && !urlBase.equals(getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod)))
                                counter.setText((lastItem + 1) + "/" + numberOfPhotos);
                            else counter.setText(String.valueOf(lastItem + 1));

                            if (dy < -15) {
                                if (!counter.isShown()) counter.setVisibility(View.VISIBLE);
                                bottomNavigationView.animate()
                                        .translationY(0)
                                        .setDuration(250);
                            } else if (dy > 0) {
                                if (snackbar.isShown())
                                    snackbar.dismiss();
                                if (dy > 15) {
                                    if (!counter.isShown()) counter.setVisibility(View.VISIBLE);
                                    bottomNavigationView.animate()
                                            .translationY(bottomNavigationView.getHeight())
                                            .setDuration(250);
                                }
                                final int visibleThreshold = 1;
                                int currentTotalCount = layoutManager.getItemCount();
                                Log.d("here", isScrolling + " currentotalCount " + currentTotalCount + " <= lastItem " + lastItem + " vt " + visibleThreshold);
                                if (isScrolling && !reachEnd && currentTotalCount <= lastItem + visibleThreshold) {

                                    progressBar.setVisibility(View.VISIBLE);
                                    secondApiCallScroll(wallpaperApi, urlBase);
                                }
                            }
                        }
                    };
                    autofitRecyclerViewWallpaper.addOnScrollListener(scrollListener);
                }
            }


            @Override
            public void onFailure(Call<WallpaperCall> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressBarCenter.setVisibility(View.GONE);
                errorConnectionToast();
            }
        });
    }

    public void secondApiCallScroll(WallpaperApi wallpaperApi, String urlBase) {
        Call<WallpaperCall> call2 = wallpaperApi.getWallpapers(urlBase + ++page);
        call2.enqueue(new Callback<WallpaperCall>() {
            @Override
            public void onResponse(Call<WallpaperCall> call, Response<WallpaperCall> response) {
                if (photoFragmentZoom != null) photoFragmentZoom.canEnter = true;

                if (!response.isSuccessful()) {
                    page--;
                    return;
                }
                isScrolling = false;
                if (response.body().wallpapers != null)
                    list.addAll(response.body().getWallpapers());
                else
                    reachEnd = true;

                if (photoFragmentZoom != null) photoFragmentZoom.notifyDataSetChanged(list);

                autofitRecyclerViewWallpaper.setListUpdate(list);
                autofitRecyclerViewWallpaper.notifyDataChange();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<WallpaperCall> call, Throwable t) {
                if (photoFragmentZoom != null) photoFragmentZoom.canEnter = true;
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
                                WallpaperActivity.this.runOnUiThread(() -> {
                                    if (list == null) searchAndLoadWallpaperImages();
                                    else
                                        secondApiCallScroll(wallpaperApi, urlBase);
                                });
                            } else {
                                WallpaperActivity.this.runOnUiThread(this::errorConnectionToast);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                });
        snackbar.show();
    }

    public void setPositionToAutofitRecyclerView(int position) {
        autofitRecyclerViewWallpaper.scrollToPosition(position);
    }

    // To test if there is internet available
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

    static PhotoFragmentZoom photoFragmentZoom;

    public static void setPhotoFragmentZoomNull() {
        if (photoFragmentZoom != null) {
            photoFragmentZoom = null;
        }
    }

    // When click a wallpaper to load all wallpapers on viewpage
    @Override
    public void onPhotoClicked(RecyclerViewAdapterWallpaper.MyViewHolder holder, int position, List<Wallpaper> wallpapers) {
        if (photoFragmentZoom == null) {
            photoFragmentZoom = PhotoFragmentZoom.newInstance(wallpapers, position, this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(holder.imageView, String.valueOf(position))
                    .add(R.id.main, photoFragmentZoom)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onAnimeClicked(AnimeTop anime) {

    }

    @Override
    public void onMangaClicked(MangaTop anime) {

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else if (bottomNavigationView.isShown() && !urlBase.equals(getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod))) {
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
        urlBase = getString(R.string.firstPart) + getString(R.string.key) + getString(R.string.categoryMethod);
        page = 1;
        searchAndLoadWallpaperImages();
        materialToolbar.setNavigationIcon(R.drawable.ic_wallpapers);
        materialToolbar.setTitle(R.string.wallpaper_title);
        counter.setVisibility(View.GONE);
    }

}