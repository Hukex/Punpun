package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class Related {

    @SerializedName("Adaptation")
    @Expose
    public List<Adaptation> adaptation = null;
    @SerializedName("Alternative version")
    @Expose
    public List<AlternativeVersion> alternativeVersion = null;
    @SerializedName("Side story")
    @Expose
    public List<SideStory> sideStory = null;
    @SerializedName("Spin-off")
    @Expose
    public List<SpinOff> spinOff = null;

}
