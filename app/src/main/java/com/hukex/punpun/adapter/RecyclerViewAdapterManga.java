package com.hukex.punpun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hukex.punpun.utils.GlideApp;
import com.hukex.punpun.utils.ItemClickListener;
import com.hukex.punpun.R;
import com.hukex.punpun.model.manga.MangaTop;

import java.util.List;

public class RecyclerViewAdapterManga extends RecyclerView.Adapter<RecyclerViewAdapterManga.MyViewHolder> {

    private final ItemClickListener itemClickListener;

    private Context mContext;
    private List<MangaTop> mangas;


    public RecyclerViewAdapterManga(Context mContext, List<MangaTop> mangas, ItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mangas = mangas;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_and_manga_view, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        GlideApp.with(mContext)
                .load(mangas.get(position).getImageUrl()).centerCrop()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> itemClickListener.onMangaClicked(mangas.get(position)));

        TextView textView = holder.imageView.getRootView().findViewById(R.id.anime_title);
        textView.setText(mangas.get(position).getTitle());
        TextView score = holder.imageView.getRootView().findViewById(R.id.anime_score);
        score.setText(String.valueOf(mangas.get(position).getScore()));
        ImageView iconStar = holder.imageView.getRootView().findViewById(R.id.anime_score_icon);
        if (mangas.get(position).getScore() >= 4 && mangas.get(position).getScore() <= 6) {
            iconStar.setImageResource(R.drawable.ic_start_half);
        } else if (mangas.get(position).getScore() < 3) {
            iconStar.setImageResource(R.drawable.ic_start_empty);
        }
        TextView rank = holder.imageView.getRootView().findViewById(R.id.rank_anime);
        if (mangas.get(position).getRank() != null)
            rank.setText(String.valueOf(mangas.get(position).getRank()));
        else
            rank.setText(String.valueOf(position + 1));
        TextView type = holder.imageView.getRootView().findViewById(R.id.type_anime);
        String typeString = mangas.get(position).getType();
        String volumes = String.valueOf(mangas.get(position).getVolumes());
        if (typeString.equals("TV") || typeString.equals("OVA") || typeString.equals("Special")) {
            if (!volumes.equals("null")) type.setText(typeString + "(" + volumes + ")");
            else type.setText(typeString);
        } else
            type.setText(mangas.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoAnime);
        }
    }
}
