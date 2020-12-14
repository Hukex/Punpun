package com.hukex.punpun.utils;

import com.hukex.punpun.adapter.RecyclerViewAdapterWallpaper;
import com.hukex.punpun.model.anime.AnimeTop;
import com.hukex.punpun.model.manga.MangaTop;
import com.hukex.punpun.model.wallpapers.Wallpaper;

import java.util.List;

public interface ItemClickListener {

    void onPhotoClicked(RecyclerViewAdapterWallpaper.MyViewHolder holder, int position, List<Wallpaper> wallpapers);

    void onAnimeClicked(AnimeTop anime);

    void onMangaClicked(MangaTop anime);


}
