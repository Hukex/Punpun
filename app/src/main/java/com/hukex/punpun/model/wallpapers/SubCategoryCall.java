package com.hukex.punpun.model.wallpapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class SubCategoryCall {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("sub-categories")
    @Expose
    public List<SubCategory> subCategories = null;

}