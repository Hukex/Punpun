package com.hukex.punpun.model.wallpapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class WallpaperCall {
    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("wallpapers")
    @Expose
    public List<Wallpaper> wallpapers = null;
    @SerializedName("total_match")
    public String total_match;
}
