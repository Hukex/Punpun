package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Result {
    @SerializedName("mal_id")
    @Expose
    public Integer malId;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("airing")
    @Expose
    public Boolean airing;
    @SerializedName("synopsis")
    @Expose
    public String synopsis;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("episodes")
    @Expose
    public Integer episodes;
    @SerializedName("score")
    @Expose
    public Double score;
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("end_date")
    @Expose
    public String endDate;
    @SerializedName("members")
    @Expose
    public Integer members;
    @SerializedName("rated")
    @Expose
    public String rated;

}
