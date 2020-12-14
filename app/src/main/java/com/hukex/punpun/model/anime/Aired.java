package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Aired {

    @SerializedName("from")
    @Expose
    public String from;
    @SerializedName("to")
    @Expose
    public String to;
    @SerializedName("prop")
    @Expose
    public Prop prop;
    @SerializedName("string")
    @Expose
    public String string;

}