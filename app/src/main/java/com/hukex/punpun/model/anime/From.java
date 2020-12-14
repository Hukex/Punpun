package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class From {

    @SerializedName("day")
    @Expose
    public Integer day;
    @SerializedName("month")
    @Expose
    public Integer month;
    @SerializedName("year")
    @Expose
    public Integer year;

}
