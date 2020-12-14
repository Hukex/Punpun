package com.hukex.punpun.model.manga;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class MangaTop {

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
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("volumes")
    @Expose
    public Integer volumes;
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
    @SerializedName("image_url")
    @Expose
    public String imageUrl;

}