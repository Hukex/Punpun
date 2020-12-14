package com.hukex.punpun.api;

import com.hukex.punpun.model.anime.AnimeInfoCall;
import com.hukex.punpun.model.anime.AnimeTopCall;
import com.hukex.punpun.model.anime.SearchAnime;
import com.hukex.punpun.model.manga.MangaInfoCall;
import com.hukex.punpun.model.manga.MangaTopCall;
import com.hukex.punpun.model.manga.SearchManga;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AnimeMangaApi {

    @GET
    Call<AnimeTopCall> getTopAnimes(@Url String url);

    @GET
    Call<MangaTopCall> getTopMangas(@Url String url);

    @GET
    Call<AnimeInfoCall> getInfoAnime(@Url String url);

    @GET
    Call<MangaInfoCall> getInfoManga(@Url String url);

    @GET
    Call<SearchAnime> getAnimes(@Url String url);

    @GET
    Call<SearchManga> getMangas(@Url String url);

}
