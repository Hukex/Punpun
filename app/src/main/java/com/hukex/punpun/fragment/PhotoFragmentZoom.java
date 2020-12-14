package com.hukex.punpun.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hukex.punpun.R;
import com.hukex.punpun.WallpaperActivity;
import com.hukex.punpun.model.wallpapers.Wallpaper;
import com.hukex.punpun.utils.GlideApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.core.view.ViewCompat.setTransitionName;


public class PhotoFragmentZoom extends Fragment {

    private List<Wallpaper> wallpapers = new ArrayList<>();
    private int position;
    private Context mContext;
    private ViewPager viewPager;
    public boolean canEnter = true;
    private int currentPage = 0;
    private Timer timer;
    final long DELAY_MS = 2000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 500; // time in milliseconds between successive task executions.
    private int realPosition;
    private PhotoWithZoomAdapter photoWithZoomAdapter;
    private SharedPreferences sharedPref;

    //Important when rotate phone(create activity)
    public PhotoFragmentZoom() {
    }

    public PhotoFragmentZoom(List<Wallpaper> wallpapers, int imagePosition, Context context) {
        this.wallpapers = wallpapers;
        this.position = imagePosition;
        this.mContext = context;
    }

    public static PhotoFragmentZoom newInstance(List<Wallpaper> wallpapers, int imagePosition, Context context) {
        PhotoFragmentZoom fragment = new PhotoFragmentZoom(wallpapers, imagePosition, context);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_view_with_zoom_container, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        madeMainBarsVisibleAndLeaveFullScreen();
        WallpaperActivity wallpaperActivity = (WallpaperActivity) getActivity();
        wallpaperActivity.setPositionToAutofitRecyclerView(realPosition);
        WallpaperActivity.setPhotoFragmentZoomNull();
    }

    private void madeMainBarsVisibleAndLeaveFullScreen() {
        AppBarLayout appBarLayoutWallpaper = ((Activity) mContext).findViewById(R.id.topWallpaperBarParent);
        appBarLayoutWallpaper.setVisibility(View.VISIBLE);
        BottomNavigationView bottomNavigationViewWallpaper = ((Activity) mContext).findViewById(R.id.bottomWallpaperBar);
        bottomNavigationViewWallpaper.setVisibility(View.VISIBLE);
        TextView counter = ((Activity) mContext).findViewById(R.id.counter);
        counter.setVisibility(View.VISIBLE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void notifyDataSetChanged(List<Wallpaper> list) {
        wallpapers = list;
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (wallpapers != null) {  // last bug?
            realPosition = position;
            super.onViewCreated(view, savedInstanceState);
            madeMainBarsInvisibleAndFullScreen(view);
            viewPager = view.findViewById(R.id.container_photo_zoom);
            photoWithZoomAdapter = new PhotoWithZoomAdapter();
            viewPager.setAdapter(photoWithZoomAdapter);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            boolean switchQualityImage = sharedPref.getBoolean("QUALITYIMAGE", false);
            if (switchQualityImage)
                viewPager.setOffscreenPageLimit(3);
            Log.d("position", "position" + position);
            viewPager.setCurrentItem(position);
            MaterialToolbar materialToolbar = view.findViewById(R.id.topWallpaperZoomBar);
            materialToolbar.bringToFront();
            materialToolbar.setNavigationOnClickListener(v -> {
                getActivity().onBackPressed();
            });
            materialToolbar.setTitle(wallpapers.get(position).getSubCategory());
            viewPager.setPageMargin(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()))); // space between images
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (viewPager.getCurrentItem() >= wallpapers.size() - 5 && canEnter) {
                        canEnter = false;
                        WallpaperActivity wallpaperActivity = (WallpaperActivity) getActivity();
                        if (!wallpaperActivity.getReachEnd())
                            wallpaperActivity.secondApiCallScroll(wallpaperActivity.getWallpaperApi(), wallpaperActivity.getUrlBase());
                    }
                }

                public void onPageSelected(int position) {
                    String title;
                    title = wallpapers.get(position).getSubCategory();
                    materialToolbar.setTitle(title);
                    realPosition = position;
                }
            });
            BottomNavigationView bottomNavigationView = view.findViewById(R.id.container_bar);
            bottomNavigationView.setOnNavigationItemSelectedListener(v -> {
                switch (v.getItemId()) {
                    case R.id.bar_share:
                        Log.d("pos", " " + realPosition);
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, wallpapers.get(realPosition).getUrlImage());
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_title)));
                        return true;
                    case R.id.bar_download:
                        return true;
                    case R.id.bar_left:
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                        return true;
                    case R.id.bar_right:
                        if (viewPager.getCurrentItem() >= wallpapers.size() - 5 && canEnter) {
                            canEnter = false;
                            WallpaperActivity wallpaperActivity = (WallpaperActivity) getActivity();
                            if (!wallpaperActivity.getReachEnd())
                                wallpaperActivity.secondApiCallScroll(wallpaperActivity.getWallpaperApi(), wallpaperActivity.getUrlBase());
                        }
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        return true;
                    case R.id.bar_more:
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                            return true;
                        }
                        currentPage = realPosition;
                        /*After setting the adapter use the timer */
                        final Handler handler = new Handler();
                        final Runnable Update = () -> {
                            if (currentPage == wallpapers.size() - 1) {
                                currentPage = 0;
                            }
                            viewPager.setCurrentItem(currentPage++, true);
                        };
                        timer = new Timer(); // This will create a new Thread
                        timer.schedule(new TimerTask() { // task to be scheduled
                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, DELAY_MS, PERIOD_MS);
                        return true;
                }
                return false;
            });
        }
    }

    private void madeMainBarsInvisibleAndFullScreen(View view) {
        View parentItem = (View) view.getParent();
        BottomNavigationView bottomNavigationViewWallpaper = parentItem.findViewById(R.id.bottomWallpaperBar);
        AppBarLayout appBarLayoutWallpaper = parentItem.findViewById(R.id.topWallpaperBarParent);
        TextView counter = parentItem.findViewById(R.id.counter);
        appBarLayoutWallpaper.setVisibility(View.GONE);
        bottomNavigationViewWallpaper.setVisibility(View.GONE);
        counter.setVisibility(View.GONE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private class PhotoWithZoomAdapter extends PagerAdapter {
        private HashMap<View, PhotoView> photoViewContainer = new HashMap<>(); // to save the photoview items so when view is remove also this photoview(Glide petition)
        private SharedPreferences sharedPref;

        @Override
        public int getCount() {
            return wallpapers.size();
        }

        // PUTA LOCURA DE CODIGO
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup parent, int position) {
            PhotoView photoView;
            Log.d("load", "loading" + position);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_view_with_zoom_item, parent, false);
            View parentItem = (View) ((ViewGroup) parent.getParent()).getParent();
            AppBarLayout appBarLayout = parentItem.findViewById(R.id.topWallpaperZoomBarParent);
            BottomNavigationView bottomNavigationView = parentItem.findViewById(R.id.container_bar);
            photoView = view.findViewById(R.id.photo_view);
            photoView.setOnClickListener(v -> {
                if (bottomNavigationView.getAlpha() == 1f) {
                    bottomNavigationView.animate()
                            .alpha(0f).translationY(bottomNavigationView.getHeight())
                            .setDuration(300);
                    appBarLayout.animate()
                            .alpha(0f).translationY(-appBarLayout.getHeight())
                            .setDuration(300);
                    parent.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                } else {
                    bottomNavigationView.animate()
                            .alpha(1f).translationY(0)
                            .setDuration(300);
                    appBarLayout.animate()
                            .alpha(1f).translationY(0)
                            .setDuration(300);
                    TypedValue typedValue = new TypedValue();
                    mContext.getTheme().resolveAttribute(R.attr.backgroundOwn, typedValue, true);
                    @ColorInt int color = typedValue.data;
                    parent.setBackgroundColor(color);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
            setTransitionName(photoView, String.valueOf(position));
            String width, height;
            width = wallpapers.get(position).getWidth();
            height = wallpapers.get(position).getHeight();
            photoView.setMaximumScale(10);
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getActivity());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(90f);
            int color = ResourcesCompat.getColor(getResources(), R.color.purple_200, null);
            //Just to change the color of spinner
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                circularProgressDrawable.setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
            } else {
                circularProgressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
            circularProgressDrawable.start();
            ProgressBar progressBar;
            progressBar = view.findViewById(R.id.progress_bar_circle_zoom_item);
            progressBar.setVisibility(View.VISIBLE);
            // I wanna the max resolution because I'm using Photoview that allow zoom. But with some larger images is too much for canvas draw
            Log.d("hefe", position + " width: " + wallpapers.get(position).getWidth() + " height:" + wallpapers.get(position).getHeight() + " id  " + wallpapers.get(position).getId() + " url  " + wallpapers.get(position).getUrlImage() + " file size " + wallpapers.get(position).getFileSize());

            sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            boolean switchQualityImage = sharedPref.getBoolean("QUALITYIMAGE", false);
            if (switchQualityImage) {
                if (Integer.parseInt(width) <= 5000 && Integer.parseInt(height) <= 5000) {
                    fullResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar);
                } else if (Integer.parseInt(width) < 10000 && Integer.parseInt(height) < 10000) {
                    optimizedResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar, 2800);
                } else {
                    optimizedResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar, 2000);
                }
            } else {
                fullOptimizedResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar);
            }
            parent.addView(view);
            photoViewContainer.put(view, photoView);
            return view;
        }

        private void optimizedResolutionGlidePetition(int position, PhotoView photoView, View view, CircularProgressDrawable circularProgressDrawable, ProgressBar progressBar, int override) {
            GlideApp.with(view.getContext()).asBitmap()
                    .load(wallpapers.get(position).getUrlImage())
                    .placeholder(circularProgressDrawable).thumbnail(GlideApp
                    .with(mContext).asBitmap()
                    .load(wallpapers.get(position).getUrlThumb())).apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL).override(override)).listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    optimizedResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar, override);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(photoView);
        }

        private void fullOptimizedResolutionGlidePetition(int position, PhotoView photoView, View view, CircularProgressDrawable circularProgressDrawable, ProgressBar progressBar) {
            Log.d("t", "te ha tocado " + position + " url " + wallpapers.get(position).getUrlImage());
            GlideApp.with(view.getContext()).asBitmap()
                    .load(wallpapers.get(position).getUrlImage())
                    .placeholder(circularProgressDrawable).thumbnail(GlideApp
                    .with(mContext).asBitmap()
                    .load(wallpapers.get(position).getUrlThumb())).listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    fullOptimizedResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(photoView);
        }


        private void fullResolutionGlidePetition(int position, PhotoView photoView, View view, CircularProgressDrawable circularProgressDrawable, ProgressBar progressBar) {
            GlideApp.with(view.getContext())
                    .load(wallpapers.get(position).getUrlImage())
                    .placeholder(circularProgressDrawable).apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL)).thumbnail(GlideApp
                    .with(mContext)
                    .load(wallpapers.get(position).getUrlThumb())).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    // TODO ADD RETRY OR SOMETHING TO RELOAD THE IMAGE IF THERE IS INTERNET PROBLEM =)
                    fullResolutionGlidePetition(position, photoView, view, circularProgressDrawable, progressBar);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(photoView);
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            View viewX = (View) view;
            GlideApp.with(viewX.getContext()).clear(photoViewContainer.get(viewX)); // this way when view is remove avoid glide to load images on background uselessly.
            containerCollection.removeView(viewX);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
