package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class AnimeTop {
    @SerializedName("mal_id")
    @Expose
    public Integer malId;
    @SerializedName("rank")
    @Expose
    public Integer rank;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("episodes")
    @Expose
    public Integer episodes;
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("end_date")
    @Expose
    public String endDate;
    @SerializedName("members")
    @Expose
    public Integer members;
    @SerializedName("score")
    @Expose
    public Double score;

}
