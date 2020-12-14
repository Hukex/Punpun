package com.hukex.punpun.model.manga;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class SearchManga {
    @SerializedName("request_hash")
    @Expose
    public String requestHash;
    @SerializedName("request_cached")
    @Expose
    public Boolean requestCached;
    @SerializedName("request_cache_expiry")
    @Expose
    public Integer requestCacheExpiry;
    @SerializedName("results")
    @Expose
    public List<MangaTop> results = null;
    @SerializedName("last_page")
    @Expose
    public Integer lastPage;
}
