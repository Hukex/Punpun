package com.hukex.punpun.model.wallpapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SubCategory {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("url")
    @Expose
    public String url;

}