package com.hukex.punpun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hukex.punpun.utils.GlideApp;
import com.hukex.punpun.utils.ItemClickListener;
import com.hukex.punpun.R;
import com.hukex.punpun.model.wallpapers.Wallpaper;

import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;

public class RecyclerViewAdapterWallpaper extends RecyclerView.Adapter<RecyclerViewAdapterWallpaper.MyViewHolder> {

    private final ItemClickListener itemClickListener;

    private int dp;

    private Context mContext;
    private List<Wallpaper> wallpapers;


    public RecyclerViewAdapterWallpaper(Context mContext, List<Wallpaper> wallpapers, ItemClickListener itemClickListener, int dp) {
        this.mContext = mContext;
        this.wallpapers = wallpapers;
        this.itemClickListener = itemClickListener;
        this.dp = dp;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        if (dp > 0) {
            View viewCard = view.findViewById(R.id.photoWallpaperParent);
            float pixels = dp * mContext.getResources().getDisplayMetrics().density;
            viewCard.getLayoutParams().width = (int) pixels;
            viewCard.getLayoutParams().height = (int) pixels;
            viewCard.requestLayout();
        }
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        // Load thumbnail and when is ready change by full image ☺
        // New Update (Nice logic, but load 8k images with infinity scroll & smooth is not possible dude, so load thumb) =(
        GlideApp.with(mContext)
                .load(wallpapers.get(position).getUrlThumb())
                .centerInside().centerCrop().thumbnail(GlideApp
                .with(mContext)
                .load(wallpapers.get(position).getUrlThumb()).centerInside().centerCrop())
                .into(holder.imageView);
        Log.d("XDR", Thread.currentThread().getName());
        setTransitionName(holder.imageView, String.valueOf(position));
        holder.imageView.setOnClickListener(v -> itemClickListener.onPhotoClicked(holder, position, wallpapers));
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoWallpaper);
        }
    }
}
