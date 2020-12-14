package com.hukex.punpun.model.wallpapers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Wallpaper {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("width")
    @Expose
    public String width;
    @SerializedName("height")
    @Expose
    public String height;
    @SerializedName("file_type")
    @Expose
    public String fileType;
    @SerializedName("file_size")
    @Expose
    public String fileSize;
    @SerializedName("url_image")
    @Expose
    public String urlImage;
    @SerializedName("url_thumb")
    @Expose
    public String urlThumb;
    @SerializedName("url_page")
    @Expose
    public String urlPage;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("sub_category")
    @Expose
    public String subCategory;
    @SerializedName("sub_category_id")
    @Expose
    public String subCategoryId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_id")
    @Expose
    public String userId;


}
