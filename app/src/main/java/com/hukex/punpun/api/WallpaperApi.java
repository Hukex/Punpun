package com.hukex.punpun.api;

import com.hukex.punpun.model.wallpapers.SubCategoryCall;
import com.hukex.punpun.model.wallpapers.WallpaperCall;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WallpaperApi {

    @GET
    Call<WallpaperCall> getWallpapers(@Url String url);

    @GET
    Call<SubCategoryCall> getSubcategories(@Url String url);

}
