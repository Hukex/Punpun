package com.hukex.punpun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hukex.punpun.R;
import com.hukex.punpun.model.anime.AnimeTop;
import com.hukex.punpun.utils.GlideApp;
import com.hukex.punpun.utils.ItemClickListener;

import java.util.List;


public class RecyclerViewAdapterAnime extends RecyclerView.Adapter<RecyclerViewAdapterAnime.MyViewHolder> {

    private final ItemClickListener itemClickListener;

    private Context mContext;
    private List<AnimeTop> animes;


    public  RecyclerViewAdapterAnime(Context mContext, List<AnimeTop> animes, ItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.animes = animes;
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
                .load(animes.get(position).getImageUrl()).centerCrop()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> itemClickListener.onAnimeClicked(animes.get(position)));
        TextView textView = holder.imageView.getRootView().findViewById(R.id.anime_title);
        textView.setText(animes.get(position).getTitle());
        Log.d("XDR", animes.get(position).toString());
        TextView score = holder.imageView.getRootView().findViewById(R.id.anime_score);
        score.setText(String.valueOf(animes.get(position).getScore()));
        ImageView iconStar = holder.imageView.getRootView().findViewById(R.id.anime_score_icon);
        if (animes.get(position).getScore() >= 4 && animes.get(position).getScore() <= 6) {
            iconStar.setImageResource(R.drawable.ic_start_half);
        } else if (animes.get(position).getScore() < 3) {
            iconStar.setImageResource(R.drawable.ic_start_empty);
        }
        TextView rank = holder.imageView.getRootView().findViewById(R.id.rank_anime);
        if (animes.get(position).getRank() != null)
            rank.setText(String.valueOf(animes.get(position).getRank()));
        else
            rank.setText(String.valueOf(position+1));

        TextView type = holder.imageView.getRootView().findViewById(R.id.type_anime);
        String typeString = animes.get(position).getType();
        String episodes = String.valueOf(animes.get(position).getEpisodes());
        if (typeString.equals("TV") || typeString.equals("OVA") || typeString.equals("Special")) {
            if (!episodes.equals("null")) type.setText(typeString + "(" + episodes + ")");
            else type.setText(typeString);
        } else
            type.setText(animes.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return animes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoAnime);
        }
    }
}
