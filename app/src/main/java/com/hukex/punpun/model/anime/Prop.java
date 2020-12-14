package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Prop {
    @SerializedName("from")
    @Expose
    public From from;
    @SerializedName("to")
    @Expose
    public To to;

}