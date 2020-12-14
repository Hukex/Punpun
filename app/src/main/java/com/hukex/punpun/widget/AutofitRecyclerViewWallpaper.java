package com.hukex.punpun.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hukex.punpun.R;
import com.hukex.punpun.utils.ItemClickListener;
import com.hukex.punpun.adapter.RecyclerViewAdapterWallpaper;
import com.hukex.punpun.model.wallpapers.Wallpaper;

import java.util.List;

public class AutofitRecyclerViewWallpaper extends RecyclerView {
    private GridLayoutManager manager;
    private int columnWidth = -1, columnWidthOriginal, image_view_dp;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    private Context mContext;
    private List<Wallpaper> list;
    private ItemClickListener itemClickListener;
    private RecyclerViewAdapterWallpaper recyclerViewAdapterWallpaper;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean firstLoad = false;

    public void notifyDataChange() {
        recyclerViewAdapterWallpaper.notifyDataSetChanged();
    }

    public void createRecyclerViewAdapter() {
        recyclerViewAdapterWallpaper = new RecyclerViewAdapterWallpaper(mContext, list, itemClickListener, image_view_dp);
        setAdapter(recyclerViewAdapterWallpaper);
    }

    public void setSharedPref(SharedPreferences sharedPref, SharedPreferences.Editor editor) {
        this.sharedPref = sharedPref;
        this.editor = editor;
        image_view_dp = sharedPref.getInt(getResources().getString(R.string.image_view_dp), 100);
        columnWidth = sharedPref.getInt(getResources().getString(R.string.column_width_pixels), columnWidthOriginal);
        columnWidthOriginal = columnWidth;
        firstLoad = true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {


        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (!firstLoad) {
                scale *= detector.getScaleFactor();
            } else {
                scale = sharedPref.getFloat(getResources().getString(R.string.zoom_value), 1);
                firstLoad = false;
            }
            scale = Math.max(1.0f, Math.min(scale, 4.0f));
            matrix.setScale(scale, scale);
            float[] num = new float[9];
            matrix.getValues(num);
            Log.d("scale", String.valueOf(num[0]));
            int pixels;
            if (num[0] > 1.5 && num[0] < 2.5) {
                // 3
                if (columnWidth == columnWidthOriginal) {
                    pixels = (int) (120 * mContext.getResources().getDisplayMetrics().density);
                    changeColumnWidth(pixels);
                    image_view_dp = 140;
                    editor.putInt(getResources().getString(R.string.column_width_pixels), pixels);
                    editor.putInt(getResources().getString(R.string.image_view_dp), image_view_dp);
                    editor.putFloat(getResources().getString(R.string.zoom_value), num[0]);
                    editor.apply();
                    recyclerViewAdapterWallpaper = new RecyclerViewAdapterWallpaper(mContext, list, itemClickListener, image_view_dp);
                    setAdapter(recyclerViewAdapterWallpaper);
                }
            } else if (num[0] >= 2.5 && num[0] <= 4) {
                // 2
                pixels = (int) (120 * mContext.getResources().getDisplayMetrics().density);
                if (columnWidth == pixels) {
                    pixels = (int) (180 * mContext.getResources().getDisplayMetrics().density);
                    changeColumnWidth(pixels);
                    image_view_dp = 210;
                    editor.putInt(getResources().getString(R.string.column_width_pixels), pixels);
                    editor.putInt(getResources().getString(R.string.image_view_dp), image_view_dp);
                    editor.putFloat(getResources().getString(R.string.zoom_value), num[0]);
                    editor.apply();
                    recyclerViewAdapterWallpaper = new RecyclerViewAdapterWallpaper(mContext, list, itemClickListener, image_view_dp);
                    setAdapter(recyclerViewAdapterWallpaper);
                }
            } else {
                //3
                pixels = (int) (180 * mContext.getResources().getDisplayMetrics().density);
                int pixels2 = (int) (120 * mContext.getResources().getDisplayMetrics().density);
                if (columnWidth == pixels) {
                    changeColumnWidth(pixels2);
                    image_view_dp = 140;
                    editor.putInt(getResources().getString(R.string.column_width_pixels), pixels);
                    editor.putInt(getResources().getString(R.string.image_view_dp), image_view_dp);
                    editor.putFloat(getResources().getString(R.string.zoom_value), num[0]);
                    editor.apply();
                    recyclerViewAdapterWallpaper = new RecyclerViewAdapterWallpaper(mContext, list, itemClickListener, image_view_dp);
                    setAdapter(recyclerViewAdapterWallpaper);
                } else if (columnWidth == pixels2) { // 4
                    int pixels3 = (int) (90 * mContext.getResources().getDisplayMetrics().density);
                    changeColumnWidth(pixels3);
                    image_view_dp = 100;
                    editor.putInt(getResources().getString(R.string.column_width_pixels), pixels3);
                    editor.putInt(getResources().getString(R.string.image_view_dp), image_view_dp);
                    editor.putFloat(getResources().getString(R.string.zoom_value), num[0]);
                    editor.apply();
                    recyclerViewAdapterWallpaper = new RecyclerViewAdapterWallpaper(mContext, list, itemClickListener, image_view_dp);
                    setAdapter(recyclerViewAdapterWallpaper);
                }
            }
            return true;
        }
    }

    public void setContextAndPhotoClickListener(Context context, ItemClickListener itemClickListener) {
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    public void setListUpdate(List<Wallpaper> list) {
        this.list = list;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mScaleDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }


    public AutofitRecyclerViewWallpaper(Context context) {
        super(context);
        init(context, null);
    }

    public AutofitRecyclerViewWallpaper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerViewWallpaper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);
            columnWidthOriginal = columnWidth;
            array.recycle();
        }
        manager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(manager);
    }

    // Seems it works =)=)=)))=)=)=))
    public void changeColumnWidth(int x) {
        columnWidth = x;
        measure(0, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(spanCount);
        }
    }
}